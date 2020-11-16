/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package calculation;

import GUI.Report;
import GUI.variables;

/**
 * Klassen inneholder fire metoder:
 * To metoder for beregning av skjærkapasitet etter NS-EN 1992-1-1
 * shearCap() og reinforcedShearCap()
 *
 * En metode som gjør en uavhengig skjærkontroll av betongskall
 * utilization()
 *
 * 
 * @author Aleksander og Einar
 */
public class Shear {
//Kapasitet for bjelker uten beregningsmessig behov for skjærarmering
    /*
     * shearCap()
     * Metode som finner skjærkapasiteten til et betongtverrsnitt ihht EC2
     * Enheter inn:
     * fck i MPa
     * d i mm
     * fi0 i rad
     * Asx og Asy i mm2/m
     * conStress settes inn med trykk som minus og strekk som pluss. N/mm
     */

    public static double shearCap(double fck, double d, double fi0, double Asx, double Asy, double gammac, double k2, double k1, double conStress) {
        //deklarerer
        double vRdc, cRdc, rhoX, rhoY, rhoL, vMin, k;

        //Må endre fortegn på betongspenningen for at den skal passe inn i skjærformelen
        conStress = conStress * -1;

        //Punkt 6.2.2(1)
        cRdc = k2 / gammac;
        k = Math.min(2.0, 1 + Math.sqrt(200 / d));
        rhoX = Asx / (1000 * d);             //bx = 1m
        rhoY = Asy / (1000 * d);             //by = 1m
        rhoL = Math.min(rhoX * Math.cos(fi0) * Math.cos(fi0) + rhoY * Math.sin(fi0) * Math.sin(fi0), 0.02);
        vMin = 0.035 * Math.pow(k, (3.0 / 2.0)) * Math.sqrt(fck);
        vRdc = Math.max(
                (cRdc * k * Math.pow(100 * rhoL * fck, (1.0 / 3.0)) + k1 * conStress) * d,
                (vMin + k1 * conStress) * d);
//        Uten trykk/strekk-spenninger:
//        vRdc = cRdc * k * Math.pow(100 * rhoL * fck, (1. / 3.)) * d;
//        vRdc = Math.max(vRdc, vMin);
        return vRdc;
    }

    //Kapasitet for betongtrykksonen i bjelker med behov for skjærarmering
    public static double reinforcedShearCap(double z, double fcd, double fck, double fywd, double theta, double alpha, double gammas, double Asw) {
        double vrdmax, v1, vrd;
        double vrds;
        double alphacw = 1.;
        Asw = Asw / 1e3; //denne er gitt per m^2, dermed er s innbakt

        if (fck <= 60) {
            v1 = 0.6;
        } else {
            v1 = 0.9 - fck / 200;

        }
        if (alpha == 90) {
            vrdmax = (alphacw * z * v1 * fcd) / (1 / Math.tan(theta) + Math.tan(theta)); //denne skal egentlig ganges med b = 1000 , men gjøres om til kN
        } else {
            alpha = alpha * Math.PI / 180;
            vrdmax = (alphacw * z * v1 * fcd) * (1 / Math.tan(theta) + 1 / Math.tan(alpha)) / (1 / Math.pow(Math.tan(theta), 2)); //denne skal egentlig ganges med b = 1000 , men gjøres om til kN
        }


        if (Asw != 0) {
            vrds = (Asw * z * fywd / Math.tan(theta)) / 1000.0; //vrds i N, dividerer med 1000 for å få kN
            vrd = Math.min(vrds, vrdmax);
        } else vrd = vrdmax;

        
//        DEBUG
//        System.out.println("v1: "+v1);
//        System.out.println("vrdmax: "+vrdmax);
//        System.out.println("vrds: "+vrds);
//        System.out.println("vrd: "+vrd);
//        System.out.println("theta: "+theta);
//        System.out.println("Asw: "+Asw);
//        System.out.println("z: "+z);
//        System.out.println("fywd: "+fywd);

        return vrd;
    }


    /*
     * Metode utilization() Sjekker skjærkap med aksialkrefter
     *
     */
    public static double[] utilization() {
        //deklarerer
        double vAngle, nAngle, angle, aAngleTop, aAngleBottom;
        double AsxShear, AsyShear;
        double[] ur = new double[4];

        double vRd, k1;

        double conStress;

        //1 henter nødvendige verdier
        variables.getValues(); //Sørger for at variables oppdaterer fields
        //Geometri
        double d, z;
        double h = variables.h;
        double[] c = {variables.ci, variables.cs};
        d = h - (c[0] + c[1]) / 2;
        z = 0.9 * d;

        //Krefter:
        double nedx = variables.nedx;
        double nedy = variables.nedy;
        double nedxy = variables.nedxy;
        double vedx = variables.vedx;
        double vedy = variables.vedy;

        //armering:
        double Asxi = variables.Asxi;
        double Asxs = variables.Asxs;
        double Asyi = variables.Asyi;
        double Asys = variables.Asys;
        double Asw = variables.Asw;
        double gammaS = variables.gammas;
        double fywd = variables.fywd;


        //betong
        double fck = variables.fck;
        double fcd = variables.fcd;
        double gammaC = variables.gammac;


        //Skjærstørrelser:
        double k1Tension = variables.k1tension;
        double k1Compression = variables.k1compression;
        double k2 = variables.k2;
        double cotTheta = variables.cotTheta;
        double alpha = variables.alpha;


        double uRatioSave = 0.0;


        //2 prøver ut vinkler med 5 graders steg og finner nye tverrsnittsverdier.
        for (int a = 0; a <= 180; a = a + 5) {

            angle = a * Math.PI / 180.0;


            vAngle = vedx * Math.cos(angle) + vedy * Math.sin(angle);
            nAngle = nedx * Math.pow(Math.cos(angle), 2.0) + nedy * Math.pow(Math.sin(angle), 2.0) + 2.0 * nedxy * Math.sin(angle) * Math.cos(angle);
            aAngleTop = Asxs * Math.pow(Math.cos(angle), 2.0) + Asys * Math.pow(Math.sin(angle), 2.0);
            aAngleBottom = Asxs * Math.pow(Math.cos(angle), 2.0) + Asys * Math.pow(Math.sin(angle), 2.0);

            conStress = nAngle / h;

            conStress = Math.max(conStress, -0.2 * fcd); //max fordi trykkspenninger er negative!


            if (conStress < 0) {
                k1 = k1Compression;
            } else {
                k1 = k1Tension;
            }


            //3 Regner ut skjærkapasitet basert på tverrsnittsverdier

            if(aAngleTop > aAngleBottom) { // Bruker den minste av armeringsmengdene for å være konservativ
                    AsxShear = Asxi;
                    AsyShear = Asyi;
            } else { 
                    AsxShear = Asxs;
                    AsyShear = Asys;
            }

            vRd = shearCap(fck, d, angle, AsxShear, AsyShear, gammaC, k2, k1, conStress);


            //4 Sjekker om betongen har tilstrekkelig skærkapasitet
            if (Math.abs(vAngle) > vRd) {

                double theta = Math.atan(1 / cotTheta);
                vRd = reinforcedShearCap(z, fcd, fck, fywd, theta, alpha, gammaS, Asw);

            }
            //5 Regner utnyttingsgrad
            double uRatio = Math.abs(vAngle) / vRd;


            if (uRatio > uRatioSave) {
                uRatioSave = uRatio;
                ur[0] = a; //vinkel
                ur[1] = vAngle; //vEd
                ur[2] = vRd;
                ur[3] = uRatioSave; //Utilzation ratio

            }

        } //end for

        Report.updateShear(ur);
        return ur;
    } //end method


//    //Skjærarmeringsforholdet (= nødvendig skjærarmering [mm^2/m^2])
//    public static double calculateAsw(double vEd, double z, double fywd, double theta) {
//        //Ganger med 1.000.000 for å få det i mm^2/m^2
//        double rhoz = 1.0e6 * vEd * Math.tan(theta) / (z * fywd);
//        return rhoz;
//    } //end method



} //end class

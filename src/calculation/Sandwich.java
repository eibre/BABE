package calculation;

import GUI.Report;
import GUI.variables;

/**
 * Sandwichmetoden.
 * En klasse som inneholder metoden sandwich()
 * Brukes til finne armeringsmengder i betongskall
 * Returnerer en liste med lagtykkelser og armeringsmengder.
 *
 * @author Aleksander Sørvik Hanssen og Einar Raknes Brekke
 * ********************************************************
 * i - bunnlaget og har indeks 0 her
 * s - topplaget og har indeks 1 her
 */
public class Sandwich {

    public static double[] sandwich() throws SandwichException {

        //updating variables fields
        try {
            variables.getValues();
        } catch (NumberFormatException e) {
            throw new SandwichException("Wrong number format " + e.getMessage() + "\nUse . as decimal seperator.");
        }
        //Variable declaration

        boolean inclShear = variables.inclShear;
        boolean autoIncrement = variables.autoIncrement;
        boolean doubleMinimum = variables.doubleMinimum;
        boolean enoughConcreteCap = false;  //true når betongtykkkapasiter er OK i begge lag
        boolean findReinforcement = variables.findReinf;
        boolean override = variables.overrideMinReinf;

        //Concrete parameters
        double fck = variables.fck;
        double fctm = variables.fctm;
        double Ec = variables.Ec;
        double gammac = variables.gammac;
        double fcd = variables.fcd;

        //Reinforcement parameters
        int m = variables.numReinfLayers;
        double fyk = variables.fyk;
        double fyd = variables.fyd;
        double fywk = variables.fywk;
        double fywd, Asmin;
        double Es = variables.Es;
        double gammas = variables.gammas;
        double Asxi = variables.Asxi;
        double Asxs = variables.Asxs;
        double Asyi = variables.Asyi;
        double Asys = variables.Asys;

        //Geometry
        double h = variables.h;
        double ci = variables.ci;
        double cs = variables.cs;
        double ti = variables.ti;
        double ts = variables.ts;
        double cotTheta = Math.abs(variables.cotTheta);         //Retning på betongtrykkstav
        double alpha = variables.alpha;                         //Skjærarmeringshelning
        double fi = Math.abs(variables.fi * Math.PI / 180);     //Rissvinkel
        double d, z, cmid, thetaV;

        //External load vector
        double nedx = variables.nedx;
        double nedy = variables.nedy;
        double nedxy = variables.nedxy;
        double medx = variables.medx;
        double medy = variables.medy;
        double medxy = variables.medxy;
        double vedx = variables.vedx;
        double vedy = variables.vedy;


        //Shear parameters
        double fi0, vEd0, vrdc, vrds, rhoz;
        fi0 = vEd0 = vrdc = vrds = rhoz = 0.0;
        double k2 = variables.k2;


        //Konvertering av benevninger til m og kN
        h = h / 1000;
        cs = cs / 1000;
        ci = ci / 1000;
        ti = ti / 1000;
        ts = ts / 1000;
        fck = fck * 1000;
        fcd = fcd * 1000;
        fctm = fctm * 1000;
        fyk = fyk * 1000;
        fyd = fyd * 1000;
        fywk = fywk * 1000;
        Es = Es * 1000;
        Ec = Ec * 1000;
        Asxi = Asxi / 1000000;
        Asxs = Asxs / 1000000;
        Asyi = Asyi / 1000000;
        Asys = Asys / 1000000;
        thetaV = Math.atan(1 / cotTheta);

        //Kontrollerer at inputverdiene er logiske
        if (nedx == 0 && nedy == 0 && nedxy == 0 && medx == 0 && medy == 0 && medxy == 0 && vedx == 0 && vedy == 0) {
            throw new SandwichException("All forces are zero!");
        }

        if (h <= 0) {
            throw new SandwichException("Section height can not be zero or less!");
        }

        if (ti <= 0 || ts <= 0) {
            throw new SandwichException("Layer thicknesses can not be zero or less!");
        }

        if (ci + cs > h) {
            throw new SandwichException("Sum of covers can not be larger than height");
        }

        if (ti > h || ts > h) {
            throw new SandwichException("Layer thicknesses can not be larger than section height!");
        }

        if (inclShear) {
            if (vedx == 0 && vedy == 0) {
                throw new SandwichException("Shear forces can not be zero when shear effects are included!");
            }
        }

        if (!findReinforcement && Asxs == 0 && Asxi == 0 && Asys == 0 && Asyi == 0) {
            throw new SandwichException("At least one reinforcement layer must ba assigned a value if you want to check reinforcement");
        }

        //Antar at overdekningen holdes konstant selv om lagtykkelsen må økes
        fywd = fywk / gammas;
        cmid = (cs + ci) / 2;
        d = h - cmid;

        double[] c = {ci, cs};
        double[] t = {ti, ts};
        double[] y = new double[m];
        double[] Asx = new double[m];
        double[] Asy = new double[m];
        double[] Nedx = new double[m];
        double[] Nedy = new double[m];
        double[] Nedxy = new double[m];
        double[] NedxA = new double[m];
        double[] NedyA = new double[m];
        double[] Fsx = new double[m];
        double[] Fsy = new double[m];
        double[] sigmaEd = new double[m];
        double[] sigmaRd = new double[m];
        double[] thetaN = {fi, fi};

        //Asmin i m^2/m (*1000000 for å få mm^2/m)
        Asmin = Math.max(0.26 * fctm / fyk * d, 0.0013 * d);
        if (doubleMinimum) {
            Asmin = 2 * Asmin;
        }

        if (findReinforcement) {
            for (int j = 0; j < m; j++) {
                Asx[j] = 0;
                Asy[j] = 0;
            }
        } else {
            //Hvis en gitt armeringsmengde skal kontrolleres
            Asx[0] = Asxi;
            Asx[1] = Asxs;
            Asy[0] = Asyi;
            Asy[1] = Asys;
        }

        //Setter tykkelsen lik 2*c hvis den er mindre enn det.
        for (int j = 0; j < m; j++) {
            if (t[j] < 2 * c[j]) {
                t[j] = 2 * c[j];
            }
        }

        if (inclShear) {
            //Hovedskjærretning (vinkel fra x-aksen)
            //(Ved skjærberegningen virker skallet som en bjelke i dennne retningen)
            fi0 = Math.atan(vedy / vedx);
            //Hovedskjærkraft i skallets tverretning
            vEd0 = Math.sqrt(Math.pow(vedy, 2) + Math.pow(vedx, 2));
            //Bruker her minimumsarmering for å være konservativ.
            vrdc = Shear.shearCap(fck / 1000., d * 1000, fi0, Asmin, Asmin, gammac, k2, 0.0, 0.0);
        }


        int r = 0;      //antall iterasjoner r
        while (!enoughConcreteCap) {
            z = h - (t[0] + t[1]) / 2;
            for (int j = 0; j < m; j++) {
                y[j] = 0.5 * (h - t[j]);
            }//End for


            //Steg 1: Beregn membrankrefter i topplag og bunnlag
            if (inclShear) {
                //Skjærkapasitet
                if (vEd0 > vrdc) {  //Trenger skjærarmering
                    //Armeringsforhold pr kvadratmeter
                    rhoz = rhoz = 1.0e6 * vEd0 * Math.tan(thetaV) / (z * fywd);
                    //Skjærkap i bjelker med skjærarmering
                    vrds = Shear.reinforcedShearCap(z * 1000., fcd / 1000., fck / 1000., fywd / 1000, thetaV, alpha, gammas, rhoz);

                    for (int j = 0; j < m; j++) {
                        //Membrane forces
                        Nedx[j] = nedx * ((z - y[j]) / z) + Math.pow(-1, j + 1) * medx / z + 0.5 * (vedx * vedx) / (vEd0 * Math.tan(thetaV));
                        Nedy[j] = nedy * ((z - y[j]) / z) + Math.pow(-1, j + 1) * medy / z + 0.5 * (vedy * vedy) / (vEd0 * Math.tan(thetaV));
                        Nedxy[j] = nedxy * ((z - y[j]) / z) - Math.pow(-1, j + 1) * medxy / z + 0.5 * (vedx * vedy) / (vEd0 * Math.tan(thetaV));
                    }//End for
                } else {
                    for (int j = 0; j < m; j++) {
                        //Membrane forces
                        Nedx[j] = nedx * ((z - y[j]) / z) + Math.pow(-1, j + 1) * medx / z;
                        Nedy[j] = nedy * ((z - y[j]) / z) + Math.pow(-1, j + 1) * medy / z;
                        Nedxy[j] = nedxy * ((z - y[j]) / z) - Math.pow(-1, j + 1) * medxy / z;
                    }//End for
                }//End if
            } else {
                for (int j = 0; j < m; j++) {
                    //Membrane forces
                    Nedx[j] = nedx * ((z - y[j]) / z) + Math.pow(-1, j + 1) * medx / z;
                    Nedy[j] = nedy * ((z - y[j]) / z) + Math.pow(-1, j + 1) * medy / z;
                    Nedxy[j] = nedxy * ((z - y[j]) / z) - Math.pow(-1, j + 1) * medxy / z;
                }//End for
            }//End if

            //Steg 2: Krefter i armeringa

            //Krefter i armeringa når armeringa ligger eksentrisk i ytterlaget
            if (t[0] != 2 * c[0] || t[1] != 2 * c[1]) {
                NedxA[1] = (Nedx[1] * (h - t[1] / 2 - c[0]) + Nedx[0] * (t[0] / 2 - c[0])) / (h - c[0] - c[1]);
                NedxA[0] = Nedx[1] + Nedx[0] - NedxA[1];
                NedyA[1] = (Nedy[1] * (h - t[1] / 2 - c[0]) + Nedy[0] * (t[0] / 2 - c[0])) / (h - c[0] - c[1]);
                NedyA[0] = Nedy[1] + Nedy[0] - NedyA[1];

            } else {
                for (int j = 0; j < m; j++) {
                    NedxA[j] = Nedx[j];
                    NedyA[j] = Nedy[j];
                }
            } //End if

            //Hvis dette er første iterasjon snus fortegnet på rissvinkelen til å passe med skiveskjærretningen
            if (r == 0) {
                for (int j = 0; j < m; j++) {
                    if (Nedxy[j] < 0) {
                        thetaN[j] = -1 * thetaN[j];
                    }
                }
            }

            for (int j = 0; j < m; j++) {

                Fsx[j] = NedxA[j] + Nedxy[j] * Math.tan(thetaN[j]); //ihht kompendiet, det står omvendt i CEB pga ulik definisjon av rissvinkel
                Fsy[j] = NedyA[j] + Nedxy[j] / Math.tan(thetaN[j]);


                //Steg 3: Betongspenninger i hovedspenningsretning
                if (Fsx[j] <= 0 && Fsy[j] <= 0) {//Trykk i armeringa i begge retninger --> Hovedtrykkraft dimensjonerende
                    //Ingen reduksjon av kapasitet ved rent trykk
                    sigmaRd[j] = -fcd;
                    //Hovedtrykkraft i hovedtrykkretning
                    sigmaEd[j] = ((Nedx[j] + Nedy[j]) / 2 - Math.sqrt(Math.pow((Nedx[j] - Nedy[j]) / 2, 2) + Math.pow(Nedxy[j], 2))) / t[j];
                } else {    //Strekk i minst en av armeringsretningene --> Trykkfeltsteori dimensjonerende
                    //Redusert dimensjonerende fasthet. Pkt 6.5.2(2)
                    sigmaRd[j] = 0.6 * (1 - (fck / 1000) / 250) * -fcd;
                    //Trykkfeltsteori
                    if (Nedxy[j] == 0) {
                        sigmaEd[j] = Math.abs(Math.min(Nedx[j] / t[j], Nedy[j] / t[j]));  //lagt til math.abs();
                    } else {
                        sigmaEd[j] = -Nedxy[j] / (t[j] * Math.sin(thetaN[j]) * Math.cos(thetaN[j]));
                    }//End if
                }//End if

                //Økning av lagtykkelsen pga for lite trykkapasitet.
                if (sigmaEd[j] < sigmaRd[j]) {
                    if (autoIncrement) {
                        t[j] += 0.001;      //Her automatisk økning av lagtykkelsen med 1mm
                    } else {
                        //Hvis manuell økning av lagtykkelser, hopper man ut av metoden her:
                        throw new SandwichException("Layer thickness must be increased! \n\nTip: Compare concrete stress and capacity in result tab [alt+6]\n or check the auto-increase option.");
                    }
                }
            }

            //Sjekker om summen av lagtykkelsene ti + ts er større enn h
            double sumT = 0;
            for (int j = 0; j < m; j++) {
                sumT += t[j];
            }
            if (sumT > h) {
                throw new SandwichException("Sum of layer thicknesses can not\n be larger than section height.\nTip: Increase section height.");
            }

            //Kontrollerer om lagtykkelesen er tilstrekkelig => nok kapasitet i betongen
            r++;
            enoughConcreteCap = (sigmaEd[0] >= sigmaRd[0] && sigmaEd[1] >= sigmaRd[1]);  //fjerna math.abs og snudd til >
        }//End while


        if (findReinforcement) {
            //Regner ut nødvendige armeringsmengder:
            for (int j = 0; j < m; j++) {
                //Armering
                if (Math.abs(Fsx[j] / fyd) < Asmin && !override) {//mtp trykkarmering burde det her sjekkes om betongen tar det opptredende trykket
                    Asx[j] = Asmin;
                } else {
                    Asx[j] = Math.abs(Fsx[j] / fyd);
                }
                if (Math.abs(Fsy[j] / fyd) < Asmin && !override) {
                    Asy[j] = Asmin;
                } else {
                    Asy[j] = Math.abs(Fsy[j] / fyd);
                }
            }//End for
        }//End if

        Report.updateSandwich(findReinforcement, t, sigmaEd, sigmaRd, Asx, Asy, Asmin, fi0, vEd0, vrdc, vrds, rhoz);

        double[] armering = new double[8];
        armering[0] = Asx[0];
        armering[1] = Asx[1];
        armering[2] = Asy[0];
        armering[3] = Asy[1];
        armering[4] = Asmin;
        armering[5] = rhoz;
        armering[6] = t[0];
        armering[7] = t[1];

        return armering;

    }//End method sandwich
} //End class

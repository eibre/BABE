/*
 * This class contains methods the following methods:
 *
 * - strengthConcrete(fck,alfacc,gammac) 'for looking up strength parameters'
 * - strengthReinforcement(ReinfClass,gammas) 'for looking up strength 
 *   parameters'
 * - Matrix materialMatrixConcrete(E11, E22, E12, nyc) 'Calculates C for 
 *	 concrete, one lamella'
 * - materialMatrixReinfX(Esx, j) 'Calculates C for reinforcement in 
 *	 x-direction,layer j'
 * - materialMatrixReinfY(Esy, j) 'Calculates C for reinforcement in 
 *	 y-direction,layer j'
 * - transMat(theta) 'calculates transformation matrix for strains from 
 *	 xy to theta-direction'
 */
package calculation;

import Jama.Matrix;

/**
 *
 * @author Aleksander
 */
public class Materials {

    public static double[] strengthConcrete(double fck, double alphacc, double gammac) {
        double[] a = new double[7];
        double Ec, epsc2, epscu2, nConc, fctm, fcd, fcm;
        fcm = fck + 8;
        fcd = alphacc * fck / gammac;

        switch ((int) fck) {
            case 12: {
                Ec = 27000;
                epsc2 = -2.0 / 1000;
                epscu2 = -3.5 / 1000;
                nConc = 2;
                fctm = 1.6;
                break;
            }
            case 16: {
                Ec = 29000;
                epsc2 = -2.0 / 1000;
                epscu2 = -3.5 / 1000;
                nConc = 2;
                fctm = 1.9;
                break;
            }
            case 20: {
                Ec = 30000;
                epsc2 = -2.0 / 1000;
                epscu2 = -3.5 / 1000;
                nConc = 2;
                fctm = 2.2;
                break;
            }
            case 25: {
                Ec = 31000;
                epsc2 = -2.0 / 1000;
                epscu2 = -3.5 / 1000;
                nConc = 2;
                fctm = 2.6;
                break;
            }
            case 30: {
                Ec = 33000;
                epsc2 = -2.0 / 1000;
                epscu2 = -3.5 / 1000;
                nConc = 2;
                fctm = 2.9;
                break;
            }
            case 35: {
                Ec = 34000;
                epsc2 = -2.0 / 1000;
                epscu2 = -3.5 / 1000;
                nConc = 2;
                fctm = 3.2;
                break;
            }
            case 40: {
                Ec = 35000;
                epsc2 = -2.0 / 1000;
                epscu2 = -3.5 / 1000;
                nConc = 2;
                fctm = 3.5;
                break;
            }
            case 45: {
                Ec = 36000;
                epsc2 = -2.0 / 1000;
                epscu2 = -3.5 / 1000;
                nConc = 2;
                fctm = 3.8;
                break;
            }
            case 50: {
                Ec = 37000;
                epsc2 = -2.0 / 1000;
                epscu2 = -3.5 / 1000;
                nConc = 2;
                fctm = 4.1;
                break;
            }
            case 55: {
                Ec = 38000;
                epsc2 = -2.2 / 1000;
                epscu2 = -3.1 / 1000;
                nConc = 1.75;
                fctm = 4.2;
                break;
            }
            case 60: {
                Ec = 39000;
                epsc2 = -2.3 / 1000;
                epscu2 = -2.9 / 1000;
                nConc = 1.6;
                fctm = 4.4;
                break;
            }
            case 70: {
                Ec = 41000;
                epsc2 = -2.4 / 1000;
                epscu2 = -2.7 / 1000;
                nConc = 1.45;
                fctm = 4.6;
                break;
            }
            case 80: {
                Ec = 42000;
                epsc2 = -2.5 / 1000;
                epscu2 = -2.6 / 1000;
                nConc = 1.4;
                fctm = 4.8;
                break;
            }
            case 90: {
                Ec = 44000;
                epsc2 = -2.6 / 1000;
                epscu2 = -2.6 / 1000;
                nConc = 1.4;
                fctm = 5;
                break;
            }
            default: {
                Ec = 22000 * Math.pow(((fcm) / 10), 0.3);
                epsc2 = -(2 + 0.085 * Math.pow((fck - 50), 0.53)) / 1000;
                epscu2 = -(2.6 + 35 * Math.pow(((90 - fck) / 100), 4)) / 1000;
                nConc = 1.4 + 23.4 * Math.pow(((90 - fck) / 100), 4);
                if (fck <= 50) {
                    fctm = 0.3 * Math.pow(fck, (2 / 3));
                } else {
                    fctm = 2.12 * Math.log(1 + (fcm / 10));
                }
                break;
            }
        }
        a[0] = Ec;
        a[1] = epsc2;
        a[2] = epscu2;
        a[3] = nConc;
        a[4] = fctm;
        a[5] = fcd;
        a[6] = fcm;
        return a;


    }

    public static double[] strengthReinforcement(int reinfClass, double gammas) {
        double[] a = new double[7];
        double fyk, fyd, Es, epsyd, epsud, fud, k;
        /*
         * reinfClass = 0: B500NA
         * reinfClass = 1: B500NB
         * reinfClass = 2: B500NC
         */
        switch (reinfClass) {
            case 0: {
                fyk = 500;                  //[MPa]
                epsud = 0.01;               //NA.3.2.7 Tab NA.3.5(901)
                fud = 1.01 * fyk / gammas;  //[MPa] NA.3.2.7
                k = 1.01;                   //Fasthetsøkning fra Tab NA.3.5(901)
                break;
            }
            case 1: {
                fyk = 500;                  //[MPa]
                epsud = 0.02;               //NA.3.2.7 Tab NA.3.5(901)
                fud = 1.02 * fyk / gammas;  //[MPa] NA.3.2.7
                k = 1.02;                   //Fasthetsøkning fra Tab NA.3.5(901)
                break;
            }
            case 2: {
                fyk = 500;                  //[MPa]
                epsud = 0.03;               //NA.3.2.7 Tab NA.3.5(901)
                fud = 1.04 * fyk / gammas;  //[MPa] NA.3.2.7
                k = 1.04;                   //Fasthetsøkning fra Tab NA.3.5(901)
                break;
            }
            default: {
                fyk = 0;                //[MPa]
                fyd = 0;                //[MPa] Figur 3.8
                Es = 0;                 //[MPa] Pkt 3.2.7(4)
                epsyd = 0;              //Figur 3.8
                epsud = 0;              //NA.3.2.7 Tab NA.3.5(901)
                fud = 0;                //[MPa] NA.3.2.7
                k = 1.;
                break;
            }
        }
        fyd = fyk / gammas;         //[MPa] Figur 3.8
        Es = 2.0E5;                 //[MPa] Pkt 3.2.7(4)
        epsyd = fyd / Es;           //Figur 3.8

        a[0] = fyk;
        a[1] = fyd;
        a[2] = Es;
        a[3] = epsyd;
        a[4] = epsud;
        a[5] = fud;
        a[6] = k;
        return a;
    }

    public static Matrix materialMatrixConcrete(double E11, double E22, double E12, double nyc) {
        double[][] C = new double[3][3];
        C[0][0] = 1 / (1 - Math.pow(nyc, 2)) * E11;
        C[1][0] = 1 / (1 - Math.pow(nyc, 2)) * nyc * E12;
        C[2][0] = 1 / (1 - Math.pow(nyc, 2)) * 0;

        C[0][1] = 1 / (1 - Math.pow(nyc, 2)) * nyc * E12;
        C[1][1] = 1 / (1 - Math.pow(nyc, 2)) * E22;
        C[2][1] = 1 / (1 - Math.pow(nyc, 2)) * 0;

        C[0][2] = 1 / (1 - Math.pow(nyc, 2)) * 0;
        C[1][2] = 1 / (1 - Math.pow(nyc, 2)) * 0;
        C[2][2] = 1 / (1 - Math.pow(nyc, 2)) * E12 * (1 - nyc) / 2;
        /*
        System.out.println("Beregning av materialmatrise");
        System.out.println("Input: E11="+E11+" E22="+E22+" E12="+E12+" nyc="+nyc);
        System.out.println(C[0][0]);
        System.out.println(C[1][0]);
        System.out.println(C[2][0]);
        System.out.println(C[0][1]);
        System.out.println(C[1][1]);
        System.out.println(C[2][1]);
        System.out.println(C[0][2]);
        System.out.println(C[1][2]);
        System.out.println(C[2][2]);
        */
        return new Matrix(C);
    }

    public static double[][] materialMatrixReinfX(double[] Esx, int j) {
        double[][] Csx = {
            {Esx[j], 0, 0},
            {0, 0, 0},
            {0, 0, 0}
        };

        return Csx;
    }

    public static double[][] materialMatrixReinfY(double[] Esy, int j) {
        double[][] Csy = {
            {0, 0, 0},
            {0, Esy[j], 0},
            {0, 0, 0}
        };

        return Csy;
    }

    public static double[][] transMat(double theta) {
        double c = Math.pow(Math.cos(theta), 2);
        double s = Math.pow(Math.sin(theta), 2);
        double s2 = Math.sin(2 * theta);

        double[][] T = {{c, s, s2 / 2},
            {s, c, -s2 / 2},
            {-s2, s2, (c - s)}};
        return T;
    }

    public static Matrix reinfTensions(Matrix epsS, int reinfModel, double Es, double epsyd, double epsud, double fyd) {
        Matrix sigmaS = new Matrix(3, 1);
        double[][] sig = new double[3][1];
        double k = GUI.variables.k - 1.;

        //Dimensjonerigsforutsetninger etter pkt 3.2.7

        //"3.2.7(2)a) En stigende øvre del med en grensetøyning, epsud, og en
        //største spenning, fud = k*fyk/gammas
        if (reinfModel == 0) {
            for (int x = 0; x < 2; x++) {
                if (-1 * epsyd <= epsS.get(x, 0) && epsS.get(x, 0) <= epsyd) {
                    sig[x][0] = Es * epsS.get(x, 0);
                } else if (epsyd < epsS.get(x, 0) && epsS.get(x, 0) <= epsud) {
                    sig[x][0] = (k * epsS.get(x, 0) * fyd) / (epsud - epsyd) + fyd - (k * fyd * epsyd) / (epsud - epsyd);
                } else if (-epsud <= epsS.get(x, 0) && epsS.get(x, 0) <= -epsyd) {
                    sig[x][0] = (k * epsS.get(x, 0) * fyd) / (epsud - epsyd) - fyd + (k * fyd * epsyd) / (epsud - epsyd);
                }
            }
        } //"3.2.7(2)b) En horisontal øvre del der det ikke er nødvendig å påvise
        //grensetøyning"
        else if (reinfModel == 1) {
            for (int x = 0; x < 2; x++) {
                if (-1 * epsyd <= epsS.get(x, 0) && epsS.get(x, 0) <= epsyd) {
                    sig[x][0] = Es * epsS.get(x, 0);
                } else if (epsS.get(x, 0) >= epsyd) {
                    sig[x][0] = fyd;
                } else if (epsS.get(x, 0) <= (-1) * epsyd) {
                    sig[x][0] = (-1) * fyd;
                }
            }
        }
        sig[2][0] = 0;
        sigmaS = new Matrix(sig);
        return sigmaS;
    }
} //end class

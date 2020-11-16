package calculation;

import GUI.Report;
import GUI.variables;
import Jama.Matrix;
import java.text.DecimalFormat;
import java.util.Set;

/**
 * ITERASJONSMETODEN
 * Denne klassen innholder 1 metode:
 * iteration()
 *
 * @author Aleksander og Einar
 */
public class IterationMethod {

    /**
     * Metoden iteratoin()
     * Returner en matrise med tall formatert som tekst som skal skrives ut.
     * Metoder som er benyttet av iteration():
     * variables.getValues()
     * JAMA-metoder
     * Report.updateInput()
     */
    public static String[][] iteration() throws IterationException {

        /*
         * Alle input-variabler skal gis med sine 'naturlige' benevninger [N og mm]
         * r - iteration number counter
         * t - number of force elements which has converged
         * abort - true when t = 6 or when r = maxiter
         * n - number of lamella
         * beta - convergence criterion
         *
         *
         * maxiter - user defined maximum number of iterations
         * m - number of reinforcement layers
         */

        String[][] tableValues = new String[5][6];

        DecimalFormat nullDec = new DecimalFormat("0");
        DecimalFormat prosent = new DecimalFormat("# %");
        DecimalFormat promille = new DecimalFormat("0.0 ‰");

        //Method specific parameters/run time parameters

        try {
            variables.getValues();
        } catch (NumberFormatException e) {
            throw new IterationException("Wrong number format " + e.getMessage() + "\nUse . as decimal seperator.");
        }
        int r = 0; //number of runs
        int t = 0; //number of forces that has converged
        boolean abort = false;
        int n = variables.numLamella;
        double beta = variables.beta;
        int maxiter = variables.maxiter;

        //Concrete parameters
        double fck = variables.fck;
        double fcd = variables.fcd;
        double Ec = variables.Ec;
        double nyc = variables.nyc;
        double epsc2 = variables.epsc2;
        double epscu2 = variables.epscu2;
        double nConc = variables.nConc;
        double URcEps, URcSig;


        //Reinforcement parameters
        int m = variables.numReinfLayers;
        int reinfModel = variables.reinfModel;
        double epsyd = variables.epsyd;
        double fyk = variables.fyk;
        double fyd = variables.fyd;
        double fud = variables.fud;
        double Es = variables.Es;
        double epsud = variables.epsud;
        double Asxi = variables.Asxi / 1e6;
        double Asxs = variables.Asxs / 1e6;
        double Asyi = variables.Asyi / 1e6;
        double Asys = variables.Asys / 1e6;

        //Geometry
        double deltah;
        double h = variables.h;
        double ci = variables.ci;
        double cs = variables.cs;

        //External load vector
        double nedx = variables.nedx;
        double nedy = variables.nedy;
        double nedxy = variables.nedxy;
        double medx = variables.medx * -1; //-1 pga. forskjellig retnining på moment i litteratur og EC2.
        double medy = variables.medy * -1; //-1 pga. forskjellig retnining på moment i litteratur og EC2.
        double medxy = variables.medxy;



        //Kontrollerer at inputverdiene er logiske
        if (nedx == 0 && nedy == 0 && nedxy == 0 && medx == 0 && medy == 0 && medxy == 0) {
            throw new IterationException("All forces are zero!");
        }

        if (h <= 0) {
            throw new IterationException("Section height can not be zero (or less)!");
        }

        if (ci <= 0 || cs <= 0) {
            throw new IterationException("Cover can not be zero (or less)!");
        }

        if ((ci + cs) >= h) {
            throw new IterationException("Sum of covers can not be larger than height");
        }



        //Initiation
        double[] theta2 = new double[n];
        double[] Esx = new double[m];
        double[] Esy = new double[m];
        double[] zc = new double[n];
        double[] zs = new double[m];
        double[] Asx = {Asxi, Asxs};
        double[] Asy = {Asyi, Asys};
        double[][] R = {{nedx}, {nedy}, {nedxy}, {medx}, {medy}, {medxy}};
        Matrix extLoad = new Matrix(R);
        Matrix Ks = new Matrix(6, 6);
        Matrix Kc = new Matrix(6, 6);
        Matrix K = new Matrix(6, 6);
        Matrix[] Cc = new Matrix[n];
        Matrix[] Cp = new Matrix[n];
        Matrix[] Ac = new Matrix[n];
        Matrix[] As = new Matrix[m];
        Matrix[] T = new Matrix[n];
        Matrix Csx, Csy;
        Matrix[] sigmaP = new Matrix[n];
        Matrix[] sigmaC = new Matrix[n];
        Matrix[] sigmaS = new Matrix[m];
        Matrix epsT = new Matrix(6, 1);
        Matrix[] epsP = new Matrix[n];
        Matrix[] epsC = new Matrix[n];
        Matrix[] epsS = new Matrix[m];
        Matrix S, Sc, Ss;
        double[] E11 = new double[n];
        double[] E22 = new double[n];
        double[] E12 = new double[n];
        double[] maxdiff = new double[6];
        double[] c = {ci, cs};

        //Konvertering av benevninger
        h = h / 1000;
        fck = fck * 1000;
        fcd = fcd * 1000;
        fyk = fyk * 1000;
        fyd = fyd * 1000;
        fud = fud * 1000;
        Es = Es * 1000;
        Ec = Ec * 1000;
        for (int j = 0; j < m; j++) {
            c[j] = c[j] / 1000;
        }
        deltah = h / n;


        //z og A-matrise for betongen
        for (int i = 1; i <= n; i++) {
            zc[i - 1] = -0.5 * (h - deltah - 2 * (i - 1) * deltah);
            double[][] AcSub = {{1, 0, 0, -zc[i - 1], 0, 0},
                {0, 1, 0, 0, -zc[i - 1], 0},
                {0, 0, 1, 0, 0, -zc[i - 1]}};
            Ac[i - 1] = new Matrix(AcSub);
        }

        //z og A-matrise for armeringslag
        for (int j = 1; j <= m; j++) {
            zs[j - 1] = (h / 2 - c[j - 1]) * Math.pow(-1, j);
            double[][] AsSub = {{1, 0, 0, -zs[j - 1], 0, 0},
                {0, 1, 0, 0, -zs[j - 1], 0},
                {0, 0, 0, 0, 0, 0}};
            As[j - 1] = new Matrix(AsSub);
        }


        //Her starter iterasjonen, den går så lenge abort == false.
        while (!abort) {

            //utnulling av matriser for hver iterasjon
            Csx = new Matrix(3, 3);
            Csy = new Matrix(3, 3);
            Kc = new Matrix(6, 6);
            Ks = new Matrix(6, 6);
            S = new Matrix(6, 1);
            Sc = new Matrix(6, 1);
            Ss = new Matrix(6, 1);
            t = 0;

            //Betongens stivhetsbidrag Kc, lager en matrise for hvert lag og summerer
            for (int i = 0; i < n; i++) {
                if (r == 0) {
                    E11[i] = Ec;
                    E22[i] = Ec;
                    E12[i] = Ec;
                    Cc[i] = Materials.materialMatrixConcrete(E11[i], E22[i], E12[i], nyc);
                } //end if
                Kc = Ac[i].transpose().times(Cc[i]).times(Ac[i]).times(deltah).plus(Kc);
            } //end for

            if (Kc.get(2, 2) == 0) {
                Kc.set(2, 2, 1);
            }
            if (Kc.get(5, 5) == 0) {
                Kc.set(5, 5, 1);
            }

//¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤
            //Kc.print(10, 5);
//¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤

            // Armeringas stivhetsbidrag
            if (r == 0) {   //Initielt
                for (int j = 0; j < m; j++) {
                    Esx[j] = Es;
                    Esy[j] = Es;
                }
            }
            for (int j = 0; j < m; j++) {
                Csx = new Matrix(Materials.materialMatrixReinfX(Esx, j));
                Csy = new Matrix(Materials.materialMatrixReinfY(Esy, j));
                Ks = As[j].transpose().times(Csx).times(As[j]).times(Asx[j]).plus((As[j]).transpose().times(Csy).times(As[j]).times(Asy[j])).plus(Ks);
            }

            K = Ks.plus(Kc);

            //Hvis K er singulær
            if (K.rank() < K.getRowDimension()) {
                System.err.println("Stiffness matrix K is singular! Stopped after " + r + " iterations.");
                throw new IterationException("Stiffness matrix K is singular!Stopped after " + r + " iterations.\n Tip: Increase reinforcement amount.");
            }

            // Tøyning/krumning i skallmiddelflaten
            epsT = K.solve(extLoad);
            // Betongspenninger
            for (int i = 0; i < n; i++) {
                epsC[i] = Ac[i].times(epsT);

//¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤
                //epsC[i].print(10, 5);
//¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤¤

                /*
                epsP[i].set(0, 0, ((epsC[i].get(0, 0) + epsC[i].get(1, 0)) / 2 + Math.sqrt(Math.pow(epsC[i].get(0, 0) - epsC[i].get(1, 0), 2) / 4 + Math.pow(epsC[i].get(2, 0), 2) / 4)));
                epsP[i].set(1, 0, ((epsC[i].get(0, 0) + epsC[i].get(1, 0)) / 2 - Math.sqrt(Math.pow(epsC[i].get(0, 0) - epsC[i].get(1, 0), 2) / 4 + Math.pow(epsC[i].get(2, 0), 2) / 4)));
                epsP[i].set(2, 0, Math.abs(epsP[i].get(0, 0) - epsP[i].get(1, 0)));
                 *

                System.out.println("EpsilonP med teori for plan tøyningstilstand:");
                epsP[i].print(10, 5);
*/
                //Hvis epsx = epsy endres vinkelen til 45 grader for å ungå 0/0
                if (epsC[i].get(0, 0) == epsC[i].get(1, 0)) {
                    theta2[i] = Math.PI / 4;
                } else {
                    theta2[i] = 0.5 * Math.atan(epsC[i].get(2, 0) / (epsC[i].get(0, 0) - epsC[i].get(1, 0)));
                }
                T[i] = new Matrix(Materials.transMat(theta2[i]));
                epsP[i] = T[i].times(epsC[i]);

                /*
                Matrix epsilonP = new Matrix(6, 1);
                epsilonP = T[i].times(epsC[i]);
                System.out.println("EpsilonP fra transformasjonsmatrisa:");
                epsilonP.print(10,5);
                 * 
                 */

                double[][] sigP = new double[3][1];

                //Materialmodell for betong etter NS-EN 1992-1-1 Pkt 3.1.7
                for (int k = 0; k < 2; k++) {
                    if (epsP[i].get(k, 0) >= 0) {
                        sigP[k][0] = 0;
                    } else if (epsc2 <= epsP[i].get(k, 0) && epsP[i].get(k, 0) < 0) {
                        sigP[k][0] = -fcd * (1 - Math.pow((1 - epsP[i].get(k, 0) / epsc2), nConc));
                    } else if (epsP[i].get(k, 0) < epsc2) {
                        sigP[k][0] = -fcd;
                    }
                    sigP[2][0] = 0;
                }
                sigmaP[i] = new Matrix(sigP);
                sigmaC[i] = T[i].transpose().times(sigmaP[i]);

            }
            //Armeringsspenninger
            //Dimensjonerigsforutsetninger etter pkt 3.2.7
            for (int j = 0; j < m; j++) {
                epsS[j] = As[j].times(epsT);
                sigmaS[j] = Materials.reinfTensions(epsS[j], reinfModel, Es, epsyd, epsud, fyd);
            }


            //Indre krefter i betongen
            for (int i = 0; i < n; i++) {
                double[][] sc = new double[6][1];
                for (int k = 0; k < Sc.getRowDimension(); k++) {
                    if (k <= 2) {
                        sc[k][0] = deltah * sigmaC[i].get(k, 0);
                    } else {
                        sc[k][0] = (-1) * zc[i] * deltah * sigmaC[i].get(k - 3, 0);
                    }
                }
                Sc.plusEquals(new Matrix(sc));
            }

            //Indre krefter i armeringa
            for (int j = 0; j < m; j++) {
                double[][] ss = new double[6][1];
                ss[0][0] = Asx[j] * sigmaS[j].get(0, 0);
                ss[1][0] = Asy[j] * sigmaS[j].get(1, 0);
                ss[2][0] = 0;
                ss[3][0] = (-1) * zs[j] * Asx[j] * sigmaS[j].get(0, 0);
                ss[4][0] = (-1) * zs[j] * Asy[j] * sigmaS[j].get(1, 0);
                ss[5][0] = 0;
                Ss.plusEquals(new Matrix(ss));
            }

            //Indre kraftvektor
            S = Sc.plus(Ss);

            //Kontrollerer konvergens
            for (int k = 0; k < 6; k++) {
                maxdiff[k] = Math.abs((extLoad.get(k, 0) - S.get(k, 0)) / extLoad.get(k, 0));
                if (maxdiff[k] <= beta || extLoad.get(k, 0) == 0) {
                    t += 1; //legger til 1 på t for hver kraft som har konvergert. konvergens når t == 6
                }
            }

            if (t == 6) {
                abort = true;
                break;
            }

            if (r >= maxiter) {
                abort = true;
                throw new IterationException("No convergence! Maximal amount of iterations reached.");
            }
            r += 1;
            GUI.gui.statusBar.setText("Calculating... iteration #" + r);


            //Oppdaterer materialmatriser som brukes i toppen av løkke for å beregne K
            //betong:
            for (int i = 0; i < n; i++) {
                if (epsP[i].get(0, 0) == 0) {
                    E11[i] = Ec;
                } else {
                    E11[i] = sigmaP[i].get(0, 0) / epsP[i].get(0, 0);
                }
                if (epsP[i].get(1, 0) == 0) {
                    E22[i] = Ec;
                } else {
                    E22[i] = sigmaP[i].get(1, 0) / epsP[i].get(1, 0);
                }
                E12[i] = 0.5 * (E11[i] + E22[i]);
                Cp[i] = Materials.materialMatrixConcrete(E11[i], E22[i], E12[i], nyc);
                Cc[i] = T[i].transpose().times(Cp[i]).times(T[i]);
            }

            //stål:
            for (int j = 0; j < m; j++) {
                if (epsS[j].get(0, 0) == 0) {
                    Esx[j] = Es;
                } else {
                    Esx[j] = sigmaS[j].get(0, 0) / epsS[j].get(0, 0);
                }
                if (epsS[j].get(1, 0) == 0) {
                    Esy[j] = Es;
                } else {
                    Esy[j] = sigmaS[j].get(1, 0) / epsS[j].get(1, 0);
                }//end if
            }//end for

        }//end while




        //GENERER VERDIER TIL RESULTATER

        if (abort && r < maxiter) {
            double minEpsCon = 0;
            double minSigCon = 0;

            //Finner maksimal trykktøyning og spenning i betongen og skrive ut utnyttingsgraden
            for (int i = 0; i < n; i++) {
                for (int a = 0; a < 2; a++) {
                    if (epsP[i].get(a, 0) < minEpsCon) {
                        minEpsCon = epsP[i].get(a, 0);
                    }
                    if (sigmaP[i].get(a, 0) < minSigCon) {
                        minSigCon = sigmaP[i].get(a, 0);
                    }
                }
            }//End for

            //utnyttingsgrader betong
            URcEps = Math.abs(minEpsCon / epscu2);
            URcSig = Math.abs(minSigCon / fcd);

            double[][] URsSig = new double[2][2]; //utnyttingsgrad tøyning
            double[][] URsEps = new double[2][2]; //utnyttingsgrad spenning
            for (int j = 0; j < m; j++) {
                for (int a = 0; a < 2; a++) {
                    if (reinfModel == 0) { //Armeringsmodell A krever at man må sjekke maks tøyning i armeirng.
                        //Utnyttingsgrader armering ( lag j , x-retning: a=0 ,  y-retning: a=1 )
                        URsSig[j][a] = Math.abs(sigmaS[j].get(a, 0) / fud);
                        URsEps[j][a] = Math.abs(epsS[j].get(a, 0) / epsud);
                    } else {
                        URsSig[j][a] = Math.abs(sigmaS[j].get(a, 0) / fyd);
                    }
                }
            }


            //Navn på lag
            tableValues[0][0] = "Top x";
            tableValues[1][0] = "Top y";
            tableValues[2][0] = "Bottom x";
            tableValues[3][0] = "Bottom y";
            tableValues[4][0] = "Concrete (princ.)";

            //armeringsarealer
            tableValues[0][1] = nullDec.format(Asx[1] * 1e6); //top x
            tableValues[1][1] = nullDec.format(Asy[1] * 1e6); //top y
            tableValues[2][1] = nullDec.format(Asx[0] * 1e6); //bottom x
            tableValues[3][1] = nullDec.format(Asy[0] * 1e6); //bottom y
            tableValues[4][1] = "---";  //concrete

            //spenninger
            tableValues[0][2] = nullDec.format(sigmaS[1].get(0, 0) / 1000); //top x
            tableValues[1][2] = nullDec.format(sigmaS[1].get(1, 0) / 1000); //top y
            tableValues[2][2] = nullDec.format(sigmaS[0].get(0, 0) / 1000); //bottom x
            tableValues[3][2] = nullDec.format(sigmaS[0].get(1, 0) / 1000); //bottom y
            tableValues[4][2] = nullDec.format(minSigCon / 1000); //Concrete

            //Utnyttingsgrader spenning
            tableValues[0][3] = prosent.format(URsSig[1][0]); //top x
            tableValues[1][3] = prosent.format(URsSig[1][1]); //top y
            tableValues[2][3] = prosent.format(URsSig[0][0]); //bottom x
            tableValues[3][3] = prosent.format(URsSig[0][1]); //bottom y
            tableValues[4][3] = prosent.format(URcSig); //

            //tøyninger
            tableValues[0][4] = promille.format(epsS[1].get(0, 0)); //top x
            tableValues[1][4] = promille.format(epsS[1].get(1, 0)); //top y
            tableValues[2][4] = promille.format(epsS[0].get(0, 0)); //bottom x
            tableValues[3][4] = promille.format(epsS[0].get(1, 0)); //bottom y
            tableValues[4][4] = promille.format(minEpsCon); //concrete

            if (reinfModel == 0) { //regner ut utnyttingsgrader hvis
                //Utnyttingsgrader tøyning
                GUI.gui.jLabel113.setVisible(false);
                tableValues[0][5] = prosent.format(URsEps[1][0]); //top x
                tableValues[1][5] = prosent.format(URsEps[1][1]); //top y
                tableValues[2][5] = prosent.format(URsEps[0][0]); //bottom x
                tableValues[3][5] = prosent.format(URsEps[0][1]); //bottom y
                tableValues[4][5] = prosent.format(URcEps); //Concrete
            } else if (reinfModel == 1) {
                //Utnyttingsgrader tøyning
                GUI.gui.jLabel113.setVisible(true);
                tableValues[0][5] = "---"; //top x
                tableValues[1][5] = "---"; //top y
                tableValues[2][5] = "---"; //bottom x
                tableValues[3][5] = "---"; //bottom y
                tableValues[4][5] = prosent.format(URcEps); //Concrete
            }

            //nuller ut rader der hvor armeringsmengden er null!
            if (Asx[1] == 0) {
                for (int k = 2; k < 6; k++) {
                    tableValues[0][k] = "---";
                }
            }
            if (Asy[1] == 0) {
                for (int k = 2; k < 6; k++) {
                    tableValues[1][k] = "---";
                }
            }
            if (Asx[0] == 0) {
                for (int k = 2; k < 6; k++) {
                    tableValues[2][k] = "---";
                }
            }
            if (Asy[0] == 0) {
                for (int k = 2; k < 6; k++) {
                    tableValues[3][k] = "---";
                }
            }
            Report.updateInput();
            Report.updateIteration(tableValues, r);

        }//End if
        return tableValues;
    }//End method iteration
}//End class IterasjonsMetoden0

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GUI;

import calculation.Materials;

/**
 *
 * @Written by: Einar
 */
public class variables extends gui {
    //Programnavn og verson
    public static String appName = "BABE v1.01";

    //Variabler til beregningsklasser:
    public static double beta;
    public static int numLamella;
    public static int numReinfLayers;
    public static int maxiter;
    public static int reinfModel;
    public static int reinfClass;

    //Valgparametere:
    public static boolean inclShear;
    public static boolean inclCalc;
    public static boolean autoIncrement;
    public static boolean doubleMinimum;
    public static boolean findReinf;
    public static boolean overrideMinReinf;

    //forces:
    public static double nedx;
    public static double nedy;
    public static double nedxy;
    public static double medx;
    public static double medy;
    public static double medxy;
    public static double vedx;
    public static double vedy;

    //Geometry:
    public static double h;
    public static double cs;
    public static double ci;
    public static double ts;
    public static double ti;
    public static double Asxs;
    public static double Asys;
    public static double Asxi;
    public static double Asyi;
    public static double Asw;

    //shear:
    public static double fywk;
    public static double fywd;
    public static double alpha;
    public static double cotTheta;
    public static double k1compression;
    public static double k1tension;
    public static double k2;

    //material:
    public static double nyc;
    public static double gammac;
    public static double gammas;
    public static double alphacc;
    public static double fck;
    public static double fyk;
    public static double fyd;
    public static double Ec;
    public static double Es;
    public static double epsc2;
    public static double epscu2;
    public static double fi;

    //slå opp i Materials
    static double[] materialsConcrete;
    static double[] materialsSteel;
    public static double nConc;
    public static double fctm;
    public static double fcd;
    public static double fcm;


    public static double fud;
    public static double epsud;
    public static double epsyd;
    public static double k;

    public static void getValues() throws NumberFormatException {
        beta = Double.parseDouble(jTextFieldBeta.getText());
        numLamella = Integer.parseInt(jTextFieldN.getText());
        numReinfLayers = 2;
        maxiter = Integer.parseInt(jTextFieldMaxiter.getText());
        reinfModel = jComboBoxModel.getSelectedIndex();
        reinfClass = jComboBoxClass.getSelectedIndex();

        //Booleans:
        inclShear = jCheckBoxShear.isSelected();
        autoIncrement = jCheckBoxAutoInc.isSelected();
        doubleMinimum = jCheckBoxDoubleMinimum.isSelected();
        findReinf = findReinforcement.isSelected();
        overrideMinReinf = jCheckBoxOverride.isSelected();

        //Forces:
        nedx = Double.parseDouble(jTextFieldNx.getText());
        nedy = Double.parseDouble(jTextFieldNy.getText());
        nedxy = Double.parseDouble(jTextFieldNxy.getText());
        medx = Double.parseDouble(jTextFieldMx.getText());
        medy = Double.parseDouble(jTextFieldMy.getText());
        medxy = Double.parseDouble(jTextFieldMxy.getText());
        vedx = Double.parseDouble(jTextFieldVx.getText());
        vedy = Double.parseDouble(jTextFieldVy.getText());

        //Geometry:
        h = Double.parseDouble(jTextFieldH.getText());
        cs = Double.parseDouble(jTextFieldCs.getText());
        ci = Double.parseDouble(jTextFieldCi.getText());
        ts = Double.parseDouble(jTextFieldTs.getText());
        ti = Double.parseDouble(jTextFieldTi.getText());
        Asxs = Double.parseDouble(jTextFieldAsxs.getText());
        Asys = Double.parseDouble(jTextFieldAsys.getText());
        Asxi = Double.parseDouble(jTextFieldAsxi.getText());
        Asyi = Double.parseDouble(jTextFieldAsyi.getText());
        Asw = Double.parseDouble(jTextFieldAsw.getText());

        //shear:
        fywk = Double.parseDouble(jTextFieldFywk.getText());
        fywd = fywk/gammas;
        alpha = Double.parseDouble(jTextFieldAlpha.getText());
        cotTheta = Double.parseDouble(jTextFieldCotT.getText());
        k1compression = Double.parseDouble(jTextFieldK1comp.getText());
        k1tension = Double.parseDouble(jTextFieldK1tens.getText());
        k2 = Double.parseDouble(jTextFieldK2.getText());

        //material:
        nyc = Double.parseDouble(jTextFieldNyc.getText());
        gammac = Double.parseDouble(jTextFieldGc.getText());
        gammas = Double.parseDouble(jTextFieldGs.getText());
        alphacc = Double.parseDouble(jTextFieldAcc.getText());
        fck = Double.parseDouble(jTextFieldFck.getText());
        fyk = Double.parseDouble(jTextFieldFyk.getText());
        fyd = fyk/gammas;

        Ec = Double.parseDouble(jTextFieldEcm.getText());
        Es = Double.parseDouble(jTextFieldEsk.getText());
        epsc2 = Double.parseDouble(jTextFieldEpsC.getText()) / -1000;
        epscu2 = Double.parseDouble(jTextFieldEpsCu.getText()) / -1000;
        fi = Double.parseDouble(jTextFieldPhi.getText());

        //slå opp i Materials
        materialsConcrete = Materials.strengthConcrete(fck, alphacc, gammac);
        materialsSteel = Materials.strengthReinforcement(reinfClass, gammas);
        nConc = materialsConcrete[3];
        fctm = materialsConcrete[4];
        fcd = alphacc*fck/gammac;
        fcm = materialsConcrete[6];

        fud = materialsSteel[5];
        epsud = Double.parseDouble(jTextFieldEpsUd.getText()) / 100;
        epsyd = materialsSteel[3];
        k = materialsSteel[6];

    } //end method


} //end class variables

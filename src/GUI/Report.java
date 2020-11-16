/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package GUI;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * REPORT
 * Klasse som skal oppdatere "Report"-fanen i gui med beregningsdata.
 * Rapporten er delt inn i header, input, sandwich, iterasjon og footer.
 * 1 Header: html-kode, overskrift, dato og inputfiladresser
 * 2 Input: Oversikt over alle inputverdier
 * 3 Sandwich: Viser beregningsresultater fra Sandwichmetoden
 * 4 Iterasjon: Viser beregningsresultater fra Iterasjonsmetoden
 * 5 Footer: lukker html-tager
 * Hver del har sin egen metode som returnerer en string med html-kode.
 *
 * @author Einar Raknes Brekke
 */
public class Report {
    //fields
    public static boolean languageEnglish;
    public static String header="<html><head><meta http-equiv='Content-Type' content='text/html; charset=utf-8' /><title>Beregningsrapport</title></head><h1>Beregningsresultater</h1>";
    public static String headerEnglish="<html><h1>Calculation results</h1>";
    public static String input="";
    public static String inputEnglish="";
    public static String sandwich="";
    public static String sandwichEnglish="";
    public static String iteration="";
    public static String iterationEnglish="";
    public static String shear="";
    public static String shearEnglish="";
    public static String footer ="<p>Calculated with: "+variables.appName+"</p></body></html>";

    //Desimalformatering:
    private static DecimalFormat nullDec = new DecimalFormat("#");
    private static DecimalFormat oneDec = new DecimalFormat("0.0");
    private static DecimalFormat twoDec = new DecimalFormat("0.00");
    private static DecimalFormat percent = new DecimalFormat("0.0 %");
    private static DecimalFormat permille = new DecimalFormat("0.0 ‰");


    //header inneholder HTML-tager + dato og filnavn
    public static void updateHeader(String fileNameSave, String fileNameOpen) {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        String dateStr = df.format(date);
        header = "<html><h1>Beregningsresultater</h1>";
        header = header
                + "<p>Dato: " + dateStr + "<br />";

        if (!fileNameSave.isEmpty()) {
            header = header + "Lagret inputfil: " + fileNameSave + "<br />";
        }
        if (!fileNameOpen.isEmpty()) {
            header = header + "Åpnet inputfil: " + fileNameOpen;
        }

        header = header + "</p>";

    } //end method header()

        //headerEnglish inneholder HTML-tager + dato og filnavn på engelsk
    public static void updateHeaderEnglish(String fileNameSave, String fileNameOpen) {
        SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
        Date date = new Date();
        String dateStr = df.format(date);
        headerEnglish = "<html><h1>Calculation Results</h1>";
        headerEnglish = headerEnglish
                + "<p>Date: " + dateStr + "<br />";

        if (!fileNameSave.isEmpty()) {
            headerEnglish = headerEnglish + "Saved inputfile: " + fileNameSave + "<br />";
        }
        if (!fileNameOpen.isEmpty()) {
            headerEnglish = headerEnglish + "Opened inputfile: " + fileNameOpen;
        }

        headerEnglish = headerEnglish + "</p>";

    } //end method headerEnglish()

    public static void updateInput() {


        //henter inputvariablene fra variables og formaterer de
        GUI.variables.getValues();
        //betong;
        String fck = nullDec.format(variables.fck);
        String fctm = nullDec.format(variables.fctm);
        String ecm = nullDec.format(variables.Ec);
        String gc = twoDec.format(variables.gammac);
        String acc = twoDec.format(variables.alphacc);
        String ecu2 = permille.format(variables.epscu2);
        String ec2 = permille.format(variables.epsc2);
        String fi = oneDec.format(variables.fi);

        //armering:
        String fyk = nullDec.format(variables.fyk);
        String esk = nullDec.format(variables.Es);
        String gs = twoDec.format(variables.gammas);
        String model;
        if (variables.reinfModel == 0) {
            model = "a)";
        } else if (variables.reinfModel == 1) {
            model = "b)";
        } else {
            model = "";
        }
        String reClass;
        if (variables.reinfClass == 0) {
            reClass = "A";
        } else if (variables.reinfClass == 1) {
            reClass = "B";
        } else if (variables.reinfClass == 2) {
            reClass = "C";
        } else {
            reClass = "";
        }

        //Geometri
        String h = nullDec.format(variables.h);
        String cs = nullDec.format(variables.cs);
        String ci = nullDec.format(variables.ci);
        String ts = nullDec.format(variables.ts);
        String ti = nullDec.format(variables.ti);

        //Krefter
        String nedx = nullDec.format(variables.nedx);
        String nedy = nullDec.format(variables.nedy);
        String nedxy = nullDec.format(variables.nedxy);
        String vedx = nullDec.format(variables.vedx);
        String vedy = nullDec.format(variables.vedy);
        String medx = nullDec.format(variables.medx);
        String medy = nullDec.format(variables.medy);
        String medxy = nullDec.format(variables.medxy);

        //Skjærparametere
        String cot = nullDec.format(variables.cotTheta);
        String a = nullDec.format(variables.alpha);
        String fywk = nullDec.format(variables.fywk);
        String k1C = twoDec.format(variables.k1compression);
        String k1T = twoDec.format(variables.k1tension);
        String k2 = twoDec.format(variables.k2);

        //Armeringsmengder
        String Asxs = nullDec.format(variables.Asxs);
        String Asys = nullDec.format(variables.Asys);
        String Asxi = nullDec.format(variables.Asxi);
        String Asyi = nullDec.format(variables.Asyi);
        String Asw = nullDec.format(variables.Asw);

        input = "<!--- INPUT ---><h2>Inputverdier:</h2><table width='700' border='0' cellpadding='0' cellspacing='0'>  <tr>    <td align='left' valign='top'><h3>Betong</h3>      <p>f<sub>ck</sub> = " + fck + " MPa<br />        f<sub>ctm</sub> = " + fctm + " MPa<br />        E<sub>cm</sub> = " + ecm + " MPa<br />        γ<sub>C</sub> = " + gc + "<br />        α<sub>cc</sub> = " + acc + "<br />        ε<sub>cu2</sub> = " + ecu2 + "<br />        ε<sub>c2</sub> = " + ec2 + "<br />        θ<sub>n</sub> = " + fi + " °</p></td>    <td align='left' valign='top'><h3>Armeringsstål</h3>      <p>f<sub>yk</sub>= " + fyk + " MPa<br />        E<sub>sk</sub>= " + esk + " MPa<br />        γ<sub>S</sub>= " + gs + "<br />        Armeringsmodell " + model + "<br />        Armeringsklasse " + reClass + "</p></td>    <td align='left' valign='top'><h3>Geometri</h3>      <p>h = " + h + " mm<br />        c<sub>s</sub> = " + cs + " mm<br />        c<sub>i</sub> = " + ci + "mm<br />        t<sub>s</sub> = " + ts + " mm<br />        t<sub>i</sub> = " + ti + " mm</p></td>  </tr>  <tr>    <td align='left' valign='top'><h3>Krefter</h3>      <p>n<sub>Edx</sub> = " + nedx + " kN/m<br />        n<sub>Edy</sub> = " + nedy + " kN/m<br />        n<sub>Edxy</sub> = " + nedxy + "kN/m<br />        v<sub>Edx</sub> = " + vedx + " kN/m<br />        v<sub>Edy</sub> = " + vedy + " kN/m<br />        m<sub>Edx</sub> = " + medx + " kNm/m<br />        m<sub>Edy</sub> = " + medy + " kNm/m<br />        m<sub>Edxy</sub> = " + medxy + " kNm/m</p></td>    <td align='left' valign='top'><h3>Skjærparametere</h3>      <p>cot(θ) = " + cot + "<br />        α =" + a + "°<br />        fywk = " + fywk + " MPa<br />        k1 = " + k1C + " / " + k1T + "<br />        k2 = " + k2 + "</p></td>    <td align='left' valign='top'><h3>Armeringsmegder</h3>      <p>A<sub>sx,s</sub> = " + Asxs + " mm<sup>2</sup>/m<br />        A<sub>sy,s</sub> = " + Asys + " mm<sup>2</sup>/m<br />        A<sub>sx,i</sub> = " + Asxi + " mm<sup>2</sup>/m<br />        A<sub>sy,i</sub> = " + Asyi + " mm<sup>2</sup>/m<br />        A<sub>sw</sub> = " + Asw + "  mm<sup>2</sup>/m<sup>2</sup></p>      <p>&nbsp;</p></td>  </tr></table>";
        inputEnglish = "<!--- INPUT ---><h2>Input values:</h2><table width='700' border='0' cellpadding='0' cellspacing='0'>  <tr>    <td align='left' valign='top'><h3>Concrete</h3>      <p>f<sub>ck</sub> = " + fck + " MPa<br />        f<sub>ctm</sub> = " + fctm + " MPa<br />        E<sub>cm</sub> = " + ecm + " MPa<br />        γ<sub>C</sub> = " + gc + "<br />        α<sub>cc</sub> = " + acc + "<br />        ε<sub>cu2</sub> = " + ecu2 + "<br />        ε<sub>c2</sub> = " + ec2 + "<br />        θ<sub>n</sub> = " + fi + " °</p></td>    <td align='left' valign='top'><h3>Reinforcement steel</h3>      <p>f<sub>yk</sub>= " + fyk + " MPa<br />        E<sub>sk</sub>= " + esk + " MPa<br />        γ<sub>S</sub>= " + gs + "<br />        Reinforcement model " + model + "<br />        Reinforcement class " + reClass + "</p></td>    <td align='left' valign='top'><h3>Geometry</h3>      <p>h = " + h + " mm<br />        c<sub>s</sub> = " + cs + " mm<br />        c<sub>i</sub> = " + ci + "mm<br />        t<sub>s</sub> = " + ts + " mm<br />        t<sub>i</sub> = " + ti + " mm</p></td>  </tr>  <tr>    <td align='left' valign='top'><h3>Forces</h3>      <p>n<sub>Edx</sub> = " + nedx + " kN/m<br />        n<sub>Edy</sub> = " + nedy + " kN/m<br />        n<sub>Edxy</sub> = " + nedxy + "kN/m<br />        v<sub>Edx</sub> = " + vedx + " kN/m<br />        v<sub>Edy</sub> = " + vedy + " kN/m<br />        m<sub>Edx</sub> = " + medx + " kNm/m<br />        m<sub>Edy</sub> = " + medy + " kNm/m<br />        m<sub>Edxy</sub> = " + medxy + " kNm/m</p></td>    <td align='left' valign='top'><h3>Shear parameters</h3>      <p>cot(θ) = " + cot + "<br />        α =" + a + "°<br />        fywk = " + fywk + " MPa<br />        k1 = " + k1C + " / " + k1T + "<br />        k2 = " + k2 + "</p></td>    <td align='left' valign='top'><h3>Reinforcement amounts</h3>      <p>A<sub>sx,s</sub> = " + Asxs + " mm<sup>2</sup>/m<br />        A<sub>sy,s</sub> = " + Asys + " mm<sup>2</sup>/m<br />        A<sub>sx,i</sub> = " + Asxi + " mm<sup>2</sup>/m<br />        A<sub>sy,i</sub> = " + Asyi + " mm<sup>2</sup>/m<br />        A<sub>sw</sub> = " + Asw + "  mm<sup>2</sup>/m<sup>2</sup></p>      <p>&nbsp;</p></td>  </tr></table>";


    } //end method input()

    public static void updateSandwich(boolean findReinforcement, double[] t, double[] sigmaEd, double[] sigmaRd, double[] Asx, double[] Asy, double Asmin, double fi0, double vEdc, double vRdc, double vRds, double rhoz) {
        sandwich = "";
        sandwichEnglish = "";
        String calcMethod;
        String calcMethodEnglish;

        //beregningsvalg hentes fra gui
        String selectedOptions="";
        String sep = "<br />";
        if (variables.inclShear) selectedOptions = selectedOptions+" - Skjærberegninger inkludert."+sep;
        if (variables.autoIncrement) selectedOptions = selectedOptions+" - Autmatisk øking av lagtykkelser."+sep;
        if (variables.doubleMinimum) selectedOptions = selectedOptions+" - Dobbel minimumsarmering."+sep;
        if (variables.overrideMinReinf) selectedOptions = selectedOptions+" - Armeringsmengder ikke overstyrt av minimumsarmering."+sep;

        //Nøyaktig det samme bare på engelsk
        String selectedOptionsEnglish="";
        if (variables.inclShear) selectedOptionsEnglish = selectedOptionsEnglish+" - Transverse shear included."+sep;
        if (variables.autoIncrement) selectedOptionsEnglish = selectedOptionsEnglish+" - Auto-increased layer thicknesses."+sep;
        if (variables.doubleMinimum) selectedOptionsEnglish = selectedOptionsEnglish+" - Doubled minimum reinforcement."+sep;
        if (variables.overrideMinReinf) selectedOptionsEnglish = selectedOptionsEnglish+" - No override from minimum reinforcement."+sep;


        String ti = nullDec.format(t[0]*1000);
        String ts = nullDec.format(t[1]*1000);
        String sEdi = oneDec.format(Math.abs(sigmaEd[0]) / 1000);
        String sEds = oneDec.format(Math.abs(sigmaEd[1]) / 1000);
        String sRdi = oneDec.format(Math.abs(sigmaRd[0]) / 1000);
        String sRds = oneDec.format(Math.abs(sigmaRd[1]) / 1000);
        String Asxi = nullDec.format(Asx[0] * 1.0e6);
        String Asxs = nullDec.format(Asx[1] * 1.0e6);
        String Asyi = nullDec.format(Asy[0] * 1.0e6);
        String Asys = nullDec.format(Asy[1] * 1.0e6);
        String min = nullDec.format(Asmin * 1.0e6);

        fi0 = fi0*180/Math.PI;

        String fi = oneDec.format(fi0);
        String ved = oneDec.format(vEdc);
        String vrdc = oneDec.format(vRdc);
        String vrds = "***";
        String Asw = "***";
        if (vRds != 0) vrds = oneDec.format(vRds);
        if (rhoz != 0) Asw = nullDec.format(rhoz);

        if (findReinforcement) {
            calcMethod = "Finn ny armering og beregn nødvendige lagtykkelser";
            calcMethodEnglish = "Find new reinforcement and calculate necessary layer thicknesses.";
        }
        else {
            calcMethod = "Behold armering og beregn nødvendige lagtykkelser.";
            calcMethodEnglish = "Keep reinforcement and calculate necessary layer thicknesses.";
        }
        
        sandwich = "<!--- SANDWICHMETODEN ---><h2>Beregningsfase 1: Sandwichmetoden</h2><p>Beregningsmetode: "+calcMethod+"<br />  Beregningsvalg:<br /> "+selectedOptions+"</p><h3>Resultater:</h3>";
        sandwichEnglish = "<!--- SANDWICHMETODEN ---><h2>Phase 1: The Sandwich method</h2><p>Calculation method: "+calcMethodEnglish+"<br />Calculation choices:<br /> "+selectedOptionsEnglish+"</p><h3>Results:</h3>";

        if (findReinforcement) {
            sandwich = sandwich+"<table width='700' border='1px' cellspacing='0' cellpadding='5'>  <tr>    <th scope='col'>Lag</th>    <th scope='col'>Tykkelse</th>    <th scope='col'>Betongspenning <br />      i hovedretning</th>    <th scope='col'>Betongens trykkapasitet</th>    <th scope='col'>Armeringsmengde <br />      i x-retning</th>    <th scope='col'>Armeringsmengde<br />        i y-retning</th>  </tr>  <tr>    <td bgcolor='#CCCCCC' scope='row'>Topp</td>    <td align='center' bgcolor='#CCCCCC'>"+ts+" mm</td>    <td align='center' bgcolor='#CCCCCC'>"+sEds+" MPa</td>    <td align='center' bgcolor='#CCCCCC'>"+sRds+" MPa</td>    <td align='center' bgcolor='#CCCCCC'>"+Asxs+" mm<sup>2</sup>/m</td>    <td align='center' bgcolor='#CCCCCC'>"+Asys+" mm<sup>2</sup>/m</td>  </tr>  <tr>    <td scope='row'>Bunn</td>    <td align='center'>"+ti+" mm</td>    <td align='center'>"+sEdi+" MPa</td>    <td align='center'>"+sRdi+" MPa</td>    <td align='center'>"+Asxi+" mm<sup>2</sup>/m</td>    <td align='center'>"+Asyi+" mm<sup>2</sup>/m</td>  </tr></table><p>Minimumsarmering: "+min+" mm<sup>2</sup>/m</p>";
            sandwichEnglish = sandwichEnglish+"<table width='700' border='1px' cellspacing='0' cellpadding='5'>  <tr>    <th scope='col'>Layer</th>    <th scope='col'>Thickness</th>    <th scope='col'>Principal concrete stress</th>    <th scope='col'>Concrete compression capacity</th>    <th scope='col'>Reinforcement amount x-dircetion</th>    <th scope='col'>Reinforcement amount y-direction</th>  </tr>  <tr>    <td bgcolor='#CCCCCC' scope='row'>Top</td>    <td align='center' bgcolor='#CCCCCC'>"+ts+" mm</td>    <td align='center' bgcolor='#CCCCCC'>"+sEds+" MPa</td>    <td align='center' bgcolor='#CCCCCC'>"+sRds+" MPa</td>    <td align='center' bgcolor='#CCCCCC'>"+Asxs+" mm<sup>2</sup>/m</td>    <td align='center' bgcolor='#CCCCCC'>"+Asys+" mm<sup>2</sup>/m</td>  </tr>  <tr>    <td scope='row'>Bottom</td>    <td align='center'>"+ti+" mm</td>    <td align='center'>"+sEdi+" MPa</td>    <td align='center'>"+sRdi+" MPa</td>    <td align='center'>"+Asxi+" mm<sup>2</sup>/m</td>    <td align='center'>"+Asyi+" mm<sup>2</sup>/m</td>  </tr></table><p>Minimum reinforcement: "+min+" mm<sup>2</sup>/m</p>";
        }
        else {
            sandwich = sandwich + "<table width='500' border='1px' cellspacing='0' cellpadding='5'>  <tr>    <th scope='col'>Lag</th>    <th scope='col'>Tykkelse</th>    <th scope='col'>Betongspenning <br />      i hovedretning</th>    <th scope='col'>Betongens trykkapasitet</th>  </tr>  <tr>    <td bgcolor='#CCCCCC' scope='row'>Topp</td>    <td align='center' bgcolor='#CCCCCC'>"+ts+" mm</td>    <td align='center' bgcolor='#CCCCCC'>"+sEds+" MPa</td>    <td align='center' bgcolor='#CCCCCC'>"+sRds+" MPa</td>  </tr>  <tr>    <td scope='row'>Bunn</td>    <td align='center'>"+ti+" mm</td>    <td align='center'>"+sEdi+" MPa</td>    <td align='center'>"+sRdi+" MPa</td>  </tr></table>";
            sandwichEnglish = sandwichEnglish + "<table width='500' border='1px' cellspacing='0' cellpadding='5'>  <tr>    <th scope='col'>Lag</th>    <th scope='col'>Tykkelse</th>    <th scope='col'>Betongspenning <br />      i hovedretning</th>    <th scope='col'>Betongens trykkapasitet</th>  </tr>  <tr>    <td bgcolor='#CCCCCC' scope='row'>Top</td>    <td align='center' bgcolor='#CCCCCC'>"+ts+" mm</td>    <td align='center' bgcolor='#CCCCCC'>"+sEds+" MPa</td>    <td align='center' bgcolor='#CCCCCC'>"+sRds+" MPa</td>  </tr>  <tr>    <td scope='row'>Bunn</td>    <td align='center'>"+ti+" mm</td>    <td align='center'>"+sEdi+" MPa</td>    <td align='center'>"+sRdi+" MPa</td>  </tr></table>";
        }

        if(variables.inclShear) {
            sandwich = sandwich + "<h3>Skjærkontroll i hovedskjærretning</h3><table border=0 cellspacing=0 cellpadding=5>  <tr>    <td scope='col'>Retning:</td>    <td scope='col'>"+fi+" °</td>  </tr>  <tr>    <td>Skjærkraft:</td>    <td>"+ved+" kN/m</td>  </tr>  <tr>    <td>Skjærkapasitet u/armering:</td>    <td>"+vrdc+" kN/m</td>  </tr>  <tr>    <td>Skjærkapasitet m/armeirng:</td>    <td>"+vrds+" kN/m</td>  </tr>  <tr>    <td>Skjærarmeringmengde:</td>    <td>"+Asw+" mm/m<sup>2</sup></td>  </tr></table>";
            sandwichEnglish = sandwichEnglish + "<h3>Shear calculation in principal shear direction:</h3><table border=0 cellspacing=0 cellpadding=5>  <tr>    <td scope='col'>Direction:</td>    <td scope='col'>"+fi+" °</td>  </tr>  <tr>    <td>Shear force:</td>    <td>"+ved+" kN/m</td>  </tr>  <tr>    <td>Shear capacity whitout shear reinforcement:</td>    <td>"+vrdc+" kN/m</td>  </tr>  <tr>    <td>Shear capacity with shear reinforcement:</td>    <td>"+vrds+" kN/m</td>  </tr>  <tr>    <td>Shear reinforcement amount:</td>    <td>"+Asw+" mm/m<sup>2</sup></td>  </tr></table>";
        }
        
    } //End method updateSandwich()


    public static void updateIteration(String[][] tab, int numIter) {
        //henter noen parametere fra variables klassen
        String maxiter = nullDec.format(variables.maxiter);
        String beta = Double.toString(variables.beta);
        String numLam = nullDec.format(variables.numLamella);
        iteration = "<h2>Beregningsfase 2: Utnyttingsgrader</h2><p>Beregningsmetode: Iterasjonsmetoden.<br />  Beregningsvalg: Iterasjonstak: "+maxiter+", konvergenskriterium "+beta+", antall lameller: "+numLam+"<br /></p><h3>Resultater:</h3><p>Beregning fullført etter "+numIter+" iterasjoner.</p><table width='700' border='1px' cellspacing='0' cellpadding='5'>  <tr>    <th scope='col'>Lag</th>    <th scope='col'>Retning</th>    <th scope='col'>Armeringsmengde</th>    <th scope='col'>Spenning</th>    <th scope='col'>Utnyttingsgrad spenning</th>    <th scope='col'>Tøyning</th>    <th scope='col'>Utnyttingsgrad tøyning</th>  </tr>  <tr>    <td bgcolor='#CCCCCC'>Armering i topp</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>X</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[0][1]+" mm<sup>2</sup>/m</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[0][2]+" MPa</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[0][3]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[0][4]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[0][5]+"</td>  </tr>  <tr>    <td>Armering i topp</td>    <td align='center' valign='middle'>Y</td>    <td align='center' valign='middle'>"+tab[1][1]+" mm<sup>2</sup>/m</td>    <td align='center' valign='middle'>"+tab[1][2]+" MPa</td>    <td align='center' valign='middle'>"+tab[1][3]+"</td>    <td align='center' valign='middle'>"+tab[1][4]+"</td>    <td align='center' valign='middle'>"+tab[1][5]+"</td>  </tr>  <tr>    <td bgcolor='#CCCCCC'>Armering i bunn</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>X</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[2][1]+" mm<sup>2</sup>/m</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[2][2]+" MPa</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[2][3]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[2][4]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[2][5]+"</td>  </tr>  <tr>    <td>Armering i bunn</td>    <td align='center' valign='middle'>Y</td>    <td align='center' valign='middle'>"+tab[3][1]+" mm<sup>2</sup>/m</td>    <td align='center' valign='middle'>"+tab[3][2]+" MPa</td>    <td align='center' valign='middle'>"+tab[3][3]+"</td>    <td align='center' valign='middle'>"+tab[3][4]+"</td>    <td align='center' valign='middle'>"+tab[3][5]+"</td>  </tr>  <tr>    <td bgcolor='#CCCCCC'>Betong</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>hoved</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[4][1]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[4][2]+" MPa</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[4][3]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[4][4]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[4][5]+"</td>  </tr></table>";
        iterationEnglish = "<h2>Phase 2: Utilization Ratios</h2><p>Calculation method: The iteration method.<br />    Calculation choices: Max iterations: "+maxiter+", Convergence criterion "+beta+", number of lamellas: "+numLam+"</p><h3>Results:</h3><p>Calculation finished after "+numIter+" iterations.</p><table width='700' border='1px' cellspacing='0' cellpadding='5'>  <tr>    <th scope='col'>Layer</th>    <th scope='col'>Direction</th>    <th scope='col'>Reinforcement amount</th>    <th scope='col'>Stress</th>    <th scope='col'>Utilzation stress</th>    <th scope='col'>Strain</th>    <th scope='col'>Utilization strain</th>  </tr>  <tr>    <td rowspan='2'>Reinforcement in top</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>X</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[0][1]+" mm<sup>2</sup>/m</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[0][2]+" MPa</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[0][3]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[0][4]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[0][5]+"</td>  </tr>  <tr>    <td align='center' valign='middle'>Y</td>    <td align='center' valign='middle'>"+tab[1][1]+" mm<sup>2</sup>/m</td>    <td align='center' valign='middle'>"+tab[1][2]+" MPa</td>    <td align='center' valign='middle'>"+tab[1][3]+"</td>    <td align='center' valign='middle'>"+tab[1][4]+"</td>    <td align='center' valign='middle'>"+tab[1][5]+"</td>  </tr>  <tr>    <td rowspan='2'>Reinforcment in bottom</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>X</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[2][1]+" mm<sup>2</sup>/m</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[2][2]+" MPa</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[2][3]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[2][4]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[2][5]+"</td>  </tr>  <tr>    <td align='center' valign='middle'>Y</td>    <td align='center' valign='middle'>"+tab[3][1]+" mm<sup>2</sup>/m</td>    <td align='center' valign='middle'>"+tab[3][2]+" MPa</td>    <td align='center' valign='middle'>"+tab[3][3]+"</td>    <td align='center' valign='middle'>"+tab[3][4]+"</td>    <td align='center' valign='middle'>"+tab[3][5]+"</td>  </tr>  <tr>    <td>Concrete</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>principal</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[4][1]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[4][2]+" MPa</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[4][3]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[4][4]+"</td>    <td align='center' valign='middle' bgcolor='#CCCCCC'>"+tab[4][5]+"</td>  </tr></table>";
    } //end method updateIteration()

    public static void updateShear(double[] ur) {
        String ang = oneDec.format(ur[0]);
        String ved = oneDec.format(ur[1]);
        String vrd = oneDec.format(ur[2]);
        String ratio = percent.format(ur[3]);

        shear = "<h2>Ekstra skjærkontroll:</h2><p>Uavhengig skjærkontroll som tar hensyn til aksialkrefter i betongskallet.</p><table width='200' border='0' cellspacing='0' cellpadding='0'>  <tr>    <td scope='col'>Vinkel:</td>    <td scope='col'>"+ang+" °</td>  </tr>  <tr>    <td>Skjærkraft:</td>    <td>"+ved+" kN/m</td>  </tr>  <tr>    <td>Skjærkapasitet:</td>    <td>"+vrd+" kN/m</td>  </tr>  <tr>    <td>Utnyttingsgrad:</td>    <td>"+ratio+"</td>  </tr></table>";
        shearEnglish = "<h2>Extra shear check:</h2><p>Independent shear check which includes axial forces in the concrete shell.</p><table width='200' border='0' cellspacing='0' cellpadding='0'>  <tr>    <td scope='col'>Angle:</td>    <td scope='col'>"+ang+" °</td>  </tr>  <tr>    <td>Shear force:</td>    <td>"+ved+" kN/m</td>  </tr>  <tr>    <td>Shear capacity:</td>    <td>"+vrd+" kN/m</td>  </tr>  <tr>    <td>Utilization ratio::</td>    <td>"+ratio+"</td>  </tr></table>";
    } //end method updateShear

    public static void writeResultArea(boolean writeHeader, boolean writeInput, boolean writeSandwich, boolean writeIteration, boolean writeShear, boolean writeFooter) {
 

        if(gui.jRadioButtonEnglish.isSelected()) {
            String report="";
            if (writeHeader) report = report+headerEnglish;
            if (writeInput) report = report+inputEnglish;
            if (writeSandwich) report = report+sandwichEnglish;
            if (writeIteration) report = report+iterationEnglish;
            if (writeShear) report = report+shearEnglish;
            if (writeFooter) report = report+footer;
            gui.textarea.setText(report);            
        } else {
            String report="";
            if (writeHeader) report = report+header;
            if (writeInput) report = report+input;
            if (writeSandwich) report = report+sandwich;
            if (writeIteration) report = report+iteration;
            if (writeShear) report = report+shear;
            if (writeFooter) report = report+footer;
            gui.textarea.setText(report);
        }
    } //end method writeResultArea()

    public static void writeResultArea(boolean writeAll) {
        if (writeAll) writeResultArea(true, true, true, true, true, true);
    }


    /*
     * saveReport
     * Metode som lagrer rapporten som en HTML-fil
     * @author Einar Raknes Brekke
     */
    public static void saveReport(String fileName) throws IOException {
        String htmlData = gui.textarea.getText();

        if (!fileName.endsWith(".htm")) {
            fileName += ".htm";
        }
        GUI.gui.statusBar.setText("Saving file..");

        FileWriter streamToFile;
        PrintWriter writeToFile;

        streamToFile = new FileWriter(fileName);
        writeToFile = new PrintWriter(streamToFile);

        writeToFile.println("<!DOCTYPE html PUBLIC '-//W3C//DTD XHTML 1.0 Transitional//EN' 'http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd'>");
        writeToFile.print(htmlData);
        writeToFile.close();

        IO.MemoryFile.writeLastSaved(fileName);

        GUI.gui.statusBar.setText("");
    } //end method saveReport()
} //END CLASS


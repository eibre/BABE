
package IO;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Klasse som behandler åpning og lagring av filer samt tolking av inputfil
 * @author Aleksander Sørvik Hanssen
 */
public class FileManager extends GUI.variables {

    protected static String[] varNames = {
        "beta", "numLamella", "maxiter", "inclShear", "autoIncrement", "doubleMinimum", "findReinf", "overrideMinReinf",
        "nedx", "nedy", "nedxy", "medx", "medy", "medxy", "vedx", "vedy", "h", "ci", "cs", "ti", "ts", "Asxi", "Asxs", "Asyi", "Asys",
        "fck", "Ec", "fctm", "alphacc", "gammac", "nyc", "fi",
        "fywk", "alpha", "cotTheta",
        "gammas", "fyk", "Es", "epsud", "reinfModel", "reinfClass"};
    //Legg til findReinf,

    public static String findVariable(String variable, String fileName) throws IOException {
        //Variabeldeklarasjon
        int tegnKode = 0;

        String word, line, value;
        FileReader streamFromFile;
        BufferedReader readFromFile;
        boolean isLetter, isNumber;

        //Initiering
        word = "";
        value = "";

        // Leser inn linje for linje av fila
        streamFromFile = new FileReader(fileName);
        readFromFile = new BufferedReader(streamFromFile);
        line = readFromFile.readLine();

        while (line != null) {

            //Går gjennom hver bokstav i linja og danner ord som kontrolleres mot søkeordet
            //Hvis søkeordet finnes på linja lagres den tilhørende verdien som en String (inkl. evt. fortegn og komma)
            for (int i = 0; i < line.length(); i++) {
                tegnKode = (int) line.charAt(i);
                isLetter = (tegnKode > 64 && tegnKode < 91) || (tegnKode > 96 && tegnKode < 123);
                isNumber = (tegnKode > 47 && tegnKode < 58) || (tegnKode > 43 && tegnKode < 47);

                if (tegnKode == 35) {// "#"
                    break;
                }//end if
                if (isLetter && value.equals("")) {// Kun engelske foreløpig
                    word += line.charAt(i);
                }//end if
                if (tegnKode == 32) {// " "
                    if (!variable.equals(word)) {
                        word = "";
                        break; //Hopper ut av for-løkka
                    }//end if
                }//end if
                if (isNumber) {
                    value += line.charAt(i);
                }//end if
                if (i == line.length() - 1) {
                    word = "";
                    break; //Hopper ut av for-løkka
                }//end if
            }//end for

            line = readFromFile.readLine();
        }//end while
        streamFromFile.close();
        return value;



    }//end method

    //Open File-method
    //Skal lese inn verdier fra inputfil og skrive dem i tekstfeltene i GUI'en
    public static void openFile(String filePath) throws IOException {

        String[] values = new String[varNames.length];

        //Henter verdier fra input-fil
        for (int i = 0; i < varNames.length; i++) {
            values[i] = findVariable(varNames[i], filePath);
            try {
                Double.parseDouble(values[i]);
            } catch (NumberFormatException e) {
                GUI.gui.statusBar.setText("Value missing ing input file: " + varNames[i]);
            }
        }

        double[] a = calculation.Materials.strengthConcrete(Double.parseDouble(values[24]), Double.parseDouble(values[27]), Double.parseDouble(values[28]));
        //Setter inn verdiene i rett felt
        jTextFieldBeta.setText(values[0]);
        jTextFieldN.setText(values[1]);
        jTextFieldMaxiter.setText(values[2]);
        if (values[3].equals("1")) {
            jCheckBoxShear.setSelected(true);
        } else {
            jCheckBoxShear.setSelected(false);
        }
        if (values[4].equals("1")) {
            jCheckBoxAutoInc.setSelected(true);
        } else {
            jCheckBoxAutoInc.setSelected(false);
        }
        if (values[5].equals("1")) {
            jCheckBoxDoubleMinimum.setSelected(true);
        } else {
            jCheckBoxDoubleMinimum.setSelected(false);
        }
        if (values[6].equals("1")) {
            findReinforcement.setSelected(true);
        } else {
            checkConcrete.setSelected(true);
        }
        if (values[7].equals("1")) {
            jCheckBoxOverride.setSelected(true);
        } else {
            jCheckBoxOverride.setSelected(false);
        }
        jTextFieldNx.setText(values[8]);
        jTextFieldNy.setText(values[9]);
        jTextFieldNxy.setText(values[10]);
        jTextFieldMx.setText(values[11]);
        jTextFieldMy.setText(values[12]);
        jTextFieldMxy.setText(values[13]);
        jTextFieldVx.setText(values[14]);
        jTextFieldVy.setText(values[15]);
        jTextFieldH.setText(values[16]);
        jTextFieldCi.setText(values[17]);
        jTextFieldCs.setText(values[18]);
        jTextFieldTi.setText(values[19]);
        jTextFieldTs.setText(values[20]);
        jTextFieldAsxi.setText(values[21]);
        jTextFieldAsxs.setText(values[22]);
        jTextFieldAsyi.setText(values[23]);
        jTextFieldAsys.setText(values[24]);

        switch (Integer.parseInt(values[25])) {
            case (12):
                GUI.gui.jComboBox1.setSelectedIndex(0);
                break;
            case (16):
                GUI.gui.jComboBox1.setSelectedIndex(1);
                break;
            case (20):
                GUI.gui.jComboBox1.setSelectedIndex(2);
                break;
            case (25):
                GUI.gui.jComboBox1.setSelectedIndex(3);
                break;
            case (30):
                GUI.gui.jComboBox1.setSelectedIndex(4);
                break;
            case (35):
                GUI.gui.jComboBox1.setSelectedIndex(5);
                break;
            case (40):
                GUI.gui.jComboBox1.setSelectedIndex(6);
                break;
            case (45):
                GUI.gui.jComboBox1.setSelectedIndex(7);
                break;
            case (50):
                GUI.gui.jComboBox1.setSelectedIndex(8);
                break;
            case (55):
                GUI.gui.jComboBox1.setSelectedIndex(9);
                break;
            case (60):
                GUI.gui.jComboBox1.setSelectedIndex(10);
                break;
            case (70):
                GUI.gui.jComboBox1.setSelectedIndex(11);
                break;
            case (80):
                GUI.gui.jComboBox1.setSelectedIndex(12);
                break;
            case (90):
                GUI.gui.jComboBox1.setSelectedIndex(13);
                break;
            default:
                GUI.gui.jCheckBox1.setSelected(true);
                jTextFieldFck.setEnabled(true);
                jTextFieldEcm.setEnabled(true);
                jTextFieldFctm.setEnabled(true);
                jTextFieldFck.setText(values[25]);
                break;
        }
        if (values[26].equals("")) {
            Double.toString(a[0]);
        } else {
            jTextFieldEcm.setText(values[26]);
        }
        if (values[27].equals("")) {
            Double.toString(a[4]);
        } else {
            jTextFieldFctm.setText(values[27]);
        }
        jTextFieldAcc.setText(values[28]);
        jTextFieldGc.setText(values[29]);
        jTextFieldNyc.setText(values[30]);
        jTextFieldPhi.setText(values[31]);
        jTextFieldFywk.setText(values[32]);
        jTextFieldAlpha.setText(values[33]);
        jTextFieldCotT.setText(values[34]);
        jTextFieldGs.setText(values[35]);
        jTextFieldFyk.setText(values[36]);
        jTextFieldEsk.setText(values[37]);
        jTextFieldEpsUd.setText(values[38]);
        if (values[39].equals("0") || values[39].equals("1")) {
            jComboBoxModel.setSelectedIndex(Integer.parseInt(values[39]));
        } else {
            jComboBoxModel.setSelectedIndex(1);
//            System.out.println("Reinforcement model not specified in tab 'Material Parameters -> Advanced options'\nModel B is default");
        }
        if (values[40].equals("0") || values[40].equals("1") || values[40].equals("2")) {
            jComboBoxClass.setSelectedIndex(Integer.parseInt(values[40]));
        } else {
            jComboBoxClass.setSelectedIndex(2);
//            System.out.println("Reinforcement Class not specified in tab 'Material Parameters'\nB500NC is default");
        }
        filePath = "";
    }

    //Method for saving input
    public static void save(String fileName) throws IOException {

        if (!fileName.endsWith(".txt")) {
            fileName += ".txt";
        }
        GUI.gui.statusBar.setText("Saving file");

        Date todaysDate = new java.util.Date();
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE, dd-MMM-yyyy HH:mm:ss\n");

        FileWriter streamToFile;
        PrintWriter writeToFile;

        streamToFile = new FileWriter(fileName);
        writeToFile = new PrintWriter(streamToFile);

        writeToFile.println("=== " + fileName + " ===\n");
        writeToFile.println("Saved: " + formatter.format(todaysDate));
        writeToFile.println(" \n \n# Run time parameters");
        writeToFile.println(varNames[0] + " " + jTextFieldBeta.getText());
        writeToFile.println(varNames[1] + " " + jTextFieldN.getText());
        writeToFile.println(varNames[2] + " " + jTextFieldMaxiter.getText());
        if (jCheckBoxShear.isSelected()) {
            writeToFile.println(varNames[3] + " 1");
        } else {
            writeToFile.println(varNames[3] + " 0");
        }
//        if (jCheckBoxInclCalc.isSelected()) {
//            writeToFile.println(varNames[4] + " 1");
//        } else {
//            writeToFile.println(varNames[4] + " 0");
//        }
        if (jCheckBoxAutoInc.isSelected()) {
            writeToFile.println(varNames[4] + " 1");
        } else {
            writeToFile.println(varNames[4] + " 0");
        }
        if (jCheckBoxDoubleMinimum.isSelected()) {
            writeToFile.println(varNames[5] + " 1");
        } else {
            writeToFile.println(varNames[5] + " 0");
        }
        if (findReinforcement.isSelected()) {
            writeToFile.println(varNames[6] + " 1");
        } else {
            writeToFile.println(varNames[6] + " 0");
        }
        if (jCheckBoxOverride.isSelected()) {
            writeToFile.println(varNames[7] + " 1");
        } else {
            writeToFile.println(varNames[7] + " 0");
        }
        writeToFile.println();
        writeToFile.println("# Load Vector");
        writeToFile.println(varNames[8] + " " + jTextFieldNx.getText());
        writeToFile.println(varNames[9] + " " + jTextFieldNy.getText());
        writeToFile.println(varNames[10] + " " + jTextFieldNxy.getText());
        writeToFile.println(varNames[11] + " " + jTextFieldMx.getText());
        writeToFile.println(varNames[12] + " " + jTextFieldMy.getText());
        writeToFile.println(varNames[13] + " " + jTextFieldMxy.getText());
        writeToFile.println(varNames[14] + " " + jTextFieldVx.getText());
        writeToFile.println(varNames[15] + " " + jTextFieldVy.getText());
        writeToFile.println();
        writeToFile.println("# Geometry");
        writeToFile.println(varNames[16] + " " + jTextFieldH.getText());
        writeToFile.println(varNames[17] + " " + jTextFieldCi.getText());
        writeToFile.println(varNames[18] + " " + jTextFieldCs.getText());
        writeToFile.println(varNames[19] + " " + jTextFieldTi.getText());
        writeToFile.println(varNames[20] + " " + jTextFieldTs.getText());
        writeToFile.println();
        writeToFile.println("# Reinforcement");
        writeToFile.println(varNames[21] + " " + jTextFieldAsxi.getText());
        writeToFile.println(varNames[22] + " " + jTextFieldAsxs.getText());
        writeToFile.println(varNames[23] + " " + jTextFieldAsyi.getText());
        writeToFile.println(varNames[24] + " " + jTextFieldAsys.getText());
        writeToFile.println();
        writeToFile.println("# Material parameters - Concrete");
        writeToFile.println(varNames[25] + " " + jTextFieldFck.getText());
        writeToFile.println(varNames[26] + " " + jTextFieldEcm.getText());
        writeToFile.println(varNames[27] + " " + jTextFieldFctm.getText());
        writeToFile.println(varNames[28] + " " + jTextFieldAcc.getText());
        writeToFile.println(varNames[29] + " " + jTextFieldGc.getText());
        writeToFile.println(varNames[30] + " " + jTextFieldNyc.getText());
        writeToFile.println();
        writeToFile.println("# Shear parameters");
        writeToFile.println(varNames[31] + " " + jTextFieldPhi.getText());
        writeToFile.println(varNames[32] + " " + jTextFieldFywk.getText());
        writeToFile.println(varNames[33] + " " + jTextFieldAlpha.getText());
        writeToFile.println(varNames[34] + " " + jTextFieldCotT.getText());
        writeToFile.println();
        writeToFile.println("# Material parameters - Reinforcement");
        writeToFile.println(varNames[35] + " " + jTextFieldGs.getText());
        writeToFile.println(varNames[36] + " " + jTextFieldFyk.getText());
        writeToFile.println(varNames[37] + " " + jTextFieldEsk.getText());
        writeToFile.println(varNames[38] + " " + jTextFieldEpsUd.getText());
        writeToFile.println(varNames[39] + " " + jComboBoxModel.getSelectedIndex());
        writeToFile.println(varNames[40] + " " + jComboBoxClass.getSelectedIndex());
        writeToFile.println();
        writeToFile.println("No results are saved in this file. Go to 'Report-tab' and select 'Create Report'");

        writeToFile.close();

        //System.out.println(fileName);
        MemoryFile.writeLastSaved(fileName);

        GUI.gui.statusBar.setText("");


    }
}

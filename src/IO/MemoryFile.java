/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package IO;

import GUI.gui;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Einar
 */
public class MemoryFile {
    public static String getLastSavePath() {
        String fileName = "";

        //lese path fra fil
        FileReader streamFromFile;
        BufferedReader readFromFile;
        try {
            streamFromFile = new FileReader("var\\lastSaved.dat");
            readFromFile = new BufferedReader(streamFromFile);
            fileName = readFromFile.readLine();
            readFromFile.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(gui.class.getName()).log(Level.SEVERE, null, ex);
        }
        return fileName;
    }

    public static File getLastSaveFile() {
        String fileName = getLastSavePath();
        File lastSavedFile = new File(fileName);
        return lastSavedFile;
    }

    public static void writeLastSaved(String fileName) throws IOException {
        FileWriter streamToFileLastSaved;
        PrintWriter writeLastSaved;

        File lastSaved = new File("var\\lastSaved.dat");
        streamToFileLastSaved = new FileWriter(lastSaved);
        writeLastSaved = new PrintWriter(streamToFileLastSaved);

        writeLastSaved.print(fileName);
        writeLastSaved.close();
    }
} //end class

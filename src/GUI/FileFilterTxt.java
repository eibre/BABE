/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GUI;

import java.io.File;

/**
 *
 * @Written by: Einar
 */
public class FileFilterTxt extends javax.swing.filechooser.FileFilter {
    @Override
    public boolean accept(File f) {
        // Allow only directories, or files with ".txt" extension
        return f.isDirectory() || f.getAbsolutePath().endsWith(".txt");
    }

    @Override
    public String getDescription() {
        return "Text documents (*.txt)";
    }


}

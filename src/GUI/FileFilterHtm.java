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
public class FileFilterHtm extends javax.swing.filechooser.FileFilter {
    @Override
    public boolean accept(File f) {
        // Allow only directories, or files with ".html" extension
        return f.isDirectory() || f.getAbsolutePath().endsWith(".htm") || f.getAbsolutePath().endsWith("html");
    }

    @Override
    public String getDescription() {
        return "HTML documents (*.htm)";
    }


}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package GUI;

import java.awt.Color;
import javax.swing.InputVerifier;
import javax.swing.JComponent;
import javax.swing.JTextField;

/**
 *
 * @author Einar
 */
public class MyInputVerifier extends InputVerifier {

    public boolean verify(JComponent input) {
        JTextField tf = (JTextField) input;
        String str = tf.getText();


        try {
            Double.parseDouble(str);
        } catch (NumberFormatException ex) {
            tf.setBackground(Color.PINK);
            gui.statusBar.setText("Input must be a number, use . as decimal seperator.");
            return false;
        }

        if (tf == gui.jTextFieldCotT) {
          double number = Double.parseDouble(str);
          if (number < 1 || number > 2.5) {
            tf.setBackground(Color.PINK);
            gui.statusBar.setText("Imput must be between 1 and 2.5");
            return false;
          }
        }

        tf.setBackground(Color.WHITE);
        gui.statusBar.setText("");
        return true;
        } //end verify()


} //end class


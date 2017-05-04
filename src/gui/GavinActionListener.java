package gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * Created by think on 2016/12/9.
 */
public class GavinActionListener implements ActionListener {
    public void actionPerformed(ActionEvent e) {
        TextField tf = (TextField) e.getSource();
        UI.file = new File(tf.getText());
        tf.setText("");
    }
}

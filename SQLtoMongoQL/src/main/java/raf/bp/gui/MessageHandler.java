package raf.bp.gui;

import javax.swing.*;

public class MessageHandler {

    public void displayOK(String message) {
        JOptionPane.showMessageDialog(null, message, "OK", JOptionPane.INFORMATION_MESSAGE);
    }
    public void displayError(String messsage) {
        JOptionPane.showMessageDialog(null, messsage, "Error", JOptionPane.ERROR_MESSAGE);
    }

}

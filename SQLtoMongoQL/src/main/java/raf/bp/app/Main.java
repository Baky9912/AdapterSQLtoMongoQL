package raf.bp.app;

import raf.bp.gui.MainFrame;

public class Main {
    public static void main(String[] args) {
        AppCore.getInstance();
        MainFrame.getInstance().getJTable().setModel(AppCore.getInstance().getTableModel());
    }
}
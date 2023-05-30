package raf.bp.app;

import raf.bp.gui.MainFrame;

public class Main {
    public static void main(String[] args) {
        AppCore.getInstace();
        MainFrame.getInstance();

        AppCore.getInstace().getDatabase().run();
    }
}
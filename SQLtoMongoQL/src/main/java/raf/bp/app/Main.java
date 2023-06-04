package raf.bp.app;

import raf.bp.executor.MongoQLExecutor;
import raf.bp.gui.MainFrame;

public class Main {
    public static void main(String[] args) {
        AppCore.getInstace();
        MainFrame.getInstance().getJTable().setModel(AppCore.getInstace().getTableModel());
        MongoQLExecutor.main(new String[] {});
    }
}
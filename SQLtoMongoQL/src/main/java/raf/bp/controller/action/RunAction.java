package raf.bp.controller.action;

import raf.bp.app.AppCore;
import raf.bp.gui.MainFrame;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.table.TableRow;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class RunAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {

        String selectedText = MainFrame.getInstance().getTextArea().getSelectedText();
        String allText = MainFrame.getInstance().getTextArea().getText();

        String query = (selectedText == null) ? allText : selectedText;

        AppCore.getInstance().getTableModel().clearTableModel();

        try {

            SQLQuery sqlQuery = AppCore.getInstance().getSqlParser().parse(query);
            AppCore.getInstance().getValidator().validate(sqlQuery);
            List<TableRow> rows = AppCore.getInstance().getSqlExecutor().execute(sqlQuery);

            if (rows.isEmpty()) {
                AppCore.getInstance().getMessageHandler().displayOK("No records found.");
            }

            AppCore.getInstance().getGuiPackager().pack(rows);

        } catch (RuntimeException re) {
            AppCore.getInstance().getMessageHandler().displayError(re.getMessage());
        }


    }
}

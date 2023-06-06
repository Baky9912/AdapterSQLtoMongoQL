package raf.bp.controller.action;

import raf.bp.adapter.AdapterSQLMongoQLExecutor;
import raf.bp.app.AppCore;
import raf.bp.executor.concrete.MongoQLExecutor;
import raf.bp.gui.MainFrame;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.table.TableRow;
import raf.bp.packager.concrete.TablePackager;
import raf.bp.parser.concrete.SQLParser;
import raf.bp.validator.concrete.SQLValidator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class RunAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {

        String selectedText = MainFrame.getInstance().getTextArea().getSelectedText();
        String allText = MainFrame.getInstance().getTextArea().getText();

        String query = (selectedText == null) ? allText : selectedText;

        try {

            SQLQuery sqlQuery = AppCore.getInstance().getSqlParser().parse(query);
            AppCore.getInstance().getValidator().validate(sqlQuery);
            List<TableRow> rows = AppCore.getInstance().getSqlExecutor().execute(sqlQuery);
            AppCore.getInstance().getGuiPackager().pack(rows);

        } catch (RuntimeException re) {
            AppCore.getInstance().getMessageHandler().displayError(re.getMessage());
        }


    }
}

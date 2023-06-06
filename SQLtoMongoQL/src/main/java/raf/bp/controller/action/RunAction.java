package raf.bp.controller.action;

import raf.bp.adapter.AdapterSQLMongoQLExecutor;
import raf.bp.app.AppCore;
import raf.bp.executor.concrete.MongoQLExecutor;
import raf.bp.gui.MainFrame;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.TableRow;
import raf.bp.packager.TablePackager;
import raf.bp.parser.SQLParser;
import raf.bp.validator.SQLValidator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class RunAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {

        String query = MainFrame.getInstance().textArea.getText();
        SQLParser parser = new SQLParser();
        SQLValidator validator = new SQLValidator();
        MongoQLExecutor executor = new MongoQLExecutor();
        AdapterSQLMongoQLExecutor sqlExecutor = new AdapterSQLMongoQLExecutor(executor);
        TablePackager packager = new TablePackager();

//        try {

//            query = preProcessor.process(query);
            SQLQuery sqlQuery = parser.parse(query);
            validator.validate(sqlQuery);
            AppCore.getInstance().getMessageHandler().displayOK("query je validan!");
            List<TableRow> rows = sqlExecutor.execute(sqlQuery);
            packager.pack(rows);

//        } catch (RuntimeException re) {
//            AppCore.getInstace().getMessageHandler().displayError(re.getMessage());
//        }



    }
}

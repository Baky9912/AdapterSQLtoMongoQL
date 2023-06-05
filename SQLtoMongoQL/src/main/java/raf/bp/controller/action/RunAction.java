package raf.bp.controller.action;

import raf.bp.app.AppCore;
import raf.bp.converter.ClauseConverterManager;
import raf.bp.executor.MongoQLExecutor;
import raf.bp.gui.MainFrame;
import raf.bp.model.MongoQL;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.TableRow;
import raf.bp.packager.TablePackager;
import raf.bp.parser.SQLParser;
import raf.bp.parser.SQLPreProcessor;
import raf.bp.validator.SQLValidator;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.List;

public class RunAction extends AbstractAction {
    @Override
    public void actionPerformed(ActionEvent e) {

        String query = MainFrame.getInstance().textArea.getText();
        SQLPreProcessor preProcessor = new SQLPreProcessor();
        SQLParser parser = new SQLParser();
        SQLValidator validator = new SQLValidator();
        MongoQLExecutor executor = new MongoQLExecutor();
        TablePackager packager = new TablePackager();

//        try {

            query = preProcessor.process(query);
            SQLQuery sqlQuery = parser.parseQuery(query);

            if (validator.validate(sqlQuery)) {
                System.out.println("validan query!");
            } else {
                System.out.println("query nije validan!");
            }

            AppCore.getInstace().getMessageHandler().displayOK("query je validan!");

            MongoQL mongoQL = new MongoQL(sqlQuery);
            mongoQL.makeAll();

            List<TableRow> rows = executor.executeAggregate(mongoQL);
            packager.pack(rows);

//        } catch (RuntimeException re) {
//            AppCore.getInstace().getMessageHandler().displayError(re.getMessage());
//        }



    }
}

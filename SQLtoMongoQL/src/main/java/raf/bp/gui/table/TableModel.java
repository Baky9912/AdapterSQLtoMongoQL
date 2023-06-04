package raf.bp.gui.table;

import raf.bp.app.AppCore;
import raf.bp.model.TableRow;

import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class TableModel extends DefaultTableModel {
    private List<TableRow> rows;

    private void updateModel(){

        if (rows.size() == 0) {
            AppCore.getInstace().getMessageHandler().displayOK("No records found");
            return;
        }
        int columnCount = rows.get(0).getFields().keySet().size();

        Vector columnVector = DefaultTableModel.convertToVector(rows.get(0).getFields().keySet().toArray());
        Vector dataVector = new Vector(columnCount);

        for (int i=0; i<rows.size(); i++){
            dataVector.add(DefaultTableModel.convertToVector(rows.get(i).getFields().values().toArray()));
        }
        setDataVector(dataVector, columnVector);
    }

    public void setRows(List<TableRow> rows) {
        this.rows = rows;
        updateModel();
    }


}
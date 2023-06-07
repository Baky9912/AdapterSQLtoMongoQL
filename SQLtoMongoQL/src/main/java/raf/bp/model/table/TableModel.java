package raf.bp.model.table;

import raf.bp.model.table.TableRow;

import javax.swing.table.DefaultTableModel;
import java.util.List;
import java.util.Vector;

public class TableModel extends DefaultTableModel {
    private List<TableRow> rows;

    private void updateModel(){

        if (rows.isEmpty()) return;
        int columnCount = rows.get(0).getFields().keySet().size();
        Vector columnVector = DefaultTableModel.convertToVector(rows.get(0).getFields().keySet().toArray());
        Vector dataVector = new Vector(columnCount);

        for (TableRow row : rows) {
            dataVector.add(DefaultTableModel.convertToVector(row.getFields().values().toArray()));
        }
        setDataVector(dataVector, columnVector);
    }

    public void clearTableModel() {
        this.getDataVector().removeAllElements();
        this.fireTableDataChanged();

    }

    public void setRows(List<TableRow> rows) {
        this.rows = rows;
        updateModel();
    }


}

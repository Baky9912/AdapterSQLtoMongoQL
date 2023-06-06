package raf.bp.packager;

import raf.bp.app.AppCore;
import raf.bp.model.TableRow;
import java.util.List;

public class TablePackager {

    public void pack(List<TableRow> rows) {
        AppCore.getInstance().getTableModel().setRows(rows);
    }

}

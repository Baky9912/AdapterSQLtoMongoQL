package raf.bp.packager.concrete;

import raf.bp.app.AppCore;
import raf.bp.model.table.TableRow;
import raf.bp.packager.Packager;

import java.util.List;

public class TablePackager implements Packager<Void> {

    public Void pack(List<TableRow> rows) {
        AppCore.getInstance().getTableModel().setRows(rows);
        return null;
    }

}

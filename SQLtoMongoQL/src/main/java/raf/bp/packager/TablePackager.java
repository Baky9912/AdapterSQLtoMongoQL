package raf.bp.packager;

import raf.bp.app.AppCore;
import raf.bp.model.TableRow;
import java.util.List;

public class TablePackager implements Packager<Void> {

    public Void pack(List<TableRow> rows) {
        AppCore.getInstance().getTableModel().setRows(rows);
        return null;
    }

}

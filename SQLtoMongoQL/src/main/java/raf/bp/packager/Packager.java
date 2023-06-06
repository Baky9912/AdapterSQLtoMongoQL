package raf.bp.packager;

import raf.bp.model.table.TableRow;

import java.util.List;

public interface Packager<R> {

    R pack(List<TableRow> input);
}

package raf.bp.executor;

import java.util.List;

import raf.bp.model.table.TableRow;

public interface Executor<T> {
    List<TableRow> execute(T input);
}


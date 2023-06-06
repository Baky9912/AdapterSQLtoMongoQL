package raf.bp.executor.concrete;

import raf.bp.executor.Executor;
import raf.bp.model.TableRow;
import raf.bp.model.SQL.SQLQuery;

import java.util.List;

abstract public class SQLExecutor implements Executor<SQLQuery>{
    // fake, so we can have SQLExecutor
    @Override
    abstract public List<TableRow> execute(SQLQuery query);
}

package raf.bp.executor;

import raf.bp.model.TableRow;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;

import java.util.ArrayList;

abstract public class SQLExecutor {
    abstract public ArrayList<TableRow> execute(String query);
    abstract public ArrayList<TableRow> execute(SQLQuery query);
    // TODO moze da se zameni sa SQLQuery
}

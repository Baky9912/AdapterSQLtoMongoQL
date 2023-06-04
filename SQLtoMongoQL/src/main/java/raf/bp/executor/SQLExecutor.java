package raf.bp.executor;

import raf.bp.model.TableRow;
import java.util.ArrayList;

abstract public class SQLExecutor {
    abstract public ArrayList<TableRow> execute(String query);
    // TODO moze da se zameni sa SQLQuery
}

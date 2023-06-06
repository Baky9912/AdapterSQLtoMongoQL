package raf.bp.parser;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.convertableSQL.CSQLOperator;

public interface Parser<R, I> {
    R parse(I input);
}

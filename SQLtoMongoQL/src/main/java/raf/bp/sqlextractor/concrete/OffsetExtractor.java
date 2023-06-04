package raf.bp.sqlextractor.concrete;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLToken;
import raf.bp.sqlextractor.SQLExtractor;

public class OffsetExtractor extends SQLExtractor {
    public static Integer extractOffset(SQLClause clause){
        assert clause.getKeyword().equals("offset");
        SQLExpression expr = clause.getSqlExpressions().get(0);
        SQLToken token = (SQLToken)expr;
        return Integer.parseInt(token.getWord());
    }
}

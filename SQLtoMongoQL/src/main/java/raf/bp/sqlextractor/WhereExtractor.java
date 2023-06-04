package raf.bp.sqlextractor;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.parser.ConditionSQLParser;

public class WhereExtractor {
    public CSQLOperator extractTopNode(SQLClause clause){
        assert clause.getKeyword().equals("where");
        ConditionSQLParser csp = new ConditionSQLParser();
        return csp.parse(clause);
    } 
}

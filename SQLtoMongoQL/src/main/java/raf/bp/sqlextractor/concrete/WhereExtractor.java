package raf.bp.sqlextractor.concrete;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.parser.ConditionSQLParser;
import raf.bp.sqlextractor.SQLExtractor;

// no abstract for Extractors, avoiding problems with return Object
public class WhereExtractor extends SQLExtractor {
    public WhereExtractor(SQLClause clause) {
        super(clause);
        assert clause.getKeyword().equals("where");
    }

    public CSQLOperator extractTopNode(){
        ConditionSQLParser csp = new ConditionSQLParser();
        return csp.parse(getClause());
    } 
}

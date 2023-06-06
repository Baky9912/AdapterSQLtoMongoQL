package raf.bp.adapter.extractor.concrete;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.parser.concrete.ConditionSQLParser;
import raf.bp.adapter.extractor.SQLExtractor;

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

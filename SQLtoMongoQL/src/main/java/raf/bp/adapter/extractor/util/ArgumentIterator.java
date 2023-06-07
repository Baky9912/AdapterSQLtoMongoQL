package raf.bp.adapter.extractor.util;

import java.util.Iterator;

import raf.bp.app.AppCore;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.parser.Parser;

public class ArgumentIterator implements Iterator<String>{
    private int i;
    SQLClause clause;

    public ArgumentIterator(SQLClause clause){
        i=0;
        this.clause = clause;
    }

    @Override
    public boolean hasNext() {
        return i<clause.getSqlExpressions().size();
    }

    @Override
    public String next() {
        SQLExpression expr = clause.getSqlExpressions().get(i++);
        SQLToken token = (SQLToken)expr;
        return token.getWord();
    }

    public static void test(String[] args){
        // test
        Parser<SQLQuery, String> p = AppCore.getInstance().getSqlParser();
        String q7 = "select department_name, department_id, location_id from hr.departments where department_id in\n";
        SQLQuery query = p.parse(q7);
        ArgumentIterator argIter = new ArgumentIterator(query.getClause("select"));
        while(argIter.hasNext()){
            String x = argIter.next();
            System.out.println(x);
        }
    }

}

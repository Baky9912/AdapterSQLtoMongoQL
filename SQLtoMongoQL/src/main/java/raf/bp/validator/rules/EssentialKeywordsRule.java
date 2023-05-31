package raf.bp.validator.rules;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.validator.Rule;

public class EssentialKeywordsRule implements Rule {

    public class MissingEssentialKeywordException extends RuntimeException {
        public MissingEssentialKeywordException(String message) {
            super(message);
        }
    }

    @Override
    public boolean check(SQLQuery query) throws MissingEssentialKeywordException {

       boolean select = false, from = false;

       for (SQLClause clause : query.getClauses()) {
           if (clause.getKeyword().equals("select")) select = true;
           if (clause.getKeyword().equals("from")) from = true;

           for (SQLExpression expression : clause.getSqlExpressions()) {
               /* we can do a recursive call like this because nested query can appear after keyword "from" at the earliest */
               if (expression instanceof SQLQuery ex) return check(ex) && select && from;
           }

       }

       return select && from;

    }
}

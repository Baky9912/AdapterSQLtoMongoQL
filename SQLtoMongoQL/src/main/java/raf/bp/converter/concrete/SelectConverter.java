package raf.bp.converter.concrete;

import raf.bp.converter.ClauseConverter;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLToken;

public class SelectConverter extends ClauseConverter {


    @Override
    public String convert(SQLClause clause) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("{");

        for (SQLExpression ex : clause.getSqlExpressions()) {


            SQLToken token = (SQLToken) ex;

            if (token.getWord().equals("*")) return "{}";

            if (skipableTokens.contains(token.getWord())) continue;

            stringBuilder.append("\"").append(token.getWord()).append("\"");
            stringBuilder.append(": 1, ");

        }

        stringBuilder.append(" \"_id\": 0 }");

        return stringBuilder.toString();
    }
}

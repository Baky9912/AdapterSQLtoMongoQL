package raf.bp.converter.concrete;

import raf.bp.converter.Converter;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLToken;

public class GroupByConverter extends Converter {
    @Override
    public String convert(SQLClause clause) {

        StringBuilder stringBuilder = new StringBuilder("{ ");
        for (SQLExpression expression : clause.getSqlExpressions()) {

            SQLToken token = (SQLToken) expression;
            if (skipableTokens.contains(token.getWord())) continue;

            if (token.getWord().equals("asc")) {
                stringBuilder.append(": 1, ");
                continue;
            }
            else if (token.getWord().equals("desc")) {
                stringBuilder.append(": -1, ");
                continue;
            }

            stringBuilder.append("\"").append(token.getWord()).append("\"");

        }

        stringBuilder.deleteCharAt(stringBuilder.length() - 2);
        stringBuilder.append(" }");

        return stringBuilder.toString();
    }
}
package raf.bp.model.SQL;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SQLClause {
    private String keyword;
    private List<SQLExpression> sqlExpressions;
    public SQLClause(String keyword, List<SQLExpression> sqlExpressions){
        this.keyword = keyword;
        this.sqlExpressions = sqlExpressions;
    }

    @Override
    public String toString() {
        String result = keyword;
        result += sqlExpressions.toString();

        return result;

    }

    public boolean hasAggregation() {
        ArrayList<String> aggFuncs = new ArrayList<>(List.of("min", "max", "avg", "count", "sum"));
        for (SQLExpression ex : sqlExpressions) {
            if (ex instanceof SQLQuery) continue;
            SQLToken token = (SQLToken) ex;

            if (aggFuncs.contains(token.getWord())) return true;
        }

        return false;
    }
}

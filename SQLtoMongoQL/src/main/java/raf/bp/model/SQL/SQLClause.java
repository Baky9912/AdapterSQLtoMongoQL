package raf.bp.model.SQL;

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
}

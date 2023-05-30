package raf.bp.model.SQL;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SQLQuery extends SQLExpression{
    private List<SQLClause> clauses;
    public SQLQuery(List<SQLClause> clauses){
        this.clauses = clauses;
    }
    public SQLQuery(){}

}

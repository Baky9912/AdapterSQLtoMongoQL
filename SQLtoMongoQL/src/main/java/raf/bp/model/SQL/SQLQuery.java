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

    private void tabOut(int depth){
        for(int i=0; i<depth; ++i){
            System.out.print("\t");
        }
    }

    public void printQuery(int depth){
        tabOut(depth);
        System.out.println("(");
        for(SQLClause clause : clauses){
            tabOut(depth);
            System.out.print(clause.getKeyword().toUpperCase());
            System.out.print(" ");
            SQLExpression last=null;
            for(SQLExpression expr : clause.getSqlExpressions()){
                if(expr instanceof SQLQuery sq){
                    System.out.println();
                    sq.printQuery(depth+1);
                }
                else if(expr instanceof SQLToken token){
                    System.out.print(token.getWord());
                    System.out.print(" ");
                }
                last = expr;
            }
            if(last!=null && !(last instanceof SQLQuery))
                System.out.println();
        }
        tabOut(depth);
        System.out.println(")");
    }

    public SQLClause getClause(String keyword){
        for(SQLClause clause : this.clauses){
            if(clause.getKeyword().equals(keyword))
                return clause;
        }
        return null;
    }

    public static void printAnyQuery(SQLQuery q){
        if(q==null){
            System.out.println("Query can't be parsed");
        }
        else{
            System.out.println("QUERY");
            System.out.println("-----------------------------");
            q.printQuery(0);
            System.out.println("-----------------------------");
        }
    }
}

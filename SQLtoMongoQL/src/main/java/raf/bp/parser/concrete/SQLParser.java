package raf.bp.parser.concrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import raf.bp.app.AppCore;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.parser.Parser;
import raf.bp.parser.expression.ComplexExpression;
import raf.bp.parser.expression.Expression;
import raf.bp.parser.expression.SymbolExpression;

public class SQLParser implements Parser<SQLQuery, String> {
    public List<String> getKeywords(){
        return l1Keywords;
    }

    public static class NoArgumentsException extends RuntimeException{
        public NoArgumentsException(String errMsg){
            super(errMsg);
        }
    }
    public static class ArgumentsBeforeKeywordException extends RuntimeException{
        public ArgumentsBeforeKeywordException(String errMsg){
            super(errMsg);
        }
    }

    public static class ParserException extends RuntimeException{
        public ParserException(String errMsg){
            super(errMsg);
        }
    }

    private int max_level = 10;
    private String[] sqlL1Keywords = {"select", "from", "where", "group_by", "order_by", "limit", "offset"};
    // They don't depend on other keywords ex HAVING depends on GROUP BY existing, it's not L1
    private List<String> l1Keywords = new ArrayList<>(Arrays.asList(sqlL1Keywords));
    private SQLLexer sqlLexer = new SQLLexer();

    public SQLParser(){}


    public boolean validateBrackets(String[] tokens){
        int level=0;
        for(String token : tokens){
            if(token.equals("("))
                level++;
            else if(token.equals(")"))
                level--;
            else continue;

            if(level<0 || level>=max_level)
                return false;
        }
        return level==0;
    }

    public ComplexExpression makeExpression(String[] tokens){
        ComplexExpression[] exprByLevel = new ComplexExpression[max_level];
        for(int i=0; i<max_level; ++i){
            exprByLevel[i] = new ComplexExpression();
        }
        int level = 0;
        for(String token : tokens){
            if(token.equals("("))
                level++;
            else if(token.equals(")")){
                ComplexExpression madeExpr = exprByLevel[level];
                exprByLevel[level] = new ComplexExpression();
                exprByLevel[level-1].getExpressions().add(madeExpr);
                level--;
            }
            else{
                exprByLevel[level].getExpressions().add(new SymbolExpression(token));
            }
        }
        return exprByLevel[0];
    }

    public Expression makeExpression(String query){
        String[] tokens = sqlLexer.lex(query);
        System.out.println("AFTER LEX");
        printTokens(tokens);
        ComplexExpression expr = makeExpression(tokens);
        System.out.println("AFTER MAKE EXPR");
        System.out.println(expr.toString());
        return expr;
    }

    public void printTokens(String[] tokens){
        for(String tok : tokens){
            System.out.println(tok);
        }
        System.out.println();
    }

    @Override
    public SQLQuery parse(String query){
        Expression expr = makeExpression(query);
        if(expr instanceof ComplexExpression){
            return parseComplexExpressionUtil((ComplexExpression)expr, null);
        }
        else{
            throw new ParserException("Preparsing error");
        }
    }

    public SQLQuery parseComplexExpressionUtil(ComplexExpression ce, String fakeKeyword){
        List<SQLClause> clauses = new ArrayList<>();
        //SQLClause clause = new SQLClause();
        String keyword = fakeKeyword;
        List<SQLExpression> sqlExpressions = new ArrayList<>();
        for(Expression e : ce.getExpressions()){
            if(e instanceof ComplexExpression inner_ce){
                if(inner_ce.isNestedQuery()){
                    SQLQuery innerQuery = parseComplexExpressionUtil(inner_ce, null);
                    sqlExpressions.add(innerQuery);
                }
                else{
                    SQLQuery innerQuery = parseComplexExpressionUtil(inner_ce, "START_NESTED");
                    List<SQLClause> innerClauses = innerQuery.getClauses();
                    if(innerClauses.size()>1) {
                        throw new ArgumentsBeforeKeywordException("Arguments before keywords in nested query");
                    }
                    List<SQLExpression> innerExpressions = innerClauses.get(0).getSqlExpressions();
                    sqlExpressions.add(new SQLToken("("));
                    sqlExpressions.addAll(innerExpressions);
                    sqlExpressions.add(new SQLToken(")"));
                }
            }
            else if(e instanceof SymbolExpression se){
                if(l1Keywords.contains(se.getWord())){
                    if(keyword==null){
                        if(sqlExpressions.size() > 0){
                            throw new ArgumentsBeforeKeywordException("Arguments before keyword");
                        }
                    }
                    else{
                        if(sqlExpressions.size()==0)
                            if(keyword.equals("START_NESTED"))
                                throw new NoArgumentsException("Missing argument inside ()");
                            else
                                throw new NoArgumentsException("Keyword " + keyword + " has no arguments");
                        clauses.add(new SQLClause(keyword, sqlExpressions));
                    }
                    keyword = se.getWord();
                    sqlExpressions = new ArrayList<>();
                }
                else{
                    SQLToken token = new SQLToken(se.getWord());
                    sqlExpressions.add(token);
                }
            }
        }
        if(keyword!=null && sqlExpressions.size()==0){
            if(keyword.equals("START_NESTED"))
                throw new NoArgumentsException("Missing argument inside ()");
            else
                throw new NoArgumentsException("Keyword " + keyword + " has no arguments");
        }
        if(sqlExpressions.size()>0){
            if(keyword==null){
                throw new ArgumentsBeforeKeywordException("Arguments before keyword");
            }
            clauses.add(new SQLClause(keyword, sqlExpressions));
        }
        return new SQLQuery(clauses);
    }

    static public void main(String[] args){
        // TESTS
        String q1 = "SELECT       avg(salary),          department_id from hr.employees group by department_id";
        String q2 = "select department_name, department_id, location_id from hr.departments where department_id in\n" 
	+ "(select department_id from hr.employees group by department_id having max(salary) > 10000)";
        String q3 = "SELECT avg(salary) FROM WHERE";
        String q4 = "a SELECT avg(salary) FROM x WHERE";
        String q5 = "SELECT avg((salary) FROM x WHERE";
        String q6 = "SELECT avg(salary)) FROM x WHERE";
        String q7 = "select department_name, department_id, location_id from hr.departments where department_id in\n" 
	+ "(ooo select department_id from hr.employees group by department_id having max(salary) > 10000)";
        String q8 = "select department_name, department_id, location_id from hr.departments where department_id in\n" 
	+ "(select department_id from hr.employees group by department_id having max(salary) > 10000) and department_name like \" hello world  \t\"";
        String q9 = "select department_name, department_id, location_id from hr.departments where department_id in\n" 
	+ "(select department_id from hr.employees group by department_id having max(salary) > 10000) and department_name like \" HelLo world  \t\"and true";

        String q10 = "select last_name as prezime from employees where salary = sum(salary)";
        String q11 = "select a where >= [\"He llllooooooo\", 3] <= x < a = b >= b >= from x";
        String q12 = "select a where > = <   = < a = b >= b > = from x";
        String q13 = ">= x <=   ";
        String q14 = "select * from table1 join table2 using ( ) ";
        List<String> queries1 = new ArrayList<>(Arrays.asList(new String[]{q1, q2, q3, q4, q5, q6, q7, q8, q9}));
        List<String> queries2 = new ArrayList<>(Arrays.asList(new String[]{q10, q11, q12, q13, q14}));
        List<String> queries3 = new ArrayList<>(Arrays.asList(new String[]{q12}));

        SQLParser p = (SQLParser)AppCore.getInstance().getSqlParser();
        
        for(String query : queries3){
            try{
                SQLQuery parsedQuery = p.parse(query);
                SQLQuery.printAnyQuery(parsedQuery);
            }
            catch (RuntimeException e){
                System.out.println(e);
            }
        }
    }
}

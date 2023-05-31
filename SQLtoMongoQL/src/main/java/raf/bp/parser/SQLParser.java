package raf.bp.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.parser.expression.ComplexExpression;
import raf.bp.parser.expression.Expression;
import raf.bp.parser.expression.SymbolExpression;

public class SQLParser {
    String[] _l1Keywords = {"select", "from", "where", "group_by", "order_by", "limit", "rownum"};
    private List<String> l1Keywords = new ArrayList<>(Arrays.asList(_l1Keywords));

    public List<String> getKeywords(){
        return l1Keywords;
    }

    public class NoArgumentsException extends RuntimeException{
        public NoArgumentsException(String errMsg){
            super(errMsg);
        }
    }
    public class ArgumentsBeforeKeywordException extends RuntimeException{
        public ArgumentsBeforeKeywordException(String errMsg){
            super(errMsg);
        }
    }
    public class BadBracketsException extends RuntimeException{
        public BadBracketsException(String errMsg){
            super(errMsg);
        }
    }
    public class ParserException extends RuntimeException{
        public ParserException(String errMsg){
            super(errMsg);
        }
    }

    // postoje arg keywords kao between, in, itd...
    //  "inner_join", "outer_join","left_join", "right_join", "join", "having" - ovo su argumenti za from
    int max_level = 10;

    Map<String, String> suffix = new HashMap<String, String>() {{
        put("inner", "join");
        put("left", "join");
        put("right", "join");
        put("outer", "join");
        put("group", "by");
        put("order", "by");
    }};


    public SQLParser(){}

    public String[] lex(String query){
        String myquery = query;
        myquery = myquery.toLowerCase();
        myquery = myquery.replace(",", " , ");
        myquery = myquery.replace("(", " ( ");
        myquery = myquery.replace(")", " ) ");
        myquery = myquery.replaceAll("\\s+", " ");
        System.out.println(myquery);
        for(Map.Entry<String, String> entry : suffix.entrySet()){
            String old_pattern = entry.getKey() + " " + entry.getValue();
            String new_pattern = entry.getKey() + "_" + entry.getValue();
            myquery = myquery.replace(old_pattern, new_pattern);
        }
        String[] tokens = myquery.split(" ");
        return tokens;
    }

    public boolean validateBrackets(String[] tokens){
        int level=0;
        for(String token : tokens){
            if(token.equals("("))
                level++;
            else if(token.equals(")"))
                level--;
            else continue;

            if(level<0 || level>max_level)
                return false;
        }
        return level==0;
    }

    public ComplexExpression makeExpression(String[] tokens){
        ComplexExpression[] exprByLevel = new ComplexExpression[10];
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
        String[] tokens = lex(query);
        System.out.println("AFTER LEX");
        printTokens(tokens);
        if(!validateBrackets(tokens)){
            throw new BadBracketsException("Brackets don't match");
        }
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

    public SQLQuery parseQuery(String query){
        Expression expr = makeExpression(query);
        if(expr instanceof ComplexExpression){
            return parseComplexExpressionUtil((ComplexExpression)expr, null);
        }
        else{
            throw new ParserException("Preparsing error");
        }
    }

    public SQLQuery parseComplexExpressionUtil(ComplexExpression ce, String fakeKeyword){
        List<SQLClause> clauses = new ArrayList<SQLClause>();
        //SQLClause clause = new SQLClause();
        String keyword = fakeKeyword;
        List<SQLExpression> sqlExpressions = new ArrayList<SQLExpression>();
        for(Expression e : ce.getExpressions()){
            if(e instanceof ComplexExpression inner_ce){
                if(inner_ce.isNestedQuery()){
                    SQLQuery innerQuery = parseComplexExpressionUtil(inner_ce, null);
                    sqlExpressions.add(innerQuery);
                }
                else{
                    SQLQuery innerQuery = parseComplexExpressionUtil(inner_ce, "fakekw");
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
                            throw new NoArgumentsException("Keyword " + keyword + " has no arguments");
                        clauses.add(new SQLClause(keyword, sqlExpressions));
                    }
                    keyword = se.getWord();
                    sqlExpressions = new ArrayList<SQLExpression>();
                }
                else{
                    SQLToken token = new SQLToken(se.getWord());
                    sqlExpressions.add(token);
                }
            }
        }
        if(keyword!=null && sqlExpressions.size()==0){
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

        List<String> queries = new ArrayList<>(Arrays.asList(new String[]{q1, q2, q3, q4, q5, q6, q7}));
        
        SQLParser p = new SQLParser();
        
        for(String query : queries){
            try{
                SQLQuery parsedQuery = p.parseQuery(query);
                SQLQuery.printAnyQuery(parsedQuery);
            }
            catch (RuntimeException e){
                System.out.println(e.toString());
            }
        }
    }
}

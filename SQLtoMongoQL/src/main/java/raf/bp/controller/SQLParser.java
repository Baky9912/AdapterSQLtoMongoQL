package raf.bp.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import raf.bp.model.expression.*;

public class SQLParser {
    String[] l1_keywords = {"select", "from", "where", "group_by", "order_by", "limit", "rownum"};
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


    public SQLParser(){
    }

    public String[] lex(String query){

        String myquery = query;
        myquery = myquery.toLowerCase();
        myquery = myquery.replace(",", " ");  // field1, field2, -> field1 field2
        myquery = myquery.replace("(", " ( ");
        myquery = myquery.replace(")", " ) ");
        myquery = myquery.replaceAll("\\s+", " ");
        //query.replaceAll(",[^ ]", " ");  // ,a u , a
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
            if(token=="(")
                level++;
            else if(token==")")
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
            if(token=="(")
                level++;
            else if(token==")"){
                ComplexExpression madeExpr = exprByLevel[level];
                exprByLevel[level] = new ComplexExpression();
                exprByLevel[level-1].expressions.add(madeExpr);
                level--;
            }
            else{
                exprByLevel[level].expressions.add(new SymbolExpression(token));
            }
        }
        return exprByLevel[0];
    }

    public Expression makeExpression(String query){
        String[] tokens = lex(query);
        System.out.println("AFTER LEX");
        printTokens(tokens);
        if(!validateBrackets(tokens)){
            return new SymbolExpression("WRONG_EXPR");
        }
        Expression expr = makeExpression(tokens);
        System.out.println("AFTER MAKE EXPR");
        System.out.println(expr);
        return expr;
    }

    public void printTokens(String[] tokens){
        for(String tok : tokens){
            System.out.println(tok);
        }
        System.out.println();
    }

    public Map<String, List<String>> groupByL1Keyword(String[] tokens){
        // mozda validate posle lex i pre ovog
        Map<String, List<String>> l1ToArgs = new HashMap<>();
        String l1Keyword = null;
        ArrayList<String> args = new ArrayList<>();

        for(String token : tokens){
            if(l1Keyword.contains(token)){
                if(l1Keyword!=null){
                    l1ToArgs.put(l1Keyword, args);
                    args.clear();
                }
                l1Keyword = token;
            }
            else
                args.add(l1Keyword);
        }
        return l1ToArgs;
    }

    static public void main(String[] args){
        String q1 = "SELECT avg(salary), department_id from hr.employees group by department_id";
        String q2 = "select department_name, department_id, location_id from hr.departments where department_id in\n" 
	+ "(select department_id from hr.employees group by department_id having max(salary) > 10000)";
        
        SQLParser p = new SQLParser();
        
        p.makeExpression(q1);
        p.makeExpression(q2);
    }
}

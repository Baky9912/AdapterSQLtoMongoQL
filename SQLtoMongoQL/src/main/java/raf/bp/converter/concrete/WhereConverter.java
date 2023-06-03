package raf.bp.converter.concrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import raf.bp.converter.ClauseConverter;
import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.model.convertableSQL.operator.CSQLBinaryOperator;
import raf.bp.model.convertableSQL.operator.CSQLUnaryOperator;

public class WhereConverter extends ClauseConverter{
    private static Map<String, String> sqlToMongoOp = new HashMap<>() {{
        put("*", "$multiply");
        put("/", "$divide");
        put("%", "$mod");
        put("<", "$le");
        put(">", "$gt");
        put("<=", "$lte");
        put(">=", "$gte");
        put("!=", "$ne");
        put("=", "$eq");
        put("not", "$not");
        put("not", "$not");
        put("and", "$and");
        put("or", "$or");
        put("like", "$regex");
        put("in", "$in");
    }};

    private static Set<String> argArray = new HashSet<>(Arrays.asList("$and", "$or"));
    private static Set<String> argElement = new HashSet<>() {{
        for(String op : sqlToMongoOp.values()){
            if(!argArray.contains(op)){
                add(op);
            }
        }
    }};
    private static Map<String, Integer> argCnt = new HashMap<>() {{
        for(String sqlOp : CSQLUnaryOperator.operators){
            if(!sqlToMongoOp.containsKey(sqlOp)) continue;
            String mongoOp = sqlToMongoOp.get(sqlOp);
            put(mongoOp, 1);
        }

        for(String sqlOp : CSQLBinaryOperator.operators){
            if(!sqlToMongoOp.containsKey(sqlOp)) continue;
            String mongoOp = sqlToMongoOp.get(sqlOp);
            put(mongoOp, 2);
        }
    }};

}

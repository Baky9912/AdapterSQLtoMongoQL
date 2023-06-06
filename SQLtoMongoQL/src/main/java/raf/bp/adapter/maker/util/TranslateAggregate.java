package raf.bp.adapter.maker.util;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import raf.bp.model.convertableSQL.datatypes.CSQLAggregateFunction;

public class TranslateAggregate {
    private static Map<String, String> aggMapping = new HashMap<>() {{
        put("count", "size");
        put("max", "max");
        put("min", "min");
        put("avg", "avg");
        put("sum", "sum");
    }};
    private static Map<String, String> aggGroupMapping = new HashMap<>() {{
        put("count", "sum");
        put("max", "max");
        put("min", "min");
        put("avg", "avg");
        put("sum", "sum");
    }};


    public static String translateAggFuncName(CSQLAggregateFunction aggFunc){
        String funcArgument = (aggFunc.getArg().equals("*")) ? "_id" : aggFunc.getFieldName();

        return funcArgument + "_" + aggFunc.getFunc();
    }

    public static Bson makeMongoAggFunc(CSQLAggregateFunction aggFunc){
        // unsure
        String funcArgument = (aggFunc.getArg().equals("*")) ? "_id" : aggFunc.getFieldName();


        return new Document("$" + aggMapping.get(aggFunc.getFunc()), "$" + funcArgument);
//        return new Document("$" + aggMapping.get(aggFunc.getFunc()), "$" + aggFunc.getTable());
    }

    public static Bson makeGroupAggFunc(CSQLAggregateFunction aggFunc) {
        String funcArgument = (aggFunc.getArg().equals("*")) ? "$_id" : aggFunc.getArg();

        var argument = (aggFunc.getFunc().equals("count")) ? 1 : "$" + funcArgument;

        return new Document("$" + aggGroupMapping.get(aggFunc.getFunc()), argument);
    }
}

package raf.bp.adapter.fields.util;

import java.util.HashMap;
import java.util.Map;

import com.mongodb.client.model.Accumulators;
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
        return aggFunc.getFieldName() + "_" + aggFunc.getFunc();
    }

    public static Bson makeMongoAggFunc(CSQLAggregateFunction aggFunc){
        // unsure
        return new Document("$" + aggMapping.get(aggFunc.getFunc()), "$" + aggFunc.getTable());
    }

    public static Bson makeGroupAggFunc(CSQLAggregateFunction aggFunc) {
        var argument = (aggFunc.getFunc().equals("count")) ? 1 : aggFunc.getArg();
        return new Document("$" + aggGroupMapping.get(aggFunc.getFunc()), argument);
    }
}

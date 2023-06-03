package raf.bp.converter.concrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import raf.bp.converter.ClauseConverterManager;
import raf.bp.model.convertableSQL.CSQLDatatype;
import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.model.convertableSQL.CSQLType;
import raf.bp.model.convertableSQL.CSQLDatatype.Subtype;
import raf.bp.model.convertableSQL.datatypes.CSQLArray;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import raf.bp.model.convertableSQL.operator.CSQLBinaryOperator;
import raf.bp.model.convertableSQL.operator.CSQLUnaryOperator;

public class WhereConverter extends ClauseConverterManager{
    // TODO UNTESTED
    private static Map<String, String> sqlToMongoOp = new HashMap<>() {{
        put("*", "$multiply");
        put("/", "$divide");
        put("%", "$mod");
        put("<", "$lt");
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
    
    private static Map<String, String> mongoToSqlOp = new HashMap<>() {{
        for (Map.Entry<String, String> entry : sqlToMongoOp.entrySet()) {
            mongoToSqlOp.put(entry.getValue(), entry.getKey());
        }   
    }};

    private static Map<String, String> reverseSign = new HashMap<>() {{
        put("$lt", "$gt");
        put("$gt", "$lt");
        put("$lte", "$gte");
        put("$gte", "$lte");
        put("$ne", "$ne");
        put("$eq", "$eq");
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


    public String makeMongo(CSQLType type, int depth){
        // TODO UNTESTED
        for(int i=0; i<depth; ++i)
            System.out.print("  ");
        System.out.println(type.toSQLString());
        if(type instanceof CSQLOperator operator){
            String lArg = makeMongo(operator.getLeftOperand(), depth+1);
            String rArg = null;
            if(operator.getRightOperand()!=null)
                rArg = makeMongo(operator.getRightOperand(), depth+1);
            
            String mongoOp = sqlToMongoOp.get(operator.getOperator());
            if(mongoOp.equals("$regex")){
                // possible problems if logical operators are not used as logical, we don't check!
                // only unary/binary 
                rArg = convertRegex(rArg);
            }
            return joinOp(mongoOp,lArg, rArg);
        }
        else if(type instanceof CSQLDatatype data){
            return convertDatatype(data);
        }

        return "PROBLEM!!!";  // shouldn't happen
    }

    public boolean isNumber(String potentialNumber){
        // quickfix
        try{
            Double.parseDouble(potentialNumber);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }

    public String joinOp(String op, String lArg, String rArg){
        // { field: { $gt: value } }
        // { $and: [ { <expression1> }, { <expression2> } , ... , { <expressionN> } ] }
        String sqlOp = mongoToSqlOp.get(op);
        if(op.equals("$regex")){
            return joinRegexOp(op, lArg, rArg);
        }
        if(op.equals("$in")){
            return joinInOp(op, lArg, rArg);
        }
        if(CSQLOperator.binOpLogical.contains(sqlOp)){
            return joinBinOpLogical(op, lArg, rArg);
        }
        if(CSQLOperator.unOpLogical.contains(sqlOp)){
            return joinUnOpLogical(op, lArg);
        }
        if(CSQLOperator.numberComparison.contains(sqlOp)){
            return joinArithemticOp(op, lArg, rArg);
        }
        throw new RuntimeException("Problem in joinOp");
    }

    public String joinBinOpLogical(String op, String lArg, String rArg){
        return "{ " + op + ": [" + lArg + ", " + rArg + "]}";
    }

    public String joinUnOpLogical(String op, String arg){
        return "{ " + op + ": " + arg + "}";
    }

    public String joinArithemticOp(String op, String lArg, String rArg){
        if(isNumber(lArg)){
            op = reverseSign.get(op);
            String t = lArg;
            lArg = rArg;
            rArg = t;
        }
        return "{ " + lArg + ": { " + op + ": " + rArg + "}}";
    }

    public String joinRegexOp(String op, String str, String re){
        return "{ " + str + " { $regex:" + convertRegex(re) + "}}";
    }

    public String joinInOp(String op, String lArg, String rArg){
        return "{ " + lArg + " { $in:" + rArg + "}}";
    }

    public String convertDatatype(CSQLDatatype datatype){
        Subtype subtype = datatype.getSubtype();
        // problem with switch and enum, ill write the bad variant
        if(subtype == Subtype.ARRAY){
            return convertArray((CSQLArray)datatype);
        }
        else if(subtype == Subtype.FIELD){
            return convertField((CSQLSimpleDatatype)datatype, false);
        }
        else if(subtype == Subtype.NUMBER){
            return convertNumber((CSQLSimpleDatatype)datatype);
        }
        else if(subtype == Subtype.STRING){
            return convertString((CSQLSimpleDatatype)datatype);
        }
        return "PROBLEM!!!";
    }

    public String convertArray(CSQLArray array){
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        sb.append("[");
        for(CSQLSimpleDatatype sData : array.getEntries()){
            if(first) first=false;
            else sb.append(", ");
            String entry = convertDatatype(sData);
            sb.append(entry);
        }
        sb.append("]");
        return sb.toString();
    }

    public String convertField(CSQLSimpleDatatype field, boolean lvalue){
        if(lvalue)
            return "$" + field.getValue();
        return field.getValue();
    }

    public String convertNumber(CSQLSimpleDatatype number){
        return number.getValue();
    }

    public String convertString(CSQLSimpleDatatype string){
        return string.getValue();
    }

    public String convertRegex(String sqlRegex){
        String mongoRegex = "^" + sqlRegex + "$";
        mongoRegex = mongoRegex.replace("?", ".");
        mongoRegex = mongoRegex.replace("%", ".*");
        return mongoRegex;
    }
}

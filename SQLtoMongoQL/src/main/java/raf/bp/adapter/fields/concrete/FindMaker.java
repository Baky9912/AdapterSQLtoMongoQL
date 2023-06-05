package raf.bp.adapter.fields.concrete;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bson.Document;
import org.bson.conversions.Bson;

import raf.bp.adapter.fields.MongoQLMaker;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.convertableSQL.CSQLDatatype;
import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.model.convertableSQL.CSQLType;
import raf.bp.model.convertableSQL.CSQLDatatype.Subtype;
import raf.bp.model.convertableSQL.datatypes.CSQLArray;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import raf.bp.parser.ConditionSQLParser;
import raf.bp.sqlextractor.concrete.WhereExtractor;
import raf.bp.model.SQL.SQLQuery;

public class FindMaker extends MongoQLMaker {
        private static Map<String, String> sqlToMongoOp = new HashMap<>() {{
        put("*", "$multiply");
        put("/", "$divide");
        put("+", "$add");
        put("-", "$subtract");
        put("%", "$mod");
        put("<", "$lt");
        put(">", "$gt");
        put("<=", "$lte");
        put(">=", "$gte");
        put("!=", "$ne");
        put("=", "$eq");
        put("not", "$not");
        put("not", "$nor");
        put("and", "$and");
        put("or", "$or");
        put("like", "$regex");
        put("in", "$in");
    }};
    
    private static Map<String, String> mongoToSqlOp = new HashMap<>() {{
        for (Map.Entry<String, String> entry : sqlToMongoOp.entrySet()) {
            put(entry.getValue(), entry.getKey());
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

    private String makeMongo(CSQLType type){
        // TODO MOVE JOINS TO TEMPLATE
        // System.out.println(type.toSQLString());
        if(type instanceof CSQLOperator operator){
            // if concat get rvalue (add $)
            String lArg = makeMongo(operator.getLeftOperand());
            String rArg = null;
            if(operator.getRightOperand()!=null)
                rArg = makeMongo(operator.getRightOperand());
            String mongoOp = sqlToMongoOp.get(operator.getOperator());
            if(mongoOp==null){
                System.out.println("null");
                System.out.println(operator.getOperator());
                System.out.println(mongoOp);
                throw new RuntimeException("aaaa");
            }
            return joinOp(mongoOp, lArg, rArg);
        }
        else if(type instanceof CSQLDatatype data){
            return convertDatatype(data);
        }

        return "PROBLEM!!!";  // shouldn't happen
    }

    private boolean isNumber(String potentialNumber){
        // quickfix
        try{
            Double.parseDouble(potentialNumber);
            return true;
        }
        catch(NumberFormatException e){
            return potentialNumber.length()>4 && reverseSign.get(potentialNumber.substring(2, 4))!=null;
        }
    }

    private String joinOp(String op, String lArg, String rArg){
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
        if(CSQLOperator.arithemticOp.contains(sqlOp)){
            return joinArithmeticOp(op, lArg, rArg);
        }
        if(CSQLOperator.numberComparison.contains(sqlOp)){
            return joinNumComparasionOp(op, lArg, rArg);
        }
        System.out.println(sqlOp);
        System.out.println(op);
        throw new RuntimeException("Problem in joinOp");
    }

    private String joinBinOpLogical(String op, String lArg, String rArg){
        return "{ " + op + ": [" + lArg + ", " + rArg + "]}";
    }

    private String joinUnOpLogical(String op, String arg){
        return "{ " + op + ": [" + arg + "]}";
    }

    private String joinNumComparasionOp(String op, String lArg, String rArg){
        if(isNumber(lArg)){
            op = reverseSign.get(op);
            String t = lArg;
            lArg = rArg;
            rArg = t;
        }
        return "{ " + lArg + ": { " + op + ": " + rArg + "}}";
    }

    private String joinRegexOp(String op, String str, String re){
        return "{ " + str + " { $regex:" + convertRegex(re) + "}}";
    }

    private String joinInOp(String op, String lArg, String rArg){
        return "{ " + lArg + ": { $in:" + rArg + "}}";
    }

    private String joinArithmeticOp(String op, String lArg, String rArg){
        return "{ " + op + ": [" + lArg + ", " + rArg + "]}";
    }

    private String convertDatatype(CSQLDatatype datatype){
        Subtype subtype = datatype.getSubtype();
        // problem with switch and enum, ill write the bad variant
        if(subtype == Subtype.ARRAY){
            return convertArray((CSQLArray)datatype);
        }
        else if(subtype == Subtype.FIELD){
            return convertField((CSQLSimpleDatatype)datatype, true);
        }
        else if(subtype == Subtype.NUMBER){
            return convertNumber((CSQLSimpleDatatype)datatype);
        }
        else if(subtype == Subtype.STRING){
            return convertString((CSQLSimpleDatatype)datatype);
        }
        return "PROBLEM!!!";
    }

    private String convertArray(CSQLArray array){
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

    private String convertField(CSQLSimpleDatatype field, boolean lvalue){
        if(lvalue)
            return field.getValue();
        return "$" + field.getValue();
    }

    private String convertNumber(CSQLSimpleDatatype number){
        return number.getValue();
    }

    private String convertString(CSQLSimpleDatatype string){
        return string.getValue();
    }

    private String convertRegex(String sqlRegex){
        String mongoRegex = "^" + sqlRegex + "$";
        mongoRegex = mongoRegex.replace("?", ".");
        mongoRegex = mongoRegex.replace("%", ".*");
        return mongoRegex;
    }

    @Override
    public Bson make(SQLQuery query) {
        SQLClause clause = query.getClause("where");
        WhereExtractor whereExtractor = new WhereExtractor(clause);
        whereExtractor.extractTopNode();
        ConditionSQLParser condSQLParser = new ConditionSQLParser();
        CSQLOperator root = condSQLParser.parse(clause);
        return Document.parse(makeMongo(root));
    }
    
}

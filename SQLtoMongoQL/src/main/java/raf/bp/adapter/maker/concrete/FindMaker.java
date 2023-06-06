package raf.bp.adapter.maker.concrete;

import java.util.HashMap;
import java.util.Map;

import org.bson.Document;
import org.bson.conversions.Bson;

import raf.bp.adapter.maker.Maker;
import raf.bp.adapter.maker.util.FieldnameFixer;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.convertableSQL.CSQLDatatype;
import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.model.convertableSQL.CSQLType;
import raf.bp.model.convertableSQL.CSQLDatatype.Subtype;
import raf.bp.model.convertableSQL.datatypes.CSQLArray;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import raf.bp.model.convertableSQL.from.CSQLFromInfo;
import raf.bp.parser.concrete.ConditionSQLParser;
import raf.bp.parser.concrete.SQLParser;
import raf.bp.adapter.extractor.concrete.FromExtractor;
import raf.bp.adapter.extractor.concrete.WhereExtractor;
import raf.bp.model.SQL.SQLQuery;

public class FindMaker implements Maker {
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

    private CSQLFromInfo fromInfo;

    public FindMaker(SQLQuery query){
        FromExtractor fromExtractor = new FromExtractor(query.getClause("from"));
        fromInfo = fromExtractor.extractFromInfo();
    }

    private String makeMongo(CSQLType type){
        if(type instanceof CSQLOperator operator){
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
            return joinComparisonOp(op, lArg, rArg);
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

    private String joinComparisonOp(String op, String lArg, String rArg){
        // {$expr:{$eq:["$first_name", "Steven"]}}
        return "{$expr: {" + op + ":[" 
        + modifyExprArg(lArg) + "," + modifyExprArg(rArg) + "]}}";
    }

    private String joinRegexOp(String op, String strSource, String re){
        return "{ \""+ strSource + "\": { $regex:" + convertRegex(re) + "}}";
    }

    private String joinInOp(String op, String lArg, String rArg){
        return "{ " + lArg + ": { $in:" + rArg + "}}";
    }

    private String joinArithmeticOp(String op, String lArg, String rArg){
        return "{ " + op + ": [" + modifyExprArg(lArg) + "," + modifyExprArg(rArg) + "]}";
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
            return FieldnameFixer.fixRvalue(this.fromInfo, field.getValue());
        return "$" + FieldnameFixer.fixRvalue(this.fromInfo, field.getValue());
    }

    private String convertNumber(CSQLSimpleDatatype number){
        return number.getValue();
    }

    private String convertString(CSQLSimpleDatatype string){
        return string.getValue();
    }

    private String convertRegex(String sqlRegex){
        int n = sqlRegex.length();
        String baseRegex = sqlRegex.substring(1, n-1);
        String mongoRegex = "^" + baseRegex + "$";
        mongoRegex = mongoRegex.replace("?", ".");
        mongoRegex = mongoRegex.replace("%", ".*");
        mongoRegex = "\"" + mongoRegex + "\"";
        System.out.println("mongo regex");
        System.out.println(mongoRegex);
        return mongoRegex;
    }

    @Override
    public Bson make(SQLQuery query) {
        SQLClause clause = query.getClause("where");
        if (clause == null) return null;
        WhereExtractor whereExtractor = new WhereExtractor(clause);
        whereExtractor.extractTopNode();
        ConditionSQLParser condSQLParser = new ConditionSQLParser();
        CSQLOperator root = condSQLParser.parse(clause);
        String json = makeMongo(root);
        System.out.println("^=^+^+^+^+^+^^+^+^+^+^+^+^+^+");
        System.out.println(json);
        return Document.parse(json);
    }

    // trash fixes below

    private String modifyExprArg(String s){
        if(isBottomLevelArg(s)) return modifyBottomLevelArg(s);
        else return s;
    }

    private String modifyBottomLevelArg(String s){
        if(isRawNum(s) || isRawString(s)) return s;
        if(isField(s)) return "\"$" + s + "\"";
        return "PROBLEM!!!";
    }

    private boolean isVal(String potentialVal){
        // quickfix
        if(isRawNum(potentialVal)) return true;
        if(isRawString(potentialVal)) return true;
        return potentialVal.length()>4 && reverseSign.get(potentialVal.substring(2, 4))!=null;
    }

    private boolean isBottomLevelArg(String potentialVal){
        return isField(potentialVal) || isRawNum(potentialVal) || isRawString(potentialVal);
    }

    private boolean isRawString(String s){
        int n = s.length();
        return s.charAt(0)=='"' && s.charAt(n-1)=='"';
    }

    private boolean isRawNum(String i){
        try{
            Double.parseDouble(i);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }

    private boolean isField(String potentialField){
        // shouldnt have converted to strings!
        if(isRawNum(potentialField) || isRawString(potentialField)) return false;
        if(potentialField.contains("{") || potentialField.contains("}")) return false;
        return true;
    }    

    public static void main(String[] args) {
        // String q = "select first_name, last_name, salary from employees where salary > 10000 order by salary desc";
        // String q = "select first_name from employees where first_name like \"S%\"";
        String q = "select first_name, last_name from employees where first_name in (select first_name from employees where first_name=\"Steven\")";
        SQLParser p = new SQLParser();
        FindMaker fm = new FindMaker(p.parse(q));
        Bson bson = fm.make(p.parse(q));
        System.out.println(bson.toString());
    }
}

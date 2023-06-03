package raf.bp.converter.concrete;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import raf.bp.converter.ClauseConverter;
import raf.bp.model.convertableSQL.CSQLDatatype;
import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.model.convertableSQL.CSQLType;
import raf.bp.model.convertableSQL.CSQLDatatype.Subtype;
import raf.bp.model.convertableSQL.datatypes.CSQLArray;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
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

    public String joinOp(String op, String lArg, String rArg){
        // check how many operands and if they are arr or element operands
        // TODO
        return "";
    }

    public String convertDatatype(CSQLDatatype datatype){
        Subtype subtype = datatype.getSubtype();
        // problem with switch and enum, ill write the bad variant
        if(subtype == Subtype.ARRAY){
            return convertArray((CSQLArray)datatype);
        }
        else if(subtype == Subtype.FIELD){
            return convertField((CSQLSimpleDatatype)datatype);
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

    public String convertField(CSQLSimpleDatatype field){
        return "$" + field.getValue();
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

package raf.bp.parser;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLToken;
import raf.bp.model.convertableSQL.CSQLDatatype;
import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.model.convertableSQL.CSQLType;
import raf.bp.model.convertableSQL.IConvertableSQL;
import raf.bp.model.convertableSQL.datatypes.CSQLArray;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import raf.bp.model.convertableSQL.operator.CSQLBinaryOperator;
import raf.bp.model.convertableSQL.operator.CSQLUnaryOperator;

public class ConditionSQLParser {

    public int findClosingArray(List<SQLToken> tokens, int start){
        int i=start;
        for(i=start; !tokens.get(i).getWord().equals("]"); ++i);
        return i;
    }

    public List<CSQLType> makeConvertables(SQLClause clause){
        List<CSQLType> convertables = new ArrayList<>();
        List<SQLToken> tokens = new ArrayList<>();
        for(SQLExpression sqlExpr : clause.getSqlExpressions()){
            SQLToken token = (SQLToken)sqlExpr;
            // assuming SQLQuery returned tokens in its place, maybe do it here
            // if sqlquery replace it with sqltoken / csqldatatype after executing with adapter
            tokens.add(token);   
        }
        // make CSQL types, watch for array
        CSQLArray array = null;
        boolean expectingComma = false;
        for(SQLToken token : tokens){
            if(token.getWord().equals("[")){
                array = new CSQLArray();
                continue;
            }
            if(token.getWord().equals("]")){
                convertables.add(array);
                array = null;
                expectingComma = false;
                continue;
            }
            // CSQLSimpleDatatype simpleDatatype = new CSQLSimpleDatatype(token.getWord());
            CSQLType entry = null;
            if(CSQLOperator.operators.contains(token.getWord())){
                if(CSQLUnaryOperator.operators.contains(token.getWord())){
                    entry = new CSQLUnaryOperator(token.getWord(), null);
                }
                else{
                    entry = new CSQLBinaryOperator(token.getWord(), null, null);
                }
            }
            else{
                System.out.println(token.getWord());
                entry = new CSQLSimpleDatatype(token.getWord());
            }

            if(array!=null){
                if(entry instanceof CSQLSimpleDatatype simpleData){
                    if(expectingComma){
                        if(!simpleData.getValue().equals(",")){
                            throw new RuntimeException("Bad array formatting");
                        }
                        expectingComma = false;
                    } 
                    else{
                        array.getEntries().add(simpleData);
                        expectingComma = true;
                    }
                }
                else{
                    throw new RuntimeException("Operator in array");
                }
            }
            else{
                convertables.add(entry);
            }
        }
        return convertables;
    }
    public CSQLOperator parse(SQLClause clause){
        if(!clause.getKeyword().equals("where")){
            throw new RuntimeException("parsing condition for non-where keyword");
        }
        List<CSQLType> convertables = makeConvertables(clause);
        return parseUtil(convertables);
    }

    public CSQLOperator parseUtil(List<CSQLType> convertables){
        // simplify until they are all one, if not operator (logica but don't check) throw exception
        // doesnt support booleans, no reason to, can be added as 0 operand operator

        // DEAL WITH BRACKETS
        // for(CSQLType t : convertables) System.out.println(t.toSQLString());
        // System.out.println("----------------------------------------");

        int n = convertables.size();
        int level=0;
        int startBracket=-1;
        for(int i=n-1; i>=0; --i){
            if(convertables.get(i) instanceof CSQLSimpleDatatype data && data.getValue()==")"){
                if(level==0) startBracket = i;
                level++;
            }
            if(convertables.get(i) instanceof CSQLSimpleDatatype data && data.getValue()=="("){
                level--;
                if(level==0){
                    if(startBracket==-1) throw new RuntimeException("Bracket missmatch in condition");
                    // List<CSQLType> nestedQuery = convertables.subList(i+1, startBracket);
                    // sublist is destructive?

                    List<CSQLType> nestedQuery = new ArrayList<>();
                    for(int j=i+1; j<startBracket; ++j) nestedQuery.add(convertables.get(j));
                    CSQLOperator op = parseUtil(nestedQuery);
                    for(int j=startBracket; j>=i; --j) convertables.remove(j);
                    convertables.add(i, op);
                    startBracket=-1;
                }
                else if(level<0) throw new RuntimeException("Bracket missmatch in condition");
            }
        }


        // MAKE OPERATION BINARY TREE
        for(String[] layer : CSQLOperator.priority){
            Set<String> mlayer = new HashSet<>(Arrays.asList(layer));
            boolean changed = true;
            while(changed){
                changed=false;
                CSQLType prev;
                CSQLType curr;
                for(int i=1; i<convertables.size(); ++i){
                    prev = convertables.get(i-1);
                    curr = convertables.get(i);
                    if(prev instanceof CSQLUnaryOperator unOp 
                    && mlayer.contains(unOp.getOperator()) && !unOp.attachedToOperands()){
                        convertables.remove(i);
                        convertables.remove(i-1);
                        unOp.setLeftOperand(curr);
                        convertables.add(i-1, unOp);
                        changed = true;
                        break;
                    }
                    else if(curr instanceof CSQLBinaryOperator binOp 
                    && mlayer.contains(binOp.getOperator()) && !binOp.attachedToOperands()){
                        if(convertables.size()<=i+1){
                            throw new RuntimeException("wrongly ordered condition");
                        }
                        CSQLType next = convertables.get(i+1);
                        convertables.remove(i+1);
                        convertables.remove(i);
                        convertables.remove(i-1);
                        binOp.setLeftOperand(prev);
                        binOp.setRightOperand(next);
                        convertables.add(i-1, binOp);
                        changed = true;
                        break;
                    }
                }

                // Integer sz =  convertables.size();
                // System.out.println(Arrays.toString(layer) + " sz = " + sz);
            }
        }
        if(convertables.size()>1){
            for(CSQLType t : convertables){ 
                CSQLOperator.preOrderPrint(t, 0);
            }
            throw new RuntimeException("can't parse convertables to a single root node");
        }
        if(convertables.get(0) instanceof CSQLOperator op)
            return op;
        else
        {
            throw new RuntimeException("returning of parser not operator");
        }
    }

    public static void main(String[] args){
        ConditionSQLParser cqp = new ConditionSQLParser();
        SQLParser p = new SQLParser();
        //String q1 = "select a from b where not a<=5";
        String q1 = "select a from b where not (( a<=5 or b>3) and c=(2+2*9)/2) and (a in [\"hello   world\", 2, 4.534])";
        SQLClause clause = p.parseQuery(q1).getClauses().get(2);
        CSQLOperator.preOrderPrint(cqp.parse(clause), 0);

    }
}

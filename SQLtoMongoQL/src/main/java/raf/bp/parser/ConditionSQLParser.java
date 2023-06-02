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
        for(SQLToken token : tokens){
            if(token.getWord().equals("[")){
                array = new CSQLArray();
            }
            if(token.getWord().equals("]")){
                convertables.add(array);
                array = null;
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
                entry = new CSQLSimpleDatatype(token.getWord());
            }

            if(array!=null){
                if(entry instanceof CSQLSimpleDatatype simpleData){
                    array.getEntries().add(simpleData);
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
        // simplify until they are all one, if not operator (logica but don't check) throw exception
        List<CSQLType> convertables = makeConvertables(clause);
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
                        if(convertables.size()>=i+1){
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

                Integer sz =  convertables.size();
                System.out.println(Arrays.toString(layer) + " sz = " + sz);
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
        String q1 = "select a from b where not a<=5 or b>3 and c=2";
        SQLClause clause = p.parseQuery(q1).getClauses().get(2);
        CSQLOperator.preOrderPrint(cqp.parse(clause), 0);

    }
}

package raf.bp.parser;

import java.util.*;

import raf.bp.adapter.AdapterSQLMongoQLExecutor;
import raf.bp.executor.MongoQLExecutor;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.model.convertableSQL.CSQLType;
import raf.bp.model.convertableSQL.datatypes.CSQLArray;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import raf.bp.model.convertableSQL.operator.CSQLBinaryOperator;
import raf.bp.model.convertableSQL.operator.CSQLUnaryOperator;
import raf.bp.packager.SqlPackager;

public class ConditionSQLParser {

    public int findClosingArray(List<SQLToken> tokens, int start){
        int i;
        for(i=start; !tokens.get(i).getWord().equals("]"); ++i);
        return i;
    }

    private List<SQLToken> makeTokens(SQLClause clause){
        List<SQLToken> tokens = new ArrayList<>();
        SQLExpression prevExpr = null;
        for(SQLExpression sqlExpr : clause.getSqlExpressions()){
            if(sqlExpr instanceof SQLQuery query){
                // inserts the return of a query as SQLTokens that represent the same data
                MongoQLExecutor executor = new MongoQLExecutor();
                AdapterSQLMongoQLExecutor adaptedExecutor = new AdapterSQLMongoQLExecutor(executor);
                SqlPackager packager = new SqlPackager();
                CSQLArray array = packager.pack(adaptedExecutor.execute(query));
                if(prevExpr==null || !(prevExpr instanceof SQLToken tok) || !tok.getWord().equals("in")){
                    System.out.println("SCALAR");
                    if(array.getEntries().size()==0){
                        throw new RuntimeException("scalar nested query returned 0 results");
                    }
                    String val = array.makeTokens().get(1).getWord();
                    tokens.add(new SQLToken(val));
                }
                else{
                    System.out.println("ARRAY");
                    // its an array, the word before is a token of value "in"
                    tokens.addAll(array.makeTokens());
                }
            }
            else if(sqlExpr instanceof SQLToken token){
                tokens.add(token);   
            }
            else{
                throw new RuntimeException("can't determine sqlExpr");
            }
            prevExpr = sqlExpr;
        }
        return tokens;
    }

    public List<CSQLType> makeConvertables(SQLClause clause){
        List<CSQLType> convertables = new ArrayList<>();
        // make CSQL types, watch for array
        CSQLArray array = null;
        boolean expectingComma = false;
        for(SQLToken token : makeTokens(clause)){
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
        if (clause == null) return null;
        if(!clause.getKeyword().equals("where")){
            throw new RuntimeException("parsing condition for non-where keyword");
        }
        List<CSQLType> convertables = makeConvertables(clause);
        CSQLType t = parseUtil(convertables);
        if(t instanceof CSQLOperator op)
            return op;
        else
            throw new RuntimeException("returning of parser not operator");
    }

    public CSQLType parseUtil(List<CSQLType> convertables){
        // simplify until they are all one, if not operator (logica but don't check) throw exception
        // doesnt support booleans, no reason to, can be added as 0 operand operator

        // DEAL WITH BRACKETS
        // for(CSQLType t : convertables) System.out.println(t.toSQLString());
        // System.out.println("----------------------------------------");

        int n = convertables.size();
        int level=0;
        int startBracket=-1;
        for(int i=n-1; i>=0; --i){
            if(convertables.get(i) instanceof CSQLSimpleDatatype data && Objects.equals(data.getValue(), ")")){
                if(level==0) startBracket = i;
                level++;
            }
            if(convertables.get(i) instanceof CSQLSimpleDatatype data && Objects.equals(data.getValue(), "(")){
                level--;
                if(level==0){
                    if(startBracket==-1) throw new RuntimeException("Bracket missmatch in condition");
                    // List<CSQLType> nestedQuery = convertables.subList(i+1, startBracket);
                    // sublist is destructive?

                    List<CSQLType> nestedQuery = new ArrayList<>();
                    for(int j=i+1; j<startBracket; ++j) nestedQuery.add(convertables.get(j));
                    CSQLType op = parseUtil(nestedQuery);
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
        return convertables.get(0);
    }

    public static void main(String[] args){
        ConditionSQLParser cqp = new ConditionSQLParser();
        SQLParser p = new SQLParser();
        //String q1 = "select a from b where not a<=5";
        //String q1 = "select a from b where not (( a<=5 or b>3) and c=((( (2) ))+2*9)/2) and (a in [\"hello   world\", 2, 4.534])";
        String q1 = "select a from b where salary>(10000/((4+2)-1)) and salary<1000000000";
        SQLClause clause = p.parseQuery(q1).getClauses().get(2);
        CSQLOperator.preOrderPrint(cqp.parse(clause), 0);
    }
}

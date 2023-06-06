package raf.bp.model.convertableSQL;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import raf.bp.model.convertableSQL.operator.CSQLBinaryOperator;
import raf.bp.model.convertableSQL.operator.CSQLUnaryOperator;

@Getter
@Setter
abstract public class CSQLOperator extends CSQLType {
    private String operator;
    private CSQLType leftOperand, rightOperand;

    public static List<String[]> priority = new ArrayList<>();
    public static List<String> operators = new ArrayList<>();
    static {
        priority.add(new String[]{"~"});
        priority.add(new String[]{"*", "/", "%"});
        priority.add(new String[]{"+", "-", "&", "^", "|"}); // bit operations
        priority.add(new String[]{"<", ">", "<=", ">=", "!=", "="});
        priority.add(new String[]{"in"});  // not same orcale and mssql
        priority.add(new String[]{"not"});
        priority.add(new String[]{"and"});
        priority.add(new String[]{"all", "any", "between", "like", "or", "some"});  // most unsupported

        operators.addAll(CSQLUnaryOperator.operators);
        operators.addAll(CSQLBinaryOperator.operators);
    }

    public static List<String> arithemticOp = new ArrayList<>(
        Arrays.asList("*", "/", "%", "+", "-", "&", "^", "|"));

    public static List<String> numberComparison = new ArrayList<>(
        Arrays.asList("<", ">", "<=", ">=", "!=", "="));

    public static List<String> binOpLogical = new ArrayList<>(
        Arrays.asList("and", "or"));
    
    public static List<String> unOpLogical = new ArrayList<>(
        Arrays.asList("not"));

    abstract public boolean attachedToOperands();
    // operations and operands should make a binary tree
    public void setLeftOperand(CSQLType csqltype){
        if(leftOperand!=null) throw new RuntimeException("Bad operator order");
        leftOperand = csqltype;
    }
    public void setRightOperand(CSQLType csqltype){
        if(rightOperand!=null) throw new RuntimeException("Bad operator order");
        rightOperand = csqltype;
    }

    @Override
    public String toString(){
        String p = getType() +  " " +  operator + "(" + leftOperand.getType() 
        + ", " + rightOperand.getType() + " " + rightOperand.toString() + ")";
        return p;
    }

    @Override
    public String toSQLString(){
        return "[OP] " + operator;
    }

    public static void preOrderPrint(CSQLType type, int depth){
        if(type==null) return;
        for(int i=0; i<depth; ++i)
            System.out.print("\t");
        System.out.println(type.toSQLString());
        if(type instanceof CSQLOperator operator){
            preOrderPrint(operator.getLeftOperand(), depth+1);
            preOrderPrint(operator.getRightOperand(), depth+1);
        }
    }
}

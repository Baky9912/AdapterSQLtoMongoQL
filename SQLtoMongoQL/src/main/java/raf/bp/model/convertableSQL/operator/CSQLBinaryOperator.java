package raf.bp.model.convertableSQL.operator;

import java.util.ArrayList;
import java.util.List;

import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.model.convertableSQL.CSQLType;

public class CSQLBinaryOperator extends CSQLOperator{

    public static List<String> operators = new ArrayList<>(
        List.of(new String[]{"*", "/", "%", "+", "-", "&", "^",
        "|", "<", ">", "<=", ">=", "!=", "=", "and", "or", " between", "like"})
    );

    public CSQLBinaryOperator(String op, CSQLType operand1, CSQLType operand2){
        setOperator(op);
        setLeftOperand(operand1);
        setRightOperand(operand2);
    }

    @Override
    public boolean attachedToOperands() {
        return getLeftOperand()!=null && getRightOperand()!=null;
    }

    // @Override
    // public String toSQLString() {
    //     // TODO Auto-generated method stub
    //     throw new UnsupportedOperationException("Unimplemented method 'toSQLString'");
    // }

    @Override
    public String convertToMongo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToMongo'");
    }

}

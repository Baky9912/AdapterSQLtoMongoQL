package raf.bp.model.convertableSQL.operator;

import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.model.convertableSQL.CSQLType;

public class CSQLBinaryOperator extends CSQLOperator{

    public CSQLBinaryOperator(String op, CSQLType operand1, CSQLType operand2){
        setOperator(op);
        setLeftOperand(operand1);
        setRightOperand(operand2);
    }

    @Override
    public String toSQLString() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toSQLString'");
    }

    @Override
    public String convertToMongo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToMongo'");
    }
}

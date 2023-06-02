package raf.bp.model.convertableSQL.operator;

import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.model.convertableSQL.CSQLType;

public class CSQLUnaryOperator extends CSQLOperator {

    public CSQLUnaryOperator(String op, CSQLType operand){
        setOperator(op);
        setLeftOperand(operand);
        setRightOperand(null);
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

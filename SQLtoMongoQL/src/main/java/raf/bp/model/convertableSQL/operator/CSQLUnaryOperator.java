package raf.bp.model.convertableSQL.operator;

import java.util.ArrayList;
import java.util.List;

import raf.bp.model.convertableSQL.CSQLOperator;
import raf.bp.model.convertableSQL.CSQLType;

public class CSQLUnaryOperator extends CSQLOperator {
    public static List<String> operators = new ArrayList<>(
        List.of(new String[]{"~", "not", "all", "any", "some"})
    );

    public CSQLUnaryOperator(String op, CSQLType operand){
        setOperator(op);
        setLeftOperand(operand);
        setRightOperand(null);
    }
    
    @Override
    public boolean attachedToOperands(){
        return getLeftOperand()!=null;
    }
}

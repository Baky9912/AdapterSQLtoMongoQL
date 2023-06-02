package raf.bp.model.convertableSQL;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
abstract public class CSQLOperator extends CSQLType {
    private String operator;
    private CSQLType leftOperand, rightOperand;
    // operations and operands should make a binary tree
}

package raf.bp.model.convertableSQL;

abstract public class CSQLType implements IConvertableSQL {
    public String getType(){
        return "[" + this.getClass().getSimpleName() + "]";
    }
}

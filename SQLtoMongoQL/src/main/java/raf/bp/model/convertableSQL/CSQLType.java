package raf.bp.model.convertableSQL;

abstract public class CSQLType {
    abstract public String toSQLString();
    public String getType(){
        return "[" + this.getClass().getSimpleName() + "]";
    }
}

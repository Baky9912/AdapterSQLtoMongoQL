package raf.bp.model.convertableSQL;

public interface IConvertableSQL {
    public String toSQLString();
    public String convertToMongo();
}

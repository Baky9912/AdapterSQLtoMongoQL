package raf.bp.model.convertableSQL.datatypes;

import lombok.Getter;
import lombok.Setter;
import raf.bp.model.convertableSQL.CSQLDatatype;
import raf.bp.model.convertableSQL.from.CSQLFromTable;

@Setter
@Getter
public class CSQLSimpleDatatype extends CSQLDatatype{
    // one token converts to one Simple Datatype
    private String value;
    // subtype will be used in translation
    
    public CSQLSimpleDatatype(String value){
        this.value = value;
        setSubtype(findSubtype());
    }

    public String getTableAndField() {
        /* returns in the format tablename_fieldname */
        return getTableIfExists() + "_" + getFieldOnly();
    }

    public String getFieldOnly() {
        String[] temp = value.split("\\.");
        if (temp.length > 1) return temp[1];
        else return value;
    }

    public String getTableIfExists() {
        if (!value.contains(".")) return null;
        return value.split("\\.")[0];
    }

    /*
     * This will remove the table name or alias if it matches with argument's table
     * */
    public String stripMainTable(CSQLFromTable mainTable) {
        if (!value.contains(".")) return value;

        String[] temp = value.split("\\.");

        if (temp[0].equals(mainTable.getTableName()) || temp[0].equals(mainTable.getAlias()))
            return temp[1];
        else return value;

    }
    public boolean isSpecial(){
        return value.equals("(") || value.equals(")") || value.equals(",");
    }

    public boolean isField(){
        return !isString() && !isNumber();
    }

    public boolean isString(){
        return value.charAt(0) == '"';
    }

    public boolean isNumber(){
        try{
            Double.parseDouble(value);
            return true;
        }
        catch(NumberFormatException e){
            return false;
        }
    }

    public Subtype findSubtype(){
        if(isSpecial())
            return Subtype.SPECIAL;
        if(isNumber())
            return Subtype.NUMBER;
        if(isString())
            return Subtype.STRING;
        if(isField())
            return Subtype.FIELD;
        return null;
    }

    @Override
    public String toSQLString() {
        return "[" + getSubtype().toString() + "] " + value;
    }
}

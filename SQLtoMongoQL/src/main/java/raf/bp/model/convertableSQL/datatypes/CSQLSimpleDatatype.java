package raf.bp.model.convertableSQL.datatypes;

import java.io.ObjectInputStream.GetField;

import lombok.Getter;
import lombok.Setter;
import raf.bp.model.convertableSQL.CSQLDatatype;

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

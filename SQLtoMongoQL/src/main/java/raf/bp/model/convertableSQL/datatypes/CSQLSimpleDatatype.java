package raf.bp.model.convertableSQL.datatypes;

import lombok.Getter;
import raf.bp.model.convertableSQL.CSQLDatatype;

@Getter
public class CSQLSimpleDatatype extends CSQLDatatype{
    // one token converts to one Simple Datatype
    private String value;
    private Subtype subtype;
    // subtype will be used in translation
    
    public CSQLSimpleDatatype(String value){
        this.value = value;
        this.subtype = findSubtype();
    }

    public enum Subtype {
        STRING("String"),
        NUMBER("Number"),
        FIELD("Field");

        private Subtype(String subtype){
            this.subtype = subtype;
        }
        private String subtype;
        public String getSubtype(){
            return subtype;
        }
    };

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'toSQLString'");
    }

    @Override
    public String convertToMongo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToMongo'");
    }

}
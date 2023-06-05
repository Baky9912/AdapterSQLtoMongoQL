package raf.bp.model.convertableSQL.datatypes;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import lombok.Getter;
import raf.bp.model.SQL.SQLToken;
import raf.bp.model.convertableSQL.CSQLDatatype;

@Getter
public class CSQLArray extends CSQLDatatype {
    private List<CSQLSimpleDatatype> entries;

    public CSQLArray(){
        entries = new ArrayList<>();
        setSubtype(Subtype.ARRAY);
    }

    public CSQLArray(Collection<SQLToken> tokens){
        entries = new ArrayList<>();
        for(SQLToken token : tokens){
            CSQLSimpleDatatype datatype = new CSQLSimpleDatatype(token.getWord());
            entries.add(datatype);
        }
    }

    @Override
    public String toSQLString() {
        StringBuilder sb = new StringBuilder();
        sb.append(getType()).append(" [");
        for(CSQLSimpleDatatype sdata : entries){
            sb.append(sdata.toSQLString()).append(", ");
        }
        sb.append("]");
        return sb.toString();
    }

    public List<SQLToken> makeTokens() {
        List<SQLToken> tokens = new ArrayList<>();
        tokens.add(new SQLToken("["));
        boolean first = true;
        for(CSQLSimpleDatatype entry : entries){
            if(first) first=false;
            else tokens.add(new SQLToken(","));
            SQLToken token = new SQLToken(entry.getValue());
            tokens.add(token);
        }
        tokens.add(new SQLToken("]"));
        System.out.println("PRINTING TOKENS FROM MAKE ARRAY");
        for(SQLToken token : tokens){
            System.out.println(token.getWord());
        }
        return tokens;
    }
}
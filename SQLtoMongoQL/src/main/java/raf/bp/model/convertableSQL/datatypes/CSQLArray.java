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

    public CSQLArray(){}
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
    @Override
    public String convertToMongo() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'convertToMongo'");
    }
}

package raf.bp.model.convertableSQL.datatypes;

import lombok.Getter;
import lombok.Setter;
import raf.bp.model.convertableSQL.CSQLDatatype;

@Getter
public class CSQLAggregateFunction extends CSQLDatatype {
    private String func;
    private String arg;

    public CSQLAggregateFunction(String func, String arg){
        setSubtype(Subtype.AGGREGATE_FUNC);
        this.func = func;
        this.arg = arg;
    }

    @Override
    public String toSQLString() {
        return "[" + getSubtype().toString() + "] " + func + "(" + arg + ")";
    }

}

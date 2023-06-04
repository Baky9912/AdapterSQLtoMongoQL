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

    public String getFieldName() {
        String[] temp = arg.split("\\.");

        if (temp.length > 1) return temp[1];
        else return arg;
    }

    public String getTable() {
        String[] temp = arg.split("\\.");
        if (temp.length > 1) return temp[0];
        else return "";
    }

    @Override
    public String toSQLString() {
        return "[" + getSubtype().toString() + "] " + func + "(" + arg + ")";
    }

}

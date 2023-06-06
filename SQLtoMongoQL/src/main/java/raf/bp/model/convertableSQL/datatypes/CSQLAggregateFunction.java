package raf.bp.model.convertableSQL.datatypes;

import lombok.Getter;
import raf.bp.model.convertableSQL.CSQLDatatype;

@Getter
public class CSQLAggregateFunction extends CSQLDatatype {
    private String func;
    private String arg;
    private String sort;

    public CSQLAggregateFunction(String func, String arg){
        setSubtype(Subtype.AGGREGATE_FUNC);
        this.func = func;
        this.arg = arg;
    }

    public CSQLAggregateFunction(String func, String arg, String sort){
        setSubtype(Subtype.AGGREGATE_FUNC);
        this.func = func;
        this.arg = arg;
        this.sort = sort;
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

    public int getSort() {
        if (sort == null) return 0;
        if (sort.equals("asc")) return 1;

        return -1;
    }

    @Override
    public String toSQLString() {
        return "[" + getSubtype().toString() + "] " + func + "(" + arg + ")";
    }

    @Override
    public String toString() {
        return "[" + getSubtype().toString() + "] " + func + "(" + arg + ")";
    }

}

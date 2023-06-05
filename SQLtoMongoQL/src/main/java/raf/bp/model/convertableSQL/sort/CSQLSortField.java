package raf.bp.model.convertableSQL.sort;

import lombok.Getter;
import lombok.Setter;
import raf.bp.model.convertableSQL.CSQLType;

@Getter
@Setter
public class CSQLSortField extends CSQLType {
    private String field;
    private String order;

    public CSQLSortField(String field, String order){
        this.field = field;
        this.order = order;
    }

    public int getSortOrder() {
        if (order.equals("asc")) return 1;
        else return -1;
    }

    public String getType(){
        return "[SORTFIELD]";
    }

    @Override
    public String toSQLString() {
        return getType() + field + "(" + order + ")";
    }

    
}

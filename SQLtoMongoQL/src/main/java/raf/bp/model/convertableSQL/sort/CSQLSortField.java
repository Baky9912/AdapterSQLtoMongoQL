package raf.bp.model.convertableSQL.sort;

import lombok.Getter;
import lombok.Setter;
import raf.bp.model.convertableSQL.CSQLDatatype;
import raf.bp.model.convertableSQL.CSQLType;

@Getter
@Setter
public class CSQLSortField extends CSQLType {
    private CSQLDatatype field;  // field (simpledatatype) or aggregate func
    private String order;

    public CSQLSortField(CSQLDatatype field, String order){
        this.field = field;
        this.order = order;
    }

    public int getSortOrder() {
        if (order.equals("asc")) return 1;
        else return -1;
    }

    /*
    * This will remove the table name or alias if it matches with argument's table
    * */
    // public String stripMainTable(CSQLFromTable mainTable) {
    //     if (!field.contains(".")) return field;

    //     String[] temp = field.split("\\.");

    //     if (temp[0].equals(mainTable.getTableName()) || temp[0].equals(mainTable.getAlias()))
    //         return temp[1];
    //     else return field;

    // }

    public String getType(){
        return "[SORTFIELD]";
    }

    @Override
    public String toSQLString() {
        return getType() + field + "(" + order + ")";
    }

    
}

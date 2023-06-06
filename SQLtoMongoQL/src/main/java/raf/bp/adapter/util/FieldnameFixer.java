package raf.bp.adapter.util;

import raf.bp.model.convertableSQL.from.CSQLFromInfo;
import raf.bp.model.convertableSQL.from.CSQLFromTable;

public class FieldnameFixer {
    public static String fix(CSQLFromInfo info, String origFieldname){
        String[] parts = origFieldname.split("\\.");
        if(parts.length > 2) throw new RuntimeException("fieldname has 2+ dots");
        if(parts.length == 1) return origFieldname;
        String tableAlias = parts[0], field = parts[1];
        if(info.getMainTable().aliasRepresentsTable(tableAlias))
            return field;
        for(CSQLFromTable table : info.getJoinedTables()){
            if(table.aliasRepresentsTable(tableAlias)){
                return table.getAlias() + "." + field;
            }
        }
        throw new RuntimeException(origFieldname + ", " + tableAlias + ", isn't in any tables");
    }
}

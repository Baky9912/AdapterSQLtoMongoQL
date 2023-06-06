package raf.bp.adapter.util;

import raf.bp.model.convertableSQL.from.CSQLFromInfo;
import raf.bp.model.convertableSQL.from.CSQLFromTable;

public class FieldnameFixer {
    private static String fix(CSQLFromInfo info, String origFieldname, String delim){
        String[] parts = origFieldname.split("\\.");
        if(parts.length > 2) throw new RuntimeException("fieldname has 2+ dots");
        if(parts.length == 1) return origFieldname;
        String tableAlias = parts[0], field = parts[1];
        if(info.getMainTable().aliasRepresentsTable(tableAlias))
            return field;
        for(CSQLFromTable table : info.getJoinedTables()){
            if(table.aliasRepresentsTable(tableAlias)){
                return table.getAlias() + delim + field;
            }
        }
        throw new RuntimeException(origFieldname + ", " + tableAlias + ", isn't in any tables");
    }

    public static String fixRvalue(CSQLFromInfo info, String origFieldname){
        return fix(info, origFieldname, ".");
    }

    public static String fixLvalue(CSQLFromInfo info, String origFieldname){
        return fix(info, origFieldname, "_");
    }
}

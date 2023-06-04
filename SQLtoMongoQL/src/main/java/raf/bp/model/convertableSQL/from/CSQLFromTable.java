package raf.bp.model.convertableSQL.from;

import lombok.Getter;

@Getter
public class CSQLFromTable {
    private String tableName;
    private String alias;
    private String localField;
    private String foreignField;

    public CSQLFromTable(String tableName, String alias){
        this.tableName = tableName;
        this.alias = alias;
        this.localField = null;
        this.foreignField = null;
    }
    
    public CSQLFromTable(String tableName, String alias, String onField){
        this.tableName = tableName;
        this.alias = alias;
        this.localField = onField;
        this.foreignField = onField;
        // maybe wrong
    }

    public CSQLFromTable(String tableName, String alias, String localField, String foreignField){
        this.tableName = tableName;
        this.alias = alias;
        this.localField = localField;
        this.foreignField = foreignField;
    }

    public boolean hasAlias(){
        return alias!=null;
    }
}

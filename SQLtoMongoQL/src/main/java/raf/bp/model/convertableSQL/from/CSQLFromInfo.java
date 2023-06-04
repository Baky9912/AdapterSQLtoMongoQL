package raf.bp.model.convertableSQL.from;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import raf.bp.model.convertableSQL.CSQLType;

@Getter
@Setter
public class CSQLFromInfo {
    CSQLFromTable mainTable;
    List<CSQLFromTable> joinedTables;

    public CSQLFromInfo(){
        joinedTables = new ArrayList<>();
    }
    
    public CSQLFromInfo(CSQLFromTable mainTable){
        this();
        this.mainTable = mainTable;
    }
    public CSQLFromInfo(CSQLFromTable mainTable, List<CSQLFromTable> joinedTables){
        this(mainTable);
        this.joinedTables = joinedTables;
    }
    
    public boolean isSimple(){
        return joinedTables.size()==0;
    }
}

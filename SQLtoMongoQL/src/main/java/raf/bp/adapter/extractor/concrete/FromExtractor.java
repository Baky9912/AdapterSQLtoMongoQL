package raf.bp.adapter.extractor.concrete;

import java.util.ArrayList;
import java.util.List;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLToken;
import raf.bp.model.convertableSQL.from.CSQLFromInfo;
import raf.bp.model.convertableSQL.from.CSQLFromTable;
import raf.bp.adapter.extractor.util.ArgumentIterator;
import raf.bp.adapter.extractor.SQLExtractor;

public class FromExtractor extends SQLExtractor{
    public FromExtractor(SQLClause clause) {
        super(clause);
        assert clause.getKeyword().equals("from");
    }

    public boolean extractHasJoin(){
        ArgumentIterator argIter = new ArgumentIterator(getClause());
        while(argIter.hasNext()) {
            String word = argIter.next();
            if(word.equals("join"))
                return true;
        }
        return false;
    }

    public List<List<String>> splitPerTable(){
        List<List<String>> tableArgs = new ArrayList<>();
        List<String> currTableArgs = new ArrayList<>();
        ArgumentIterator argIter = new ArgumentIterator(getClause());
        while(argIter.hasNext()) {
            String arg = argIter.next();
            if(arg.equals("join")){
                tableArgs.add(currTableArgs);
                currTableArgs = new ArrayList<>();
            }
            else{
                currTableArgs.add(arg);
            }
        }
        tableArgs.add(currTableArgs);
        return tableArgs;
    }

    public CSQLFromTable extractMainTable() {

        String tableName, alias = null;
        ArrayList<String> wordsIndicatingMainTableIsDeclared = new ArrayList<>(List.of("outer", "cross", "inner", "left", "right", "join"));

        tableName = ((SQLToken) getClause().getSqlExpressions().get(0)).getWord();

        if (getClause().getSqlExpressions().size() < 2) return new CSQLFromTable(tableName, null);
        if (!wordsIndicatingMainTableIsDeclared.contains(((SQLToken) getClause().getSqlExpressions().get(1)).getWord())) {
            alias = (((SQLToken) getClause().getSqlExpressions().get(1)).getWord());
        }

        return new CSQLFromTable(tableName, alias);
    }

    public CSQLFromTable extractFromTable(List<String> args){
        // employees e on x=y
        // employees using x
        System.out.println("EXTRACT FROM TABLE");
        System.out.println(args);
        List<String> table = new ArrayList<>();
        List<String> condition = new ArrayList<>();
        String splitWord = null;
        boolean preCondition = true;
        for(String arg : args){
            if(arg.equals("on") || arg.equals("using")){
                preCondition = false;
                splitWord = arg;
            }
            else if(preCondition)
                table.add(arg);
            else
                condition.add(arg);
        }

        String tableName;
        String alias = null;
        String localField = null;
        String foreignField = null;

        tableName = table.get(0);
        if(table.size()==2){
            alias = table.get(1);
        }

        if(splitWord==null){}
        else if(splitWord.equals("using")){
            localField = stripAlias(condition.get(1));
            foreignField = stripAlias(condition.get(1));
        }
        else{
            localField = stripAlias(condition.get(0));
            foreignField = stripAlias(condition.get(2));
        }
        if (alias == null) alias = tableName;
        return new CSQLFromTable(tableName, alias, localField, foreignField);
    }

    public CSQLFromInfo extractFromInfo(){
        List<List<String>> tableArgs = splitPerTable();
        CSQLFromInfo csqlFromInfo = new CSQLFromInfo();
        CSQLFromTable mainTable = extractMainTable();
        csqlFromInfo.setMainTable(mainTable);
        boolean first = true;
        for (List<String> tableArg : tableArgs) {
            if(first)
                first=false;
            else{
                CSQLFromTable joinedTable = extractFromTable(tableArg);
                csqlFromInfo.getJoinedTables().add(joinedTable);
            }
        }
        System.out.println("EXTRACT FROM INFO RETURNING");
        System.out.println(mainTable);
        for(CSQLFromTable table : csqlFromInfo.getJoinedTables())
            System.out.println(table);
        return csqlFromInfo;
    }

    private static String stripAlias(String fieldname){
        String[] parts = fieldname.split("\\.");
        int n = parts.length;
        return parts[n-1];
    }

}

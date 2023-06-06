package raf.bp.packager;

import java.util.Iterator;
import java.util.List;

import raf.bp.model.TableRow;
import raf.bp.model.convertableSQL.datatypes.CSQLArray;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;

public class SqlPackager implements Packager<CSQLArray> {
    public CSQLArray pack(List<TableRow> rows){
        // for subqueries, guaranteed to return only one column
        CSQLArray array = new CSQLArray();
        for(TableRow row : rows){
            Iterator<Object> iter = row.getFields().values().iterator();
            Object obj = iter.next();
            String strRow = null;
            if(obj instanceof Integer i)
                strRow = i.toString();
            else if(obj instanceof Double d)
                strRow = d.toString();
            else if(obj instanceof Float f)
                strRow = f.toString();
            else if(obj instanceof String s){
                strRow = "\"" + s + "\"";
            }
            if(strRow==null)
                throw new RuntimeException("row type undetermined");
            array.getEntries().add(new CSQLSimpleDatatype(strRow));
        }
        return array;
    }
}

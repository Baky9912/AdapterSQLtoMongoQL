package raf.bp.adapter.maker.concrete;

import com.mongodb.client.model.Aggregates;
import org.bson.Document;
import org.bson.conversions.Bson;

import raf.bp.adapter.maker.MongoQLMaker;
import raf.bp.adapter.maker.util.TranslateAggregate;
import raf.bp.adapter.maker.util.FieldnameFixer;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.SQL.SQLToken;
import raf.bp.model.convertableSQL.datatypes.CSQLAggregateFunction;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import raf.bp.model.convertableSQL.from.CSQLFromInfo;
import raf.bp.model.convertableSQL.from.CSQLFromTable;
import raf.bp.adapter.extractor.concrete.FromExtractor;
import raf.bp.adapter.extractor.concrete.SelectExtractor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectMaker extends MongoQLMaker {

    Map<String, String> aggMapping = new HashMap<>() {{
        put("count", "size");
        put("max", "max");
        put("min", "min");
        put("avg", "avg");
        put("sum", "sum");
    }};

    @Override
    public Bson make(SQLQuery query) {
        Bson project;
        SelectExtractor selectExtractor = new SelectExtractor(query.getClause("select"));
        FromExtractor fromExtractor = new FromExtractor(query.getClause("from"));

        if (isSelectStar(query.getClause("select"))) return Aggregates.project(new Document("_id", 0));

        CSQLFromInfo fromInfo = fromExtractor.extractFromInfo();
        CSQLFromTable mainTable = fromInfo.getMainTable();

        List<CSQLSimpleDatatype> simpleFields = selectExtractor.extractSimpleFields();
        List<CSQLAggregateFunction> aggregateFunctions = selectExtractor.extractAggregateFunctions();

        Document d = new Document("_id", 0);
        for (CSQLSimpleDatatype field : simpleFields) {

            if (query.getClause("group_by") != null) {
                /* if group by exists, we need to handle fields differently */
                if (!d.containsKey(field.getFieldOnly()))
                    d.append(field.getFieldOnly(), "$_id." + field.getFieldOnly());
                else if (field.getTableIfExists() == null)
                    d.append(mainTable.getTableName() + "_" + field.getFieldOnly(), "$_id." + field.getFieldOnly());
                else
                    d.append(field.getTableAndField(), "$_id." + field.getFieldOnly());
                continue;

            }

            if (field.getTableIfExists() != null && !mainTable.getTableName().equals(field.getTableIfExists()) && !field.getTableIfExists().equals(mainTable.getAlias())) {
                /* this is a foreign field, table exists and isn't equal to main table name or it's alias */
                /* this will put all foreign fields in the format -> tableAlias_fieldName = $tableName.fieldName */
                d.append(FieldnameFixer.fixLvalue(fromInfo, field.getValue()), "$" + FieldnameFixer.fixRvalue(fromInfo, field.getValue()));
            }

            else if (field.getTableIfExists() != null && (mainTable.getTableName().equals(field.getTableIfExists()) || field.getTableIfExists().equals(mainTable.getAlias()) ) ) {
                /* this handles all local fields that have the table declared */
                d.append(field.getFieldOnly(), 1);
            }
            else {
                d.append(field.getValue(), 1);
            }
        }

        if(query.getClause("group_by") == null && !query.getClause("select").hasAggregation()){
            for (CSQLAggregateFunction agg : aggregateFunctions) {
                d.append(TranslateAggregate.translateAggFuncName(agg), TranslateAggregate.makeMongoAggFunc(agg));
            }
        }
        else{
            for (CSQLAggregateFunction agg : aggregateFunctions) {
                String fieldname = TranslateAggregate.translateAggFuncName(agg);
                d.append(fieldname, 1);
            }
        }
        project = Aggregates.project(d);
        return project;
    }

//    public Document appendToDocument(Document document, CSQLSimpleDatatype field,Object value) {
//        /* this function will avoid name conflicts while appending */
//        if (field.getTableIfExists() == null || field.getTableIfExists().equals(mainTable.getTableName()) || field.getTableIfExists().equals(mainTable.getAlias()))
//    }

    public boolean isSelectStar(SQLClause clause) {

        return ((SQLToken) clause.getSqlExpressions().get(0)).getWord().equals("*");

    }
    
}

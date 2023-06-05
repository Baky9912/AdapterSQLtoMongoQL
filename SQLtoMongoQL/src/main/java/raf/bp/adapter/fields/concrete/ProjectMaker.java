package raf.bp.adapter.fields.concrete;

import com.mongodb.client.model.Aggregates;
import org.bson.Document;
import org.bson.conversions.Bson;

import raf.bp.adapter.fields.MongoQLMaker;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.convertableSQL.datatypes.CSQLAggregateFunction;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import raf.bp.model.convertableSQL.from.CSQLFromInfo;
import raf.bp.model.convertableSQL.from.CSQLFromTable;
import raf.bp.sqlextractor.concrete.FromExtractor;
import raf.bp.sqlextractor.concrete.SelectExtractor;

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

        CSQLFromInfo fromInfo = fromExtractor.extractFromInfo();
        CSQLFromTable mainTable = fromInfo.getMainTable();

        List<CSQLSimpleDatatype> simpleFields = selectExtractor.extractSimpleFields();
        List<CSQLAggregateFunction> aggregateFunctions = selectExtractor.extractAggregateFunctions();

        Document d = new Document();
        for (CSQLSimpleDatatype field : simpleFields) {
            if (field.getTableIfExists() != null && mainTable.getTableName().equals(field.getTableIfExists()))
                d.append(field.getFieldOnly(), 1);
            else d.append(field.getValue(), 1);
        }

        for (CSQLAggregateFunction agg : aggregateFunctions) {
            Document temp = new Document("$" + aggMapping.get(agg.getFunc()), "$" + agg.getTable());
//            Document temp = new Document("$" + aggMapping.get(agg.getFunc()), "$e");
            d.append(agg.getFieldName() + "_" + agg.getFunc(), temp);
        }

        project = Aggregates.project(d);

        return project;
    }
    
}

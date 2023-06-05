package raf.bp.adapter.fields.concrete;

import com.mongodb.client.model.Aggregates;
import org.bson.Document;
import org.bson.conversions.Bson;
import raf.bp.adapter.fields.MongoQLMaker;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.convertableSQL.sort.CSQLSortField;
import raf.bp.sqlextractor.concrete.OrderByExtractor;

import java.util.List;

public class SortMaker extends MongoQLMaker {
    @Override
    public Bson make(SQLQuery query) {
        SQLClause clause = query.getClause("order_by");
        if (clause == null) return null;

        List<CSQLSortField> sortFields = (new OrderByExtractor(clause)).extractSortFields();

        Document sorts = new Document();
        for (CSQLSortField field : sortFields) {

            sorts.append(field.getField(), field.getSortOrder());
        }


        return Aggregates.sort(sorts);
    }
}

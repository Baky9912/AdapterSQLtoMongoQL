package raf.bp.adapter.fields.concrete;

import com.mongodb.client.model.Aggregates;
import org.bson.Document;
import org.bson.conversions.Bson;
import raf.bp.adapter.fields.MongoQLMaker;
import raf.bp.adapter.fields.util.TranslateAggregate;
import raf.bp.adapter.util.FieldnameFixer;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.convertableSQL.datatypes.CSQLAggregateFunction;
import raf.bp.model.convertableSQL.from.CSQLFromInfo;
import raf.bp.model.convertableSQL.from.CSQLFromTable;
import raf.bp.model.convertableSQL.sort.CSQLSortField;
import raf.bp.sqlextractor.concrete.FromExtractor;
import raf.bp.sqlextractor.concrete.OrderByExtractor;

import java.util.List;

public class SortMaker extends MongoQLMaker {
    @Override
    public Bson make(SQLQuery query) {
        SQLClause clause = query.getClause("order_by");
        CSQLFromInfo fromInfo = (new FromExtractor(query.getClause("from"))).extractFromInfo();
        if (clause == null) return null;

        OrderByExtractor orderByExtractor = new OrderByExtractor(clause);
        List<CSQLSortField> sortFields = orderByExtractor.extractSortFields();

        Document sorts = new Document();
        for (CSQLSortField field : sortFields) {
            sorts.append(FieldnameFixer.fixRvalue(fromInfo, field.getField()), field.getSortOrder());
        }

        for (CSQLAggregateFunction aggregateFunction : orderByExtractor.extractAggregateFunctions() ) {

            String fieldname = TranslateAggregate.translateAggFuncName(aggregateFunction);
            sorts.append(fieldname, aggregateFunction.getSort());

        }


        return Aggregates.sort(sorts);
    }
}

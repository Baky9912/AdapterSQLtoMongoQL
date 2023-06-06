package raf.bp.adapter.maker.concrete;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.model.Accumulators;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;

import raf.bp.adapter.maker.MongoQLMaker;
import raf.bp.adapter.maker.util.TranslateAggregate;
import raf.bp.adapter.maker.util.FieldnameFixer;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.convertableSQL.datatypes.CSQLAggregateFunction;
import raf.bp.model.convertableSQL.datatypes.CSQLSimpleDatatype;
import raf.bp.model.convertableSQL.from.CSQLFromInfo;
import raf.bp.model.convertableSQL.sort.CSQLSortField;
import raf.bp.adapter.extractor.concrete.FromExtractor;
import raf.bp.adapter.extractor.concrete.GroupByExtractor;
import raf.bp.adapter.extractor.concrete.OrderByExtractor;
import raf.bp.adapter.extractor.concrete.SelectExtractor;

public class GroupMaker extends MongoQLMaker{

    @Override
    public Bson make(SQLQuery query) {

        Document id = new Document();
        SQLClause fromClause = query.getClause("from");
        SQLClause groupByClause = query.getClause("group_by");
        SQLClause selectClause = query.getClause("select");
        SQLClause sortClause = query.getClause("order_by");

        CSQLFromInfo fromInfo = (new FromExtractor(fromClause).extractFromInfo());

        if(groupByClause == null && !selectClause.hasAggregation()) return null;

        if (groupByClause != null) {

            GroupByExtractor extractor = new GroupByExtractor(groupByClause);
            for(String field : extractor.extractFields()){
                id.append(field, "$" + field);
            }

        }

        List<BsonField> fieldAccumulators = new ArrayList<>();
        SelectExtractor selectExtractor = new SelectExtractor(selectClause);
        if (selectClause != null) {
            List<CSQLSimpleDatatype> fields = selectExtractor.extractSimpleFields();
            for (CSQLSimpleDatatype field : fields) {
                BsonField first = Accumulators.first(FieldnameFixer.fixLvalue(fromInfo, field.getValue()), "$" + FieldnameFixer.fixLvalue(fromInfo, field.getValue()));
                fieldAccumulators.add(first);
            }
        }
        if (selectClause.hasAggregation()) {


            for(CSQLAggregateFunction aggFunc : selectExtractor.extractAggregateFunctions()){

                String fieldname = TranslateAggregate.translateAggFuncName(aggFunc);
                Bson mongoAgg = TranslateAggregate.makeGroupAggFunc(aggFunc);
                BsonField bsonField = new BsonField(fieldname, mongoAgg);
                fieldAccumulators.add(bsonField);
            }

        }

        if (sortClause != null && sortClause.hasAggregation()) {

            OrderByExtractor orderByExtractor = new OrderByExtractor(sortClause);

            for (CSQLSortField sortField : orderByExtractor.extractFieldsInOrder()) {

                if (!(sortField.getField() instanceof CSQLAggregateFunction)) continue;
                CSQLAggregateFunction aggFunc = (CSQLAggregateFunction) sortField.getField();

                if (aggFunc.getArg() == null || aggFunc.getFunc() == null) continue;

                String fieldname = TranslateAggregate.translateAggFuncName(aggFunc);
                Bson mongoAgg = TranslateAggregate.makeGroupAggFunc(aggFunc);
                BsonField bsonField = new BsonField(fieldname, mongoAgg);
                fieldAccumulators.add(bsonField);

            }

        }

        return Aggregates.group(id, fieldAccumulators);
    }

}

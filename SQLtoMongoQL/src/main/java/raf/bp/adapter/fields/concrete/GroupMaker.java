package raf.bp.adapter.fields.concrete;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.client.model.Accumulators;
import org.bson.BsonDocument;
import org.bson.BsonString;
import org.bson.Document;
import org.bson.conversions.Bson;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.BsonField;

import raf.bp.adapter.fields.MongoQLMaker;
import raf.bp.adapter.fields.util.TranslateAggregate;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.convertableSQL.datatypes.CSQLAggregateFunction;
import raf.bp.sqlextractor.concrete.GroupByExtractor;
import raf.bp.sqlextractor.concrete.SelectExtractor;

public class GroupMaker extends MongoQLMaker{

    @Override
    public Bson make(SQLQuery query) {

        Document id = new Document();
        SQLClause groupByClause = query.getClause("group_by");
        SQLClause selectClause = query.getClause("select");
        if(groupByClause == null && !selectClause.hasAggregation()) return null;

        if (groupByClause != null) {

            GroupByExtractor extractor = new GroupByExtractor(groupByClause);
            for(String field : extractor.extractFields()){
                id.append(field, "$" + field);
            }

        }

        List<BsonField> fieldAccumulators = new ArrayList<>();
        if (selectClause.hasAggregation()) {

            SelectExtractor selectExtractor = new SelectExtractor(selectClause);

            for(CSQLAggregateFunction aggFunc : selectExtractor.extractAggregateFunctions()){

                String fieldname = TranslateAggregate.translateAggFuncName(aggFunc);
                Bson mongoAgg = TranslateAggregate.makeGroupAggFunc(aggFunc);
                System.out.println("HERE " + fieldname);
                BsonField bsonField = new BsonField(fieldname, mongoAgg);
                fieldAccumulators.add(bsonField);

            }

        }
        return Aggregates.group(id, fieldAccumulators);
    }

    
    public static void main(String[] args) {
        GroupMaker gm = new GroupMaker();
        Bson bson = gm.make(null);
        System.out.println(bson.toString());
    }
}

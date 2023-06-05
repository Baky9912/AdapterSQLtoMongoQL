package raf.bp.adapter.fields.concrete;

import java.util.ArrayList;
import java.util.List;

import org.bson.BsonDocument;
import org.bson.BsonString;
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
        BsonDocument id = new BsonDocument();
        SQLClause clause = query.getClause("group_by");
        if(clause==null) return null;
        GroupByExtractor extractor = new GroupByExtractor(clause);
        for(String field : extractor.extractFields()){
            id.put(field,new BsonString("$"+field));
        }
        List<BsonField> fieldAccumulators = new ArrayList<>();
        SelectExtractor selectExtractor = new SelectExtractor(query.getClause("select"));
        for(CSQLAggregateFunction aggFunc : selectExtractor.extractAggregateFunctions()){
            String fieldname = TranslateAggregate.translateAggFuncName(aggFunc);
            Bson mongoAgg = TranslateAggregate.makeMongoAggFunc(aggFunc);
            BsonField bsonField = new BsonField(fieldname, mongoAgg);
            fieldAccumulators.add(bsonField);
        }
        return Aggregates.group(id, fieldAccumulators);
    }

    
    public static void main(String[] args) {
        GroupMaker gm = new GroupMaker();
        Bson bson = gm.make(null);
        System.out.println(bson.toString());
    }
}

package raf.bp.adapter.fields.concrete;

import com.mongodb.client.model.Aggregates;
import org.bson.conversions.Bson;
import raf.bp.adapter.fields.MongoQLMaker;
import raf.bp.model.SQL.SQLQuery;

public class MatchMaker extends MongoQLMaker {
    @Override
    public Bson make(SQLQuery query) {
        FindMaker findMaker = new FindMaker();

        Bson find = findMaker.make(query);

        Bson match = Aggregates.match(find);

        return match;


    }
}

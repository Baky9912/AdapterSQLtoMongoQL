package raf.bp.adapter.maker.concrete;

import com.mongodb.client.model.Aggregates;
import org.bson.conversions.Bson;
import raf.bp.adapter.maker.Maker;
import raf.bp.model.SQL.SQLQuery;

public class MatchMaker implements Maker {
    @Override
    public Bson make(SQLQuery query) {
        FindMaker findMaker = new FindMaker(query);
        Bson find = findMaker.make(query);
        if (find == null) return null;

        Bson match = Aggregates.match(find);
        return match;
    }
}

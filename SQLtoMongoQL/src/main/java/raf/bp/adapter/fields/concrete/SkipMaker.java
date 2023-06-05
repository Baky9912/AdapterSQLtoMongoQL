package raf.bp.adapter.fields.concrete;


import org.bson.Document;
import org.bson.conversions.Bson;
import raf.bp.adapter.fields.MongoQLMaker;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.sqlextractor.concrete.OffsetExtractor;
import raf.bp.sqlextractor.concrete.SkipExtractor;

public class SkipMaker extends MongoQLMaker {
    @Override
    public Bson make(SQLQuery query) {
        SQLClause clause = query.getClause("offset");
        if(clause==null)
            return null;
        OffsetExtractor offsetExtractor = new OffsetExtractor(clause);
        Integer skip = offsetExtractor.extractOffset();
        return new Document("$skip", skip);
    }
}

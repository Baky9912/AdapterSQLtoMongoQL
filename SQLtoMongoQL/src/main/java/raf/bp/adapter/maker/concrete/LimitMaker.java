package raf.bp.adapter.maker.concrete;

import org.bson.Document;
import org.bson.conversions.Bson;
import raf.bp.adapter.maker.MongoQLMaker;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.adapter.extractor.concrete.LimitExtractor;


public class LimitMaker extends MongoQLMaker {
    @Override
    public Bson make(SQLQuery query) {
        SQLClause clause = query.getClause("limit");
        if(clause==null)
            return null;
        LimitExtractor limitExtractor = new LimitExtractor(clause);
        Integer limit = limitExtractor.extractLimit();
        return new Document("$limit", limit);
    }
    
}

package raf.bp.adapter.fields.concrete;

import raf.bp.adapter.fields.MongoQLMaker;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.sqlextractor.concrete.OffsetExtractor;

public class LimitMaker extends MongoQLMaker {
    @Override
    public Integer make(SQLQuery query) {
        SQLClause clause = query.getClause("offset");
        if(clause==null)
            return 0;
        OffsetExtractor offsetExtractor = new OffsetExtractor(clause);
        return offsetExtractor.extractOffset();
    }
    
}

package raf.bp.adapter.fields;

import org.bson.conversions.Bson;
import raf.bp.model.SQL.SQLQuery;

public abstract class MongoQLMaker {
    abstract public Object make(SQLQuery query);
}

package raf.bp.adapter.fields;

import raf.bp.model.SQL.SQLQuery;

public abstract class MongoQLMaker {
    abstract public Object make(SQLQuery query);
}

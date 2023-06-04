package raf.bp.adapter.fields;

import org.bson.conversions.Bson;

import raf.bp.model.SQL.SQLQuery;

public abstract class BsonFieldMaker {
    abstract public Bson make(SQLQuery query);
    // some use recursive building, some use builders and templates, no clear abstraction
}

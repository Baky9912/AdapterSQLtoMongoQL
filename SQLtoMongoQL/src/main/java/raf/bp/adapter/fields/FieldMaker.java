package raf.bp.adapter.fields;

import raf.bp.model.SQL.SQLQuery;

public abstract class FieldMaker {
    abstract public String make(SQLQuery query);
    // some use recursive building, some use builders and templates, no clear abstraction
}

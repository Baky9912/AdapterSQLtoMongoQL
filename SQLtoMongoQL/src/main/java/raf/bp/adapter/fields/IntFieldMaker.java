package raf.bp.adapter.fields;

import raf.bp.model.SQL.SQLQuery;

abstract public class IntFieldMaker {
    abstract public int make(SQLQuery query);
    // some use recursive building, some use builders and templates, no clear abstraction
}

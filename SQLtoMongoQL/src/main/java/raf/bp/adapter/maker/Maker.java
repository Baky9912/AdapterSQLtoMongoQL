package raf.bp.adapter.maker;

import raf.bp.model.SQL.SQLQuery;

public interface Maker {
    Object make(SQLQuery query);
}

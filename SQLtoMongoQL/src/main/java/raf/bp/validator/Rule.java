package raf.bp.validator;

import raf.bp.model.SQL.SQLQuery;

public interface Rule {
    boolean check(SQLQuery query);
}

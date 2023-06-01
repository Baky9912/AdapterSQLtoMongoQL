package raf.bp.validator;

import raf.bp.model.SQL.SQLQuery;

public interface SQLValidatorRule {
    boolean check(SQLQuery query);
    void performTests();
}

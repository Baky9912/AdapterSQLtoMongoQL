package raf.bp.validator;

import raf.bp.model.SQL.SQLQuery;

public interface Validator {
    boolean validate(SQLQuery query);
}

package raf.bp.validator.rules;

import raf.bp.model.SQL.SQLQuery;
import raf.bp.validator.SQLValidatorRule;

public class GroupByRule extends SQLValidatorRule {
    @Override
    public boolean check(SQLQuery query) {
        return false;
    }

    @Override
    public void setTests() {

    }

}

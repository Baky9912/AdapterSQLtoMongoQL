package raf.bp.converter;

import raf.bp.model.SQL.SQLClause;

import java.util.ArrayList;
import java.util.List;

public abstract class ClauseConverter {

    protected ArrayList<String> skipableTokens = new ArrayList<>(List.of(","));

    public abstract String convert(SQLClause clause);
}

package raf.bp.sqlextractor.concrete;

import java.util.List;

import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLExpression;
import raf.bp.model.SQL.SQLToken;
import raf.bp.sqlextractor.ArgumentIterator;
import raf.bp.sqlextractor.SQLExtractor;

public class FromExtractor extends SQLExtractor{
    public FromExtractor(SQLClause clause) {
        super(clause);
        assert clause.getKeyword().equals("from");
    }

    public boolean extractHasJoin(){
        ArgumentIterator argIter = new ArgumentIterator(getClause());
        while(argIter.hasNext()) {
            String word = argIter.next();
            if(word.equals("join"))
                return true;
        }
        return false;
    }

    public List<List<String>> splitPerTable(){
        return null;
    }
}

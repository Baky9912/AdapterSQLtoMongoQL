package raf.bp.adapter.fields.concrete;

import com.mongodb.client.model.Aggregates;
import org.bson.conversions.Bson;
import raf.bp.adapter.fields.MongoQLMaker;
import raf.bp.model.SQL.SQLClause;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.convertableSQL.from.CSQLFromInfo;
import raf.bp.model.convertableSQL.from.CSQLFromTable;
import raf.bp.sqlextractor.concrete.FromExtractor;

import java.util.ArrayList;
import java.util.List;

public class LookupUnwindMaker extends MongoQLMaker {
    @Override
    public ArrayList<Bson> make(SQLQuery query) {
        Bson lookup;
        Bson unwind;

        ArrayList<Bson> result = new ArrayList<>();

        SQLClause clause = query.getClause("from");
        FromExtractor extractor = new FromExtractor(clause);

        if (!extractor.extractHasJoin()) return null;

        CSQLFromInfo fromTable = extractor.extractFromInfo();

        int currentJoinedTableIndex = 0;
        List<CSQLFromTable> joinedTables = fromTable.getJoinedTables();
        for (CSQLFromTable table : fromTable.getJoinedTables()) {
            currentJoinedTableIndex++;

            if (currentJoinedTableIndex > 1) {
                String fullLocalFieldName = joinedTables.get(currentJoinedTableIndex - 2).getAlias() + "." + table.getLocalFieldName();
                lookup = Aggregates.lookup(table.getTableName(), fullLocalFieldName, table.getForeignFieldName(), table.getAlias());
            }
            else
                lookup = Aggregates.lookup(table.getTableName(), table.getLocalFieldName(), table.getForeignFieldName(), table.getAlias());
            unwind = Aggregates.unwind("$" + table.getAlias());
            result.add(lookup);
            result.add(unwind);

        }

        return result;
    }
}

package raf.bp.adapter.fields.concrete;

import com.mongodb.client.model.Aggregates;
import org.bson.conversions.Bson;
import raf.bp.adapter.fields.MongoQLMaker;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.convertableSQL.from.CSQLFromInfo;
import raf.bp.model.convertableSQL.from.CSQLFromTable;
import raf.bp.sqlextractor.concrete.FromExtractor;

import java.util.ArrayList;

public class LookupUnwindMaker extends MongoQLMaker {
    @Override
    public ArrayList<Bson> make(SQLQuery query) {
        Bson lookup = null;
        Bson unwind = null;

        ArrayList<Bson> result = new ArrayList<>();

        FromExtractor extractor = new FromExtractor(query.getClause("from"));
        CSQLFromInfo fromTable = extractor.extractFromInfo();

        for (CSQLFromTable table : fromTable.getJoinedTables()) {
            lookup = Aggregates.lookup(table.getTableName(), table.getLocalFieldName(), table.getForeignFieldName(), table.getAlias());
//            lookup = Aggregates.lookup(table.getTableName(), table.getLocalFieldName(), table.getForeignFieldName(), "e");
//            unwind = Aggregates.unwind("$e");
//            unwind = Aggregates.unwind("$departments");
            unwind = Aggregates.unwind("$" + table.getAlias());
            result.add(lookup);
            result.add(unwind);
        }

        return result;
    }
}

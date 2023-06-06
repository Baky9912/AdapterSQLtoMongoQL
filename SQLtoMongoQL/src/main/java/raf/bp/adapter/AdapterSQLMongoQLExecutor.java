package raf.bp.adapter;

import java.util.ArrayList;
import java.util.List;

import raf.bp.app.AppCore;
import raf.bp.executor.concrete.MongoQLExecutor;
import raf.bp.executor.concrete.SQLExecutor;
import raf.bp.model.MongoQL;
import raf.bp.model.TableRow;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.parser.SQLParser;
import raf.bp.validator.SQLValidator;

public class AdapterSQLMongoQLExecutor extends SQLExecutor{
    private MongoQLExecutor mongoExecutor;

    public AdapterSQLMongoQLExecutor(MongoQLExecutor mongoExecutor){
        this.mongoExecutor = mongoExecutor;
    }

    /*
    @Override
    public List<TableRow> execute(String query) {
        SQLParser parser = new SQLParser();
        SQLValidator validator = new SQLValidator();
        SQLQuery sqlQuery = parser.parse(query);
        validator.validate(sqlQuery);
        AppCore.getInstance().getMessageHandler().displayOK("query je validan!");
        return execute(sqlQuery);
    }
    */
    // Nisam bio siguran da li je pravilno sa String ili SQLQuery pa sam napravio oba

    @Override
    public List<TableRow> execute(SQLQuery query) {
        MongoQL mongoQL = new MongoQL(query);
        return mongoExecutor.execute(mongoQL);
    }
    
}

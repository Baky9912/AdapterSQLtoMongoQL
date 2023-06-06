package raf.bp.adapter;

import java.util.List;

import raf.bp.executor.Executor;
import raf.bp.executor.concrete.SQLExecutor;
import raf.bp.model.MongoQL;
import raf.bp.model.table.TableRow;
import raf.bp.model.SQL.SQLQuery;

public class AdapterSQLMongoQLExecutor extends SQLExecutor {
    private Executor<MongoQL> mongoExecutor;

    public AdapterSQLMongoQLExecutor(Executor<MongoQL> mongoExecutor){
        this.mongoExecutor = mongoExecutor;
    }

    @Override
    public List<TableRow> execute(SQLQuery query) {
        MongoQL mongoQL = new MongoQL(query);
        return mongoExecutor.execute(mongoQL);
    }
    
}

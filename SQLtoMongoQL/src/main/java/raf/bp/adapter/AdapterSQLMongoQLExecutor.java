package raf.bp.adapter;

import java.util.ArrayList;

import raf.bp.executor.MongoQLExecutor;
import raf.bp.executor.SQLExecutor;
import raf.bp.model.TableRow;

public class AdapterSQLMongoQLExecutor extends SQLExecutor{
    private MongoQLExecutor mongoExecutor;

    public AdapterSQLMongoQLExecutor(MongoQLExecutor mongoExecutor){
        this.mongoExecutor = mongoExecutor;
    }

    @Override
    public ArrayList<TableRow> execute(String query) {
        // TODO Auto-generated method stub

        // return mongoExecutor.execute...(...)
        throw new UnsupportedOperationException("Unimplemented method 'execute'");
    }
    
}

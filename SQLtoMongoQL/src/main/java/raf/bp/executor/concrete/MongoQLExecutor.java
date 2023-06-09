package raf.bp.executor.concrete;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import raf.bp.controller.MongoDBController;
import raf.bp.executor.Executor;
import raf.bp.model.MongoQL;
import raf.bp.model.table.TableRow;

import java.util.ArrayList;
import java.util.List;


public class MongoQLExecutor implements Executor<MongoQL>{

    @Override
    public List<TableRow> execute(MongoQL mongoQL) {
        MongoClient mongoClient = MongoDBController.getConnection();

        MongoDatabase database = mongoClient.getDatabase("bp_tim51");

        MongoCollection<Document> mongoCollection = database.getCollection(mongoQL.getMainTable().getTableName());

        AggregateIterable<Document> result;

        /* delete these lines when everything is done, left for debugging purposes */
        for (Bson b : mongoQL.getAggregate()) {
            System.out.println("EXECUTOR!!!!");
            System.out.println(b.toString());
        }

        result = mongoCollection.aggregate(mongoQL.getAggregate());

        MongoCursor<Document> cursor = result.iterator();
        List<TableRow> rows = getRows(cursor, mongoQL.getMainTable().getTableName());
        mongoClient.close();

        return rows;
    }
    public ArrayList<TableRow> getRows(MongoCursor<Document> cursor, String table) {

        ArrayList<TableRow> rows = new ArrayList<>();
        while (cursor.hasNext()){
            TableRow tr = new TableRow();
            tr.setName(table);

            Document d = cursor.next();

            d.forEach(tr::addField);
            System.out.println(tr);
            rows.add(tr);
        }
        return rows;
    }
}

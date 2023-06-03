package raf.bp.executor;

import com.mongodb.MongoClient;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import raf.bp.app.AppCore;
import raf.bp.controller.MongoDBController;
import raf.bp.model.TableRow;

import java.util.ArrayList;
import java.util.Arrays;

public class MongoQLExecutor {

    /*
    * this is a proof of concept. Needs to be expanded.
    * */
    public ArrayList<TableRow> execute() {
        MongoClient mongoClient = MongoDBController.getConnection();

        MongoDatabase database = mongoClient.getDatabase("bp_tim51");
        MongoCursor<Document> cursor = database.getCollection("employees").find(Document.parse("{}")).sort(Document.parse("{employee_id: -1}")).iterator();

        ArrayList<TableRow> rows = getRows(cursor, "employees");
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
            rows.add(tr);
        }
        return rows;
    }

}

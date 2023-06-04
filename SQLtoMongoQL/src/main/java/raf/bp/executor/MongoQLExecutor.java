package raf.bp.executor;

import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.Projections;
import com.mongodb.client.model.Sorts;
import org.bson.Document;
import org.bson.conversions.Bson;
import raf.bp.controller.MongoDBController;
import raf.bp.model.TableRow;
import raf.bp.packager.TablePackager;

import java.util.ArrayList;

import static com.mongodb.client.model.Filters.eq;

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

    public ArrayList<TableRow> executeFind(String collection, String select, String sort) {
        MongoClient mongoClient = MongoDBController.getConnection();

        MongoDatabase database = mongoClient.getDatabase("bp_tim51");
        MongoCollection<Document> mongoCollection = database.getCollection(collection);

        MongoCursor<Document> cursor = mongoCollection.find().projection(Document.parse(select)).sort(Document.parse(sort)).iterator();

        ArrayList<TableRow> rows = getRows(cursor, collection);
        mongoClient.close();

        return rows;
    }

    public ArrayList<TableRow> executeFind(String collection, Bson find, Bson projection, Bson sort, int skip, int limit) {
        MongoClient mongoClient = MongoDBController.getConnection();

        MongoDatabase database = mongoClient.getDatabase("bp_tim51");

        MongoCollection<Document> mongoCollection = database.getCollection(collection);

        FindIterable<Document> result;

        if (find == null)
            result = mongoCollection.find();
        else
            result = mongoCollection.find(find);

        if (projection != null)
           result.projection(projection);
        if (sort != null)
            result.sort(sort);
        if (skip != 0)
            result.skip(skip);
        if (limit != 0)
            result.limit(limit);

        MongoCursor<Document> cursor = result.iterator();
        ArrayList<TableRow> rows = getRows(cursor, collection);
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

    public static void main(String[] args) {
        MongoQLExecutor executor = new MongoQLExecutor();

        String collection = "employees";

        Bson find = eq("salary", 24000);
        Bson projection = Projections.fields(Projections.include("first_name", "last_name", "salary"), Projections.excludeId());
        Bson sort = Sorts.descending("salary");

        ArrayList<TableRow> rows = executor.executeFind(collection, find, projection, sort, 0, 0);

        TablePackager packager = new TablePackager();
        packager.pack(rows);
    }

}

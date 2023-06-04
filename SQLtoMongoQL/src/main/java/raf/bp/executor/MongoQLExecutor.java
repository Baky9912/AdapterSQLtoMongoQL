package raf.bp.executor;

import com.mongodb.*;
import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.BsonDocument;
import org.bson.Document;
import org.bson.conversions.Bson;
import raf.bp.adapter.fields.concrete.ProjectMaker;
import raf.bp.controller.MongoDBController;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.TableRow;
import raf.bp.packager.TablePackager;
import raf.bp.parser.SQLParser;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.size;

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
    public ArrayList<TableRow> executeAggregate(String collection, Bson lookup, Bson unwind, Bson match, Bson group, Bson sort, Bson projection, int skip, int limit) {
        MongoClient mongoClient = MongoDBController.getConnection();

        MongoDatabase database = mongoClient.getDatabase("bp_tim51");

        MongoCollection<Document> mongoCollection = database.getCollection(collection);

        AggregateIterable<Document> result;
        ArrayList<Bson> aggregate = new ArrayList<>();
        Bson skipBson = new Document("$skip", skip);
        Bson limitBson = new Document("$limit", skip);

        if (lookup != null) aggregate.add(lookup);
        if (unwind != null) aggregate.add(unwind);
        if (match != null) aggregate.add(match);
        if (group != null) aggregate.add(group);
        if (sort != null) aggregate.add(sort);
        if (projection != null) aggregate.add(projection);
        if (skip != 0) aggregate.add(skipBson);
        if (limit != 0) aggregate.add(limitBson);

        for (Bson b : aggregate) {
            System.out.println(b.toString());
        }

        result = mongoCollection.aggregate(aggregate);

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

//        Bson find = eq("salary", 24000);
//        Bson projection = Projections.fields(Projections.include("first_name", "last_name", "salary"), Projections.excludeId());
//        Bson sort = Sorts.descending("salary");
//        ArrayList<TableRow> rows = executor.executeFind(collection, find, projection, sort, 0, 0);

        /*
        db.departments.aggregate([
              {
                $lookup: {
                  from: "employees",
                  localField: "department_id",
                  foreignField: "department_id",
                  as: "employees"
                }
              }, {
                  $unwind: "$department_name"
              },
              {
                $project: {
                  department_name: 1,
                  employee_count: { $size: "$employees" }
                }
              }
            ]);
         */

        Bson lookup = Aggregates.lookup("employees", "department_id", "department_id", "employees");
        Bson unwind = Aggregates.unwind("$department_name");
        String q1 = "SELECT department_name, count(employees.employee_id) from departments join employees on departments.department_id=employees.department_id group by department_name";

        SQLQuery query = (new SQLParser()).parseQuery(q1);
        Bson project = (new ProjectMaker()).make(query);

        Bson projection = new Document("$size", "$employees");
        Bson project2 = Aggregates.project(new Document("department_name", 1).append("employee_count", projection));

        System.out.println("PROJECTIONS");
        System.out.println(project);
        System.out.println(project2);

        ArrayList<TableRow> rows = executor.executeAggregate("departments", lookup, unwind, null, null, null, project, 0, 0);


        TablePackager packager = new TablePackager();
        packager.pack(rows);
    }

}

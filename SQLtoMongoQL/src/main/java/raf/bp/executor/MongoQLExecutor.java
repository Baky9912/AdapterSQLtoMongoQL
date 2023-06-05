package raf.bp.executor;

import com.mongodb.MongoClient;
import com.mongodb.client.*;
import com.mongodb.client.model.*;
import org.bson.Document;
import org.bson.conversions.Bson;
import raf.bp.adapter.fields.concrete.LookupUnwindMaker;
import raf.bp.adapter.fields.concrete.ProjectMaker;
import raf.bp.controller.MongoDBController;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.TableRow;
import raf.bp.packager.TablePackager;
import raf.bp.parser.SQLParser;

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
    public ArrayList<TableRow> executeAggregate(String collection, ArrayList<Bson> lookups, Bson match, Bson group, Bson sort, Bson projection, int skip, int limit) {
        MongoClient mongoClient = MongoDBController.getConnection();

        MongoDatabase database = mongoClient.getDatabase("bp_tim51");

        MongoCollection<Document> mongoCollection = database.getCollection(collection);

        AggregateIterable<Document> result;
        ArrayList<Bson> aggregate = new ArrayList<>();
        Bson skipBson = new Document("$skip", skip);
        Bson limitBson = new Document("$limit", skip);

        if (lookups != null) aggregate.addAll(lookups);
        if (match != null) aggregate.add(match);
        if (group != null) aggregate.add(group);
        if (sort != null) aggregate.add(sort);
        if (projection != null) aggregate.add(projection);
        if (skip != 0) aggregate.add(skipBson);
        if (limit != 0) aggregate.add(limitBson);

        for (Bson b : aggregate) {
            System.out.println("EXECUTOR!!!!");
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
            System.out.println(tr);
            rows.add(tr);
        }
        return rows;
    }

    public static void main(String[] args) {
        MongoQLExecutor executor = new MongoQLExecutor();


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

        Bson lookup2 = Aggregates.lookup("employees", "department_id", "department_id", "employees");
//        Bson unwind = Aggregates.unwind("$department_name");
//        String q1 = "SELECT department_name, count(employees.employee_id) from departments join employees on departments.department_id=employees.department_id group by department_name";
        String collection = "employees";
//        String q1 = "select employees.first_name, employees.last_name, departments.department_name from employees join departments on employees.department_id = departments.department_id";
        String q1 = "SELECT employees.first_name, employees.last_name, departments.department_name FROM employees" +
                " JOIN departments ON employees.department_id = departments.department_id";

        SQLQuery query = (new SQLParser()).parseQuery(q1);
        ArrayList<Bson> lookup = (new LookupUnwindMaker()).make(query);
        Bson project = (new ProjectMaker()).make(query);

//        Bson projection = new Document("$size", "$employees");
//        Bson project2 = Aggregates.project(new Document("department_name", 1).append("employee_count", projection));
        Bson projection = new Document("first_name", 1).append("last_name", 1).append("departments.department_name", 1);
//        Bson project2 = Aggregates.project(projection);
        Bson project3 = Document.parse("{\"$project\": {\"first_name\":1, \"last_name\":1, \"department_name\":\"$departments.department_name\"}}");

        System.out.println("PROJECTIONS");
        System.out.println(project);
        System.out.println(project3);
        System.out.println("LOOKUPS");
        System.out.println(lookup);
        System.out.println(lookup2);

        ArrayList<TableRow> rows = executor.executeAggregate("employees", lookup, null, null, null, project, 0, 0);


        TablePackager packager = new TablePackager();
        packager.pack(rows);
    }

}

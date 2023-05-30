package raf.bp.database;

import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import raf.bp.controller.MongoDBController;

import com.mongodb.*;
import org.bson.Document;

@Getter
@Setter
public class MongoDB implements Database {


    @Override
    public void run() {

        MongoClient mongoClient = MongoDBController.getConnection();

        MongoDatabase database = mongoClient.getDatabase("bp_tim51");

        MongoCursor<Document> cursor = database.getCollection("employees").find(Document.parse("{}")).sort(Document.parse("{employee_id: -1}")).iterator();

        while (cursor.hasNext()){
            Document d = cursor.next();
            System.out.println(d.toJson());
        }

        mongoClient.close();
    }
}

package raf.bp.controller;

import java.util.List;

import com.mongodb.*;

public class MongoDBController {

    private static String user = "writer";
    private static String database = "bp_tim51";
    private static String password = "BjAE7QAgnIOvOcBG";


    public static MongoClient getConnection(){

        MongoCredential credential = MongoCredential.createCredential(user, database, password.toCharArray());
        MongoClient mongoClient = new MongoClient(new ServerAddress("134.209.239.154", 27017), List.of(credential));

        System.out.println ("Mongo Database connection established");

        return mongoClient;

    }
}

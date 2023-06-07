package raf.bp.adapter;

import raf.bp.model.MongoQL;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.parser.Parser;

public class AdapterSQLMongoQLParser implements Parser<MongoQL, String> {
    // Ako treba adapter da stoji na levoj strani projekta, ovaj adapter treba da se koristi
    // Ovaj adapter adaptira String -> SQLQuery na String -> MongoQL
    // Drugi adapter adaptira execute(MongoQL) -> execute(SQLQuery) 
    // Oba mogu da se vezu / odvezu u par linija koda
    private Parser<SQLQuery, String> sqlParser;

    public AdapterSQLMongoQLParser(Parser<SQLQuery, String> sqlParser){
        this.sqlParser = sqlParser;
    }

    @Override
    public MongoQL parse(String input) {
        return new MongoQL(sqlParser.parse(input));
    }
}

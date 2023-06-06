package raf.bp.adapter.maker.concrete;

import com.mongodb.client.model.Aggregates;
import org.bson.conversions.Bson;
import raf.bp.adapter.maker.MongoQLMaker;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.parser.SQLParser;

public class MatchMaker extends MongoQLMaker {
    @Override
    public Bson make(SQLQuery query) {
        FindMaker findMaker = new FindMaker(query);

        Bson find = findMaker.make(query);

        if (find == null) return null;

        Bson match = Aggregates.match(find);

        return match;


    }

    public static void main(String[] args) {
        String q = "select first_name, last_name, salary from employees where salary > 10000 order by salary desc";
        SQLParser p = new SQLParser();
        MatchMaker mm = new MatchMaker();
        Bson bson = mm.make(p.parseQuery(q));
        System.out.println(bson.toString());
    }
}

package raf.bp.model;

import lombok.Getter;
import lombok.Setter;
import org.bson.Document;
import org.bson.conversions.Bson;
import raf.bp.adapter.maker.concrete.*;
import raf.bp.model.SQL.SQLQuery;
import raf.bp.model.convertableSQL.from.CSQLFromTable;
import raf.bp.adapter.extractor.concrete.FromExtractor;

import java.util.ArrayList;

@Getter
@Setter
public class MongoQL {
    SQLQuery sqlQuery;

    CSQLFromTable mainTable;
    ArrayList<Bson> lookup_unwind;
    Bson match;
    Bson group;
    Bson sort;
    Bson projection;
    Bson skip;
    Bson limit;

    LookupUnwindMaker lookupUnwindMaker = new LookupUnwindMaker();
    MatchMaker matchMaker = new MatchMaker();
    GroupMaker groupMaker = new GroupMaker();
    SortMaker sortMaker = new SortMaker();
    ProjectMaker projectMaker = new ProjectMaker();
    LimitMaker limitMaker = new LimitMaker();
    SkipMaker skipMaker = new SkipMaker();

    public MongoQL(ArrayList<Bson> lookup_unwind, Bson match, Bson group, Bson sort, Bson projection, Bson skip, Bson limit) {
        this.lookup_unwind = lookup_unwind;
        this.match = match;
        this.group = group;
        this.sort = sort;
        this.projection = projection;
        this.skip = skip;
        this.limit = limit;
        makeAll();
    }

    public MongoQL(ArrayList<Bson> lookup_unwind, Bson match, Bson group, Bson sort, Bson projection, int skip, int limit) {
        this.lookup_unwind = lookup_unwind;
        this.match = match;
        this.group = group;
        this.sort = sort;
        this.projection = projection;
        this.skip = new Document("$skip", skip);
        this.limit = new Document("$limit", limit);
        makeAll();
    }

    public MongoQL(SQLQuery sqlQuery) {
        this.sqlQuery = sqlQuery;
        makeAll();
    }

    public MongoQL(SQLQuery sqlQuery, int skip, int limit) {
        this.sqlQuery = sqlQuery;
        if (skip != 0)
            this.skip = new Document("$skip", skip);
        if (limit != 0)
            this.limit = new Document("$limit", limit);
        makeAll();
    }

    public ArrayList<Bson> getAggregate() {
        ArrayList<Bson> aggregate = new ArrayList<>();

        if (lookup_unwind != null) aggregate.addAll(lookup_unwind);
        if (match != null) aggregate.add(match);
        if (group != null) aggregate.add(group);
        if (sort != null) aggregate.add(sort);
        if (projection != null) aggregate.add(projection);
        if (skip != null) aggregate.add(skip);
        if (limit != null) aggregate.add(limit);

        return aggregate;
    }

    public CSQLFromTable getMainTable() {
        if (mainTable == null)
            mainTable = (new FromExtractor(sqlQuery.getClause("from")).extractFromInfo().getMainTable());
        return mainTable;
    }

    public void makeAll() {
        makeLookups();
        makeMatch();
        makeGroup();
        makeSort();
        makeProject();
        makeSkip();
        makeLimit();
    }

    public void makeLookups() {
        lookup_unwind = lookupUnwindMaker.make(sqlQuery);
    }
    public void makeMatch() {
        match = matchMaker.make(sqlQuery);
    }

    public void makeGroup() {
        group = groupMaker.make(sqlQuery);
    }
    public void makeSort() {
        sort = sortMaker.make(sqlQuery);
    }

    public void makeProject() {
        projection = projectMaker.make(sqlQuery);
    }

    public void makeLimit() {
        limit = limitMaker.make(sqlQuery);
    }
    public void makeSkip() {
        skip = skipMaker.make(sqlQuery);
    }
}

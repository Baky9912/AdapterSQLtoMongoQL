package raf.bp.controller;

public class RegexAdapter {
    // Zamenicemo strukturu i povezacemo sve lepo kasnije, samo pisem individualne stvari
    public String convert(String sqlRegex){
        String mongoRegex = "^" + sqlRegex + "$";
        mongoRegex = mongoRegex.replace("?", ".");
        mongoRegex = mongoRegex.replace("%", ".*");
        return mongoRegex;
    }
}

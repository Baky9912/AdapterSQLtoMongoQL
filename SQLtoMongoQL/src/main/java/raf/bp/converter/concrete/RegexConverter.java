package raf.bp.converter.concrete;

public class RegexConverter {
    // Zamenicemo strukturu i povezacemo sve lepo kasnije, samo pisem individualne stvari
    public String convert(String sqlRegex){
        String mongoRegex = "^" + sqlRegex + "$";
        mongoRegex = mongoRegex.replace("?", ".");
        mongoRegex = mongoRegex.replace("%", ".*");
        return mongoRegex;
    }
}

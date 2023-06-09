package raf.bp.parser.concrete;

import java.util.*;
import java.util.regex.Pattern;

import raf.bp.parser.Lexer;

public class SQLLexer implements Lexer {
    public static class BadBracketsException extends RuntimeException{
        public BadBracketsException(String errMsg){
            super(errMsg);
        }
    }

    public static class BadQuotesException extends RuntimeException{
        public BadQuotesException(String errMsg){
            super(errMsg);
        }
    }

    public static class SpacedOperatorException extends RuntimeException{
        public SpacedOperatorException(String errMsg){
            super(errMsg);
        }
    }

    Map<String, String> suffix = new HashMap<>() {{
        put("inner", "join");
        put("left", "join");
        put("right", "join");
        put("outer", "join");
        put("group", "by");
        put("order", "by");
    }};

    public boolean isString(String tokens){
        //return tokens.length==1 && tokens[0].charAt(0)=='"' && tokens[0].charAt(tokens[0].length()-1)=='"';
        return tokens.charAt(0)=='"' && tokens.charAt(tokens.length()-1)=='"';
    }

    public String[] splitForTokenizing(String query){
        List<String> strs = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        boolean inStr = false;
        for(int i=0; i<query.length(); ++i){
            char c = query.charAt(i);
            if(c=='"'){
                if(inStr){
                    sb.append(c);
                    strs.add(sb.toString());
                    sb = new StringBuilder();
                    inStr = false;
                }
                else{
                    strs.add(sb.toString());
                    sb = new StringBuilder();
                    sb.append(c);
                    inStr = true;
                }
            }
            else{
                sb.append(c);
            }
        }
        String last = sb.toString().strip();
        if(last.length()>0){
            strs.add(last);
        }
        return strs.toArray(new String[strs.size()]);
    }

    public boolean validateNoSpacedOperators(String query){
        // Can't be separated by whitespace, breaks lexing later
        // System.out.println(!Pattern.compile("<\\s+=").matcher(query).find());
        // System.out.println(!Pattern.compile(">\\s+=").matcher(query).find());
        // System.out.println(!Pattern.compile("!\\s+=").matcher(query).find());
        return !Pattern.compile("<\\s+=").matcher(query).find() && !Pattern.compile(">\\s+=").matcher(query).find()
        && !Pattern.compile("!\\s+=").matcher(query).find();
    }

    public String[] lex(String query){
        if(!validateBrackets(query)){
            throw new BadBracketsException("Brackets missmatch");
        }
        if(!validateQuotes(query)){
            throw new BadQuotesException("Quotes missmatch");
        }
        if(!validateNoSpacedOperators(query)){
            throw new SpacedOperatorException("Spaced operator, like <  =");
        }
        List<String> allTokens = new ArrayList<>();
        // saving string to not modify them later
        String[] brokenQuery = splitForTokenizing(query);
        for(String partQuery : brokenQuery){
            String[] tokens = lexUtil(partQuery);
            allTokens.addAll(Arrays.asList(tokens));
        }
        return allTokens.toArray(new String[allTokens.size()]);
    }

    public String[] lexUtil(String query){
        if(isString(query)){
            return new String[]{query};
        }
        String myquery = query;
        myquery = myquery.toLowerCase();
        String[] tokensToBeSeparated = {",", "(", ")", "*", "!", "=", ">", "<", "+", "-", "/", "[", "]", "&", "^", "|", "~"};
        // "<=",">="
        // bug <= gets spaced < = 
        for(String token : tokensToBeSeparated){
            myquery = myquery.replace(token, " " + token + " ");
        }
        myquery = myquery.replace(";", "");
        myquery = myquery.replace("\n", " ");
        myquery = myquery.replace("\r", " ");
        myquery = myquery.replace("\0", " ");
        myquery = myquery.strip();
        myquery = myquery.replaceAll("\\s+", " ");
        myquery = myquery.replace("< =", "<=");
        myquery = myquery.replace("> =", ">=");
        myquery = myquery.replace("! =", "!=");
        System.out.println(myquery);
        for(Map.Entry<String, String> entry : suffix.entrySet()){
            String old_pattern = entry.getKey() + " " + entry.getValue();
            String new_pattern = entry.getKey() + "_" + entry.getValue();
            myquery = myquery.replace(old_pattern, new_pattern);
        }
        myquery = myquery.replace("inner_join", "join");
        myquery = myquery.strip();
        String[] tokens = myquery.split(" ");
        if(tokens.length == 1 && tokens[0].equals(""))
            return new String[0];
        return tokens;
    }

    public boolean validateQuotes(String s){
        int cnt=0;
        for(int i=0; i<s.length(); ++i)
            if(s.charAt(i)=='"') cnt++;
        return cnt%2==0;
    }

    public boolean validateBrackets(String s){
        int level=0;
        for(int i=0; i<s.length(); ++i){
            if(s.charAt(i)=='(')
                level++;
            else if (s.charAt(i)==')')
                level--;
            if(level<0)
                return false;
        }
        return level==0;
    }
}

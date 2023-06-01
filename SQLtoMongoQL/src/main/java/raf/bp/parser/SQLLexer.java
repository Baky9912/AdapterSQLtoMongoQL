package raf.bp.parser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLLexer {
    public class BadBracketsException extends RuntimeException{
        public BadBracketsException(String errMsg){
            super(errMsg);
        }
    }

    public class BadQuotesException extends RuntimeException{
        public BadQuotesException(String errMsg){
            super(errMsg);
        }
    }

    Map<String, String> suffix = new HashMap<String, String>() {{
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
        String last = sb.toString();
        if(last.length()>0){
            strs.add(last);
        }
        return strs.toArray(new String[strs.size()]);
    }

    public String[] lex(String query){
        if(!validateBrackets(query)){
            throw new BadBracketsException("Brackets missmatch");
        }
        if(!validateQuotes(query)){
            throw new BadQuotesException("Quotes missmatch");
        }
        List<String> allTokens = new ArrayList<>();
        String[] brokenQuery = splitForTokenizing(query);
        for(String partQuery : brokenQuery){
            String[] tokens = lexUtil(partQuery);
            for(String token : tokens) allTokens.add(token);
        }
        return allTokens.toArray(new String[allTokens.size()]);
    }

    public String[] lexUtil(String query){
        if(!validateBrackets(query)){
            throw new BadBracketsException("Brackets missmatch");
        }
        if(!validateQuotes(query)){
            throw new BadQuotesException("Quotes missmatch");
        }
        if(isString(query)){
            return new String[]{query};
        }
        String myquery = query;
        myquery = myquery.toLowerCase();
        String[] tokensToBeSeparated = {",", "(", ")", "*", "=", ">", "<", "+", "-", "/"};
        for(String token : tokensToBeSeparated){
            myquery = myquery.replace(token, " " + token + " ");
        }
        myquery = myquery.replaceAll("\\s+", " ");
        System.out.println(myquery);
        for(Map.Entry<String, String> entry : suffix.entrySet()){
            String old_pattern = entry.getKey() + " " + entry.getValue();
            String new_pattern = entry.getKey() + "_" + entry.getValue();
            myquery = myquery.replace(old_pattern, new_pattern);
        }
        String[] tokens = myquery.split(" ");
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

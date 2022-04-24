package io.deeplay.qlab.util;


import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CmdLineArgs {
    private static final Pattern ARG_PATTERN = Pattern.compile("^--(?<name>[\\w]+)=(?<value>.+)$");
    private String history;
    
    
    private CmdLineArgs() {
    }
    
    
    public static CmdLineArgs parse(String[] args) {
        CmdLineArgs parsedArgs = new CmdLineArgs();
        
        for (String arg: args) {
            Matcher argMatcher = ARG_PATTERN.matcher(arg);
            
            if (!argMatcher.find())
                continue;
            
            if ("history".equals(argMatcher.group("name")))
                parsedArgs.setHistory(argMatcher.group("value"));
        }
        
        return parsedArgs;
    }
    
    
    public String getHistory() {
        return history;
    }
    
    
    public void setHistory(String history) {
        this.history = history;
    }
}

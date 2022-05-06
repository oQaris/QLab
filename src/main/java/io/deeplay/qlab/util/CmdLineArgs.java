package io.deeplay.qlab.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CmdLineArgs {
    private static final Pattern ARG_PATTERN = Pattern.compile("^--(?<key>[\\w]+)=(?<value>.+)$");
    private String history;
    private String filtered;

    private CmdLineArgs() {
    }

    public static CmdLineArgs parse(String[] args) {
        CmdLineArgs parsedArgs = new CmdLineArgs();

        for (String arg : args) {
            Matcher argMatcher = ARG_PATTERN.matcher(arg);

            if (!argMatcher.find())
                continue;

            String key = argMatcher.group("key");
            String value = argMatcher.group("value");

            if ("history".equals(key))
                parsedArgs.setHistory(value);
            else if ("filtered".equals(key)) {
                parsedArgs.setFiltered(value);
            }
        }
        return parsedArgs;
    }

    public String getHistory() {
        return history;
    }

    public void setHistory(String history) {
        this.history = history;
    }

    public String getFiltered() {
        return filtered;
    }

    public void setFiltered(String filtered) {
        this.filtered = filtered;
    }
}

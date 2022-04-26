package io.deeplay.qlab.parser;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import io.deeplay.qlab.parser.models.history.Round;
import io.deeplay.qlab.parser.models.input.World;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;


public class Parser {
    private static final Type TYPE = TypeToken.getParameterized(List.class, Round.class).getType();
    
    
    public static List<Round> parseRoundList(File file) throws IOException {
        try (JsonReader reader = new JsonReader(new FileReader(file))) {
            return new Gson().fromJson(reader, TYPE);
        }
    }
    
    
    public static World parseInput(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, World.class);
    }
}

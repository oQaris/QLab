package io.deeplay.qlab.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import io.deeplay.qlab.parser.models.Round;
import io.deeplay.qlab.parser.models.World;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class Parser {
    private static final Type TYPE = new TypeToken<List<Round>>() {
    }.getType();

    public static List<Round> parseListRounds(String filename) throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(filename));
        return gson.fromJson(reader, TYPE);
    }

    public static World parseWorld(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, World.class);
    }
}

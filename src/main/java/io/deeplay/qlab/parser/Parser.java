package io.deeplay.qlab.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import io.deeplay.qlab.parser.models.history.Round;
import io.deeplay.qlab.parser.models.input.World;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.Type;
import java.util.List;

public class Parser {
    private static final Type TYPE = new TypeToken<List<Round>>() {
    }.getType();

    public static List<Round> parseListRounds(File file) throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(file));
        return gson.fromJson(reader, TYPE);
    }

    public static World parseInput(String data) {
        Gson gson = new Gson();
        return gson.fromJson(data, World.class);
    }
}

package io.deeplay.qlab.parser;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import io.deeplay.qlab.parser.models.Round;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

public class Parser {

    public static List<Round> parse(String filename) throws FileNotFoundException {
        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new FileReader(filename));
        return gson.fromJson(reader, new TypeToken<List<Round>>() {
        }.getType());
    }
}

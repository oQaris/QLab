package io.deeplay.qlab.parser;


import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;
import io.deeplay.qlab.parser.models.history.Round;

import java.io.*;
import java.util.List;


public class Writer {
    public static void writeRoundList(List<Round> rounds, File file) throws IOException {
        try (JsonWriter jsonWriter = new JsonWriter(new FileWriter(file))) {
            new Gson().toJson(rounds, TypeToken.getParameterized(List.class, Round.class).getType(), jsonWriter);
        }
    }
}

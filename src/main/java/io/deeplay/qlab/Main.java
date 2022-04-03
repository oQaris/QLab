package io.deeplay.qlab;

import io.deeplay.qlab.parser.Parser;
import io.deeplay.qlab.parser.models.Round;

import java.io.FileNotFoundException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws FileNotFoundException {
        List<Round> data = Parser.parseListRounds("C:/Users/oQaris/Downloads/anonymized_data.json");
        System.out.println(data.size());
    }
}

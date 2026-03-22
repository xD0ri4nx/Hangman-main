package hangman;

import java.io.FileReader;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class JSONFileReader {

    public static JSONArray readJSONFile() {

        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(new FileReader("./src/hangman/data.json"));

            JSONObject jsonObject = (JSONObject) obj;
            JSONArray words = (JSONArray) jsonObject.get("words");

            return words;

        } catch (IOException | ParseException e) {
            System.out.println("ERROR READING FILE");
        }

        return null;
    }
}
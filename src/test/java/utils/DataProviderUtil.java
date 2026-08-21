package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class DataProviderUtil {
    public static Object[][] getJsonDataToMap(String filePath) throws IOException {
        String jsonContent = new String(
                Files.readAllBytes(Paths.get(System.getProperty("user.dir") + filePath))); //reading whole json file as String
        Gson gson = new Gson(); // Initialize Gson instance
        Type type = new TypeToken<List<HashMap<String, String>>>() {}.getType(); // Define the target Map type using TypeToken
        List<HashMap<String, String>> list = gson.fromJson(jsonContent, type); // convert all above String jsonContent into List of HashMap using Gson

        Object[][] table = new Object[list.size()][1]; //converting the above list data in multidimensional array so that can be return. Here the number of rows matches the size of a list, and every row contains exactly one column
        for (int i = 0; i < list.size(); i++) {
            table[i][0] = list.get(i);
        }
        return table;


    }



}

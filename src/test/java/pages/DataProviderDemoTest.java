package pages;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;

public class DataProviderDemoTest { // NOTE : "@DataProvider" is an Annotation in testNG and "dataProvider" is parameter inside @Test Annotation

    // ways 1 Using Array object
    @DataProvider(name = "basicData")
    public Object[][] basicData() {  // this method always returns multidimensional Object OR Array i.e. Array inside Array. here method name can be anything
        return new Object[][]{{"user!@yahoo.com", "password1"}, {"user2@gmail.com", "password2"}}; // here we have 2 different Arrays inside Array. we can have any number of Array 3, 4 etc
    }

    @Test(dataProvider = "basicData") // here basicData is name of DataProvider in above method, not the method name.
    public void testFormFill(String userName, String password) {  // pass parameter for all data
        System.out.println(userName);
        System.out.println(password);
    }

    //Ways 2 using HashMap object
    @DataProvider(name = "hashMapData")
    public Object[][] hashMapData() { // whatever set of data we need, have to prepare that many HashMap
        HashMap<String, String> user1 = new HashMap<>();   //set of data 1
        user1.put("email", "user!@yahoo.com");
        user1.put("password", "password1");

        HashMap<String, String> user2 = new HashMap<>();  //set of data 2
        user2.put("email", "user!@gmail.com");
        user2.put("password", "password2");

        return new Object[][]{{user1}, {user2}};     // return all dataset together
    }

    @Test(dataProvider = "hashMapData")
    public void testWithHashMap(HashMap<String, String> data) {     // need to pass only one HasMap Object as parameter
        System.out.println(data.get("email"));
        System.out.println(data.get("password"));
    }

    //Ways 3 using jsonfile
    //To convert JSON content into a HashMap object in Java, you can use popular libraries like Jackson or Gson. here we use Gson as we do not need to add any pom.xml dependency for Gson
    @DataProvider(name = "jsonData")
    public Object[][] jsonData() throws IOException {

        String jsonContent = new String(
                Files.readAllBytes(Paths.get(System.getProperty("user.dir") + "/src/test/resources/testData_TC1.json"))); //reading whole json file as String
        Gson gson = new Gson(); // Initialize Gson instance
        Type type = new TypeToken<List<HashMap<String, String>>>() {}.getType(); // Define the target Map type using TypeToken
        List<HashMap<String, String>> list = gson.fromJson(jsonContent, type); // convert all above String jsonContent into List of HashMap using Gson

        Object[][] table = new Object[list.size()][1]; //converting the above list data in multidimensional array so that can be return. Here the number of rows matches the size of a list, and every row contains exactly one column
        for (int i = 0; i < list.size(); i++) {
            table[i][0] = list.get(i);
        }
        return table;
    }
    /**
     * Explanation of Type type = new TypeToken<List<HashMap<String,String >>>() {}.getType();
     * TypeToken: A utility class provided by the Google Gson library designed to store and protect complex data types from being lost.
     * {}: The empty curly braces create an Anonymous Inner Class.
     * It creates a nameless subclass of TypeToken on the fly,
     * forcing the Java compiler to permanently save the <HashMap<String, Object>> type information into the compiled bytecode, hiding it from Java's type erasure.
     * getType(): The specific method inside TypeToken that extracts that saved, hidden type information out of the bytecode and returns it as a standard Java java.lang.reflect.Type object.
     */
    @Test(dataProvider = "jsonData")
    public void testWithHashMapUsingJson(HashMap<String, String> data) {     // need to pass only one HasMap Object as parameter same like testWithHashMapUsing() method, nothing will change here
        System.out.println(data.get("email"));
        System.out.println(data.get("password"));
    }


}

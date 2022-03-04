
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class jsonParser {
    
    public void json_reader(GA ga, String path) {
        JSONParser jsonParser = new JSONParser();
        try {
			Object obj = jsonParser.parse(new FileReader(path));
			// A JSON object. Key value pairs are unordered. JSONObject supports java.util.Map interface.
			JSONObject jsonObject = (JSONObject) obj;

            // Set easy objects
            ga.setInstance_name( (String) jsonObject.get("instance_name"));
            ga.setNbr_nurses((Integer) jsonObject.get("nbr_nurses"));
            ga.setCapacity_nurse((Integer) jsonObject.get("capacity_nurse"));
            ga.setBenchmark((Double) jsonObject.get("benchmark"));

            // Set dict
            Map<String, Integer> depot = (Map<String, Integer>) jsonObject.get("depot");
            ga.setDepot(depot);

            // Set nested dicts
            Map<Integer, Map<String, Integer>> patients = (Map<Integer, Map<String, Integer>>) jsonObject.get("patients");
            ga.setPatients(patients);

            Double[][] travel_times = (Double[][]) jsonObject.get("travel_times");
            ga.setTravel_times(travel_times);
 
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

}

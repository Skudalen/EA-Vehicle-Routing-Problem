
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
@SuppressWarnings("unchecked")

public class JSONReader {
    
    public void json_read(GA ga, String path) {
        JSONParser jsonParser = new JSONParser();
        try {
			JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(path));

            // Set easy objects
            ga.setInstance_name( (String) jsonObject.get("instance_name"));
            ga.setNbr_nurses((Long) jsonObject.get("nbr_nurses"));
            ga.setCapacity_nurse((Long) jsonObject.get("capacity_nurse"));
            ga.setBenchmark((Double) jsonObject.get("benchmark"));

            // Set dict
            Map<String, Long> depot = (Map<String, Long>) jsonObject.get("depot");
            ga.setDepot(depot);

            // Set nested dicts
            Map<String, Map<String, Long>> patients = (Map<String, Map<String, Long>>) jsonObject.get("patients");
            ga.setPatients(patients);

            // Set nested arrays
            JSONArray rowsArray = (JSONArray) jsonObject.get("travel_times");
            int rows = rowsArray.size();
            Double[][] travel_times = new Double[rows][rows];
            for (int i=0; i<rows; i++){
                JSONArray row = (JSONArray) rowsArray.get(i);
                for (int j=0; j<rows; j++){
                    Double item;
                    if (row.get(j) instanceof Long){
                        Long l = (Long) row.get(j);
                        item = (double) l;
                    }
                    else {
                        item = (double) row.get(j);
                    }
                    travel_times[i][j] = item;
                }
            }
            ga.setTravel_times(travel_times);
 
		} catch (Exception e) {
			e.printStackTrace();
		}
    }

    public void jsonWriteResults(List<List<Object>> eval_log) {
        //List of all generational info
        JSONArray genList = new JSONArray();

        // Add details from each generation to genList
        for (int i=0; i<eval_log.size(); i++) {
            int[][][] pop = (int[][][]) eval_log.get(i).get(0);
            double[] fitness = (double[]) eval_log.get(i).get(1);
            double[] weights = (double[]) eval_log.get(i).get(2);

            JSONObject genDetails = new JSONObject();
            genDetails.put("pop", Arrays.deepToString(pop));
            genDetails.put("fitness", Arrays.toString(fitness));
            genDetails.put("weights", Arrays.toString(weights));

            genList.add(genDetails);
        }
        // Write to file
        try (FileWriter file = new FileWriter("log.json")) {
            file.write(genList.toJSONString()); 
            file.flush();
 
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

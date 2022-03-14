import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    public static void setup() {
    }

    public static void main(String[] args) throws Exception {

        // Setting parameters
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("indiv_len", 10);
        params.put("pop_size", 10);
        params.put("p_m", 0.1);
        params.put("p_c", 0.6);
        params.put("gen_stop", 10);
        
        // Setup algorithm
        String train_path = System.getProperty("user.dir") + "/src/train/train_0.json/";
        JSONReader json_parser = new JSONReader();
        GACustomization custom_GA = new GACustomization();
        GA ga_algorithm = new GA(params, json_parser, custom_GA, train_path);
        

        // Run
        //ga_algorithm.testJsonParser();
        ga_algorithm.testMakeIndiv();
        //ga_algorithm.testInit_pop();
        //ga_algorithm.testIsValid();
        
        //Map<Integer, List<Object>> eval_log = ga_algorithm.main();
        //System.out.println(eval_log.get(10));
    }
}



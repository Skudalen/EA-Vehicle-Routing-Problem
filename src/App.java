import java.util.HashMap;
import java.util.Map;

public class App {

    public static void setup() {



    }

    public static void main(String[] args) throws Exception {
        System.out.println("Hello, World!");
        // Setup data 

        // Setting parameters
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("indiv_len", 10);
        params.put("pop_size", 10);
        params.put("p_m", 0.1);
        params.put("p_c", 0.6);
        params.put("gen_stop", 10);
        

        // Setup algorithm
        GA ga_algorithm = new GA(params);
    }
}



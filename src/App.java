import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class App {

    public static void setup() {
    }

    public static void main(String[] args) throws Exception {

        // ----- Setting parameters -----
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pop_size", 10);
        params.put("p_m", 0.1);
        params.put("p_c", 0.6);
        params.put("gen_stop", 10);
        params.put("nurse_cut", 0.2);
        params.put("worst_traveltime", 10000);
        params.put("GC_phi", 0.5);
        // --------------------------------------
        params.put("how_indiv", "RandCut");
        params.put("how_selPar", "RandCut");
        params.put("how_doCross", "RandCut");
        params.put("how_mutate", "RandCut");
        params.put("how_makeOff", "RandCut");
        params.put("how_selSurv", "RandCut");
        
        // ----- Setup algorithm -----
        String train_path = System.getProperty("user.dir") + "/src/train/train_0.json/";
        JSONReader json_parser = new JSONReader();
        GACustomization custom_GA = new GACustomization(params);
        GA ga_algorithm = new GA(params, json_parser, custom_GA, train_path);
        

        // ----- Run tests -----
        //ga_algorithm.testJsonParser();
        //ga_algorithm.testMakeIndiv_BASE();
        //ga_algorithm.testMakeIndiv_RandCut();
        //ga_algorithm.testInitPop_BASE();
        //ga_algorithm.testInitPop_RandCut();
        //ga_algorithm.testIsValid();
        
        // ----- Main ------
        //Map<Integer, List<Object>> eval_log = ga_algorithm.main();
        //System.out.println(eval_log.get(10));
    }
}



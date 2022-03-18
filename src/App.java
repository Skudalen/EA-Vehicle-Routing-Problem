import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.math3.analysis.function.Logit;

public class App {

    public static void setup() {
    }

    public static void main(String[] args) throws Exception {

        // ----- Setting parameters -----
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pop_size", 10);
        params.put("p_m", 0.5);
        params.put("p_c", 0.6);
        params.put("gen_stop", 10);
        params.put("nurse_cut", 0.2);
        params.put("worst_traveltime", 10000);
        params.put("GC_phi", 0.5);
        params.put("theta", 30);
        // --------------------------------------
        params.put("how_indiv", "RANDCUT");     // BASE, RANDCUT
        params.put("how_selPar", "BASE");       // BASE,
        params.put("how_doCross", "BASE");      // BASE,
        params.put("how_mutate", "BASE");       // BASE,
        params.put("how_makeOff", "BASE");      // BASE,
        params.put("how_selSurv", "BASE");      // BASE,
        
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
        ga_algorithm.testInitPop_RANDCUT();
        //ga_algorithm.testIsValid();
        //ga_algorithm.testGetByWeights();
        //ga_algorithm.testGetPopWeights();
        //ga_algorithm.testCross();
        //ga_algorithm.testMutate();
        //ga_algorithm.testSelSurv();
        
        /*
        Logit logit = new Logit();
        double d = logit.value(0);
        System.out.print(d);
        */

        // ----- Main ------
        //List<List<Object>> eval_log = ga_algorithm.main();
        //System.out.println(eval_log.get(10));
    }
}



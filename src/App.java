import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class App {

    public static void setup() {
    }

    public static void main(String[] args) throws Exception {

        // ----- Setting parameters -----
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("pop_size", 200);
        params.put("gen_stop", 200);
        params.put("nurse_cut", 0.5);
        params.put("GC_phi", 0.9);
        params.put("theta_base", 10);
        params.put("theta_exp", 1.0);
        params.put("p_m", 0.05);
        params.put("p_c", 1.0);
        params.put("p_steal", 0.5);
        // --------------------------------------
        params.put("how_indiv", "BASE");        // BASE, RANDCUT
        params.put("how_selPar", "BASE");       // BASE,
        params.put("how_doCross", "BASE");      // BASE,
        params.put("how_mutate", "SWAP");       // BASE, SWAP
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
        //ga_algorithm.testInitPop_RANDCUT_easy();
        //ga_algorithm.testInitPop_RANDCUT();
        //ga_algorithm.testIsValid();
        //ga_algorithm.testGetByWeights();
        //ga_algorithm.testGetPopWeights();
        //ga_algorithm.testCross();
        //ga_algorithm.testMutate();
        //ga_algorithm.testSelSurv();
        

        // ----- Main ------
        ///*
        List<List<Object>> eval_log = ga_algorithm.main();
        List<Object> last_gen = eval_log.get(10);
        int[][][] pop = (int[][][]) last_gen.get(0);
        double[] fitness = (double[]) last_gen.get(1);
        double[] feasible = (double[]) last_gen.get(3);
        System.out.println(Arrays.deepToString(pop));
        System.out.println(Arrays.toString(fitness));
        System.out.println(Arrays.toString(feasible));

        json_parser.jsonWriteResults(eval_log);
        //*/
    }
}



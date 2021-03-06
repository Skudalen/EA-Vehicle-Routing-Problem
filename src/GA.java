import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GA {

    private Map<String, Object> params;
    private int pop_size;
    private int gen_stop;
    private double theta_base;
    private double theta_exp;
    private double GC_phi;
    private double p_c;
    private double p_m;
    private double p_steal;
    // -------------------------------
    private String instance_name;
    private Long nbr_nurses;
    private Long capacity_nurse;
    private double benchmark;
    private Map<String, Long> depot;
    private Map<String, Map<String, Long>> patients;
    private Double[][] travel_times;
    private Long num_patients;
    // -------------------------------
    private GACustomization custom_GA;
    // -------------------------------
 
    
    
    // CONSTRUCTOR. Set all params and read json
    public GA (Map<String, Object> params, JSONReader json_reader, GACustomization custom, String path) {
        this.params = params;
        this.pop_size = (Integer) params.get("pop_size");
        this.gen_stop = (Integer) params.get("gen_stop");
        this.theta_base = (double) (int) params.get("theta_base");
        this.theta_exp = (double) params.get("theta_exp");
        this.GC_phi = (double) params.get("GC_phi");
        this.p_c = (double) params.get("p_c");
        this.p_m = (double) params.get("p_m");
        this.p_steal = (double) params.get("p_steal");
        // -------------------------------
        json_reader.json_read(this, path);
        this.custom_GA = custom;
        // -------------------------------
        this.num_patients = (long) this.patients.size();
    }

    public Double[][] getTravel_times() {
        return travel_times;
    }
    public void setTravel_times(Double[][] travel_times) {
        this.travel_times = travel_times;
    }
    public Map<String, Map<String, Long>> getPatients() {
        return patients;
    }
    public void setPatients(Map<String, Map<String, Long>> patients) {
        this.patients = patients;
    }
    public Map<String, Long> getDepot() {
        return depot;
    }
    public void setDepot(Map<String, Long> depot) {
        this.depot = depot;
    }
    public Double getBenchmark() {
        return benchmark;
    }
    public void setBenchmark(Double benchmark) {
        this.benchmark = benchmark;
    }
    public Long getCapacity_nurse() {
        return capacity_nurse;
    }
    public void setCapacity_nurse(Long long1) {
        this.capacity_nurse = long1;
    }
    public Long getNbr_nurses() {
        return this.nbr_nurses;
    }
    public void setNbr_nurses(Long long1) {
        this.nbr_nurses = long1;
    }
    public String getInstance_name() {
        return instance_name;
    }
    public void setInstance_name(String instance_name) {
        this.instance_name = instance_name;
    }
    public double getGCPhi() {
        return GC_phi;
    }
    public double getP_c() {
        return p_c;
    }
    public double getP_m() {
        return p_m;
    }
    public double getP_steal() {
        return p_steal;
    }
    // ------------------------- TESTING METHODS -----------------------------

    public void testJsonParser() {
        System.out.println(instance_name);
        System.out.println(nbr_nurses);
        System.out.println(capacity_nurse);
        System.out.println(benchmark);

        System.out.println("Depot:\n"+ depot.get("x_coord"));
        System.out.println(depot.get("return_time"));
        System.out.println(depot.get("y_coord"));

        System.out.println("Patient 42:\n" + this.patients.get("42").get("start_time"));
        System.out.println(patients.get("42").get("demand"));
        System.out.println(this.patients.getClass());

        System.out.println("Travel_times 1,3:\n" + travel_times[1][3]);
        System.out.println(travel_times.length);
    }
    public void testMakeIndiv_BASE() {
        int[][] indiv = this.custom_GA.makeIndiv_BASE((long) 5, 20, (long) 200, this.patients, this.depot, travel_times);
        System.out.println(Arrays.deepToString(indiv));
        //System.out.println(indiv[0][0]);
    }
    public void testMakeIndiv_RandCut() {
        int[][] indiv = this.custom_GA.makeIndiv_RANDCUT((long) 5, 20, (long) 200, this.patients, this.depot, travel_times);
        System.out.println(Arrays.deepToString(indiv));
        //System.out.println(indiv[0][0]);
    }
    public void testInitPop_BASE(){
        int[][][] pop = (int[][][]) initPop(pop_size=5, nbr_nurses=(long)25, num_patients=(long)100, capacity_nurse=(long)200, this.patients, this.depot).get(0);
        for (int[][] array : pop) {
            System.out.println(Arrays.deepToString(array));
        }
    }
    public void testInitPop_RANDCUT(){
        int[][][] pop = new int[pop_size][][];
        double[] pop_fitness = new double[pop_size];
        double[] pop_feasible = new double[pop_size];

        for (int i=0; i<pop_size; i++) {
            int[][] indiv = custom_GA.makeIndiv_RANDCUT(nbr_nurses, num_patients, capacity_nurse, patients, depot, travel_times);
            pop[i] = indiv;
            List<Object> fit_info = checkIndivValidTravel(indiv, patients, depot, travel_times);
            pop_fitness[i] = (double) fit_info.get(0);
            pop_feasible[i] = (double) fit_info.get(1);
        }
        for (int i=0; i<pop_size; i++) {
            System.out.println(Arrays.deepToString(pop[i]));
            System.out.println("SCORE: ");
            System.out.println(pop_fitness[i]);
            System.out.println(pop_feasible[i]);
        }
    }
    public void testInitPop_RANDCUT_easy(){
        int[][][] pop = {
            {
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {10, 11, 12, 13, 14, 15},
                {},
                {},
                {}
            },
            {
                {9, 8, 7, 6},
                {5, 4},
                {3, 2, 1},
                {},
                {}
            }
        };
        double[] pop_fitness = new double[pop.length];

        for (int i=0; i<pop.length; i++) {
            int[][] indiv = pop[i];
            pop_fitness[i] = (double) checkIndivValidTravel(indiv, patients, depot, travel_times).get(0);
        }
        for (int i=0; i<pop.length; i++) {
            System.out.println(Arrays.deepToString(pop[i]));
            System.out.println("SCORE: ");
            System.out.println(pop_fitness[i]);
        }
    }
    public void testIsValid(){
        int pop_size = 10;
        //int[][][] pop = init_pop(pop_size, this.nbr_nurses, this.num_patients, this.capacity_nurse, this.patients, this.depot);
        int[][][] pop = (int[][][]) initPop(pop_size, 5, 10, 200, this.patients, this.depot).get(0);
        List<Double> test = IntStream.range(0,pop_size)
                                        .mapToDouble(i -> (double) checkIndivValidTravel(pop[i], this.patients, this.depot, this.travel_times).get(0))
                                        .boxed()
                                        .collect(Collectors.toCollection(ArrayList::new));
        System.out.println(test);
        int[][][] valid_indivs = new int[pop_size][][];
        for (int i=0; i < pop.length; i++) {
            if (test.get(i) < 10000000) {
                valid_indivs[i] = pop[i];
            }
        }
        System.out.println(Arrays.deepToString(valid_indivs));
    }
    public void testGetByWeights() {
        List<Double> weights = Arrays.asList(1.0, 2.0, 5.0, 7.0);
        System.out.println(weights);
        int test = GACustomization.getByWeight(weights);
        System.out.println(test);
    }
    public void testGetPopWeights() {
        List<Object> pop_info = initPop(this.pop_size, this.nbr_nurses, this.num_patients, this.capacity_nurse, this.patients, this.depot);
        double[] weights = getPopWeights(pop_info);

        for (double e : weights) {
            System.out.println(e);
        }
        System.out.println(weights.length);
        //int test = GACustomization.getByWeight(weights);
        //System.out.println(test);
    }
    public void testCross(){
        int[][][] pop = {
            {
                {1, 2, 3, 4, 5},
                {6, 7, 8, 9},
                {},
                {}
            },
            {
                {9, 8, 7, 6},
                {5, 4},
                {3, 2, 1},
                {},
                {}
            }
        };
        for (int[][] array : pop) {
            System.out.println(Arrays.deepToString(array));
        }
        System.out.println("\n");

        pop = custom_GA.doCrossover_BASE(pop, this);
        for (int[][] array : pop) {
            System.out.println(Arrays.deepToString(array));
        }
    }
    public void testMutate(){
        int[][][] pop = {
            {
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {10, 11, 12, 13, 14},
                {},
                {}
            },
            {
                {9, 8, 7, 6},
                {5, 4},
                {3, 2, 1},
                {},
                {}
            }
        };
        for (int i=0; i<pop.length; i++) {
            System.out.println(Arrays.deepToString(pop[i]));
        }
        System.out.println("\n");

        pop = (int[][][]) custom_GA.mutate_BASE(pop, this).get(0);
        for (int[][] array : pop) {
            System.out.println(Arrays.deepToString(array));
        }
    }
    public void testSelSurv(){
        int[][][] pop = {
            {
                {1, 2, 3, 4, 5, 6, 7, 8, 9},
                {10, 11, 12, 13, 14, 15},
                {},
                {},
                {}
            },
            {
                {9, 8, 7, 6},
                {5, 4},
                {3, 2, 1},
                {},
                {}
            }
        };
        int[][][] offsprings = {
            {
                {9, 8, 7, 6, 5, 4, 3, 2, 1},
                {15, 14, 13, 12, 11},
                {10},
                {},
                {}
            },
            {
                {6, 7, 6, 5},
                {4, 5},
                {1, 2, 3},
                {},
                {},
                {}
            }
        };
        // Set up and print pop info
        double[] pop_fitness = new double[offsprings.length];
        double[] pop_feasible = new double[offsprings.length];
        for (int i=0; i<pop.length; i++) {
            System.out.println(Arrays.deepToString(pop[i]));
            pop_fitness[i] = (double) checkIndivValidTravel(pop[i], patients, depot, travel_times).get(0);
        }
        double[] pop_weights = getPopWeights(Arrays.asList(pop, pop_fitness));
        System.out.println(Arrays.toString(pop_fitness));
        System.out.println(Arrays.toString(pop_weights));

        System.out.println("\n");

        // Set up and print off info
        double[] off_fitness = new double[offsprings.length];
        double[] off_feasible = new double[offsprings.length];
        for (int i=0; i<offsprings.length; i++){
            System.out.println(Arrays.deepToString(offsprings[i]));
            off_fitness[i] = (double) checkIndivValidTravel(offsprings[i], patients, depot, travel_times).get(0);
        }
        double[] off_weights = getPopWeights(Arrays.asList(offsprings, off_fitness));
        System.out.println(Arrays.toString(off_fitness)); 
        System.out.println(Arrays.toString(off_weights));

        System.out.println("\n");

        // Calc and print new pop
        List<Object> pop_info = selectSurvivors(pop, pop_fitness, pop_weights, pop_feasible, offsprings, off_fitness, off_weights, off_feasible);
        int[][][] newPop = (int[][][]) pop_info.get(0);
        for (int i=0; i<pop.length; i++) {
            System.out.println(Arrays.deepToString(newPop[i]));
        }
        double[] newFitness = (double[]) pop_info.get(1);
        System.out.println(Arrays.toString(newFitness));
    }


    // ------------------------- MAIN METHODS -----------------------------


    // IMPORTANT: Fitness / Travel_time
    public List<Object> checkIndivValidTravel(int[][] indiv, Map<String, Map<String, Long>> patients, 
                                                Map<String, Long> depot, Double[][] travel_times) {
        double rt = (double) depot.get("return_time");
        double theta_base = this.theta_base;
        double theta_exp = this.theta_exp;
        double penalties = 0;
        double tt_total = 0.0;

        for (int[] nurse : indiv) { 
            double time = 0;
            double travel_time = 0;
            int last_patient = 0;
            for (int patient : nurse) {
                String patient_str = String.valueOf(patient);
                // add travel time
                double travel = (double) travel_times[last_patient][patient];
                time += travel;
                travel_time += travel;
                // def start time and wait to that
                double start_time = (double) patients.get(patient_str).get("start_time");
                double end_time = (double) patients.get(patient_str).get("end_time");
                double wait_time = (start_time - time);
                if (start_time > time) time += wait_time;
                // Not valid if the nurse arrives after end_time
                if (time > end_time) {
                    penalties += 1;
                }
                // Add care_time
                double care_time = (double) patients.get(patient_str).get("care_time");
                time += care_time;
                // Not valid if the nurse finishes after end_time
                if (time > end_time) penalties += 1;
                // Update last_patient
                last_patient = patient;
                // -----
                // Valid and patient travel_time is added
            }
            // Add time to get back to depot
            double getBack_time = (double) travel_times[last_patient][0];
            time += getBack_time;
            // Not valid if nurse is not back within the return_time
            if (time > rt) penalties += 1;
            // -----
            // Valid and nurse travel_time is added
            tt_total += travel_time;
        }
        // Add penalty to travel_time (tt_total + theta * penalties)
        tt_total += (theta_base * Math.pow(penalties, theta_exp));

        return Arrays.asList(tt_total, penalties);
    }

    public List<Object> initPop(int pop_size, long nbr_nurses, long num_patients, long capacity_nurse, 
                                Map<String, Map<String, Long>> patients, Map<String, Long> depot) {
        int[][][] pop = new int[pop_size][(int)nbr_nurses][(int)num_patients];
        double[] popFitness = new double[pop_size];
        double[] popFeasible = new double[pop_size];

        for (int i=0; i<pop_size; i++) {
            int[][] indiv;
            if ((String)params.get("how_indiv") == "RANDCUT") {
                indiv = custom_GA.makeIndiv_RANDCUT(nbr_nurses, num_patients, capacity_nurse, patients, depot, travel_times);
            }
            else {
                indiv = custom_GA.makeIndiv_BASE(nbr_nurses, num_patients, capacity_nurse, patients, depot, travel_times);
            }
            List<Object> fitness_info = checkIndivValidTravel(indiv, patients, depot, this.travel_times);
            popFitness[i] = (double) fitness_info.get(0);
            popFeasible[i] = (double) fitness_info.get(1);
            pop[i] = indiv;
        }
        return Arrays.asList(pop, popFitness, popFeasible);
    }

    public double[] getPopWeights(List<Object> pop_info){
        //int[][][] pop = (int[][][]) pop_info.get(0);
        List<Double> pop_fitness = Arrays.stream((double[]) pop_info.get(1)).boxed().collect(Collectors.toList());

        // Scale from low-best to high-best
        double max_traveltime = Collections.max(pop_fitness) * 1.01;
        double[] weights = pop_fitness.stream()
                                    //.map(x -> Math.pow((max_traveltime - x), 1.2) )
                                    .map(x -> (max_traveltime - x))
                                    .mapToDouble(x -> x)
                                    .toArray();
        // Min, Max normalization 
        /*
        double min_worst = Collections.min(rev_fitness);
        double max_best = Collections.max(rev_fitness);

        if ((max_best - min_worst) != 0) {
            weights = rev_fitness.stream()
                                .map(x ->  (x - min_worst)/(max_best - min_worst))
                                .mapToDouble(x -> x)
                                .toArray();
        }
        else {
            weights = rev_fitness.stream()
                                .mapToDouble(x -> 1.0)
                                .toArray();
        }
        */
        double weight_sum = Arrays.stream(weights).sum();
        weights = Arrays.stream(weights).map(x -> x / weight_sum).toArray();
        return weights;
    }

    public List<Double[]> evaluatePop(List<Object> pop) {
        
        Double[] a = new Double[this.pop_size];
        return Arrays.asList(a, a);
    }

    public Boolean doTerminate(double[] pop_fitness, int gen_count) {
        
        return gen_count > this.gen_stop;
    }

    public int[][][] selectParents(int[][][] pop, double[] pop_weights) {
        int[][][] parents;
        if (params.get("how_selPar") == "X") {
            parents = custom_GA.selectParents_BASE(pop, pop_weights);
        }
        else {
            parents = custom_GA.selectParents_BASE(pop, pop_weights);
        }
        return parents;
    }

    public int[][][] doCrossover(int[][][] parents) {
        int[][][] offsprings;
        if (params.get("how_doCross") == "X") {
            offsprings = custom_GA.doCrossover_BASE(parents, this);
        }
        else {
            offsprings = custom_GA.doCrossover_BASE(parents, this);
        }
        return offsprings;
    }

    public List<Object> mutate(int[][][] offsprings) {
        List<Object> off_info;
        if (params.get("how_mutate") == "SWAP") {
            off_info = custom_GA.mutate_SWAP(offsprings, this);
        }
        else {
            off_info = custom_GA.mutate_BASE(offsprings, this);
        }
        return off_info;
    }

    public List<Object> makeOffsprings(int[][][] parents) {
        int[][][] offsprings = doCrossover(parents);
        List<Object> off_info = mutate(offsprings);
        return off_info;
    }
    
    public List<Object> selectSurvivors(int[][][] pop, double[] pop_fitness, 
                                        double[] pop_weights, double[] pop_feasible, int[][][] offsprings, 
                                        double[] off_fitness,  double[] off_weights, double[] off_feasible) {
        List<Object> pop_info;
        if (params.get("how_selSurv") == "X") {
            pop_info = custom_GA.selectSurvivors_BASE(pop, pop_fitness, pop_weights, pop_feasible,
                                                    offsprings, off_fitness, off_weights, off_feasible, this);
        }
        else {
            pop_info = custom_GA.selectSurvivors_BASE(pop, pop_fitness, pop_weights, pop_feasible,
                                                    offsprings, off_fitness, off_weights, off_feasible, this);
        }
        return pop_info;
    }


    public List<List<Object>> main() {
        // Set the generation counter to zero
        int gen_count = 0;

         // -------INITIALIZATION-------:
        // Initialize population and return {pop, pop_fitness, pop_feasible}
        List<Object> pop_info = initPop(this.pop_size, this.nbr_nurses, this.num_patients, this.capacity_nurse, this.patients, this.depot);
        // Create var for the pop and pop_fitness
        int[][][] pop = (int[][][]) pop_info.get(0);
        double[] pop_fitness = (double[]) pop_info.get(1);
        double[] pop_feasible = (double[]) pop_info.get(2);
        // Get population weights and return {pop_weights}
        double[] pop_weights = getPopWeights(pop_info);
        // Add the first log at gen=0 -> gen_count:{pop, pop_weights, pop_fitness}
        List<List<Object>> eval_log = new ArrayList<List<Object>>();
        eval_log.add(Arrays.asList(pop, pop_fitness, pop_weights, pop_feasible));

        // -------EVOLUTION-------:
        // Terminate on condition
        while (! doTerminate(pop_fitness, gen_count) ) {
            // Select parents and return {parents}
            int[][][] parents = selectParents(pop, pop_weights);
            // Make offsprings and return {offsprings, off_fitness}
            List<Object> offsprings_info = makeOffsprings(parents);
            // Create var for the offsprings and off_fitness
            int[][][] offsprings = (int[][][]) offsprings_info.get(0);
            double[] off_fitness = (double[]) offsprings_info.get(1);
            double[] off_feasible = (double[]) offsprings_info.get(2);
            // Get offspring weights and return {off_weights}
            double[] off_weights = getPopWeights(offsprings_info);
            // Update population and return {pop, pop_fitness}
            pop_info = selectSurvivors(pop, pop_fitness, pop_weights, pop_feasible, 
                                        offsprings, off_fitness, off_weights, off_feasible);

            // -------UPDATING-------:
            // Updating var for the pop, pop_fitness, and pop_weights
            pop = (int[][][]) pop_info.get(0);
            pop_fitness = (double[]) pop_info.get(1);
            pop_feasible = (double[]) pop_info.get(2);
            pop_weights = getPopWeights(pop_info);
            // Increment generation counter
            gen_count += 1;
            // Update params
            this.theta_base += 1;
            //this.theta_exp += 0.1;
            this.GC_phi -= 0.005;
            if (GC_phi < 0.01) this.GC_phi = 0.01;
            // Store data for gen > 0
            eval_log.add(Arrays.asList(pop, pop_fitness, pop_weights, pop_feasible));
        }
        System.out.println("Algorithm succsessfully executed");
        return eval_log;


        
    }
}

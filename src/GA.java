import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public class GA {

    private Map<String, Object> params;
    private int pop_size;
    private int gen_stop;
    private Double p_c;
    private Double p_m;
    // -------------------------------
    private String instance_name;
    private Long nbr_nurses;
    private Long capacity_nurse;
    private Double benchmark;
    private Map<String, Long> depot;
    private Map<String, Map<String, Long>> patients;
    private Double[][] travel_times;
    private Long num_patients;
    private Double worst_traveltime;

    // -------------------------------
    private GACustomization custom_GA;
    // -------------------------------
    private Double[] popFitness;
    private Double[] offFitness;
    private Double[] popWeights;
    
    
    // CONSTRUCTOR. Set all params and read json
    public GA (Map<String, Object> params, JSONReader json_reader, GACustomization custom, String path) {
        this.pop_size = (Integer) params.get("pop_size");
        this.gen_stop = (Integer) params.get("gen_stop");
        this.p_c = (Double) params.get("p_c");
        this.p_m = (Double) params.get("p_m");
        this.worst_traveltime = (double) (int) params.get("worst_traveltime");
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
        int[][] indiv = this.custom_GA.makeIndiv_BASE((long) 5, 20, (long) 200, this.patients, this.depot);
        System.out.println(Arrays.deepToString(indiv));
        //System.out.println(indiv[0][0]);
    }
    public void testMakeIndiv_RandCut() {
        int[][] indiv = this.custom_GA.makeIndiv_RANDCUT((long) 5, 20, (long) 200, this.patients, this.depot);
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
        int[][][] pop = new int[pop_size][(int)25][(int)100];

        for (int i=0; i<pop_size; i++) {
            int[][] indiv = custom_GA.makeIndiv_RANDCUT(nbr_nurses, num_patients, capacity_nurse, patients, depot);
            pop[i] = indiv;
        }
        for (int[][] array : pop) {
            System.out.println(Arrays.deepToString(array));
        }
    }

    public void testIsValid(){
        int pop_size = 100;
        //int[][][] pop = init_pop(pop_size, this.nbr_nurses, this.num_patients, this.capacity_nurse, this.patients, this.depot);
        int[][][] pop = (int[][][]) initPop(pop_size, 5, 10, 200, this.patients, this.depot).get(0);
        List<Double> test = IntStream.range(0,pop_size)
                                        .mapToDouble(i -> checkIndivValidTravel(pop[i], this.patients, this.depot, this.travel_times))
                                        .boxed()
                                        .collect(Collectors.toCollection(ArrayList::new));
        System.out.println(test);
        int[][][] valid_indivs = new int[pop_size][][];
        for (int i=0; i < pop.length; i++) {
            if (test.get(i) < this.worst_traveltime) {
                valid_indivs[i] = pop[i];
            }
        }
        System.out.println(Arrays.deepToString(valid_indivs));
    }


    // ------------------------- MAIN METHODS -----------------------------


    // IMPORTANT
    public double checkIndivValidTravel(int[][] indiv, Map<String, Map<String, Long>> patients, Map<String, Long> depot, Double[][] travel_times) {
        Double rt = (double) depot.get("return_time");
        Double tt_total = 0.0;

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
                if (time > end_time) return this.worst_traveltime;
                // Add care_time
                double care_time = (double) patients.get(patient_str).get("care_time");
                time += care_time;
                // Not valid if the nurse finishes after end_time
                if (time > end_time) return this.worst_traveltime;
                // -----
                // Valid and patient travel_time is added
            }
            // Not valid if nurse is not back within the return_time
            if (time > rt) return this.worst_traveltime;
            // -----
            // Valid and nurse travel_time is added
            tt_total += travel_time;
        }
        return tt_total;
    }

    
    public List<Object> initPop(int pop_size, long nbr_nurses, long num_patients, long capacity_nurse, 
                                Map<String, Map<String, Long>> patients, Map<String, Long> depot) {
        int[][][] pop = new int[pop_size][(int)nbr_nurses][(int)num_patients];
        Double[] popFitness = new Double[pop_size];

        for (int i=0; i<pop_size; i++) {
            int[][] indiv;
            if (params.get("how_indiv") == "RANDCUT") {
                indiv = custom_GA.makeIndiv_RANDCUT(nbr_nurses, num_patients, capacity_nurse, patients, depot);
            }
            else {
                indiv = custom_GA.makeIndiv_BASE(nbr_nurses, num_patients, capacity_nurse, patients, depot);
            }
            popFitness[i] = checkIndivValidTravel(indiv, patients, depot, this.travel_times);
            pop[i] = indiv;
        }
        return Arrays.asList(pop, popFitness);
    }


    public double[] getPopWeights(List<Object> pop_info){

        int[][][] pop = (int[][][]) pop_info.get(0);
        List<Double> pop_fitness = Arrays.asList( (Double[]) pop_info.get(1) );

        // Scale from low-best to high-best
        double max_traveltime = Collections.max(pop_fitness);

        List<Double> rev_fitness = pop_fitness.stream()
                                                .map(x -> Math.pow((max_traveltime - x), 2) )
                                                .collect(Collectors.toList());
        // Normalize
        double min_worst = Collections.min(rev_fitness);
        double max_best = Collections.max(rev_fitness);
        double[] weights = new double[rev_fitness.size()];

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
        //double[] temp = weights;
        //weights = Arrays.stream(weights).map(x -> x / Arrays.stream(temp).sum()).toArray();
        return weights;
    }

    
    public List<Double[]> evaluatePop(List<Object> pop) {
        
    Double[] a = new Double[this.pop_size];
        return Arrays.asList(a, a);
    }

    
    public Boolean doTerminate(double[] pop_fitness, int gen_count) {
        
        return gen_count > this.gen_stop;
    }



    /*
    def select_parents(self, pop):
        # Stocastic
        _, _, weights = self.evaluate_pop(pop)
        #fitness_sum = sum(pop_fitness)
        #weights = np.divide(pop_fitness, fitness_sum)
        #print('\nWeights used to select parents based on normalized fitness:\n', weights)
        parents = random.choices(pop, weights=weights, k=self.num_parents)
        return parents
    */
    public int[][][] selectParents(int[][][] pop, Double[] pop_weights) {
        
    int[][][] a = custom_GA.selectParents(pop);
        return a;
    }

    /*
    def crossover(self, parents):
        offsprings = []
        for i in range(0, self.num_parents-1, 2):
            parent1 = parents[i]
            parent2 = parents[i+1]
            crosspoint = None
            for k in range(1, self.indiv_len-1):
                temp = random.choices([1, 0], weights=[self.p_c, 1 - self.p_c])
                if temp[0] == 1:
                    crosspoint = k
                    break
            if crosspoint:
                child1 = parent1[:crosspoint] + parent2[crosspoint:]
                child2 = parent2[:crosspoint] + parent1[crosspoint:]
                offsprings.extend([child1, child2])
            else:
                offsprings.extend([parent1, parent2])
        return offsprings
    */
    public int[][][] doCrossover(int[][][] offsprings) {
        
    int[][][] offsprings_mod = custom_GA.doCrossover(offsprings);
        return offsprings_mod;
    }

    /*
    def mutate(self, offsprings:list):
        offsprings_mod = []
        for indiv in offsprings:
            new_indiv = indiv
            for i in range(len(indiv)):
                temp = random.choices([1, 0], weights=[self.p_m, 1 - self.p_m])
                if temp[0] == 1:
                    if new_indiv[i] == '0': 
                        new_indiv = indiv[:i] + '1' + indiv[i+1:]  
                    else: 
                        new_indiv = indiv[:i] + '0' + indiv[i+1:]
            offsprings_mod.append(new_indiv)
        return offsprings_mod
    */
    public int[][][] mutate(int[][][] offsprings) {
        
    int[][][] offsprings_mod = custom_GA.mutate(offsprings);
        return offsprings_mod;
    }

    /*
    def make_offsprings(self, parents):
        offsprings = self.crossover(parents)
        offsprings_mod = self.mutate(offsprings)
        return offsprings_mod
    */
    public List<Object> makeOffsprings(int[][][] parents) {
        
        List<Object> offsprings = Arrays.asList(0);
        return offsprings;
    }
    
    /*
    def select_survivors(self, parents, offsprings, pop_weights, off_weights, is_high_best):
        if self.survival_selecter:
            return self.survival_selecter(parents, offsprings, pop_weights, off_weights, is_high_best)
        else:   # Default: generational survival selection 
            return offsprings
    */
    public List<Object> selectSurvivors(int[][][] pop, 
                                    int[][][] offsprings, 
                                    Double[] pop_weights, 
                                    Double[] off_weights) {
        
        List<Object> survivors = custom_GA.selectSurvivors(pop,
                                                        offsprings,
                                                        pop_weights,
                                                        off_weights);
        return survivors;
    }


    /*
    def get_pop_entropy(self, pop):
        char_count = {k:0 for k in range(self.indiv_len)}
        for indiv in pop:
            for i, char in enumerate(list(indiv)):
                if char == '1':
                    char_count[i] += 1
        #[print(key, value) for key, value in char_count.items()]
        probs = [char_count.get(i)/self.pop_size for i in char_count.keys()]
        #entropy = - sum([p * np.log2(p) for p in probs])
        entropy = entr(probs).sum()
        return entropy
    */
    public static double getPopEntropy(int[][] pop) {
        return 0.0;
    }


    public List<List<Object>> main() {
        // Set the generation counter to zero
        int gen_count = 0;

         // -------INITIALIZATION-------:
        // Initialize population and return {pop, pop_fitness}
        List<Object> pop_info = initPop(this.pop_size, this.nbr_nurses, this.num_patients, this.capacity_nurse, this.patients, this.depot);
        // Create var for the pop and pop_fitness
        int[][][] pop = (int[][][]) pop_info.get(0);
        double[] pop_fitness = (double[]) pop_info.get(1);
        // Get population weights and return {pop_weights}
        Double[] pop_weights = getPopWeights(pop_info);
        // Add the first log at gen=0 -> gen_count:{pop, pop_weights, pop_fitness}
        List<List<Object>> eval_log = Arrays.asList(Arrays.asList(pop, pop_fitness, pop_weights));

        // -------EVOLUTION-------:
        // Terminate on condition
        while (! doTerminate(pop_fitness, gen_count) ) {
            // Select parents and return {parents}
            int[][][] parents = selectParents(pop, pop_weights);
            // Make offsprings and return {offsprings, off_fitness}
            List<Object> offsprings_info = makeOffsprings(parents);
            // Create var for the offsprings and off_fitness
            int[][][] offprings = (int[][][]) offsprings_info.get(0);
        //Double[] off_fitness = (Double[]) offsprings_info.get(1);
            // Get offspring weights and return {off_weights}
            Double[] off_weights = getPopWeights(offsprings_info);
            pop_info = selectSurvivors(pop, offprings, pop_weights, off_weights);

            // -------UPDATING-------:
            // Updating var for the pop, pop_fitness, and pop_weights
            pop = (int[][][]) pop_info.get(0);
            pop_fitness = (double[]) pop_info.get(1);
            pop_weights = getPopWeights(pop_info);
            // Increment generation counter
            gen_count += 1;
            // Store data for gen > 0
            eval_log.add(Arrays.asList(pop, pop_fitness, pop_weights));
        }
        System.out.println("Algorithm succsessfully executed");
        return eval_log;


        
    }
}

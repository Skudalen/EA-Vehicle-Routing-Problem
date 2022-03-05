import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GA {

    private Map<String, Object> params;
    private int pop_size;
    private int gen_stop;
    private Double p_c;
    private Double p_m;

    private String instance_name;
    private Long nbr_nurses;
    private Long capacity_nurse;
    private Double benchmark;
    private Map<String, Integer> depot;
    private Map<Integer, Map<String, Integer>> patients;
    private Double[][] travel_times; 

    
    // Set all params and read json
    public GA (Map<String, Object> params, jsonParser jsonParser, String path) {
        this.pop_size = (Integer) params.get("pop_size");
        this.gen_stop = (Integer) params.get("gen_stop");
        this.p_c = (Double) params.get("p_c");
        this.p_m = (Double) params.get("p_m");
        
        jsonParser.json_reader(this, path);
    }


    public Double[][] getTravel_times() {
        return travel_times;
    }


    public void setTravel_times(Double[][] travel_times) {
        this.travel_times = travel_times;
    }


    public Map<Integer, Map<String, Integer>> getPatients() {
        return patients;
    }


    public void setPatients(Map<Integer, Map<String, Integer>> patients) {
        this.patients = patients;
    }


    public Map<String, Integer> getDepot() {
        return depot;
    }


    public void setDepot(Map<String, Integer> depot) {
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

    public void testJsonParser() {
        System.out.println(instance_name);
        System.out.println(nbr_nurses);
        System.out.println(capacity_nurse);
        System.out.println(benchmark);

        System.out.println("Depot:\n"+ depot.get("x_coord"));
        System.out.println(depot.get("return_time"));
        System.out.println(depot.get("y_coord"));

        System.out.println("Patient 42:\n" + patients.get("42").get("start_time"));
        System.out.println(patients.get("42").get("demand"));

        System.out.println("Travel_times 1,3:\n" + travel_times[1][3]);
        System.out.println(travel_times.length);
    }


    /*
    def init_pop(self):
        rand_ints = [random.getrandbits(self.indiv_len) for x in range(self.pop_size)]
        pop = list(map(lambda x: np.binary_repr(x, self.indiv_len), rand_ints))
        return pop
    */
    public static int[][] init_pop() {
        
        int[][] a = new int[200][200];
        return a;
    }

    /*
    def evaluate_pop(self, pop):
        x, fitness, weights = self.fitness(pop, self.params)   # returns x-values list, fitness list, weights list
        return x, fitness, weights
    */
    public static List<Double[]> evaluate_pop(int[][] pop) {
        
    Double[] a = new Double[pop.length];
        return Arrays.asList(a, a);
    }

    /*
    def do_terminate(self, pop_eval, gen_count):
        term = True if gen_count >= self.max_gen else False
        return term
    */
    public static Boolean do_terminate(Double[] pop_eval, int gen_count) {
        
        return gen_count > 5;
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
    public static int[][] select_parents(int[][] pop) {
        
    int[][] a = new int[0][0];
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
    public static int[][] crossover(int[][] offsprings) {
        
    int[][] a = new int[0][0];
        return offsprings;
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
    public static int[][] mutate(int[][] offsprings) {
        
    int[][] a = new int[0][0];
        return offsprings;
    }

    /*
    def make_offsprings(self, parents):
        offsprings = self.crossover(parents)
        offsprings_mod = self.mutate(offsprings)
        return offsprings_mod
    */
    public static int[][] make_offsprings(int[][] parents) {
        
    int[][] a = new int[0][0];
        return parents;
    }
    
    /*
    def select_survivors(self, parents, offsprings, pop_weights, off_weights, is_high_best):
        if self.survival_selecter:
            return self.survival_selecter(parents, offsprings, pop_weights, off_weights, is_high_best)
        else:   # Default: generational survival selection 
            return offsprings
    */
    public static int[][] select_survivors(int[][] parents, 
                                    int[][] offsprings, 
                                    Double[] pop_weights, 
                                    Double[] off_weights) {
        
    int[][] a = new int[0][0];
        return a;
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
    public static double get_pop_entropy(int[][] pop) {
        return 0.0;
    }



    public Map<Integer, List<Object>> main() {

        int[][] pop = init_pop();
        int gen_count = 0;
        List<Double[]> pop_eval = evaluate_pop(pop); //pop_weights, pop_fitness
        Map<Integer, List<Object>> eval_log = new HashMap<Integer, List<Object>>(); 
        eval_log.put(gen_count, Arrays.asList(pop, pop_eval.get(0), pop_eval.get(1)));

        // EVOLUTION:
        while (! do_terminate((Double[]) pop_eval.get(1), gen_count) ) {
            int[][] parents = select_parents(pop);
            int[][] offsprings = make_offsprings(parents);
            List<Double[]> off_eval = evaluate_pop(offsprings);   //off_weights, off_fitness
            pop = select_survivors(parents, offsprings, (Double[]) pop_eval.get(1), (Double[]) off_eval.get(1));
            gen_count += 1;
            // Store data, gen > 0
            pop_eval = evaluate_pop(pop);
            eval_log.put(gen_count, Arrays.asList(pop, pop_eval.get(0), pop_eval.get(1)));
        }
            
        System.out.println("Algorithm succsessfully executed");
        return eval_log;


        
    }
}

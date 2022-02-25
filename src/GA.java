import java.lang.reflect.Array;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class GA {

    private Map<String, Object> params;
    private int pop_size;
    private int gen_stop;
    private Double p_c;
    private Double p_m;
    
    // Set all params
    public GA (Map<String, Object> params) {
        this.pop_size = (Integer) params.get("pop_size");
        this.gen_stop = (Integer) params.get("gen_stop");
        this.p_c = (Double) params.get("p_c");
        this.p_m = (Double) params.get("p_m");
    }


    /*
    def init_pop(self):
        rand_ints = [random.getrandbits(self.indiv_len) for x in range(self.pop_size)]
        pop = list(map(lambda x: np.binary_repr(x, self.indiv_len), rand_ints))
        return pop
    */
    public static int[][] init_pop(int[][] pop) {
        
        int[][] a = new int[0][0];
        return a;
    }

    /*
    def evaluate_pop(self, pop):
        x, fitness, weights = self.fitness(pop, self.params)   # returns x-values list, fitness list, weights list
        return x, fitness, weights
    */
    public static List<Object> evaluate_pop(int[][] pop) {
        
    int[][] a = new int[0][0];
        return Arrays.asList(a);
    }

    /*
    def do_terminate(self, pop_eval, gen_count):
        term = True if gen_count >= self.max_gen else False
        return term
    */
    public static Boolean do_terminate(int[][] pop_eval, int gen_count) {
        
        return true;
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
        return a;
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
        return a;
    }

    /*
    def make_offsprings(self, parents):
        offsprings = self.crossover(parents)
        offsprings_mod = self.mutate(offsprings)
        return offsprings_mod
    */
    public static int[][] make_offsprings(int[][] parents) {
        
    int[][] a = new int[0][0];
        return a;
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
                                    int[] pop_weights, 
                                    int[][]off_weights) {
        
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



    /*
    pop = self.init_pop() # numpy array (pop_size, 1)
    gen_count = 0
    # Store data, gen 0
    x, pop_fitness, pop_weights = self.evaluate_pop(pop)
    entropy = self.get_pop_entropy(pop)
    eval_log = {gen_count: [pop, pop_weights, x, pop_fitness, entropy]}
    # Evolution:
    while not self.do_terminate(pop_fitness, gen_count):
        parents = self.select_parents(pop)
        offsprings = self.make_offsprings(parents)
        _, off_fitness, off_weights = self.evaluate_pop(offsprings)
        pop = self.select_survivors(parents, offsprings, pop_fitness, off_fitness, self.params['is_high_best'])
        gen_count += 1
        # Store data, gen > 0
        x, pop_fitness, pop_weights = self.evaluate_pop(pop)
        entropy = self.get_pop_entropy(pop)
        eval_log[gen_count] = [pop, pop_weights, x, pop_fitness, entropy]

    print('Algorithm succsessfully executed')
    */
    public static void main(String[] args) {

        int[][] pop = new int[1][200];
        int gen_count = 0;
        List<Object> pop_eval = evaluate_pop(pop);


        
    }
}

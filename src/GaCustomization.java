import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;

public class GACustomization {

    private Map<String, Object> params;

    GACustomization(Map<String, Object> params) {
        this.params = params;
    }

     // ----------------------------- Help Methods -------------------------------

    public int getByWeight(List<Double> weights) {
        List<Pair<Integer, Double>> itemWeights = new ArrayList<Pair<Integer, Double>>();
        for (int i=0; i < weights.size(); i++) {
            itemWeights.add(new Pair<Integer,Double>(i, weights.get(i)));
        }
        int selected_int = new EnumeratedDistribution<>(itemWeights).sample();
        return selected_int;
    }


    // ----------------------------- CustomGA Methods -------------------------------

    public int[][] makeIndiv_BASE(long num_nurses, long num_patients, long nurse_cap, Map<String, Map<String, Long>> patients, Map<String, Long> depot) {
        // Make return object shell 
        int[][] indiv = new int[(int) num_nurses][];
        // Make list of all patients to be assigned
        List<Integer> patient_ints = new ArrayList<Integer>(IntStream.rangeClosed(1, (int)num_patients)
                                                    .boxed()
                                                    .collect(Collectors.toList()));
        // For each nurse available 
        for (int i=0; i<num_nurses; i++) {
            // Initialize with nurse capacity and return time
            Long cap = nurse_cap;
            Long rt = depot.get("return_time");
            // Initialize with nurse capacity and return time
            List<Integer> nurse_list = new ArrayList<Integer>();
            // For patient that nurse can treat 
            for (int j=1; j<num_patients; j++) {
                if (cap > 0 && patient_ints.size() > 0 && rt > 0) {
                    // Select patient randomly
                    int rand_index = ThreadLocalRandom.current().nextInt(0, patient_ints.size());
                    int patient = patient_ints.remove(rand_index);
                    // Substract demand from capacity
                    String patient_str = String.valueOf(patient);
                    Long cap_used = patients.get(patient_str).get("demand");
                    cap -= cap_used;
                    // Substract time consumption from return time
                    Long time_used = patients.get(patient_str).get("care_time");
                    rt -= time_used;
                    // Add patient to nurse
                    if (cap > 0.0 && rt > 0.0){
                        nurse_list.add(patient);
                    }
                    else {
                        patient_ints.add(patient);
                        break;
                    }
                    //System.out.println("Nurse " + i + ", capacity: " + cap);
                    //System.out.println("Nurse " + i + ", return time left: " + rt);
                }
                else {
                    break;
                }
            }
            if (nurse_list.size()>0) {
                indiv[i] = nurse_list.stream().filter(x -> x != 0.0).mapToInt(x -> x).toArray();
            }
            else {
                indiv[i] = new int[0];
            }
            //System.out.println(Arrays.deepToString(indiv));
        }
        return indiv;
    }
    
    public int[][] makeIndiv_RANDCUT(long num_nurses, long num_patients, long nurse_cap, Map<String, Map<String, Long>> patients, Map<String, Long> depot) {
        // Make return object shell 
        int[][] indiv = new int[(int) num_nurses][];
        // Make list of all patients to be assigned
        List<Integer> patient_ints = new ArrayList<Integer>(IntStream.rangeClosed(1, (int)num_patients)
                                                    .boxed()
                                                    .collect(Collectors.toList()));
        // Set up randCut for skipping to next nurse prob
        double nurse_cut = (double) params.get("nurse_cut");
        List<Double> cut_weights = Arrays.asList(1-nurse_cut, nurse_cut);
        // For each nurse available 
        for (int i=0; i<num_nurses; i++) {
            // Initialize with nurse capacity and return time
            Long cap = nurse_cap;
            Long rt = depot.get("return_time");
            // Initialize with nurse capacity and return time
            List<Integer> nurse_list = new ArrayList<Integer>();
            // For patient that nurse can treat 
            for (int j=1; j<num_patients; j++) {
                if (cap > 0 && patient_ints.size() > 0 && rt > 0) {
                    // Select patient randomly
                    int rand_index = ThreadLocalRandom.current().nextInt(0, patient_ints.size());
                    int patient = patient_ints.remove(rand_index);
                    // Substract demand from capacity
                    String patient_str = String.valueOf(patient);
                    Long cap_used = patients.get(patient_str).get("demand");
                    cap -= cap_used;
                    // Substract time consumption from return time
                    Long time_used = patients.get(patient_str).get("care_time");
                    rt -= time_used;
                    // Add patient to nurse
                    if (cap > 0.0 && rt > 0.0){
                        nurse_list.add(patient);
                        if (getByWeight(cut_weights) == 1) break;
                    }
                    else {
                        patient_ints.add(patient);
                        break;
                    }
                    //System.out.println("Nurse " + i + ", capacity: " + cap);
                    //System.out.println("Nurse " + i + ", return time left: " + rt);
                }
                else {
                    break;
                }
            }
            if (nurse_list.size()>0) {
                indiv[i] = nurse_list.stream().filter(x -> x != 0.0).mapToInt(x -> x).toArray();
            }
            else {
                indiv[i] = new int[0];
            }
            //System.out.println(Arrays.deepToString(indiv));
        }
        return indiv;
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
    public int[][][] selectParents_BASE(int[][][] pop, double[] pop_weights) {
        List<Double> weights = DoubleStream.of(pop_weights).boxed().collect(Collectors.toList());
        for (int i=0; i < pop.length; i++) {
            int[][] child = pop[getByWeight(weights)];
            pop[i] = child;
        }
        return pop;
    }

    public int[][][] doCrossover(int[][][] offsprings) {
        
        int[][][] offsprings_mod = new int[1][1][1];
            return offsprings_mod;
    }

    public int[][][] mutate(int[][][] offsprings) {
        
        int[][][] offsprings_mod = new int[0][0][0];
            return offsprings_mod;
    }


    public List<Object> selectSurvivors(int[][][] parents, 
                                    int[][][] offsprings, 
                                    double[] pop_weights, 
                                    double[] off_weights) {

        List<Object> offspr = Arrays.asList(0);
        return offspr;
    }
    



}

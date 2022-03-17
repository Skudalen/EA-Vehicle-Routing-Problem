import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
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

    public int[][][] selectParents_BASE(int[][][] pop, double[] pop_weights) {
        List<Double> weights = DoubleStream.of(pop_weights).boxed().collect(Collectors.toList());
        for (int i=0; i < pop.length; i++) {
            int[][] child = pop[getByWeight(weights)];
            pop[i] = child;
        }
        return pop;
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
    public int[][][] doCrossover_BASE(int[][][] pop) {

        // Retrive the prob for crossover 
        double p_c = (double) params.get("p_c");
        List<Double> doCrossProb = Arrays.asList(1-p_c, p_c);
        // Iterate each individual
        for (int i=0; i<pop.length-1; i+=2) {
            // Choose two parents 
            int[][] parent1 = pop[i];
            int[][] parent2 = pop[i+1];
            // Iterate each nurse pair 
            for (int j=0; j<parent1.length; j++) {
                int crosspoint = 0;
                // Choose a nurse pair 
                int[] nurse1 = parent1[j];
                int[] nurse2 = parent2[j];
                // Determine the longest and shortest
                int[] shortest = nurse1; 
                int[] longest = nurse2; 
                if (nurse2.length < nurse1.length) {
                    shortest = nurse2;
                    longest = nurse1;
                }
                // Iterate each patient for each nurse
                for (int k=0; k<shortest.length; k++) {
                    // Determine if this index k is the crosspoint
                    int temp = getByWeight(doCrossProb);
                    if (temp == 1) {
                        crosspoint = k;
                        break;
                    }
                }
                // Do crossover if k was chosen
                if (crosspoint != 0) {
                    int[] nurse1_1 = Arrays.copyOfRange(nurse1, 0, crosspoint);
                    int[] nurse2_2 = Arrays.copyOfRange(nurse2, crosspoint, longest.length);
                    nurse1 = ArrayUtils.addAll(nurse1_1, nurse2_2);

                    int[] nurse2_1 = Arrays.copyOfRange(nurse2, 0, crosspoint);
                    int[] nurse1_2 = Arrays.copyOfRange(nurse1, crosspoint, longest.length);
                    nurse2 = ArrayUtils.addAll(nurse2_1, nurse1_2);
                    
                    pop[i][j] = nurse1;
                    pop[i+1][j] = nurse2;
                }
            }
        }
        return pop;
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

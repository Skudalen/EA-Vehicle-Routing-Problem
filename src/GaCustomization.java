import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class GACustomization {

    public int[][] make_indiv(long num_nurses, long num_patients, long nurse_cap, Map<String, Map<String, Long>> patients, Map<String, Long> depot) {

        int[][] indiv = new int[(int) (long) num_nurses][(int)num_patients+1];
        List<Integer> patient_ints = new ArrayList<Integer>(IntStream.rangeClosed(1, (int)num_patients)
                                                    .boxed()
                                                    .collect(Collectors.toList()));
        for (int i=0; i<num_nurses; i++) {
            if (patient_ints.size() < 1) {
                break;
            }
            // Initialize with depot (0)
            indiv[i][0] = 0;
            // Initialize with nurse capacity and return time
            Long cap = nurse_cap;
            Long rt = depot.get("return_time");

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
                    indiv[i][j] = patient;
                }
                else {
                    break;
                }
            }
        }
        return indiv;
    }
    

    public int[][][] select_parents(int[][][] pop) {
        
        int[][][] parents = new int[0][0][0];
            return parents;
    }

    public int[][][] crossover(int[][][] offsprings) {
        
        int[][][] offsprings_mod = new int[1][1][1];
            return offsprings_mod;
    }

    public int[][][] mutate(int[][][] offsprings) {
        
        int[][][] offsprings_mod = new int[0][0][0];
            return offsprings_mod;
    }

    public int[][][] make_offsprings(int[][][] parents) {
        
        int[][][] offsprings_mod = new int[0][0][0];
        return offsprings_mod;
    }

    public int[][][] select_survivors(int[][][] parents, 
                                    int[][][] offsprings, 
                                    Double[][] pop_weights, 
                                    Double[][] off_weights) {

        int[][][] a = new int[0][0][0];
        return a;
    }
    



}

import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;
import java.util.stream.IntStream;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.math3.analysis.function.Logit;
import org.apache.commons.math3.distribution.EnumeratedDistribution;
import org.apache.commons.math3.util.Pair;


public class GACustomization {

    private Map<String, Object> params;

    GACustomization(Map<String, Object> params) {
        this.params = params;
    }

     // ----------------------------- Help Methods -------------------------------

    static public int getByWeight(List<Double> weights) {
        List<Pair<Integer, Double>> itemWeights = new ArrayList<Pair<Integer, Double>>();
        for (int i=0; i<weights.size(); i++) {
            itemWeights.add(new Pair<Integer,Double>(i, weights.get(i)));
        }
        int selected_int = new EnumeratedDistribution<>(itemWeights).sample();
        return selected_int;
    }

    static public double getIndivDiff(int[][] indiv1, int[][] indiv2) {
        double diff = 0;
        int[] shortest;
        int[] longest;
        for (int i=0; i<indiv1.length; i++) {
            int[] nurse1 = indiv1[i];
            int[] nurse2 = indiv2[i];
            if (nurse1.length < nurse2.length) {
                shortest = nurse1;
                longest = nurse2;
            }
            else {
                shortest = nurse2;
                longest = nurse1;
            }
            diff += longest.length - shortest.length;
            for (int j=0; j<shortest.length; j++) {
                if (nurse1[j] != nurse2[j]) diff += 1;
            }
        }
        return diff;    
    }
    static private boolean isDuplicates(int [] offspring, int index){
        for(int i = 0; i < offspring.length; i++){
            if((offspring[i] == offspring[index]) &&
                    (index != i) ){
                return true;
            }
        }
        return false;
    }

    static public List<int[]> PMX(Map<String, Object> params, int[] nurse1, int[] nurse2) {
        // Retrive the prob for crossover 
        double p_c = (double) params.get("p_c");
        List<Double> doCrossProb = Arrays.asList(1-p_c, p_c);
        // Determine the longest and shortest
        int[] shortest = nurse1; 
        if (nurse2.length < nurse1.length) shortest = nurse2;
        // Get two crossover points
        int point1 = -1;
        int point2 = -1;
        int point_counter = 0;
        for (int k=0; k<shortest.length; k++) {
            int temp = getByWeight(doCrossProb);
            if (temp == 1) {
                if (point_counter == 0) {
                    point1 = k;
                    point_counter += 1;
                }
                else if (point_counter == 1) {
                    point2 = k;
                    point_counter += 1;
                }
                else break;
            }
        }
        // Finish if one crossoverpoint was not found
        if (point1 == -1 || point2 == -1) return Arrays.asList(nurse1, nurse2);

        // -------- DO CROSSOVER ---------
        // Init offsprings
        int[] offspring1 = new int[nurse1.length];
        int[] offspring2 = new int[nurse2.length];
        // Create the segments
        int segment_length = (point2 - point1) + 1;
        int[] segment1 = new int[segment_length];
        int[] segment2 = new int[segment_length];
        int segmentIndex = 0;
        for(int i = 0; i<shortest.length; i++){
            if((i >= point1) && (i <= point2)){
                segment1[segmentIndex] = nurse1[i];
                segment2[segmentIndex] = nurse2[i];
                segmentIndex++;
            }
        }
        // Insert sements 
        // Off1 gets Seg2 and Off2 gets Seg1
        segmentIndex = 0;
        for(int j = 0; j<shortest.length; j++){
           if((j >= point1) && (j <= point2)){
               offspring1[j] = segment2[segmentIndex];
               offspring2[j] = segment1[segmentIndex];
               segmentIndex++;
           }
        }
        // Fill Off1 from Nurse1
        for(int i = 0; i < offspring1.length; i++){
            if((i < point1) || (i > point2)){
               offspring1[i] = nurse1[i];
            }
        }
        // Fill Off2 from Nurse2
        for(int index = 0; index < offspring2.length; index++){
            if((index < point1) || (index > point2)){
               offspring2[index] = nurse2[index];
            }
        }
        // Deal with duplicates Off1
        for(int i = 0; i < offspring1.length; i++){
            if((i < point1) || (i > point2)){
                while(isDuplicates(offspring1, i)){
                    for(int j = 0; j < segment1.length; j++){
                        if(segment1[j] == offspring1[i]){
                            offspring1[i] = segment2[j];
                        }
                        else if(segment2[j] == offspring1[i]){
                            offspring1[i] = segment1[j];
                        }
                    }
                }
            }
        }
        // Deal with duplicates Off2
        for(int i = 0; i < offspring2.length; i++){
            if((i < point1) || (i > point2)){
                while(isDuplicates(offspring2, i)){
                    for(int j = 0; j < segment1.length; j++){
                        if(segment1[j] == offspring2[i]){
                            offspring2[i] = segment2[j];
                        }
                        else if(segment2[j] == offspring2[i]){
                            offspring2[i] = segment1[j];
                        }
                    }
                }
            }
        }
        
        return Arrays.asList(offspring1, offspring2);
    }

    public static double logistic(double x) {
        return 1 / (1 + Math.exp(x));
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
            // Add remaining patients if more left
            if (i == (num_nurses-1) && patient_ints.size() > 0){
                int patients_left = patient_ints.size();
                for (int k=0; k<patients_left; k++) {
                    nurse_list.add(patient_ints.remove(0));
                }
            }
            // Remove unnecessary zeroes
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
            // Add remaining patients if more left
            if (i == (num_nurses-1) && patient_ints.size() > 0){
                int patients_left = patient_ints.size();
                for (int k=0; k<patients_left; k++) {
                    nurse_list.add(patient_ints.remove(0));
                }
            }
            // Remove unnecessary zeroes
            if (nurse_list.size()>0) {
                indiv[i] = nurse_list.stream().filter(x -> x != 0.0).mapToInt(x -> x).toArray();
            }
            else {
                indiv[i] = new int[0];
            }
            //System.out.println(Arrays.deepToString(indiv));
        }
        //System.out.println(patient_ints);
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

    // One-point Crossover
    public int[][][] doCrossover_OLD(int[][][] pop) {
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
                // Break if one nurse has no patients
                if (nurse1.length < 1 || nurse2.length < 1) break;
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
    // PMX Crossover
    public int[][][] doCrossover_BASE(int[][][] pop) {
        
        // Iterate each individual
        for (int i=0; i<pop.length-1; i+=2) {
            // Choose two parents 
            int[][] parent1 = pop[i];
            int[][] parent2 = pop[i+1];
            // Iterate each nurse pair 
            for (int j=0; j<parent1.length; j++) {
                // Choose a nurse pair 
                int[] nurse1 = parent1[j];
                int[] nurse2 = parent2[j];
                // Break if one nurse has no patients
                if (nurse1.length < 1 || nurse2.length < 1) break;
                
                // DO CROSSOVER
                List<int[]> offsprings = PMX(params, nurse1, nurse2);
                pop[i][j] = offsprings.get(0);
                pop[i+1][j] = offsprings.get(1);
            }
        }
        return pop;
    }

    // Swap Mutation
    public List<Object> mutate_BASE(int[][][] offsprings, GA ga) {
        // Initialize off_info list and off_fitness array
        List<Object> off_info = new ArrayList<>();
        double[] off_fitness = new double[offsprings.length];
        // Retrive the prob for crossover 
        double p_m = (double) params.get("p_m");
        List<Double> doMuteProb = Arrays.asList(1-p_m, p_m);
        // Iterate each indiv, set of nurses
        for (int i=0; i<offsprings.length; i++) {
            int[][] indiv = offsprings[i];
            // Iterate each nurse 
            for (int j=0; j<indiv.length; j++) {
                int[] nurse = indiv[j];
                int point1 = -1;
                int point2 = -1;
                // Iterate each patient to find mutation point
                int point_counter = 0;
                for (int k=0; k<nurse.length; k++) {
                    // Determine if this index k is the mutation point
                    int temp = getByWeight(doMuteProb);
                    if (temp == 1) {
                        if (point_counter == 0) {
                            point1 = k;
                            point_counter += 1;
                        }
                        else if (point_counter == 1) {
                            point2 = k;
                            point_counter += 1;
                        }
                        else break;
                    }
                }
                // Set mutation
                if (point1 != -1 && point2 != -1) {
                    int val1 = offsprings[i][j][point1];
                    int val2 = offsprings[i][j][point2];
                    offsprings[i][j][point1] = val2;
                    offsprings[i][j][point2] = val1;
                }
            }
            double indiv_fitnes = ga.checkIndivValidTravel(offsprings[i], ga.getPatients(), ga.getDepot(), ga.getTravel_times());
            off_fitness[i] = indiv_fitnes;
        }
        off_info.add(offsprings);
        off_info.add(off_fitness);
        return off_info;
    }

    // Genaralized Crowding
    public List<Object> selectSurvivors_BASE(int[][][] pop, double[] pop_fitness, 
                                            double[] pop_weights, int[][][] offsprings, 
                                            double[] off_fitness,  double[] off_weights) {
        List<Object> newPop_info = new ArrayList<>();
        // Get phi to GC
        double phi = (double) params.get("GC_phi");
        // Init new pop and pop_fitness
        int[][][] newPop = new int[pop.length][][];
        double[] newFitness = new double[pop.length];

        for (int i=0; i<pop.length; i+=2) {
            int[][] p1 = pop[i];
            int[][] p2 = pop[i+1];
            int[][] o1 = offsprings[i];
            int[][] o2 = offsprings[i+1];
            double p1_w = pop_weights[i];
            double p2_w = pop_weights[i+1];
            double o1_w = off_weights[i];
            double o2_w = off_weights[i+1];
            Logit logit = new Logit();
            if (getIndivDiff(p1, o1) + getIndivDiff(p2, o2) < getIndivDiff(p1, o2) + getIndivDiff(p2, o1)) {
                // p1 vs. o1
                double logit_po = Math.pow(phi, logit.value(p1_w-o1_w));
                double logit_op = Math.pow(phi, logit.value(o1_w-p1_w));
                double p_o = (logit_po * o1_w) / (logit_po * o1_w + logit_op * p1_w);
                List<Double> chooseOffProb = Arrays.asList(1-p_o, p_o);
                int temp = getByWeight(chooseOffProb);
                if (temp == 0) {
                    newPop[i] = p1;
                    newFitness[i] = pop_fitness[i];
                }
                else {
                    newPop[i] = o1;
                    newFitness[i] = off_fitness[i];
                }
                // p2 vs. o2
                logit_po = Math.pow(phi, logit.value(p2_w-o2_w));
                logit_op = Math.pow(phi, logit.value(o2_w-p2_w));
                p_o = (logit_po * o2_w) / (logit_po * o2_w + logit_op * p2_w);
                chooseOffProb = Arrays.asList(1-p_o, p_o);
                temp = getByWeight(chooseOffProb);
                if (temp == 0) {
                    newPop[i+1] = p2;
                    newFitness[i+1] = pop_fitness[i+1];
                }
                else {
                    newPop[i+1] = o2;
                    newFitness[i+1] = off_fitness[i+1];
                }
            }
            else {
                // p1 vs. o2
                double logit_po = Math.pow(phi, logit.value(p1_w-o2_w));
                double logit_op = Math.pow(phi, logit.value(o2_w-p1_w));
                double p_o = (logit_po * o2_w) / (logit_po * o2_w + logit_op * p1_w);
                List<Double> chooseOffProb = Arrays.asList(1-p_o, p_o);
                int temp = getByWeight(chooseOffProb);
                if (temp == 0) {
                    newPop[i] = p1;
                    newFitness[i] = pop_fitness[i];
                }
                else {
                    newPop[i] = o2;
                    newFitness[i] = off_fitness[i+1];
                }
                // p2 vs. o1
                logit_po = Math.pow(phi, logit.value(p2_w-o1_w));
                logit_op = Math.pow(phi, logit.value(o1_w-p2_w));
                p_o = (logit_po * o1_w) / (logit_po * o1_w + logit_op * p2_w);
                chooseOffProb = Arrays.asList(1-p_o, p_o);
                temp = getByWeight(chooseOffProb);
                if (temp == 0) {
                    newPop[i+1] = p2;
                    newFitness[i+1] = pop_fitness[i+1];
                }
                else {
                    newPop[i+1] = o1;
                    newFitness[i+1] = off_fitness[i];
                }
            }
        }
        newPop_info.add(newPop);
        newPop_info.add(newFitness);
        return newPop_info;
    }
    



}

package HospitalSimulation_Threads;

import java.util.Random;

public class Main {
    static final int iterations = 10; // Total program cycles
    static final int k = 5; // Disease creation [0...k]
    static final int h = 3; // Hospital cures [0...h], h<k
    static final int e = 20; // ICU capacity
    static final int disease_period = 1000;
    static final int cure_period = 5000;

    static int occupied = 0; // occupied beds
    static int TotalRejected = 0; // total rejections
    static int TotalRecovered = 0; // total recoveries
    static boolean diseaseDone = false;
    private static final Object lock = new Object();


    public static void main(String[] args) throws InterruptedException {
        Thread disease = new Thread(new Disease());
        Thread hospital = new Thread(new Hospital());

        disease.start();
        hospital.start();

        disease.join();
        hospital.join();

        System.out.println("\n=== Final Results ===");
        System.out.println("Total recoveries: " + TotalRecovered);
        System.out.println("Total rejections: " + TotalRejected);
        System.out.println("ICU occupation: " + occupied + "/" + e);
    }

    // The Disease thread periodically generates new cases and tries to admit them to available beds.
    static class Disease implements Runnable{
        Random random = new Random();

        @Override
        public void run(){
            for(int i =0; i<iterations;i++){
                // Generate a random number of new cases between 0 and k
                int newK = random.nextInt(k+1);
                int admitted;
                int rejected;

                synchronized (lock){
                    int free = e - occupied;
                    admitted = Math.min(newK,free);
                    rejected = newK - admitted;

                    occupied += admitted;
                    TotalRejected += rejected;
                }
                System.out.printf("[DISEASE] Iteration %02d: new=%d, admitted=%d, rejected=%d | ICU: %d/%d%n", i, newK, admitted, rejected, occupied, e);

                if (i < iterations) {
                    try {
                        Thread.sleep(disease_period);
                    }
                    catch (InterruptedException ignored) {}
                }
            }
            diseaseDone = true;
        }
    }

    // Hospital thread periodically heals some patients and frees ICU beds.
    static class Hospital implements Runnable{
        Random random = new Random();

        @Override
        public void run(){
            for(int i=0; i<iterations; i++){
                // Randomly decide how many patients to heal
                int heal = random.nextInt(h+1);
                int healed;

                synchronized (lock){
                    healed = Math.min(heal,occupied);
                    occupied -= healed;
                    TotalRecovered += healed;
                }

                System.out.printf("[HOSPITAL] tried to heal=%d, healed=%d | ICU: %d/%d%n", heal, healed, occupied, e);

                synchronized (lock) {
                    if (diseaseDone && occupied == 0) {
                        break; // stop if disease thread is done and there are no more patients in the ICU
                    }
                }
                try {
                    Thread.sleep(cure_period);
                }
                catch (InterruptedException ignored) {}
            }
        }
    }
}

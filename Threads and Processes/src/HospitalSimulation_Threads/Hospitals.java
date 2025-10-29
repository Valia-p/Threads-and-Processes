package HospitalSimulation_Threads;

import java.util.Random;

public class Hospitals {
    static final int iterations = 10; // Total program cycles
    static final int k = 10; // Disease creation [0...k]
    static final int h = 3;  // Hospital cures [0...h], h < k
    static final int e = 15;   // ICU capacity per hospital
    static final int disease_period = 1000;
    static final int cure_period = 5000;
    static final int NumberOfHospitals = 3; // Total number of hospitals

    static int[] occupied = new int[NumberOfHospitals];  // occupied beds per hospital
    static int totalRecovered = 0;  // total recoveries
    static int totalRejected = 0;  // total rejections

    private static final Object lock = new Object();

    public static void main(String[] args) throws InterruptedException {
        Thread disease = new Thread(new Disease(), "DISEASE");
        Thread[] hospitals = new Thread[NumberOfHospitals];

        for (int i = 0; i < NumberOfHospitals; i++) {
            hospitals[i] = new Thread(new Hospital(i), "HOSPITAL-" + (i + 1));
        }

        // Start all threads
        disease.start();
        for (int i = 0; i < hospitals.length; i++) {
            hospitals[i].start();
        }

        // Wait for all threads to finish
        disease.join();
        for (int i = 0; i < hospitals.length; i++) {
            hospitals[i].join();
        }

        // Final results
        int sumOccupied = 0;
        for (int i = 0; i < NumberOfHospitals; i++) {
            System.out.printf("Hospital %d: ICU=%d/%d%n", i + 1, occupied[i], e);
            sumOccupied += occupied[i];
        }

        System.out.println("\n=== Final Results ===");
        System.out.println("Total recoveries: " + totalRecovered);
        System.out.println("Total rejections: " + totalRejected);
        System.out.println("Total ICU occupancy: " + sumOccupied + "/" + (NumberOfHospitals * e));
    }

    // The Disease thread periodically generates new cases and tries to admit them to available hospitals.
    static class Disease implements Runnable {
        Random random = new Random();

        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                // Generate a random number of new cases between 0 and k
                int newK = random.nextInt(k + 1);
                int admittedTotal = 0;
                int remaining;

                synchronized (lock) {
                    remaining = newK;
                    // Distribute cases greedily among hospitals in order
                    // Each hospital takes as many as it can
                    for (int j = 0; j < NumberOfHospitals && remaining > 0; j++) {
                        int free = e - occupied[j];  // available beds in this hospital
                        int admitted = Math.min(free, remaining);
                        occupied[j] += admitted;
                        admittedTotal += admitted;
                        remaining -= admitted;
                    }
                    // Any cases still remaining after all hospitals are full get rejected
                    totalRejected += remaining;
                }

                System.out.printf("[DISEASE] iteration=%02d: new=%d, admitted=%d, rejected=%d%n",i + 1, newK, admittedTotal, remaining);

                if (i < iterations) {
                    try { Thread.sleep(disease_period); } catch (InterruptedException ignored) {}
                }
            }
        }
    }

    // Each Hospital thread periodically heals some patients and frees ICU beds.
    static class Hospital implements Runnable {
        Random random = new Random();
        int id;

        Hospital(int id) { this.id = id; }

        @Override
        public void run() {
            for (int i = 0; i < iterations; i++) {
                // Randomly decide how many patients to heal
                int heal = random.nextInt(h + 1);

                int healedNow;
                int occAfter;
                synchronized (lock) {
                    healedNow = Math.min(heal, occupied[id]);
                    occupied[id] -= healedNow;  // free the beds
                    totalRecovered += healedNow; // update total recoveries
                    occAfter = occupied[id];
                }

                System.out.printf("[%s] iteration=%02d: triedHeal=%d, healed=%d, ICU=%d/%d%n", Thread.currentThread().getName(), i + 1, heal, healedNow, occAfter, e);

                if (i < iterations) {
                    try { Thread.sleep(cure_period); } catch (InterruptedException ignored) {}
                }
            }
        }
    }
}

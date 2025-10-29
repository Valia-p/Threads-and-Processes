package MatrixVectorMultiplication;

import java.util.Random;

public class MatrixVectorMultiplication {
    private static int N = 2048; // matrix rows
    private static int M = 2048; // matrix columns
    private static int k = 1; // number of threads (k<N)

    public static void main(String[] args) throws InterruptedException {
        int[][] A = new int[N][M];
        int[] v = new int[M];
        int[] result = new int[N];
        int rowsPerThread = N/k;

        fillMatrix(A);
        fillVector(v);

        long start = System.nanoTime(); // start time

        Thread threads[] = new Thread[k];
        // Create and start threads
        for (int thread = 0; thread < k; thread++){
            int firstRow = thread * rowsPerThread;
            int lastRow  = firstRow + rowsPerThread;
            threads[thread] = new Thread(new Multiply(A, v, result, firstRow, lastRow));
            threads[thread].start();
        }

        // Wait for all threads to finish
        for (int i = 0; i < k; i++) {
            threads[i].join();
        }

        long end = System.nanoTime();

        // Print result
        System.out.println("Result:");
        printVector(result);
        System.out.printf("Time: %.2f ms%n", (end - start) / 1_000_000.0);
    }

    static class Multiply implements Runnable {
        private  int[][] A;
        private  int[] v;
        private  int[] result;
        private  int firstRow, lastRow;

        Multiply(int[][] A, int[] v, int[] result, int firstRow, int lastRow){
            this.A = A;
            this.v = v;
            this.result = result;
            this.firstRow = firstRow;
            this.lastRow = lastRow;
        }

        @Override
        public void run() {
            for (int i = firstRow; i<lastRow; i++){
                int sum = 0;
                for (int j = 0; j < v.length; j++){
                    sum += A[i][j] * v[j];
                }
                result[i] = sum;
            }
        }
    }

    // Fill matrix with random numbers [0..10]
    static void fillMatrix(int[][] matrix) {
        Random random = new Random();
        for (int i = 0; i < matrix.length; i++){
            for (int j = 0; j < matrix[0].length; j++){
                matrix[i][j] = random.nextInt(11);
            }
        }
    }

    // Fill vector with random numbers [0..10]
    static void fillVector(int[] vector) {
        Random random = new Random();
        for (int i=0; i < vector.length; i++){
            vector[i] = random.nextInt(11);
        }
    }

    static void printVector(int[] v) {
        for (int x : v) System.out.printf("%d ", x);
        System.out.println();
    }

}
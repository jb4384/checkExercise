/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author Tiffany
 */
public class Assignment2 {

    /**
     * @param args the command line arguments
     */
    public static NumberFormat formatter = new DecimalFormat("#0.000");

    public static void main(String[] args) {
        //a = adjacent matrix
        double[][] a = {{0, 0, 1, 0, 0, 0, 1, 1, 1, 1},
        {0, 0, 1, 1, 0, 1, 1, 0, 0, 0},
        {1, 1, 0, 1, 1, 1, 0, 0, 0, 0},
        {0, 1, 1, 0, 1, 1, 0, 0, 1, 0},
        {0, 0, 1, 1, 0, 1, 0, 0, 0, 0},
        {0, 1, 1, 1, 1, 0, 0, 0, 0, 1},
        {1, 1, 0, 0, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 1, 0, 1, 1},
        {1, 0, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 1, 1, 0}};

        double[][] b = {{0, 0, 0, 0, 0, 0, 1, 1, 1, 1},
        {0, 0, 1, 1, 0, 1, 0, 0, 0, 0},
        {0, 1, 0, 1, 1, 1, 0, 0, 0, 0},
        {0, 1, 1, 0, 1, 1, 0, 0, 1, 0},
        {0, 0, 1, 1, 0, 1, 0, 0, 0, 0},
        {0, 1, 1, 1, 1, 0, 0, 0, 0, 1},
        {1, 0, 0, 0, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 1, 0, 1, 1},
        {1, 0, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 1, 1, 0}};

        spectral(a);
    }

    public static void spectral(double[][] a) {
        double[] kj = countOnes(a); // kj
        double[] ki = kj; // ki
        double M = sumRow(kj);

        /* Create the modularity matrix B (B = A - P) 
        Matrix A is created above. Matrix P is P1 / M.
        P1 is kj * ki.*/
        double[][] B = new double[a.length][a.length];
        for (int row = 0; row < a.length; row++) {
            for (int col = 0; col < a.length; col++) {
                /* In each cell, multiply sum of that cell's row in A by
                sum of that cell's column in A. Divide result by M, the
                total sum of A. Subtract this value P from corresponding 
                value in A. */
                double p = (ki[row] * kj[col]) / M;
                B[row][col] = a[row][col] - p;
            }
        }
        RealMatrix m = MatrixUtils.createRealMatrix(B);
        EigenDecomposition ev = new EigenDecomposition(m);
        System.out.println("This is all the eigenvalues: " + Arrays.toString(ev.getRealEigenvalues()));
        RealVector t = ev.getEigenvector(0);

        System.out.println("Eigenvector: " + t);

        /*Created two arrays: g1 and g2. g1 holds the vertices of the pos values
        in the eigenvector. g2 holds the vetices of the neg values in the
        eigenvector. Sg1 and Sg2 are vectors that will be used in calculating
        the modularity value later.*/
        int size = t.getDimension();
        ArrayList<Integer> g1 = new ArrayList<>();
        //Positive vertices g1: 2, 3, 4, 5, 6
        double[] Sg1 = new double[size];
        ArrayList<Integer> g2 = new ArrayList<>();
        //Negative vertices g2: 1, 7, 8, 9, 10
        double[] Sg2 = new double[size];
        int count = 1;
        for (int i = 0; i < size; i++) {
            if (t.getEntry(i) > 0) {
                g1.add(count);
                Sg1[i] = 1;
            } else if (t.getEntry(i) < 0) {
                g2.add(count);
                Sg2[i] = 1;
            }
            count++;
        }
        if (g1.isEmpty() || g2.isEmpty()) {
            System.out.println("Group 1 or Group 2 are empty.");
            System.exit(0);
        }
        System.out.println();
        double rank1 = 0;
        System.out.print("Subsidary for g1: ");
        for (int i = 0; i < g1.size(); i++) {
            double temp = t.getEntry(g1.get(i) - 1);
            System.out.printf("%-10s", formatter.format(temp));
            if (Math.abs(temp) > rank1) {
                rank1 = temp;
            }
        }
        System.out.println();
        System.out.println("Rank: " + rank1);
        System.out.println();

        double rank2 = 0;
        System.out.print("Subsidary for g2: ");
        for (int i = 0; i < g2.size(); i++) {
            double temp = t.getEntry(g2.get(i) - 1);
            System.out.printf("%-10s", formatter.format(temp));
            if (Math.abs(temp) > rank2) {
                rank2 = temp;
            }
        }
        System.out.println();
        System.out.println("Rank: " + rank2);
        System.out.println();

        //G1:
        double Zg1 = modularityValue(B, Sg1, M);
        System.out.println("Modularity Value of g1: " + Zg1);

        //G2:
        double Zg2 = modularityValue(B, Sg2, M);
        System.out.println("Modularity Value of g2: " + Zg2);

        System.out.println("------------------------------------------------");
        /* If Z is less than or equal to zero, then the group is not required
        to be divided further. Else, repeat the entire process for the
        subgraph composed of the vertices in the group. Zg1 and Zg2 are greater
        than zero, therefore we must continue the subgroups.*/
        if (Zg1 > 0) {
            //Create g1 matrix
            double[][] g11 = new double[g1.size()][g1.size()];
            for (int i = 0; i < g1.size(); i++) {
                for (int j = 0; j < g1.size(); j++) {
                    g11[i][j] = a[g1.get(i) - 1][g1.get(j) - 1];
                }
            }
            System.out.println("Subgraphs: ");
            spectral(g11);
        }

        if (Zg2 > 0) {
            //Create g2 matrix
            double[][] g21 = new double[g2.size()][g2.size()];
            for (int i = 0; i < g2.size(); i++) {
                for (int j = 0; j < g2.size(); j++) {
                    g21[i][j] = a[g2.get(i) - 1][g2.get(j) - 1];
                }
            }
            System.out.println("Subgraphs: ");
            spectral(g21);
        }

    }

    /* Calculate the modularity value for each group g1 and g2.
        Modulatiry value is Z = (1/(4 * m))*(S^t)* B * S, where m is M / 2, 
        (M = 44) S is the column vector of for group g1 or g2, 
        t is transposition, and B is the modularity matrix.*/
    public static double modularityValue(double[][] a, double[] b, double size) {
        double[] temp1 = multiplyVector(a, b); // S^t * B
        double dTemp1 = multiVect(temp1, b); // (S^t * B) * S
        double mM = size / 2;
        return ((1 / (4 * mM)) * dTemp1);
    }

    // Counts the frequency of ones in an array
    public static double[] countOnes(double[][] a) {
        double[] count = new double[a.length];

        for (int i = 0; i < a.length; i++) {
            double temp = 0;
            for (int j = 0; j < a[0].length; j++) {
                if (a[i][j] == 1) {
                    temp++;
                }
            }
            count[i] = temp;
        }
        return count;
    }

    public static double sumRow(double[] a) {
        double count = 0;
        for (int i = 0; i < a.length; i++) {
            count += a[i];
        }
        return count;
    }

    // Multiply two matrices together
    public static double[][] multiplyMatrices(double[][] a, double[][] b) {
        int m1 = a.length;
        int n1 = a[0].length;
        int m2 = b.length;
        int n2 = b[0].length;
        if (n1 != m2) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        double[][] c = new double[m1][n2];
        for (int i = 0; i < m1; i++) {
            for (int j = 0; j < n2; j++) {
                for (int k = 0; k < n1; k++) {
                    c[i][j] += a[i][k] * b[k][j];
                }
            }
        }
        return c;
    }

    // Multiplies a matrix with a vector
    public static double[] multiplyVector(double[][] a, double[] x) {
        int m = a.length;
        int n = a[0].length;
        if (x.length != n) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        double[] y = new double[m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                y[i] += a[j][i] * x[j];
            }
        }
        return y;
    }

    //Multiplies a vector with a vector
    public static double multiVect(double[] a, double[] b) {
        int m = a.length;
        int n = b.length;
        if (a.length != n) {
            throw new RuntimeException("Illegal matrix dimensions.");
        }
        double y = 0;
        for (int i = 0; i < m; i++) {
            y += a[i] * b[i];

        }
        return y;
    }

    //Prints a formatted 2D array as matrix
    public static void print(double[][] a) {
        NumberFormat formatter = new DecimalFormat("#0.000");
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                System.out.printf("%-10s", formatter.format(a[i][j]));
            }
            System.out.println();
        }
    }
}

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
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
    public static void main(String[] args) {
        double[][] a = {{0, 0, 1, 0, 0, 0, 1, 1, 1, 1}, //d = adjacent matrix
        {0, 0, 1, 1, 0, 1, 1, 0, 0, 0},
        {1, 1, 0, 1, 1, 1, 0, 0, 0, 0},
        {0, 1, 1, 0, 1, 1, 0, 0, 1, 0},
        {0, 0, 1, 1, 0, 1, 0, 0, 0, 0},
        {0, 1, 1, 1, 1, 0, 0, 0, 0, 1},
        {1, 1, 0, 0, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 0, 1, 0, 1, 1},
        {1, 0, 0, 1, 0, 0, 0, 1, 0, 1},
        {1, 0, 0, 0, 0, 1, 1, 1, 1, 0}};

        int[] kj = new int[a.length]; // kj
        int[] ki = new int[a.length]; // ki
        int M = 0;
        for (int i = 0; i < a.length; i++) {
            int count = countOnes(a[i]);
            M += count;
            kj[i] = count;
            ki[i] = count;
        }

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
                double p = 1.0 * ki[row] * kj[col] / M;
                B[row][col] = a[row][col] - p;
            }
        }

        RealMatrix m = MatrixUtils.createRealMatrix(B);
        NumberFormat formatter = new DecimalFormat("#0.000");
        System.out.println("Matrix B: ");
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a[0].length; j++) {
                System.out.printf("%10s", formatter.format(m.getEntry(i, j)));
            }
            System.out.println();
        }
        EigenDecomposition ev = new EigenDecomposition(m);
        RealVector t = ev.getEigenvector(0);

        System.out.println("This is the eigenvector at i: " + t);

        /*Created two arrays: g1 and g2. g1 holds the vertices of the pos values
        in the eigenvector. g2 holds the vetices of the neg values in the
        eigenvector. Sg1 and Sg2 are vectors that will be used in calculating
        the modularity value later.*/
        int size = t.getDimension();
        ArrayList<Integer> g1 = new ArrayList<>();
        double[] Sg1 = new double[size];
        ArrayList<Integer> g2 = new ArrayList<>();
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

        System.out.println("G1: " + g1);
        System.out.println("G2: " + g2);
        
        /* Calculate the modularity value for each group g1 and g2.
        Modulatiry value is Z = (1/(4m))(S^t)(BS), where m is M / 2, (M = 44)
        S is the column vector of for group g1 or g2, t is transposition, and
        B is the modularity matrix.*/
        //G1:
        double[] temp1 = multiplyVector(B, Sg1);
        
        //G2:
        double[] temp2 = multiplyVector(B, Sg2);
        
        
        
        double Zg1;
        double Zg2;
        
        /*If Z is less than or equal to zero, then the group is not required
        to be divided further. Else, repeat the entire process for the
        subgraph composed of the vertices in the group.*/

    }
    
    // Counts the frequency of ones in an array
    public static int countOnes(double[] a) {
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 1) {
                count++;
            }
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
                y[i] += a[i][j] * x[j];
            }
        }
        return y;
    }
    
    //Multiplies a vector with a vector
    public static double[] multiVect(double[] a, double[] b) {
        
        return null;
    }

    //Prints a formatted 2D array as matrix
    public static void print(double[][] a) {
        NumberFormat formatter = new DecimalFormat("#0.000");
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                System.out.printf("%10s", formatter.format(a[i][j]));
            }
            System.out.println();
        }
    }
}
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package assignment2;

import java.util.Arrays;

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

        // Build the modularity matrix B
        double[][] b = new double[a.length][a.length];
        for (int row = 0; row < a.length; row++) {
            for (int col = 0; col < a.length; col++) {

                /* In each cell, multiply sum of that cell's row in A by
                sum of that cell's column in A. Divide result by M, the
                total sum of A. Subtract this value P from corresponding 
                value in A. */
                double p = 1.0 * ki[row] * kj[col] / M;
                b[row][col] = a[row][col] - p;
            }
        }
        double eValue = eigen(b);
        print(b);

    }

    /* Find the largest positive eigenvalue in matrix B. 
    Create the identity matrix I * lamba. */
    public static double eigen(double[][] b) {
        /* The first bracket is the the length of the row for the matrix.
        The second bracket is the number of columns. The third bracket is
        to hold the values of the ordered pair. */
        double[][][] r = new double[b.length][b.length][2];
        for (int i = 0; i < b.length; i++) {
            for (int j = 0; j < b.length; j++) {
                
            }
        }
        return 0;
    }

    public static int countOnes(double[] a) {
        int count = 0;
        for (int i = 0; i < a.length; i++) {
            if (a[i] == 1) {
                count++;
            }
        }
        return count;
    }

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

    public static double[][] transpose(double[][] a) {
        int m = a.length;
        int n = a[0].length;
        double[][] b = new double[n][m];
        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                b[j][i] = a[i][j];
            }
        }
        return b;
    }

    public static void print(double[][] a) {
        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < a.length; j++) {
                System.out.print(a[i][j] + " ");
            }
            System.out.println();
        }
    }
    
}

class Polynomial {
    
    public double[] coef = {0};
    
    // Construct using a list of coefficients for terms n^0, n^1, n^2... 
    public Polynomial(double[] params) {
        if (coef.length == 0) coef = params;
    }
    
    // Store a number as a polynomial
    public Polynomial(double c) {
        coef[0] = c;
    }
    
    // Order of a polynomial (highest exponent)
    public int order() {
        return coef.length - 1;
    }
    
    // Add 2 polynomials, return sum as new polynomial
    public Polynomial add(Polynomial otherPoly) {
        // Order of sum matches input with highest order.
        int sumOrder = order();
        if (order() > otherPoly.order()) {
            sumOrder = otherPoly.order();
        }
        
    }
}
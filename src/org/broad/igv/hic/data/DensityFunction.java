package org.broad.igv.hic.data;

import org.broad.igv.hic.tools.DensityCalculation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

/**
 * @author Jim Robinson
 * @date 12/5/11
 */
public class DensityFunction {

    private double[] density;
    //private int gridSize;
    private int nPoints;
    private Map<Integer, Double> normFactors;
    private double sum;

    public DensityFunction(int gridSize, double[] densities, Map<Integer, Double> normFactors) {
        //this.gridSize = gridSize;
        this.density = densities;
        this.nPoints = densities.length;
        this.normFactors = normFactors;
        this.sum = 0;
        for (int i=0; i<this.nPoints; i++)
            sum += this.density[i];
    }

    public DensityFunction(DensityCalculation calculation) {
        this(calculation.getGridSize(), calculation.getDensityAvg(), calculation.getNormalizationFactors());
    }

    public double getSum() {
        return sum;
    }
    
    public double getDensity(int chrIdx, int distance) {

       // double normFactor = normFactors.containsKey(chrIdx) ? normFactors.get(chrIdx) : 1.0;
       // normFactor *= .8;
        //   multiplied by this in Python:  (float(matrix.sum())/controlmatrix.sum())
        double normFactor = 1;

        if (distance >= nPoints) {

            return density[nPoints - 1] / normFactor;
        } else {
            return density[distance] / normFactor;
        }
    }

}

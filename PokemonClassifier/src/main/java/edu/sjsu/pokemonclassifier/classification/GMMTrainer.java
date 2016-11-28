/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sjsu.pokemonclassifier.classification;

import org.apache.spark.ml.clustering.GaussianMixture;
import org.apache.spark.ml.clustering.GaussianMixtureModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

/**
 *
 * @author wayne
 */
public class GMMTrainer {
    
    private int numOfmodels;
    GaussianMixtureModel model;

    public GMMTrainer(int numOfmodels) {
        this.numOfmodels = numOfmodels;
    }
 
    public void train(String userDataFile) {
        // Creates a SparkSession
        SparkSession spark = SparkSession
                .builder()
                .appName("GMMTrainer")
                .getOrCreate();

        // Loads data
        Dataset<Row> dataset = spark.read().format("libsvm").load(userDataFile);

        // Trains a GaussianMixture model
        GaussianMixture gmm = new GaussianMixture().setK(numOfmodels);
        model = gmm.fit(dataset);

        spark.stop();
    }

    // Output the parameters of the mixture model
    public GaussianMixtureModel getModel() {
        // ToDo:
        //   Will remove in the future. Instead, returing high-level parameters.
        return model;
    }
}

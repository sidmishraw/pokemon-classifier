/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sjsu.pokemonclassifier.classification;

import org.apache.spark.SparkConf;
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
    SparkConf sparkConf;

    public GMMTrainer(int numOfmodels) {
        this.numOfmodels = numOfmodels;
    }
 
    public void train(String userDataFile) {
        // Creates a SparkSession
        SparkSession spark;

        if (sparkConf == null) {
            spark = SparkSession
                    .builder()
                    .appName("GMMTrainer")
                    .getOrCreate();
        } else {

            spark = SparkSession.builder().config(sparkConf).getOrCreate();
        }

        // Loads data
        Dataset<Row> dataset = spark.read().format("libsvm").load(userDataFile);

        // Trains a GaussianMixture model
        GaussianMixture gmm = new GaussianMixture().setK(numOfmodels);
        model = gmm.fit(dataset);

        System.out.println("DEBUG INFO :: Stopping spark session");

        // spark.stop();
    }

    // Output the parameters of the mixture model
    public GaussianMixtureModel getModel() {
        // ToDo:
        //   Will remove in the future. Instead, returing high-level parameters.
        return model;
    }

    public void setSparkConf(SparkConf sparkConf) {
        this.sparkConf = sparkConf;
    }
}

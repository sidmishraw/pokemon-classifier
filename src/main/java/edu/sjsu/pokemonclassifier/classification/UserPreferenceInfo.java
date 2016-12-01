/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sjsu.pokemonclassifier.classification;

import edu.sjsu.pokemonclassifier.analysis.PokemonAnalysis;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import javafx.util.Pair;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.spark.ml.clustering.GaussianMixtureModel;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;

/**
 *
 * @author wayne
 */
public class UserPreferenceInfo {
    
    @SuppressWarnings("unused")
	private class GmmIndexAndWeight implements Comparable<GmmIndexAndWeight> {
        
        private int index;
        private double weight;
        public GmmIndexAndWeight(int index, double weight) {
            this.index = index;
            this.weight = weight;
        }
        
        @Override
        public int compareTo(GmmIndexAndWeight o) {
            return 0;
        }
    }
    
    private int userID;
    private HashMap<String, Integer> favoriteCountingMap = new HashMap<String, Integer>();
    private int numOfLikedPokemon = 0;
    private static ArrayList<String> strongerCandidates = new ArrayList<String>();
    private String fileName = null;
    private GMMTrainer gmmTrainer = new GMMTrainer(10);
    private JavaSparkContext sparkContext;
    private String baseFilePath;

    /**
     * @param userID
     * @param sparkContext
     * @param conf
     * @param baseFilePath
     */
    public UserPreferenceInfo(int userID, JavaSparkContext sparkContext, SparkConf conf, String baseFilePath) {

        this(userID,baseFilePath);

        this.sparkContext 	= sparkContext;
        this.baseFilePath 	= baseFilePath;

        gmmTrainer.setSparkConf(conf);
    }

    /**
     * Modified by sidmishraw
     * @param userID
     */
    public UserPreferenceInfo(int userID,String baseFilePath) {

        this.setUserID(userID);

        this.baseFilePath = baseFilePath;

        this.fileName = baseFilePath + File.separator + String.format(userID + "_pref.txt");
    }
    
    public void setFavoritePokemon(String name) {
        int count = 1;
        
        if (favoriteCountingMap.containsKey(name))
            count += favoriteCountingMap.get(name);
        favoriteCountingMap.put(name, count);
        
        // Lookup Pokemon Dictionary for retrieving details e.g. Att or Def
        PokemonDict pokDict = PokemonDict.getInstance();
        int att = pokDict.getAttack(name);
        int def = pokDict.getDefense(name);
        int HP = pokDict.getHP(name);
        
        // Write or append the detail to file
        String pokInfo = String.format("%d:%d %d:%d %d:%d", 1, att, 2, def, 3, HP);
        System.out.println("pokeInfo: " + pokInfo);
        writeInfoToFile(pokInfo);
        
        System.out.println("TotalPokem: " + numOfLikedPokemon);
        
        // Train GMM model if needed (e.g. greater than certain threshold)
        // 10 is temp number
        if (numOfLikedPokemon > 10)
            gmmTrainer.train(fileName);
    }
    
    public void setDefenderPokemon(String name) {
        // ToDo: (from first module)
        //   Get all pokemons which are greater or stronger than current defender.
        //   Then put them in strongerCandidates.
        
        List<String> strongerPokemons = PokemonAnalysis.analyzePokemon(name, sparkContext, baseFilePath);

        for (int i = 0; i < strongerPokemons.size(); i++)
            strongerCandidates.add(strongerPokemons.get(i));
    }
    
    public Map<String, Integer> getRecommendPokemon() {
        // API for downard module
        //  Put all strongerCandidates to GMM model
        List<Pair<Integer, Double>> weightPairList = new ArrayList<>();    
        GaussianMixtureModel model = gmmTrainer.getModel();
        for (int j = 0; j < model.getK(); j++) {
            weightPairList.add(Pair.of(j, model.weights()[j]));
        }        
        
        Collections.sort(weightPairList, new Comparator<Pair<Integer, Double>>() {
            @Override
            public int compare(Pair<Integer, Double> o1, Pair<Integer, Double> o2) {
                if (o1.getValue() < o2.getKey())
                    return -1;
                else if (o1.getValue().equals(o2.getValue()))
                    return 0;
                else
                    return 1;
            }
        });        
        
        // Get top-5
        // 5 is temp number
        // <String, Interger> -> <PokemonName, Rank#>
        HashMap<String, Integer> rankedPokemon 				= new HashMap<String, Integer>();
        HashMap<Integer, String> strongerCandidatesMap 		= new HashMap<Integer, String>();

        for ( String strongerCandidate : strongerCandidates ) {

        	strongerCandidatesMap.put(strongerCandidates.indexOf(strongerCandidate), strongerCandidate);
        }

//        for (int i = 0; i < strongerCandidates.size(); i++) {
//
//        	strongerCandidatesMap.put(i, strongerCandidates.get(i).toLowerCase());
//        }

        // modified by sidmishraw for getting top 10 pokemons rather than 5
        int totalClusters = Math.min(model.getK(), 10);

        int rank = 1;

        for (int i = totalClusters - 1; i >= 0; i--) {
            int modelIdx = weightPairList.get(i).getKey();
            double[] meanVector = model.gaussians()[modelIdx].mean().toArray();
            
            double att = meanVector[0];
            double def = meanVector[1];
            double hp = meanVector[2];
            
            double minDist = Double.MAX_VALUE;
            int minIdx = 0;
            String bestFitName = null;

            for (int j = 0; j < strongerCandidatesMap.size(); j++) {

                String name = strongerCandidatesMap.get(j);

                if (name == null) {

                	continue;
                }

                //name = name.toLowerCase();
                System.out.println("HARMLESS:::: name = " + name);
                System.out.println("HARMLESS:::: att2 = " + PokemonDict.getInstance().getAttack(name));
                System.out.println("HARMLESS:::: def2 = " + PokemonDict.getInstance().getDefense(name));
                System.out.println("HARMLESS:::: hp2 = " + PokemonDict.getInstance().getHP(name));

                int att2 	= PokemonDict.getInstance().getAttack(name);
                int def2 	= PokemonDict.getInstance().getDefense(name);
                int hp2 	= PokemonDict.getInstance().getHP(name);

                double dist = Math.sqrt((att - att2) * (att - att2) +
                                        (def - def2) * (def - def2) +
                                        (hp - hp2) * (hp - hp2));
                if (dist < minDist) {
                    minDist = dist;
                    minIdx = j;
                    bestFitName = name;
                }
            }
            
            strongerCandidatesMap.remove(minIdx);
            rankedPokemon.put(bestFitName, rank);
            rank++;
        }
        
        return rankedPokemon;
    }    

    /**
     * Modified by sidmishraw -- closed reading and writing streams
     * @param dataRow
     */
    private void writeInfoToFile(String dataRow) {

    	BufferedWriter bufferedWriter 				= null;
    	ReversedLinesFileReader rLinesFileReader 	= null;

        try {

            int labelIndex = 0;

            File userStoredData = new File(fileName);

            System.out.println("FIle: " + userStoredData.getAbsolutePath());

            if (!userStoredData.exists()) {

                userStoredData.createNewFile();

                System.out.println("DEBUG INFO :: Created file " +  fileName);
            } else {

                rLinesFileReader 							= new ReversedLinesFileReader(userStoredData);

                String bottomLine 							= rLinesFileReader.readLine();

                System.out.println("DEBUG INFO :: line read " +  bottomLine);

                String[] tokens 							= bottomLine.split(" ");

                labelIndex 									= Integer.parseInt(tokens[0]) + 1;
            }

            numOfLikedPokemon = labelIndex + 1;

            // ToDo:
            //   Set a limit of file writing.
            //   Need to write in specific line
            
            String line 			= String.format("%d %s", labelIndex, dataRow);

            bufferedWriter 			= new BufferedWriter(new FileWriter(userStoredData, true));

            bufferedWriter.write(line + "\n");
        } catch(IOException e) {
            System.out.println("COULD NOT WRITE TO FILE!!");
        } finally {

        	if ( null != rLinesFileReader) {

        		try {

					rLinesFileReader.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
        	}

        	if ( null != bufferedWriter) {

        		try {

					bufferedWriter.close();
				} catch (IOException e) {

					e.printStackTrace();
				}
        	}
        }
    }

	/**
	 * @return the userID
	 */
	public int getUserID() {
		return userID;
	}

	/**
	 * @param userID the userID to set
	 */
	public void setUserID(int userID) {
		this.userID = userID;
	}
}

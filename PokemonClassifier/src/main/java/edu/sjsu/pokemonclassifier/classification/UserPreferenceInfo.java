/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sjsu.pokemonclassifier.classification;

import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
//import javafx.util.Pair;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.apache.spark.ml.clustering.GaussianMixtureModel;
import org.apache.commons.lang3.tuple.Pair;

/**
 *
 * @author wayne
 */
public class UserPreferenceInfo {
    
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
    private ArrayList<String> strongerCandidates = new ArrayList<String>();
    private String fileName = null;
    private GMMTrainer gmmTrainer = new GMMTrainer(10);

    public UserPreferenceInfo(int userID) {
        this.userID = userID;
        fileName = String.format(userID + "_pref.txt");
        
        File userStoredData = new File(fileName);
        if (!userStoredData.exists()) {
            try {
                userStoredData.createNewFile();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
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
        
        // temp datas for testing until getting first module
        strongerCandidates.add("Charmeleon");
        strongerCandidates.add("Pidgeot");
        strongerCandidates.add("Ekans");
        strongerCandidates.add("Nidorino");
        strongerCandidates.add("Oddish");
        strongerCandidates.add("Abra");
        strongerCandidates.add("Golem");
        strongerCandidates.add("Koffing");
        strongerCandidates.add("Flareon");
        strongerCandidates.add("Mewtwo");
        strongerCandidates.add("Venonat");
    }
    
    public HashMap<Integer, String> getRecommendPokemon() {
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
        HashMap<Integer, String> rankedPokemon = new HashMap<Integer, String>();
        HashMap<Integer, String> strongerCandidatesMap = new HashMap<Integer, String>();
        for (int i = 0; i < strongerCandidates.size(); i++)
            strongerCandidatesMap.put(i, strongerCandidates.get(i));
        
        int totalClusters = Math.min(model.getK(), 5);
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
                if (name == null)
                    continue;

                int att2 = PokemonDict.getInstance().getAttack(name);
                int def2 = PokemonDict.getInstance().getDefense(name);
                int hp2 = PokemonDict.getInstance().getHP(name);

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
            rankedPokemon.put(rank, bestFitName);
            rank++;
        }
        
        return rankedPokemon;
    }    
    
    private void writeInfoToFile(String dataRow) {
        try {
            int labelIndex = 0;
            File userStoredData = new File(fileName);
            if (!userStoredData.exists()) {
                userStoredData.createNewFile();
            }
            else {
                ReversedLinesFileReader rLinesFileReader = new ReversedLinesFileReader(userStoredData);
                String bottomLine = rLinesFileReader.readLine();
                String[] tokens = bottomLine.split(" ");
                labelIndex = Integer.parseInt(tokens[0]) + 1;
            }
            numOfLikedPokemon = labelIndex + 1;
            
            // ToDo:
            //   Set a limit of file writing.
            //   Need to write in specific line
            
            String line = String.format("%d %s", labelIndex, dataRow);
            FileWriter fileWriter = new FileWriter(userStoredData, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(line + "\n");
            bufferedWriter.close();
        } catch(IOException e) {
            System.out.println("COULD NOT WRITE TO FILE!!");
        }               
    }
}

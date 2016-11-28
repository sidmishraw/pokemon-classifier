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
import java.util.HashMap;

import org.apache.commons.io.input.ReversedLinesFileReader;

/**
 *
 * @author wayne
 */
public class UserPreferenceInfo {
    
    private int userID;
    private HashMap<String, Integer> favoriteCountingMap = new HashMap<String, Integer>();
    private ArrayList<String> strongerCandidates = new ArrayList<String>();
    private String fileName = null;
    private GMMTrainer gmmTrainer = new GMMTrainer(10);

    public UserPreferenceInfo(int userID) {
        this.userID = userID;   
    }
    
    public void setFavoritePokemon(String name) {
        int count = 1;
        
        if (favoriteCountingMap.containsKey(name))
            count += favoriteCountingMap.get(name);
        favoriteCountingMap.put(name, count);
        
        // ToDo:
        //   Lookup Pokemon Dictionary for retrieving details e.g. Att or Def
        
        // ToDo:
        //   Write or append the detail to file
        
        // ToDo:
        //   Train GMM model if needed (e.g. greater than certain threshold)
    }
    
    public void setDefenderPokemon(String name) {
        // ToDo:
        //   Get all pokemons which are greater or stronger than current defender.
        //   Then put them in strongerCandidates.
    }
    
    public HashMap<Integer, String> getRecommendPokemon() {
        // ToDo:
        //   Put strongerCandidates to GMM model
        
        return null;
    }    
    
    private void WriteInfoToFile(String dataRow) {
        try {
            int labelIndex = 0;
            fileName = String.format(userID + "_pref.txt");
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
            
            FileWriter fileWriter = new FileWriter(userStoredData, true);
            BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
            bufferedWriter.write(dataRow + "\n");
            bufferedWriter.close();
        } catch(IOException e) {
            System.out.println("COULD NOT WRITE TO FILE!!");
        }               
    }
}

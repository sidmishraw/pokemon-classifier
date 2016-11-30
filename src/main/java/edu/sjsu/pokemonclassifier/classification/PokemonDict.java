/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sjsu.pokemonclassifier.classification;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author wayne
 */
public class PokemonDict {
    
    public class PokemonInfo {
        private final String name;
        private final int attack;
        private final int defense;
        private final int HP;
        // Could be more properties
        
        public PokemonInfo(String name, int attack, int defense, int HP) {
            this.name = name;
            this.attack = attack;
            this.defense = defense;
            this.HP = HP;
        }

        public String getName() {
            return name;
        }

        public int getAttack() {
            return attack;
        }

        public int getDefense() {
            return defense;
        }
   
        public int getHP() {
            return HP;
        }    
    }
        
    private static PokemonDict instance = null;
    private final String file = "Pokemon.csv";
    private HashMap<String, PokemonInfo> PokemonInfos = new HashMap<String, PokemonInfo>();

    public PokemonDict() {
        String csvFile = file;
        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";
        
        try {
            br = new BufferedReader(new FileReader(csvFile));
            while ((line = br.readLine()) != null) {
                // use comma as separator
                String[] dataRow = line.split(cvsSplitBy);
                if (dataRow[1].equals("Name"))
                    continue;
                
                String name = dataRow[1];
                int HP = Integer.parseInt(dataRow[5]);
                int att = Integer.parseInt(dataRow[6]);
                int def = Integer.parseInt(dataRow[7]);
                
                System.out.println("Name = " + name + "At");
                
                PokemonInfo pokemon = new PokemonInfo(name, att, def, HP);
                PokemonInfos.put(name, pokemon);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }
    
    public static PokemonDict getInstance() {
        if(instance == null) {
            instance = new PokemonDict();
        }
        return instance;
    }

    public int getAttack(String name) {
        return PokemonInfos.get(name).getAttack();
    }
    
    public int getDefense(String name) {
        return PokemonInfos.get(name).getDefense();
    }
    
    public int getHP(String name) {
        return PokemonInfos.get(name).getHP();
    }
}

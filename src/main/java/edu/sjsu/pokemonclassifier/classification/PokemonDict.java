/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package edu.sjsu.pokemonclassifier.classification;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

/**
 *
 * @author wayne
 */
public class PokemonDict {
    
    public static class PokemonInfo {

        private final String name;
        private final int attack;
        private final int defense;
        private final int HP;
        // Could be more properties

        //added by sidmishraw for storing the pokemon serial nbr
        private int pokemonSerialNbr;

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

        // added by sidmishraw for getting and setting pokemon serial nbr
        public void setPokemonSerialNbr(int nbr) {

        	this.pokemonSerialNbr = nbr;
        }

        public int getPokemonSerialNbr() {

        	return this.pokemonSerialNbr;
        }
    }

    private static PokemonDict instance = null;

    // modified by sidmishraw -- made file static field
    private static String file = "Pokemon.csv";

	private HashMap<String, PokemonInfo> PokemonInfos;

    private PokemonDict() {

    	PokemonInfos =  new HashMap<String, PokemonInfo>();

        String csvFile = file;

        BufferedReader br = null;
        String line = "";
        String cvsSplitBy = ",";

        try {

            br = new BufferedReader(new FileReader(csvFile));

            while ((line = br.readLine()) != null) {

                // use comma as separator
            	System.out.println("DEBUG INFO :::::  datarow = " + line);

                String[] dataRow = line.split(cvsSplitBy);
                if (dataRow[1].equals("Name"))
                    continue;

                String name = dataRow[1];
                int HP = Integer.parseInt(dataRow[5]);
                int att = Integer.parseInt(dataRow[6]);
                int def = Integer.parseInt(dataRow[7]);

                // added by sidmishraw to read in pokemonNbr
                int pokemonNbr = Integer.parseInt(dataRow[0]);

                System.out.println("Name = " + name + "At");

                PokemonInfo pokemon = new PokemonInfo(name, att, def, HP);

                //added by sidmishraw for setting pokemonNbr
                pokemon.setPokemonSerialNbr(pokemonNbr);

                PokemonInfos.put(name, pokemon);
            }
        } catch (FileNotFoundException ex) {
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    /**
     * Added by sidmishraw for setting base file path to read in PokemonGO.csv
     * @param baseFilePath
     */
    public static void setBaseFilePath(String baseFilePath) {

    	file	= baseFilePath + File.separator + file;
    }

    public static PokemonDict getInstance() {

        if(instance == null) {

            instance = new PokemonDict();
        }

        return instance;
    }

    /**
     * 
     * @param name
     * @return int
     */
    public int getAttack(String name) {

    	PokemonInfo pokemonInfo = PokemonInfos.get(name);

    	if ( null != pokemonInfo ) {

    		return pokemonInfo.getAttack();
    	}

        return 0;
    }

    public int getDefense(String name) {

    	PokemonInfo pokemonInfo = PokemonInfos.get(name);

    	if ( null != pokemonInfo ) {

    		return pokemonInfo.getDefense();
    	}

        return 0;
    }

    public int getHP(String name) {

    	PokemonInfo pokemonInfo = PokemonInfos.get(name);

    	if ( null != pokemonInfo ) {

    		return pokemonInfo.getHP();
    	}

        return 0;
    }

    // added by sidmishraw for fetching pokemon serialNbr from pokemonInfo
    public int getPokemonSerialNbr(String pokemonName) {

    	PokemonInfo pokemonInfo = PokemonInfos.get(pokemonName);

    	if ( null != pokemonInfo ) {

    		return pokemonInfo.getPokemonSerialNbr();
    	}

    	return 0;
    }
}

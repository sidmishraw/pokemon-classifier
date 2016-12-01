/**
 * This project is used to
 */
package edu.sjsu.pokemonclassifier.analysis;

/**
 * @author Joshua Zheng
 */

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PokemonAnalysis {

    private static String defender = "";

    private static double[][] pokemonValue 			= new double[][]{
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 0.5, 0, 1, 1, 0.5, 1},
            {1, 0.5, 0.5, 1, 2, 2, 1, 1, 1, 1, 1, 2, 0.5, 1, 0.5, 1, 2, 1},
            {1, 2, 0.5, 1, 0.5, 1, 1, 1, 2, 1, 1, 1, 2, 1, 0.5, 1, 1, 1},
            {1, 1, 2, 0.5, 0.5, 1, 1, 1, 0, 2, 1, 1, 1, 1, 0.5, 1, 1, 1},
            {1, 0.5, 2, 1, 0.5, 1, 1, 0.5, 2, 0.5, 1, 0.5, 2, 1, 0.5, 1, 0.5, 1},
            {1, 0.5, 0.5, 1, 2, 0.5, 1, 1, 2, 2, 1, 1, 1, 1, 2, 1, 0.5, 1},
            {2, 1, 1, 1, 1, 2, 1, 0.5, 1, 0.5, 0.5, 0.5, 2, 0, 1, 2, 2, 0.5},
            {1, 1, 1, 1, 2, 1, 1, 0.5, 0.5, 1, 1, 1, 0.5, 0.5, 1, 1, 0, 2},
            {1, 2, 1, 2, 0.5, 1, 1, 2, 1, 0, 1, 0.5, 2, 1, 1, 1, 2, 1},
            {1, 1, 1, 0.5, 2, 1, 2, 1, 1, 1, 1, 2, 0.5, 1, 1, 1, 0.5, 1},
            {1, 1, 1, 1, 1, 1, 2, 2, 1, 1, 0.5, 1, 1, 1, 1, 0, 0.5, 1},
            {1, 0.5, 1, 1, 2, 1, 0.5, 0.5, 1, 0.5, 2, 1, 1, 0.5, 1, 2, 0.5, 0.5},
            {1, 2, 1, 1, 1, 2, 0.5, 1, 0.5, 2, 1, 2, 1, 1, 1, 1, 0.5, 1},
            {0, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 1, 2, 1, 0.5, 1, 1},
            {1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 1, 2, 1, 0.5, 0},
            {1, 1, 1, 1, 1, 1, 0.5, 1, 1, 1, 2, 1, 1, 2, 1, 0.5, 1, 0.5},
            {1, 0.5, 0.5, 0.5, 1, 2, 1, 1, 1, 1, 1, 1, 2, 1, 1, 1, 0.5, 2},
            {1, 0.5, 1, 1, 1, 1, 2, 0.5, 1, 1, 1, 1, 1, 1, 2, 2, 0.5, 1}
    };

    /**
     * Calculates the pokemon's attack power against the defending pokemon.
     * 
     * @param defender
     * @param PA
     * @param SA
     * @param SP
     * @param type1
     * @param type2
     * @return double
     */
    private static double pokeHash(String[] defender, int PA, int SA, int SP, String type1, String type2) {

    	double pokeMulti 		= pokeMultiplier(defender, type1, type2);
        double physicalAtk 		= (PA * pokeMulti) - toInt(defender[8]);
        double specialAtk 		= (SA * pokeMulti) - toInt(defender[10]);
        double Atk 				= (physicalAtk > specialAtk) ? physicalAtk : specialAtk;
        Atk 					= (SP > toInt(defender[11])) ? Atk : ((int) Atk * .8);

        return Atk;
    }

    private static int typeConversion(String pokeType) {

        String[] pokemonTypes 		= { "Normal", "Fire", "Water", "Electric", "Grass", "Ice"
        		, "Fighting", "Poison", "Ground", "Flying", "Psychic", "Bug", "Rock"
        		, "Ghost", "Dragon", "Dark", "Steel", "Fairy" };

        for ( int i = 0; i < pokemonTypes.length; i ++ ) {

            if (pokemonTypes[i].equalsIgnoreCase(pokeType)) {

            	System.out.println(pokeType + "   " + i);
            	return i;
            }
        }

        return -1;
    }

    private static double pokeMultiplier(String[] defenderStats, String pokeType1, String pokeType2) {

    	System.out.println("DEBUG INFO :::: " + defenderStats[2] + "   "  
    						+ defenderStats[3] + "   " + pokeType1 + "    " + pokeType2);

        double multiplier1 = 1, multiplier2 = 1, multiplier3 = 1, multiplier4 = 1;

        int attackerType1 = typeConversion(pokeType1);
        int attackerType2 = typeConversion(pokeType2);
        int defenderType1 = typeConversion(defenderStats[2]);
        int defenderType2 = typeConversion(defenderStats[3]);

        multiplier1 		= pokemonValue[attackerType1][defenderType1];

        if (defenderType2 != -1) {

        	multiplier2 	= pokemonValue[attackerType1][defenderType2];
        }

        if (attackerType2 != -1) {

        	multiplier3 = pokemonValue[attackerType2][defenderType1];
        }

        if ( attackerType2 != -1 && defenderType2 != -1 ) {
        	
        	multiplier4 = pokemonValue[attackerType2][defenderType1];
        }

        return multiplier1 * multiplier2 * multiplier3 * multiplier4;
    }

    public static int toInt(String aString) {

        return Integer.parseInt(aString);
    }

    /**
     * Modified by sidmishraw
     * 
     * @param defenderPokemon
     * @param sparkContext
     * @param baseFilePath
     * @return List<String> -- returns the list of pokemons that can defeat the defending pokemon
     */
    public static List<String> analyzePokemon(String defenderPokemon, JavaSparkContext sparkContext, String baseFilePath) {

        /**
         * Used to extract the defending pokemon's stats
         */
        try ( BufferedReader br = new BufferedReader(new FileReader(baseFilePath + File.separator + "Pokemon.csv")) ) {

        	System.out.println("DEBUG INFO:: Reading from " + baseFilePath + File.separator + "Pokemon.csv" );

            String sCurrentLine = "";

            while ((sCurrentLine = br.readLine()) != null) {

                if (!sCurrentLine.contains("Mega") && sCurrentLine.contains(defenderPokemon)) {

                    defender = sCurrentLine;
                }
            }
        } catch (IOException e) {

            e.printStackTrace();
        }

        JavaRDD<String> data 				= sparkContext.textFile(baseFilePath + File.separator + "Pokemon.csv");

        JavaRDD<PokemonInfo> pokemonData 	= data.map(

            new Function<String, PokemonInfo>() {

                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public PokemonInfo call(String s) throws Exception {

                    if (s.equalsIgnoreCase("#,Name,Type 1,Type 2,Total,HP,Attack,Defense,Sp. Atk,Sp. Def,Speed,Generation,Legendary")) {

                        return new PokemonInfo("000", "MissingNo.", "normal", " ", "136", "6", "29");
                    }

                    String[] row = s.split(",");

                    return new PokemonInfo(row[0], row[1], row[2], row[3], row[6], row[8], row[10]);
                }
            }
        );

        System.out.println("DEBUG INFO :::: PokemonAnalysis -- defender = " + defender);

        final String[] defenderStat = defender.split(",");

        PokemonInfo.setDefendingPokemonAttack(pokeHash( defenderStat, Integer.parseInt(defenderStat[6])
                    		, Integer.parseInt(defenderStat[8]), Integer.parseInt(defenderStat[10]), defenderStat[2]
                    		, defenderStat[3] ));

        JavaRDD<PokemonInfo> pokePower = pokemonData.map(

            new Function<PokemonInfo, PokemonInfo>() {

                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
                public PokemonInfo call(PokemonInfo pokemonInfo) throws Exception {

                    double pokemonAttackPower = pokeHash( defenderStat, pokemonInfo.getPokemonPA()
                    		, pokemonInfo.getPokemonSA(), pokemonInfo.getPokemonSp(), pokemonInfo.getType1()
                    		, pokemonInfo.getType2() );

                    if ( defenderPokemon.equalsIgnoreCase(pokemonInfo.getPokemonName()) ) {

                    	PokemonInfo.setDefendingPokemonAttack(pokemonAttackPower);
                    }

                    PokemonInfo pokemonInfo2 = new PokemonInfo(pokemonInfo.getPokemonName() + "," + pokemonAttackPower);

                    pokemonInfo2.setPokemonName(pokemonInfo.getPokemonName());

                    return pokemonInfo2;
                }
            }
        );

        final Map<String, Integer> pokemonPowerMap = new HashMap<>();

        JavaRDD<PokemonInfo> getStrongerPokemon = pokePower.filter(

            new Function<PokemonInfo, Boolean>() {

                /**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
                public Boolean call(PokemonInfo pokemonInfo) throws Exception {

                    String[] data = pokemonInfo.getData().split(",");

                    if (Double.parseDouble(data[1]) > PokemonInfo.getDefendingPokemonAttack()) {

                        pokemonPowerMap.put(data[0], (int) Double.parseDouble(data[1]));

                        return true;
                    } else {

                    	return false;
                    }
                }
            }
        );

        List<PokemonInfo> strongPokemonInfos	= getStrongerPokemon.collect();
        List<String> strongPokemons				= new ArrayList<>();

        for ( PokemonInfo pokemonInfo : strongPokemonInfos ) {

        	System.out.println("DEBUG INFO :::: strong pokemon " + pokemonInfo.getPokemonName() );

        	strongPokemons.add(pokemonInfo.getPokemonName());
        }

        return strongPokemons;
    }
}

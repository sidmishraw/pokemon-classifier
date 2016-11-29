/**
 * This project is used to
 */
package edu.sjsu.pokemonclassifier.analysis;

/**
 * @author Joshua Zheng
 */

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.rdd.RDD;
import org.apache.spark.sql.SparkSession;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class PokemonAnalysis {
    private static boolean choice = true;
    private static String defender;
    private static boolean stronger = false;
    double[][] pokemonValue = new double[][]{
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

    public static double pokeHash(String[] defender, int PA, int SA, int SP, String type1, String type2) {
        int physicalAtk = PA - toInt(defender[8]);
        int specialAtk = SA - toInt(defender[10]);
        double Atk = (physicalAtk > specialAtk) ? physicalAtk : specialAtk;
        Atk = (SP > toInt(defender[11])) ? Atk : ((int) Atk * .8);
        return Atk * pokeMultiplier(type1, type2);

    }

    public int typeConversion(String pokeType) {
        String[] pokemonTypes = {"Normal", "Fire", "Water", "Electric", "Grass", "Ice", "Fighting", "Poison", "Ground", "Flying", "Psychic", "Bug", "Rock", "Ghost", "Dragon", "Dark", "Steel", "Fairy" };
        for (int i = 0; i < pokemonTypes.length; i) {
            if (pokemonTypes[i] == pokeType) return i;
        }

    }

    public static double pokeMultiplier(String pokeType1, String pokeType2) {
        double multiplier1, multiplier2, multiplier3, multiplier4 = 1;
        double multiplier1 = pokemonValue[typeConversion(pokeType1)][typeConversion(defenderStats[3])];
        if (defenderStats[4] != null)
            multiplier2 = pokemonValue[typeConversion(pokeType1)][typeConversion(defenderStats[4]]);
        if (pokeType2 != null) multiplier3 = pokemonValue[typeConversion(pokeType2)][typeConversion(defenderStats[3]]);
        if (pokeType2 != null && defenderStats[4] != null)
            multiplier3 = pokemonValue[typeConversion(pokeType2)][typeConversion(defenderStats[4]]);
        if (multiplier1 * multiplier2 * multiplier3 * multiplier4 >= 1) stronger = true;
        return multiplier1 * multiplier2 * multiplier3 * multiplier4;


    }

    public int toInt(String aString) {
        return Integer.parseInt(aString);
    }


    public static void main(String[] args) {
        /**
         * Used to pick extract the defending pokemon's stats
         */
        boolean firstTime = true;
        while (choice) {
            if (firstTime) System.out.print("What pokemon are you trying to beat?");
            else System.out.print("Try again, Error: could not figure out which pokemon you wanted.");
            firstTime = false;
            Scanner input = new Scanner(System.in);
            String pokemonChoice = input.next().toLowerCase();
            BufferedReader br = null;
            try {
                String sCurrentLine;
                br = new BufferedReader(new FileReader("src\\main\\resources\\Pokemon.csv"));

                while ((sCurrentLine = br.readLine()) != null) {
                    if (!sCurrentLine.contains("Mega") && sCurrentLine.toLowerCase().contains(pokemonChoice)) {
                        defender = sCurrentLine;
                        choice = false;
                    } else ;
                }

                System.out.print(defender);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (br != null) br.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }

        }

        SparkConf sparkConf = new SparkConf().setAppName("PokemonAnalysis");
        JavaSparkContext sparkContext = new JavaSparkContext(sparkConf);
        SparkSession sparkSQLContext = new SparkSession(sparkContext.sc());
        JavaRDD<String> data = sparkContext.textFile("src\\main\\resources\\Pokemon.csv");
        JavaRDD<PokemonInfo> pokemonData = data.map(
                new Function<String, PokemonInfo>() {

                    public PokemonInfo call(String s) throws Exception {
                        if (s.equalsIgnoreCase("#,Name,Type 1,Type 2,Total,HP,Attack,Defense,Sp. Atk,Sp. Def,Speed,Generation,Legendary")) {
                            return new PokemonInfo("000", "MissingNo.", "normal", " ", "136", "6", "29");
                        }
                        String[] row = s.split(",");
                        return new PokemonInfo(row[0], row[1], row[2], row[3], row[6], row[8], row[10]);

                    }


                }

        );
        final String[] defenderStat = defender.split(",");

        JavaRDD<PokemonInfo> pokePower = pokemonData.map(
                new Function<PokemonInfo, String>() {
                    @Override
                    public PokemonInfo call(PokemonInfo pokemonInfo) throws Exception {
                        double pokemonAttackPower = pokeHash(defenderStat, pokemonInfo.getPokemonPA(), pokemonInfo.getPokemonSA(), pokemonInfo.getPokemonSp(), pokemonInfo.getType1(), pokemonInfo.getType2());
                        return new PokemonInfo(pokemonInfo.getPokemonName() + "," + pokemonAttackPower);
                    }
                }


        );
        final Map<String, Integer> pokemonPowerMap = new HashMap<>();

        JavaRDD<PokemonInfo> getStrongerPokemon = pokePower.filter(
                new Function<PokemonInfo, Boolean>() {
                    @Override
                    public Boolean call(PokemonInfo pokemonInfo) throws Exception {
                        String[] data = pokemonInfo.getData().split(",");
                        if (Integer.parseInt(data[1]) > 0 && stronger) {
                            pokemonPowerMap.put(data[0], data[1]);
                            return true;
                        } else return false;
                    }
                }


        );


        sparkContext.close();


    }
}

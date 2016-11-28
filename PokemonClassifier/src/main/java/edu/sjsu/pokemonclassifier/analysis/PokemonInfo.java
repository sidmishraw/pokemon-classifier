package edu.sjsu.pokemonclassifier.analysis;

import java.io.Serializable;

/**
 * Created by Joshua on 11/27/2016.
 */
public class PokemonInfo implements Serializable {
    private int pokemonNumber;
    private String pokemonName;
    private String type1;
    private String type2;
    private int pokemonPA;
    private int pokemonSA;
    private int pokemonSp;
    private String data;


    public PokemonInfo(){

    }

    public PokemonInfo(String pokemonNumber, String pokemonName, String type1, String type2, String pokemonPA, String pokemonSA, String pokemonSp){
        super();
        this.pokemonNumber = toInt(pokemonNumber);
        this.pokemonName = pokemonName;
        this.type1=type1;
        this.type2 = type2;
        this.pokemonPA = toInt(pokemonPA);
        this.pokemonSA = toInt(pokemonSA);
        this.pokemonSp = toInt(pokemonSp);


    }
    public PokemonInfo(String data){
        super();
        this.data = data;
    }
    public String getData(){
        return data;
    }
    public int getPokemonNumber() {
        return pokemonNumber;
    }

    public String getPokemonName() {
        return pokemonName;
    }

    public String getType1() {
        return type1;
    }

    public String getType2() {
        return type2;
    }

    public int getPokemonPA() {
        return pokemonPA;
    }

    public int getPokemonSA() {
        return pokemonSA;
    }

    public int getPokemonSp() {
        return pokemonSp;
    }
    public int toInt(String aString){
        return Integer.parseInt(aString);
    }


    public String toString(){
      return "Number:"+getPokemonNumber()+",Pokemon:"+getPokemonName();
  }
}

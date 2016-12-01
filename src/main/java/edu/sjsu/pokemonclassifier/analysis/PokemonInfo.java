package edu.sjsu.pokemonclassifier.analysis;

import java.io.Serializable;

/**
 * Created by Joshua on 11/27/2016.
 */
public class PokemonInfo implements Serializable {
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int pokemonNumber;
    private String pokemonName;
    private String type1;
    private String type2;
    private int pokemonPA;
    private int pokemonSA;
    private int pokemonSp;
    private String data;

    // added by sidmishraw to store the defender attack stats
    private static double defendingPokemonAttack = 0.0;

    public PokemonInfo() {}

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

    
    /**
	 * @param pokemonNumber the pokemonNumber to set
	 */
	public void setPokemonNumber(int pokemonNumber) {
		this.pokemonNumber = pokemonNumber;
	}

	/**
	 * @param pokemonName the pokemonName to set
	 */
	public void setPokemonName(String pokemonName) {
		this.pokemonName = pokemonName;
	}

	/**
	 * @param type1 the type1 to set
	 */
	public void setType1(String type1) {
		this.type1 = type1;
	}

	/**
	 * @param type2 the type2 to set
	 */
	public void setType2(String type2) {
		this.type2 = type2;
	}

	/**
	 * @param pokemonPA the pokemonPA to set
	 */
	public void setPokemonPA(int pokemonPA) {
		this.pokemonPA = pokemonPA;
	}

	/**
	 * @param pokemonSA the pokemonSA to set
	 */
	public void setPokemonSA(int pokemonSA) {
		this.pokemonSA = pokemonSA;
	}

	/**
	 * @param pokemonSp the pokemonSp to set
	 */
	public void setPokemonSp(int pokemonSp) {
		this.pokemonSp = pokemonSp;
	}

	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}

	public String toString() {
      return "Number:"+getPokemonNumber()+",Pokemon:"+getPokemonName();
  }

	/**
	 * @return the defendingPokemonAttack
	 */
	public static double getDefendingPokemonAttack() {
		return defendingPokemonAttack;
	}

	/**
	 * @param defendingPokemonAttack the defendingPokemonAttack to set
	 */
	public static void setDefendingPokemonAttack(double defendingPokemonAttack) {
		PokemonInfo.defendingPokemonAttack = defendingPokemonAttack;
	}
}

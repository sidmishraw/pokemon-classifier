/**
 * 
 */
package edu.sjsu.pokemonclassifier.classification;

import java.util.HashMap;

/**
 * @author wayne
 *
 */
public class DummyClassClassification {
    
      public static void main(String[] args) {
     
      System.out.println("Hello Pokemon Classificaiton");
      
      PokemonDict pDict = PokemonDict.getInstance();
      
      UserPreferenceInfo user = new UserPreferenceInfo(001);
      user.setFavoritePokemon("Bulbasaur");
      user.setFavoritePokemon("Charmeleon");
      user.setFavoritePokemon("Pidgeot");
      user.setFavoritePokemon("Rattata");
      user.setFavoritePokemon("Rattata");
      user.setFavoritePokemon("Rattata");
      user.setFavoritePokemon("Rattata");

      user.setDefenderPokemon("Bulbasaur");

      HashMap<Integer, String> rTable = user.getRecommendPokemon();
      for (int i = 0; i < rTable.size(); i++) {
          System.out.println("Rank: " + i + ", Name: " + rTable.get(i + 1));
      }
      
      System.out.println("Hello Pokemon Classificaiton Done");
  }
    
    
    
}

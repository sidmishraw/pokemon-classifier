/**
 * 
 */
package edu.sjsu.pokemonclassifier.spawnrecommender;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaPairRDD;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.Function2;
import org.apache.spark.api.java.function.PairFunction;

import edu.sjsu.pokemonclassifier.classification.PokemonDict;
import edu.sjsu.pokemonclassifier.classification.UserPreferenceInfo;

// Used to fix the serialization error when 
import static edu.sjsu.pokemonclassifier.spawnrecommender.SerializableComparator.serialize; 

import scala.Tuple2;

/**
 * @author sidmishraw
 *
 */
public class DummyPokemonSpawnRecommender implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private static DummyPokemonSpawnRecommender dummyPokemonSpawnRecommender = null;

	private DummyPokemonSpawnRecommender() {}

	/**
	 * Singleton-maker - fetches the same instance
	 * @return DummyPokemonSpawnRecommender
	 */
	public static DummyPokemonSpawnRecommender getInstance() {

		if ( null == dummyPokemonSpawnRecommender ) {

			return new DummyPokemonSpawnRecommender();
		}

		return dummyPokemonSpawnRecommender;
	}

	// my lat and long coorinate, used to compute the distance
	private static double mylat 	= 0, mylong 	= 0;
	private static String defendingPokemonName			= "";
	private static Map<String,Integer> pokemonRankMap 	= new HashMap<>();
	private static String pokemonSpawnFilePath			= "pokemon-spawns.csv";
	private static String baseFilePath					= "";

	/**
	 * Takes the souce and destination lat,long coordinates and returns the distance
	 * between them in meters
	 * @param lat1
	 * @param lng1
	 * @param lat2
	 * @param lng2
	 * @return double
	 */
	// lat,long - distance calculation in meters
	// Formula from http://www.movable-type.co.uk/scripts/latlong.html
	// and http://stackoverflow.com/questions/837872/calculate-distance-in-meters-when-you-know-longitude-and-latitude-in-java
	private static double distFrom(double lat1, double lng1, double lat2, double lng2) {

	    double earthRadius 		= 6371000; //meters
	    double dLat 			= Math.toRadians(lat2-lat1);
	    double dLng 			= Math.toRadians(lng2-lng1);

	    double a 				= Math.sin(dLat/2) * Math.sin(dLat/2) +
	               Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
	               Math.sin(dLng/2) * Math.sin(dLng/2);

	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

	    double dist = (double) (earthRadius * c);

	    return dist;
	}

	/**
	 * Converts the string into title case
	 * @param string
	 * @return String in title casing lie Myname
	 */
	private static String titleCase(String string) {

		char [] charArray = string.toCharArray();

		if ( Character.isUpperCase(charArray[0]) ) {
	
			return string;
		} else {

			charArray[0] = Character.toUpperCase(charArray[0]);

			System.out.println(String.valueOf(charArray));

			return String.valueOf(charArray);
		}
	}

	// trying out the recommendation for spawn
	@SuppressWarnings("unused")
	public static void main(String[] args) {

		System.out.println("Please enter defending pokemon name, your latitude and "
					+ "longitude coordinates and path to the project source folder SEPARATED BY SPACES.");

		try( BufferedReader br = new BufferedReader(new InputStreamReader(System.in)) ) {

			String [] strCoordinates = br.readLine().split("\\s",4);

			if (strCoordinates.length < 4) {

				System.out.println("Insufficient parameters, Exiting ...");
				System.exit(1);
			} else {

				defendingPokemonName 	= titleCase(strCoordinates[0]);
				mylat 					= Double.parseDouble(strCoordinates[1]);
				mylong 					= Double.parseDouble(strCoordinates[2]);
				baseFilePath 			= strCoordinates[3].replaceAll("\\s", "\\ ");
			}
		} catch (IOException e) {

			e.printStackTrace();
		}

		System.out.println("DEBUG INFO:::" + defendingPokemonName + "\n" + mylat + "\n" + mylong + "\n" + baseFilePath );

		//Configure spark
		// Step 1 - set the Application name
		SparkConf sparkConf 				= new SparkConf().setAppName("DummyPokemonSpawnRecommenderTest");
		// Step 2 - get spark context from conf
		JavaSparkContext sparkContext 		= new JavaSparkContext(sparkConf);

		// Step 3 - Load and parse the data
		System.out.println("Data file path  --------> " + baseFilePath + File.separator + pokemonSpawnFilePath);

		/**
		 * "/Users/sidmishraw/Documents/SJSU/Classes Fall 2016/CS 185-C Solving BigData Problems/pokemon-spawns.csv"
		 */
		JavaRDD<String> data = sparkContext.textFile(baseFilePath + File.separator + pokemonSpawnFilePath);

		// Hardcoding the pokemon map just for now
		PokemonDict.setBaseFilePath(baseFilePath);
		PokemonDict pokemonDict = PokemonDict.getInstance();

		// spoofing userId to be equal to the defending pokemon's serial number
		//int spoofedUserId 		=	pokemonDict.getPokemonSerialNbr(defendingPokemonName);
		// hardcoding userId
		int spoofedUserId			= 1;

		// get the userpreference object and set the userId as the spoofed user ID
		UserPreferenceInfo userPreferenceInfo = new UserPreferenceInfo(spoofedUserId,sparkContext,sparkConf,baseFilePath);

		// hardcoding the preset favorite pokemon names
		// for demo
		// 10 favorite pokemon
		userPreferenceInfo.setFavoritePokemon("Bulbasaur");
		userPreferenceInfo.setFavoritePokemon("Squirtle");
		userPreferenceInfo.setFavoritePokemon("Charmander");
		userPreferenceInfo.setFavoritePokemon("Pikachu");
		userPreferenceInfo.setFavoritePokemon("Pidgey");
		userPreferenceInfo.setFavoritePokemon("Weedle");
		userPreferenceInfo.setFavoritePokemon("Zubat");
		userPreferenceInfo.setFavoritePokemon("Growlithe");
		userPreferenceInfo.setFavoritePokemon("Machop");
		userPreferenceInfo.setFavoritePokemon("Mankey");

		// setting the defending pokemon
		userPreferenceInfo.setDefenderPokemon(defendingPokemonName);

		// fetching the ranked recommended pokemons from GMM model -- classifier
		pokemonRankMap = userPreferenceInfo.getRecommendPokemon();

		for ( Map.Entry<String,Integer> entry : pokemonRankMap.entrySet() ) {

			System.out.println("HARMLESS :::: ranked entry :" + entry.getKey() + " ---> " + entry.getValue() );
		}

		// Step 4 - Create RDDs of SpawnInfo types
		// use spark's inbuilt map function
		JavaRDD<SpawnInfo> spawnInfoRDD = data.map(

			new Function<String, SpawnInfo>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				public SpawnInfo call(String s) throws Exception {

					if (s.equalsIgnoreCase("s2_id,s2_token,num,name,lat,lng,encounter_ms,disppear_ms")) {

						return new SpawnInfo("0","000", 0.0, 0.0, 0L, 0L);
					}

					String [] csvRow = s.split(",");

					return new SpawnInfo(csvRow[2], csvRow[3], Double.parseDouble(csvRow[4]),
							Double.parseDouble(csvRow[5]), Long.parseLong(csvRow[6]), Long.parseLong(csvRow[7]));
				}
			}
		);

		// Convert the Spawninfo into Rating format
		// with userId as 1
		// and item as Pokemon names
		// ratings as closeness ratios
		JavaRDD<SpawnRating> spawnRating = spawnInfoRDD.map(

				new Function<SpawnInfo, SpawnRating>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public SpawnRating call(SpawnInfo sinfo) throws Exception {

						double distanceM = distFrom(mylat, mylong, sinfo.getLatitude(), sinfo.getLongitude());

						double rating = 0;

						if ( distanceM <= 200.0) {

							rating = 5;
						} else if ( distanceM > 200.0 && distanceM <= 500.0 ) {
							
							rating = 4;
						} else if ( distanceM > 500.0 && distanceM <= 700.0 ) {
							
							rating = 3;
						} else if ( distanceM > 700.0 && distanceM <= 1000.0) {
							
							rating = 2;
						} else if ( distanceM > 1000.0 && distanceM <= 10000.0 ){
							
							rating = 1;
						}

						return new SpawnRating(sinfo.getPokemonSerialNbr(),sinfo.getPokemonName()
								,rating, sinfo.getLatitude(), sinfo.getLongitude(), distanceM);
					}

				}
		);

		// filter the spawn ratings into desirable pokemons based on pokemon map and spawn ratings
		JavaRDD<SpawnRating> filteredSpawnRating = spawnRating.filter(

				new Function<SpawnRating, Boolean>() {

					/**
					 * 
					 */
					private static final long serialVersionUID = 1L;

					@Override
					public Boolean call(SpawnRating spawnRating) throws Exception {

						if ( spawnRating.getSpawnrating() > 2.5 && pokemonRankMap.containsKey(spawnRating.getPokemonName()) ) {

							return true;
						}

						return false;
					}
				}
		);

		// map the filtered spawn ratings, need the most frequent ones on higher ranks
		// and reduce them to get the actual number of occurences of the spawns for the rated pokemons
		JavaPairRDD<SpawnRating, Integer> remappedReducedSpawnRating = filteredSpawnRating.mapToPair(

			new PairFunction<SpawnRating, SpawnRating, Integer>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public Tuple2<SpawnRating, Integer> call(SpawnRating spawnRating) throws Exception {

					return new Tuple2<SpawnRating, Integer>(spawnRating, 1);
				}
			}
		).reduceByKey(

			new Function2<Integer, Integer, Integer>() {

				/**
				 * 
				 */
				private static final long serialVersionUID = 1L;

				@Override
				public Integer call(Integer arg0, Integer arg1) throws Exception {

					return arg0 + arg1;
				}
			}
		);

		// find the intersection between - sorted based on rating and nearest pokemon 
		// compare the sorted based on Ranking from pokemon as well
		// #Crucial method
		// fixed sortByKey using comparator
		// DESC order maintained
		JavaPairRDD<SpawnRating, Integer> sortedSpawnRating = remappedReducedSpawnRating.sortByKey( serialize (

			(SpawnRating firstSpawnRating, SpawnRating secondSpawnRating) -> {

				if ( null != pokemonRankMap.get(firstSpawnRating.getPokemonName()) 
						&& null != pokemonRankMap.get(secondSpawnRating.getPokemonName()) ) {

					int rank1 = pokemonRankMap.get(firstSpawnRating.getPokemonName());
					int rank2 = pokemonRankMap.get(secondSpawnRating.getPokemonName());

					if ( rank1 == rank2 ) {

						return -firstSpawnRating.compareTo(secondSpawnRating);
					} else if (rank1 < rank2){

						return -1;
					} else {

						return 1;
					}
				} else {

					return -1;
				}
			})
		);

		// fetch all the ratings from the spawnRating RDD
		List<SpawnRating> ratings = filteredSpawnRating.collect();

		String pokemonDirPath =  baseFilePath.replaceAll("\\s", "\\ ") + File.separator + "pokemonspawnsRecommender";

		File pokemonDir = new File(pokemonDirPath);

		// create dir if it doesn't exist
		if ( !pokemonDir.exists() ) {

			System.out.println("CREATING DIR ____>" + pokemonDirPath);
			pokemonDir.mkdir();
		}

		// trim the last quotemark
		String filteredRatingOutPutPath = pokemonDirPath + File.separator + "pokemonspawnsRating.txt";

		File opfile = new File(filteredRatingOutPutPath);

		// create the file if it doesn't exist
		try {

			opfile.createNewFile();
		} catch (IOException e2) {

			e2.printStackTrace();
		}

		try ( BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(opfile, false))) ) {

			StringBuffer buffer = new StringBuffer();

			buffer.append("---------------------------------------- POKEMON SPAWN RATINGS -----------------------------------------\n")
			  .append("Spawn Rating - Higher is better\n\n");

			buffer.append("SERIAL#")
			  .append("\t")
			  .append("NAME")
			  .append("\t")
			  .append("SPAWN RATING")
			  .append("\t")
			  .append("SPAWN LATITUDE CO-ORDINATE")
			  .append("\t")
			  .append("SPAWN LONGITUDE CO-ORDINATE")
			  .append("\n");

			System.out.println(buffer.toString());

			bufferedWriter.write(buffer.toString());

			for (SpawnRating rating : ratings) {

				buffer.setLength(0);

				buffer.append(rating.getPokemonSerialNbr())
					  .append("\t")
					  .append(rating.getPokemonName())
					  .append("\t")
					  .append(rating.getSpawnrating())
					  .append("\t")
					  .append(rating.getSpawnlatcooridinate())
					  .append("\t")
					  .append(rating.getSpawnlongcooridinate())
					  .append("\n");

				System.out.println(buffer.toString());

				bufferedWriter.write(buffer.toString());
			}

			buffer.setLength(0);

			buffer.append("---------------------------------------- POKEMON SPAWN RATINGS ---------------------------------------\n")
			  .append("Thanks for using this tool.\n")
			  .append("TEAM:\nJoshua\nSidharth\nWei Chung");

			System.out.println(buffer.toString());

			bufferedWriter.write(buffer.toString());
		} catch(IOException e) {

			e.printStackTrace();
		}

		System.out.println("RATING COMPLETED, OUTPUT AT :" + filteredRatingOutPutPath );

		// remappedReducedSpawnRating
		List<Tuple2<SpawnRating, Integer>> filteredSpawnRatings = sortedSpawnRating.collect();

		String finalSpawnRecommendedOutPutPath = pokemonDirPath + File.separator + "pokemonspawnsRecommended.txt";

		opfile = new File(finalSpawnRecommendedOutPutPath);

		try {

			opfile.createNewFile();
		} catch (IOException e1) {

			e1.printStackTrace();
		}

		try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(opfile)))) {

			StringBuffer buffer = new StringBuffer();

			buffer.append("---------------------------------------- POKEMON SPAWN RECOMMENDATIONS ------------------------------------\n")
				  .append("Spawn Rating - Higher is better\n")
				  .append("Higher ranked pokemons on the top of the list.\n\n");

			buffer.append("SERIAL#")
			  .append("\t")
			  .append("NAME")
			  .append("\t")
			  .append("SPAWN RATING")
			  .append("\t")
			  .append("SPAWN LATITUDE CO-ORDINATE")
			  .append("\t")
			  .append("SPAWN LONGITUDE CO-ORDINATE")
			  .append("\t")
			  .append("DISTANCE FROM ME(in meters)")
			  .append("\t")
			  .append("PAST SPAWN FREQUENCY")
			  .append("\n");

			System.out.println(buffer.toString());

			bufferedWriter.write(buffer.toString());

			for (Tuple2<SpawnRating, Integer> rating : filteredSpawnRatings ) {

				buffer.setLength(0);

				buffer.append(rating._1().getPokemonSerialNbr())
					  .append("\t")
					  .append(rating._1().getPokemonName())
					  .append("\t")
					  .append(rating._1().getSpawnrating())
					  .append("\t")
					  .append(rating._1().getSpawnlatcooridinate())
					  .append("\t")
					  .append(rating._1().getSpawnlongcooridinate())
					  .append("\t")
					  .append(rating._1().getApproximateDistanceFromMe())
					  .append("\t")
					  .append(rating._2())
					  .append("\n");

				System.out.println(buffer.toString());

				bufferedWriter.write(buffer.toString());
			}

			buffer.setLength(0);

			buffer.append("---------------------------------------- POKEMON SPAWN RECOMMENDATIONS ------------------------------------\n")
			  .append("Thanks for using this tool.\n")
			  .append("TEAM:\nJoshua\nSidharth\nWei Chung");

			System.out.println(buffer.toString());

			bufferedWriter.write(buffer.toString());
		} catch(IOException e) {

			e.printStackTrace();
		}

		System.out.println("PROGRAM COMPLETED, OUTPUT AT :" + finalSpawnRecommendedOutPutPath );

		sparkContext.close();
	}
}

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
	private static double mylat = 0, mylong 			= 0;
	@SuppressWarnings("unused")
	private static String defendingPokemonName			= "";
	private static Map<String,Integer> pokemonRankMap 	= new HashMap<>();

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

	    float dist = (float) (earthRadius * c);

	    return dist;
	}

	// trying out the recommendation for spawn
	public static void main(String[] args) {

		System.out.println("please enter your Defending pokemon name and lat and long "
				+ "coordinates separated by space like ----> Defending pokemon Lat Long");

		try( BufferedReader br = new BufferedReader(new InputStreamReader(System.in)) ) {

			String [] strCoordinates = br.readLine().split("\\s");

			defendingPokemonName = strCoordinates[0];
			mylat = Double.parseDouble(strCoordinates[1]);
			mylong = Double.parseDouble(strCoordinates[2]);
		} catch (IOException e) {

			e.printStackTrace();
		}

		//Configure spark
		// Step 1 - set the Application name
		SparkConf sparkConf 				= new SparkConf().setAppName("DummyPokemonSpawnRecommenderTest");
		// Step 2 - get spark context from conf
		JavaSparkContext sparkContext 		= new JavaSparkContext(sparkConf);

		// Step 3 - Load and parse the data
		JavaRDD<String> data = sparkContext.textFile("/Users/sidmishraw/Documents/SJSU"
				+ "/Classes Fall 2016/CS 185-C Solving BigData Problems/pokemon-spawns.csv");
		
		// Hardcoding the pokemon map just for now
		pokemonRankMap.put("Moltress",1);
		pokemonRankMap.put("Charizard",2);
		pokemonRankMap.put("Charmeleon",3);
		pokemonRankMap.put("Arcanine",4);
		pokemonRankMap.put("Raichu",5);
		pokemonRankMap.put("Pikachu",6);
		pokemonRankMap.put("Charmander",7);
		pokemonRankMap.put("Weedle",8);
		pokemonRankMap.put("Growlithe",9);
		pokemonRankMap.put("Machamp",10);

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
		// and item as pokemon names
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

						return new SpawnRating(sinfo.getPokemonSerialNbr(),sinfo.getPokemonName(),rating, sinfo.getLatitude(), sinfo.getLongitude(), distanceM);
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
		@SuppressWarnings("resource")
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

		File opfile = new File("/Users/sidmishraw/Documents/SJSU"
				+ "/Classes Fall 2016/CS 185-C Solving BigData Problems/pokemonspawnsRating.txt");

		try (BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(opfile)))) {

			StringBuffer buffer = new StringBuffer();

			for (SpawnRating rating : ratings) {

				buffer.setLength(0);

				buffer.append(rating.getPokemonSerialNbr())
					  .append(" ")
					  .append(rating.getPokemonName())
					  .append(" ")
					  .append(rating.getSpawnrating())
					  .append(" ")
					  .append(rating.getSpawnlatcooridinate())
					  .append(" ")
					  .append(rating.getSpawnlongcooridinate())
					  .append("\n");

				bufferedWriter.write(buffer.toString());
			}
		} catch(IOException e) {

			e.printStackTrace();
		}

		System.out.println("PROgram completed, output at /Users/sidmishraw/Documents/SJSU"
				+ "/Classes Fall 2016/CS 185-C Solving BigData Problems/pokemonspawnsRating.txt");

		// remappedReducedSpawnRating
		List<Tuple2<SpawnRating, Integer>> filteredSpawnRatings = sortedSpawnRating.collect();

		StringBuffer buffer = new StringBuffer();

		for (Tuple2<SpawnRating, Integer> rating : filteredSpawnRatings ) {

			buffer.setLength(0);

			buffer.append(rating._1().getPokemonSerialNbr())
				  .append(" ")
				  .append(rating._1().getPokemonName())
				  .append(" ")
				  .append(rating._1().getSpawnrating())
				  .append(" ")
				  .append(rating._1().getSpawnlatcooridinate())
				  .append(" ")
				  .append(rating._1().getSpawnlongcooridinate())
				  .append(" ")
				  .append(rating._1().getApproximateDistanceFromMe())
				  .append(" ")
				  .append(rating._2())
				  .append("\n");

			System.out.println(buffer.toString());
		}

		sparkContext.close();
	}
}

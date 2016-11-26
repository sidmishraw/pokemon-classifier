/**
 * 
 */
package edu.sjsu.pokemonclassifier.spawnrecommender;

import java.util.HashMap;
import java.util.Map;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.ml.recommendation.ALS;
import org.apache.spark.ml.recommendation.ALSModel;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SQLContext;
import org.apache.spark.sql.SparkSession;

/**
 * @author sidmishraw
 *
 */
public class DummyPokemonSpawnRecommenderTest {

	// trying out the recommendation for spawn
	public static void main(String[] args) {

		//Configure spark
		// Step 1 - set the Application name
		SparkConf sparkConf 				= new SparkConf().setAppName("DummyPokemonSpawnRecommenderTest");
		// Step 2 - get spark context from conf
		JavaSparkContext sparkContext 		= new JavaSparkContext(sparkConf);
		SparkSession sparkSQLContext 		= new SparkSession(sparkContext.sc());

		// Step 3 - Load and parse the data
		JavaRDD<String> data = sparkContext.textFile("/Users/sidmishraw/Documents/SJSU"
				+ "/Classes Fall 2016/CS 185-C Solving BigData Problems/pokemon-spawns.csv");

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

						return new SpawnInfo("999","Mew", 37.7935915752623,-122.408720633183,1469520187732L,1469519919988L);
					}

					String [] csvRow = s.split(",");

					return new SpawnInfo(csvRow[2], csvRow[3], Double.parseDouble(csvRow[4]),
							Double.parseDouble(csvRow[5]), Long.parseLong(csvRow[6]), Long.parseLong(csvRow[7]));
				}
			}
		);

		// This part will be receiving ranking from WeiChung's module
		// A hardcoded map to get things started
		Map<Integer, String> pokemonRankedMap = new HashMap<Integer, String>();
		pokemonRankedMap.put(1, "Zapdos");
		pokemonRankedMap.put(2, "Moltress");
		pokemonRankedMap.put(3, "Charizard");
		pokemonRankedMap.put(4, "Charmeleon");
		pokemonRankedMap.put(5, "Arcanine");
		pokemonRankedMap.put(6, "NineTails");
		pokemonRankedMap.put(7, "Magmar");
		pokemonRankedMap.put(8, "Charmander");

		// Step 5 - Buliding recommendation model using ALS - Alternating Least Squares algorithm
		Dataset<Row> ratings = sparkSQLContext.createDataFrame(spawnInfoRDD, SpawnInfo.class);
		Dataset<Row> [] splits = ratings.randomSplit(new double [] {0.7,0.3});
		Dataset<Row> trainingSplit = splits[0];
		Dataset<Row> testingSplit = splits[1];
		
		ALS alsInstance = new ALS()
				.setMaxIter(10)
				.setRegParam(0.01)
				.setUserCol("pokemonSerialNbr")
				.setItemCol("pokemonName")
				.setItemCol("pokemonSerialNbr")
				.setRatingCol("longitude")
				.setRatingCol("latitude");

		ALSModel alsModel = alsInstance.fit(trainingSplit);
		
		System.out.println("END OF PROCESSING");
		System.out.println(alsModel.toString());
	}
}

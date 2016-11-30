/**
 * 
 */
package edu.sjsu.pokemonclassifier.spawnrecommender;

import java.io.Serializable;

/**
 * @author sidmishraw
 *
 */
public class SpawnRating implements Serializable,Comparable<SpawnRating> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int pokemonSerialNbr;
	private String pokemonName; 
	private double spawnrating;
	private double spawnlatcooridinate;
	private double spawnlongcooridinate;
	private double approximateDistanceFromMe;

	/**
	 * @param pokemonSerialNbr
	 * @param pokemonName
	 * @param spawnrating
	 */
	public SpawnRating(int pokemonSerialNbr, String pokemonName, double spawnrating, double lat, double longc, double approximateDistanceFromMe) {
		super();
		this.pokemonSerialNbr = pokemonSerialNbr;
		this.pokemonName = pokemonName;
		this.spawnrating = spawnrating;
		this.spawnlatcooridinate = lat;
		this.spawnlongcooridinate = longc;
		this.approximateDistanceFromMe = approximateDistanceFromMe;
	}

	/**
	 * @return the approximateDistanceFromMe
	 */
	public double getApproximateDistanceFromMe() {
		return approximateDistanceFromMe;
	}

	/**
	 * 
	 */
	public SpawnRating() {}

	/**
	 * @return the pokemonSerialNbr
	 */
	public int getPokemonSerialNbr() {
		return pokemonSerialNbr;
	}

	/**
	 * @return the pokemonName
	 */
	public String getPokemonName() {
		return pokemonName;
	}

	/**
	 * @return the spawnrating
	 */
	public double getSpawnrating() {
		return spawnrating;
	}

	
	/**
	 * @return the spawnlatcooridinate
	 */
	public double getSpawnlatcooridinate() {
		return spawnlatcooridinate;
	}

	/**
	 * @return the spawnlongcooridinate
	 */
	public double getSpawnlongcooridinate() {
		return spawnlongcooridinate;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		long temp;
		temp = Double.doubleToLongBits(approximateDistanceFromMe);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		result = prime * result + ((pokemonName == null) ? 0 : pokemonName.hashCode());
		result = prime * result + pokemonSerialNbr;
		temp = Double.doubleToLongBits(spawnlatcooridinate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(spawnlongcooridinate);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		temp = Double.doubleToLongBits(spawnrating);
		result = prime * result + (int) (temp ^ (temp >>> 32));
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof SpawnRating)) {
			return false;
		}
		SpawnRating other = (SpawnRating) obj;
		if (Double.doubleToLongBits(approximateDistanceFromMe) != Double
				.doubleToLongBits(other.approximateDistanceFromMe)) {
			return false;
		}
		if (pokemonName == null) {
			if (other.pokemonName != null) {
				return false;
			}
		} else if (!pokemonName.equals(other.pokemonName)) {
			return false;
		}
		if (pokemonSerialNbr != other.pokemonSerialNbr) {
			return false;
		}
		if (Double.doubleToLongBits(spawnlatcooridinate) != Double.doubleToLongBits(other.spawnlatcooridinate)) {
			return false;
		}
		if (Double.doubleToLongBits(spawnlongcooridinate) != Double.doubleToLongBits(other.spawnlongcooridinate)) {
			return false;
		}
		if (Double.doubleToLongBits(spawnrating) != Double.doubleToLongBits(other.spawnrating)) {
			return false;
		}
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append("SpawnRating [pokemonSerialNbr=");
		builder.append(pokemonSerialNbr);
		builder.append(", pokemonName=");
		builder.append(pokemonName);
		builder.append(", spawnrating=");
		builder.append(spawnrating);
		builder.append(", spawnlatcooridinate=");
		builder.append(spawnlatcooridinate);
		builder.append(", spawnlongcooridinate=");
		builder.append(spawnlongcooridinate);
		builder.append(", approximateDistanceFromMe=");
		builder.append(approximateDistanceFromMe);
		builder.append("]");
		return builder.toString();
	}

	/**
	 * comparison -- if rating is high and near then greater SpawnRating obj
	 */
	@Override
	public int compareTo(SpawnRating anotherSpawnRating) {

		if (this.equals(anotherSpawnRating)) {

			return 0;
		} else if ( this.spawnrating < anotherSpawnRating.getSpawnrating() ) {

			return -1;
		} else if ( this.approximateDistanceFromMe > anotherSpawnRating.getApproximateDistanceFromMe() ){

			return -1;
		} else {

			return 1;
		}
	}
}

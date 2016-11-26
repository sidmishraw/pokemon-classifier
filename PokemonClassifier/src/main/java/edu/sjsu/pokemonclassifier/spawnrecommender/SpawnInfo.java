/**
 * 
 */
package edu.sjsu.pokemonclassifier.spawnrecommender;

import java.io.Serializable;

/**
 * @author sidmishraw
 *  This class will be used to make RDDs, hence it needs to be immutable.
 *  For this reason, this class has no setter methods.
 */
public class SpawnInfo implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private int pokemonSerialNbr;
	private String pokemonName;
	private Double latitude;
	private Double longitude;
	private long pokemonEncounterMS;
	private long pokemonDisappearMS;

	public SpawnInfo() {}

	/**
	 * @param pokemonSerialNbr
	 * @param pokemonName
	 * @param latitude
	 * @param longitude
	 * @param pokemonEncounterMS
	 * @param pokemonDisappearMS
	 */
	public SpawnInfo(String pokemonSerialNbr, String pokemonName, Double latitude, Double longitude,
			long pokemonEncounterMS, long pokemonDisappearMS) {
		super();
		this.pokemonSerialNbr = Integer.parseInt(pokemonSerialNbr);
		this.pokemonName = pokemonName;
		this.latitude = latitude;
		this.longitude = longitude;
		this.pokemonEncounterMS = pokemonEncounterMS;
		this.pokemonDisappearMS = pokemonDisappearMS;
	}
	
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
	 * @return the latitude
	 */
	public Double getLatitude() {
		return latitude;
	}

	/**
	 * @return the longitude
	 */
	public Double getLongitude() {
		return longitude;
	}

	/**
	 * @return the pokemonEncounterMS
	 */
	public long getPokemonEncounterMS() {
		return pokemonEncounterMS;
	}

	/**
	 * @return the pokemonDisappearMS
	 */
	public long getPokemonDisappearMS() {
		return pokemonDisappearMS;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((latitude == null) ? 0 : latitude.hashCode());
		result = prime * result + ((longitude == null) ? 0 : longitude.hashCode());
		result = prime * result + (int) (pokemonDisappearMS ^ (pokemonDisappearMS >>> 32));
		result = prime * result + (int) (pokemonEncounterMS ^ (pokemonEncounterMS >>> 32));
		result = prime * result + ((pokemonName == null) ? 0 : pokemonName.hashCode());
		result = prime * result + pokemonSerialNbr;
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
		if (!(obj instanceof SpawnInfo)) {
			return false;
		}
		SpawnInfo other = (SpawnInfo) obj;
		if (latitude == null) {
			if (other.latitude != null) {
				return false;
			}
		} else if (!latitude.equals(other.latitude)) {
			return false;
		}
		if (longitude == null) {
			if (other.longitude != null) {
				return false;
			}
		} else if (!longitude.equals(other.longitude)) {
			return false;
		}
		if (pokemonDisappearMS != other.pokemonDisappearMS) {
			return false;
		}
		if (pokemonEncounterMS != other.pokemonEncounterMS) {
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
		return true;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("SpawnInfo [pokemonSerialNbr=");
		builder.append(pokemonSerialNbr);
		builder.append(", pokemonName=");
		builder.append(pokemonName);
		builder.append(", latitude=");
		builder.append(latitude);
		builder.append(", longitude=");
		builder.append(longitude);
		builder.append(", pokemonEncounterMS=");
		builder.append(pokemonEncounterMS);
		builder.append(", pokemonDisappearMS=");
		builder.append(pokemonDisappearMS);
		builder.append("]");
		return builder.toString();
	}
	
}

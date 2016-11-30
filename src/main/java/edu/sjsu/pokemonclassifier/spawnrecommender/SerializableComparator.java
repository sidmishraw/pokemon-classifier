/**
 * 
 */
package edu.sjsu.pokemonclassifier.spawnrecommender;

import java.io.Serializable;
import java.util.Comparator;

/**
 * Fix#1::
 * Adding this to fix spark serialization errors while using sortByKey() of RDDs
 * Using java 1.8 and above only
 * This feature of using static methods in interfaces is not available below 1.8
 * @author sidmishraw
 *
 * @param <T>
 */
public interface SerializableComparator<T> extends Comparator<T>, Serializable {

	public static <T> SerializableComparator<T> serialize(SerializableComparator<T> comparator) {

	    return comparator;
	}
}

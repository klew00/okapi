package net.sf.okapi.lib.search.lucene.scorer;

public class Util {
	/**
	 * Calculate Dice's Coefficient
	 * 
	 * @param intersection
	 *            number of tokens in common between input 1 and input 2
	 * @param size1
	 *            token size of first input
	 * @param size2
	 *            token size of second input
	 * @return Dice's Coefficient as a float
	 */
	public static float calculateDiceCoefficient(int intersection, int size1, int size2) {
		return (float) ((2.0f * (float) intersection)) / (float) (size1 + size2) * 100.0f;
	}
}

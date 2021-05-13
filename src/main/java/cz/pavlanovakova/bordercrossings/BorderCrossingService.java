package cz.pavlanovakova.bordercrossings;

import java.util.Optional;

public interface BorderCrossingService {

	/**
	 * Calculates border crossing from origin country to destination country.
	 * 
	 * @param origin country code (cca3) of the origin country
	 * @param destination country code (cca3) of the destination country
	 * @return border crossings from origin to destination country if there is at least one border crossing, empty if no crossing exists.
	 */
	Optional<String[]> getBorderCrossings(String origin, String destination);
	
}

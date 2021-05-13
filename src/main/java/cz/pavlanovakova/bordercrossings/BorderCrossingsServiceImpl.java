package cz.pavlanovakova.bordercrossings;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.fasterxml.jackson.databind.ObjectMapper;

/*
 * TO FURTHER IMPROVE EFFICIENCY we could:
 *  
 * 1/ parse json file just once (and refresh on data change) 
 *    a/ eagerly on application startup using @EventListener(ApplicationReadyEvent.class) for example
 *    b/ lazily on first incoming request 
 * 2/ use Least Frequently Used (LFU) cache for already calculated paths, 
 *    for example: [CZ,IT] -> [CZ,AT,IT] (covers also [IT, CZ] -> [IT, AT, CZ])
 *     
 */
@Service
public class BorderCrossingsServiceImpl implements BorderCrossingService {

	@Value("classpath:countries.json")
	private Resource countriesFile;

	private ObjectMapper objectMapper = new ObjectMapper();

	public Optional<String[]> getBorderCrossings(String origin, String destination) {
		Assert.notNull(origin, "Origin country code is not set.");
		Assert.notNull(destination, "Destination country code is not set.");

		Map<String, CountryNode> countries = getCountries();
		CountryNode originCountryNode = countries.get(origin);
		if (originCountryNode == null) throw new IllegalArgumentException("Origin country with code: " + origin + " doesn't exist.");
		CountryNode destinationCountryNode = countries.get(destination);
		if (destinationCountryNode == null) throw new IllegalArgumentException("Destination country with code: " + destination + " doesn't exist.");

		// handle simple cases without running search algorithm
		if (originCountryNode.getNeighbours().isEmpty() ||
			destinationCountryNode.getNeighbours().isEmpty() ||
			originCountryNode.equals(destinationCountryNode))
			return Optional.empty();

		return searchForBorderCrossings(countries, originCountryNode, destinationCountryNode);
	}

	private Map<String, CountryNode> getCountries() {
		CountryNode[] countries;
		try {
			countries = objectMapper.readValue(countriesFile.getInputStream(), CountryNode[].class);
		} catch (IOException e) {
			throw new RuntimeException("Cannot read countries file.", e);
		}
		Map<String, CountryNode> countriesMap = new HashMap<>();
		for (CountryNode countryNode : countries) {
			countriesMap.put(countryNode.getCountryCode(), countryNode);
		}
		return countriesMap;
	}

	/**
	 * Breath-first search algorithm for shortest path from initial vertex (origin) to target vertex (destination) in unweighted graph.
	 * 
	 * @param countries          countries borders graph represented by an adjacency list
	 * @param originCountry      initial vertex for the search algorithm
	 * @param destinationCountry target vertex for the search algorithm
	 * 
	 * @return border crossings from
	 */
	private Optional<String[]> searchForBorderCrossings(Map<String, CountryNode> countries, CountryNode originCountry, CountryNode destinationCountry) {
		Queue<CountryNode> countryQueue = new LinkedList<>();
		originCountry.setDistance(0);
		countryQueue.add(originCountry);
		CountryNode visited = null;
		CountryNode neighbour = null;
		while (!countryQueue.isEmpty() && !destinationCountry.isVisited()) {
			visited = countryQueue.poll();
			for (String neighbourCountryCode : visited.getNeighbours()) {
				neighbour = countries.get(neighbourCountryCode);
				if (neighbour.isVisited()) continue;
				neighbour.setPredecessor(visited);
				neighbour.setDistance(visited.getDistance() + 1);
				if (destinationCountry.equals(neighbour)) break;
				countryQueue.add(neighbour);
			}
		}
		
		if (!destinationCountry.isVisited()) return Optional.empty(); // destination node not reached

		// reconstructing path to target vertex
		String[] borderCrossings = new String[destinationCountry.getDistance() + 1];
		visited = destinationCountry;
		while (visited != null) {
			borderCrossings[visited.getDistance()] = visited.getCountryCode();
			visited = visited.getPredecessor();
		}
		return Optional.of(borderCrossings);
	}
}

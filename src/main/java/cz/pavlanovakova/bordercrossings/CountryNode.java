package cz.pavlanovakova.bordercrossings;

import java.util.List;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CountryNode {
	
	private static final int NOT_VISITED = -1;

	// properties mapped from json file
	@JsonProperty("cca3")
	private String countryCode;
	@JsonProperty("borders")
	private List<String> neighbours;
	
	// helper properties for breadth-first search algorithm (BFS)
	/**
	 * The number of countries between this country and origin country when running BFS.
	 * (0 for origin country, 1 for neighboring country, ...)
	 */
	private int distance = NOT_VISITED;
	
	/**
	 * The BFS traversing predecessor of this country. 
	 */
	private CountryNode predecessor;
	
	public boolean isVisited() {
		return distance != NOT_VISITED;
	}
	
	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}

	public List<String> getNeighbours() {
		return neighbours;
	}

	public void setNeighbours(List<String> neighbours) {
		this.neighbours = neighbours;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	public CountryNode getPredecessor() {
		return predecessor;
	}

	public void setPredecessor(CountryNode predecessor) {
		this.predecessor = predecessor;
	}

	@Override
	public int hashCode() {
		return Objects.hash(countryCode);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		CountryNode other = (CountryNode) obj;
		return Objects.equals(countryCode, other.countryCode);
	}
	
}

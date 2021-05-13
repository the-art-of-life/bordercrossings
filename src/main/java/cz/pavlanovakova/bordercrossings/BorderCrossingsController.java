package cz.pavlanovakova.bordercrossings;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class BorderCrossingsController {

	@Autowired
	private BorderCrossingService borderCrossingService;

	@GetMapping("/routing/{origin}/{destination}")
	public String borderCrossings(@PathVariable String origin, @PathVariable String destination) {
		// accept both upper case and lower case values in url?
		String originCountryCode = origin.toUpperCase();
		String destinationCountryCode = destination.toUpperCase();
		// if cannot access / parse countries.json file, HTTP 500 without additional details is generated in current (simplest) implementation 
		// if country codes are in invalid format / not existing, HTTP 500 without additional details is generated in current (simplest) implementation
		// (custom InvalidCountryCodeException could be handled here and sending BAD_REQUEST could provide better feedback)
		Optional<String[]> borderCrossings = borderCrossingService.getBorderCrossings(originCountryCode, destinationCountryCode);
		if (borderCrossings.isEmpty()) throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		return String.join("-", borderCrossings.get());
	}

}
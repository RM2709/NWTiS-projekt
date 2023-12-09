package org.foi.nwtis.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// TODO: Auto-generated Javadoc
/**
 * The Class Airport.
 *
 * @author Dragutin Kermek
 * @version 2.3.0
 */
@AllArgsConstructor()
public class Airport {

	/** The ident. */
	@Getter
	@Setter
	private String ident;
	
	/** The type. */
	@Getter
	@Setter
	private String type;
	
	/** The name. */
	@Getter
	@Setter
	private String name;
	
	/** The elevation ft. */
	@Getter
	@Setter
	private String elevation_ft;
	
	/** The continent. */
	@Getter
	@Setter
	private String continent;
	
	/** The iso country. */
	@Getter
	@Setter
	private String iso_country;
	
	/** The iso region. */
	@Getter
	@Setter
	private String iso_region;
	
	/** The municipality. */
	@Getter
	@Setter
	private String municipality;
	
	/** The gps code. */
	@Getter
	@Setter
	private String gps_code;
	
	/** The iata code. */
	@Getter
	@Setter
	private String iata_code;
	
	/** The local code. */
	@Getter
	@Setter
	private String local_code;
	
	/** The coordinates. */
	@Getter
	@Setter
	private String coordinates;

	/**
	 * Instantiates a new airport.
	 */
	public Airport() {
	}
}

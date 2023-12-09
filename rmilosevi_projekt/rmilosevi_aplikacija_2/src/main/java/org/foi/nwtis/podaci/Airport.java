package org.foi.nwtis.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasa zapisa za objekt tipa Aerodrom.
 *
 * @author Dragutin Kermek
 * @version 2.3.0
 */
@AllArgsConstructor()
public class Airport {

	/**  ID. */
	@Getter
	@Setter
	private String ident;
	
	/**  Tip. */
	@Getter
	@Setter
	private String type;
	
	/**  Naziv. */
	@Getter
	@Setter
	private String name;
	
	/**  Nadmorska visina. */
	@Getter
	@Setter
	private String elevation_ft;
	
	/**  Kontinent. */
	@Getter
	@Setter
	private String continent;
	
	/**  Oznaka države. */
	@Getter
	@Setter
	private String iso_country;
	
	/**  Oznaka regije. */
	@Getter
	@Setter
	private String iso_region;
	
	/**  Okrug/Županija. */
	@Getter
	@Setter
	private String municipality;
	
	/**  GPS kod. */
	@Getter
	@Setter
	private String gps_code;
	
	/**  IATA kod. */
	@Getter
	@Setter
	private String iata_code;
	
	/**  Lokalni kod. */
	@Getter
	@Setter
	private String local_code;
	
	/**  Koordinate. */
	@Getter
	@Setter
	private String coordinates;

	/**
	 * Konstruktor.
	 */
	public Airport() {
	}
}

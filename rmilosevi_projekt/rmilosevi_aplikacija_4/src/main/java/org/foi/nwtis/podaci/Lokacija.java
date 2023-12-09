package org.foi.nwtis.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// TODO: Auto-generated Javadoc
/**
 * Klasa za zapis lokacije.
 *
 * @author Dragutin Kermek
 * @author Matija Novak
 * @version 2.3.0
 */
@AllArgsConstructor()
public class Lokacija {

    /**  Zemljopisna širina. */
    @Getter
    @Setter
    private String latitude;
    
    /**  Zemljopisna duljina. */
    @Getter
    @Setter    
    private String longitude;

    /**
     * Konstruktor. Instancira novu lokaciju.
     */
    public Lokacija() {
    }

    /**
     * Postavi lokaciju.
     *
     * @param latitude the latitude
     * @param longitude the longitude
     */
    public void postavi(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

package org.foi.nwtis.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// TODO: Auto-generated Javadoc
/**
 * Klasa zapisa za objekt tipa Lokacija.
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
     * Konstruktor.
     */
    public Lokacija() {
    }

    /**
     * Postavljanje lokacije.
     *
     * @param latitude Širina
     * @param longitude Duljina
     */
    public void postavi(String latitude, String longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}

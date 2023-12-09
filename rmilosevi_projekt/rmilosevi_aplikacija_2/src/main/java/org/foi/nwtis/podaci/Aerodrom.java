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
public class Aerodrom {

    /**  ICAO oznaka. */
    @Getter
    @Setter
    private String icao;
    
    /**  Naziv aerodroma. */
    @Getter
    @Setter
    private String naziv;
    
    /**  Država u kojoj se aerodrom nalazi. */
    @Getter
    @Setter
    private String drzava;
    
    /**  Lokacija aerodroma. */
    @Getter
    @Setter
    private Lokacija lokacija;

    /**
     * Konstruktor.
     */
    public Aerodrom() {
    }
}

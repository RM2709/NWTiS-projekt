package org.foi.nwtis.podaci;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

// TODO: Auto-generated Javadoc
/**
 * Klasa za zapis Aerodroma.
 *
 * @author Dragutin Kermek
 * @version 2.3.0
 */
@AllArgsConstructor()
public class Aerodrom {

    /**  icao oznaka. */
    @Getter
    @Setter
    protected String icao;
    
    /**  naziv aerodroma. */
    @Getter
    @Setter
    protected String naziv;
    
    /**  država u kojoj se aerodrom nalazi. */
    @Getter
    @Setter
    protected String drzava;
    
    /**  lokacija na kojoj se aerodrom nalazi. */
    @Getter
    @Setter
    protected Lokacija lokacija;

    /**
     * Konstruktor. Instancira novi aerodrom
     */
    public Aerodrom() {
    }
}

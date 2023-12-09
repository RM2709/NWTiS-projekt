package org.foi.nwtis.rmilosevi.aplikacija_4.korisnici;

/**
 * Greška za okidanje pri pogrešnoj autentifikaciji korisnika
 */
public class PogresnaAutentifikacija extends Exception{
  
  /** Konstanta serialVersionUID. */
  private static final long serialVersionUID = 1L;

  /**
   * Instancira novu grešku pogresna autentifikacija.
   */
  public PogresnaAutentifikacija() {
    
  }
  
  /**
   * Instancira novu grešku pogresna autentifikacija.
   *
   * @param poruka poruka za ispis
   */
  public PogresnaAutentifikacija(String poruka) {
    super(poruka);
  }

}

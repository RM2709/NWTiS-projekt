package org.foi.nwtis.podaci;

// TODO: Auto-generated Javadoc
/**
 * Klasa za zapis udaljenosti aerodroma.
 */
public class UdaljenostAerodromKlasa {
  
  /**  icao oznaka. */
  private String icao;
  
  /**  kilometri. */
  private float km;

  /**
   * Konstruktor.
   *
   * @param icao icao oznaka
   * @param km kilometri
   */
  public UdaljenostAerodromKlasa(String icao, float km) {
    super();
    this.icao = icao;
    this.km = km;
  }

  /**
   * Dohvaća icao.
   *
   * @return icao
   */
  public String getIcao() {
    return icao;
  }

  /**
   * Postavlja icao.
   *
   * @param icao novi icao
   */
  public void setIcao(String icao) {
    this.icao = icao;
  }

  /**
   * Dohvaća kilometre.
   *
   * @return km
   */
  public float getKm() {
    return km;
  }

  /**
   * Postavlja kilometre.
   *
   * @param km novi km
   */
  public void setKm(float km) {
    this.km = km;
  }



}

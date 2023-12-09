package org.foi.nwtis.podaci;


// TODO: Auto-generated Javadoc
/**
 * Klasa za zapis udaljenosti aerodroma unutar države.
 */
public class UdaljenostAerodromDrzavaKlasa {
  
  /** icao. */
  private String icao;
  
  /** drzava. */
  private String drzava;
  
  /**  kilometri. */
  private float km;

  /**
   * Konstruktor.
   *
   * @param icao icao
   * @param drzava drzava
   * @param km km
   */
  public UdaljenostAerodromDrzavaKlasa(String icao, String drzava, float km) {
    super();
    this.icao = icao;
    this.drzava = drzava;
    this.km = km;
  }

  /**
   * Dohvaća icao.
   *
   * @return  icao
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
   * Dohvaća drzavu.
   *
   * @return drzava
   */
  public String getDrzava() {
    return drzava;
  }

  /**
   * Postavlja drzavu.
   *
   * @param drzava nova drzava
   */
  public void setDrzava(String drzava) {
    this.drzava = drzava;
  }

  /**
   * Dohvaća km.
   *
   * @return km
   */
  public float getKm() {
    return km;
  }

  /**
   * Postavlja km.
   *
   * @param km the new km
   */
  public void setKm(float km) {
    this.km = km;
  }


}

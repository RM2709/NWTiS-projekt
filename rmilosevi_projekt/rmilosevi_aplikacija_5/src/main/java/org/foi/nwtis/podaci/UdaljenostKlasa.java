package org.foi.nwtis.podaci;

// TODO: Auto-generated Javadoc
/**
 * Klasa za zapis udaljenosti.
 */
public class UdaljenostKlasa {
  
  /** država. */
  private String drzava;
  
  /**  broj kilometara. */
  private float km;

  /**
   * Konstruktor.
   *
   * @param drzava  drzava
   * @param km broj kilometara
   */
  public UdaljenostKlasa(String drzava, float km) {
    super();
    this.drzava = drzava;
    this.km = km;
  }

  /**
   * Dohvaća državu.
   *
   * @return drzava
   */
  public String getDrzava() {
    return drzava;
  }

  /**
   * Postavlja državu.
   *
   * @param drzava nova država
   */
  public void setDrzava(String drzava) {
    this.drzava = drzava;
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
   * @param km novi kilometri
   */
  public void setKm(float km) {
    this.km = km;
  }


}


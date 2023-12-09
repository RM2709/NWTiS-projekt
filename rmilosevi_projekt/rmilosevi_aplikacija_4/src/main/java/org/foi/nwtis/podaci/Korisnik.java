package org.foi.nwtis.podaci;

// TODO: Auto-generated Javadoc
/**
 * Klasa za zapis korisnika
 */
public class Korisnik {
  
  /** Korisničko ime */
  private String korime;
  

  /** Lozinka */
  private String lozinka;
  
  /** Ime. */
  private String ime;
  
  /** Prezime */
  private String prezime;

  /**
   * Dohvaća korisničko ime.
   *
   * @return the korime
   */
  public String getKorime() {
    return korime;
  }

  /**
   * Postavlja korisničko ime.
   *
   * @param korime the new korime
   */
  public void setKorime(String korime) {
    this.korime = korime;
  }

  /**
   * Dohvaća lozinku
   *
   * @return the lozinka
   */
  public String getLozinka() {
    return lozinka;
  }

  /**
   * Postavlja lozinku
   *
   * @param lozinka the new lozinka
   */
  public void setLozinka(String lozinka) {
    this.lozinka = lozinka;
  }
  
  /**
   * Dohvaća ime.
   *
   * @return the ime
   */
  public String getIme() {
    return ime;
  }

  /**
   * Postavljaime.
   *
   * @param ime the new ime
   */
  public void setIme(String ime) {
    this.ime = ime;
  }

  /**
   * Dohvaća prezime.
   *
   * @return the prezime
   */
  public String getPrezime() {
    return prezime;
  }

  /**
   * Postavlja prezime.
   *
   * @param prezime the new prezime
   */
  public void setPrezime(String prezime) {
    this.prezime = prezime;
  }
  
  /**
   * Instancira novog korisnika
   *
   * @param korime korisničko ime
   * @param lozinka lozinka
   * @param ime ime
   * @param prezime prezime
   */
  public Korisnik(String korime, String lozinka, String ime, String prezime) {
    super();
    this.korime = korime;
    this.lozinka = lozinka;
    this.ime = ime;
    this.prezime = prezime;
  }

  /**
   * Instancira novog korisnika
   */
  public Korisnik() {
    super();
  }

}

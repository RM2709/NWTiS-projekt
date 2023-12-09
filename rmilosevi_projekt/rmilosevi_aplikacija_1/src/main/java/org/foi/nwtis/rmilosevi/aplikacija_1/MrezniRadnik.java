package org.foi.nwtis.rmilosevi.aplikacija_1;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.foi.nwtis.Konfiguracija;
import java.lang.Math;

/**
 * Klasa mrežni radnik. Obavlja obradu zahtjeva za poslužitelja.
 * 
 * @author Roko Milošević
 */
public class MrezniRadnik extends Thread {

  /** mrežna utičnica. */
  protected Socket mreznaUticnica;

  /** konfiguracija. */
  protected Konfiguracija konfig;

  /**  Pisač u datoteku. */
  protected BufferedWriter pisac;

  /**  Čitač iz datoteke. */
  protected BufferedReader citac;

  /**  Instanca glavnog poslužitelja koji je pokrenuo ovu dretvu. */
  public GlavniPosluzitelj posluzitelj;

  /**
   * Instancira novog mrežnog radnika.
   *
   * @param mreznaUticnica mrežna utičnica za spajanje s klijentom
   * @param konfig konfiguracija
   */
  public MrezniRadnik(Socket mreznaUticnica, Konfiguracija konfig) {
    super();
    this.mreznaUticnica = mreznaUticnica;
    this.konfig = konfig;
  }

  /**
   * Start.
   */
  @Override
  public synchronized void start() {
    super.start();
  }

  /**
   * Pokretanje rada dretve.
   */
  @Override
  public void run() {
    this.posluzitelj.brAktivnih++;
    try {
      citac = new BufferedReader(
          new InputStreamReader(this.mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
      pisac = new BufferedWriter(
          new OutputStreamWriter(this.mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));
      var poruka = new StringBuilder();
      while (true) {
        var red = citac.readLine();
        if (red == null)
          break;

        poruka.append(red);
      }
      this.mreznaUticnica.shutdownInput();
      String odgovor = this.obradiZahtjev(poruka.toString());
      pisac.write(odgovor);
      pisac.flush();
      this.mreznaUticnica.shutdownOutput();
      this.mreznaUticnica.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Greška pri radu dretve.");
      e.printStackTrace();
    }
    this.posluzitelj.brAktivnih--;
  }

  /**
   * Provjerava ispravnost izraza.
   *
   * @param poruka izraz koji se provjerava
   * @param regularniIzraz regularni izraz koji definira kako bi izraz trebao izgledati
   * @return true, ako izraz odgovara formatu regularnog izraza, false ako ne
   */
  public boolean provjeriDozvoljeniIzraz(String poruka, String regularniIzraz) {
    Pattern uzorak = Pattern.compile(regularniIzraz);
    Matcher provjeraUzorka = uzorak.matcher(poruka);
    return provjeraUzorka.find();
  }

  /**
   * Obrada korisničkog zahtjeva.
   *
   * @param zahtjev korisnički zahtjev
   * @return odgovor korisniku u String obliku
   */
  public String obradiZahtjev(String zahtjev) {
    if(posluzitelj.ispis) {
      System.out.println(zahtjev);
    }
    if (provjeriDozvoljeniIzraz(zahtjev, "UDALJENOST")) {
      return zahtjevUdaljenost(zahtjev);
    } else if (provjeriDozvoljeniIzraz(zahtjev, "STATUS")) {
      return "OK " + this.posluzitelj.status.toString();
    } else if (provjeriDozvoljeniIzraz(zahtjev, "INIT")) {
      return zahtjevInit();
    } else if (provjeriDozvoljeniIzraz(zahtjev, "PAUZA")) {
      return zahtjevPauza();
    } else if (provjeriDozvoljeniIzraz(zahtjev, "KRAJ")) {
      this.posluzitelj.ugasiPosluzitelja(this);
      return "OK";
    } else if (provjeriDozvoljeniIzraz(zahtjev, "INFO")) {
      return zahtjevInfo(zahtjev);
    }else {
      return "ERROR 05 Krivi format komande";
    }
  }

  /**
   * Obrada zahtjeva INFO.
   *
   * @param zahtjev the zahtjev
   * @return the string
   */
  private String zahtjevInfo(String zahtjev) {
    if (this.posluzitelj.status == 0) {
      return "ERROR 01 Posluzitelj je pauziran";
    } else {
      if(provjeriDozvoljeniIzraz(zahtjev, "INFO DA")) {
        if(this.posluzitelj.ispis==true) {
          return "ERROR 03 Posluzitelj vec ispisuje podatke na standardni izlaz!";
        }else {
          this.posluzitelj.ispis=true;
          return "OK";
        }
      }else if(provjeriDozvoljeniIzraz(zahtjev, "INFO NE")){
        if(this.posluzitelj.ispis==false) {
          return "ERROR 04 Posluzitelj vec ne ispisuje podatke na standardni izlaz!";
        }else {
          this.posluzitelj.ispis=false;
          return "OK";
        }
      }else {
        return "ERROR 05 Krivi format komande";
      }
    }
  }

  /**
   * Obrada zahtjeva PAUZA.
   *
   * @return the string
   */
  private String zahtjevPauza() {
    if (this.posluzitelj.status == 1) {
      this.posluzitelj.status = 0;
      return "OK " + this.posluzitelj.brojOdradenihZahtjeva.toString();
    } else {
      return "ERROR 01 Posluzitelj je pauziran";
    }
  }

  /**
   * Obrada zahtjeva INIT.
   *
   * @return the string
   */
  private String zahtjevInit() {
    if (this.posluzitelj.status == 0) {
      this.posluzitelj.status = 1;
      this.posluzitelj.brojOdradenihZahtjeva = 0;
      return "OK ";
    } else {
      return "ERROR 02 Posluzitelj je vec aktivan";
    }
  }

  /**
   * Obrada zahtjeva UDALJENOST.
   *
   * @param zahtjev the zahtjev
   * @return the string
   */
  private String zahtjevUdaljenost(String zahtjev) {
    if (this.posluzitelj.status == 0) {
      return "ERROR 01 Posluzitelj je pauziran";
    } else {
      return obradaUdaljenost(zahtjev);
    }
  }

  /**
   * Provjera ispravnosti zahtjeva korisnika za računanjem udaljenosti između dvije točke.
   *
   * @param zahtjev zahtjev korisnika
   * @return odgovor korisniku
   */
  public String obradaUdaljenost(String zahtjev) {
    if (provjeriDozvoljeniIzraz(zahtjev,
        "^UDALJENOST (-?[0-9]{1,3}.[0-9]{0,} ){3}(-?[0-9]{1,3}.[0-9]{0,})$")) {
      String[] koordinate = zahtjev.split(" ");
      this.posluzitelj.brojOdradenihZahtjeva++;
      return "OK "
          + izracunajUdaljenost(koordinate[1], koordinate[2], koordinate[3], koordinate[4]);
    } else {
      return "ERROR 05 Neispravni podaci udaljenosti!";
    }
  }

  /**
   * Izračunava udaljenost između dvije točke.
   *
   * @param gpsSirina1 geografska širina prve točke
   * @param gpsDuzina1 geografska dužina prve točke
   * @param gpsSirina2 geografska širina druge točke
   * @param gpsDuzina2 geografska dužina druge točke
   * @return udaljenost između dvije točke u String formatu
   */
  private String izracunajUdaljenost(String gpsSirina1, String gpsDuzina1, String gpsSirina2,
      String gpsDuzina2) {
    double sirina1 = Double.parseDouble(gpsSirina1);
    double duzina1 = Double.parseDouble(gpsDuzina1);
    double sirina2 = Double.parseDouble(gpsSirina2);
    double duzina2 = Double.parseDouble(gpsDuzina2);
    int radius = 6371;
    double a = Math.sin(Math.toRadians(sirina2 - sirina1) / 2)
        * Math.sin(Math.toRadians(sirina2 - sirina1) / 2)
        + Math.cos(Math.toRadians(sirina1)) * Math.cos(Math.toRadians(sirina2))
            * Math.sin(Math.toRadians(duzina2 - duzina1) / 2)
            * Math.sin(Math.toRadians(duzina2 - duzina1) / 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double udaljenost = radius * c;
    udaljenost = Math.pow(udaljenost, 2) + Math.pow(0, 2);
    udaljenost = Math.sqrt(udaljenost);
    NumberFormat formater = new DecimalFormat("####0.0#");
    return formater.format(udaljenost);
  }

  /**
   * Prekid dretve kod zatvaranja glavnog poslužitelja.
   */
  @Override
  public void interrupt() {
    try {
      pisac.write("OK");
      pisac.flush();
      this.mreznaUticnica.shutdownOutput();
      this.mreznaUticnica.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
    super.interrupt();
  }



}

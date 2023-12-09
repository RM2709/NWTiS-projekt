package org.foi.nwtis.rmilosevi.aplikacija_1;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;

/**
 * Klasa glavnog poslužitelja, otvara vezu na određenim mrežnim vratima/portu.
 * 
 * @author Roko Milošević
 *
 */
public class GlavniPosluzitelj {

  /** Konfiguracija. */
  protected Konfiguracija konfig;

  /**
   * Status poslužitelja. 0 = pauza, 1 = aktivan
   */
  protected Integer status = 0;

  /**  Treba li prestati čekati korisničke zahtjeve. */
  protected boolean kraj = false;

  /**  Broj odrađenih zahtjeva. */
  protected Integer brojOdradenihZahtjeva = 0;

  /**  Ispisuje li poslužitelj podatke o radu na standardni izlaz. */
  protected boolean ispis = false;

  /**  servis za pokretanje dretvi. */
  protected ExecutorService servisZaPokretanjeDretvi;

  /**
   * Instancira novi poslužitelj.
   *
   * @param konfiguracija konfiguracija
   */
  public GlavniPosluzitelj(Konfiguracija konfiguracija) {
    this.konfig = konfiguracija;
  }

  /** The br aktivnih. */
  protected int brAktivnih = 0;

  /**
   * Priprema poslužitelja za rad. Provjerava jesu li mrežna vrata slobodna te pokreće poslužitelja.
   */
  public void pripremiPosluzitelja() {
    int mreznaVrata = Integer.parseInt(konfig.dajPostavku("mreznaVrata"));
    try (ServerSocket ss = new ServerSocket(mreznaVrata)) {
      ss.close();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, "Mrežna vrata '" + mreznaVrata + "' su zauzeta!");
      e.printStackTrace();
      return;
    }
    try {
      this.pokreniPosluzitelja();
    } catch (IOException e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
      e.printStackTrace();
    } catch (InterruptedException e) {
      Logger.getGlobal().log(Level.SEVERE, e.getMessage());
      e.printStackTrace();
    }
  }

  /**
   * Pokreće rad poslužitelja.
   *
   * @throws IOException Greška s kreiranjem Socketa.
   * @throws InterruptedException Greška u radu dretve.
   */
  public void pokreniPosluzitelja() throws IOException, InterruptedException {
    var brojac = 0;
    int mreznaVrata = Integer.parseInt(konfig.dajPostavku("mreznaVrata"));
    int brojRadnika = Integer.parseInt(konfig.dajPostavku("brojRadnika"));
    servisZaPokretanjeDretvi = Executors.newFixedThreadPool(brojRadnika);
    try (ServerSocket ss = new ServerSocket(mreznaVrata)) {
      while (!this.kraj) {
        Socket veza = ss.accept();
        MrezniRadnik mrezniRadnik = new MrezniRadnik(veza, konfig);
        mrezniRadnik.posluzitelj = this;
        mrezniRadnik.setName("rmilosevi_" + brojac);
        brojac++;
        servisZaPokretanjeDretvi.execute(mrezniRadnik);
      }
    }
  }

  /**
   * Gasi poslužitelja.
   *
   * @param mr Dretva koja gasi poslužitelja.
   */
  public void ugasiPosluzitelja(MrezniRadnik mr) {
    try {
      this.kraj = true;
      mr.interrupt();
      this.servisZaPokretanjeDretvi.awaitTermination(500, TimeUnit.MILLISECONDS);
      System.exit(0);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
  }

}

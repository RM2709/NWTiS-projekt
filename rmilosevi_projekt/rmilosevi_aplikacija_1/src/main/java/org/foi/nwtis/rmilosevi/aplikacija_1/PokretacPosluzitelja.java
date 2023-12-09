package org.foi.nwtis.rmilosevi.aplikacija_1;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;

/**
 * Klasa za pokretanje poslužitelja.
 *
 * @author Roko Milošević
 */
public class PokretacPosluzitelja {

  /**
   * Glavna metoda. Pokreće poslužitelja.
   *
   * @param args naziv konfiguracijske datoteke
   */
  public static void main(String[] args) {
    var pokretac = new PokretacPosluzitelja();
    if (!pokretac.provjeriArgumente(args)) {
      Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
          "Nije upisan naziv datoteke!");
      return;
    }
    if (!Files.exists(Path.of(args[0])))
      return;
    try {
      var konfig = pokretac.ucitajPostavke(args[0]);
      var glavniPosluzitelj = new GlavniPosluzitelj(konfig);
      glavniPosluzitelj.pripremiPosluzitelja();
    } catch (NeispravnaKonfiguracija e) {
      Logger.getLogger(PokretacPosluzitelja.class.getName()).log(Level.SEVERE,
          "Pogreška kod učitavanja postavki iz datoteke!" + e.getMessage());
    }
  }

  /**
   * Provjerava je li unesen jedan argument.
   *
   * @param args argument
   * @return true, ako je unesen točno jedan argument
   */
  private boolean provjeriArgumente(String[] args) {
    if (args.length == 1) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Ucitava postavke iz datoteke konfiguracije.
   *
   * @param nazivDatoteke naziv datoteke konfiguracije.
   * @return konfiguracija
   * @throws NeispravnaKonfiguracija greška pri učitavanju konfiguracije
   */
  Konfiguracija ucitajPostavke(String nazivDatoteke) throws NeispravnaKonfiguracija {
    Konfiguracija konfig = KonfiguracijaApstraktna.preuzmiKonfiguraciju(nazivDatoteke);
    return konfig;
  }

}

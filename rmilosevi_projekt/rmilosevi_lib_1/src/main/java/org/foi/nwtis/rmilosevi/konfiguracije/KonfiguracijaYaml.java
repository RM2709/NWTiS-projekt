package org.foi.nwtis.rmilosevi.konfiguracije;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

import org.foi.nwtis.Konfiguracija;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.yaml.snakeyaml.Yaml;

/**
 * Klasa za rad s yaml datotekom koja sadrži postavke.
 * 
 * @author Roko Milošević
 *
 */
public class KonfiguracijaYaml extends KonfiguracijaApstraktna {

  /**
   * Konstanta TIP
   */
  public static final String TIP = "yaml";

  /**
   * Konstruktor
   * 
   * @param nazivDatoteke naziv datoteke konfiguracije
   */
  public KonfiguracijaYaml(String nazivDatoteke) {
    super(nazivDatoteke);
  }

  @Override
  public void spremiKonfiguraciju(String datoteka) throws NeispravnaKonfiguracija {
    var putanja = Path.of(datoteka);
    var tip = Konfiguracija.dajTipKonfiguracije(datoteka);

    if (tip == null || tip.compareTo(TIP) != 0) {
      throw new NeispravnaKonfiguracija("Datoteka '" + datoteka + "' nije ispravnog tipa: " + TIP);
    } else if (Files.exists(putanja)
        && (!Files.isWritable(putanja) || Files.isDirectory(putanja))) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' je direktorij ili nije moguće spremati!");
    }

    try {
      PrintWriter writer = new PrintWriter(datoteka);
      Yaml yaml = new Yaml();
      yaml.dump(this.postavke, writer);
    } catch (IOException e) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' nije moguće pisati." + e.getMessage());
    }

  }

  @Override
  public void ucitajKonfiguraciju() throws NeispravnaKonfiguracija {
    var datoteka = this.nazivDatoteke;
    var putanja = Path.of(datoteka);
    var tip = Konfiguracija.dajTipKonfiguracije(datoteka);

    if (tip == null || tip.compareTo(TIP) != 0) {
      throw new NeispravnaKonfiguracija("Datoteka '" + datoteka + "' nije ispravnog tipa: " + TIP);
    } else if (Files.exists(putanja)
        && (!Files.isReadable(putanja) || Files.isDirectory(putanja))) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' je direktorij ili nije moguće čitati!");
    }

    try {
      InputStream inputStream = new FileInputStream(datoteka);
      Yaml yaml = new Yaml(new org.yaml.snakeyaml.constructor.Constructor(Properties.class));
      Properties data = yaml.load(inputStream);
      this.postavke = data;
    } catch (IOException e) {
      throw new NeispravnaKonfiguracija(
          "Datoteka '" + datoteka + "' nije moguće čitati." + e.getMessage());
    }
  }
}

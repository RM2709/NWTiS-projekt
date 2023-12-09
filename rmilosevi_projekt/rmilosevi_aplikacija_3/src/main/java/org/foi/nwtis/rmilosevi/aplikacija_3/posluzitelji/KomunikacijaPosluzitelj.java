package org.foi.nwtis.rmilosevi.aplikacija_3.posluzitelji;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import jakarta.servlet.ServletContext;

/**
 * Klasa za komunikaciju (slanje zahtjeva) na poslužitelja iz Aplikacije 1
 */
public class KomunikacijaPosluzitelj {
  
  /** Adresa poslužitelja */
  private String adresa;
  
  /** Mrežna vrata poslužitelja */
  private Integer mreznaVrata;
  
  /**
   * Instancira klasu 
   *
   * @param kontekst kontekst
   */
  public KomunikacijaPosluzitelj(ServletContext kontekst) {
    adresa = kontekst.getAttribute("adresa").toString();
    mreznaVrata = Integer.parseInt(kontekst.getAttribute("mreznaVrata").toString());
  }
  
  /**
   * Šalje zahtjev prema poslužitelju
   *
   * @param zahtjev the zahtjev
   * @return the string
   */
  public String posaljiZahtjevNaPosluzitelj(String zahtjev) {
    try {
      Socket mreznaUticnica = new Socket(adresa, mreznaVrata);
      var citac = new BufferedReader(
          new InputStreamReader(mreznaUticnica.getInputStream(), Charset.forName("UTF-8")));
      var pisac = new BufferedWriter(
          new OutputStreamWriter(mreznaUticnica.getOutputStream(), Charset.forName("UTF-8")));
      pisac.write(zahtjev);
      pisac.flush();
      mreznaUticnica.shutdownOutput();
      var poruka = new StringBuilder();
      while (true) {
        var red = citac.readLine();
        if (red == null)
          break;
        poruka.append(red);
      }
      mreznaUticnica.shutdownInput();
      mreznaUticnica.close();
      return poruka.toString();
    } catch (IOException e) {
      return "Greška pri spajanju na poslužitelj!";
    }
  }
  
  

}

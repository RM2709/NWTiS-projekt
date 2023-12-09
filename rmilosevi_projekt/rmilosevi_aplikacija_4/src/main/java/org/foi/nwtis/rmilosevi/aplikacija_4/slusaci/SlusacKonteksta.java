package org.foi.nwtis.rmilosevi.aplikacija_4.slusaci;

import java.util.Enumeration;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.foi.nwtis.rmilosevi.aplikacija_4.posluzitelji.KomunikacijaPosluzitelj;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;


/**
 * Klasa slušač konteksta.
 */
@WebListener
public class SlusacKonteksta implements ServletContextListener {

  /** Kontekst. */
  private ServletContext context = null;

  /** Spajanje na bazu podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Dohvaćanje konteksta i inicijalizacija slušača. Gasi aplikaciju ako poslužitelj iz AP1 ne radi.
   *
   * @param event event koji pokreće inicijalizaciju
   */
  @Override
  public void contextInitialized(ServletContextEvent event) {
    context = event.getServletContext();
    ucitajKonfiguraciju();
    if (!provjeriPosluzitelja()) {
      throw new IllegalStateException("Poslužitelj nije dostupan!");
    }
  }

  /**
   * Učitavanje konfiguracije u kontekst.
   */
  private void ucitajKonfiguraciju() {
    java.util.Properties configData = new java.util.Properties();
    String path = context.getRealPath("/WEB-INF") + java.io.File.separator;
    String datoteka = context.getInitParameter("konfiguracija");
    try {
      configData = KonfiguracijaApstraktna.preuzmiKonfiguraciju(path + datoteka).dajSvePostavke();
      for (Enumeration<?> e = configData.propertyNames(); e.hasMoreElements();) {
        String key = (String) e.nextElement();
        context.setAttribute(key, configData.getProperty(key));
      }
    } catch (NeispravnaKonfiguracija e) {
      e.printStackTrace();
    }
  }

  /**
   * Provjeri radi li poslužitelj iz Aplikacije 1.
   *
   * @return true, if successful
   */
  private boolean provjeriPosluzitelja() {
    KomunikacijaPosluzitelj posluzitelj = new KomunikacijaPosluzitelj(context);
    String odgovor = posluzitelj.posaljiZahtjevNaPosluzitelj("STATUS");
    if (odgovor.contains("OK 1")) {
      return true;
    } else {
      return false;
    }
  }
}

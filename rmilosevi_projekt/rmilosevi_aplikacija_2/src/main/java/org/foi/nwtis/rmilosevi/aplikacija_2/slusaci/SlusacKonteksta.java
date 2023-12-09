package org.foi.nwtis.rmilosevi.aplikacija_2.slusaci;

import java.util.Enumeration;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.foi.nwtis.rmilosevi.aplikacija_2.posluzitelji.KomunikacijaPosluzitelj;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;


// TODO: Auto-generated Javadoc
/**
 * Klasa slušač konteksta.
 */
@WebListener
public class SlusacKonteksta implements ServletContextListener {

  /**  Kontekst. */
  private ServletContext context = null;

  /**
   * Dohvaćanje konteksta.
   *
   * @param event event koji pokreće inicijalizaciju
   */
  @Override
  public void contextInitialized(ServletContextEvent event) {
    context = event.getServletContext();
    ucitajKonfiguraciju();
    if(!provjeriPosluzitelja()) {
      context.setAttribute("ograniciRad", true);
      //throw new IllegalStateException("Poslužitelj nije dostupan!");
    }else {
      context.setAttribute("ograniciRad", false);
    }
  }
  
  /**
   * Provjeri posluzitelja.
   *
   * @return true, if successful
   */
  private boolean provjeriPosluzitelja() {
    KomunikacijaPosluzitelj posluzitelj = new KomunikacijaPosluzitelj(context);
    String odgovor = posluzitelj.posaljiZahtjevNaPosluzitelj("STATUS");
    if(odgovor.contains("OK 1")) {
      return true; 
    }else {
      return false;
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
}

package org.foi.nwtis.rmilosevi.aplikacija_3.slusaci;

import java.util.Enumeration;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.foi.nwtis.KonfiguracijaApstraktna;
import org.foi.nwtis.NeispravnaKonfiguracija;
import org.foi.nwtis.rmilosevi.aplikacija_3.posluzitelji.KomunikacijaPosluzitelj;
import org.foi.nwtis.rmilosevi.aplikacija_3.sakupljaci.SakupljacLetovaAviona;
import org.foi.nwtis.rmilosevi.aplikacija_3.zrna.JmsPosiljatelj;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
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

  /**  Izvršitelj dretve. */
  private ExecutorService scheduler;

  /** Jms posiljatelj. */
  @EJB
  JmsPosiljatelj jmsPosiljatelj;

  /**  Spajanje na bazu podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /** Sakupljac letova aviona. */
  SakupljacLetovaAviona sakupljacLetovaAviona;

  /**
   * Dohvaćanje konteksta i pokretanje dretve za sakupljanje aviona.
   *
   * @param event event koji pokreće inicijalizaciju
   */
  @Override
  public void contextInitialized(ServletContextEvent event) {
    context = event.getServletContext();
    ucitajKonfiguraciju();
    if(provjeriPosluzitelja()) {
      scheduler = Executors.newSingleThreadExecutor();
      sakupljacLetovaAviona = new SakupljacLetovaAviona(context, jmsPosiljatelj, ds);
      scheduler.execute(sakupljacLetovaAviona);
    }else {
      System.out.println("Poslužitelj ne radi, ne pokrećem dretvu.");
    }
    
  }

  /**
   * Gašenje programa i dretve za sakupljanje letova.
   *
   * @param event the event
   */
  @Override
  public void contextDestroyed(ServletContextEvent event) {
    System.out.println("Gasim: " + Thread.currentThread().getId());
    sakupljacLetovaAviona.timer.cancel();
    scheduler.shutdownNow();
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
   * Provjerava je li poslužitelj (AP1) aktivan
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
}

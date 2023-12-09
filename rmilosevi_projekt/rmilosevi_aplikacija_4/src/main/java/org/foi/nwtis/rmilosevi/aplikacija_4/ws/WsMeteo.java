package org.foi.nwtis.rmilosevi.aplikacija_4.ws;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Lokacija;
import org.foi.nwtis.rest.klijenti.LIQKlijent;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OWMKlijent;
import org.foi.nwtis.rest.podaci.MeteoPodaci;
import org.foi.nwtis.rmilosevi.aplikacija_4.korisnici.AutentifikacijaKorisnika;
import org.foi.nwtis.rmilosevi.aplikacija_4.korisnici.PogresnaAutentifikacija;
import org.foi.nwtis.rmilosevi.aplikacija_4.posluzitelji.KomunikacijaAP2;
import jakarta.annotation.Resource;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

/**
 * Klasa meteo web servisa.
 */
@WebService(serviceName = "meteo")
public class WsMeteo {

  /**  baza podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**  context web servisa. */
  @Resource
  private WebServiceContext context;

  /**  kontekst servleta. */
  ServletContext kontekst;

  /**
   * Vraća meteo podatke za aerodrom.
   *
   * @param korisnik korisnik
   * @param lozinka lozinka
   * @param icao icao aerodroma
   * @return meteo podaci aerodroma
   * @throws Exception iznimka
   */
  @WebMethod
  public MeteoPodaci dajMeteo(@WebParam String korisnik, @WebParam String lozinka, @WebParam String icao) throws Exception {
    AutentifikacijaKorisnika autentifikacijaKorisnika = new AutentifikacijaKorisnika();
    if(!autentifikacijaKorisnika.autentifikacijaKorisnika(korisnik, lozinka, ds)) {
      throw new PogresnaAutentifikacija("Pogrešna kombinacija korisničkog imena i lozinke!");
    }
    kontekst = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
    PreparedStatement stmt = null;
    String latitude = null;
    String longitude = null;
    MeteoPodaci meteoPodaci;
    KomunikacijaAP2 komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    Aerodrom aerodrom = komunikacijaAP2.getAerodrom(icao);
    if(aerodrom==null) {
      throw new NwtisRestIznimka("Aerodrom s unesenom ICAO oznakom ne postoji!");
    }
    Lokacija lokacija = aerodrom.getLokacija();
    try {
      OWMKlijent owmKlijent =
          new OWMKlijent(kontekst.getAttribute("OpenWeatherMap.apikey").toString());
      meteoPodaci = owmKlijent.getRealTimeWeather(lokacija.getLatitude(), lokacija.getLongitude());
    } catch(Exception e) {
      meteoPodaci = null;
    }
    return meteoPodaci; 
  }


}

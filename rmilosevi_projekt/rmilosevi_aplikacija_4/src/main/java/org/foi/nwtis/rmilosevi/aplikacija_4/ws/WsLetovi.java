package org.foi.nwtis.rmilosevi.aplikacija_4.ws;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.podaci.Lokacija;
import org.foi.nwtis.podaci.Udaljenost;
import org.foi.nwtis.podaci.UdaljenostAerodrom;
import org.foi.nwtis.podaci.UdaljenostAerodromDrzava;
import org.foi.nwtis.podaci.UdaljenostAerodromDrzavaKlasa;
import org.foi.nwtis.podaci.UdaljenostAerodromKlasa;
import org.foi.nwtis.podaci.UdaljenostKlasa;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijentBP;
import org.foi.nwtis.rest.podaci.LetAviona;
import org.foi.nwtis.rest.podaci.LetAvionaID;
import org.foi.nwtis.rmilosevi.aplikacija_4.korisnici.AutentifikacijaKorisnika;
import org.foi.nwtis.rmilosevi.aplikacija_4.korisnici.PogresnaAutentifikacija;
import org.foi.nwtis.rmilosevi.aplikacija_4.posluzitelji.KomunikacijaAP2;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import jakarta.xml.ws.WebServiceContext;
import jakarta.xml.ws.handler.MessageContext;

/**
 * Klasa web servisa za letove.
 */
@WebService(serviceName = "letovi")
public class WsLetovi {

  /**  baza podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**  kontekst web servisa. */
  @Resource
  private WebServiceContext context;

  /**  kontekst servleta. */
  ServletContext kontekst;
  
  /** autentifikacija korisnika. */
  AutentifikacijaKorisnika autentifikacijaKorisnika = new AutentifikacijaKorisnika();

  /**
   * Daje polaske u intervalu.
   *
   * @param korisnik korisnik
   * @param lozinka lozinka
   * @param icao icao aerodroma
   * @param danOd prvi dan intervala
   * @param danDo zadnji dan intervala
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   * @return lista polazaka
   * @throws PogresnaAutentifikacija ako je lozinka/korisničko ime pogrešno
   */
  @WebMethod
  public List<LetAviona> dajPolaskeInterval(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao, @WebParam String danOd, @WebParam String danDo, @WebParam int odBroja,
      @WebParam int broj) throws PogresnaAutentifikacija {
    if (odBroja == 0)
      odBroja = 1;
    if (broj == 0)
      broj = 20;
    if (!autentifikacijaKorisnika.autentifikacijaKorisnika(korisnik, lozinka, ds)) {
      throw new PogresnaAutentifikacija("Pogrešna kombinacija korisničkog imena i lozinke!");
    }
    List<LetAviona> letovi = new ArrayList<>();
    PreparedStatement stmt = null;
    long danPocetak = 0;
    long danKraj = 0;
    try {
      danPocetak = new SimpleDateFormat("dd.MM.yyyy.").parse(danOd).getTime() / 1000;
      danKraj =
          (new SimpleDateFormat("dd.MM.yyyy.").parse(danDo).getTime() / 1000) + (60 * 60 * 24);
    } catch (ParseException e1) {
      e1.printStackTrace();
    }
    String upit =
        "SELECT * FROM LETOVI_POLASCI WHERE ESTDEPARTUREAIRPORT=? AND FIRSTSEEN > ? AND FIRSTSEEN < ? OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icao);
      stmt.setLong(2, danPocetak);
      stmt.setLong(3, danKraj);
      stmt.setInt(4, odBroja);
      stmt.setInt(5, broj);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        letovi.add(new LetAviona(rs.getString("ICAO24"), rs.getInt("FIRSTSEEN"),
            rs.getString("ESTDEPARTUREAIRPORT"), rs.getInt("LASTSEEN"),
            rs.getString("ESTARRIVALAIRPORT"), rs.getString("CALLSIGN"),
            rs.getInt("ESTDEPARTUREAIRPORTHORIZDISTANCE"),
            rs.getInt("ESTDEPARTUREAIRPORTVERTDISTANCE"),
            rs.getInt("ESTARRIVALAIRPORTHORIZDISTANCE"), rs.getInt("ESTARRIVALAIRPORTVERTDISTANCE"),
            rs.getInt("DEPARTUREAIRPORTCANDIDATESCOUNT"),
            rs.getInt("ARRIVALAIRPORTCANDIDATESCOUNT")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null && !stmt.isClosed()) {
          stmt.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return letovi;
  }

  /**
   * Daje polaske na odabrani dan.
   *
   * @param korisnik korisnik
   * @param lozinka lozinka
   * @param icao icao aerodroma
   * @param dan odabrani dan
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   * @return lista polazaka
   * @throws PogresnaAutentifikacija ako je lozinka/korisničko ime pogrešno
   */
  @WebMethod
  public List<LetAviona> dajPolaskeNaDan(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao, @WebParam String dan, @WebParam int odBroja, @WebParam int broj)
      throws PogresnaAutentifikacija {
    if (odBroja == 0)
      odBroja = 1;
    if (broj == 0)
      broj = 20;
    if (!autentifikacijaKorisnika.autentifikacijaKorisnika(korisnik, lozinka, ds)) {
      throw new PogresnaAutentifikacija("Pogrešna kombinacija korisničkog imena i lozinke!");
    }
    List<LetAviona> letovi = new ArrayList<>();
    PreparedStatement stmt = null;
    long danPocetak = 0;
    long danKraj = 0;
    try {
      danPocetak = new SimpleDateFormat("dd.MM.yyyy.").parse(dan).getTime() / 1000;
      danKraj = danPocetak + (60 * 60 * 24);
    } catch (ParseException e1) {
      e1.printStackTrace();
    }
    String upit =
        "SELECT * FROM LETOVI_POLASCI WHERE ESTDEPARTUREAIRPORT=? AND FIRSTSEEN > ? AND FIRSTSEEN < ? OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icao);
      stmt.setLong(2, danPocetak);
      stmt.setLong(3, danKraj);
      stmt.setInt(4, odBroja);
      stmt.setInt(5, broj);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        letovi.add(new LetAviona(rs.getString("ICAO24"), rs.getInt("FIRSTSEEN"),
            rs.getString("ESTDEPARTUREAIRPORT"), rs.getInt("LASTSEEN"),
            rs.getString("ESTARRIVALAIRPORT"), rs.getString("CALLSIGN"),
            rs.getInt("ESTDEPARTUREAIRPORTHORIZDISTANCE"),
            rs.getInt("ESTDEPARTUREAIRPORTVERTDISTANCE"),
            rs.getInt("ESTARRIVALAIRPORTHORIZDISTANCE"), rs.getInt("ESTARRIVALAIRPORTVERTDISTANCE"),
            rs.getInt("DEPARTUREAIRPORTCANDIDATESCOUNT"),
            rs.getInt("ARRIVALAIRPORTCANDIDATESCOUNT")));
      }
    } catch (SQLException e) {
      e.printStackTrace();
    } finally {
      try {
        if (stmt != null && !stmt.isClosed()) {
          stmt.close();
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return letovi;
  }
  
  /**
   * Daje polaske na odabrani dan. Dohvaća ih sa OS servisa.
   *
   * @param korisnik korisnik
   * @param lozinka lozinka
   * @param icao icao aerodroma
   * @param dan odabrani dan
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   * @return lista polazaka
   * @throws PogresnaAutentifikacija ako je lozinka/korisničko ime pogrešno
   */
  @WebMethod
  public List<LetAviona> dajPolaskeNaDanOS(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao, @WebParam String dan)
      throws PogresnaAutentifikacija {
    if (!autentifikacijaKorisnika.autentifikacijaKorisnika(korisnik, lozinka, ds)) {
      throw new PogresnaAutentifikacija("Pogrešna kombinacija korisničkog imena i lozinke!");
    }
    kontekst = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
    OSKlijentBP osKlijentBP = new OSKlijentBP(kontekst.getAttribute("OpenSkyNetwork.korisnik").toString(), kontekst.getAttribute("OpenSkyNetwork.lozinka").toString());
    List<LetAviona> letovi = new ArrayList<>();
    long danPocetak = 0;
    long danKraj = 0;
    try {
      danPocetak = new SimpleDateFormat("dd.MM.yyyy.").parse(dan).getTime() / 1000;
      danKraj = danPocetak + (60 * 60 * 24);
    } catch (ParseException e1) {
      e1.printStackTrace();
    }
    try {
      letovi = osKlijentBP.getDepartures(icao, danPocetak, danKraj);
    } catch (NwtisRestIznimka e) {
      e.printStackTrace();
    }    
    return letovi;
  }

}

package org.foi.nwtis.rmilosevi.aplikacija_4.ws;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
 * Klasa web servisa za aerodrome.
 */
@WebService(serviceName = "aerodromi")
public class WsAerodromi {

  /**  baza podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;
  
  /**  context web servisa. */
  @Resource
  private WebServiceContext context;

  /**  kontekst servleta. */
  ServletContext kontekst;

  /**
   * Dohvaća sve aerodrome.
   *
   * @param korisnik the korisnik
   * @param lozinka the lozinka
   * @return Listu objekata tipa Aerodrom
   * @throws PogresnaAutentifikacija the pogresna autentifikacija
   */
  @WebMethod
  public List<Aerodrom> dajAerodromeZaLetove(@WebParam String korisnik, @WebParam String lozinka)
      throws PogresnaAutentifikacija {
    AutentifikacijaKorisnika autentifikacijaKorisnika = new AutentifikacijaKorisnika();
    if (!autentifikacijaKorisnika.autentifikacijaKorisnika(korisnik, lozinka, ds)) {
      throw new PogresnaAutentifikacija("Pogrešna kombinacija korisničkog imena i lozinke!");
    }
    List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();
    PreparedStatement stmt = null;
    String upit =
        "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES FROM AIRPORTS a INNER JOIN AERODROMI_LETOVI al ON a.ICAO=al.ICAO";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String icaoProcitan = rs.getString("ICAO");
        String naziv = rs.getString("NAME");
        String drzava = rs.getString("ISO_COUNTRY");
        String latitude = rs.getString("COORDINATES").split(", ")[0];
        String longitude = rs.getString("COORDINATES").split(", ")[1];
        Lokacija lokacija = new Lokacija(latitude, longitude);
        aerodromi.add(new Aerodrom(icaoProcitan, naziv, drzava, lokacija));
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
    return aerodromi;
  }

  /**
   * Vraća status aerodroma za preuzimanje letova (Da/Pauza)
   *
   * @param korisnik korisnik
   * @param lozinka lozinka
   * @param icao icao aerodroma
   * @return true, ako je aktivan
   * @throws Exception iznimka
   */
  @WebMethod
  public boolean dajStatus(@WebParam String korisnik, @WebParam String lozinka,
      @WebParam String icao) throws Exception {
    AutentifikacijaKorisnika autentifikacijaKorisnika = new AutentifikacijaKorisnika();
    if (!autentifikacijaKorisnika.autentifikacijaKorisnika(korisnik, lozinka, ds)) {
      throw new PogresnaAutentifikacija("Pogrešna kombinacija korisničkog imena i lozinke!");
    }
    PreparedStatement stmt = null;
    String upit = "SELECT STATUS FROM AERODROMI_LETOVI WHERE ICAO=?";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icao);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        return rs.getBoolean("STATUS");
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
    throw new Exception("Aerodrom sa unesenom ICAO oznakom nije u skupu aerodroma za letove!");
  }

  /**
   * Dodaje aerodrom za preuzimanje letova.
   *
   * @param korisnik korisnik
   * @param lozinka lozinka
   * @param icao icao aerodroma
   * @return true, ako je uspješno
   * @throws PogresnaAutentifikacija ako je upisana kriva lozinka/korisničko ime
   */
  @WebMethod
  public boolean dodajAerodromZaLetove(@WebParam String korisnik, @WebParam String lozinka, @WebParam String icao) throws PogresnaAutentifikacija {
    AutentifikacijaKorisnika autentifikacijaKorisnika = new AutentifikacijaKorisnika();
    if(!autentifikacijaKorisnika.autentifikacijaKorisnika(korisnik, lozinka, ds)) {
      throw new PogresnaAutentifikacija("Pogrešna kombinacija korisničkog imena i lozinke!");
    }
    kontekst = (ServletContext) context.getMessageContext().get(MessageContext.SERVLET_CONTEXT);
    KomunikacijaAP2 komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    if(komunikacijaAP2.getAerodrom(icao)==null) {
      return false;
    }
    PreparedStatement stmt = null;
    int red = 0;
    String upit = "INSERT INTO AERODROMI_LETOVI (ICAO, STATUS) VALUES (?, TRUE)";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icao);
      red = stmt.executeUpdate();
    } catch (SQLException e) {
      return false;
    } finally {
      try {
        if (stmt != null && !stmt.isClosed()) {
          stmt.close();
        }
      } catch (SQLException e) {
        return false;
      }
    }
    if(red==1) {
      return true;
    }else {
      return false;
    }
  }
  
  /**
   * Pauzira preuzimanje letova za odabrani aerodrom.
   *
   * @param korisnik korisnik
   * @param lozinka lozinka
   * @param icao icao aerodroma
   * @return true, ako je uspješno
   * @throws PogresnaAutentifikacija ako je upisana kriva lozinka/korisničko ime
   */
  @WebMethod
  public boolean pauzirajAerodromZaLetove(@WebParam String korisnik, @WebParam String lozinka, @WebParam String icao) throws PogresnaAutentifikacija {
    AutentifikacijaKorisnika autentifikacijaKorisnika = new AutentifikacijaKorisnika();
    if(!autentifikacijaKorisnika.autentifikacijaKorisnika(korisnik, lozinka, ds)) {
      throw new PogresnaAutentifikacija("Pogrešna kombinacija korisničkog imena i lozinke!");
    }
    PreparedStatement stmt = null;
    int red = 0;
    String upit = "UPDATE AERODROMI_LETOVI SET STATUS=FALSE WHERE ICAO=?";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icao);
      red = stmt.executeUpdate();
    } catch (SQLException e) {
      return false;
    } finally {
      try {
        if (stmt != null && !stmt.isClosed()) {
          stmt.close();
        }
      } catch (SQLException e) {
        return false;
      }
    }
    if(red==1) {
      return true;
    }else {
      return false;
    }
  }
  
  /**
   * Pokreće preuzimanje letova za odabrani aerodrom.
   *
   * @param korisnik korisnik
   * @param lozinka lozinka
   * @param icao icao aerodroma
   * @return true, ako je uspješno
   * @throws PogresnaAutentifikacija ako je upisana kriva lozinka/korisničko ime
   */
  @WebMethod
  public boolean aktivirajAerodromZaLetove(@WebParam String korisnik, @WebParam String lozinka, @WebParam String icao) throws PogresnaAutentifikacija {
    AutentifikacijaKorisnika autentifikacijaKorisnika = new AutentifikacijaKorisnika();
    if(!autentifikacijaKorisnika.autentifikacijaKorisnika(korisnik, lozinka, ds)) {
      throw new PogresnaAutentifikacija("Pogrešna kombinacija korisničkog imena i lozinke!");
    }
    PreparedStatement stmt = null;
    int red = 0;
    String upit = "UPDATE AERODROMI_LETOVI SET STATUS=TRUE WHERE ICAO=?";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icao);
      red = stmt.executeUpdate();
    } catch (SQLException e) {
      return false;
    } finally {
      try {
        if (stmt != null && !stmt.isClosed()) {
          stmt.close();
        }
      } catch (SQLException e) {
        return false;
      }
    }
    if(red==1) {
      return true;
    }else {
      return false;
    }
  }
  
}

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
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.jws.WebMethod;
import jakarta.jws.WebParam;
import jakarta.jws.WebService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.core.Response;

/**
 * Klasa web servisa za korisnike.
 */
@WebService(serviceName = "korisnici")
public class WsKorisnici {

  /**  baza podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Dodaje korisnika.
   *
   * @param korisnik korisnik
   * @return true, ako je uspješno
   */
  @WebMethod
  public boolean dodajKorisnika(@WebParam Korisnik korisnik) {
    PreparedStatement stmt = null;
    int red = 0;
    String upit = "INSERT INTO KORISNICI (KORISNIK, LOZINKA, IME, PREZIME) VALUES (?, ?, ?, ?)";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, korisnik.getKorime());
      stmt.setString(2, korisnik.getLozinka());
      stmt.setString(3, korisnik.getIme());
      stmt.setString(4, korisnik.getPrezime());
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
    if (red == 1) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Daje sve korisnike.
   *
   * @param korisnik korisnik
   * @param lozinka lozinka
   * @param traziImeKorisnika ime za pretragu
   * @param traziPrezimeKorisnika prezime za pretragu
   * @return listu korisnika
   * @throws PogresnaAutentifikacija ako je lozinka/korisničko ime pogrešno
   */
  @WebMethod
  public List<Korisnik> dajKorisnike(String korisnik, String lozinka, String traziImeKorisnika,
      String traziPrezimeKorisnika) throws PogresnaAutentifikacija{
    AutentifikacijaKorisnika autentifikacijaKorisnika = new AutentifikacijaKorisnika();
    if (!autentifikacijaKorisnika.autentifikacijaKorisnika(korisnik, lozinka, ds)) {
      throw new PogresnaAutentifikacija("Pogrešna kombinacija korisničkog imena i lozinke!");
    }
    List<Korisnik> korisnici = new ArrayList<>();
    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection()) {
      stmt = konstruirajUpit(traziImeKorisnika, traziPrezimeKorisnika, con);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String korisnikProcitan = rs.getString("KORISNIK");
        String lozinkaProcitana = rs.getString("LOZINKA");
        String ime = rs.getString("IME");
        String prezime = rs.getString("PREZIME");
        korisnici.add(new Korisnik(korisnikProcitan, lozinkaProcitana, ime, prezime));
      }
    } catch (SQLException e) {
      return null;
    } finally {
      try {
        if (stmt != null && !stmt.isClosed()) {
          stmt.close();
        }
      } catch (SQLException e) {
        return null;
      }
    }
    return korisnici;
  }
  
  /**
   * Daje korisnika.
   *
   * @param korisnik korisnik
   * @param lozinka lozinka
   * @param traziKorisnika korisničko ime za pretragu
   * @return korisnik
   * @throws PogresnaAutentifikacija ako je lozinka/korisničko ime pogrešno
   */
  @WebMethod
  public Korisnik dajKorisnika(String korisnik, String lozinka, String traziKorisnika) throws PogresnaAutentifikacija{
    AutentifikacijaKorisnika autentifikacijaKorisnika = new AutentifikacijaKorisnika();
    if (!autentifikacijaKorisnika.autentifikacijaKorisnika(korisnik, lozinka, ds)) {
      throw new PogresnaAutentifikacija("Pogrešna kombinacija korisničkog imena i lozinke!");
    }
    PreparedStatement stmt = null;
    Korisnik dohvaceniKorisnik = null;
    String upit =
        "SELECT * FROM KORISNICI WHERE KORISNIK=?";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, traziKorisnika);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        dohvaceniKorisnik = new Korisnik(rs.getString("KORISNIK"), rs.getString("LOZINKA"), rs.getString("IME"), rs.getString("PREZIME"));
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
    return dohvaceniKorisnik;
  }

  /**
   * Konstruiraj upit za pretragu korisnika
   *
   * @param traziImeKorisnika ime za pretragu
   * @param traziPrezimeKorisnika prezime za pretragu
   * @param con veza na bazu podataka
   * @return PreparedStatement
   */
  private PreparedStatement konstruirajUpit(String traziImeKorisnika, String traziPrezimeKorisnika,
      Connection con) {
    PreparedStatement stmt = null;
    try {
      if (traziImeKorisnika != null && traziPrezimeKorisnika != null) {
        stmt = con.prepareStatement(
            "SELECT * FROM KORISNICI WHERE UPPER(IME) LIKE UPPER(?) AND UPPER(PREZIME) LIKE UPPER(?)");
        stmt.setString(1, "%" + traziImeKorisnika + "%");
        stmt.setString(2, "%" + traziPrezimeKorisnika + "%");
        return stmt;
      } else if (traziImeKorisnika != null && traziPrezimeKorisnika == null) {
        stmt = con.prepareStatement("SELECT * FROM KORISNICI WHERE UPPER(IME) LIKE UPPER(?)");
        stmt.setString(1, "%" + traziImeKorisnika + "%");
        return stmt;
      } else if (traziImeKorisnika == null && traziPrezimeKorisnika != null) {
        stmt = con.prepareStatement("SELECT * FROM KORISNICI WHERE UPPER(PREZIME) LIKE UPPER(?)");
        stmt.setString(1, "%" + traziPrezimeKorisnika + "%");
        return stmt;
      } else {
        return con.prepareStatement("SELECT * FROM KORISNICI");
      }
    } catch (SQLException e) {
      e.printStackTrace();
      return null;
    }
  }

}

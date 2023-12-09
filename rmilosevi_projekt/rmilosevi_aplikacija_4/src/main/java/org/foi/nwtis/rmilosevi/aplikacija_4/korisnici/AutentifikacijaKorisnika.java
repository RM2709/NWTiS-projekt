package org.foi.nwtis.rmilosevi.aplikacija_4.korisnici;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.sql.DataSource;
import jakarta.annotation.Resource;

/**
 * Klasa za provjeru autentifikacije korisnika
 */
public class AutentifikacijaKorisnika {
  
  /**
   * Autentifikacija korisnika.
   *
   * @param korisnik korisnik
   * @param lozinka lozinka
   * @param ds veza na bazu podataka
   * @return true, ako je uspješna
   */
  public boolean autentifikacijaKorisnika (String korisnik, String lozinka, DataSource ds) {
    PreparedStatement stmt = null;
    Integer brojKorisnika = 0;
    String upit =
        "SELECT COUNT(*) AS BROJ FROM KORISNICI WHERE KORISNIK=? AND LOZINKA=?";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, korisnik);
      stmt.setString(2, lozinka);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        brojKorisnika = rs.getInt("BROJ");
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
    if(brojKorisnika==1) {
      return true;
    }else {
      return false;
    }
  }

}

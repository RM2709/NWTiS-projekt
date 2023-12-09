package org.foi.nwtis.rmilosevi.aplikacija_4.filteri;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.foi.nwtis.podaci.Korisnik;
import jakarta.annotation.Resource;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Filter za Aplikaciju 4
 */
@WebFilter(urlPatterns = {"/*"})
public class FilterAP4 implements Filter {

  /** Filter konfiguracija */
  private FilterConfig filterConfig = null;

  /**  Spajanje na bazu podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Inicijalizira filter
   *
   * @param filterConfig filter konfiguracija
   * @throws ServletException servlet greška
   */
  @Override
  public void init(FilterConfig filterConfig) throws ServletException {
    this.filterConfig = filterConfig;
  }

  /**
   * Uništava instancu filtera.
   */
  @Override
  public void destroy() {
    this.filterConfig = null;
  }

  /**
   * Filtrira zahtjeve poslane na Aplikaciju 4
   *
   * @param request zahtjev
   * @param response odgovor
   * @param chain lanac zahtjeva
   * @throws IOException Javlja da se javila I/O pogreška
   * @throws ServletException servlet greška
   */
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest zahtjev = (HttpServletRequest) request;
    upisDnevnik(zahtjev);
    chain.doFilter(request, response);
  }

  /**
   * Piše zapis o zahtjevu u dnevnik
   *
   * @param zahtjev zahtjev
   */
  private void upisDnevnik(HttpServletRequest zahtjev) {
    String upit = "INSERT INTO DNEVNIK (ZAHTJEV, VRSTA, METODA, PARAMETRI) VALUES (?, ?, ?, ?)";
    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, zahtjev.getRequestURI().split("/")[2]);
      stmt.setString(2, "AP4");
      stmt.setString(3, zahtjev.getMethod());
      stmt.setString(4, "");
      stmt.executeUpdate();
    } catch (SQLException e) {
      System.out.println("Greska: " + e.getMessage());
    } finally {
      try {
        if (stmt != null && !stmt.isClosed()) {
          stmt.close();
        }
      } catch (SQLException e) {
        System.out.println("Greska: " + e.getMessage());
      }
    }
  }

}


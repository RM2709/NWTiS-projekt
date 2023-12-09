package org.foi.nwtis.rmilosevi.aplikacija_2.filteri;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.foi.nwtis.rmilosevi.aplikacija_2.posluzitelji.KomunikacijaPosluzitelj;
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
 * Filter za Aplikaciju 2
 */
@WebFilter(urlPatterns = {"/api/*"})
public class FilterAP2 implements Filter {

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
   * Filtrira zahtjeve poslane na Aplikaciju 2
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
    boolean ograniciRad = (boolean) filterConfig.getServletContext().getAttribute("ograniciRad");
    if (request instanceof HttpServletRequest) {
      HttpServletResponse odgovor = (HttpServletResponse) response;
      HttpServletRequest zahtjev = (HttpServletRequest) request;
      if (!ograniciRad || (ograniciRad && zahtjev.getRequestURI().contains("nadzor"))) {
        upisDnevnik(zahtjev);
        chain.doFilter(request, response);
        if (ograniciRad && zahtjev.getRequestURI().contains("INIT")) {
          if(provjeriPosluzitelja()) {
            filterConfig.getServletContext().setAttribute("ograniciRad", false);
          }
        } else if (!ograniciRad && (zahtjev.getRequestURI().contains("PAUZA")
            || zahtjev.getRequestURI().contains("KRAJ"))) {
          filterConfig.getServletContext().setAttribute("ograniciRad", true);
        }
      } else {
        odgovor.sendError(503, "Poslužitelj trenutno nije dostupan.");
      }
    }
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
      stmt.setString(1, zahtjev.getPathInfo());
      stmt.setString(2, "AP2");
      stmt.setString(3, zahtjev.getMethod());
      stmt.setString(4,
          (zahtjev.getQueryString() != null) ? zahtjev.getQueryString().replace("&", ", ")
              : ((zahtjev.getMethod() == "POST") ? "" : "NULL"));
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
  
  /**
   * Provjerava je li poslužitelj (Aplikacija 1) radi
   *
   * @return true, ako radi
   */
  private boolean provjeriPosluzitelja() {
    KomunikacijaPosluzitelj posluzitelj = new KomunikacijaPosluzitelj(filterConfig.getServletContext());
    String odgovor = posluzitelj.posaljiZahtjevNaPosluzitelj("STATUS");
    if(odgovor.contains("OK 1")) {
      return true; 
    }else {
      return false;
    }
  }

}


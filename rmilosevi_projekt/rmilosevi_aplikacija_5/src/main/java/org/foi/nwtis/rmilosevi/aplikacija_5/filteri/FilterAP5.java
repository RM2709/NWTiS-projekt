package org.foi.nwtis.rmilosevi.aplikacija_5.filteri;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.foi.nwtis.podaci.Dnevnik;
import org.foi.nwtis.podaci.Korisnik;
import org.foi.nwtis.rmilosevi.aplikacija_5.posluzitelji.KomunikacijaAP2;
import org.foi.nwtis.rmilosevi.aplikacija_5.posluzitelji.KomunikacijaPosluzitelj;
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
 * Filter za Aplikaciju 5
 */
@WebFilter(urlPatterns = {"/*"})
public class FilterAP5 implements Filter {

  /** Filter konfiguracija */
  private FilterConfig filterConfig = null;

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
   * Filtrira zahtjeve poslane na Aplikaciju 5
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
    KomunikacijaAP2 komunikacijaAP2 = new KomunikacijaAP2(filterConfig.getServletContext());
    KomunikacijaPosluzitelj posluzitelj = new KomunikacijaPosluzitelj(filterConfig.getServletContext());
    String odgovor = posluzitelj.posaljiZahtjevNaPosluzitelj("STATUS");
    if(odgovor.contains("OK 1")) {
      komunikacijaAP2.dodajZapis(new Dnevnik((zahtjev.getPathInfo()!=null) ? zahtjev.getPathInfo() : "/index", "AP5", zahtjev.getMethod(),
          (zahtjev.getQueryString() != null) ? zahtjev.getQueryString().replace("&", ", ")
              : ((zahtjev.getMethod() == "POST") ? "" : "NULL")));
    }
  }

}


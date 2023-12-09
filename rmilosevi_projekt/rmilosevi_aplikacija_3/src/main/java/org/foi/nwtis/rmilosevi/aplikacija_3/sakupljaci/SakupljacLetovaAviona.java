package org.foi.nwtis.rmilosevi.aplikacija_3.sakupljaci;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.sql.DataSource;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rest.klijenti.OSKlijentBP;
import org.foi.nwtis.rest.podaci.LetAviona;
import org.foi.nwtis.rmilosevi.aplikacija_3.zrna.JmsPosiljatelj;
import jakarta.servlet.ServletContext;


/**
 * Dretva za sakupljanje letova.
 */
public class SakupljacLetovaAviona implements Runnable {

  /**  Kontekst. */
  private ServletContext kontekst;

  /** Jms posiljatelj. */
  JmsPosiljatelj jmsPosiljatelj;

  /** trajanje ciklusa. */
  Long trajanjeCiklusa;

  /**  aerodromi. */
  List<String> aerodromi;

  /**  veza na bazu podataka. */
  javax.sql.DataSource ds;

  /** Početni i završni dan */
  long odDana, doDana, pocetniDan;

  /**  timer. */
  public Timer timer;

  /**  duljina dana. */
  static long duljinaDana = 60 * 60 * 24;


  /**
   * Konstruktor.
   *
   * @param context kontekst
   * @param jmsPosiljatelj jms posiljatelj
   * @param ds veza na bazu podataka
   */
  public SakupljacLetovaAviona(ServletContext context, JmsPosiljatelj jmsPosiljatelj,
      DataSource ds) {
    this.kontekst = context;
    this.jmsPosiljatelj = jmsPosiljatelj;
    this.trajanjeCiklusa = Long.parseLong(kontekst.getAttribute("ciklus.trajanje").toString());
    this.ds = ds;
  }

  /**
   * Izvršna metoda. Dohvaća sve letove sa odabranih aerodroma
   * na odabrane dan i šalje JMS poruku
   */
  public void run() {
    timer = new Timer();
    OSKlijentBP osKlijentBP =
        new OSKlijentBP(kontekst.getAttribute("OpenSkyNetwork.korisnik").toString(),
            kontekst.getAttribute("OpenSkyNetwork.lozinka").toString());
    aerodromi = dohvatiAerodrome();
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
    try {
      doDana = new SimpleDateFormat("dd.MM.yyyy")
          .parse(kontekst.getAttribute("preuzimanje.do").toString()).getTime() / 1000;
      odDana = izracunajPocetniDan();
      pocetniDan = odDana;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    TimerTask radDretve = new TimerTask() {
      @Override
      public void run() {
        List<LetAviona> letovi;
        if (odDana <= doDana) {
          System.out.println("Ciklus za dan " + format.format(new Date(odDana * 1000)));
          int brojacLetovaNaDan = 0;
          for (String aerodrom : aerodromi) {
            try {
              letovi = osKlijentBP.getDepartures(aerodrom, odDana, odDana + duljinaDana);
            } catch (NwtisRestIznimka e) {
              System.out.println(aerodrom + " odraden (nema letova)");
              continue;
            }
            System.out.println(aerodrom + " odraden");
            for (LetAviona let : letovi) {
              if (dodajLet(let) == 1) {
                brojacLetovaNaDan++;
              }
            }
          }
          String dan = format.format(new Date(odDana * 1000));
          jmsPosiljatelj
              .saljiPoruku("Na dan " + dan + " preuzeto ukupno " + brojacLetovaNaDan + " letova");
          odDana += duljinaDana;
        } else {
          System.out.println(
              "Raspon datuma u konfiguraciji netočan ILI baza podataka mora biti očišćena (datum zadnjeg unosa veći od 'preuzimanje.do')");
          this.cancel();
        }

        if (odDana > doDana && pocetniDan < doDana) {
          System.out
              .println("Dohvaćeni svi letovi od dana " + format.format(new Date(pocetniDan * 1000))
                  + " do dana " + kontekst.getAttribute("preuzimanje.do").toString());
          this.cancel();
        }
      }
    };
    timer.schedule(radDretve, Long.parseLong("1000"), trajanjeCiklusa * 1000);
  }

  /**
   * Dohvaća aerodrome iz SQL tablice za koje se dohvaćaju letovi
   *
   * @return the list
   */
  private List<String> dohvatiAerodrome() {
    List<String> aerodromi = new ArrayList<String>();
    PreparedStatement stmt = null;
    String upit = "SELECT ICAO FROM AERODROMI_LETOVI WHERE STATUS=TRUE";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        aerodromi.add(rs.getString("ICAO"));
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
   * Odabire od kojeg dana se kreće.
   *
   * @return dan od kojeg započinje dohvaćanje letova
   * @throws ParseException parse exception
   */
  private long izracunajPocetniDan() throws ParseException {
    long danZadnjegPreuzimanja = dajDanZadnjegPreuzimanja();
    long preuzimanjeOd = new SimpleDateFormat("dd.MM.yyyy")
        .parse(kontekst.getAttribute("preuzimanje.od").toString()).getTime() / 1000;
    if (danZadnjegPreuzimanja > preuzimanjeOd) {
      return danZadnjegPreuzimanja + duljinaDana;
    } else {
      return preuzimanjeOd;
    }
  }

  /**
   * Dohvaća dan zadnjeg leta zapisanog u bazu.
   *
   * @return dan u long (epoch) formatu
   */
  private long dajDanZadnjegPreuzimanja() {
    long polazak = 0;
    PreparedStatement stmt = null;
    String upit = "SELECT * FROM LETOVI_POLASCI WHERE ID=(SELECT max(ID) FROM LETOVI_POLASCI)";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        polazak = rs.getLong("FIRSTSEEN");
      }
    } catch (SQLException e) {
      return 0;
    } finally {
      try {
        if (stmt != null && !stmt.isClosed()) {
          stmt.close();
        }
      } catch (SQLException e) {
        return 0;
      }
    }
    SimpleDateFormat format = new SimpleDateFormat("dd.MM.yyyy");
    String polazakString = format.format(new Date(polazak*1000));
    long polazakDan = 0;
    try {
      polazakDan = format.parse(polazakString).getTime() / 1000;
    } catch (ParseException e) {
      e.printStackTrace();
    }
    return polazakDan;
  }

  /**
   * Dodaje let u bazu.
   *
   * @param let let
   * @return the broj dodanih letova u bazu
   */
  private int dodajLet(LetAviona let) {
    PreparedStatement stmt = null;
    int red = 0;
    String upit = "INSERT INTO LETOVI_POLASCI (ICAO24, FIRSTSEEN, ESTDEPARTUREAIRPORT, LASTSEEN, "
        + "ESTARRIVALAIRPORT, CALLSIGN, ESTDEPARTUREAIRPORTHORIZDISTANCE, ESTDEPARTUREAIRPORTVERTDISTANCE,"
        + " ESTARRIVALAIRPORTHORIZDISTANCE, ESTARRIVALAIRPORTVERTDISTANCE, DEPARTUREAIRPORTCANDIDATESCOUNT,"
        + " ARRIVALAIRPORTCANDIDATESCOUNT, STORED) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, NOW)";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, let.getIcao24());
      stmt.setInt(2, let.getFirstSeen());
      stmt.setString(3, let.getEstDepartureAirport());
      stmt.setInt(4, let.getLastSeen());
      stmt.setString(5, let.getEstArrivalAirport());
      stmt.setString(6, let.getCallsign());
      stmt.setInt(7, let.getEstDepartureAirportHorizDistance());
      stmt.setInt(8, let.getEstDepartureAirportVertDistance());
      stmt.setInt(9, let.getEstArrivalAirportHorizDistance());
      stmt.setInt(10, let.getEstArrivalAirportVertDistance());
      stmt.setInt(11, let.getDepartureAirportCandidatesCount());
      stmt.setInt(12, let.getArrivalAirportCandidatesCount());
      red = stmt.executeUpdate();
    } catch (SQLException e) {
      return 0;
    } finally {
      try {
        if (stmt != null && !stmt.isClosed()) {
          stmt.close();
        }
      } catch (SQLException e) {
        return 0;
      }
    }
    return red;
  }



}

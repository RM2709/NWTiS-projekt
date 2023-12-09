package org.foi.nwtis.rmilosevi.aplikacija_2.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Lokacija;
import org.foi.nwtis.podaci.Udaljenost;
import org.foi.nwtis.podaci.UdaljenostAerodrom;
import org.foi.nwtis.podaci.UdaljenostAerodromDrzava;
import org.foi.nwtis.rmilosevi.aplikacija_2.posluzitelji.KomunikacijaPosluzitelj;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Početna klasa za REST servise o aerodromima.
 */
@Path("aerodromi")
@RequestScoped
public class RestAerodromi {

  /** Kontekst aplikacije. */
  @Context
  private ServletContext kontekst;

  /** Spajanje na bazu podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Dohvaća sve aerodrome.
   *
   * @param traziDrzavu naziv drzave
   * @param traziNaziv naziv aerodroma
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   * @return odgovor u obliku Response objekta
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajSveAerodrome(@QueryParam("traziDrzavu") String traziDrzavu,
      @QueryParam("traziNaziv") String traziNaziv,
      @DefaultValue("1") @QueryParam("odBroja") Integer odBroja,
      @DefaultValue("20") @QueryParam("broj") Integer broj) {
    List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();
    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection()) {
      String upit = konstruirajUpitZaSveAerodrome(traziDrzavu, traziNaziv, odBroja, broj);
      stmt = con.prepareStatement(upit);
      stmt = postaviVrijednostiUpita(traziDrzavu, traziNaziv, odBroja, broj, stmt);
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
    Gson gson = new Gson();
    String podaci = gson.toJson(aerodromi);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }

  /**
   * Postavi vrijednosti upita.
   *
   * @param traziDrzavu naziv drzave
   * @param traziNaziv naziv aerodroma
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   * @param stmt objekt PreparedStatement
   * @return objekt PreparedStatement
   * @throws SQLException SQL greška
   */
  private PreparedStatement postaviVrijednostiUpita(String traziDrzavu, String traziNaziv,
      Integer odBroja, Integer broj, PreparedStatement stmt) throws SQLException {
    if (traziDrzavu == null && traziNaziv == null) {
      stmt.setInt(1, odBroja);
      stmt.setInt(2, broj);
    } else if (traziDrzavu != null && traziNaziv != null) {
      traziNaziv = "%" + traziNaziv + "%";
      stmt.setString(1, traziNaziv);
      stmt.setString(2, traziDrzavu);
      stmt.setInt(3, odBroja);
      stmt.setInt(4, broj);
    } else if (traziDrzavu == null && traziNaziv != null) {
      traziNaziv = "%" + traziNaziv + "%";
      stmt.setString(1, traziNaziv);
      stmt.setInt(2, odBroja);
      stmt.setInt(3, broj);
    } else if (traziDrzavu != null && traziNaziv == null) {
      stmt.setString(1, traziDrzavu);
      stmt.setInt(2, odBroja);
      stmt.setInt(3, broj);
    }
    return stmt;
  }

  /**
   * Konstruiraj upit za sve aerodrome.
   *
   * @param traziDrzavu naziv drzave
   * @param traziNaziv naziv aerodroma
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   * @return the string
   */
  private String konstruirajUpitZaSveAerodrome(String traziDrzavu, String traziNaziv,
      Integer odBroja, Integer broj) {
    String upit = "";
    if (traziDrzavu == null && traziNaziv == null) {
      upit =
          "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES FROM AIRPORTS ORDER BY ICAO OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    } else if (traziDrzavu != null && traziNaziv != null) {
      upit =
          "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES FROM AIRPORTS WHERE UPPER(NAME) LIKE UPPER(?) AND ISO_COUNTRY=? ORDER BY ICAO OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    } else if (traziDrzavu == null && traziNaziv != null) {
      upit =
          "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES FROM AIRPORTS WHERE UPPER(NAME) LIKE UPPER(?) ORDER BY ICAO OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    } else if (traziDrzavu != null && traziNaziv == null) {
      upit =
          "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES FROM AIRPORTS WHERE ISO_COUNTRY=? ORDER BY ICAO OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    }
    return upit;
  }

  /**
   * Dohvaća pojedini aerodrom.
   *
   * @param icao ICAO traženog aerodroma
   * @return objekt tipa Response
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icao}")
  public Response dajAerodrom(@PathParam("icao") String icao) {
    Aerodrom aerodrom = null;
    PreparedStatement stmt = null;
    String upit = "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES FROM AIRPORTS WHERE ICAO = ?";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icao);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String icaoProcitan = rs.getString("ICAO");
        String naziv = rs.getString("NAME");
        String drzava = rs.getString("ISO_COUNTRY");
        String latitude = rs.getString("COORDINATES").split(", ")[0];
        String longitude = rs.getString("COORDINATES").split(", ")[1];
        Lokacija lokacija = new Lokacija(latitude, longitude);
        aerodrom = new Aerodrom(icaoProcitan, naziv, drzava, lokacija);
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
    Gson gson = new Gson();
    String podaci = gson.toJson(aerodrom);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }

  /**
   * Vraća udaljenosti izmedu aerodoma.
   *
   * @param icaoFrom ICAO ishodišnog aerodroma
   * @param icaoTo ICAO odredišnog aerodroma
   * @return aerodrome u objektu tipa Response
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icaoOd}/{icaoDo}")
  public Response dajUdaljenostiIzmeduAerodoma(@PathParam("icaoOd") String icaoFrom,
      @PathParam("icaoDo") String icaoTo) {
    var udaljenosti = new ArrayList<Udaljenost>();
    PreparedStatement stmt = null;
    String upit =
        "SELECT ICAO_FROM, ICAO_TO, COUNTRY, DIST_CTRY FROM AIRPORTS_DISTANCE_MATRIX WHERE "
            + "ICAO_FROM = ? AND ICAO_TO = ?";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icaoFrom);
      stmt.setString(2, icaoTo);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String drzava = rs.getString("COUNTRY");
        float udaljenost = rs.getFloat("DIST_CTRY");
        var u = new Udaljenost(drzava, udaljenost);
        udaljenosti.add(u);
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
    Gson gson = new Gson();
    String podaci = gson.toJson(udaljenosti);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }


  /**
   * Vraća udaljenosti svih aerodroma od odabranog aerodroma.
   *
   * @param icao ICAO odabranog aerodroma
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   * @return udaljenosti u objektu tipa Response
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icao}/udaljenosti")
  public Response dajUdaljenostiOdAerodoma(@PathParam("icao") String icao,
      @DefaultValue("1") @QueryParam("odBroja") int odBroja,
      @DefaultValue("20") @QueryParam("broj") int broj) {
    var udaljenosti = new ArrayList<UdaljenostAerodrom>();
    PreparedStatement stmt = null;
    String upit =
        "SELECT DISTINCT ICAO_FROM, ICAO_TO, DIST_TOT FROM AIRPORTS_DISTANCE_MATRIX WHERE ICAO_FROM = ? "
            + "ORDER BY ICAO_FROM OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icao);
      stmt.setInt(2, odBroja);
      stmt.setInt(3, broj);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String icaoTo = rs.getString("ICAO_TO");
        float udaljenostUkupna = rs.getFloat("DIST_TOT");
        var u = new UdaljenostAerodrom(icaoTo, udaljenostUkupna);
        udaljenosti.add(u);
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
    Gson gson = new Gson();
    String podaci = gson.toJson(udaljenosti);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }


  /**
   * Vraća udaljenost izmedu aerodroma izračunatu pomoću poslužitelja iz AP1
   *
   * @param icaoOd ICAO ishodišnog aerodroma
   * @param icaoDo ICAO odredišnog aerodroma
   * @return udaljenost u objektu tipa Response
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icaoOd}/izracunaj/{icaoDo}")
  public Response izracunajUdaljenostIzmeduAerodroma(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo) {
    KomunikacijaPosluzitelj posluzitelj = new KomunikacijaPosluzitelj(kontekst);
    Gson gson = new Gson();
    Aerodrom odAerodroma = dohvatiAerodrom(icaoOd);
    Aerodrom doAerodroma = dohvatiAerodrom(icaoDo);
    String zahtjev = "UDALJENOST " + odAerodroma.getLokacija().getLatitude() + " "
        + odAerodroma.getLokacija().getLongitude() + " " + doAerodroma.getLokacija().getLatitude()
        + " " + doAerodroma.getLokacija().getLongitude();
    String udaljenost = posluzitelj.posaljiZahtjevNaPosluzitelj(zahtjev);
    String podaci = gson.toJson(udaljenost);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }

  /**
   * Vraća udaljenosti ishodišnog aerodroma i svih aerodroma iz države odredišnog aerodroma koje su
   * manje od udaljenosti između ishodišnog aerodroma i odredišnog aerodroma
   *
   * @param icaoOd ICAO ishodišnog aerodroma
   * @param icaoDo ICAO odredišnog aerodroma
   * @return udaljenosti u objektu tipa Response
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icaoOd}/udaljenost1/{icaoDo}")
  public Response izracunajUdaljenost1(@PathParam("icaoOd") String icaoOd,
      @PathParam("icaoDo") String icaoDo) {
    KomunikacijaPosluzitelj posluzitelj = new KomunikacijaPosluzitelj(kontekst);
    Gson gson = new Gson();
    List<UdaljenostAerodrom> udaljenosti = new ArrayList<UdaljenostAerodrom>();
    Aerodrom odAerodroma = dohvatiAerodrom(icaoOd);
    Aerodrom doAerodroma = dohvatiAerodrom(icaoDo);
    String zahtjev = "UDALJENOST " + odAerodroma.getLokacija().getLatitude() + " "
        + odAerodroma.getLokacija().getLongitude() + " " + doAerodroma.getLokacija().getLatitude()
        + " " + doAerodroma.getLokacija().getLongitude();
    Response odgovor;
    String udaljenost = posluzitelj.posaljiZahtjevNaPosluzitelj(zahtjev);
    if (udaljenost.contains("OK")) {
      udaljenost = udaljenost.split(" ")[1];
      String drzava = doAerodroma.getDrzava();
      List<Aerodrom> aerodromi = dohvatiAerodromeDrzave(drzava);
      for (Aerodrom aerodrom : aerodromi) {
        zahtjev = "UDALJENOST " + odAerodroma.getLokacija().getLatitude() + " "
            + odAerodroma.getLokacija().getLongitude() + " " + aerodrom.getLokacija().getLatitude()
            + " " + aerodrom.getLokacija().getLongitude();
        float udaljenostAerodroma =
            Float.parseFloat(posluzitelj.posaljiZahtjevNaPosluzitelj(zahtjev).split(" ")[1]);
        if (udaljenostAerodroma < Float.parseFloat(udaljenost)) {
          udaljenosti.add(new UdaljenostAerodrom(aerodrom.getIcao(), udaljenostAerodroma));
        }
      }
      String podaci = gson.toJson(udaljenosti);
      odgovor = Response.ok().entity(podaci).build();
    } else {
      String podaci = udaljenost;
      odgovor = Response.serverError().status(503, podaci).build();
    }
    return odgovor;
  }

  /**
   * Vraća udaljenosti ishodišnog aerodroma i svih aerodroma iz odabrane države koje su
   * manje od odabrane udaljenosti
   *
   * @param icaoOd ICAO ishodišnog aerodroma
   * @param drzava drzava
   * @param km udaljenost u km
   * @return udaljenosti u objektu tipa Response
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{icaoOd}/udaljenost2")
  public Response izracunajUdaljenost2(@PathParam("icaoOd") String icaoOd,
      @QueryParam("drzava") String drzava, @QueryParam("km") String km) {
    if (drzava == null || km == null) {
      return Response.serverError().status(400, "Parametri drzava i km ne smiju biti null").build();
    }
    KomunikacijaPosluzitelj posluzitelj = new KomunikacijaPosluzitelj(kontekst);
    Gson gson = new Gson();
    List<UdaljenostAerodrom> udaljenosti = new ArrayList<UdaljenostAerodrom>();
    Aerodrom odAerodroma = dohvatiAerodrom(icaoOd);
    List<Aerodrom> aerodromi = dohvatiAerodromeDrzave(drzava);
    for (Aerodrom aerodrom : aerodromi) {
      String zahtjev = "UDALJENOST " + odAerodroma.getLokacija().getLatitude() + " "
          + odAerodroma.getLokacija().getLongitude() + " " + aerodrom.getLokacija().getLatitude()
          + " " + aerodrom.getLokacija().getLongitude();
      float udaljenost =
          Float.parseFloat(posluzitelj.posaljiZahtjevNaPosluzitelj(zahtjev).split(" ")[1]);
      if (udaljenost < Float.parseFloat(km)) {
        udaljenosti.add(new UdaljenostAerodrom(aerodrom.getIcao(), udaljenost));
      }
    }
    String podaci = gson.toJson(udaljenosti);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }

  /**
   * Dohvaća aerodrom
   *
   * @param icao icao aerodroma
   * @return aerodrom
   */
  private Aerodrom dohvatiAerodrom(String icao) {
    Aerodrom aerodrom = null;
    PreparedStatement stmt = null;
    String upit = "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES FROM AIRPORTS WHERE ICAO = ?";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, icao);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String icaoProcitan = rs.getString("ICAO");
        String naziv = rs.getString("NAME");
        String drzava = rs.getString("ISO_COUNTRY");
        String latitude = rs.getString("COORDINATES").split(", ")[0];
        String longitude = rs.getString("COORDINATES").split(", ")[1];
        Lokacija lokacija = new Lokacija(latitude, longitude);
        aerodrom = new Aerodrom(icaoProcitan, naziv, drzava, lokacija);
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
    return aerodrom;
  }

  /**
   * Dohvaća aerodrome iz odabrane države
   *
   * @param drzava država
   * @return lista aerodroma iz odabrane države
   */
  public List<Aerodrom> dohvatiAerodromeDrzave(String drzava) {
    List<Aerodrom> aerodromi = new ArrayList<Aerodrom>();
    PreparedStatement stmt = null;
    String upit = "SELECT ICAO, NAME, ISO_COUNTRY, COORDINATES FROM AIRPORTS WHERE ISO_COUNTRY='"
        + drzava + "'";
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String icao = rs.getString("ICAO");
        String naziv = rs.getString("NAME");
        String procitanaDrzava = rs.getString("ISO_COUNTRY");
        String latitude = rs.getString("COORDINATES").split(", ")[0];
        String longitude = rs.getString("COORDINATES").split(", ")[1];
        Lokacija lokacija = new Lokacija(latitude, longitude);
        aerodromi.add(new Aerodrom(icao, naziv, procitanaDrzava, lokacija));
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

}

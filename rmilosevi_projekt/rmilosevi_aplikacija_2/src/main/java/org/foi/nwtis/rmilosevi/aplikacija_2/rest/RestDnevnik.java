package org.foi.nwtis.rmilosevi.aplikacija_2.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.podaci.Dnevnik;
import com.google.gson.Gson;
import jakarta.annotation.Resource;
import jakarta.enterprise.context.RequestScoped;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

/**
 * Početna klasa za REST servise o dnevniku.
 */
@Path("dnevnik")
@RequestScoped
public class RestDnevnik {

  /**  Kontekst aplikacije. */
  @Context
  private ServletContext kontekst;

  /**  Spajanje na bazu podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Daj podatke iz dnevnika.
   *
   * @param vrsta vrsta upisa (AP2, AP4, AP5)
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   * @return odgovor u obliku Response objekta
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response dajPodatkeIzDnevnika(@QueryParam("vrsta") String vrsta,
      @DefaultValue("1") @QueryParam("odBroja") Integer odBroja,
      @DefaultValue("20") @QueryParam("broj") Integer broj) {
    List<Dnevnik> zapisi = new ArrayList<Dnevnik>();
    PreparedStatement stmt = null;
    try (Connection con = ds.getConnection()) {
      stmt = konstruirajUpitZaDnevnik(vrsta, odBroja, broj, con);
      ResultSet rs = stmt.executeQuery();
      while (rs.next()) {
        String zahtjev = rs.getString("ZAHTJEV");
        String procitanaVrsta = rs.getString("VRSTA");
        String metoda = rs.getString("METODA");
        String parametri = rs.getString("PARAMETRI");
        zapisi.add(new Dnevnik(zahtjev, procitanaVrsta, metoda, parametri));
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
    String podaci = gson.toJson(zapisi);
    Response odgovor = Response.ok().entity(podaci).build();
    return odgovor;
  }

  /**
   * Konstruira SQL upit za dnevnik
   *
   * @param vrsta the vrsta
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   * @param con Veza na bazu podataka
   * @return PreparedStatement
   * @throws SQLException SQL greška
   */
  private PreparedStatement konstruirajUpitZaDnevnik(String vrsta, Integer odBroja, Integer broj,
      Connection con) throws SQLException {
    String upit;
    PreparedStatement stmt;
    if(vrsta==null) {
      vrsta="";
    }
    if (vrsta.contentEquals("AP2") || vrsta.contentEquals("AP4") || vrsta.contentEquals("AP5")) {
      upit = "SELECT * FROM DNEVNIK WHERE VRSTA=? ORDER BY ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
      stmt = con.prepareStatement(upit);
      stmt.setString(1, vrsta);
      stmt.setInt(2, odBroja);
      stmt.setInt(3, broj);    
    } else {
      upit = "SELECT * FROM DNEVNIK ORDER BY ID OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
      stmt = con.prepareStatement(upit);
      stmt.setInt(1, odBroja);
      stmt.setInt(2, broj);
    }
    return stmt;
  }

  /**
   * Dodaje zapis u dnevnik
   *
   * @param zapis zapis
   * @return odgovor u obliku Response objekta
   */
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  public Response dodajZapis(Dnevnik zapis) {
    String upit = "INSERT INTO DNEVNIK (ZAHTJEV, VRSTA, METODA, PARAMETRI) VALUES (?, ?, ?, ?)";
    PreparedStatement stmt = null;
    Integer uspjeh = null;
    try (Connection con = ds.getConnection()) {
      stmt = con.prepareStatement(upit);
      stmt.setString(1, zapis.zahtjev());
      stmt.setString(2, zapis.vrsta());
      stmt.setString(3, zapis.metoda());
      stmt.setString(4, zapis.parametri());
      uspjeh = stmt.executeUpdate();
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
    Response odgovor = Response.ok().entity(uspjeh).build();
    return odgovor;
  }

}

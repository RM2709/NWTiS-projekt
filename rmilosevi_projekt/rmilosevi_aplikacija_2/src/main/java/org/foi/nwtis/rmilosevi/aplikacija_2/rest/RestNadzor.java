package org.foi.nwtis.rmilosevi.aplikacija_2.rest;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.podaci.Aerodrom;
import org.foi.nwtis.podaci.Lokacija;
import org.foi.nwtis.podaci.OdgovorPosluzitelja;
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
 * Početna klasa za REST servise o nadzoru.
 */
@Path("nadzor")
@RequestScoped
public class RestNadzor {

  /**  Kontekst aplikacije. */
  @Context
  private ServletContext kontekst;

  /**  Spajanje na bazu podataka. */
  @Resource(lookup = "java:app/jdbc/nwtis_bp")
  javax.sql.DataSource ds;

  /**
   * Šalje komandu tipa STATUS na poslužitelj
   *
   * @return Odgovor poslužitelja
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  public Response saljiStatus() {
    KomunikacijaPosluzitelj posluzitelj = new KomunikacijaPosluzitelj(kontekst);
    String odgovor = posluzitelj.posaljiZahtjevNaPosluzitelj("STATUS");
    if (odgovor.contains("OK")) {
      OdgovorPosluzitelja odgovorPosluzitelja;
      if(Float.parseFloat(odgovor.split(" ")[1])==1) {
        odgovorPosluzitelja = new OdgovorPosluzitelja(odgovor.split(" ")[1], "aktivan");
      }else {
        odgovorPosluzitelja = new OdgovorPosluzitelja(odgovor.split(" ")[1], "pauza");
      }
      return Response.ok().entity(odgovorPosluzitelja).build();
    } else {
      return Response.serverError().status(400, odgovor).build();
    }
  }
  
  /**
   * Šalje komande tipa INIT, PAUZA, KRAJ na poslužitelj
   *
   * @param komanda komanda
   * @return Odgovor poslužitelja
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("{komanda}")
  public Response saljiKomandu(@PathParam("komanda") String komanda) {
    KomunikacijaPosluzitelj posluzitelj = new KomunikacijaPosluzitelj(kontekst);
    String odgovor = posluzitelj.posaljiZahtjevNaPosluzitelj(komanda.toUpperCase());
    if (odgovor.contains("OK")) {
      OdgovorPosluzitelja odgovorPosluzitelja;
      if(komanda.contains("PAUZA")) {
        odgovorPosluzitelja = new OdgovorPosluzitelja(odgovor.split(" ")[1], odgovor.split(" ")[0]);
      }else {
        odgovorPosluzitelja = new OdgovorPosluzitelja("", "OK");
      }
      return Response.ok().entity(odgovorPosluzitelja).build();
    } else {
      return Response.serverError().status(400, odgovor).build();
    }
  }
  
  /**
   * Šalje komandu tipa INFO na poslužitelj
   *
   * @param vrsta vrsta komande INFO (DA ili NE)
   * @return Odgovor poslužitelja
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Path("INFO/{vrsta}")
  public Response saljiKomanduInfo(@PathParam("vrsta") String vrsta) {
    KomunikacijaPosluzitelj posluzitelj = new KomunikacijaPosluzitelj(kontekst);
    String odgovor = posluzitelj.posaljiZahtjevNaPosluzitelj("INFO " + vrsta.toUpperCase());
    if (odgovor.contains("OK")) {
      OdgovorPosluzitelja odgovorPosluzitelja = new OdgovorPosluzitelja("", "OK");;
      return Response.ok().entity(odgovorPosluzitelja).build();
    } else {
      return Response.serverError().status(400, odgovor).build();
    }
  }

}

/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package org.foi.nwtis.rmilosevi.aplikacija_5.mvc;

import java.util.ArrayList;
import java.util.List;
import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.Aerodrom;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.Aerodromi;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsKorisnici.endpoint.Korisnik;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsLetovi.endpoint.LetAviona;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsLetovi.endpoint.Letovi;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsLetovi.endpoint.PogresnaAutentifikacija_Exception;
import org.foi.nwtis.rmilosevi.aplikacija_5.posluzitelji.KomunikacijaAP2;
import org.foi.nwtis.rmilosevi.aplikacija_5.sakupljaci.SakupljacJmsPoruka;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.xml.ws.WebServiceRef;

/**
 * Klasa za kontrolu zahtjeva letova.
 *
 * @author rmilosevi
 */
@Controller
@Path("letovi")
@RequestScoped
public class KontrolerLetovi {

  /** Adresa servisa letovi */
  @WebServiceRef(wsdlLocation = "http://localhost:8080/rmilosevi_aplikacija_4/letovi?wsdl")
  private Letovi service;

  /** Model. */
  @Inject
  private Models model;

  /** Kontekst. */
  @Context
  private ServletContext kontekst;

  /** sakupljac jms poruka. */
  @EJB
  SakupljacJmsPoruka sakupljacJmsPoruka;

  /**
   * Prikazuje početnu stranicu za rad s letovima
   */
  @GET
  @View("56.jsp")
  public void pocetakLetovi() {
    model.put("kontekst", kontekst);
  }

  /**
   * Prikazuje polaske s odabranog aerodroma u odabranom intervalu
   *
   * @param icao icao aerodroma
   * @param datumOd datum početka intervala
   * @param datumDo datum kraj intervala
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   */
  @GET
  @Path("polasciInterval")
  @View("561.jsp")
  public void polasciInterval(@QueryParam("icao") String icao,
      @QueryParam("datumOd") String datumOd, @QueryParam("datumDo") String datumDo,
      @QueryParam("odBroja") Integer odBroja, @QueryParam("broj") Integer broj) {
    var port = service.getWsLetoviPort();
    Korisnik prijavljeniKorisnik = (Korisnik) kontekst.getAttribute("korisnik");
    List<LetAviona> letovi;
    model.put("icao", icao);
    model.put("datumOd", datumOd);
    model.put("datumDo", datumDo);
    model.put("odBroja", odBroja);
    model.put("broj", broj);
    if (icao != null && datumOd != null && datumDo != null) {
      try {
        letovi = port.dajPolaskeInterval(prijavljeniKorisnik.getKorime(),
            prijavljeniKorisnik.getLozinka(), icao, datumOd, datumDo, odBroja, broj);
        if (letovi.isEmpty()) {
          model.put("letovi", new ArrayList<LetAviona>());
        } else {
          model.put("letovi", letovi);
        }
        model.put("kontekst", kontekst);
      } catch (PogresnaAutentifikacija_Exception e) {
        model.put("kontekst", kontekst);
        model.put("letovi", new ArrayList<LetAviona>());
      }
    } else {
      model.put("kontekst", kontekst);
      model.put("letovi", null);
    }
  }

  /**
   * Prikazuje polaske s odabranog aerodroma na odabrani datum iz tablice preuzetih letova.
   *
   * @param icao icao aerodroma
   * @param datum datum
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   */
  @GET
  @Path("polasciDatum")
  @View("562.jsp")
  public void polasciDatum(@QueryParam("icao") String icao, @QueryParam("datum") String datum,
      @QueryParam("odBroja") Integer odBroja, @QueryParam("broj") Integer broj) {
    var port = service.getWsLetoviPort();
    Korisnik prijavljeniKorisnik = (Korisnik) kontekst.getAttribute("korisnik");
    List<LetAviona> letovi;
    model.put("icao", icao);
    model.put("datum", datum);
    model.put("odBroja", odBroja);
    model.put("broj", broj);
    if (icao != null && datum != null) {
      try {
        letovi = port.dajPolaskeNaDan(prijavljeniKorisnik.getKorime(),
            prijavljeniKorisnik.getLozinka(), icao, datum, odBroja, broj);
        if (letovi.isEmpty()) {
          model.put("letovi", new ArrayList<LetAviona>());
        } else {
          model.put("letovi", letovi);
        }
        model.put("kontekst", kontekst);
      } catch (PogresnaAutentifikacija_Exception e) {
        model.put("kontekst", kontekst);
        model.put("letovi", new ArrayList<LetAviona>());
      }
    } else {
      model.put("kontekst", kontekst);
      model.put("letovi", null);
    }
  }

  /**
   * Prikazuje polaske s odabranog aerodroma na odabrani datum sa servisa Open Sky
   *
   * @param icao icao aerodroma
   * @param datum datum
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   */
  @GET
  @Path("polasciDatumOS")
  @View("563.jsp")
  public void polasciDatumOS(@QueryParam("icao") String icao, @QueryParam("datum") String datum) {
    var port = service.getWsLetoviPort();
    Korisnik prijavljeniKorisnik = (Korisnik) kontekst.getAttribute("korisnik");
    List<LetAviona> letovi;
    model.put("icao", icao);
    model.put("datum", datum);
    if (icao != null && datum != null) {
      try {
        letovi = port.dajPolaskeNaDanOS(prijavljeniKorisnik.getKorime(),
            prijavljeniKorisnik.getLozinka(), icao, datum);
        if (letovi.isEmpty()) {
          model.put("letovi", new ArrayList<LetAviona>());
        } else {
          model.put("letovi", letovi);
        }
        model.put("kontekst", kontekst);
      } catch (PogresnaAutentifikacija_Exception e) {
        model.put("kontekst", kontekst);
        model.put("letovi", new ArrayList<LetAviona>());
      }
    } else {
      model.put("kontekst", kontekst);
      model.put("letovi", null);
    }
  }

}

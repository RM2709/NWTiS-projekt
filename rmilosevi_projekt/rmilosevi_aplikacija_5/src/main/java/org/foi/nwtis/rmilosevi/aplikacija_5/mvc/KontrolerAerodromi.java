/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package org.foi.nwtis.rmilosevi.aplikacija_5.mvc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeMap;
import org.foi.nwtis.podaci.Udaljenost;
import org.foi.nwtis.podaci.UdaljenostAerodrom;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.Aerodrom;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.Aerodromi;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsKorisnici.endpoint.Korisnik;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsKorisnici.endpoint.PogresnaAutentifikacija_Exception;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsMeteo.endpoint.Exception_Exception;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsMeteo.endpoint.Meteo;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsMeteo.endpoint.MeteoPodaci;
import org.foi.nwtis.rmilosevi.aplikacija_5.posluzitelji.KomunikacijaAP2;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.xml.ws.WebServiceRef;

/**
 * Klasa za kontrolu zahtjeva aerodroma.
 *
 * @author rmilosevi
 */
@Controller
@Path("aerodromi")
@RequestScoped
public class KontrolerAerodromi {

  /**  Adresa servisa aerodromi. */
  @WebServiceRef(wsdlLocation = "http://localhost:8080/rmilosevi_aplikacija_4/aerodromi?wsdl")
  private Aerodromi service;

  /**  Adresa servisa meteo. */
  @WebServiceRef(wsdlLocation = "http://localhost:8080/rmilosevi_aplikacija_4/meteo?wsdl")
  private Meteo serviceMeteo;

  /**  Model. */
  @Inject
  private Models model;

  /**  Kontekst. */
  @Context
  private ServletContext kontekst;

  /**
   * Početni izbornik za aerodrome.
   */
  @GET
  @View("55.jsp")
  public void pocetakAerodromi() {
    model.put("kontekst", kontekst);
  }

  /**
   * Pregled aerodroma.
   *
   * @param traziDrzavu naziv drzave
   * @param traziNaziv naziv aerodroma
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   */
  @GET
  @Path("pregledAerodroma")
  @View("551.jsp")
  public void pregledAerodroma(@QueryParam("traziNaziv") String traziNaziv,
      @QueryParam("traziDrzavu") String traziDrzavu, @QueryParam("odBroja") Integer odBroja,
      @QueryParam("broj") Integer broj) {
    model.put("traziNaziv", traziNaziv);
    model.put("traziDrzavu", traziDrzavu);
    KomunikacijaAP2 komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    List<Aerodrom> aerodromi =
        komunikacijaAP2.dajSveAerodrome(traziNaziv, traziDrzavu, odBroja, broj);
    model.put("kontekst", kontekst);
    model.put("aerodromi", aerodromi);
    model.put("odBroja", odBroja);
    model.put("broj", broj);
  }

  /**
   * Dodaj aerodrom u skup aerodroma za preuzimanje letova.
   *
   * @param icao icao aerodroma
   * @return preusmjeravanje na pogled aerodroma za preuzimanje letova
   */
  @GET
  @Path("dodajAerodrom")
  public String dodajAerodrom(@QueryParam("icao") String icao) {
    var port = service.getWsAerodromiPort();
    Korisnik prijavljeniKorisnik = (Korisnik) kontekst.getAttribute("korisnik");
    try {
      port.dodajAerodromZaLetove(prijavljeniKorisnik.getKorime(), prijavljeniKorisnik.getLozinka(),
          icao);
    } catch (org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.PogresnaAutentifikacija_Exception e) {
      e.printStackTrace();
    }
    return "redirect:aerodromi/pregledAerodromaZaLetove";
  }

  /**
   * Prikaz podataka o aerodromu
   *
   * @param icao icao aerodroma
   */
  @GET
  @Path("podaciAerodrom/{icao}")
  @View("552.jsp")
  public void podaciAerodrom(@PathParam("icao") String icao) {
    KomunikacijaAP2 komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    Aerodrom aerodrom = komunikacijaAP2.dajAerodrom(icao);
    Korisnik prijavljeniKorisnik = (Korisnik) kontekst.getAttribute("korisnik");
    var port = serviceMeteo.getWsMeteoPort();
    try {
      MeteoPodaci meteoPodaci =
          port.dajMeteo(prijavljeniKorisnik.getKorime(), prijavljeniKorisnik.getLozinka(), icao);
      model.put("kontekst", kontekst);
      model.put("aerodrom", aerodrom);
      model.put("meteoPodaci", meteoPodaci);
    } catch (Exception_Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Pregled aerodroma za preuzimanje letova
   */
  @GET
  @Path("pregledAerodromaZaLetove")
  @View("553.jsp")
  public void pregledAerodromaZaLetove() {
    Korisnik prijavljeniKorisnik = (Korisnik) kontekst.getAttribute("korisnik");
    var port = service.getWsAerodromiPort();
    TreeMap<String, Boolean> aerodromiStatus = new TreeMap<>();
    HashMap<String, Aerodrom> aerodromiString = new HashMap<>();
    try {
      for (Aerodrom aerodrom : port.dajAerodromeZaLetove(prijavljeniKorisnik.getKorime(),
          prijavljeniKorisnik.getLozinka())) {
        aerodromiStatus.put(aerodrom.getIcao(), port.dajStatus(prijavljeniKorisnik.getKorime(),
            prijavljeniKorisnik.getLozinka(), aerodrom.getIcao()));
        aerodromiString.put(aerodrom.getIcao(), aerodrom);
      }
      model.put("kontekst", kontekst);
      model.put("aerodromi", aerodromiStatus);
      model.put("aerodromiString", aerodromiString);
    } catch (org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.PogresnaAutentifikacija_Exception e) {
      e.printStackTrace();
    } catch (org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.Exception_Exception e) {
      e.printStackTrace();
    }
  }

  /**
   * Aktivacija aerodroma za preuzimanje letova
   *
   * @param icao icao aerodroma
   * @return preusmjeravanje na pregled aerodroma za preuzimanje letova
   */
  @GET
  @Path("aktivirajAerodrom")
  public String aktivirajAerodrom(@QueryParam("icao") String icao) {
    var port = service.getWsAerodromiPort();
    Korisnik prijavljeniKorisnik = (Korisnik) kontekst.getAttribute("korisnik");
    try {
      port.aktivirajAerodromZaLetove(prijavljeniKorisnik.getKorime(),
          prijavljeniKorisnik.getLozinka(), icao);
    } catch (org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.PogresnaAutentifikacija_Exception e) {
      e.printStackTrace();
    }
    return "redirect:aerodromi/pregledAerodromaZaLetove";
  }

  /**
   * Pauziranje aerodroma za preuzimanje letova
   *
   * @param icao icao aerodroma
   * @return preusmjeravanje na pregled aerodroma za preuzimanje letova
   */
  @GET
  @Path("pauzirajAerodrom")
  public String pauzirajAerodrom(@QueryParam("icao") String icao) {
    var port = service.getWsAerodromiPort();
    Korisnik prijavljeniKorisnik = (Korisnik) kontekst.getAttribute("korisnik");
    try {
      port.pauzirajAerodromZaLetove(prijavljeniKorisnik.getKorime(),
          prijavljeniKorisnik.getLozinka(), icao);
    } catch (org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.PogresnaAutentifikacija_Exception e) {
      e.printStackTrace();
    }
    return "redirect:aerodromi/pregledAerodromaZaLetove";
  }

  /**
   * Prikazuje udaljenosti izmedu aerodoma po državama
   *
   * @param icaoOd ICAO ishodišnog aerodroma
   * @param icaoDo ICAO odredišnog aerodroma
   */
  @GET
  @Path("udaljenostiDrzaveAerodromi")
  @View("554.jsp")
  public void udaljenostiDrzaveAerodromi(@QueryParam("icaoOd") String icaoOd,
      @QueryParam("icaoDo") String icaoDo) {
    KomunikacijaAP2 komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    List<Udaljenost> udaljenosti = komunikacijaAP2.dajUdaljenostiDrzaveAerodromi(icaoOd, icaoDo);
    if (icaoOd == null || icaoDo == null) {
      udaljenosti = null;
    }
    model.put("kontekst", kontekst);
    model.put("udaljenosti", udaljenosti);
  }

  /**
   * Prikazuje udaljenost izmedu aerodroma izračunatu pomoću poslužitelja iz AP1
   *
   * @param icaoOd ICAO ishodišnog aerodroma
   * @param icaoDo ICAO odredišnog aerodroma
   */
  @GET
  @Path("udaljenostAerodromi")
  @View("555.jsp")
  public void udaljenostAerodromi(@QueryParam("icaoOd") String icaoOd,
      @QueryParam("icaoDo") String icaoDo) {
    KomunikacijaAP2 komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    if (icaoOd != null && icaoDo != null) {
      try {
        String udaljenost = komunikacijaAP2.dajUdaljenostAerodroma(icaoOd, icaoDo);
        udaljenost = udaljenost.split(" ")[1].replace('"', ' ').trim();
        model.put("kontekst", kontekst);
        model.put("udaljenost", udaljenost);
      } catch (Exception e) {
        model.put("kontekst", kontekst);
        model.put("udaljenost", null);
      }
    } else {
      model.put("kontekst", kontekst);
      model.put("udaljenost", "index");
    }
  }

  /**
   * Prikazuje udaljenosti ishodišnog aerodroma i svih aerodroma iz države odredišnog aerodroma koje su
   * manje od udaljenosti između ishodišnog aerodroma i odredišnog aerodroma
   *
   * @param icaoOd ICAO ishodišnog aerodroma
   * @param icaoDo ICAO odredišnog aerodroma
   */
  @GET
  @Path("aerodromiBliziOd")
  @View("556.jsp")
  public void aerodromiBliziOd(@QueryParam("icaoOd") String icaoOd,
      @QueryParam("icaoDo") String icaoDo) {
    KomunikacijaAP2 komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    if (icaoOd != null && icaoDo != null) {
      try {
        List<UdaljenostAerodrom> udaljenosti = komunikacijaAP2.dajAerodromeBlizeOd(icaoOd, icaoDo);
        model.put("kontekst", kontekst);
        model.put("udaljenosti", udaljenosti);
      } catch (Exception e) {
        model.put("kontekst", kontekst);
        model.put("udaljenosti", new ArrayList<UdaljenostAerodrom>());
      }
    } else {
      model.put("kontekst", kontekst);
      model.put("udaljenosti", null);
    }
  }

  /**
   * Prikazuje udaljenosti ishodišnog aerodroma i svih aerodroma iz odabrane države koje su
   * manje od odabrane udaljenosti
   *
   * @param icao ICAO ishodišnog aerodroma
   * @param drzava država
   * @param km udaljenost u km
   */
  @GET
  @Path("udaljenostAerodromaDrzaveManjaOd")
  @View("557.jsp")
  public void udaljenostAerodromaDrzaveManjaOd(@QueryParam("icao") String icao,
      @QueryParam("drzava") String drzava, @QueryParam("km") String km) {
    KomunikacijaAP2 komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    if (icao != null && drzava != null && km != null) {
      try {
        List<UdaljenostAerodrom> udaljenosti =
            komunikacijaAP2.dajAerodromeBlizeOdKm(icao, drzava, km);
        model.put("kontekst", kontekst);
        model.put("udaljenosti", udaljenosti);
      } catch (Exception e) {
        e.printStackTrace();
        model.put("kontekst", kontekst);
        model.put("udaljenosti", new ArrayList<UdaljenostAerodrom>());
      }
    } else {
      model.put("kontekst", kontekst);
      model.put("udaljenosti", null);
    }
  }

}

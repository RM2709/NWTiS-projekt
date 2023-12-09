/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package org.foi.nwtis.rmilosevi.aplikacija_5.mvc;

import java.util.List;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsKorisnici.endpoint.Korisnici;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsKorisnici.endpoint.Korisnik;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsKorisnici.endpoint.PogresnaAutentifikacija_Exception;
import org.foi.nwtis.rmilosevi.aplikacija_5.posluzitelji.KomunikacijaAP2;
import org.foi.nwtis.rmilosevi.aplikacija_5.posluzitelji.KomunikacijaPosluzitelj;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.View;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.FormParam;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.xml.ws.WebServiceRef;

/**
 * Klasa za kontrolu zahtjeva poslužitelja.
 */
@Controller
@Path("posluzitelj")
@RequestScoped
public class KontrolerPosluzitelj {

  /** Model. */
  @Inject
  private Models model;

  /** Kontekst. */
  @Context
  private ServletContext kontekst;

  /** Komunikacija s AP2 */
  KomunikacijaAP2 komunikacijaAP2;

  /**
   * Prikaz početne stranice za rad s poslužiteljem
   */
  @GET
  @View("53.jsp")
  public void pocetakPosluzitelj() {
    model.put("kontekst", kontekst);
  }

  /**
   * Prikaz statusa poslužitelja
   */
  @GET
  @Path("status")
  @View("53.jsp")
  public void status() {
    komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    String odgovor = komunikacijaAP2.saljiStatus();
    model.put("poruka", odgovor);
    model.put("kontekst", kontekst);
  }

  /**
   * Slanje komande KRAJ, tj. gašenje poslužitelja
   */
  @GET
  @Path("kraj")
  @View("53.jsp")
  public void kraj() {
    komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    String odgovor = komunikacijaAP2.saljiKomandu("KRAJ");
    model.put("poruka", odgovor);
    model.put("kontekst", kontekst);
  }

  /**
   * Slanje komande INIT, tj. aktivacija poslužitelja
   */
  @GET
  @Path("init")
  @View("53.jsp")
  public void init() {
    komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    String odgovor = komunikacijaAP2.saljiKomandu("INIT");
    model.put("poruka", odgovor);
    model.put("kontekst", kontekst);
  }

  /**
   * Slanje komande PAUZA, tj. pauziranje poslužitelja
   */
  @GET
  @Path("pauza")
  @View("53.jsp")
  public void pauza() {
    komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    String odgovor = komunikacijaAP2.saljiKomandu("PAUZA");
    model.put("poruka", odgovor);
    model.put("kontekst", kontekst);
  }

  /**
   * Slanje komande INFO DA, tj. odobravanje ispisa primljenih zahtjeva poslužitelja
   */
  @GET
  @Path("infoDa")
  @View("53.jsp")
  public void infoDa() {
    komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    String odgovor = komunikacijaAP2.saljiKomanduInfo("DA");
    model.put("poruka", odgovor);
    model.put("kontekst", kontekst);
  }

  /**
   * Slanje komande INFO NE, tj. zabrana ispisa primljenih zahtjeva poslužitelja
   */
  @GET
  @Path("infoNe")
  @View("53.jsp")
  public void infoNe() {
    komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    String odgovor = komunikacijaAP2.saljiKomanduInfo("NE");
    model.put("poruka", odgovor);
    model.put("kontekst", kontekst);
  }


}



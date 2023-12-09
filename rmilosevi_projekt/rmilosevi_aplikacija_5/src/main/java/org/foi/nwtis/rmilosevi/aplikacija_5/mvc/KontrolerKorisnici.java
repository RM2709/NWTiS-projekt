/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package org.foi.nwtis.rmilosevi.aplikacija_5.mvc;

import java.util.List;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsKorisnici.endpoint.Korisnici;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsKorisnici.endpoint.Korisnik;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsKorisnici.endpoint.PogresnaAutentifikacija_Exception;
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
import jakarta.xml.ws.WebServiceRef;

/**
 * Klasa za kontrolu zahtjeva korisnika.
 */
@Controller
@Path("korisnici")
@RequestScoped
public class KontrolerKorisnici {

  /**  Adresa servisa korisnici */
  @WebServiceRef(wsdlLocation = "http://localhost:8080/rmilosevi_aplikacija_4/korisnici?wsdl")
  private Korisnici service;

  /**  Model. */
  @Inject
  private Models model;

  /**  Kontekst. */
  @Context
  private ServletContext kontekst;

  /**
   * Prikazuje početnu stranicu za rad s korisnicima
   */
  @GET
  @View("52.jsp")
  public void pocetakKorisnici() {
    model.put("kontekst", kontekst);
  }

  /**
   * Odjavljuje korisnika
   */
  @GET
  @Path("odjava")
  @View("522.jsp")
  public void odjava() {
    kontekst.setAttribute("korisnik", null);
    model.put("kontekst", kontekst);
  }

  /**
   * Prikazuje stranicu za prijavu korisnika
   */
  @GET
  @Path("prijava")
  @View("522.jsp")
  public void prijava() {
    model.put("kontekst", kontekst);
  }

  /**
   * Prijavljuje korisnika
   *
   * @param korime korisničko ime
   * @param lozinka lozinka
   */
  @POST
  @Path("prijavi")
  @View("51.jsp")
  public void prijavi(@FormParam("korime") String korime, @FormParam("lozinka") String lozinka) {
    var port = service.getWsKorisniciPort();
    try {
      Korisnik korisnik = port.dajKorisnika(korime, lozinka, korime);
      kontekst.setAttribute("korisnik", korisnik);
      model.put("kontekst", kontekst);
    } catch (Exception e) {
      model.put("neuspjesnaPrijava", true);
      kontekst.setAttribute("korisnik", null);
      model.put("kontekst", kontekst);
    }
  }

  /**
   * Prikazuje stranicu za registraciju novih korisnika
   */
  @GET
  @Path("registracija")
  @View("521.jsp")
  public void registracija() {
    model.put("kontekst", kontekst);
  }

  /**
   * Registrira novog korisnika
   *
   * @param ime ime
   * @param prezime prezime
   * @param korime korisničko ime
   * @param lozinka lozinka
   */
  @POST
  @Path("registriraj")
  @View("52.jsp")
  public void registriraj(@FormParam("ime") String ime, @FormParam("prezime") String prezime,
      @FormParam("korime") String korime, @FormParam("lozinka") String lozinka) {
    var port = service.getWsKorisniciPort();
    try {
      Korisnik korisnik = new Korisnik();
      korisnik.setIme(ime);
      korisnik.setPrezime(prezime);
      korisnik.setKorime(korime);
      korisnik.setLozinka(lozinka);
      port.dodajKorisnika(korisnik);
      model.put("kontekst", kontekst);
    } catch (Exception e) {
      model.put("kontekst", kontekst);
    }
  }

  /**
   * Prikazuje popis svih registriranih korisnika i omogućuje pretragu po imenu i prezimenu.
   *
   * @param traziImeKorisnika ime korisnika
   * @param traziPrezimeKorisnika prezime korisnika
   */
  @GET
  @Path("pregled")
  @View("523.jsp")
  public void pregled(@QueryParam("traziImeKorisnika") String traziImeKorisnika,
      @QueryParam("traziPrezimeKorisnika") String traziPrezimeKorisnika) {
    model.put("traziImeKorisnika", traziImeKorisnika);
    model.put("traziPrezimeKorisnika", traziPrezimeKorisnika);
    if (traziImeKorisnika == "") {
      traziImeKorisnika = null;
    }
    if (traziPrezimeKorisnika == "") {
      traziPrezimeKorisnika = null;
    }
    var port = service.getWsKorisniciPort();
    Korisnik prijavljeniKorisnik = (Korisnik) kontekst.getAttribute("korisnik");
    List<Korisnik> korisnici;
    try {
      korisnici = port.dajKorisnike(prijavljeniKorisnik.getKorime(),
          prijavljeniKorisnik.getLozinka(), traziImeKorisnika, traziPrezimeKorisnika);
      model.put("kontekst", kontekst);
      model.put("korisnici", korisnici);
    } catch (PogresnaAutentifikacija_Exception e) {
      model.put("kontekst", kontekst);
      model.put("korisnici", null);
    }
  }

}

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
 * Klasa za kontrolu zahtjeva dnevnika.
 */
@Controller
@Path("dnevnik")
@RequestScoped
public class KontrolerDnevnik {

  /**  Model. */
  @Inject
  private Models model;

  /**  Kontekst. */
  @Context
  private ServletContext kontekst;

  /** Komunikacija s Aplikacijom 2 */
  KomunikacijaAP2 komunikacijaAP2;

  /**
   * Prikaz zapisa u dnevniku.
   *
   * @param vrsta vrsta zapisa (AP2, AP4, AP5)
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   */
  @GET
  @View("57.jsp")
  public void dnevnik(@QueryParam("vrsta") String vrsta, @QueryParam("odBroja") Integer odBroja,
      @QueryParam("broj") Integer broj) {
    komunikacijaAP2 = new KomunikacijaAP2(kontekst);
    model.put("podaci", komunikacijaAP2.dajPodatkeIzDnevnika(vrsta, odBroja, broj));
    model.put("kontekst", kontekst);
    if(vrsta=="" || vrsta==null) {
      vrsta=null;
    }
    model.put("vrsta", vrsta);
    model.put("odBroja", odBroja);
    model.put("broj", broj);
  }

}


/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package org.foi.nwtis.rmilosevi.aplikacija_5.mvc;

import org.foi.nwtis.rest.klijenti.NwtisRestIznimka;
import org.foi.nwtis.rmilosevi.aplikacija_5.sakupljaci.SakupljacJmsPoruka;
import jakarta.ejb.EJB;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;
import jakarta.mvc.Controller;
import jakarta.mvc.Models;
import jakarta.mvc.RedirectScoped;
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
 * Klasa za kontrolu zahtjeva JMS poruka.
 *
 * @author rmilosevi
 */
@Controller
@Path("jms")
@RequestScoped
public class KontrolerJmsPoruke {

  /**  Model. */
  @Inject
  private Models model;

  /**  Kontekst. */
  @Context
  private ServletContext kontekst;
  
  /** Sakupljac jms poruka. */
  @EJB
  SakupljacJmsPoruka sakupljacJmsPoruka;

  /**
   * Prikazuje JMS poruka o dohvaćanju letova.
   */
  @GET
  @View("54.jsp")
  public void dohvatiJmsPoruke() {
    model.put("kontekst", kontekst);
    model.put("jmsPoruke", sakupljacJmsPoruka.dajPoruke());
  }
  

  /**
   * Briše JMS poruke
   *
   * @return preusmjerava na stranicu za prikaz JMS poruka
   */
  @GET
  @Path("obrisi")
  public String obrisiJmsPoruke() {
    sakupljacJmsPoruka.obrisiPoruke();
    return "redirect:jms";
  }

}

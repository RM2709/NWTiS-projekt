/*
 * To change this license header, choose License Headers in Project Properties. To change this
 * template file, choose Tools | Templates and open the template in the editor.
 */
package org.foi.nwtis.rmilosevi.aplikacija_5.mvc;

import java.util.List;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.Aerodromi;
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
 * Klasa za kontrolu početne stranice.
 *
 * @author rmilosevi
 */
@Controller
@Path("index")
@RequestScoped
public class KontrolerIndex {


  /**   Model. */
  @Inject
  private Models model;

  /**   Kontekst. */
  @Context
  private ServletContext kontekst;

  /**
   * Prikaz početne stranice aplikacije.
   */
  @GET
  @View("51.jsp")
  public void pocetak() {
    model.put("kontekst", kontekst);
  }

}

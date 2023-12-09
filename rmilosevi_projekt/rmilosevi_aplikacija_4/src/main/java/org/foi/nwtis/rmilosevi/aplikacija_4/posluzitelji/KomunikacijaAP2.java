package org.foi.nwtis.rmilosevi.aplikacija_4.posluzitelji;

import org.foi.nwtis.podaci.Aerodrom;
import com.google.gson.Gson;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;

/**
 * Klasa za komunikaciju s Aplikacijom 2
 */
public class KomunikacijaAP2 {
  
  /**  WebTarget. */
  private WebTarget webTarget;

  /**  Klijent. */
  private Client client;

  /**  Osnovna putanja. */
  private String BASE_URI;
  
  /** Kontekst. */
  private ServletContext kontekst;

  /**
   * Instancira klasu za komunikaciju s Aplikacijom 2
   *
   * @param context kontekst
   */
  public KomunikacijaAP2(ServletContext context) {
    this.kontekst = context;
    BASE_URI = kontekst.getAttribute("adresaAP2").toString();
    client = ClientBuilder.newClient();
    webTarget = client.target(BASE_URI).path("aerodromi");
  }
  
  /**
   * Dohvaća pojedini aerodrom.
   *
   * @param icao ICAO traženog aerodroma
   * @return Objekt tipa Aerodrom
   * @throws ClientErrorException Client error exception
   */
  public Aerodrom getAerodrom(String icao) throws ClientErrorException {
    WebTarget resource = webTarget;
    resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icao}));
    Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
    String aerodromString = request.get(String.class);
    if (aerodromString.isEmpty()) {
      return null;
    }
    Gson gson = new Gson();
    Aerodrom aerodrom = gson.fromJson(aerodromString, Aerodrom.class);
    return aerodrom;
  }

}

package org.foi.nwtis.rmilosevi.aplikacija_5.posluzitelji;

import java.util.Arrays;
import java.util.List;
import org.foi.nwtis.podaci.Dnevnik;
import org.foi.nwtis.podaci.OdgovorPosluzitelja;
import org.foi.nwtis.podaci.Udaljenost;
import org.foi.nwtis.podaci.UdaljenostAerodrom;
import org.foi.nwtis.rmilosevi.aplikacija_4.ws.WsAerodromi.endpoint.Aerodrom;
import com.google.gson.Gson;
import jakarta.servlet.ServletContext;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.client.Client;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.client.Invocation;
import jakarta.ws.rs.client.WebTarget;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

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
  }

  /**
   * Dohvaća pojedini aerodrom.
   *
   * @param icao ICAO traženog aerodroma
   * @return Objekt tipa Aerodrom
   * @throws ClientErrorException Client error exception
   */
  public Aerodrom dajAerodrom(String icao) throws ClientErrorException {
    webTarget = client.target(BASE_URI).path("aerodromi");
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

  /**
   * Dohvaća sve aerodrome.
   *
   * @param traziDrzavu naziv drzave
   * @param traziNaziv naziv aerodroma
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   * @return lista aerodroma
   */
  public List<Aerodrom> dajSveAerodrome(String traziNaziv, String traziDrzavu, Integer odBroja,
      Integer broj) {
    webTarget = client.target(BASE_URI).path("aerodromi");
    if (traziNaziv != null && traziNaziv.compareTo("") != 0) {
      webTarget = webTarget.queryParam("traziNaziv", traziNaziv);
    }
    if (traziDrzavu != null && traziDrzavu.compareTo("") != 0) {
      webTarget = webTarget.queryParam("traziDrzavu", traziDrzavu);
    }
    webTarget = webTarget.queryParam("odBroja", odBroja).queryParam("broj", broj);
    Response odgovor = webTarget.request(MediaType.APPLICATION_JSON).get(Response.class);
    Gson gson = new Gson();
    String json = odgovor.readEntity(String.class);
    Aerodrom[] aerodromi = gson.fromJson(json, Aerodrom[].class);
    return Arrays.asList(aerodromi);
  }

  /**
   * Dodaje zapis u dnevnik
   *
   * @param zapis zapis
   * @return broj dodanih zapisa
   * @throws ClientErrorException greška
   */
  public Integer dodajZapis(Dnevnik zapis) throws ClientErrorException {
    return client.target(BASE_URI).path("dnevnik").request(MediaType.APPLICATION_JSON)
        .post(Entity.json(zapis), Integer.class);
  }

  /**
   * Daj podatke iz dnevnika.
   *
   * @param vrsta vrsta upisa (AP2, AP4, AP5)
   * @param odBroja Broj elementa od kojeg počinje straničenje
   * @param broj Broj elemenata na pojedinoj stranici
   * @return listu zapisa dnevnika
   */
  public List<Dnevnik> dajPodatkeIzDnevnika(String vrsta, Integer odBroja, Integer broj) {
    Response odgovor = client.target(BASE_URI).path("dnevnik").queryParam("vrsta", vrsta)
        .queryParam("odBroja", odBroja).queryParam("broj", broj).request(MediaType.APPLICATION_JSON)
        .get(Response.class);
    Gson gson = new Gson();
    String json = odgovor.readEntity(String.class);
    Dnevnik[] zapisi = gson.fromJson(json, Dnevnik[].class);
    return Arrays.asList(zapisi);
  }

  /**
   * Šalje komandu tipa STATUS na poslužitelj
   *
   * @return odgovor poslužitelja
   */
  public String saljiStatus() {
    Response odgovor = client.target(BASE_URI).path("nadzor").request(MediaType.APPLICATION_JSON)
        .get(Response.class);
    if (odgovor.getStatus() != 400) {
      OdgovorPosluzitelja odgovorPosluzitelja = odgovor.readEntity(OdgovorPosluzitelja.class);
      return "Status: " + odgovorPosluzitelja.status() + "<br> Opis: " + odgovorPosluzitelja.opis();
    } else {
      return "Status: 400" + "<br> Opis: Greška pri spajanju na poslužitelj!";
    }
  }

  /**
   * Šalje komande tipa INIT, PAUZA, KRAJ na poslužitelj
   *
   * @param komanda komanda
   * @return odgovor poslužitelja
   */
  public String saljiKomandu(String komanda) {
    Response odgovor = client.target(BASE_URI).path("nadzor")
        .path(java.text.MessageFormat.format("{0}", new Object[] {komanda}))
        .request(MediaType.APPLICATION_JSON).get(Response.class);
    if (odgovor.getStatus() != 400) {
      OdgovorPosluzitelja odgovorPosluzitelja = odgovor.readEntity(OdgovorPosluzitelja.class);
      if (komanda == "INIT" || komanda == "KRAJ") {
        return "Opis: " + odgovorPosluzitelja.opis();
      }
      return "Status: " + odgovorPosluzitelja.status() + "<br> Opis: " + odgovorPosluzitelja.opis();
    } else {
      return "Status: " + odgovor.getStatusInfo().getStatusCode() + "<br> Opis: "
          + odgovor.getStatusInfo().getReasonPhrase();
    }
  }

  /**
   * Šalje komandu tipa INFO na poslužitelj
   *
   * @param vrsta vrsta komande INFO (DA ili NE)
   * @return odgovor poslužitelja
   */
  public String saljiKomanduInfo(String vrsta) {
    Response odgovor = client.target(BASE_URI).path("nadzor").path("INFO")
        .path(java.text.MessageFormat.format("{0}", new Object[] {vrsta}))
        .request(MediaType.APPLICATION_JSON).get(Response.class);
    if (odgovor.getStatus() != 400) {
      OdgovorPosluzitelja odgovorPosluzitelja = odgovor.readEntity(OdgovorPosluzitelja.class);
      return "Opis: " + odgovorPosluzitelja.opis();
    } else {
      return "Status: " + odgovor.getStatusInfo().getStatusCode() + "<br> Opis: "
          + odgovor.getStatusInfo().getReasonPhrase();
    }
  }

  /**
   * Vraća udaljenosti izmedu aerodoma po državama
   *
   * @param icaoOd ICAO ishodišnog aerodroma
   * @param icaoDo ICAO odredišnog aerodroma
   * @return lista udaljenosti izmedu aerodoma po državama
   */
  public List<Udaljenost> dajUdaljenostiDrzaveAerodromi(String icaoOd, String icaoDo) {
    webTarget = client.target(BASE_URI).path("aerodromi");
    WebTarget resource = webTarget;
    resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icaoOd}));
    resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icaoDo}));
    Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
    Response odgovor = request.get(Response.class);
    Gson gson = new Gson();
    String json = odgovor.readEntity(String.class);
    Udaljenost[] udaljenosti = gson.fromJson(json, Udaljenost[].class);
    return Arrays.asList(udaljenosti);
  }
  
  /**
   * Vraća udaljenost izmedu aerodroma izračunatu pomoću poslužitelja iz AP1
   *
   * @param icaoOd ICAO ishodišnog aerodroma
   * @param icaoDo ICAO odredišnog aerodroma
   * @return udaljenost
   */
  public String dajUdaljenostAerodroma(String icaoOd, String icaoDo) {
    webTarget = client.target(BASE_URI).path("aerodromi");
    WebTarget resource = webTarget;
    resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icaoOd}));
    resource = resource.path("izracunaj");
    resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icaoDo}));
    Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
    String odgovor = request.get(String.class);
    return odgovor;
  }
  
  /**
   * Vraća udaljenosti ishodišnog aerodroma i svih aerodroma iz države odredišnog aerodroma koje su
   * manje od udaljenosti između ishodišnog aerodroma i odredišnog aerodroma
   *
   * @param icaoOd ICAO ishodišnog aerodroma
   * @param icaoDo ICAO odredišnog aerodroma
   * @return listu udaljenosti
   */
  public List<UdaljenostAerodrom> dajAerodromeBlizeOd(String icaoOd, String icaoDo) {
    webTarget = client.target(BASE_URI).path("aerodromi");
    WebTarget resource = webTarget;
    resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icaoOd}));
    resource = resource.path("udaljenost1");
    resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icaoDo}));
    Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
    Response odgovor = request.get(Response.class);
    Gson gson = new Gson();
    String json = odgovor.readEntity(String.class);
    UdaljenostAerodrom[] udaljenosti = gson.fromJson(json, UdaljenostAerodrom[].class);
    return Arrays.asList(udaljenosti);
  }
  
  /**
   * Vraća udaljenosti ishodišnog aerodroma i svih aerodroma iz odabrane države koje su
   * manje od odabrane udaljenosti
   *
   * @param icao ICAO ishodišnog aerodroma
   * @param drzava drzava
   * @param km udaljenost u km
   * @return listu udaljenosti
   */
  public List<UdaljenostAerodrom> dajAerodromeBlizeOdKm(String icao, String drzava, String km) {
    webTarget = client.target(BASE_URI).path("aerodromi");
    WebTarget resource = webTarget;
    resource = resource.path(java.text.MessageFormat.format("{0}", new Object[] {icao}));
    resource = resource.path("udaljenost2").queryParam("drzava", drzava).queryParam("km", km);
    Invocation.Builder request = resource.request(MediaType.APPLICATION_JSON);
    Response odgovor = request.get(Response.class);
    Gson gson = new Gson();
    String json = odgovor.readEntity(String.class);
    UdaljenostAerodrom[] udaljenosti = gson.fromJson(json, UdaljenostAerodrom[].class);
    return Arrays.asList(udaljenosti);
  }


}

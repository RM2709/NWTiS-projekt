package org.foi.nwtis.rmilosevi.aplikacija_5.sakupljaci;

import java.util.ArrayList;
import java.util.List;
import jakarta.ejb.Singleton;
import jakarta.jms.JMSException;
import jakarta.jms.TextMessage;

/**
 * Sakupljac JMS poruka. Singleton.
 */
@Singleton
public class SakupljacJmsPoruka {
  
  /**  Instanca sakupljaca. */
  private static SakupljacJmsPoruka instanca = new SakupljacJmsPoruka();
  
  /**  Kolekcija JMS poruka. */
  public List<TextMessage> jmsPoruke = new ArrayList<TextMessage>();

  /**
   * Dohvaća kolekciju JMS poruka.
   *
   * @return lista JMS poruka
   */
  public List<TextMessage> dajPoruke() {
    return jmsPoruke;
  }
  
  /**
   * Dodaj poruku u kolekciju JMS poruka.
   *
   * @param poruka poruka
   */
  public void dodajPoruku(TextMessage poruka) {
    jmsPoruke.add(poruka);
  }
  
  /**
   * Obrisi poruke.
   */
  public void obrisiPoruke() {
    jmsPoruke.clear();
  }
  
  
  
}

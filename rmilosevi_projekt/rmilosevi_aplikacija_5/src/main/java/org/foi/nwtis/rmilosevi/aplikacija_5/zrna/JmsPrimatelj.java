package org.foi.nwtis.rmilosevi.aplikacija_5.zrna;

import org.foi.nwtis.rmilosevi.aplikacija_5.sakupljaci.SakupljacJmsPoruka;
import jakarta.ejb.ActivationConfigProperty;
import jakarta.ejb.EJB;
import jakarta.ejb.MessageDriven;
import jakarta.jms.Message;
import jakarta.jms.MessageListener;
import jakarta.jms.TextMessage;

/**
 * Klasa za primanje JMS poruka.
 */
@MessageDriven(mappedName = "jms/NWTiS_rmilosevi",
    activationConfig = {
        @ActivationConfigProperty(propertyName = "acknowledgeMode",
            propertyValue = "Auto-acknowledge"),
        @ActivationConfigProperty(propertyName = "destinationType",
            propertyValue = "jakarta.jms.Queue")})
public class JmsPrimatelj implements MessageListener {
  
  /**  SIngleton objekt Sakupljaca JMS poruka. */
  @EJB
  SakupljacJmsPoruka sakupljacJmsPoruka;

  /**
   * Metoda primljenu poruku sprema u kolekciju objekta SakupljacJmsPoruka.
   * Aktivira se pri primitku poruke.
   *
   * @param message primljena poruka
   */
  @Override
  public void onMessage(Message message) {
    if (message instanceof TextMessage) {
      try {
        var msg = (TextMessage) message;
        sakupljacJmsPoruka.dodajPoruku(msg);
      } catch (Exception ex) {
        ex.printStackTrace();
      }
    }
  }
}

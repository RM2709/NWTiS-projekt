package org.foi.nwtis.rmilosevi.aplikacija_3.zrna;

import jakarta.annotation.Resource;
import jakarta.ejb.Stateless;
import jakarta.jms.Connection;
import jakarta.jms.ConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;

/**
 * Klasa za slanje JMS poruka.
 */
@Stateless
public class JmsPosiljatelj {

  /**  Tvornica veza. */
  @Resource(mappedName = "jms/NWTiS_rmilosevi_qf")
  private ConnectionFactory connectionFactory;
  
  /**  Red čekanja. */
  @Resource(mappedName = "jms/NWTiS_rmilosevi")
  private Queue queue;

  /**
   * Šalje poruku.
   *
   * @param tekstPoruke tekst poruke
   * @return true, ako je slanje uspješno
   */
  public boolean saljiPoruku(String tekstPoruke) {
    boolean status = true;
    try {
      Connection connection = connectionFactory.createConnection();
      Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
      MessageProducer messageProducer = session.createProducer(queue);
      TextMessage message = session.createTextMessage();
      message.setText(tekstPoruke);
      messageProducer.send(message);
      messageProducer.close();
      connection.close();
    } catch (JMSException ex) {
      ex.printStackTrace();
      status = false;
    }
    return status;
  }

}

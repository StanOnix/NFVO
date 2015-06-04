package org.project.neutrino.nfvo.vnfm_reg.impl.sender;

import org.project.neutrino.nfvo.catalogue.nfvo.CoreMessage;
import org.project.neutrino.nfvo.catalogue.nfvo.VnfmManagerEndpoint;
import org.project.neutrino.vnfm.interfaces.sender.VnfmSender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.ObjectMessage;
import javax.jms.Session;
import javax.naming.NamingException;

/**
 * Created by lto on 03/06/15.
 */
@Service(value = "jmsSender")
public class JmsSender implements VnfmSender{

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private JmsTemplate jmsTemplate;

    @Override
    public void sendCommand(final CoreMessage coreMessage, final VnfmManagerEndpoint endpoint) throws JMSException, NamingException {
        String queueName = "core-vnfm-actions";
        log.debug("Sending message: " + coreMessage + " to Queue: " + queueName + " where selector is: type=\'" + endpoint.getEndpoint() + "\'");
        MessageCreator messageCreator = new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                ObjectMessage objectMessage = session.createObjectMessage(coreMessage);
                String endpointValue = endpoint.getEndpoint().substring(1, endpoint.getEndpoint().length() - 1);
                log.trace("SELECTOR: type=\'"+ endpointValue + "\'");
                objectMessage.setStringProperty("type", endpointValue);
                return objectMessage;
            }
        };

        jmsTemplate.send(queueName, messageCreator);

    }
}

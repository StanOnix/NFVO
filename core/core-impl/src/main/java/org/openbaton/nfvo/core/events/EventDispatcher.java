/*
 * Copyright (c) 2015 Fraunhofer FOKUS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openbaton.nfvo.core.events;

/**
 * Created by lto on 03/06/15.
 */

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.openbaton.catalogue.mano.record.NetworkServiceRecord;
import org.openbaton.catalogue.mano.record.VirtualNetworkFunctionRecord;
import org.openbaton.catalogue.nfvo.ApplicationEventNFVO;
import org.openbaton.catalogue.nfvo.EventEndpoint;
import org.openbaton.exceptions.NotFoundException;
import org.openbaton.nfvo.common.internal.model.EventNFVO;
import org.openbaton.nfvo.core.interfaces.EventSender;
import org.openbaton.nfvo.repositories.EventEndpointRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Scope;
import org.springframework.jms.annotation.EnableJms;
import org.springframework.stereotype.Service;

import java.io.IOException;

/**
 * This class implements the interface {@Link EventDispatcher} so is in charge
 * of handling the de/registration of a EventEndpoint.
 * <p/>
 * Moreover receives also internal events and dispatches them to the external applications.
 */
@Service
@Scope
@EnableJms
class EventDispatcher implements ApplicationListener<EventNFVO>, org.openbaton.nfvo.core.interfaces.EventDispatcher {

    private Logger log = LoggerFactory.getLogger(this.getClass());
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();
    @Autowired
    private EventEndpointRepository eventEndpointRepository;
    @Autowired
    private ConfigurableApplicationContext context;

    @Override
    public EventEndpoint register(String endpoint_json) {
        EventEndpoint endpoint = gson.fromJson(endpoint_json,EventEndpoint.class);
        return saveEventEndpoint(endpoint);
    }

    public EventEndpoint saveEventEndpoint(EventEndpoint endpoint) {
        EventEndpoint save = eventEndpointRepository.save(endpoint);
        log.info("Registered event endpoint" + save);
        return save;
    }

    @Override
    public void onApplicationEvent(EventNFVO event) {
        log.debug("Received event: " + event);
        dispatchEvent(event);
    }

    @Override
    public void dispatchEvent(EventNFVO event) {
        log.debug("dispatching event to the world!!!");
        log.debug("event is: " + event);

        Iterable<EventEndpoint> endpoints = eventEndpointRepository.findAll();

        for (EventEndpoint endpoint : endpoints) {
            log.debug("Checking endpoint: " + endpoint);
            if (endpoint.getEvent().ordinal() == event.getEventNFVO().getAction().ordinal()) {
                if (endpoint.getVirtualNetworkFunctionId() != null) {
                    if (event.getEventNFVO().getPayload() instanceof VirtualNetworkFunctionRecord) {
                        if (((VirtualNetworkFunctionRecord) event.getEventNFVO().getPayload()).getId().equals(endpoint.getVirtualNetworkFunctionId())) {
                            log.debug("dispatching event to: " + endpoint);
                            sendEvent(endpoint, event.getEventNFVO());
                        }
                    }
                } else if (endpoint.getNetworkServiceId() != null) {
                    if (event.getEventNFVO().getPayload() instanceof NetworkServiceRecord) {
                        if (((NetworkServiceRecord) event.getEventNFVO().getPayload()).getId().equals(endpoint.getNetworkServiceId())) {
                            log.debug("dispatching event to: " + endpoint);
                            sendEvent(endpoint, event.getEventNFVO());
                        }
                    }
                } else {
                    log.debug("dispatching event to: " + endpoint);
                    sendEvent(endpoint, event.getEventNFVO());
                }
            }
        }

    }

    private void sendEvent(EventEndpoint endpoint, ApplicationEventNFVO event) {
        EventSender sender = (EventSender) context.getBean(endpoint.getType().toString().toLowerCase() + "EventSender");
        log.trace("Sender is: " + sender.getClass().getSimpleName());
        try {
            sender.send(endpoint, event);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("Error while dispatching event " + event);
        }
    }

    @Override
    public void unregister(String id) throws NotFoundException {
        if (eventEndpointRepository.exists(id)) {
            log.info("Removing EventEndpoint with id: " + id);
            eventEndpointRepository.delete(id);
        }
    }

}

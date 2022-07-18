// tag::copyright[]
/*******************************************************************************
 * Copyright (c) 2020 IBM Corporation and others.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     IBM Corporation - Initial implementation
 *******************************************************************************/
// end::copyright[]
package io.openliberty.guides.system;

import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.TimeUnit;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.websocket.server.ServerEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.Session;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.reactivestreams.Publisher;
import org.xml.sax.SAXException;

import io.openliberty.guides.models.SystemLoad;
import io.reactivex.rxjava3.core.Flowable;

@ApplicationScoped
@ServerEndpoint(value = "/")
public class SystemService {

    //@Inject
    //@ConfigProperty(name="UPDATE_INTERVAL", defaultValue="5")
    private long updateInterval;

    private static final OperatingSystemMXBean osMean = 
            ManagementFactory.getOperatingSystemMXBean();

    private static String hostname = null;

    private static Set<Session> sessions = new HashSet<>();

    public static void broadcastLoad(SystemLoad systemLoad) {
        for (Session session : sessions) {
            try {
                session.sendSystemLoad(systemLoad);
            } catch (IOException | EncodeException e) {
                e.printStackTrace();
            }
        }
    }

    private static String getHostname() {
        if (hostname == null) {
            try {
                hostname = InetAddress.getLocalHost().getHostName();
            } catch (UnknownHostException e) {
                hostname = System.getenv("HOSTNAME");
            }
        }
        return hostname;
    }

    @OnOpen
    public void onOpen(Session session){
        System.out.println("Web Socket opened");
        sessions.add(session);
    }

    @OnError
    public void onError(){
        System.out.println("Web Socket encoutered an error");
    }

    @OnClose
    public void onClose(Session session){
        System.out.println("Web Socket has been closed");
        sessions.remove(session);

    }

    @OnMessage
    public Publisher<SystemLoad> onMessage(Session session){
        System.out.println("Information received: " + session.getHostName());
        return session.sendSystemLoad();
    }
}
 
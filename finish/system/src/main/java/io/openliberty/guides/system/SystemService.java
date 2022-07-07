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

    @Outgoing("systemLoad")
    public Publisher<SystemLoad> sendSystemLoad() {
        return Flowable.interval(updateInterval, TimeUnit.SECONDS)
                       .map((interval -> new SystemLoad(getHostname(),
                           Double.valueOf(osMean.getSystemLoadAverage()))));
    }
    
    @OnOpen
    public void onOpen(){
        System.out.println("Web Socket opened");
    }

    @OnError
    public void onError(){
        System.out.println("Web Socket encoutered an error");
    }

    @OnClose
    public void onClose(){
        System.out.println("Web Socket has been closed");
    }

    @OnMessage
    public void onMessage(){
        System.out.println("Information received: ");
        return Flowable.interval(updateInterval, TimeUnit.SECONDS)
                       .map((interval -> new SystemLoad(getHostname(),
                           Double.valueOf(osMean.getSystemLoadAverage()))));

    }
}
 
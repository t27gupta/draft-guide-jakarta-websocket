package io.openliberty.guides.system;

import java.util.concurrent.Flow.Publisher;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;

@ClientEndpoint
public class SystemEndpoint {

    @OnMessage
    public void processMessage(Publisher<SystemLoad> systemLoad) {
        return systemLoad;
    }
}
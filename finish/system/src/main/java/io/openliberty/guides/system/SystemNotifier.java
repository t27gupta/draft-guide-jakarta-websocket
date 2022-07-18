package io.openliberty.guides.system;

import javax.annotation.PostConstruct;

public class SystemNotifier{

    public void init(){
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        try {
            this.session = container.connectToServer(SystemEndpoint.class,
                    new URI("ws://localhost:9080/"));
        } catch (DeploymentException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Outgoing("systemLoad")
    public Publisher<SystemLoad> sendSystemLoad() {
        return Flowable.interval(updateInterval, TimeUnit.SECONDS)
                       .map((interval -> new SystemLoad(getHostname(),
                           Double.valueOf(osMean.getSystemLoadAverage()))));
    }
    
}
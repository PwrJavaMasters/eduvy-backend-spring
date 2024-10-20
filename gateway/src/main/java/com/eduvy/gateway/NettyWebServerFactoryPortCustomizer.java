package com.eduvy.gateway;

import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Component;
import reactor.netty.http.server.HttpServer;

@Component
public class NettyWebServerFactoryPortCustomizer
        implements WebServerFactoryCustomizer<NettyReactiveWebServerFactory> {

    // Create an SLF4J logger for this class
    private static final Logger logger = LoggerFactory.getLogger(NettyWebServerFactoryPortCustomizer.class);

    public NettyWebServerFactoryPortCustomizer() {
        logger.debug("NettyWebServerFactoryPortCustomizer initialized");
    }

    @Override
    public void customize(NettyReactiveWebServerFactory serverFactory) {
        // Customize the port
        serverFactory.setPort(8081);

        // Add wiretap logging (for detailed Netty request/response logs)
        serverFactory.addServerCustomizers(httpServer -> httpServer.wiretap(true)
                .doOnBound(server -> logger.debug("Netty server bound to port 8081")));

        // Set additional Netty log level (optional for more detailed logs)
        serverFactory.addServerCustomizers(httpServer -> httpServer.doOnConnection(connection -> {
            connection.addHandlerLast(new LoggingHandler(LogLevel.DEBUG));
        }));
    }
}

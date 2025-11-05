package de.szut.lf8_starter.security;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.URL;
import java.util.concurrent.atomic.AtomicBoolean;

@Service
@Slf4j
public class KeycloakHealthService {

    private final AtomicBoolean isKeycloakUp = new AtomicBoolean(false);

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @EventListener(ApplicationReadyEvent.class)
    public void checkKeycloakConnectionOnStartup() {
        checkKeycloakConnection();
    }

    public void checkKeycloakConnection() {
        try {
            URL url = new URL(jwkSetUri);
            String host = url.getHost();
            int port = url.getPort() == -1 ? url.getDefaultPort() : url.getPort();

            try (Socket socket = new Socket()) {
                socket.connect(new InetSocketAddress(host, port), 2000); // 2-second timeout
                isKeycloakUp.set(true);
                log.info("Keycloak connection successful.");
            } catch (IOException e) {
                isKeycloakUp.set(false);
                log.warn("Keycloak connection failed. Service will be unavailable. Details: {}", e.getMessage());
            }
        } catch (Exception e) {
            isKeycloakUp.set(false);
            log.error("Invalid JWK Set URI: {}", jwkSetUri, e);
        }
    }

    public boolean isKeycloakUp() {
        return isKeycloakUp.get();
    }
}


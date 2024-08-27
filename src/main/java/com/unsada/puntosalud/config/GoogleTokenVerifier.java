package com.unsada.puntosalud.config;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.stereotype.Component;

import java.util.Collections;

@Component
public class GoogleTokenVerifier {

    private final GoogleIdTokenVerifier verifier;

    public GoogleTokenVerifier() {
        this.verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), JacksonFactory.getDefaultInstance())
                .setAudience(Collections.singletonList("858931895617-pfthj4jrajhjs0pcc97elqqo6n0u0qrc.apps.googleusercontent.com"))
                .build();
    }

    public GoogleIdToken.Payload verify(String tokenId) throws Exception {
        GoogleIdToken idToken = verifier.verify(tokenId);
        return (idToken != null) ? idToken.getPayload() : null;
    }
}

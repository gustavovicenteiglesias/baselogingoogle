package com.unsada.puntosalud.controller;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.unsada.puntosalud.config.JwtTokenProvider;
import com.unsada.puntosalud.config.GoogleTokenVerifier;
import com.unsada.puntosalud.model.Role;
import com.unsada.puntosalud.model.User;
import com.unsada.puntosalud.payload.GoogleAuthRequest;
import com.unsada.puntosalud.payload.JwtResponse;
import com.unsada.puntosalud.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private GoogleTokenVerifier googleTokenVerifier;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private UserRepository userRepository;

    @PostMapping("/google")
    public ResponseEntity<?> authenticateGoogleUser(@RequestBody GoogleAuthRequest googleAuthRequest) {
        try {
            GoogleIdToken.Payload payload = googleTokenVerifier.verify(googleAuthRequest.getIdToken());

            if (payload != null) {
                String email = payload.getEmail();
                Optional<User> user=userRepository.findByEmail(email);
                // Aquí podrías buscar el usuario en tu base de datos y asignar roles

                if(user.isPresent()){
                    String pictureUrl = (String) payload.get("picture"); // Obtener el claim picture
                    String name= (String) payload.get("name");
                    Long id=user.get().getId();
                    Set<Role> roles = user.get().getRoles();
                    // Generar un JWT con los roles obtenidos
                    String jwt = jwtTokenProvider.createToken(email, roles);

                    return ResponseEntity.ok(new JwtResponse(jwt, id,name,email,pictureUrl, List.of("ROLE-ADMIN")));
                }else {
                    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Usuario no registrado ");
                }

            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token de Google no válido");
            }

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al validar el token");
        }
    }
}

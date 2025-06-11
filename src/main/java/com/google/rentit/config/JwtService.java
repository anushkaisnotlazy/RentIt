package com.google.rentit.config;


import java.security.KeyPair;
import java.security.PublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.google.rentit.user.model.User;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.SignatureAlgorithm;
import io.jsonwebtoken.security.SignatureException;

@Service
public class JwtService {
    private static final SignatureAlgorithm alg = Jwts.SIG.RS512;
    private KeyPair pair;
    private static final Integer ACCESS_EXPIRY_SECONDS = 30;
    private static final Integer REFERSH_EXPIRY_SECONDS = 15 * 60 * 60;

    public JwtService() {
        pair = alg.keyPair().build();
    }

    public PublicKey getPublicKey() {
        return pair.getPublic();
        //instead, you can use System.getProperty(...) to get something in the environment
    }

    public String createAccessToken(User user) {
        return Jwts.builder()
                    .signWith(pair.getPrivate(), alg) //instead, you can use System.getProperty(...)
                    .subject(String.valueOf(user.getId()))
                    .claims(Map.of("name", user.getUserName()))
                    .expiration(Date.from(Instant.now().plusSeconds(ACCESS_EXPIRY_SECONDS)))
                    .compact();
    }

    public String createNewAccessToken(String refreshToken) throws SignatureException {
        var parsedRefreshToken = Jwts.parser()
                                        .verifyWith(getPublicKey())
                                        .build()
                                        .parseSignedClaims(refreshToken);
        return Jwts.builder()
                    .signWith(pair.getPrivate(), alg)
                    .subject(parsedRefreshToken.getPayload().getSubject())
                    .claims(parsedRefreshToken.getPayload())
                    .expiration(Date.from(Instant.now().plusSeconds(ACCESS_EXPIRY_SECONDS)))
                    .compact();
    }

    public String createRefreshToken(User user) {
        return Jwts.builder()
                    .signWith(pair.getPrivate(), alg)
                    .subject(String.valueOf(user.getId()))
                    .claims(Map.of("name", user.getUserName()))
                    .expiration(Date.from(Instant.now().plusSeconds(REFERSH_EXPIRY_SECONDS)))
                    .compact();
    }

}


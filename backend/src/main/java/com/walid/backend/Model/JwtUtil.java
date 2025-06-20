package com.walid.backend.Model;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import com.walid.backend.App;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(App.class);

    // Secret key for signing the JWT
    private static final String SECRET_KEY = "your-secret-key"; // Replace with your secret key

    // Expiration time for tokens (1 hour in milliseconds)
    private static final long EXPIRATION_TIME = 3600000; // 1 hour in milliseconds

    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    // Generates a JWT token for the given username and accountID
    public static String generateToken(String username, int accountID) {
        return JWT.create()
                .withSubject(username)
                .withClaim("accountID", accountID)
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    // Generates a CSRF token
    public static String generateCsrfToken() {
        return JWT.create()
                .withSubject("csrf-token")
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .sign(algorithm);
    }

    // Extracts the username from a JWT token
    public static String getUsernameFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getSubject();
        } catch (JWTDecodeException e) {
            System.err.println("Failed to decode JWT: " + e.getMessage());
            return null;
        }
    }

    // Extracts the account ID from a JWT token
    public static Integer getAccountIDFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Claim accountIDClaim = jwt.getClaim("accountID");
            return accountIDClaim.isNull() ? null : accountIDClaim.asInt();
        } catch (JWTDecodeException e) {
            System.err.println("Failed to decode JWT: " + e.getMessage());
            return null;
        }
    }

    // Validates a JWT token
    /*public static boolean validateToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
            return true; // Token is valid
        } catch (JWTVerificationException e) {
            // Token verification failed
            return false;
        }
    }*/

    public static boolean validateToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
            logger.info("Token is valid. Subject: {}, Expiration: {}", jwt.getSubject(), jwt.getExpiresAt());
            return true;
        } catch (JWTVerificationException e) {
            // Token verification failed
            logger.error("Token verification failed: {}", e.getMessage());
            return false;
        }
    }


    public static int getAccountIdFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.require(algorithm).build().verify(token);
            Claim accountIdClaim = jwt.getClaim("accountID");
    
            if (accountIdClaim.isNull()) {
                logger.error("Account ID is missing from the token.");
                throw new RuntimeException("Account ID is missing from the token.");
            }
    
            return accountIdClaim.asInt();
        } catch (JWTVerificationException e) {
            logger.error("Failed to extract accountId from token: {}", e.getMessage());
            throw new RuntimeException("Invalid token");
        }
    }
    
    
    
}

package com.walid.backend.Model;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTDecodeException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Date;

public class AdminJwtUtil {
    private static final Logger logger = LoggerFactory.getLogger(AdminJwtUtil.class);

    private static final String SECRET_KEY = "admin-secret-key";  // Use a different key for admins
    private static final long EXPIRATION_TIME = 7200000;  // 2 hours for admin sessions

    private static final Algorithm algorithm = Algorithm.HMAC256(SECRET_KEY);

    public static String generateToken(String username, int adminId) {
        return JWT.create()
                  .withSubject(username)
                  .withClaim("adminId", adminId)
                  .withIssuedAt(new Date())
                  .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                  .sign(algorithm);
    }


    public static String getUsernameFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            return jwt.getSubject();
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static Integer getAdminIdFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Claim adminIdClaim = jwt.getClaim("adminId");
            if (!adminIdClaim.isNull()) {
                return adminIdClaim.asInt();
            }
            return null;
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static boolean validateToken(String token) {
        try {
            JWT.require(algorithm).build().verify(token);
            logger.info("Token is valid.");
            return true;
        } catch (JWTVerificationException e) {
            logger.error("Token validation failed.", e);
            return false;
        }
    }

    public static String getRoleFromToken(String token) {
        try {
            DecodedJWT jwt = JWT.decode(token);
            Claim roleClaim = jwt.getClaim("role");
            if (!roleClaim.isNull()) {
                return roleClaim.asString();  // Return the role as a string
            }
            return null;
        } catch (JWTDecodeException e) {
            return null;
        }
    }

    public static String generateToken(String username, int adminId, String role) {
        return JWT.create()
                  .withSubject(username)
                  .withClaim("adminId", adminId)
                  .withClaim("role", role)  // Add role to the token
                  .withIssuedAt(new Date())
                  .withExpiresAt(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                  .sign(algorithm);
    }
    
}

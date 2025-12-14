package com.logistics.parcelandcarrier.security;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.logistics.parcelandcarrier.entity.User;
import com.logistics.parcelandcarrier.enums.Role;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Service for JWT token generation and validation
 */
@Slf4j
@Service
public class JwtService {

  @Value("${jwt.secret}")
  private String secretKey;

  @Value("${jwt.expiration}")
  private long expirationTime;

  @Value("${jwt.issuer:parcel-and-carrier-api}")
  private String issuer;

  /**
   * Generate JWT token for a user
   * @param user the authenticated user
   * @return JWT token string
   */
  public String generateToken(User user) {
    if (user == null) {
      throw new IllegalArgumentException("User cannot be null");
    }

    Algorithm algorithm = Algorithm.HMAC256(secretKey);
    Date now = new Date();
    Date expiryDate = new Date(now.getTime() + expirationTime);

    return JWT.create()
      .withIssuer(issuer)
      .withSubject(user.getLogin())
      .withClaim("userId", user.getId())
      .withClaim("role", user.getRole().name())
      .withIssuedAt(now)
      .withExpiresAt(expiryDate)
      .sign(algorithm);
  }

  /**
   * Validate JWT token
   * @param token the JWT token
   * @return true if valid, false otherwise
   */
  public boolean validateToken(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secretKey);
      JWTVerifier verifier = JWT.require(algorithm)
        .withIssuer(issuer)
        .build();

      verifier.verify(token);
      return true;
    } catch (JWTVerificationException e) {
      log.error("JWT validation failed: {}", e.getMessage());
      return false;
    }
  }

  /**
   * Extract username (login) from token
   * @param token the JWT token
   * @return username
   */
  public String extractUsername(String token) {
    DecodedJWT decodedJWT = decodeToken(token);
    return decodedJWT.getSubject();
  }

  /**
   * Extract user ID from token
   * @param token the JWT token
   * @return user ID
   */
  public String extractUserId(String token) {
    DecodedJWT decodedJWT = decodeToken(token);
    return decodedJWT.getClaim("userId").asString();
  }

  /**
   * Extract role from token
   * @param token the JWT token
   * @return Role enum
   */
  public Role extractRole(String token) {
    DecodedJWT decodedJWT = decodeToken(token);
    String roleName = decodedJWT.getClaim("role").asString();
    return Role.valueOf(roleName);
  }

  /**
   * Check if token is expired
   * @param token the JWT token
   * @return true if expired, false otherwise
   */
  public boolean isTokenExpired(String token) {
    try {
      DecodedJWT decodedJWT = decodeToken(token);
      return decodedJWT.getExpiresAt().before(new Date());
    } catch (Exception e) {
      return true;
    }
  }

  /**
   * Extract all claims from token
   * @param token the JWT token
   * @return Map of claims
   */
  public Map<String, Object> extractAllClaims(String token) {
    DecodedJWT decodedJWT = decodeToken(token);
    Map<String, Object> claims = new HashMap<>();

    claims.put("subject", decodedJWT.getSubject());
    claims.put("userId", decodedJWT.getClaim("userId").asString());
    claims.put("role", decodedJWT.getClaim("role").asString());
    claims.put("issuedAt", decodedJWT.getIssuedAt());
    claims.put("expiresAt", decodedJWT.getExpiresAt());

    return claims;
  }

  /**
   * Decode JWT token
   * @param token the JWT token
   * @return DecodedJWT
   */
  private DecodedJWT decodeToken(String token) {
    Algorithm algorithm = Algorithm.HMAC256(secretKey);
    JWTVerifier verifier = JWT.require(algorithm)
      .withIssuer(issuer)
      .build();

    return verifier.verify(token);
  }
}

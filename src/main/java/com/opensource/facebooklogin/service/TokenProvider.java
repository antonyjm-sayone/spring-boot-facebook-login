package com.opensource.facebooklogin.service;

import com.nimbusds.jose.util.Base64;
import com.opensource.facebooklogin.exception.TokenException;
import com.opensource.facebooklogin.token.TokenProperties;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Date;
import java.util.StringTokenizer;

@Service
public class TokenProvider {

    private static final Logger logger = LoggerFactory.getLogger(TokenProvider.class);

    @Autowired
    private TokenProperties tokenProperties;

    @Autowired
    private SecureRandom secureRandom;

//    public TokenProvider(TokenProperties tokenProperties) {
//        this.tokenProperties = tokenProperties;
//    }

    public String createToken(String email) {

        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenProperties.getAuth().getAccessTokenExpirationMsec());

        return Jwts.builder()
                .setSubject(email)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getAuth().getTokenSecret())
                .compact();
    }

    public String createRefreshToken(String email){
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + tokenProperties.getAuth().getRefreshTokenExpirationMsec());

        return Jwts.builder()
                .setSubject("refresh:"+email)
                .setIssuedAt(new Date())
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, tokenProperties.getAuth().getTokenSecret())
                .compact();
    }

    public String getEmailFromToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokenProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();

        return claims.getSubject();
    }

    public String getEmailFromRefreshToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(tokenProperties.getAuth().getTokenSecret())
                .parseClaimsJws(token)
                .getBody();
        StringTokenizer tokenizer = new StringTokenizer(claims.getSubject(), ":");
        int tokenCount = tokenizer.countTokens();
        int i =0;
        while (i < tokenCount -1){
            tokenizer.nextToken();
            i++;
        }
        return tokenizer.nextToken();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(tokenProperties.getAuth().getTokenSecret()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            logger.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            logger.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty.");
        }
        throw new TokenException("Invalid token");
    }
}

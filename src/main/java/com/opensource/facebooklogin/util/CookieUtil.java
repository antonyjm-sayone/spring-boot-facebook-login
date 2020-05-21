package com.opensource.facebooklogin.util;

import com.opensource.facebooklogin.token.TokenProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

// reference https://medium.com/@altunkan/spring-boot-authentication-with-vuejs-jwt-http-only-cookie-4d8cfe7e4f0f
@Component
public class CookieUtil {
    @Value("${app.auth.accessTokenCookieName}")
    private String accessTokenCookieName;

    @Value("${app.auth.refreshTokenCookieName}")
    private String refreshTokenCookieName;

    @Autowired
    private TokenProperties tokenProperties;

    public HttpCookie createAccessTokenCookie(String token) {
        return ResponseCookie.from(accessTokenCookieName, token)
                .maxAge(tokenProperties.getAuth().getAccessTokenExpirationMsec()/1000)
                .path("/")
//                .httpOnly(true)
                .secure(false)
                .build();
    }

    public HttpCookie createRefreshTokenCookie(String token) {
        return ResponseCookie.from(refreshTokenCookieName, token)
                .maxAge(tokenProperties.getAuth().getRefreshTokenExpirationMsec()/1000)
//                .httpOnly(false)
                .secure(false)
                .path("/")
                .build();
    }

    public HttpCookie deleteAccessTokenCookie() {
        return ResponseCookie.from(accessTokenCookieName, "").maxAge(0).httpOnly(false).secure(false).path("/").build();
    }

    public HttpCookie deleteRefreshTokenCookie() {
        return ResponseCookie.from(refreshTokenCookieName, "").maxAge(0).httpOnly(false).secure(false).path("/").build();
    }

    public String getAccessTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (accessTokenCookieName.equals(cookie.getName())) {
                String accessToken = cookie.getValue();
                if (accessToken == null) return null;

                return accessToken;
            }
        }
        return null;
    }


    public String getRefreshTokenFromCookie(HttpServletRequest request) {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if (refreshTokenCookieName.equals(cookie.getName())) {
                String refreshToken = cookie.getValue();
                if (refreshToken == null) return null;

                return refreshToken;
            }
        }
        return null;
    }
}

package com.opensource.facebooklogin.token;

import com.opensource.facebooklogin.exception.TokenException;
import com.opensource.facebooklogin.service.TokenProvider;
import com.opensource.facebooklogin.util.CookieUtil;
import jdk.nashorn.internal.parser.Token;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.util.StringUtils;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;

public class TokenAuthenticationFilter extends BasicAuthenticationFilter {


    private static final String HEADER_STRING = "Authorization";
    private static final String TOKEN_PREFIX = "Bearer ";

    private TokenProvider tokenProvider;

    private CookieUtil cookieUtil;

    public TokenAuthenticationFilter(AuthenticationManager authenticationManager, TokenProvider tokenProvider, CookieUtil cookieUtil){
        super(authenticationManager);
        this.tokenProvider = tokenProvider;
        this.cookieUtil = cookieUtil;
    }

    @Override
    public void doFilterInternal(HttpServletRequest req,
                                 HttpServletResponse res,
                                 FilterChain chain) throws IOException, ServletException {

            String accessToken = cookieUtil.getAccessTokenFromCookie(req);
            String refreshToken = cookieUtil.getRefreshTokenFromCookie(req);

            // if tokens are null allow them to access login urls
            if((accessToken == null || accessToken.equals(""))
                    && ( refreshToken == null ||  refreshToken.equals(""))) {
                chain.doFilter(req, res);
                return;
            }

            // create authentication from token data
            try {
                UsernamePasswordAuthenticationToken authentication = getAuthentication(req);
                SecurityContextHolder.getContext().setAuthentication(authentication);
                chain.doFilter(req, res);
            } catch (TokenException e) {
                // create new access token if refresh token is valid
                try{

                    boolean refreshTokenValid = tokenProvider.validateToken(refreshToken);

                    // get email from refresh token and generate new access token
                    String refreshTokenEmail = tokenProvider.getEmailFromRefreshToken(refreshToken);
                    String newAccessToken = tokenProvider.createToken(refreshTokenEmail);

                    // set new access cookie
                    res.setHeader(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(newAccessToken).toString());


                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(refreshTokenEmail, null, new ArrayList<>());
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    chain.doFilter(req, res);
                }
                // invalidate cookies if both tokens are invalid and return error
                catch (TokenException te) {
                    res.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.deleteAccessTokenCookie().toString());
                    res.setHeader(HttpHeaders.SET_COOKIE, cookieUtil.deleteRefreshTokenCookie().toString());
                    res.setStatus(HttpStatus.BAD_REQUEST.value());
                    res.getWriter().write(e.getMessage());
                    res.flushBuffer();
                }
            }


    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) throws TokenException {
        String jwt = cookieUtil.getAccessTokenFromCookie(request);
        if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
            String email = tokenProvider.getEmailFromToken(jwt);

            if (email != null) {
                return new UsernamePasswordAuthenticationToken(email, null, new ArrayList<>());
            }

        }
        throw new TokenException("Invalid token");
    }

    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(HEADER_STRING);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_PREFIX)) {
            return bearerToken.substring(7, bearerToken.length());
        }
        return null;
    }
}

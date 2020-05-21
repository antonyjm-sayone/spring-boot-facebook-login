package com.opensource.facebooklogin.controller;

import com.opensource.facebooklogin.model.AccessTokenParams;
import com.opensource.facebooklogin.model.RedirectParams;
import com.opensource.facebooklogin.service.OAuthService;
import com.opensource.facebooklogin.service.TokenProvider;
import com.opensource.facebooklogin.token.TokenProperties;
import com.opensource.facebooklogin.util.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/custom-oauth")
public class OauthController {

    @Autowired
    private ClientRegistrationRepository clientRegistrationRepo;

    @Autowired
    private OAuthService oAuthService;

    @Autowired
    private CookieUtil cookieUtil;

    @Autowired
    private TokenProperties tokenProperties;

    @Autowired
    private TokenProvider tokenProvider;

    @PostMapping("/verify-user/facebook")
    public ResponseEntity verifyUser(@RequestBody() RedirectParams redirectParams){
        ClientRegistration clientInfo = clientRegistrationRepo.findByRegistrationId("facebook");
        if(clientInfo != null){

            String tokenUrl = clientInfo.getProviderDetails().getTokenUri();
            AccessTokenParams params = new AccessTokenParams(clientInfo.getAuthorizationGrantType().getValue(),
                    redirectParams.getCode(),
                    clientInfo.getRedirectUriTemplate(),
                    redirectParams.getState());
            List<String> tokens = this.oAuthService.getAccessTokens(clientInfo, params, tokenUrl);
            String accessToken = tokens.get(0);
            String refreshToken = tokens.get(1);
            if(accessToken != null){
                HttpHeaders responseHeaders = new HttpHeaders();
                responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(accessToken).toString());
                responseHeaders.add(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(refreshToken).toString());
                return ResponseEntity.ok().headers(responseHeaders).build();
            }

            return  new ResponseEntity(HttpStatus.BAD_REQUEST);

            }
        return new ResponseEntity(HttpStatus.NOT_FOUND);
    }
}

package com.opensource.facebooklogin.service;

import com.opensource.facebooklogin.model.AccessTokenParams;
import org.springframework.security.oauth2.client.registration.ClientRegistration;

import java.util.List;

public interface OAuthService {

    public List<String> getAccessTokens(ClientRegistration clientInfo, AccessTokenParams params, String tokenUrl);
}

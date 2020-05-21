package com.opensource.facebooklogin.service.impl;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opensource.facebooklogin.model.AccessTokenParams;
import com.opensource.facebooklogin.model.User;
import com.opensource.facebooklogin.service.OAuthService;
import com.opensource.facebooklogin.service.UserService;
import com.opensource.facebooklogin.service.TokenProvider;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class OAuthServiceImpl implements OAuthService {

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private UserService userService;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<String> getAccessTokens(ClientRegistration clientInfo, AccessTokenParams params, String tokenUrl) {

        // encode client id and secret
        String credentials = clientInfo.getClientId()+ ":" + clientInfo.getClientSecret();
        String encodedCredentials = new String(Base64.encodeBase64(credentials.getBytes()));

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(Arrays.asList(MediaType.APPLICATION_JSON));
        headers.setBasicAuth(encodedCredentials);
        HttpEntity entity = new HttpEntity(params, headers);

        ResponseEntity response = this.restTemplate.exchange(tokenUrl, HttpMethod.POST, entity, String.class);
        if(response.getStatusCode() == HttpStatus.OK) {
            try {
                JsonNode jsonNode = objectMapper.readTree(response.getBody().toString());
                String accessToken = jsonNode.get("access_token").asText();

                String userInfoUri = clientInfo.getProviderDetails().getUserInfoEndpoint().getUri();
                HttpHeaders userInfoHeaders = new HttpHeaders();
                userInfoHeaders.add("Authorization", "Bearer " + accessToken);
                HttpEntity userInfoEntity = new HttpEntity(userInfoHeaders);
                ResponseEntity<User> userInfoResponse = this.restTemplate.exchange(userInfoUri, HttpMethod.GET, userInfoEntity, User.class);
                if(userInfoResponse.getStatusCode() == HttpStatus.OK) {
                    User user = userInfoResponse.getBody();
                    user = userService.createOrUpdateUser(user.getId(), user.getEmail(), user.getName());
                    String newAccessToken = tokenProvider.createToken(user.getEmail());
                    String refreshToken = tokenProvider.createRefreshToken(user.getEmail());
                    List<String> tokens = new ArrayList<>();
                    tokens.add(newAccessToken);
                    tokens.add(refreshToken);
                    return tokens;
                }

            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }
}

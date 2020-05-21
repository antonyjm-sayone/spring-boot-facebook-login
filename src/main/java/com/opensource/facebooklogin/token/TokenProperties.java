package com.opensource.facebooklogin.token;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app")
public class TokenProperties {

    private final Auth auth = new Auth();

    public static class Auth {
        private String tokenSecret;
        private long accessTokenExpirationMsec;
        private long refreshTokenExpirationMsec;

        public String getTokenSecret() {
            return tokenSecret;
        }

        public void setTokenSecret(String tokenSecret) {
            this.tokenSecret = tokenSecret;
        }

        public long getAccessTokenExpirationMsec() {
            return accessTokenExpirationMsec;
        }

        public void setAccessTokenExpirationMsec(long accessTokenExpirationMsec) {
            this.accessTokenExpirationMsec = accessTokenExpirationMsec;
        }

        public long getRefreshTokenExpirationMsec() {
            return refreshTokenExpirationMsec;
        }

        public void setRefreshTokenExpirationMsec(long refreshTokenExpirationMsec) {
            this.refreshTokenExpirationMsec = refreshTokenExpirationMsec;
        }
    }

    public Auth getAuth() {
        return auth;
    }
}

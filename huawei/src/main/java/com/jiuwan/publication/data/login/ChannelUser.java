package com.jiuwan.publication.data.login;

public class ChannelUser {
    String slug;
    String AuthToken;

    public ChannelUser(String slug, String authToken) {
        this.slug = slug;
        AuthToken = authToken;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getAuthToken() {
        return AuthToken;
    }

    public void setAuthToken(String authToken) {
        AuthToken = authToken;
    }
}
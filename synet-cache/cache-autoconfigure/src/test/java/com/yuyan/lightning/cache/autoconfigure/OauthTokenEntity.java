package com.yuyan.lightning.cache.autoconfigure;


import java.util.Objects;

public class OauthTokenEntity implements java.io.Serializable {
    private Long id;
    private String accessToken;
    private String refreshToken;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OauthTokenEntity entity = (OauthTokenEntity) o;
        return Objects.equals(id, entity.id) &&
                Objects.equals(accessToken, entity.accessToken) &&
                Objects.equals(refreshToken, entity.refreshToken);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, accessToken, refreshToken);
    }
}



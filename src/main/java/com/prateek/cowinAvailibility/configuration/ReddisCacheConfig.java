package com.prateek.cowinAvailibility.configuration;

//@Configuration
//@PropertySource("classpath:application.yml")
//@ConfigurationProperties(prefix = "reddis")
public class ReddisCacheConfig {
    private String host;
    private String key;
    private boolean ssl;
    private int port;
    private int cacheExpiryInSeconds;

    public boolean isSsl() {
        return ssl;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public int getCacheExpiryInSeconds() {
        return cacheExpiryInSeconds;
    }

    public void setCacheExpiryInSeconds(int cacheExpiryInSeconds) {
        this.cacheExpiryInSeconds = cacheExpiryInSeconds;
    }

}

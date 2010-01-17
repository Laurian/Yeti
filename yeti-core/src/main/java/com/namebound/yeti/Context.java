package com.namebound.yeti;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 *
 * @author Laurian Gridinoc
 */
public class Context {

    private HttpClient httpClient;

    public Context() {
    }

    public Context(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public Context httpClient(HttpClient httpClient) {
        this.httpClient = httpClient;
        return this;
    }

    public HttpClient httpClient() {
        if (httpClient == null) {
            httpClient = new DefaultHttpClient();
        }
        return httpClient;
    }

}

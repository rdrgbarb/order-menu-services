package com.rodrigobarbosa.order.external.menu;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.util.Optional;

@Component
public class RestClientMenuClient implements MenuClient {
    private final RestClient restClient;

    public RestClientMenuClient(
        @Value("${menu.base-url}") String baseUrl,
        @Value("${menu.timeout-ms}") long timeoutMs
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout((int) timeoutMs);
        requestFactory.setReadTimeout((int) timeoutMs);
        this.restClient = RestClient.builder()
            .baseUrl(baseUrl)
            .requestFactory(requestFactory)
            .build();
    }

    @Override
    public Optional<MenuItem> getMenuItem(String productId) {
        try {
            MenuItem item = restClient.get()
                .uri("/menu-items/{id}", productId)
                .retrieve()
                .body(MenuItem.class);
            return Optional.ofNullable(item);
        } catch (HttpClientErrorException.NotFound e) {
            return Optional.empty();
        } catch (ResourceAccessException e) {
            throw new MenuUnavailableException("Menu service unavailable (timeout/connection)", e);
        } catch (RestClientException e) {
            throw new MenuUnavailableException("Menu service error", e);
        }
    }

}

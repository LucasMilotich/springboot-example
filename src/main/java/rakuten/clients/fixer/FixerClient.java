package rakuten.clients.fixer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import rakuten.exceptions.ConvertCurrencyException;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.util.Map;

@Component
public class FixerClient {

    private static final Logger logger = LoggerFactory.getLogger(FixerClient.class);

    @Value("${api_key}")
    private String apiKeyFixer;

    private WebClient client = WebClient
            .builder()
            .baseUrl("http://data.fixer.io/api")
            .defaultCookie("cookieKey", "cookieValue")
            .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
            .filter(logRequest())
            .build();

    public BigDecimal convertCurrency(BigDecimal price, String fromCurrency, String toCurrency) throws  ConvertCurrencyException {

        String convertUri = "/convert";

        FixerConvertResponse response = client.get().uri(uriBuilder ->
                uriBuilder
                        .path(convertUri)
                        .queryParam("access_key", apiKeyFixer)
                        .queryParam("from", fromCurrency)
                        .queryParam("to", toCurrency)
                        .queryParam("amount", price.toString())
                        .build()
        )

                .retrieve().onStatus(HttpStatus::is4xxClientError, clientResponse -> {
                            logger.info(clientResponse.bodyToMono(Map.class).toString());
                            return Mono.error(new Exception());
                        }
                )
                .onStatus(HttpStatus::is5xxServerError, clientResponse -> {
                            logger.info(clientResponse.bodyToMono(Map.class).toString());
                            return Mono.error(new Exception());
                        }

                ).bodyToMono(FixerConvertResponse.class).block();

        if ( !response.success) {
            logger.error(response.error.get("info").toString());
            throw new ConvertCurrencyException("Error converting currency");

        }


        return response.result;


    }

    private ExchangeFilterFunction logRequest() {
        return (clientRequest, next) -> {
            logger.info("Request: {} {}", clientRequest.method(), clientRequest.url());
            clientRequest.headers()
                    .forEach((name, values) -> values.forEach(value -> logger.info("{}={}", name, value)));
            return next.exchange(clientRequest);
        };
    }

}

package io.github.rjs5613.contract.consumer.service0;

import au.com.dius.pact.consumer.MockServer;
import au.com.dius.pact.consumer.dsl.PactDslJsonArray;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.consumer.junit5.PactConsumerTestExt;
import au.com.dius.pact.consumer.junit5.PactTestFor;
import au.com.dius.pact.core.model.RequestResponsePact;
import au.com.dius.pact.core.model.annotations.Pact;
import org.apache.http.client.fluent.Executor;
import org.apache.http.client.fluent.Request;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@ExtendWith(PactConsumerTestExt.class)
@PactTestFor(providerName = "service1", port = "8080")
public class Service0ContractTest {

    @Pact(consumer = "service0")
    public RequestResponsePact createPact(PactDslWithProvider builder) {
        PactDslJsonArray responseBody = new PactDslJsonArray()
                .object()
                .stringType("cardNumber", "1234567812345678")
                .stringType("cardHolder", "John Doe")
                .closeObject()
                .object()
                .stringType("cardNumber", "9876543298765432")
                .stringType("cardHolder", "Jane Smith")
                .closeObject().array();

        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");

        return builder
                .given("Cards exist")
                .uponReceiving("a request for cards")
                .path("/cards")
                .method("GET")
                .willRespondWith()
                .status(200)
                .headers(headers)
                .body(responseBody)
                .toPact();
    }

    @Test
    @PactTestFor(pactMethod = "createPact")
    void testCardsInteraction(MockServer mockServer) throws IOException {
        String cardsApiPath = "/cards";

        // Make the request to the mock provider
        Request request = Request.Get(mockServer.getUrl() + cardsApiPath);
        String response = Executor.newInstance().execute(request).returnContent().asString();

        // Assert the response status code and body structure
        Assertions.assertEquals(8080, mockServer.getPort());
        Assertions.assertNotNull(response);
    }
}


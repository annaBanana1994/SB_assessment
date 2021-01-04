package com.starlingbank.assessment.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.starlingbank.assessment.model.clientResponse.Accounts;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockserver.integration.ClientAndServer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;

import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@RunWith(SpringRunner.class)
public class testtrial {

    private static ClientAndServer mockServer;

    private ObjectMapper mapper = new ObjectMapper();

    @InjectMocks
    ClientServiceImpl clientService;
    @Value("${clientAPI.rootPath}")
    private String rootPath;


    @BeforeClass
    public void startServer() {
        mockServer = startClientAndServer(1080);
    }

    @AfterClass
    public void stopServer() {
        mockServer.stop();
    }

    @Test
    public void getAccountHoldersAccounts() throws Exception {

        JsonNode jsonNode = mapper.readTree(new File("/Users/Student/Documents/Coding Projects/assessment/src/test/java/com/starlingbank/assessment/data/savings_list_one.json"));

        //        ResponseEntity<Accounts> response = this.restTemplate.exchange(url, HttpMethod.GET, request, Accounts.class);
        mockServer.when(request()
                .withMethod(HttpMethod.GET.name())
                .withPath(rootPath + "/accounts")
                .withHeader("\"Content-type\", \"application/json\"")
                .withHeader("\"Authorization\", \"Bearer 1234\"")
                ).respond(response()
                .withStatusCode(HttpStatus.OK.value())
                .withBody(mapper.writeValueAsString(jsonNode))
                );
        Accounts accounts = clientService.getAccountHoldersAccounts("1234");

//
//        assertEquals(1, responses.size());
//        assertEquals("first", responses.get(0).getFirstName());
//        assertEquals("last", responses.get(0).getLastName());
//
//        mockServer.verify(
//                request().withMethod(HttpMethod.GET.name())
//                        .withPath("/legacy/persons")
//        );
    }



    ///public class TestMockServer {
    //    //    private void createExpectationForInvalidAuth() {
    //    //        new MockServerClient("127.0.0.1", 1080)
    //    //          .when(
    //    //            request()
    //    //              .withMethod("POST")
    //    //              .withPath("/validate")
    //    //              .withHeader("\"Content-type\", \"application/json\"")
    //    //              .withBody(exact("{username: 'foo', password: 'bar'}")),
    //    //              exactly(1))
    //    //                .respond(
    //    //                  response()
    //    //                    .withStatusCode(401)
    //    //                    .withHeaders(
    //    //                      new Header("Content-Type", "application/json; charset=utf-8"),
    //    //                      new Header("Cache-Control", "public, max-age=86400"))
    //    //                    .withBody("{ message: 'incorrect username and password combination' }")
    //    //                    .withDelay(TimeUnit.SECONDS,1)
    //    //                );
    //    //    }

}


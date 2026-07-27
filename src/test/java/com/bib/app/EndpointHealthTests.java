package com.bib.app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
class EndpointHealthTests {

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    private void assertNotDead(String endpoint) throws Exception {
        int status = mockMvc.perform(get(endpoint))
                .andReturn()
                .getResponse()
                .getStatus();
        assertTrue(status < 500, endpoint + " returned " + status + " — dead endpoint");
    }

    @Test
    void projectGetAllRespondsWithoutServerError() throws Exception {
        assertNotDead("/project/getall");
    }

    @Test
    void usersRespondsWithoutServerError() throws Exception {
        assertNotDead("/users");
    }

    @Test
    void usersWithDashboardRespondsWithoutServerError() throws Exception {
        assertNotDead("/users/with-dashboard");
    }

    @Test
    void usersWithGraphsRespondsWithoutServerError() throws Exception {
        assertNotDead("/users/with-graphs");
    }

    @Test
    void companyGetAllRespondsWithoutServerError() throws Exception {
        assertNotDead("/company");
    }

    @Test
    void cohortGetAllRespondsWithoutServerError() throws Exception {
        assertNotDead("/cohort");
    }
}
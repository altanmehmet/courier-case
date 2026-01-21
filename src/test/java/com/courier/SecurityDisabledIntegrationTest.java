package com.courier;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "security.enabled=false",
        "app.exitAfterStartup=false"
})
@AutoConfigureMockMvc
class SecurityDisabledIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    void allowsRequestsWithoutToken() throws Exception {
        mockMvc.perform(post("/locations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"courierId":"c1","timeMillis":1723900000000,"lat":40.0,"lng":29.0}
                                """))
                .andExpect(status().isAccepted());

        mockMvc.perform(get("/couriers/c1/distance"))
                .andExpect(status().isOk());
    }
}


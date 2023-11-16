package controllers;


import com.fasterxml.jackson.databind.ObjectMapper;
import model.User;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import service.Baloot;



import static org.mockito.BDDMockito.given;


@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserController.class)
public class UserControllerTest {


    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper;


    @MockBean
    private Baloot baloot;
    @Autowired
    public UserControllerTest(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Test
    public void testGetUserById() throws Exception {
        // Your mock behavior for Baloot
        User mockUser = new User("testUser", "password", "test@example.com", "01-01-1990", "Test Address");
        mockUser.addCredit(100.0f);

        // Mocking the behavior of Baloot's getUserById
        given(baloot.getUserById("1")).willReturn(mockUser);

        // Perform the GET request
        mockMvc.perform(MockMvcRequestBuilders.get("/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.username").value("testUser"))
                .andExpect(MockMvcResultMatchers.jsonPath("$.credit").value(100.0));
    }

//    @Test
//    public void testAddCredit() throws Exception {
//        // Your mock behavior for Baloot
//        User mockUser = new User("testUser", "password", "test@example.com", "01-01-1990", "Test Address");
//
//        // Mocking the behavior of Baloot's getUserById
//        given(baloot.getUserById("1")).willReturn(mockUser);
//
//        // Prepare the request body
//        Map<String, String> requestBody = new HashMap<>();
//        requestBody.put("credit", "50.0");
//
//        // Perform the POST request
//        mockMvc.perform(MockMvcRequestBuilders.post("/users/1/credit")
//                        .contentType(MediaType.APPLICATION_JSON)
//                        .content(objectMapper.writeValueAsString(requestBody)))
//                .andExpect(MockMvcResultMatchers.status().isOk())
//                .andExpect(MockMvcResultMatchers.content().string("credit added successfully!"));
//    }
    @Configuration
    public static class TestConfig {
        @Bean
        public ObjectMapper objectMapper() {
            return new ObjectMapper();
        }
    }

}


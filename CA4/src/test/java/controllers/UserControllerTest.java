package controllers;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import exceptions.InvalidCreditRange;
import exceptions.NotExistentUser;
import model.User;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import service.Baloot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
public class UserControllerTest {


    @InjectMocks
    private UserController usersController;
    @Mock
    private Baloot baloot;

    @Mock
    private User balootUsr;
    private MockMvc mockMvc;

    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File usersJsonFile = new File("src/test/java/resources/users.json").getAbsoluteFile();
    private static ArrayList<User> initUsers;



    private final String USER_BASE_URL = "/users";
    private final String ID_PATH_VARIABLE_URL = "/{id}";
    private final String CREDIT_API_URL = "/credit";

    private final String CREDIT_ADD_SUCCESSFULLY_FEEDBACK = "credit added successfully!";
    private final String CREDIT_INVALID_NUMBER_FEEDBACK = "Please enter a valid number for the credit amount.";
    private final String CREDIT_INVALID_RANGE_FEEDBACK = "Credit value must be a positive float";
    private final String CREDIT_USER_NOT_EXIST_FEEDBACK = "User does not exist.";

    @BeforeAll
    public static void setup(){
        try {
            String initJsonUsers = FileUtils.readFileToString(usersJsonFile);
            initUsers = gson.fromJson(initJsonUsers, new TypeToken<ArrayList<User>>() {}.getType());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(usersController).build();
    }

    @AfterEach
    public void tearDown() {
        Mockito.reset(baloot, balootUsr);
    }

    @Test
    void test_valid_user_success() throws Exception {
        String userId = initUsers.get(0).getUsername();
        when(baloot.getUserById(any())).thenReturn(initUsers.get(0));
        String action = USER_BASE_URL + ID_PATH_VARIABLE_URL;
        MvcResult result = mockMvc.perform(get(action, userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();
            String content = result.getResponse().getContentAsString();
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

            User actual = objectMapper.readValue(content, new TypeReference<User>() {});

            assertThat(actual)
                    .usingRecursiveComparison()
                    .isEqualTo(initUsers.get(0));
    }

    @Test
    void test_incorrect_user_id_expected_NotExistentUser() throws Exception {
        String userId = "123";
        when(baloot.getUserById(userId)).thenThrow(new NotExistentUser());
        MvcResult result = mockMvc.perform(get("/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        assertNull(usersController.getUser(userId).getBody());
        assertEquals(HttpStatus.NOT_FOUND, usersController.getUser(userId).getStatusCode());
    }

    @Test
    void test_valid_credit_success_add() throws Exception {
        String userId = "1";
        float creditToAdd = 100.0f;
        Map<String, String> input = new HashMap<String, String>() {{
            put("credit", String.valueOf(creditToAdd));
        }};

        doNothing().when(balootUsr).addCredit(creditToAdd);
        when(baloot.getUserById(userId)).thenReturn(balootUsr);

        ObjectMapper objectMapper = new ObjectMapper();

        String requestJson=objectMapper.writeValueAsString(input);

        String action = USER_BASE_URL + ID_PATH_VARIABLE_URL + CREDIT_API_URL;
        MvcResult result = mockMvc.perform(post(action, userId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(CREDIT_ADD_SUCCESSFULLY_FEEDBACK, content);
        assertEquals(HttpStatus.OK, usersController.addCredit(userId, input).getStatusCode());
    }


    @Test
    void test_invalid_range_add_creadit_bad_request_expected_InvalidCreditRange() throws Exception {
        String userId = "1";
        float creditToAdd = 0;
        Map<String, String> input = Collections.singletonMap("credit", String.valueOf(creditToAdd));

        doThrow(new InvalidCreditRange()).when(balootUsr).addCredit(anyFloat());
        when(baloot.getUserById(any())).thenReturn(balootUsr);
        String requestJson = new ObjectMapper().writeValueAsString(input);

        String action = USER_BASE_URL + ID_PATH_VARIABLE_URL + CREDIT_API_URL;
        MvcResult result = mockMvc.perform(post(action, userId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andReturn();

        ResponseEntity<String> responseEntity = usersController.addCredit(userId, input);
        assertEquals(CREDIT_INVALID_RANGE_FEEDBACK, responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }

    @Test
    void test_user_not_found_exception_user_expected_NotExistentUser() throws Exception {

        String userId = "1";
        Map<String, String> input = Collections.singletonMap("credit", "0");

        String requestJson = new ObjectMapper().writeValueAsString(input);

        doNothing().when(balootUsr).addCredit(anyFloat());
        doThrow(new NotExistentUser()).when(baloot).getUserById(any());


        String action = USER_BASE_URL + ID_PATH_VARIABLE_URL + CREDIT_API_URL;
        MvcResult result = mockMvc.perform(post(action, userId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isNotFound())
                .andReturn();

        ResponseEntity<String> responseEntity = usersController.addCredit(userId, input);
        assertEquals(CREDIT_USER_NOT_EXIST_FEEDBACK, responseEntity.getBody());
        assertEquals(HttpStatus.NOT_FOUND, responseEntity.getStatusCode());

    }
    @Test
    void test_invalid_credit_isBadRequest_expected_NumberFormatException() throws Exception {
        String userId = "1";
        float creditToAdd = -100.0f;
        Map<String, String> input = new HashMap<String, String>(){{
            put("credit", String.valueOf(creditToAdd));
        }};

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson=objectMapper.writeValueAsString(input);

        doNothing().when(balootUsr).addCredit(anyFloat());
        doThrow(new NumberFormatException()).when(baloot).getUserById(any());

        String action = USER_BASE_URL + ID_PATH_VARIABLE_URL + CREDIT_API_URL;
        MvcResult result = mockMvc.perform(post(action, userId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))

                .andExpect(status().isBadRequest())
                .andReturn();

        ResponseEntity<String> responseEntity = usersController.addCredit(userId, input);
        assertEquals(CREDIT_INVALID_NUMBER_FEEDBACK, responseEntity.getBody());
        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());

    }
}
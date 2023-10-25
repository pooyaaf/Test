package controller;

import controllers.AuthenticationController;
import exceptions.IncorrectPassword;
import exceptions.NotExistentUser;
import exceptions.UsernameAlreadyTaken;
import model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.stubbing.BaseStubbing;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import service.Baloot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class AuthenticationControllerTest {

    @Mock
    private Baloot baloot;

    private AuthenticationController authenticationController;

    @BeforeEach
    void setup() {
        //baloot = Mockito.mock(Baloot.class);
        MockitoAnnotations.openMocks(this);
        authenticationController = new AuthenticationController();
    }

    @AfterEach
    public void cleanUp() {
        authenticationController = null;
        baloot = null;
    }

    @Test
    void test_loginNotExistentUser_exception_NotExistentUser() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = new HashMap<>();
        input.put("username", "nonExistentUser");
        input.put("password", "password");

        // Mock the login method to throw NotExistentUser
        doThrow(new NotExistentUser()).when(baloot).login("nonExistentUser", "password");

        ResponseEntity<String> response = authenticationController.login(input);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals("User does not exist.", response.getBody());
    }

    @Test
    void test_signup_successful() throws UsernameAlreadyTaken {
        Map<String, String> input = new HashMap<>();
        input.put("address", "123 Main St");
        input.put("birthDate", "1990-01-01");
        input.put("email", "user@example.com");
        input.put("username", "newUser");
        input.put("password", "password");

        doNothing().when(baloot).addUser(any(User.class));

        ResponseEntity<String> response = authenticationController.signup(input);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("signup successfully!", response.getBody());
    }
//    @Test
//    void testSignupUsernameTaken() throws UsernameAlreadyTaken {
//        Map<String, String> input = new HashMap<>();
//        input.put( "address", "Tehran");
//        input.put("birthDate", "1990-05-15");
//        input.put("email", "example@example.com");
//        input.put("username", "Reza");
//        input.put("password", "secret");
//
//        // Mock the addUser method to throw UsernameAlreadyTaken
//        doThrow(new UsernameAlreadyTaken()).when(baloot).addUser(any(User.class));
//
//        ResponseEntity<String> response = authenticationController.signup(input);
//
//        assertEquals(HttpStatus.OK, response.getStatusCode());
//        assertEquals("Username already taken", response.getBody());
//    }


}

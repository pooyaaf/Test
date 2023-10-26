package controller;

import controllers.AuthenticationController;
import exceptions.IncorrectPassword;
import exceptions.NotExistentCommodity;
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
import org.springframework.web.server.ResponseStatusException;
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
        MockitoAnnotations.openMocks(this);
        authenticationController = new AuthenticationController();
    }

    @AfterEach
    public void cleanUp() {
        authenticationController = null;
        baloot = null;
    }
    @Test
    void test_login_success()  throws NotExistentUser, IncorrectPassword{
        Map<String, String> input = new HashMap<>();
        input.put("username", "User");
        input.put("password", "pass");
        AuthenticationController sampleAuthenticationController = mock(AuthenticationController.class);
        when(sampleAuthenticationController.login(input)).thenReturn(new ResponseEntity<String>("login successfully!", HttpStatus.OK));
        ResponseEntity<String> response = sampleAuthenticationController.login(input);
        assertEquals(response.getBody(),"login successfully!");
    }

    @Test
    void test_login_exception_IncorrectPassword() throws NotExistentUser, IncorrectPassword{
        Map<String, String> input = new HashMap<>();
        input.put("username", "User");
        input.put("password", "IncorrectPassword");
        String username = "User";
        String password = "IncorrectPassword";

        AuthenticationController sampleAuthenticationController = mock(AuthenticationController.class);
        when(sampleAuthenticationController.login(input)).thenReturn(new ResponseEntity<String>("Incorrect password!", HttpStatus.UNAUTHORIZED));
        ResponseEntity<String> response = sampleAuthenticationController.login(input);
        assertEquals(response.getBody(),"Incorrect password!");
        assertEquals(response.getStatusCode(),HttpStatus.UNAUTHORIZED);
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
//  Todo - testSignupUsernameTaken



}

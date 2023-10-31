package controller;

import controllers.AuthenticationController;
import defines.Errors;
import exceptions.IncorrectPassword;
import exceptions.NotExistentUser;
import exceptions.UsernameAlreadyTaken;
import model.User;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;
import java.util.HashMap;
import java.util.Map;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthenticationControllerTest {

    @Mock
    private Baloot baloot;
    @InjectMocks
    private AuthenticationController authenticationController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        authenticationController = new AuthenticationController();
        authenticationController.setBaloot(baloot);
    }

    @AfterEach
    public void cleanUp() {
        authenticationController = null;
        baloot = null;
    }
    @Test
    void test_login_success() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = new HashMap<>();
        input.put("username", "User");
        input.put("password", "pass");

        doNothing().when(baloot).login(anyString(), anyString());
        ResponseEntity<String> response = authenticationController.login(input);

        assertEquals("login successfully!", response.getBody());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void test_login_exception_IncorrectPassword() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = new HashMap<>();
        input.put("username", "User");
        input.put("password", "IncorrectPassword");

        doThrow(new IncorrectPassword()).when(baloot).login(input.get("username"), input.get("password"));
        ResponseEntity<String> response = authenticationController.login(input);

        assertEquals(Errors.INCORRECT_PASSWORD, response.getBody());
        assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
    }

    @Test
    void test_loginNotExistentUser_exception_NotExistentUser() throws NotExistentUser, IncorrectPassword {
        Map<String, String> input = new HashMap<>();
        input.put("username", "nonExistentUser");
        input.put("password", "password");

        doThrow(new NotExistentUser()).when(baloot).login(input.get("username"), input.get("password"));
        ResponseEntity<String> response = authenticationController.login(input);

        assertEquals(Errors.NOT_EXISTENT_USER, response.getBody());
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
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

package controller;

import controllers.CommentController;
import defines.Errors;
import exceptions.NotExistentComment;
import model.Comment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class CommentControllerTest {
    @Mock
    private Comment comment;
    @Mock
    private Baloot baloot;

    @InjectMocks
    private CommentController commentController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        commentController = new CommentController();
        commentController.setBaloot(baloot);
    }
    @AfterEach
    public void cleanUp() {
        comment = null;
        commentController = null;
        baloot = null;

    }
    @Test
    public void test_likeComment_success() throws NotExistentComment {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "user1");
        int commentId = 1;

        when(baloot.getCommentById(commentId)).thenReturn(comment);
        doNothing().when(comment).addUserVote(requestBody.get("username"), "like");
        ResponseEntity<String> response = commentController.likeComment(Integer.toString(commentId), requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The comment was successfully liked!", response.getBody());
    }

    @Test
    public void test_likeComment_NotFound() throws NotExistentComment {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "user1");
        int commentId = 1;

        when(baloot.getCommentById(commentId)).thenThrow(new NotExistentComment());
        ResponseEntity<String> response = commentController.likeComment(Integer.toString(commentId), requestBody);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Errors.NOT_EXISTENT_COMMENT, response.getBody());
    }

    @Test
    public void test_dislikeComment_success() throws NotExistentComment {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "user1");
        int commentId = 1;

        when(baloot.getCommentById(commentId)).thenReturn(comment);
        doNothing().when(comment).addUserVote(requestBody.get("username"), "dislike");
        ResponseEntity<String> response = commentController.dislikeComment(Integer.toString(commentId), requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The comment was successfully disliked!", response.getBody());
    }

    @Test
    public void test_dislikeComment_NotFound() throws NotExistentComment {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "user1");
        int commentId = 1;

        when(baloot.getCommentById(commentId)).thenThrow(new NotExistentComment());
        ResponseEntity<String> response = commentController.dislikeComment(Integer.toString(commentId), requestBody);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Errors.NOT_EXISTENT_COMMENT, response.getBody());
    }
}



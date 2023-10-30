package controller;

import controllers.CommentController;
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
        doNothing().when(comment).addUserVote("user1", "like");
        ResponseEntity<String> response = commentController.likeComment("1", requestBody);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The comment was successfully liked!", response.getBody());
    }

    // Todo test_likeComment_NotFound()

//    @Test
//    public void test_likeComment_NotFound() throws NotExistentComment {
//        Map<String, String> requestBody = new HashMap<>();
//        int commentId = 1;
//
//        when(baloot.getCommentById(commentId)).thenThrow(new NotExistentComment());
//        ResponseEntity<String> response = null;
//        response = commentController.likeComment("1", requestBody);
//
//
//    }

    @Test
    public void test_dislikeComment_success() throws NotExistentComment {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("username", "user1");
        int commentId = 1;

        when(baloot.getCommentById(commentId)).thenReturn(comment);
        doNothing().when(comment).addUserVote("user1", "dislike");

        ResponseEntity<String> response = commentController.dislikeComment("1", requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("The comment was successfully disliked!", response.getBody());
    }
    // Todo test_dislikeComment_NotFound()
//    @Test
//    public void test_dislikeComment_NotFound() throws NotExistentComment {
//        Map<String, String> requestBody = new HashMap<>();
//        int commentId = 1;
//
//        when(baloot.getCommentById(commentId)).thenThrow(new NotExistentComment());
//
//        doThrow(new NotExistentComment()).when(comment).addUserVote("user1", "dislike");
//
//        ResponseEntity<String> response = commentController.dislikeComment("1", requestBody);
//
//        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
//        assertEquals("Comment not found", response.getBody());
//    }
}



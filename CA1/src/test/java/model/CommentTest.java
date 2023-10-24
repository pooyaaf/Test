package model;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public class CommentTest {
    private Comment comment;

    @BeforeEach
    void setUp() {
        comment = new Comment();
        comment.setId(1);
        comment.setUserEmail("userEmail");
        comment.setUsername("username");
        comment.setCommodityId(1);
        comment.setText("text");
    }


    @AfterEach
    void tearDown() {
        comment = null;
    }

}

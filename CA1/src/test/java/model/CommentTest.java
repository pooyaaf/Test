package model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.text.SimpleDateFormat;
import java.util.Date;

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

    @ParameterizedTest
    @CsvSource({
            "2, userEmail2, username2, 2, text2",
            "3, userEmail3, username3, 3, text3",
            "4, userEmail4, username4, 4, text4",
    })
    void testConstructor(int id, String userEmail, String username, int commodityId, String text) {
        Comment comment = new Comment(id, userEmail, username, commodityId, text);
        Assertions.assertEquals(id, comment.getId());
        Assertions.assertEquals(userEmail, comment.getUserEmail());
        Assertions.assertEquals(username, comment.getUsername());
        Assertions.assertEquals(commodityId, comment.getCommodityId());
        Assertions.assertEquals(text, comment.getText());
    }

    @AfterEach
    void tearDown() {
        comment = null;
    }
}

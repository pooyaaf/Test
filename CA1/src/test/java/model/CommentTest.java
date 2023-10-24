package model;

import org.junit.jupiter.api.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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
        // Add user votes
        Map<String, String> userVote = new HashMap<>();
        userVote.put("user1", "like");
        userVote.put("user2", "dislike");
        userVote.put("user3", "like");
        comment.setUserVote(userVote);
        comment.setLike(2);
        comment.setDislike(1);
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

    @Test
    void testGetCurrentDate() {
        Date now = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Assertions.assertEquals(dateFormat.format(now), comment.getCurrentDate());
    }

    @ParameterizedTest
    @CsvSource({
            "user5, dislike",
            "user6, like",
    })
    void testAddUserVote(String userName, String vote) {
        System.out.println("vote: " + vote);

        int pre_like = comment.getLike();
        int pre_dislike = comment.getDislike();

        System.out.println("pre_like: " + pre_like);
        System.out.println("pre_dislike: " + pre_dislike);

        comment.addUserVote(userName, vote);

        System.out.println("like: " + comment.getLike());
        System.out.println("dislike: " + comment.getDislike());

        if (vote.equals("like")) {
            Assertions.assertEquals(pre_like + 1, comment.getLike());
            Assertions.assertEquals(pre_dislike, comment.getDislike());
        } else if (vote.equals("dislike")) {
            Assertions.assertEquals(pre_like, comment.getLike());
            Assertions.assertEquals(pre_dislike + 1, comment.getDislike());
        }
    }

    @AfterEach
    void tearDown() {
        comment = null;
    }
}

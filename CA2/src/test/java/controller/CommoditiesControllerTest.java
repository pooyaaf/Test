package controller;

import controllers.CommoditiesController;
import defines.Errors;
import exceptions.NotExistentCommodity;
import exceptions.NotExistentUser;
import model.Comment;
import model.Commodity;
import model.User;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.matchers.Null;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


public class CommoditiesControllerTest {

    @Mock
    private Baloot baloot;

    @InjectMocks
    private CommoditiesController commoditiesController;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        commoditiesController = new CommoditiesController();
        commoditiesController.setBaloot(baloot);
    }

    @AfterEach
    public void cleanUp() {
        commoditiesController = null;
        baloot = null;
    }


    @Test
    public void test_getCommodities() {
        ArrayList<Commodity> sampleCommodities = new ArrayList<>();
        //setup commodities
        Commodity commodity1 = new Commodity();
        commodity1.setId("1");
        commodity1.setName("A");
        Commodity commodity2 = new Commodity();
        commodity2.setId("2");
        commodity2.setImage("B");
        sampleCommodities.add(commodity1);
        sampleCommodities.add(commodity2);

        when(baloot.getCommodities()).thenReturn(sampleCommodities);
        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getCommodities();

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleCommodities, response.getBody());
    }

    @Test
    public void test_getCommodity_success() throws NotExistentCommodity {
        String commodityId = "1";
        Commodity sampleCommodity = new Commodity();
        sampleCommodity.setId(commodityId);

        when(baloot.getCommodityById(commodityId)).thenReturn(sampleCommodity);
        ResponseEntity<Commodity> response = commoditiesController.getCommodity(commodityId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleCommodity, response.getBody());
    }

    @Test
    public void test_getCommodity_NotExistentCommodity() throws NotExistentCommodity {
        String commodityId = "NotExistentCommodity";

        when(baloot.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());
        ResponseEntity<Commodity> response = commoditiesController.getCommodity(commodityId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertNull(response.getBody());
    }

    @Test
    public void test_rateCommodity_success() throws NotExistentCommodity {
        String commodityId = "1";
        Map<String, String> requestBody = Map.of("username", "user1", "rate", "5");

        Commodity sampleCommodity = new Commodity();
        sampleCommodity.setId(commodityId);

        when(baloot.getCommodityById(commodityId)).thenReturn(sampleCommodity);
        ResponseEntity<String> response = commoditiesController.rateCommodity(commodityId, requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("rate added successfully!", response.getBody());
    }
    @Test
    public void test_rateCommodity_NotExistentCommodity() throws NotExistentCommodity {
        String commodityId = "NotExistentCommodity";
        Map<String, String> requestBody = Map.of("username", "user1", "rate", "5");

        when(baloot.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());
        ResponseEntity<String> response = commoditiesController.rateCommodity(commodityId, requestBody);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Errors.NOT_EXISTENT_COMMODITY, response.getBody());
    }

    @Test
    public void test_rateCommodity_invalidRate() throws NotExistentCommodity {
        String commodityId = "1";
        Map<String, String> requestBody = Map.of("username", "user1", "rate", "invalid_rate");

        when(baloot.getCommodityById(commodityId)).thenReturn(new Commodity());
        ResponseEntity<String> response = commoditiesController.rateCommodity(commodityId, requestBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("For input string: \"invalid_rate\"", response.getBody());
    }

    @Test
    public void test_addCommodityComment_success() throws NotExistentUser {
        String commodityId = "1";
        Map<String, String> requestBody = Map.of("username", "user1", "comment", "comment1");

        when(baloot.generateCommentId()).thenReturn(1);

        User sampleUser = new User("user1", "password1", "email@gmail.com", "2000-01-01", "address1");
        when(baloot.getUserById("user1")).thenReturn(sampleUser);

        doNothing().when(baloot).addComment(any(Comment.class));
        ResponseEntity<String> response = commoditiesController.addCommodityComment(commodityId, requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals("comment added successfully!", response.getBody());
    }

    @Test
    public void test_addCommodityComment_NotExistentUser() throws NotExistentUser {
        String commodityId = "1";
        Map<String, String> requestBody = Map.of("username", "user1", "comment", "comment1");

        when(baloot.generateCommentId()).thenReturn(1);
        when(baloot.getUserById("user1")).thenThrow(new NotExistentUser());

        ResponseEntity<String> response = commoditiesController.addCommodityComment(commodityId, requestBody);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(Errors.NOT_EXISTENT_USER, response.getBody());
    }

    @Test
    public void test_getCommodityComment() {
        String commodityId = "1";
        ArrayList<Comment> sampleComments = new ArrayList<>();

        when(baloot.getCommentsForCommodity(Integer.parseInt(commodityId))).thenReturn(sampleComments);

        ResponseEntity<ArrayList<Comment>> response = commoditiesController.getCommodityComment(commodityId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleComments, response.getBody());
    }

    @Test
    public void test_searchCommoditiesByName() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("searchOption", "name");
        requestBody.put("searchValue", "SampleName");

        ArrayList<Commodity> sampleCommodities = new ArrayList<>();
        when(baloot.filterCommoditiesByName(requestBody.get("searchValue"))).thenReturn(sampleCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleCommodities, response.getBody());
    }

    @Test
    public void test_searchCommoditiesByCategory() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("searchOption", "category");
        requestBody.put("searchValue", "SampleCategory");

        ArrayList<Commodity> sampleCommodities = new ArrayList<>();
        when(baloot.filterCommoditiesByName(requestBody.get("searchValue"))).thenReturn(sampleCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleCommodities, response.getBody());
    }

    @Test
    public void test_searchCommoditiesByProvider() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("searchOption", "category");
        requestBody.put("searchValue", "SampleProvider");

        ArrayList<Commodity> sampleCommodities = new ArrayList<>();
        when(baloot.filterCommoditiesByName(requestBody.get("searchValue"))).thenReturn(sampleCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(sampleCommodities, response.getBody());
    }

    @Test
    public void test_searchCommoditiesByNothing() {
        Map<String, String> requestBody = new HashMap<>();
        requestBody.put("searchOption", "Nothing");
        requestBody.put("searchValue", "SampleNothing");

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.searchCommodities(requestBody);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(new ArrayList<Commodity>(), response.getBody());
    }

    @Test
    public void test_getSuggestedCommodities_Success() throws NotExistentCommodity {
        String commodityId = "1";

        Commodity sampleCommodity = new Commodity();
        sampleCommodity.setId(commodityId);

        ArrayList<Commodity> suggestedCommodities = new ArrayList<>();


        when(baloot.getCommodityById(commodityId)).thenReturn(sampleCommodity);
        when(baloot.suggestSimilarCommodities(sampleCommodity)).thenReturn(suggestedCommodities);

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodityId);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(suggestedCommodities, response.getBody());

    }

    @Test
    public void test_getSuggestedCommodities_NotExistentCommodity() throws NotExistentCommodity {
        String commodityId = "NotExistentCommodity";

        when(baloot.getCommodityById(commodityId)).thenThrow(new NotExistentCommodity());

        ResponseEntity<ArrayList<Commodity>> response = commoditiesController.getSuggestedCommodities(commodityId);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(new ArrayList<Commodity>(), response.getBody());
    }

}

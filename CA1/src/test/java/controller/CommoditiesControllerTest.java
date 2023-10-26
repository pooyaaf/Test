package controller;

import controllers.CommentController;
import controllers.CommoditiesController;
import exceptions.NotExistentCommodity;
import model.Comment;
import model.Commodity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import service.Baloot;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
        assertEquals(sampleCommodities.size(), response.getBody().size());
    }

    @Test
    public void test_getCommodity_success() throws NotExistentCommodity {
        String commodityId = "1";
        Commodity sampleCommodity = new Commodity();
        sampleCommodity.setId(commodityId);

        when(baloot.getCommodityById(commodityId)).thenReturn(sampleCommodity);

        ResponseEntity<Commodity> response = commoditiesController.getCommodity(commodityId);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        // Todo - add assertEquals for check sampleCommodity, response.getBody()
    }

    @Test
    public void test_getCommodity_NotExistentCommodity() throws NotExistentCommodity {
        String commodityId = "NotExistentCommodity";
        CommoditiesController sampleCommoditiesController = mock(CommoditiesController.class);
        when(baloot.getCommodityById(commodityId)).thenAnswer(invocation -> {
            throw new NotExistentCommodity();
        });
        ResponseEntity<Commodity> response = commoditiesController.getCommodity(commodityId);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        assertEquals(null, response.getBody());

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

        when(baloot.getCommodityById(commodityId)).thenAnswer(invocation -> {
            throw new NotExistentCommodity();
        });

        ResponseEntity<String> response = commoditiesController.rateCommodity(commodityId, requestBody);

        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    public void test_rateCommodity_invalidRate() throws NotExistentCommodity {
        String commodityId = "1";
        Map<String, String> requestBody = Map.of("username", "user1", "rate", "invalid_rate");

        when(baloot.getCommodityById(commodityId)).thenReturn(new Commodity());

        ResponseEntity<String> response = commoditiesController.rateCommodity(commodityId, requestBody);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
    }


}

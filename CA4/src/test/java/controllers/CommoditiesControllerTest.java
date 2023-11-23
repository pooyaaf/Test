package controllers;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.SerializationFeature;
import exceptions.InvalidCreditRange;
import exceptions.NotExistentCommodity;
import exceptions.NotExistentUser;
import model.Comment;
import model.Commodity;
import model.User;

import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import service.Baloot;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockitoSettings(strictness = Strictness.LENIENT)
@ExtendWith(MockitoExtension.class)
@WebAppConfiguration
public class CommoditiesControllerTest {

    private static Stream<Arguments> populateCommodityRatingScenario(){
        ratingCommodity.addRate("user1", 5);
        Commodity addFirstUser = ratingCommodity;
        ratingCommodity.addRate("user2", 8);
        Commodity addSecUser = ratingCommodity;
        ratingCommodity.addRate("user1", 6);
        Commodity changeFirstUser = ratingCommodity;
        ratingCommodity.addRate("user3", 7);
        Commodity addThirdUser = ratingCommodity;

        return Stream.of(
                Arguments.of(addFirstUser, "user2", 5),
                Arguments.of(addSecUser, "user1", 8),
                Arguments.of(changeFirstUser, "user2", 6),
                Arguments.of(addThirdUser, "user2", 7)
        );
    }

    private static Stream<Arguments> populateCommodityRating(){
        return Stream.of(
                Arguments.of("0"),
                Arguments.of("-1"),
                Arguments.of("11")
        );
    }
    @InjectMocks
    private CommoditiesController commoditiesController;
    @Mock
    private Baloot baloot;
    private MockMvc mockMvc;

    // JSON
    private static Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private static final File commoditiesJsonFile = new File("src/test/java/resources/commodities.json").getAbsoluteFile();
    private static final File ratingCommodityJsonFile = new File("src/test/java/resources/ratingCommodity.json").getAbsoluteFile();
    private static ArrayList<Commodity> initCommodities;
    private static Commodity ratingCommodity;
    private static String initJsonCommodities;

    private final String BASE_URL_COMMODITIES = "/commodities";
    private final String BASE_URL_VARIABLE = "/{id}";
    private final String BASE_URL_RATE = "/rate";
    private final String BASE_URL_API_COMMENT = "/comment";
    private final String BASE_URL_SEARCH = "/search";
    private final String BASE_URL_SUGGEST = "/suggested";
    private final String BASE_URL_FEEDBACK_COMMENT_SUCCESSFUL = "comment added successfully!";

    @BeforeAll
    public static void setup(){
        try {
            initJsonCommodities = FileUtils.readFileToString(commoditiesJsonFile);
            initCommodities = gson.fromJson(initJsonCommodities, new TypeToken<ArrayList<Commodity>>() {}.getType());

            String initRatingJsonComment = FileUtils.readFileToString(ratingCommodityJsonFile);
            ratingCommodity = gson.fromJson(initRatingJsonComment, (Type) Commodity.class);

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(commoditiesController).build();
    }

    @AfterEach
    public void tearDown() {
        Mockito.reset(baloot);
    }

    @Test
    void test_get_commodities_success() throws Exception {

        when(baloot.getCommodities()).thenReturn(initCommodities);

        MvcResult result = mockMvc.perform(get(BASE_URL_COMMODITIES)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        ArrayList<Commodity> actual = gson.fromJson(content, new TypeToken<ArrayList<Commodity>>() {}.getType());


        Assertions.assertEquals(initCommodities.size(), actual.size());
        for (int i = 0; i < initCommodities.size(); i++) {
            assertThat(actual.get(i))
                    .usingRecursiveComparison()
                    .isEqualTo(initCommodities.get(i));
        }

    }

    @Test
    void test_get_commodity_success() throws Exception {

        String commodityId = "1";
        when(baloot.getCommodityById(any())).thenReturn(initCommodities.get(0));

        String action = BASE_URL_COMMODITIES + BASE_URL_VARIABLE;
        MvcResult result = mockMvc.perform(get(action, commodityId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        Commodity actual = gson.fromJson(content, Commodity.class);

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(initCommodities.get(0));

    }

    @Test
    void test_get_not_existed_commodity_expected_NOT_FOUND() throws Exception {

        String commodityId = "1";
        doThrow(new NotExistentCommodity()).when(baloot).getCommodityById(any());
        String action = BASE_URL_COMMODITIES +"{id}";
        MvcResult result = mockMvc.perform(get(action, commodityId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
        result.getResponse().getContentAsString();
        assertNull(commoditiesController.getCommodity(commodityId).getBody());
        assertEquals(HttpStatus.NOT_FOUND, commoditiesController.getCommodity(commodityId).getStatusCode());
    }

    @ParameterizedTest
    @MethodSource("populateCommodityRatingScenario")
    void test_change_rate_successful(Commodity c, String username, int newVote) throws Exception {
        int countRating = c.getUserRate().size() +1;
        float sumBeforeVote = c.getRating() * countRating;
        float expectedRate = 0;

        Map<String, String> input = new HashMap<String, String>(){{
            put("rate", String.valueOf(newVote));
            put("username", username);
        }};

        when(baloot.getCommodityById(any())).thenReturn(c);

        if (c.getUserRate().containsKey(username))
            expectedRate = (sumBeforeVote - c.getUserRate().get(username) + newVote)/(countRating);
        else
            expectedRate = (sumBeforeVote + newVote)/(countRating+1);

        c.addRate(username, newVote);
        float finalRating = c.getRating();

        ObjectMapper mapper = new ObjectMapper();
        String requestJson=mapper.writeValueAsString(input);

        MvcResult result = mockMvc.perform(post("/commodities/{id}/rate", "1")
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(expectedRate, finalRating);
        assertEquals("rate added successfully!", content);

    }

    @Test
    void test_vote_on_invalid_commodity_id_expected_NotExistentCommodity() throws Exception {
        String commodityId = "-1";
        Map<String, String> input = new HashMap<String, String>(){{
            put("rate", "0");
            put("username", "user2");
        }};
        ObjectMapper mapper = new ObjectMapper();
        String requestJson=mapper.writeValueAsString(input);

        doThrow(new NotExistentCommodity()).when(baloot).getCommodityById(any());

        String action = BASE_URL_COMMODITIES + BASE_URL_VARIABLE + BASE_URL_RATE;
        MvcResult result = mockMvc.perform(post(action, commodityId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        result.getResponse().getContentAsString();

    }

    @ParameterizedTest
    @MethodSource("populateCommodityRating")
    void test_vote_no_commodity_expected_NumberFormatException(String vote) throws Exception {
        String commodityId = "-1";
        Map<String, String> input = new HashMap<String, String>() {{
            put("rate", String.valueOf(vote));
            put("username", "user2");
        }};
        ObjectMapper mapper = new ObjectMapper();
        String requestJson=mapper.writeValueAsString(input);

        doThrow(new NumberFormatException()).when(baloot).getCommodityById(any());

        String action = BASE_URL_COMMODITIES + BASE_URL_VARIABLE + BASE_URL_RATE;
        MvcResult result = mockMvc.perform(post(action, commodityId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        result.getResponse().getContentAsString();
    }

    @Test
    void test_comment_successful() throws Exception {
        String commodityId = "1";
        String username = "username";
        Map<String, String> input = new HashMap<String, String>() {{
            put("username", username);
            put("comment", "The product xxx was great!!!");
        }};

        ObjectMapper objectMapper = new ObjectMapper();
        String requestJson = objectMapper.writeValueAsString(input);

        User u =new User("username", "password", "email", "01/01/1899", "1 St Jones");
        when(baloot.getUserById(any())).thenReturn(u);

        String action = BASE_URL_COMMODITIES + BASE_URL_VARIABLE + BASE_URL_API_COMMENT;
        MvcResult result = mockMvc.perform(post(action, commodityId)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertEquals(BASE_URL_FEEDBACK_COMMENT_SUCCESSFUL, content);

    }

    @Test
    void test_get_commodity_list_when_commodity_has_comments_successful() throws Exception {
        String commodityId = "1";
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date temp = new Date();

        List<Comment> expectedComments = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            Comment comment = new Comment(i, "userEmail@gmail.com", "username", 8101, "text");
            comment.setDate(dateFormat.format(temp));
            expectedComments.add(comment);
        }
        when(baloot.getCommentsForCommodity(anyInt())).thenReturn((ArrayList<Comment>) expectedComments);

        String action = BASE_URL_COMMODITIES + BASE_URL_VARIABLE + BASE_URL_API_COMMENT;
        MvcResult result = mockMvc.perform(get(action, commodityId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ArrayList<Comment> actual = objectMapper.readValue(content, new TypeReference<ArrayList<Comment>>() {});        //verify
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(expectedComments);

    }

    @Test
    void test_user_not_exist_in_addin_comment_not_existence_user() throws Exception {
        String commodityId = "-1";
        doThrow(new NotExistentUser()).when(baloot).getUserById(any());

        String action = BASE_URL_COMMODITIES + BASE_URL_VARIABLE + BASE_URL_API_COMMENT;
        MvcResult result = mockMvc.perform(post(action, commodityId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
        result.getResponse().getContentAsString();
    }

    @Test
    void test_search_category_success() throws Exception {
        List<String> searchOptions = Arrays.asList("category", "name", "provider");
        Map<String, String> input = new HashMap<String, String>() {{
            put("searchOption", "category");
            put("searchValue", "phone");
        }};
        ObjectMapper mapper = new ObjectMapper();
        String requestJson = mapper.writeValueAsString(input);


        when(baloot.filterCommoditiesByName(any())).thenReturn(initCommodities);
        when(baloot.filterCommoditiesByCategory(any())).thenReturn(initCommodities);
        when(baloot.filterCommoditiesByProviderName(any())).thenReturn(initCommodities);

        String action = BASE_URL_COMMODITIES + BASE_URL_SEARCH;
        MvcResult result = mockMvc.perform(post(action)
                        .content(requestJson)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        List<Commodity> actual = objectMapper.readValue(content, new TypeReference<List<Commodity>>() {});
        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(initCommodities);

    }

    @Test
    void test_suggest_based_on_category_successful() throws Exception {
        String commodityId = "1";

        when(baloot.getCommodityById(any())).thenReturn(initCommodities.get(0));
        when(baloot.suggestSimilarCommodities(any())).thenReturn(new ArrayList<>(List.of(initCommodities.get(1))));

        String action = BASE_URL_COMMODITIES + BASE_URL_VARIABLE + BASE_URL_SUGGEST;
        MvcResult result = mockMvc.perform(get(action, commodityId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ArrayList<Commodity> actual = objectMapper.readValue(content, new TypeReference<ArrayList<Commodity>>() {});

        assertThat(actual)
                .usingRecursiveComparison()
                .isEqualTo(new ArrayList<>(List.of(initCommodities.get(1))));

    }

    @Test
    void test_commodity_is_not_in_suggestion_expected_NotExistentCommodity() throws Exception {
        String commodityId = "1";

        doThrow(new NotExistentCommodity()).when(baloot).getCommodityById(any());
        when(baloot.suggestSimilarCommodities(any())).thenReturn(new ArrayList<>());

        String action = BASE_URL_COMMODITIES + BASE_URL_VARIABLE + BASE_URL_SUGGEST;
        MvcResult result = mockMvc.perform(get(action, commodityId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();

        String content = result.getResponse().getContentAsString();

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        ArrayList<Commodity> actual = objectMapper.readValue(content, new TypeReference<ArrayList<Commodity>>() {});
    }

}
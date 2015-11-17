import com.fasterxml.jackson.databind.ObjectMapper;
import com.test.vote.manager.helper.DateController;
import com.test.vote.manager.VoteServiceStarter;
import com.test.vote.manager.dao.MenuRepository;
import com.test.vote.manager.dao.RestaurantRepository;
import com.test.vote.manager.dao.UserRepository;
import com.test.vote.manager.dao.VoteRepository;
import com.test.vote.manager.dao.entity.UserEntity;
import com.test.vote.manager.dao.entity.UserRole;
import com.test.vote.manager.rest.dto.*;
import junit.framework.TestCase;
import org.apache.tomcat.util.codec.binary.Base64;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.WebIntegrationTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.http.converter.xml.SourceHttpMessageConverter;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = VoteServiceStarter.class)
@WebIntegrationTest

public class BasicTests extends TestCase {

    public static final String HOST_PORT = "http://localhost:8080";
    public static final String VOTES_URI = HOST_PORT + "/votes";
    public static final String VOTES_RESTAURANT_URI = HOST_PORT + "/votes?restaurantId={restaurantId}";

    public static final String USERS_URI = HOST_PORT + "/users";
    public static final String USERS_BY_ID_URI = HOST_PORT + "/users/{id}";
    public static final String RESTAURANTS_URI = HOST_PORT + "/restaurants";
    public static final String RESTAURANTS_BY_ID_URI = HOST_PORT + "/restaurants/{id}";
    public static final String MENU_URI = RESTAURANTS_BY_ID_URI + "/menu";


    public static final String VOTE_STATISTICS_URI = HOST_PORT + "/voteStatistics";


    public static final String ID_FIELD = "id";
    public static final String RESTAURANT_ID_FIELD = "restaurantId";
    public static final String MENU_ID_FIELD = "menuId";

    public static final String ADMIN_USER_NAME = "ADMIN_USER";
    public static final String FIRST_REGULAR_USER_NAME = "FIRST_USER";
    public static final String SECOND_REGULAR_USER_NAME = "SECOND_USER";

    public static final String PASSWORD = "PASSWORD";

    public static final String FIRST_RESTAURANT_NAME = "FIRST_RESTAURANT";
    public static final String SECOND_RESTAURANT_NAME = "SECOND_RESTAURANT";
    public static final String THIRD_RESTAURANT_NAME = "THIRD_RESTAURANT";

    public static final String MENU_ITEM = "menu item";
    public static final int ITEM_PRICE = 3;
    public static final int MENU_SIZE = 3;



    private static final Logger LOGGER = LoggerFactory.getLogger(BasicTests.class);
    @Autowired
    private UserRepository userRepo;
    @Autowired
    private VoteRepository voteRepo;
    @Autowired
    private RestaurantRepository restRepo;
    @Autowired
    private MenuRepository menuRepo;

    private ObjectMapper objectMapper;
    private RestTemplate template;

    @Before
    public void before() {
        UserEntity admin = new UserEntity();
        admin.setName(ADMIN_USER_NAME);
        admin.setRole(UserRole.ROLE_ADMIN);
        admin.setPassword(PASSWORD);
        userRepo.save(admin);

        objectMapper = new ObjectMapper();

        template = new RestTemplate();
        template.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
        template.getMessageConverters().add(new SourceHttpMessageConverter());

    }


    @After
    public void after() {
        userRepo.deleteAll();
        voteRepo.deleteAll();
        restRepo.deleteAll();
        menuRepo.deleteAll();

        DateController.getInstance().notOverride();
    }


    private HttpHeaders getAuthHeaders(String name, String password) {
        String plainCreds = name + ":" + password;
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);
        return headers;
    }

    @Test
    public void addAllNessesaryUsersTest() {
//add first user
        UserDTO user = new UserDTO();
        user.setName(FIRST_REGULAR_USER_NAME);
        user.setRole(UserRole.ROLE_USER);
        user.setPassword(PASSWORD);

        HttpEntity<?> request = new HttpEntity<Object>(user, getAuthHeaders(ADMIN_USER_NAME, PASSWORD));
        ResponseEntity<DTOContainer> createdUser = template.exchange(USERS_URI, HttpMethod.POST, request, DTOContainer.class);
        assertTrue(createdUser.getBody().getSuccess());
        UserDTO data = objectMapper.convertValue(createdUser.getBody().getData(), UserDTO.class);
        Map<String, String> params = new HashMap<String, String>();
        params.put(ID_FIELD, String.valueOf(data.getId()));
        request = new HttpEntity<Object>(getAuthHeaders(ADMIN_USER_NAME, PASSWORD));
        ResponseEntity<DTOContainer> exchangedUser = template.exchange(USERS_BY_ID_URI, HttpMethod.GET, request, DTOContainer.class, params);
        assertTrue(exchangedUser.getBody().getSuccess());
        UserDTO receivedUser = objectMapper.convertValue(createdUser.getBody().getData(), UserDTO.class);
        assertEquals(data.getId(), receivedUser.getId());

//add second regular user


        user = new UserDTO();
        user.setName(SECOND_REGULAR_USER_NAME);
        user.setRole(UserRole.ROLE_USER);

        user.setPassword(PASSWORD);
        request = new HttpEntity<Object>(user, getAuthHeaders(ADMIN_USER_NAME, PASSWORD));
        createdUser = template.exchange(USERS_URI, HttpMethod.POST, request, DTOContainer.class);
        assertTrue(createdUser.getBody().getSuccess());
        data = objectMapper.convertValue(createdUser.getBody().getData(), UserDTO.class);
        params = new HashMap<String, String>();
        params.put(ID_FIELD, String.valueOf(data.getId()));
        request = new HttpEntity<Object>(getAuthHeaders(ADMIN_USER_NAME, PASSWORD));
        exchangedUser = template.exchange(USERS_BY_ID_URI, HttpMethod.GET, request, DTOContainer.class, params);
        assertTrue(exchangedUser.getBody().getSuccess());
        receivedUser = objectMapper.convertValue(createdUser.getBody().getData(), UserDTO.class);
        assertEquals(data.getId(), receivedUser.getId());
    }

    @Test
    public void addRestaurants() {

        //add first restaurant
        RestaurantDTO restaurant = new RestaurantDTO();
        restaurant.setName(FIRST_RESTAURANT_NAME);
        HttpEntity<?> request = new HttpEntity<Object>(restaurant, getAuthHeaders(ADMIN_USER_NAME, PASSWORD));
        ResponseEntity<DTOContainer> createdRestaurant = template.exchange(RESTAURANTS_URI, HttpMethod.POST, request, DTOContainer.class);
        assertTrue(createdRestaurant.getBody().getSuccess());
        UserDTO receivedRestaurant = objectMapper.convertValue(createdRestaurant.getBody().getData(), UserDTO.class);
        int receivedId = receivedRestaurant.getId();
        Map<String, String> params = new HashMap<String, String>();
        params.put(ID_FIELD, String.valueOf(receivedId));
        request = new HttpEntity<Object>(getAuthHeaders(ADMIN_USER_NAME, PASSWORD));
        ResponseEntity<DTOContainer> exchangedRestaurant = template.exchange(RESTAURANTS_BY_ID_URI, HttpMethod.GET, request, DTOContainer.class, params);
        assertTrue(exchangedRestaurant.getBody().getSuccess());
        receivedRestaurant = objectMapper.convertValue(exchangedRestaurant.getBody().getData(), UserDTO.class);
        assertEquals(receivedId, receivedRestaurant.getId());

//add second restaurant
        restaurant = new RestaurantDTO();
        restaurant.setName(SECOND_RESTAURANT_NAME);

        request = new HttpEntity<Object>(restaurant, getAuthHeaders(ADMIN_USER_NAME, PASSWORD));
        createdRestaurant = template.exchange(RESTAURANTS_URI, HttpMethod.POST, request, DTOContainer.class);
        assertTrue(createdRestaurant.getBody().getSuccess());
        receivedRestaurant = objectMapper.convertValue(createdRestaurant.getBody().getData(), UserDTO.class);
        receivedId = receivedRestaurant.getId();

        params = new HashMap<String, String>();
        params.put(ID_FIELD, String.valueOf(receivedId));

        request = new HttpEntity<Object>(getAuthHeaders(ADMIN_USER_NAME, PASSWORD));
        exchangedRestaurant = template.exchange(RESTAURANTS_BY_ID_URI, HttpMethod.GET, request, DTOContainer.class, params);
        assertTrue(exchangedRestaurant.getBody().getSuccess());
        receivedRestaurant = objectMapper.convertValue(exchangedRestaurant.getBody().getData(), UserDTO.class);
        assertEquals(receivedId, receivedRestaurant.getId());

    }

    @Test
    public void addRestaurantsMenus() {
        addRestaurants();
        HttpEntity<?> request = new HttpEntity<Object>(getAuthHeaders(ADMIN_USER_NAME, PASSWORD));
        ResponseEntity<DTOContainer> restaurants = template.exchange(RESTAURANTS_URI, HttpMethod.GET, request, DTOContainer.class);
        assertTrue(restaurants.getBody().getSuccess());
        List restaurantsList = objectMapper.convertValue(restaurants.getBody().getData(), List.class);
        for (Object restaurant : restaurantsList) {
            RestaurantDTO restaurantDTO = objectMapper.convertValue(restaurant, RestaurantDTO.class);
            int restaurantId = restaurantDTO.getId();
            Map<String, String> params = new HashMap<String, String>();
            params.put(ID_FIELD, String.valueOf(restaurantId));
            for (int i = 0; i < MENU_SIZE; i++) {
                MenuDTO menuItem = new MenuDTO();
                menuItem.setPrice(ITEM_PRICE);
                menuItem.setName(MENU_ITEM);
                menuItem.setRestaurantId(restaurantId);

                request = new HttpEntity<Object>(menuItem, getAuthHeaders(ADMIN_USER_NAME, PASSWORD));
                ResponseEntity<DTOContainer> postResult = template.exchange(MENU_URI, HttpMethod.POST, request, DTOContainer.class,params);
                assertTrue(postResult.getBody().getSuccess());
            }
            request = new HttpEntity<Object>(getAuthHeaders(ADMIN_USER_NAME, PASSWORD));
            ResponseEntity<DTOContainer> menuResult = template.exchange(MENU_URI, HttpMethod.GET, request, DTOContainer.class,params);
            List menuList = objectMapper.convertValue(menuResult.getBody().getData(), List.class);
            assertEquals(MENU_SIZE, menuList.size());
        }


    }

    @Test
    public void voteRestaurants() {
        addAllNessesaryUsersTest();
        addRestaurantsMenus();
        HttpEntity<?> request = new HttpEntity<Object>(getAuthHeaders(FIRST_REGULAR_USER_NAME, PASSWORD));
        ResponseEntity<DTOContainer> restaurants = template.exchange(RESTAURANTS_URI, HttpMethod.GET, request, DTOContainer.class);
        assertTrue(restaurants.getBody().getSuccess());
        List restaurantsList = objectMapper.convertValue(restaurants.getBody().getData(), List.class);
        RestaurantDTO restaurantDTO = objectMapper.convertValue(restaurantsList.get(0), RestaurantDTO.class);
        VoteDTO voteDTO = new VoteDTO();
        voteDTO.setRestaurantId(restaurantDTO.getId());
        request = new HttpEntity<Object>(voteDTO,getAuthHeaders(FIRST_REGULAR_USER_NAME, PASSWORD));

        ResponseEntity<DTOContainer> postResult = template.exchange(VOTES_URI, HttpMethod.POST, request, DTOContainer.class);
        assertTrue(postResult.getBody().getSuccess());

        request = new HttpEntity<Object>(voteDTO,getAuthHeaders(SECOND_REGULAR_USER_NAME, PASSWORD));
        postResult = template.exchange(VOTES_URI, HttpMethod.POST, request, DTOContainer.class);
        assertTrue(postResult.getBody().getSuccess());


        Map<String, String> params = new HashMap<String, String>();
        params.put(RESTAURANT_ID_FIELD, String.valueOf(restaurantDTO.getId()));
        ResponseEntity<DTOContainer> exchange = template.exchange(VOTES_RESTAURANT_URI, HttpMethod.GET, request, DTOContainer.class,params);
        assertTrue(exchange.getBody().getSuccess());
        List list= objectMapper.convertValue(exchange.getBody().getData(), List.class);
        assertEquals(2, list.size());
    }

    @Test
      public void voteRestaurantsChangeMind() {
        voteRestaurants();
        HttpEntity<?> request = new HttpEntity<Object>(getAuthHeaders(FIRST_REGULAR_USER_NAME, PASSWORD));
        ResponseEntity<DTOContainer> restaurants = template.exchange(RESTAURANTS_URI, HttpMethod.GET, request, DTOContainer.class);
        assertTrue(restaurants.getBody().getSuccess());
        List restaurantsList = objectMapper.convertValue(restaurants.getBody().getData(), List.class);
        RestaurantDTO restaurantDTO = objectMapper.convertValue(restaurantsList.get(0), RestaurantDTO.class);
        int restaurantIdFirst = restaurantDTO.getId();

        restaurantDTO = objectMapper.convertValue(restaurantsList.get(1), RestaurantDTO.class);
        int restaurantIdSecond = restaurantDTO.getId();

        VoteDTO voteDTO = new VoteDTO();
        voteDTO.setRestaurantId(restaurantIdSecond);
        request = new HttpEntity<Object>(voteDTO,getAuthHeaders(SECOND_REGULAR_USER_NAME, PASSWORD));

        ResponseEntity<DTOContainer> postResult = template.exchange(VOTES_URI, HttpMethod.PUT, request, DTOContainer.class);
        assertTrue(postResult.getBody().getSuccess());

        request = new HttpEntity<Object>(getAuthHeaders(SECOND_REGULAR_USER_NAME, PASSWORD));

        Map<String, String> params = new HashMap<String, String>();
        params.put(RESTAURANT_ID_FIELD, String.valueOf(restaurantIdFirst));
        ResponseEntity<DTOContainer> exchange = template.exchange(VOTES_RESTAURANT_URI, HttpMethod.GET, request, DTOContainer.class,params);
        assertTrue(exchange.getBody().getSuccess());
        List list= objectMapper.convertValue(exchange.getBody().getData(), List.class);
        assertEquals(1, list.size());


        params = new HashMap<String, String>();
        params.put(RESTAURANT_ID_FIELD, String.valueOf(restaurantIdSecond));
        exchange = template.exchange(VOTES_RESTAURANT_URI, HttpMethod.GET, request, DTOContainer.class,params);
        assertTrue(exchange.getBody().getSuccess());
        list= objectMapper.convertValue(exchange.getBody().getData(), List.class);
        assertEquals(1, list.size());

    }

    @Test
    public void voteRestaurantsChangeTime(){
        voteRestaurants();
        HttpEntity<?> request = new HttpEntity<Object>(getAuthHeaders(FIRST_REGULAR_USER_NAME, PASSWORD));
        ResponseEntity<DTOContainer> restaurants = template.exchange(RESTAURANTS_URI, HttpMethod.GET, request, DTOContainer.class);
        assertTrue(restaurants.getBody().getSuccess());
        List restaurantsList = objectMapper.convertValue(restaurants.getBody().getData(), List.class);
        RestaurantDTO restaurantDTO = objectMapper.convertValue(restaurantsList.get(0), RestaurantDTO.class);
        int restaurantIdFirst = restaurantDTO.getId();

        restaurantDTO = objectMapper.convertValue(restaurantsList.get(1), RestaurantDTO.class);
        int restaurantIdSecond = restaurantDTO.getId();

        VoteDTO voteDTO = new VoteDTO();
        voteDTO.setRestaurantId(restaurantIdSecond);
        request = new HttpEntity<Object>(voteDTO,getAuthHeaders(SECOND_REGULAR_USER_NAME, PASSWORD));

        LocalDateTime currentTime = DateController.getInstance().getCurrentTime();
        LocalDateTime dt = LocalDateTime.of(currentTime.getYear(),currentTime.getMonth(),currentTime.getDayOfMonth(),0,0);

        DateController.getInstance().setPresetTime( dt.plusHours(12));
        ResponseEntity<DTOContainer> postResult = template.exchange(VOTES_URI, HttpMethod.PUT, request, DTOContainer.class);
        assertTrue(!postResult.getBody().getSuccess());

        request = new HttpEntity<Object>(getAuthHeaders(SECOND_REGULAR_USER_NAME, PASSWORD));

        Map<String, String> params = new HashMap<String, String>();
        params.put(RESTAURANT_ID_FIELD, String.valueOf(restaurantIdFirst));
        ResponseEntity<DTOContainer> exchange = template.exchange(VOTES_RESTAURANT_URI, HttpMethod.GET, request, DTOContainer.class,params);
        assertTrue(exchange.getBody().getSuccess());
        List list= objectMapper.convertValue(exchange.getBody().getData(), List.class);
        assertEquals(2, list.size());

    }

    @Test
    public void voteViewVoteStatistic() {
       voteRestaurants();
        HttpEntity<?> request = new HttpEntity<Object>(getAuthHeaders(FIRST_REGULAR_USER_NAME, PASSWORD));
        ResponseEntity<DTOContainer> statistics = template.exchange(VOTE_STATISTICS_URI, HttpMethod.GET, request, DTOContainer.class);
        assertTrue(statistics.getBody().getSuccess());
        List statisticsList = objectMapper.convertValue(statistics.getBody().getData(), List.class);
        assertEquals(1,statisticsList.size() );
        StatisticItemDTO dto = objectMapper.convertValue(statisticsList.get(0), StatisticItemDTO.class);
        assertEquals(2, dto.getVotesCount());
    }



    @Test
    public void forbiddenAccess() {
        addAllNessesaryUsersTest();
        UserDTO user = new UserDTO();
        user.setName(FIRST_REGULAR_USER_NAME);
        user.setRole(UserRole.ROLE_USER);
        user.setPassword(PASSWORD);

        HttpEntity<?> request = new HttpEntity<Object>(user, getAuthHeaders(FIRST_REGULAR_USER_NAME, PASSWORD));
        ResponseEntity<DTOContainer> createdUser = template.exchange(USERS_URI, HttpMethod.POST, request, DTOContainer.class);
        assertFalse(createdUser.getBody().getSuccess());
    }

}

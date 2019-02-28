package com.base.unit;

import com.base.DataSeed;
import com.base.controller.AuthenticationController;
import com.base.model.User;
import com.base.service.UserService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Toyin on 2/28/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthenticationControllerTest {
    MockMvc mockMvc;

    @Mock
    private AuthenticationController authController;

    @MockBean
    private UserService userService;

    @Autowired
    private TestRestTemplate template;

    private User user = DataSeed.generateUser(1);
    private User user2 = DataSeed.generateUser(2);

    @Before
    public void setup() throws Exception {
        when(userService.findByUsernameOrEmail(anyString(), eq("user2@mail.com"))).thenReturn(user2);
        when(userService.save(any(User.class))).thenReturn(user);
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    public void createUser() {
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"username\": \"username 1\", \"email\": \"user1@mail.com\","
                        + " \"firstname\": \"firstname1\",\"lastname\":\"lastname1\","
                        + " \"password\": \"password\",\"dateOfBirth\":\"2000-10-10\"}");

        ResponseEntity<User> response = template.postForEntity(
                "/signup", userRequest, User.class);
        Assert.assertEquals(200,response.getStatusCode().value());
        Assert.assertEquals(user.getUsername(), response.getBody().getUsername());
    }

    @Test
    public void shouldNotCreateUserWithExistingEmail() {
        HttpEntity<Object> member = getHttpEntity(
                "{\"username\": \"username 1\", \"email\": \"user2@mail.com\","
                        + " \"firstname\": \"firstname1\",\"lastname\":\"lastname1\","
                        + " \"password\": \"password\",\"dateOfBirth\":\"2000-10-10\"}");

        ResponseEntity<User> response = template.postForEntity(
                "/signup", member, User.class);
        Assert.assertEquals(409,response.getStatusCode().value());
    }

    @Test
    public void shouldNotCreateUserWithoutEmail() {
        HttpEntity<Object> member = getHttpEntity(
                "{\"username\": \"username 1\","
                        + " \"firstname\": \"firstname1\",\"lastname\":\"lastname1\","
                        + " \"password\": \"password\",\"dateOfBirth\":\"2000-10-10\"}");

        ResponseEntity<User> response = template.postForEntity(
                "/signup", member, User.class);
        Assert.assertEquals(400,response.getStatusCode().value());
    }

    private HttpEntity<Object> getHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<Object>(body, headers);
    }
}

package com.base.integration;

import com.base.DataSeed;
import com.base.controller.AuthenticationController;
import com.base.dao.UserDao;
import com.base.dto.AuthToken;
import com.base.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


/**
 * Created by Toyin on 2/28/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AuthenticationControllerTest {
    MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Mock
    private AuthenticationController authenticationController;

    private AuthToken authToken;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private UserDao userDao;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private FilterChainProxy springSecurityFilter;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(springSecurityFilter, "/*").build();
    }

    @Test
    public void signup() {
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"username\": \"username 1\", \"email\": \"user1@mail.com\","
                        + " \"firstname\": \"firstname1\",\"lastname\":\"lastname1\","
                        + " \"password\": \"password\",\"dateOfBirth\":\"2000-10-10\"}");

        ResponseEntity<User> response = template.postForEntity(
                "/signup", userRequest, User.class);
        userDao.deleteById(response.getBody().getId());
        Assert.assertEquals(200,response.getStatusCode().value());
        Assert.assertEquals("username 1", response.getBody().getUsername());
    }

    @Test
    public void shouldSignupWithExistingEmail() {
        userDao.saveAndFlush(DataSeed.generateUser(2));
        HttpEntity<Object> member = getHttpEntity(
                "{\"username\": \"username 1\", \"email\": \"user2@mail.com\","
                        + " \"firstname\": \"firstname1\",\"lastname\":\"lastname1\","
                        + " \"password\": \"password\",\"dateOfBirth\":\"2000-10-10\"}");

        ResponseEntity<User> response = template.postForEntity(
                "/signup", member, User.class);
        Assert.assertEquals(409,response.getStatusCode().value());
    }

    @Test
    public void shouldNotSignupWithoutEmail() {
        HttpEntity<Object> member = getHttpEntity(
                "{\"username\": \"username 1\","
                        + " \"firstname\": \"firstname1\",\"lastname\":\"lastname1\","
                        + " \"password\": \"password\",\"dateOfBirth\":\"2000-10-10\"}");

        ResponseEntity<User> response = template.postForEntity(
                "/signup", member, User.class);
        Assert.assertEquals(400,response.getStatusCode().value());
    }

    @Test
    public void shouldGenerateToken() {
        User user = DataSeed.generateUser(3);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        userDao.saveAndFlush(user);
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"username\": \"user3\", \"password\": \"password\"}");
        ResponseEntity<AuthToken> response = template.postForEntity(
                "/login", userRequest, AuthToken.class);
        authToken = response.getBody();
        Assert.assertEquals(200,response.getStatusCode().value());
        Assert.assertNotNull(authToken);
        Assert.assertNotNull(authToken.getToken());
    }

    @Test
    public void shouldFailInvalidLogin() {
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"username\": \"user3\", \"password\": \"password\"}");
        ResponseEntity<AuthToken> response = template.postForEntity(
                "/login", userRequest, AuthToken.class);
        Assert.assertEquals(401,response.getStatusCode().value());
    }

    private HttpEntity<Object> getHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return new HttpEntity<Object>(body, headers);
    }
}

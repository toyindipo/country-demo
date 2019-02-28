package com.base.integration;

import com.base.DataSeed;
import com.base.controller.CountryController;
import com.base.dao.CountryDao;
import com.base.dao.UserDao;
import com.base.dto.AuthToken;
import com.base.model.Country;
import com.base.model.User;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Optional;


/**
 * Created by Toyin on 2/28/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CountryControllerTest {
    MockMvc mockMvc;
    @Autowired
    private WebApplicationContext context;
    @Mock
    private CountryController countryController;

    @Autowired
    private CountryDao countryDao;

    @Autowired
    private UserDao userDao;

    private AuthToken authToken;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    private User user;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private FilterChainProxy springSecurityFilter;

    @Before
    public void setup() throws Exception {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .addFilter(springSecurityFilter, "/*").build();
        generateToken();
    }

    @Test
    public void createCountry() {
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"name\": \"country1\", \"continent\": \"continent\"}");
        ResponseEntity<Country> response = template.postForEntity(
                "/countries", userRequest, Country.class);
        countryDao.deleteById(response.getBody().getId());
        userDao.deleteById(user.getId());
        Assert.assertEquals(200,response.getStatusCode().value());
        Assert.assertEquals("country1", response.getBody().getName());
    }

    @Test
    public void shouldNotCreateCountryWithoutName() {
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"continent\": \"continent\"}");
        ResponseEntity<Country> response = template.postForEntity(
                "/countries", userRequest, Country.class);
        userDao.deleteById(user.getId());
        Assert.assertEquals(400,response.getStatusCode().value());
    }

    @Test
    public void shouldNotCreateCountryWithExistingName() {
        countryDao.saveAndFlush(DataSeed.generateCountry(2));
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"name\": \"country2\", \"continent\": \"continent\"}");
        ResponseEntity<Country> response = template.postForEntity(
                "/countries", userRequest, Country.class);
        userDao.deleteById(user.getId());
        Assert.assertEquals(409,response.getStatusCode().value());
    }

    @Test
    public void getAllCountries() {
        for (int i = 3; i < 8; i++) {
            countryDao.saveAndFlush(DataSeed.generateCountry(i));
        }
        HttpEntity<Object> userRequest = getHttpEntity(
                null);
        ResponseEntity<Country[]> response =
                template.exchange("/countries", HttpMethod.GET,
                        new HttpEntity<>(userRequest.getHeaders()), Country[].class);
        userDao.deleteById(user.getId());
        Assert.assertEquals(200,response.getStatusCode().value());
        Assert.assertEquals(5, response.getBody().length);
    }

    @Test
    public void editCountry() {
        Country country = countryDao.saveAndFlush(DataSeed.generateCountry(8));
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"name\": \"country80\", \"continent\": \"continent80\"}");
        ResponseEntity<Country> response =
                template.exchange("/countries/" + country.getId(), HttpMethod.PUT,
                        userRequest, Country.class);
        countryDao.deleteById(country.getId());
        userDao.deleteById(user.getId());
        Assert.assertEquals(200,response.getStatusCode().value());
        Assert.assertEquals("country80", response.getBody().getName());
        Assert.assertEquals("continent80", response.getBody().getContinent());
    }

    @Test
    public void deleteCountry() {
        Country country = countryDao.saveAndFlush(DataSeed.generateCountry(9));
        HttpEntity<Object> userRequest = getHttpEntity(
                null);
        ResponseEntity<Country> response =
                template.exchange("/countries/" + country.getId(), HttpMethod.DELETE,
                        userRequest, Country.class);
        userDao.deleteById(user.getId());
        Assert.assertEquals(200,response.getStatusCode().value());
        Optional<Country> countryOptional = countryDao.findById(country.getId());
        Assert.assertFalse(countryOptional.isPresent());
    }

    private HttpEntity<Object> getHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authToken != null)
            headers.set("Authorization", "Bearer " + authToken.getToken());
        return new HttpEntity<Object>(body, headers);
    }

    private void generateToken() {
        user = DataSeed.generateUser(1);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user = userDao.saveAndFlush(user);
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"username\": \"user1\", \"password\": \"password\"}");
        ResponseEntity<AuthToken> response = template.postForEntity(
                "/login", userRequest, AuthToken.class);
        authToken = response.getBody();
    }
}

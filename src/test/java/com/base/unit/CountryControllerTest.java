package com.base.unit;

import com.base.DataSeed;
import com.base.controller.CountryController;
import com.base.dao.UserDao;
import com.base.dto.AuthToken;
import com.base.model.Country;
import com.base.model.User;
import com.base.service.CountryService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

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

    @MockBean
    private CountryService countryService;

    @MockBean
    private UserDao userDao;

    private AuthToken authToken;

    @Autowired
    private TestRestTemplate template;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    private FilterChainProxy springSecurityFilter;


    private Country country = DataSeed.generateCountry(1);
    private Country country2 = DataSeed.generateCountry(2);
    private List<Country> countries;

    @Before
    public void setup() throws Exception {
        when(countryService.edit(any(Country.class), any(Country.class))).thenReturn(country2);
        when(countryService.save(any(Country.class))).thenReturn(country);
        when(countryService.findByName(country2.getName())).thenReturn(country2);
        when(countryService.findById(1l)).thenReturn(Optional.of(country));
        doNothing().when(countryService).delete(anyLong());

        countries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            countries.add(DataSeed.generateCountry(i));
        }
        when(countryService.getAll()).thenReturn(countries);
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
        Assert.assertEquals(200,response.getStatusCode().value());
        Assert.assertEquals(country.getName(), response.getBody().getName());
    }

    @Test
    public void shouldNotCreateCountryWithoutName() {
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"continent\": \"continent\"}");
        ResponseEntity<Country> response = template.postForEntity(
                "/countries", userRequest, Country.class);
        Assert.assertEquals(400,response.getStatusCode().value());
    }

    @Test
    public void shouldNotCreateCountryWithExistingName() {
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"name\": \"country2\", \"continent\": \"continent\"}");
        ResponseEntity<Country> response = template.postForEntity(
                "/countries", userRequest, Country.class);
        Assert.assertEquals(409,response.getStatusCode().value());
    }

    @Test
    public void getAllCountries() {
        HttpEntity<Object> userRequest = getHttpEntity(
                null);
        ResponseEntity<Country[]> response =
                template.exchange("/countries", HttpMethod.GET,
                        new HttpEntity<>(userRequest.getHeaders()), Country[].class);

        Assert.assertEquals(200,response.getStatusCode().value());
        Assert.assertEquals(5, response.getBody().length);
    }

    @Test
    public void editCountry() {
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"name\": \"country2\", \"continent\": \"continent\"}");
        ResponseEntity<Country> response =
                template.exchange("/countries/1", HttpMethod.PUT,
                        userRequest, Country.class);
        Assert.assertEquals(200,response.getStatusCode().value());
        Assert.assertEquals("country2", response.getBody().getName());
    }

    @Test
    public void deleteCountry() {
        HttpEntity<Object> userRequest = getHttpEntity(
                null);
        ResponseEntity<Country> response =
                template.exchange("/countries/1", HttpMethod.DELETE,
                        userRequest, Country.class);
        Assert.assertEquals(200,response.getStatusCode().value());
    }

    private HttpEntity<Object> getHttpEntity(Object body) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if (authToken != null)
            headers.set("Authorization", "Bearer " + authToken.getToken());
        return new HttpEntity<Object>(body, headers);
    }

    private void generateToken() {
        User user = DataSeed.generateUser(1);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        when(userDao.findByUsernameOrEmail(user.getUsername())).thenReturn(user);
        HttpEntity<Object> userRequest = getHttpEntity(
                "{\"username\": \"user1\", \"password\": \"password\"}");
        ResponseEntity<AuthToken> response = template.postForEntity(
                "/login", userRequest, AuthToken.class);
        authToken = response.getBody();
    }
}

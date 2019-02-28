package com.base.unit;

import com.base.DataSeed;
import com.base.dao.CountryDao;
import com.base.model.Country;
import com.base.service.CountryService;
import com.base.service.impl.CountryServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Toyin on 2/28/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class CountryServiceTest {
    @MockBean
    private CountryDao countryDao;
    private Country newCountry;
    private List<Country> countries;
    private CountryService countryService;

    @Before
    public void setUp() throws Exception {
        newCountry = DataSeed.generateCountry(1);
        when(countryDao.saveAndFlush(newCountry)).thenReturn(newCountry);
        when(countryDao.findById(1l)).thenReturn(Optional.of(newCountry));
        when(countryDao.findByNameEquals(newCountry.getName())).thenReturn(newCountry);
        doNothing().when(countryDao).delete(any(Country.class));
        doNothing().when(countryDao).flush();

        countries = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            countries.add(DataSeed.generateCountry(i));
        }
        when(countryDao.findAll()).thenReturn(countries);
        countryService = new CountryServiceImpl(countryDao);
    }

    @Test
    public void saveNewCountry() {
        Country country = countryService.save(newCountry);
        assertEquals(newCountry, country);
    }

    @Test
    public void getAllCountries() {
        List<Country> countryList = countryService.getAll();
        assertEquals(5, countryList.size());
    }

    @Test
    public void findById() {
        Optional<Country> country = countryService.findById(1l);
        assertEquals(newCountry, country.get());
    }

    @Test
    public void findByName() {
        Country country = countryService.findByName(newCountry.getName());
        assertEquals(newCountry, country);
    }

    @Test
    public void editCountry() {
        Country editCountry = DataSeed.generateCountry(2);
        Country country = countryService.edit(newCountry, editCountry);
        assertEquals(country.getName(), editCountry.getName());
    }
}

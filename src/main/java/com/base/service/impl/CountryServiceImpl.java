package com.base.service.impl;

import com.base.dao.CountryDao;
import com.base.model.Country;
import com.base.service.CountryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Created by Toyin on 2/28/19.
 * An implementation of com.base.service.CountryService interface
 */
@Service
public class CountryServiceImpl implements CountryService {
    private CountryDao countryDao;

    /**
     * Injects an instance of CountryDao
     * @param countryDao countryDao is a Data Access Object for Country management
     */
    @Autowired
    public CountryServiceImpl(CountryDao countryDao) {
        this.countryDao = countryDao;
    }

    /**
     * Saves a new country row in the countries table
     * @param country country to be saved in the database
     * @return the instance of country saved in the database
     */
    @Override
    public Country save(Country country) {
        return countryDao.saveAndFlush(country);
    }

    /**
     * Returns all countries row stored in the countries table
     * @return a list of countries found in the database
     */
    @Override
    public List<Country> getAll() {
        return countryDao.findAll();
    }

    /**
     * Returns an optional value of country searched by id
     * @param id id of country row to be retrieved from the countries table
     * @return an Optional instance of Country which is fetched by id
     */
    @Override
    public Optional<Country> findById(Long id) {
        return countryDao.findById(id);
    }

    /**
     * Search country by name
     * @param name name of the country to be retrieved from the countries table
     * @return the Country instance with the given name
     */
    @Override
    public Country findByName(String  name) {
        return countryDao.findByNameEquals(name);
    }

    /**
     * Updates an existing Country instance in database and returns the updated form
     * @param old Country instance to be edited
     * @param update Country instance which contains update data
     * @return updated Country instance
     */
    @Override
    public Country edit(Country old, Country update) {
        old.setName(update.getName());
        old.setContinent(update.getContinent());
        return countryDao.saveAndFlush(old);
    }

    /**
     * Delete a country record from database
     * @param id of the Country record to be deleted
     */
    @Override
    public void delete(Long id) {
        countryDao.deleteById(id);
    }
}

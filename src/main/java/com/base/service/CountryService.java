package com.base.service;

import com.base.model.Country;

import java.util.List;
import java.util.Optional;

/**
 * Created by Toyin on 2/28/19.
 */
public interface CountryService {
    Country save(Country country);
    List<Country> getAll();
    Optional<Country> findById(Long id);
    Country findByName(String country);
    Country edit(Country old, Country update);
    void delete(Long id);
}

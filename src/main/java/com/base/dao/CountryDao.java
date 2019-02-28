package com.base.dao;

import com.base.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by Toyin on 2/28/19.
 */
@Repository
public interface CountryDao extends JpaRepository<Country, Long> {
    Country findByNameEquals(String name);
}

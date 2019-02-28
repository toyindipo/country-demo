package com.base;

import com.base.model.Country;
import com.base.model.User;

import java.time.LocalDate;

/**
 * Created by Toyin on 2/28/19.
 */
public class DataSeed {
    public static User generateUser(int count) {
        User user = new User();
        user.setDateOfBirth(LocalDate.now().minusYears(10));
        user.setUsername("user" + count);
        user.setEmail(user.getUsername() + "@mail.com");
        user.setFirstname("Firstname" + count);
        user.setLastname("Lastname" + count);
        user.setPassword("password");
        return user;
    }

    public static Country generateCountry(int count) {
        Country country = new Country();
        country.setName("country" + count);
        country.setContinent("continent");
        return country;
    }
}

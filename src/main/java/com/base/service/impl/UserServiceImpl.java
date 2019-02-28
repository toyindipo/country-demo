package com.base.service.impl;

import com.base.dao.UserDao;
import com.base.model.User;
import com.base.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * Created by Toyin on 2/28/19.
 * An implementation of com.base.service.UserService Interface
 */
@Service
public class UserServiceImpl implements UserService {
    private UserDao userDao;
    private BCryptPasswordEncoder bcryptEncoder;

    /**
     * Inject an instance of UserDao
     * @param userDao userDao is a Data Access Object for User management
     */
    @Autowired
    public UserServiceImpl(UserDao userDao, BCryptPasswordEncoder bcryptEncoder) {
        this.userDao = userDao;
        this.bcryptEncoder = bcryptEncoder;
    }

    /**
     * Save a new record of User in the database
     * @param user user to be stored in the database
     * @return the user record stored in the database
     */
    @Override
    public User save(User user) {
        user.setPassword(bcryptEncoder.encode(user.getPassword()));
        return userDao.saveAndFlush(user);
    }

    /**
     * Retrieve a user record by username or email
     * @param username username or email of user record to be retrieved from database
     * @return found user instance in database
     */
    @Override
    public User findByUsernameOrEmail(String username) {
        return userDao.findByUsernameOrEmail(username);
    }

    /**
     * Retrieve a user record by username or email
     * @param username username of user record to be retrieved from database
     * @param email email of user record to be retrieved from database
     * @return found user instance in database
     */
    @Override
    public User findByUsernameOrEmail(String username, String email) {
        return userDao.findByUsernameEqualsOrEmailEquals(username, email);
    }
}

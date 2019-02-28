package com.base.service;

import com.base.model.User;

/**
 * Created by Toyin on 2/28/19.
 */
public interface UserService {
    User save(User user);
    User findByUsernameOrEmail(String username);
    User findByUsernameOrEmail(String username, String email);
}

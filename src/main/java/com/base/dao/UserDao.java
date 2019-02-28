package com.base.dao;

import com.base.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDao extends JpaRepository<User, Long> {
    @Query("select u from User u where u.email = ?1 or u.username = ?1")
    User findByUsernameOrEmail(String username);

    User findByUsernameEqualsOrEmailEquals(String username, String email);
}

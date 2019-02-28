package com.base.unit;

import com.base.DataSeed;
import com.base.dao.UserDao;
import com.base.model.User;
import com.base.service.UserService;
import com.base.service.impl.UserServiceImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by Toyin on 2/28/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserServiceTest {
    @MockBean
    private UserDao userDao;
    private UserService userService;
    private User newUser;
    private String password;

    @Before
    public void setUp() throws Exception {
        newUser = DataSeed.generateUser(1);
        password = newUser.getPassword();
        when(userDao.saveAndFlush(newUser)).thenReturn(newUser);
        when(userDao.findByUsernameOrEmail(newUser.getUsername())).thenReturn(newUser);
        when(userDao.findByUsernameEqualsOrEmailEquals(anyString(), eq(newUser.getEmail()))).thenReturn(newUser);
        userService = new UserServiceImpl(userDao, new BCryptPasswordEncoder());
    }

    @Test
    public void saveNewUser() {
        User user = userService.save(newUser);
        assertNotEquals(password, user.getPassword());
    }

    @Test
    public void findUserByUsernameOrEmail() {
        User user = userService.findByUsernameOrEmail(newUser.getUsername());
        assertEquals(newUser, user);
    }

    @Test
    public void findUserByBothUsernameAndEmail() {
        User user = userService.findByUsernameOrEmail("nill", newUser.getEmail());
        assertEquals(newUser, user);
    }
}

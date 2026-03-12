package org.example.service;

import org.example.dao.UserDAO;
import org.example.entity.User;

import java.util.List;

public class UserService {

    private final UserDAO userDAO = new UserDAO();

    public User registerUser(String name, String email, String password, String imageUrl) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setHashedPassword(password);
        user.setImageUrl(imageUrl);
        userDAO.save(user);
        return user;
    }

    public User registerUser(String name, String email, String password) {
        return registerUser(name, email, password, null);
    }

    public List<User> getAllUsers() {
        return userDAO.findAll();
    }

    public User findById(int id) {
        return userDAO.findById(id);
    }
}

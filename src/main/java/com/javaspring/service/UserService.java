package com.javaspring.service;

import com.javaspring.entity.User;

import java.util.List;

public interface UserService {

    public User getUserById(long id);

    public User getUserByEmail(String email);

    public List<User> getAllUsers();

    public User addUser(User user);

    public boolean updateUser(User user);

    public boolean deleteUser(long id);
}

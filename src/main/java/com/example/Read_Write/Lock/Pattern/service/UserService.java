package com.example.Read_Write.Lock.Pattern.service;

import com.example.Read_Write.Lock.Pattern.dto.User;
import com.example.Read_Write.Lock.Pattern.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

@Service
public class UserService {
    private final UserMapper userMapper;
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock();

    @Autowired
    public UserService(UserMapper userMapper) {
        this.userMapper = userMapper;
    }

    public User getUserById(int id) {
        readWriteLock.readLock().lock();
        try {
            return userMapper.getUserById(id);
        } finally {
            readWriteLock.readLock().unlock();
        }
    }

    // Assume there's a method to update user
    public int updateUser(User user) {
        readWriteLock.writeLock().lock();
        try {
            // Call the method to update user in the database
            userMapper.updateUser(user);
        } finally {
            readWriteLock.writeLock().unlock();
        }
        return 0;
    }
}

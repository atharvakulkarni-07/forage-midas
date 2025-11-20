package com.jpmc.midascore;

import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DebugHelper {

    @Autowired
    private UserRepository userRepository;

    public void printAllUsers() {
        System.out.println("\n========== ALL USERS ==========");
        Iterable<UserRecord> allUsers = userRepository.findAll();
        for (UserRecord user : allUsers) {
            System.out.println("ID: " + user.getId() + ", Name: " + user.getName() + ", Balance: " + user.getBalance());
        }
        System.out.println("================================\n");
    }

    public UserRecord findUserByName(String name) {
        Iterable<UserRecord> allUsers = userRepository.findAll();
        for (UserRecord user : allUsers) {
            if (user.getName().equals(name)) {
                return user;
            }
        }
        return null;
    }
}

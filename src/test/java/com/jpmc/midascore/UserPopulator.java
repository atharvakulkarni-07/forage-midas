package com.jpmc.midascore;

import com.jpmc.midascore.component.DatabaseConduit;
import com.jpmc.midascore.entity.UserRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserPopulator {
    @Autowired
    private FileLoader fileLoader;

    @Autowired
    private DatabaseConduit databaseConduit;

    public void populate() {
        String[] userLines = fileLoader.loadStrings("/test_data/lkjhgfdsa.hjkl");
        long userId = 1; // Start ID from 1

        for (String userLine : userLines) {
            String[] userData = userLine.split(", ");
            String name = userData[0];
            float balance = Float.parseFloat(userData[1]);

            UserRecord user = new UserRecord(userId, name, balance);
            databaseConduit.save(user);
            userId++;
        }
    }
}

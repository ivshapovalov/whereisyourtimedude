package ru.ivan.whereisyourtimedude.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import java.util.List;

import ru.ivan.whereisyourtimedude.database.entities.User;
import ru.ivan.whereisyourtimedude.database.manager.SQLiteDatabaseManager;

import static ru.ivan.whereisyourtimedude.common.Session.sessionCurrentUser;

public abstract class AbstractActivity extends AppCompatActivity {

    protected final SQLiteDatabaseManager DB = new SQLiteDatabaseManager(this);

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        defineCurrentUser();
    }

    public void defineCurrentUser() {

        if (sessionCurrentUser == null) {
            List<User> userList = DB.getAllUsers();
            if (userList.size() == 1) {
                User currentUser = userList.get(0);
                sessionCurrentUser = currentUser;
                currentUser.setIsCurrentUser(1);
                currentUser.dbSave(DB);
            } else {
                for (User user : userList
                        ) {
                    if (user.isCurrentUser() == 1) {
                        sessionCurrentUser = user;
                        break;
                    }
                }
                isUserDefined();
            }
        }
    }

    public boolean isUserDefined() {
        if (sessionCurrentUser == null) {
            Toast toast = Toast.makeText(this,
                    "No active user. Create the user and make it active!", Toast.LENGTH_SHORT);
            toast.show();
            return false;
        }
        return true;
    }
}

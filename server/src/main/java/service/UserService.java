package service;

import dataAccess.AuthDAO;
import dataAccess.DataAccessException;
import dataAccess.UserDAO;
import model.AuthData;
import model.UserData;

import java.util.UUID;

public class UserService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public AuthData register(String username, String password, String email) throws DataAccessException{
        if(username == null || password == null || email == null){
            throw new DataAccessException("Cannot have an empty register values");
        }
        if(userDAO.getUser(username) != null){
            throw new DataAccessException("User already exists");
        }

        UserData newUser = new UserData(username, password, email);
        userDAO.createUser(newUser);

        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, username);
        authDAO.createAuth(newAuth);
        return newAuth;
    }
}

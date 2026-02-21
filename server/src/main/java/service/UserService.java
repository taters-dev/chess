package service;

import dataAccess.*;
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

    public AuthData register(String username, String password, String email) throws BadRequestException, AlreadyTakenException, DataAccessException{
        if(username == null || password == null || email == null){
            throw new BadRequestException("Cannot have an empty register values");
        }
        if(userDAO.getUser(username) != null){
            throw new AlreadyTakenException("User already exists");
        }

        UserData newUser = new UserData(username, password, email);
        userDAO.createUser(newUser);

        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, username);
        authDAO.createAuth(newAuth);
        return newAuth;
    }

    public AuthData login(String username, String password) throws BadRequestException, UnauthorizedException, DataAccessException{
        if(username == null | password == null){
            throw new BadRequestException("Cannot have an empty login value");
        }
        if(userDAO.getUser(username) == null){
            throw new UnauthorizedException("User does not exist");
        }
        if(!userDAO.getUser(username).password().equals(password)){
            throw new UnauthorizedException("Incorrect Password");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData newAuth = new AuthData(authToken, username);
        authDAO.createAuth(newAuth);
        return newAuth;
    }
}

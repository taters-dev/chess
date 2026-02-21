package service;

import dataAccess.*;
import model.AuthData;
import model.UserData;
import requestAndResult.*;

import java.util.UUID;

public class UserService {
    private UserDAO userDAO;
    private AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO){
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws BadRequestException, AlreadyTakenException, DataAccessException{
        String username = registerRequest.username();
        String password = registerRequest.password();
        String email = registerRequest.email();
        if(username == null || password == null || email == null){
            throw new BadRequestException("Cannot have an empty register values");
        }
        if(userDAO.getUser(username) != null){
            throw new AlreadyTakenException("User already exists");
        }

        UserData newUser = new UserData(username, password, email);
        userDAO.createUser(newUser);

        String authToken = UUID.randomUUID().toString();

        return new RegisterResult(username, authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, UnauthorizedException, DataAccessException{

        String username = loginRequest.username();
        String password = loginRequest.password();

        if(username == null || password == null){
            throw new BadRequestException("Cannot have an empty login value");
        }
        if(userDAO.getUser(username) == null){
            throw new UnauthorizedException("User does not exist");
        }
        if(!userDAO.getUser(username).password().equals(password)){
            throw new UnauthorizedException("Incorrect Password");
        }

        String authToken = UUID.randomUUID().toString();

        return new LoginResult(username, authToken);
    }

    public void logout(LogoutRequest logoutRequest) throws UnauthorizedException, DataAccessException{
        String authToken = logoutRequest.authToken();

        if(authDAO.getAuth(authToken) == null){
            throw new UnauthorizedException("Unauthorized");
        }

        authDAO.deleteAuth(authToken);
    }
}

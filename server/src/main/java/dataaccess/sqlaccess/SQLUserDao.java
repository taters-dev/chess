package dataaccess.sqlaccess;

import dataaccess.DataAccessException;
import dataaccess.DatabaseManager;
import dataaccess.UserDAO;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class SQLUserDao implements UserDAO {
    @Override
    public void createUser(UserData userData) throws DataAccessException {
        var statement = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        String hashedPassword = BCrypt.hashpw(userData.password(), BCrypt.gensalt());
        ExecuteUpdate.executeUpdate(statement, userData.username(), hashedPassword, userData.email());

    }

    @Override
    public UserData getUser(String username) throws DataAccessException{
        try (Connection conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username, password, email FROM users WHERE username=?";
            try (PreparedStatement ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (ResultSet rs = ps.executeQuery()) {
                    if (rs.next()) {
                       return new UserData(rs.getString("username"), rs.getString("password"), rs.getString("email"));
                    }
                }
            }
        } catch (Exception e) {
           throw new DataAccessException(e.getMessage(), e);
        }
        return null;
    }

    @Override
    public void clear() throws DataAccessException {
        try {
            var statement = "TRUNCATE users";
            ExecuteUpdate.executeUpdate(statement);
        } catch (DataAccessException e){
            throw new DataAccessException(e.getMessage(), e);
        }

    }


}

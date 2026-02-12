package org.example.dao;

import org.example.entity.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import org.example.util.DatabaseCfg;
import org.example.util.DatabaseInit;


public class UserDAO {

  private static UserDAO instance;


  public UserDAO() {
    // Припускаємо, що цей метод створює таблицю, якщо вона не існує
    DatabaseInit.Init();
  }

  public static synchronized UserDAO getInstance() {
    if (instance == null) {
      instance = new UserDAO();
    }
    return instance;
  }


  private User extractUserFromResultSet(ResultSet rs) throws SQLException {
    Integer id = rs.getInt("id");
    String name = rs.getString("name");
    String email = rs.getString("email");
    String hashedPassword = rs.getString("hashedPassword");
    // Handle potential missing column if migration failed or legacy issues, though Init() should handle it.
    String imageUrl = null;
    try {
        imageUrl = rs.getString("imageUrl");
    } catch (SQLException e) {
        // Column might not exist in result set if SELECT didn't request it or table structure mismatch
        // ignore safely
    }
    return User.createFromDb(id, name, email, hashedPassword, imageUrl);
  }


  public boolean create(User user) {
    String sql = "INSERT INTO users (name, email, hashedPassword, imageUrl) VALUES (?, ?, ?, ?)";
    try (Connection conn = DatabaseCfg.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

      ps.setString(1, user.getName());
      ps.setString(2, user.getEmail());
      ps.setString(3, user.getHashedPassword());
      ps.setString(4, user.getImageUrl());

      int affectedRows = ps.executeUpdate();

      if (affectedRows > 0) {
        try (ResultSet rs = ps.getGeneratedKeys()) {
          if (rs.next()) {
            user.setId(rs.getInt(1));
          }
        }
        return true;
      }
      return false;

    } catch (SQLException e) {
      throw new RuntimeException(e);
    }
  }


  public User findById(int id) throws SQLException {
    String sql = "SELECT id, name, email, hashedPassword, imageUrl FROM users WHERE id = ?";
    try (Connection conn = DatabaseCfg.getConnection();
        PreparedStatement ps = conn.prepareStatement(sql)) {

      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
          return extractUserFromResultSet(rs);
        }


      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }
    return null;
  }

    public User findByEmail (String email){
      String sql = "SELECT id, name, email, hashedPassword, imageUrl FROM users WHERE email = ?";
      try (Connection conn = DatabaseCfg.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, email);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            return extractUserFromResultSet(rs);
          }
        }
        return null;

      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }

    /**
     * Отримує всіх користувачів з бази даних.
     */
    public List<User> findAll () {
      List<User> users = new ArrayList<>();
      String sqlQuery = "SELECT id, name, email, hashedPassword, imageUrl FROM users";
      try (Connection conn = DatabaseCfg.getConnection();
          Statement stmt = conn.createStatement();
          ResultSet rs = stmt.executeQuery(sqlQuery)) {

        while (rs.next()) {
          users.add(extractUserFromResultSet(rs));
        }

      } catch (SQLException e) {
        throw new RuntimeException(e);
      }

      return users;
    }
    
    /**
     * Шукає користувачів за частковим співпадінням імені або email.
     * @param query пошуковий запит
     * @return список знайдених користувачів
     */
    public List<User> searchUsers(String query) {
        List<User> users = new ArrayList<>();
        String sql = "SELECT id, name, email, hashedPassword, imageUrl FROM users WHERE LOWER(name) LIKE ? OR LOWER(email) LIKE ?";
        try (Connection conn = DatabaseCfg.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            String pattern = "%" + query.toLowerCase() + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    users.add(extractUserFromResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return users;
    }

    /**
     * Оновлює дані користувача у базі даних.
     *
     * @param user користувач з оновленими даними
     * @return true, якщо користувач успішно оновлений, false - інакше
     */
    public boolean update (User user){
      String sql = "UPDATE users SET name = ?, email = ?, hashedPassword = ?, imageUrl = ? WHERE id = ?";
      try (Connection conn = DatabaseCfg.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, user.getName());
        ps.setString(2, user.getEmail());
        ps.setString(3, user.getHashedPassword());
        ps.setString(4, user.getImageUrl());
        ps.setInt(5, user.getId());

        return ps.executeUpdate() > 0;

      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }

    /**
     * Видаляє користувача за ідентифікатором.
     *
     * @param id ідентифікатор користувача для видалення
     * @return true, якщо користувач успішно видалений, false - інакше
     */
    public boolean delete ( int id){
      String sql = "DELETE FROM users WHERE id = ?";
      try (Connection conn = DatabaseCfg.getConnection();
          PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);

        return ps.executeUpdate() > 0;
      } catch (SQLException e) {
        throw new RuntimeException(e);
      }
    }

  }

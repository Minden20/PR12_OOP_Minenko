package org.example.dao;

import org.example.entity.User;
import org.example.util.DatabaseCfg;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * DAO для роботи з користувачами через Spring JDBC Template.
 */
public class UserDAO {

    private final JdbcTemplate jdbcTemplate = DatabaseCfg.getJdbcTemplate();

    private final RowMapper<User> rowMapper = (rs, rowNum) -> {
        User user = new User();
        user.setId(rs.getInt("id"));
        user.setName(rs.getString("name"));
        user.setEmail(rs.getString("email"));
        user.setHashedPassword(rs.getString("hashedPassword"));
        user.setImageUrl(rs.getString("imageUrl"));
        return user;
    };

    public User findById(int id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        List<User> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<User> findAll() {
        String sql = "SELECT * FROM users";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void save(User user) {
        String sql = "INSERT INTO users (name, email, hashedPassword, imageUrl) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getHashedPassword());
            ps.setString(4, user.getImageUrl());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            user.setId(keyHolder.getKey().intValue());
        }
    }

    public void update(User user) {
        String sql = "UPDATE users SET name = ?, email = ?, hashedPassword = ?, imageUrl = ? WHERE id = ?";
        jdbcTemplate.update(sql, user.getName(), user.getEmail(),
                user.getHashedPassword(), user.getImageUrl(), user.getId());
    }

    public void delete(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

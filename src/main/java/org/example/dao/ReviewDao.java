package org.example.dao;

import org.example.entity.Review;
import org.example.util.DatabaseCfg;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * DAO для роботи з відгуками через Spring JDBC Template.
 */
public class ReviewDao {

    private final JdbcTemplate jdbcTemplate = DatabaseCfg.getJdbcTemplate();

    private final RowMapper<Review> rowMapper = (rs, rowNum) -> {
        Review review = new Review();
        review.setId(rs.getInt("id"));
        review.setUserId(rs.getInt("userID"));
        review.setServiceId(rs.getInt("serviceID"));
        review.setText(rs.getString("text"));
        review.setRating(rs.getInt("rating"));
        return review;
    };

    public Review findById(int id) {
        String sql = "SELECT * FROM reviews WHERE id = ?";
        List<Review> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Review> findAll() {
        String sql = "SELECT * FROM reviews";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void save(Review review) {
        String sql = "INSERT INTO reviews (userID, serviceID, text, rating) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, review.getUserId());
            ps.setInt(2, review.getServiceId());
            ps.setString(3, review.getText());
            ps.setInt(4, review.getRating());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            review.setId(keyHolder.getKey().intValue());
        }
    }

    public void update(Review review) {
        String sql = "UPDATE reviews SET userID = ?, serviceID = ?, text = ?, rating = ? WHERE id = ?";
        jdbcTemplate.update(sql, review.getUserId(), review.getServiceId(),
                review.getText(), review.getRating(), review.getId());
    }

    public void delete(int id) {
        String sql = "DELETE FROM reviews WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

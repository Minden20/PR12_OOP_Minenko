package org.example.dao;

import org.example.entity.Service;
import org.example.util.DatabaseCfg;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * DAO для роботи з послугами через Spring JDBC Template.
 */
public class ServiceDao {

    private final JdbcTemplate jdbcTemplate = DatabaseCfg.getJdbcTemplate();

    private final RowMapper<Service> rowMapper = (rs, rowNum) -> {
        Service service = new Service();
        service.setId(rs.getInt("id"));
        service.setName(rs.getString("name"));
        service.setDescription(rs.getString("description"));
        return service;
    };

    public Service findById(int id) {
        String sql = "SELECT * FROM services WHERE id = ?";
        List<Service> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Service> findAll() {
        String sql = "SELECT * FROM services";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void save(Service service) {
        String sql = "INSERT INTO services (name, description) VALUES (?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, service.getName());
            ps.setString(2, service.getDescription());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            service.setId(keyHolder.getKey().intValue());
        }
    }

    public void update(Service service) {
        String sql = "UPDATE services SET name = ?, description = ? WHERE id = ?";
        jdbcTemplate.update(sql, service.getName(), service.getDescription(), service.getId());
    }

    public void delete(int id) {
        String sql = "DELETE FROM services WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

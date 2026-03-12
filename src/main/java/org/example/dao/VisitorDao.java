package org.example.dao;

import org.example.entity.Visitor;
import org.example.util.DatabaseCfg;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

/**
 * DAO для роботи з відвідувачами через Spring JDBC Template.
 */
public class VisitorDao {

    private final JdbcTemplate jdbcTemplate = DatabaseCfg.getJdbcTemplate();

    private final RowMapper<Visitor> rowMapper = (rs, rowNum) -> {
        Visitor visitor = new Visitor();
        visitor.setId(rs.getInt("id"));
        visitor.setName(rs.getString("name"));
        visitor.setPhone(rs.getString("phone"));
        visitor.setVisitDate(rs.getString("visitDate"));
        visitor.setServiceId(rs.getInt("serviceId"));
        return visitor;
    };

    public Visitor findById(int id) {
        String sql = "SELECT * FROM visitors WHERE id = ?";
        List<Visitor> results = jdbcTemplate.query(sql, rowMapper, id);
        return results.isEmpty() ? null : results.get(0);
    }

    public List<Visitor> findAll() {
        String sql = "SELECT * FROM visitors";
        return jdbcTemplate.query(sql, rowMapper);
    }

    public void save(Visitor visitor) {
        String sql = "INSERT INTO visitors (name, phone, visitDate, serviceId) VALUES (?, ?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, visitor.getName());
            ps.setString(2, visitor.getPhone());
            ps.setString(3, visitor.getVisitDate());
            ps.setInt(4, visitor.getServiceId());
            return ps;
        }, keyHolder);
        if (keyHolder.getKey() != null) {
            visitor.setId(keyHolder.getKey().intValue());
        }
    }

    public void update(Visitor visitor) {
        String sql = "UPDATE visitors SET name = ?, phone = ?, visitDate = ?, serviceId = ? WHERE id = ?";
        jdbcTemplate.update(sql, visitor.getName(), visitor.getPhone(),
                visitor.getVisitDate(), visitor.getServiceId(), visitor.getId());
    }

    public void delete(int id) {
        String sql = "DELETE FROM visitors WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
}

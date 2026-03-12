package org.example.util;

import org.h2.jdbcx.JdbcDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;

public class DatabaseCfg {

    private static final String URL = "jdbc:h2:~/testdb";
    private static final String USER = "sa";
    private static final String PASSWORD = "";

    private static DataSource dataSource;
    private static JdbcTemplate jdbcTemplate;

    public static DataSource getDataSource() {
        if (dataSource == null) {
            JdbcDataSource ds = new JdbcDataSource();
            ds.setURL(URL);
            ds.setUser(USER);
            ds.setPassword(PASSWORD);
            dataSource = ds;
        }
        return dataSource;
    }

    public static JdbcTemplate getJdbcTemplate() {
        if (jdbcTemplate == null) {
            jdbcTemplate = new JdbcTemplate(getDataSource());
        }
        return jdbcTemplate;
    }

    public static java.sql.Connection getConnection() throws java.sql.SQLException {
        return getDataSource().getConnection();
    }
}

package com.khelacademy.dao;

import javax.ws.rs.core.Response;
import java.sql.SQLException;

public interface HomeDao {
    Response getHome() throws SQLException;
}

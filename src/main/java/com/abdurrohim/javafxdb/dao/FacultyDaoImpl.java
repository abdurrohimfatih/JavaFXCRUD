package com.abdurrohim.javafxdb.dao;

import com.abdurrohim.javafxdb.entities.Faculty;
import com.abdurrohim.javafxdb.utils.DaoService;
import com.abdurrohim.javafxdb.utils.MySQLConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FacultyDaoImpl implements DaoService<Faculty> {

    @Override
    public List<Faculty> fetchAll() throws SQLException, ClassNotFoundException {
        List<Faculty> faculties = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT id, name FROM faculty ORDER BY id ASC";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Faculty faculty = new Faculty();
                        faculty.setId(rs.getInt("id"));
                        faculty.setName(rs.getString("name"));
                        faculties.add(faculty);
                    }
                }
            }
        }

        return faculties;
    }

    @Override
    public int addData(Faculty object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String resetId = "ALTER TABLE faculty AUTO_INCREMENT = 1";
            try (PreparedStatement ps = connection.prepareStatement(resetId)) {
                ps.execute();
            }

            String query = "INSERT INTO faculty(name) VALUES(?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getName());

                if (ps.executeUpdate() != 0) {
                    connection.commit();
                    result = 1;
                } else {
                    connection.rollback();
                }
            }
        }

        return result;
    }

    @Override
    public int updateData(Faculty object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "UPDATE faculty SET name = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getName());
                ps.setInt(2, object.getId());

                if (ps.executeUpdate() != 0) {
                    connection.commit();
                    result = 1;
                } else {
                    connection.rollback();
                }
            }
        }

        return result;
    }

    @Override
    public int deleteData(Faculty object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "DELETE FROM faculty WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, object.getId());

                if (ps.executeUpdate() != 0) {
                    String deleteFK = "ALTER TABLE department DROP FOREIGN KEY department_ibfk_1";
                    try (PreparedStatement ps1 = connection.prepareStatement(deleteFK)) {
                        ps1.executeUpdate();
                    }
                    String deleteId = "ALTER TABLE faculty DROP id";
                    try (PreparedStatement ps2 = connection.prepareStatement(deleteId)) {
                        ps2.executeUpdate();
                    }
                    String addId = "ALTER TABLE faculty ADD id INT NOT NULL AUTO_INCREMENT FIRST, ADD PRIMARY KEY(id)";
                    try (PreparedStatement ps3 = connection.prepareStatement(addId)) {
                        ps3.executeUpdate();
                    }
                    String addFK = "ALTER TABLE department ADD FOREIGN KEY (faculty_id) REFERENCES faculty (id) ON DELETE CASCADE ON UPDATE CASCADE";
                    try (PreparedStatement ps4 = connection.prepareStatement(addFK)) {
                        ps4.executeUpdate();
                    }

                    connection.commit();
                    result = 1;
                } else {
                    connection.rollback();
                }

            }
        }

        return result;
    }
}

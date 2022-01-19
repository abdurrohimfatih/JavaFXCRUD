package com.abdurrohim.javafxdb.dao;

import com.abdurrohim.javafxdb.entities.Department;
import com.abdurrohim.javafxdb.entities.Faculty;
import com.abdurrohim.javafxdb.utils.DaoService;
import com.abdurrohim.javafxdb.utils.MySQLConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DepartmentDaoImpl implements DaoService<Department> {

    @Override
    public List<Department> fetchAll() throws SQLException, ClassNotFoundException {
        List<Department> departments = new ArrayList<>();
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "SELECT d.id, d.name, d.faculty_id, f.name AS faculty_name FROM department d JOIN faculty f ON d.faculty_id = f.id ORDER BY id ASC";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        Faculty faculty = new Faculty();
                        faculty.setId(rs.getInt("faculty_id"));
                        faculty.setName(rs.getString("faculty_name"));

                        Department department = new Department();
                        department.setId(rs.getInt("id"));
                        department.setName(rs.getString("name"));
                        department.setFaculty(faculty);
                        departments.add(department);
                    }
                }
            }
        }

        return departments;
    }

    @Override
    public int addData(Department object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String resetId = "ALTER TABLE department AUTO_INCREMENT = 1";
            try (PreparedStatement ps = connection.prepareStatement(resetId)) {
                ps.execute();
            }

            String query = "INSERT INTO department(name, faculty_id) VALUES(?, ?)";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getName());
                ps.setInt(2, object.getFaculty().getId());

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
    public int updateData(Department object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "UPDATE department SET name = ?, faculty_id = ? WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setString(1, object.getName());
                ps.setInt(2, object.getFaculty().getId());
                ps.setInt(3, object.getId());

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
    public int deleteData(Department object) throws SQLException, ClassNotFoundException {
        int result = 0;
        try (Connection connection = MySQLConnection.createConnection()) {
            String query = "DELETE FROM department WHERE id = ?";
            try (PreparedStatement ps = connection.prepareStatement(query)) {
                ps.setInt(1, object.getId());

                if (ps.executeUpdate() != 0) {
                    String deleteId = "ALTER TABLE department DROP id";
                    try (PreparedStatement ps1 = connection.prepareStatement(deleteId)) {
                        ps1.executeUpdate();
                    }
                    String addId = "ALTER TABLE department ADD id INT NOT NULL AUTO_INCREMENT FIRST, ADD PRIMARY KEY(id)";
                    try (PreparedStatement ps2 = connection.prepareStatement(addId)) {
                        ps2.executeUpdate();
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

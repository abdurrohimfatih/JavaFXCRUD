package com.abdurrohim.javafxdb.controllers;

import com.abdurrohim.javafxdb.dao.DepartmentDaoImpl;
import com.abdurrohim.javafxdb.dao.FacultyDaoImpl;
import com.abdurrohim.javafxdb.entities.Department;
import com.abdurrohim.javafxdb.entities.Faculty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private Button btnResetFaculty;
    @FXML
    private Button btnResetDepartment;
    @FXML
    private Button btnSaveFaculty;
    @FXML
    private Button btnUpdateFaculty;
    @FXML
    private Button btnDeleteFaculty;
    @FXML
    private Button btnSaveDepartment;
    @FXML
    private Button btnUpdateDepartment;
    @FXML
    private Button btnDeleteDepartment;
    @FXML
    private TextField txtFacultyName;
    @FXML
    private TableView<Faculty> tableFaculty;
    @FXML
    private TableColumn<Faculty, Integer> facultyCol1;
    @FXML
    private TableColumn<Faculty, String> facultyCol2;
    @FXML
    private TextField txtDepartmentName;
    @FXML
    private ComboBox<Faculty> comboFaculty;
    @FXML
    private TableView<Department> tableDepartment;
    @FXML
    private TableColumn<Department, Integer> departmentCol1;
    @FXML
    private TableColumn<Department, String> departmentCol2;
    @FXML
    private TableColumn<Department, Faculty> departmentCol3;
    private ObservableList<Department> departments;
    private ObservableList<Faculty> faculties;
    private DepartmentDaoImpl departmentDao;
    private FacultyDaoImpl facultyDao;
    private Faculty selectedFaculty;
    private Department selectedDepartment;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        facultyDao = new FacultyDaoImpl();
        departmentDao = new DepartmentDaoImpl();
        faculties = FXCollections.observableArrayList();
        departments = FXCollections.observableArrayList();

        try {
            faculties.addAll(facultyDao.fetchAll());
            departments.addAll(departmentDao.fetchAll());
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        comboFaculty.setItems(faculties);
        tableFaculty.setItems(faculties);
        facultyCol1.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        facultyCol2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        tableDepartment.setItems(departments);
        departmentCol1.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getId()).asObject());
        departmentCol2.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        departmentCol3.setCellValueFactory(data -> new SimpleObjectProperty<>(data.getValue().getFaculty()));
    }

    @FXML
    private void saveFacultyAction(ActionEvent event) {
        if (txtFacultyName.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please fill faculty name");
            alert.showAndWait();
        } else {
            Faculty faculty = new Faculty();
            faculty.setName(txtFacultyName.getText().trim());

            try {
                if (facultyDao.addData(faculty) == 1) {
                    faculties.clear();
                    faculties.addAll(facultyDao.fetchAll());
                    resetFacultyAction();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void saveDepartmentAction(ActionEvent event) {
        if (txtDepartmentName.getText().trim().isEmpty() || comboFaculty.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please fill department name and faculty");
            alert.showAndWait();
        } else {
            Department department = new Department();
            department.setName(txtDepartmentName.getText().trim());
            department.setFaculty(comboFaculty.getValue());

            try {
                if (departmentDao.addData(department) == 1) {
                    departments.clear();
                    departments.addAll(departmentDao.fetchAll());
                    resetDepartmentAction();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void tableFacultyClicked(MouseEvent mouseEvent) {
        selectedFaculty = tableFaculty.getSelectionModel().getSelectedItem();
        if (selectedFaculty != null) {
            txtFacultyName.setText(selectedFaculty.getName());
            btnSaveFaculty.setDisable(true);
            btnUpdateFaculty.setDisable(false);
            btnDeleteFaculty.setDisable(false);
        }
    }

    @FXML
    private void tableDepartmentClicked(MouseEvent mouseEvent) {
        selectedDepartment = tableDepartment.getSelectionModel().getSelectedItem();
        if (selectedDepartment != null) {
            txtDepartmentName.setText(selectedDepartment.getName());
            comboFaculty.setValue(selectedDepartment.getFaculty());
            btnSaveDepartment.setDisable(true);
            btnUpdateDepartment.setDisable(false);
            btnDeleteDepartment.setDisable(false);
        }
    }

    @FXML
    private void updateFacultyAction(ActionEvent event) {
        if (txtFacultyName.getText().trim().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please fill faculty name");
            alert.showAndWait();
        } else {
            selectedFaculty.setName(txtFacultyName.getText().trim());

            try {
                if (facultyDao.updateData(selectedFaculty) == 1) {
                    faculties.clear();
                    faculties.addAll(facultyDao.fetchAll());
                    departments.clear();
                    departments.addAll(departmentDao.fetchAll());
                    resetFacultyAction();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteFacultyAction(ActionEvent event) throws SQLException, ClassNotFoundException {
        departments.clear();
        departments.addAll(departmentDao.fetchAll());
        deleteObject(selectedFaculty);
    }

    @FXML
    private void updateDepartmentAction(ActionEvent event) {
        if (txtDepartmentName.getText().trim().isEmpty() || comboFaculty.getValue() == null) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please fill department name and faculty");
            alert.showAndWait();
        } else {
            selectedDepartment.setName(txtDepartmentName.getText().trim());
            selectedDepartment.setFaculty(comboFaculty.getValue());

            try {
                if (departmentDao.updateData(selectedDepartment) == 1) {
                    departments.clear();
                    departments.addAll(departmentDao.fetchAll());
                    resetDepartmentAction();
                }
            } catch (SQLException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    private void deleteDepartmentAction(ActionEvent event) {
        deleteObject(selectedDepartment);
    }

    @FXML
    private void resetFacultyAction() {
        txtFacultyName.clear();
        selectedFaculty = null;
        btnSaveFaculty.setDisable(false);
        btnUpdateFaculty.setDisable(true);
        btnDeleteFaculty.setDisable(true);
    }

    @FXML
    private void resetDepartmentAction() {
        txtDepartmentName.clear();
        comboFaculty.setValue(null);
        selectedDepartment = null;
        btnSaveDepartment.setDisable(false);
        btnUpdateDepartment.setDisable(true);
        btnDeleteDepartment.setDisable(true);
    }

    private void deleteObject(Object object) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setContentText("Are you sure want to delete?");
        alert.showAndWait();
        if (alert.getResult() == ButtonType.OK) {
            if (object instanceof Faculty) {
                try {
                    if (facultyDao.deleteData(selectedFaculty) == 1) {
                        faculties.clear();
                        faculties.addAll(facultyDao.fetchAll());
                        resetFacultyAction();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            } else if (object instanceof Department) {
                try {
                    if (departmentDao.deleteData(selectedDepartment) == 1) {
                        departments.clear();
                        departments.addAll(departmentDao.fetchAll());
                        resetDepartmentAction();
                    }
                } catch (SQLException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
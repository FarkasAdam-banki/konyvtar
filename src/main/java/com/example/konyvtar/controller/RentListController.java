package com.example.konyvtar.controller;

import com.example.konyvtar.DatabaseConnection;
import com.example.konyvtar.model.Rent;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RentListController {
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private ComboBox<String> month;
    @FXML
    private Label resultCountLabel;
    @FXML
    private TableView<Rent> rentsTableView;
    @FXML
    private TableColumn<Rent, String> membershipIdColumn;
    @FXML
    private TableColumn<Rent, String> serialNumberColumn;
    @FXML
    private Button saveButton;
    @FXML
    private TableColumn<Rent, String> borrowDateColumn;
    @FXML
    private TableColumn<Rent, String> dueDateColumn;
    @FXML
    private TableColumn<Rent, Boolean> returnedColumn;

    private  ObservableList<Rent> rents;
    private Connection conn;

    public void initialize() {
        month.setItems(FXCollections.observableArrayList("Az elmúlt harminc napban?", "Igen"));
        month.setValue("Az elmúlt harminc napban?");
        this.conn = DatabaseConnection.getConnection();

        membershipIdColumn.setCellValueFactory(new PropertyValueFactory<>("membershipId"));
        serialNumberColumn.setCellValueFactory(new PropertyValueFactory<>("serialNumber"));
        borrowDateColumn.setCellValueFactory(new PropertyValueFactory<>("borrowDate"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        returnedColumn.setCellValueFactory(cellData -> cellData.getValue().returnedProperty());
        returnedColumn.setCellFactory(CheckBoxTableCell.forTableColumn(returnedColumn));
        rentsTableView.setEditable(true);

        searchField.setOnAction(event -> fetchRents());
        searchButton.setOnMouseClicked(event -> fetchRents());
        month.setOnAction(event -> fetchRents());
        saveButton.setOnMouseClicked(event -> save());
        fetchRents();
    }
    private void fetchRents() {
        String search = searchField.getText();
        rents = FXCollections.observableArrayList();
        ObservableList<Rent> resultRents = FXCollections.observableArrayList();
        String sql;
        try {
            PreparedStatement pstmt;
             sql = """ 
                     SELECT * FROM kolcsonzes
                     WHERE (tag_id LIKE ?
                     OR leltar_leltariszam LIKE ?
                     OR kolcsonzes_datum LIKE ?
                     OR kolcsonzes_hatar LIKE ?)
                     AND (? = 'Igen' AND DATEDIFF(CURRENT_DATE, kolcsonzes_datum) < 30 OR ? != 'Igen')""";
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + search.trim() + "%");
            pstmt.setString(2, "%" + search.trim() + "%");
            pstmt.setString(3, "%" + search.trim() + "%");
            pstmt.setString(4, "%" + search.trim() + "%");
            pstmt.setString(5, month.getSelectionModel().getSelectedItem());
            pstmt.setString(6, month.getSelectionModel().getSelectedItem());

            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                int rentId = rs.getInt("kolcsonzes_id");
                String serialNumber = rs.getString("leltar_leltariszam");
                String membershipId = rs.getString("tag_id");
                String borrowDate = rs.getString("kolcsonzes_datum");
                String dueDate = rs.getString("kolcsonzes_hatar");
                boolean returned = rs.getBoolean("kolcsonzes_visszaE");
                rents.add(new Rent(rentId, membershipId, serialNumber, borrowDate, dueDate, returned));
            }
            rentsTableView.setItems(rents);

            resultCountLabel.setText("Találatok száma: " + rents.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void save() {
        try {
            String sql = "UPDATE kolcsonzes SET kolcsonzes_visszaE = ? WHERE kolcsonzes_id = ?";
            PreparedStatement pstmt = conn.prepareStatement(sql);

            for (Rent rent : rents) {
                pstmt.setBoolean(1,  rent.isReturned());
                pstmt.setInt(2, rent.getRentId());
                pstmt.executeUpdate();
            }

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText("Adatok mentése");
            alert.setContentText("Sikeres mentés!");
            alert.show();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}

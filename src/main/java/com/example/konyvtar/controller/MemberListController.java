package com.example.konyvtar.controller;

import com.example.konyvtar.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;

public class MemberListController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> delayFilter;

    @FXML
    private ListView<String> listView;

    @FXML
    private Button searchButton;

    @FXML
    private Label resultCountLabel;

    private Connection conn;

    public void initialize() {
        delayFilter.setItems(FXCollections.observableArrayList("Összes", "Igen", "Nem"));
        delayFilter.setValue("Összes");

        this.conn = DatabaseConnection.getConnection();
        loadMembers(null, null);

        searchField.setOnAction(event -> searchMembers());
        delayFilter.setOnAction(event -> searchMembers());
        searchButton.setOnMouseClicked(event -> searchMembers());
    }


    private void loadMembers(String searchQuery, String delayFilter) {
        ObservableList<String> members = FXCollections.observableArrayList();

        String sql = "SELECT t.tag_id AS tagsagi_azonosito, t.tag_nev AS nev, c.cim_utca AS cim, t.tag_tel AS telefonszam, " +
                "(SELECT COUNT(*) FROM kolcsonzes k WHERE k.tag_id = t.tag_id) AS kolcsonzott_konyvek_szama, " +
                "(CASE WHEN EXISTS (SELECT 1 FROM kolcsonzes k WHERE k.tag_id = t.tag_id AND k.kolcsonzes_hatar < CURDATE()) THEN 1 ELSE 0 END) AS aktiv_keses " +
                "FROM tag t " +
                "LEFT JOIN cim c ON t.cim_id = c.cim_id " +
                "WHERE 1=1";
        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql += " AND (t.tag_id LIKE ? OR t.tag_nev LIKE ?)";
        }

        if ("Igen".equals(delayFilter)) {
            sql += " AND (SELECT COUNT(*) FROM kolcsonzes k WHERE k.tag_id = t.tag_id AND k.kolcsonzes_hatar < CURDATE()) > 0";
        } else if ("Nem".equals(delayFilter)) {
            sql += " AND NOT EXISTS (SELECT 1 FROM kolcsonzes k WHERE k.tag_id = t.tag_id AND k.kolcsonzes_hatar < CURDATE())";
        }

        try (PreparedStatement preparedStatement = conn.prepareStatement(sql)) {
            int paramIndex = 1;

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                preparedStatement.setString(paramIndex++, "%" + searchQuery + "%");
                preparedStatement.setString(paramIndex++, "%" + searchQuery + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String member = String.format("Tagsági azonositó: %s | Név: %s | Cím: %s | Telefonszám: %s | Kölcsönzött könyvek: %d | Késedelem: %d",
                        resultSet.getString("tagsagi_azonosito"),
                        resultSet.getString("nev"),
                        resultSet.getString("cim"),
                        resultSet.getString("telefonszam"),
                        resultSet.getInt("kolcsonzott_konyvek_szama"),
                        resultSet.getInt("aktiv_keses"));
                members.add(member);
            }

            listView.setItems(members);
            resultCountLabel.setText("Találatok száma: " + members.size());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchMembers() {
        String searchQuery = searchField.getText().trim();
        String selectedDelay = delayFilter.getValue();

        if (searchQuery.length() > 50) {
            searchQuery = searchQuery.substring(0, 50);
        }
        if (!selectedDelay.equals("Összes") && !selectedDelay.equals("Igen") && !selectedDelay.equals("Nem")) {
            selectedDelay = "Összes";
        }

        loadMembers(searchQuery.isEmpty() ? null : searchQuery, selectedDelay);
    }

}

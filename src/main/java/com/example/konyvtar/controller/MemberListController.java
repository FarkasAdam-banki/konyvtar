package com.example.konyvtar.controller;

import com.example.konyvtar.input.TextInput;
import com.example.konyvtar.input.ValidationResult;
import com.example.konyvtar.model.Member;
import com.example.konyvtar.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.*;

public class MemberListController {

    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> delayFilter;

    @FXML
    private Button searchButton;

    @FXML
    private TableView<Member> tableView;

    @FXML
    private TableColumn<Member, Integer> idColumn;

    @FXML
    private TableColumn<Member, String> nameColumn;

    @FXML
    private TableColumn<Member, String> addressColumn;

    @FXML
    private TableColumn<Member, String> phoneColumn;

    @FXML
    private TableColumn<Member, Integer> borrowedBooksColumn;

    @FXML
    private TableColumn<Member, Integer> delayColumn;

    @FXML
    private Label resultCountLabel;

    private TextInput searchFieldInput;

    private final ObservableList<Member> memberList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        delayFilter.setItems(FXCollections.observableArrayList("Késlekedők", "Igen", "Nem"));

        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        addressColumn.setCellValueFactory(new PropertyValueFactory<>("address"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        borrowedBooksColumn.setCellValueFactory(new PropertyValueFactory<>("borrowedBooks"));
        delayColumn.setCellValueFactory(new PropertyValueFactory<>("delay"));

        loadMembers(null, null);

        searchFieldInput = new TextInput(searchField, 50);

        searchField.setOnAction(_ -> searchMembers());
        delayFilter.setOnAction(_ -> searchMembers());
        searchButton.setOnMouseClicked(_ -> searchMembers());
    }


    private void loadMembers(String searchQuery, String delayFilter) {
        memberList.clear();

        String sql = """
                SELECT
                    t.tag_id AS tagsagi_azonosito,
                    t.tag_nev AS nev,
                    CONCAT(c.cim_utca, ' ', c.cim_hsz, ', ', tp.telepules_megnevezese) AS cim,
                    t.tag_tel AS telefonszam,
                    (SELECT COUNT(*) FROM kolcsonzes k WHERE k.tag_id = t.tag_id) AS kolcsonzott_konyvek_szama,
                    (SELECT COUNT(*) FROM kolcsonzes k
                     WHERE k.tag_id = t.tag_id
                     AND k.kolcsonzes_hatar < CURDATE()
                     AND (k.kolcsonzes_visszaE IS NULL OR k.kolcsonzes_visszaE = 0)) AS aktiv_keses
                FROM tag t
                LEFT JOIN cim c ON t.cim_id = c.cim_id
                LEFT JOIN telepules tp ON c.telepules_id = tp.telepules_id;
                """;

        if (searchQuery != null && !searchQuery.trim().isEmpty()) {
            sql += " AND (t.tag_id LIKE ? OR t.tag_nev LIKE ?)";
        }

        if ("Igen".equals(delayFilter)) {
            sql += " AND (SELECT COUNT(*) FROM kolcsonzes k WHERE k.tag_id = t.tag_id AND k.kolcsonzes_hatar < CURDATE()) > 0";
        } else if ("Nem".equals(delayFilter)) {
            sql += " AND NOT EXISTS (SELECT 1 FROM kolcsonzes k WHERE k.tag_id = t.tag_id AND k.kolcsonzes_hatar < CURDATE())";
        }

        try (PreparedStatement preparedStatement = DatabaseConnection.getPreparedStatement(sql)) {
            int paramIndex = 1;

            if (searchQuery != null && !searchQuery.trim().isEmpty()) {
                preparedStatement.setString(paramIndex++, "%" + searchQuery + "%");
                preparedStatement.setString(paramIndex++, "%" + searchQuery + "%");
            }

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                Member member = new Member(
                        resultSet.getInt("tagsagi_azonosito"),
                        resultSet.getString("nev"),
                        resultSet.getString("cim"),
                        resultSet.getString("telefonszam"),
                        resultSet.getInt("kolcsonzott_konyvek_szama"),
                        resultSet.getInt("aktiv_keses")
                );
                memberList.add(member);
            }

            tableView.setItems(memberList);
            resultCountLabel.setText("Találatok száma: " + memberList.size());


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void searchMembers() {
        String searchQuery = searchFieldInput.getValue();
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
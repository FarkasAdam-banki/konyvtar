package com.example.konyvtar.controller;

import com.example.konyvtar.DatabaseConnection;
import com.example.konyvtar.model.Book;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

public class FindBookController {
    @FXML
    private TextField searchField;

    @FXML
    private ComboBox<String> available;

    @FXML
    private TableView<Book> booksTableView;

    @FXML
    private TableColumn<Book, String> titleColumn;

    @FXML
    private TableColumn<Book, String> authorColumn;

    @FXML
    private TableColumn<Book, String> isbnColumn;

    @FXML
    private TableColumn<Book, Integer> yearColumn;

    @FXML
    private TableColumn<Book, String> genreColumn;

    @FXML
    private TableColumn<Book, Boolean> availabilityColumn;

    @FXML
    private ListView<String> popularGendres;

    @FXML
    private Button searchButton;

    @FXML
    private Label resultCountLabel;

    private Connection conn;

    public void initialize() {
        available.setItems(FXCollections.observableArrayList("Kölcsönözhető", "Igen", "Nem"));
        available.setValue("Kölcsönözhető");
        this.conn = DatabaseConnection.getConnection();

        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        yearColumn.setCellValueFactory(new PropertyValueFactory<>("year"));
        genreColumn.setCellValueFactory(new PropertyValueFactory<>("genre"));
        availabilityColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        searchField.setOnAction(event -> fetchBooks());
        searchButton.setOnMouseClicked(event -> fetchBooks());
        available.setOnAction(event -> fetchBooks());
        fetchBooks();
        fetchStats();
    }

    private void fetchBooks() {
        String search = searchField.getText();
        ObservableList<Book> books = FXCollections.observableArrayList();
        ObservableList<Book> resultBooks = FXCollections.observableArrayList();
        String sql;
        try {
            PreparedStatement pstmt;
            sql = """
                        SELECT k.konyv_ISBN, k.konyv_cim, k.konyv_szerzo, k.konyv_kiadas, k.konyv_mufaj,
                        IF(ko.kolcsonzes_visszaE = 0, FALSE, TRUE) AS available
                       FROM konyv k
                       JOIN leltar l ON k.konyv_ISBN = l.konyv_ISBN
                       LEFT JOIN kolcsonzes ko ON l.leltar_leltariszam = ko.leltar_leltariszam AND ko.kolcsonzes_visszaE = 0
                       WHERE k.konyv_ISBN LIKE ? OR k.konyv_cim LIKE ?;
                    """;
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, "%" + search.strip() + "%");
            pstmt.setString(2, "%" + search.strip() + "%");
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String isbn = rs.getString("konyv_ISBN");
                String title = rs.getString("konyv_cim");
                String author = rs.getString("konyv_szerzo");
                int year = rs.getInt("konyv_kiadas");
                String genre = rs.getString("konyv_mufaj");
                boolean available = rs.getBoolean("available");

                books.add(new Book(title, author, isbn, year, genre, available));
            }
            boolean selected = available.getSelectionModel().getSelectedIndex() != 0;
            if (selected) {
                boolean isAvailable = available.getValue()=="Igen";
                for (int i = 0; i < books.size(); i++) {
                    if(books.get(i).isAvailable()==isAvailable){
                        resultBooks.add(books.get(i));
                    }
                }
            }
            else{
                resultBooks=books;
            }
            booksTableView.setItems(resultBooks);
            resultCountLabel.setText("Találatok száma: " + resultBooks.size());


        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void fetchStats() {
        HashMap<String, Integer> genreCount = new HashMap<>();

        String sql = "SELECT konyv_mufaj FROM konyv";
        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String genre = rs.getString("konyv_mufaj");
                genreCount.put(genre, 0);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        sql = "SELECT k.konyv_mufaj, COUNT(leltar.konyv_ISBN) AS book_count " +
                "FROM leltar " +
                "INNER JOIN konyv k ON leltar.konyv_ISBN = k.konyv_ISBN " +
                "GROUP BY k.konyv_mufaj";

        try {
            PreparedStatement pstmt = conn.prepareStatement(sql);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                String genre = rs.getString("konyv_mufaj");
                int count = rs.getInt("book_count");
                genreCount.put(genre, count);
            }
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        Set<String> keys = genreCount.keySet();
        Iterator<String> it = keys.iterator();

        while (it.hasNext()) {
            String genre = it.next();
            popularGendres.getItems().addAll(genre + ": " + genreCount.get(genre) + " példány");
        }
    }
}



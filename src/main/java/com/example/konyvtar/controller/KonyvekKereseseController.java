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

public class KonyvekKereseseController {
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
    private ListView<String> booksListView;

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

        /*
        searchField.setOnAction(event -> fetchBooks());
        available.setOnAction(event -> fetchBooks());*/
        searchButton.setOnMouseClicked(event -> fetchBooks());
    }
    private void fetchBooks(){
        int isAvailable=(available.getValue()=="Igen")?1:0;
        String search=searchField.getText();

        ObservableList<Book> books = FXCollections.observableArrayList();
        String sql="SELECT * FROM konyv WHERE konyv_statusz=? AND (konyv_ISBN LIKE ? OR konyv_cim LIKE ?);";
        try {
            PreparedStatement pstmt=conn.prepareStatement(sql);
            pstmt.setInt(1,isAvailable);
            pstmt.setString(2, "%" + search + "%");
            pstmt.setString(3, "%" + search + "%");
            System.out.println(isAvailable);
            System.out.println(search);
            ResultSet rs=pstmt.executeQuery();
            while (rs.next()){
                String isbn = rs.getString("konyv_ISBN");
                String title = rs.getString("konyv_cim");
                String author = rs.getString("konyv_szerzo");
                int year = rs.getInt("konyv_kiadas");
                String genre = rs.getString("konyv_mufaj");
                boolean available = rs.getBoolean("konyv_statusz");

                books.add(new Book(title, author, isbn, year, genre, available));
            }

            booksTableView.setItems(books);
            resultCountLabel.setText("Találatok száma: " + books.size());


        }
        catch (SQLException e){
            e.printStackTrace();
        }
    }



}



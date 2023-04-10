package com.vojtechruzicka.javafxweaverexample;



import com.vojtechruzicka.javafxweaverexample.entity.BookEntity;
import com.vojtechruzicka.javafxweaverexample.service.BookService;
import com.vojtechruzicka.javafxweaverexample.service.UserService;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static javafx.scene.control.ButtonType.CANCEL;


@Component
@FxmlView("main-stage.fxml")
public class MyController {

    @FXML
    private TableColumn<BookEntity, String> author;

    @FXML
    private Button bDelete;

    @FXML
    private TableColumn<BookEntity, String> genre;

    @FXML
    private AnchorPane isRol;

    @FXML
    private Button onSave;

    @FXML
    private TableColumn<BookEntity, String> publisher;

    @FXML
    private TextField tAuthor;

    @FXML
    private TextField tGenre;

    @FXML
    private TextField tPublisher;

    @FXML
    private TextField tTitle;

    @FXML
    private TextField tYear;

    @FXML
    private TableView<BookEntity> table;

    @FXML
    private TableColumn<BookEntity, String> title;

    @FXML
    private TableColumn<BookEntity, String> year;

    private UserService userService;
    private BookService bookService;

    private ObservableList<BookEntity> bookArr = FXCollections.observableArrayList();



    @FXML
    void onDelete(ActionEvent event) {

        int selectedIndex = table.getSelectionModel().getSelectedIndex();
        if(selectedIndex >=0){
            bookService.deleteBook(table.getItems().get(selectedIndex).getId());
            table.getItems().remove(selectedIndex);
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle(("Нет выбора"));
            alert.setHeaderText("Никто не выбран");
            alert.setContentText("Пожалуйста выберите человека в таблице.");
            alert.showAndWait();
        }
    }

    @FXML
    void onSave(ActionEvent event) {
      if(!(tTitle.equals("")) && !(tAuthor.equals(""))){
          BookEntity book = new BookEntity();
          book.setTitle((tTitle.getText()));
          book.setGenre(tGenre.getText());
          book.setPublisher(tPublisher.getText());
          book.settAuthor(tAuthor.getText());
          book.setYear(tYear.getText());

          bookArr.add(bookService.saveBook(book));
          tTitle.setText("");
          tGenre.setText("");
          tPublisher.setText("");
          tAuthor.setText("");
          tYear.setText("");

      }
    }



   //конструктор, используется для определения сервисного слоя
    public MyController(UserService userService, BookService bookService) {
        this.userService = userService;
        this.bookService = bookService;
    }

   //настройка программы в зависимости от роли
   private void isRol(int rol){
       if (rol == 0){
           isRol.setVisible(true);
       }
   }
    // создание диалогового окна логин/пароль
    private void loginDialog(){

        // Создание диалогового окна.
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Окно логина");
        dialog.setHeaderText("Введите логин пароль для входа в программу");

        // Создание кнопок(OK, Cancel).
        ButtonType loginButtonType = new ButtonType("Вход", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(loginButtonType, CANCEL);

        // Разметка окна через GridPane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        //Поля ввода
        TextField username = new TextField();
        username.setPromptText("Логин");
        PasswordField password = new PasswordField();
        password.setPromptText("Пароль");
        // надписи
        grid.add(new Label("Логин"), 0, 0);
        grid.add(username, 1, 0);
        grid.add(new Label("Пароль"), 0, 1);
        grid.add(password, 1, 1);

        // Включить/выключить кнопку входа в зависимости от того, было ли введено имя пользователя.
        Node loginButton = dialog.getDialogPane().lookupButton(loginButtonType);
        loginButton.setDisable(true);

        // Фокус на поле имени пользователя по умолчанию.
        Platform.runLater(() -> username.requestFocus());

        // Выполните некоторую проверку (используя лямбда-синтаксис Java 8).
        username.textProperty().addListener((observable, oldValue, newValue) -> {
            loginButton.setDisable(newValue.trim().isEmpty());
        });

        dialog.getDialogPane().setContent(grid);

        // Преобразование результата в пару "имя пользователя-пароль" при нажатии кнопки входа.
        // И смотрим на правильность ввода логина пароля
        // если правильно то узнаем роль


        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == loginButtonType) {
                return new Pair<>(username.getText(), password.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        if (result.isPresent()){
            if(userService.logIn(username.getText(), password.getText())){
                System.out.println("Username=" + username.getText() + ", Password=" + password.getText());
                isRol(userService.getRol(username.getText(), password.getText()));
            }


        }



/*
        result.ifPresent(usernamePassword -> {
            if(userService.logIn(usernamePassword.getValue(),usernamePassword.getKey()))
            System.out.println("Username=" + usernamePassword.getKey() + ", Password=" + usernamePassword.getValue());
        });*/
    }

    @FXML

    private void initialize() {
        loginDialog(); // вызов диалогового окна логин/пароль
        bookService.getAll().forEach(bookArr::add);

        author.setCellValueFactory(new PropertyValueFactory<BookEntity, String>("author"));
        title.setCellValueFactory(new PropertyValueFactory<BookEntity, String> ( "title"));
        genre.setCellValueFactory(new PropertyValueFactory<BookEntity, String> ( "genre"));
        publisher.setCellValueFactory(new PropertyValueFactory<BookEntity, String> ( "publisher"));
        year.setCellValueFactory (new PropertyValueFactory<BookEntity, String> ( "year"));
        table.setItems(bookArr);//3
        // Слушаем изменения выбора, и при изменении отображаем
        // дополнительную информацию об студенте.
        table.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBookEntityDetails(newValue));

    }

    private void showBookEntityDetails(BookEntity BookEntity) {
        if (BookEntity != null) {
            // Заполняем метки информацией из объекта BookEntity.
            tTitle.setText(BookEntity.getTitle());
            tGenre.setText(BookEntity.getGenre());
            tPublisher.setText(BookEntity.getPublisher());
            tAuthor.setText(BookEntity.getAuthor());
            tYear.setText(BookEntity.getYear());
        } else {
            // Если BookEntity = null, TO убираем весь текст.
            tTitle.setText(")");
            tGenre.setText("");
            tPublisher.setText("");
            tAuthor.setText("");
            tYear.setText("");

        }
    }

}

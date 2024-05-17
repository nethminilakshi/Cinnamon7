package lk.ijse.restaurantManagement.controller;


import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;
import lk.ijse.restaurantManagement.model.Customer;
import lk.ijse.restaurantManagement.model.Item;
import lk.ijse.restaurantManagement.model.tm.CustomerTm;
import lk.ijse.restaurantManagement.model.tm.EmployeeTm;
import lk.ijse.restaurantManagement.model.tm.ItemTm;
import lk.ijse.restaurantManagement.repository.CustomerRepo;
import lk.ijse.restaurantManagement.repository.EmployeeRepo;
import lk.ijse.restaurantManagement.util.Regex;

import javax.swing.text.html.ImageView;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerFormController {

    @FXML
    private ImageView nextbtn;
    @FXML
    private TextField txtGmailSend;

    @FXML
    private TableColumn<?, ?> colAddress;

    @FXML
    private TableColumn<?, ?> colContact;

    @FXML
    private TableColumn<?, ?> colId;

    @FXML
    private TableColumn<?, ?> colName;

    @FXML
    private TableColumn<?, ?> colEmail;

    @FXML
    private AnchorPane root;

    @FXML
    private TableView<CustomerTm> tblCustomers;

    @FXML
    private TextField txtAddress;

    @FXML
    private TextField txtContact;

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;
    public static String gmail="";

    private List<Customer> customerList = new ArrayList<>();

    public void initialize() {

        try {
            autoGenarateId();
        } catch (ClassNotFoundException | SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }

        this.customerList = getAllCustomers();
        setCellValueFactory();
        loadCustomerTable();
    }

    private void setCellValueFactory() {
        colId.setCellValueFactory(new PropertyValueFactory<>("cusId"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colContact.setCellValueFactory(new PropertyValueFactory<>("contact"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
    }

    private List<Customer> getAllCustomers() {
        List<Customer> customerList = null;
        try {
            customerList = CustomerRepo.getAll();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return customerList;
    }

    private void loadCustomerTable() {
        ObservableList<CustomerTm> tmList = FXCollections.observableArrayList();

        for (Customer customer : customerList) {
            CustomerTm customerTm = new CustomerTm(
                    customer.getCusId(),
                    customer.getName(),
                    customer.getAddress(),
                    customer.getContact(),
                    customer.getEmail()
            );
            tmList.add(customerTm);
        }
        tblCustomers.setItems(tmList);
        System.out.println(tmList.toString());
        /*CustomerTm selectedItem = tblCustomers.getSelectionModel().getSelectedItem();*/
        /*System.out.println(selectedItem.toString());*/
    }

    @FXML
    void btnBackOnAction(ActionEvent event) throws IOException {
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource("/view/main_form.fxml"));
        Stage stage = (Stage) root.getScene().getWindow();

        stage.setScene(new Scene(anchorPane));
        stage.setTitle("Dashboard Form");
        stage.centerOnScreen();
    }

    private void clearFields() {
        txtId.setText("");
        txtName.setText("");
        txtAddress.setText("");
        txtContact.setText("");
        txtEmail.setText("");
    }

    @FXML
    void btnClearOnAction(ActionEvent event) {
        clearFields();
    }

    @FXML
    void btnDeleteOnAction(ActionEvent event) {
        String contact = txtContact.getText();

        try {
            boolean isDeleted = CustomerRepo.delete(contact);
            if (isDeleted) {
                new Alert(Alert.AlertType.CONFIRMATION, "customer deleted!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        clearFields();
        initialize();
    }

    @FXML
    void btnSaveOnAction(ActionEvent event) {
        String cusId = txtId.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();
        String contact = txtContact.getText();
        String email = txtEmail.getText();

        Customer customer = new Customer(cusId, name, address,contact,email);

        try {
            boolean isSaved = CustomerRepo.save(customer);
            if (isSaved) {
                new Alert(Alert.AlertType.CONFIRMATION, "customer saved!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        clearFields();
        initialize();
    }

    @FXML
    void btnUpdateOnAction(ActionEvent event) {
        String cusId=txtId.getText();
        String name = txtName.getText();
        String address = txtAddress.getText();
        String contact = txtContact.getText();
        String email = txtEmail.getText();

        Customer customer = new Customer(cusId, name, address,contact,email);

        try {
            boolean isUpdated = CustomerRepo.update(customer);
            if (isUpdated) {
                new Alert(Alert.AlertType.CONFIRMATION, "customer updated!").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
        initialize();
    }
    @FXML
    private void autoGenarateId() throws SQLException, ClassNotFoundException {
        txtId.setText(new CustomerRepo().autoGenarateCustomerId());
    }
    @FXML
    void txtSearchOnAction(ActionEvent event) {
        String contact = txtContact.getText();

        try {
            Customer customer = CustomerRepo.searchByContact(contact);

            if (customer != null) {
                txtId.setText(customer.getCusId());
                txtName.setText(customer.getName());
                txtAddress.setText(customer.getAddress());
                txtContact.setText(customer.getContact());
                txtEmail.setText(customer.getEmail());
            } else {
                new Alert(Alert.AlertType.INFORMATION, "Not Found Customer").show();
            }
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).show();
        }
    }

    public void tblOnClickAction(MouseEvent mouseEvent) {
        CustomerTm selectedItem = tblCustomers.getSelectionModel().getSelectedItem();
        txtId.setText(selectedItem.getCusId());
        txtName.setText(selectedItem.getName());
        txtAddress.setText(selectedItem.getAddress());
        txtContact.setText(selectedItem.getContact());
        txtEmail.setText(selectedItem.getEmail());

    }

    public void txtCustomerNameOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.restaurantManagement.util.TextField.DESC,txtName);
    }

    public void txtCustomerIContactOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.restaurantManagement.util.TextField.CONTACT,txtContact);
    }

    public void txtCustomerAddressOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.restaurantManagement.util.TextField.ADDRESS,txtAddress);
    }

    public void txtCustomerEmailOnKeyReleased(KeyEvent keyEvent) {
        Regex.setTextColor(lk.ijse.restaurantManagement.util.TextField.EMAIL,txtEmail);
    }
    public boolean isValidate(){
        if(!Regex.setTextColor(lk.ijse.restaurantManagement.util.TextField.DESC,txtName))return false;
        if(!Regex.setTextColor(lk.ijse.restaurantManagement.util.TextField.CONTACT,txtContact))return false;
        if(!Regex.setTextColor(lk.ijse.restaurantManagement.util.TextField.ADDRESS,txtAddress))return false;
        if(!Regex.setTextColor(lk.ijse.restaurantManagement.util.TextField.EMAIL,txtEmail))return false;
        return true;
    }

    public void txtGmailKeyTyped(KeyEvent keyEvent) {
        if (!txtGmailSend.getText().equals("")){
            for (int i = 0; i < txtGmailSend.getText().length(); i++) {
                char ansible = txtGmailSend.getText().charAt(i);
                if (ansible == '@'){
                    txtGmailSend.setStyle("-fx-border-color: green");
                  //  nextbtn.setDisable(false);
                    break;
                }
                else {
                    txtGmailSend.setStyle("-fx-border-color: red");
                  //  nextbtn.setDisable(true);
                }
            }
        }
    }

    public void btnNextOnAction(MouseEvent mouseEvent) {
        gmail = txtGmailSend.getText();
        if (txtGmailSend.getText().equals("")){
            new Alert(Alert.AlertType.WARNING,"enter valid gmail address !").show();
            return;
        }
        Parent root = null;
        try {
            root = FXMLLoader.load(this.getClass().getResource("/view/mailSender_form.fxml"));
            if (root != null) {
                Scene subScene = new Scene(root);
                Stage primaryStage = (Stage) this.root.getScene().getWindow();
                primaryStage.setScene(subScene);
                primaryStage.centerOnScreen();

                TranslateTransition tt = new TranslateTransition(Duration.millis(350), subScene.getRoot());
                tt.setFromX(-subScene.getWidth());
                tt.setToX(0);
                tt.play();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void mouseEnter(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof javafx.scene.image.ImageView) {
            javafx.scene.image.ImageView icon = (javafx.scene.image.ImageView) mouseEvent.getSource();

            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1.2);
            scaleT.setToY(1.2);
            scaleT.play();

            DropShadow glow = new DropShadow();
//            glow.setColor(Color.valueOf("#EF233C"));
            glow.setColor(Color.CORNFLOWERBLUE);
            glow.setWidth(15);
            glow.setHeight(15);
            glow.setRadius(15);
            icon.setEffect(glow);
        }
    }

    public void mouseExit(MouseEvent mouseEvent) {
        if (mouseEvent.getSource() instanceof javafx.scene.image.ImageView) {
            javafx.scene.image.ImageView icon = (javafx.scene.image.ImageView) mouseEvent.getSource();
            ScaleTransition scaleT = new ScaleTransition(Duration.millis(200), icon);
            scaleT.setToX(1);
            scaleT.setToY(1);
            scaleT.play();
            icon.setEffect(null);
        }
    }
}

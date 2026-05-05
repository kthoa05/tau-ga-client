package controller.ql.tau;

import dto.TauDTO;
import entity.TauEntity;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.TauRequest;
import service.ITauService;
import service.impl.TauServiceImpl;
import utils.GiaoDienUtils;

import java.util.Collections;
import java.util.List;

public class QuanLyTau {
    @FXML private TextField txtMaTau;
    @FXML private TextField txtTenTau;
    @FXML private TextField txtSoToaTau;

    @FXML private TableView<TauDTO> tblTau;
    @FXML private TableColumn<TauDTO, String> colMaTau;
    @FXML private TableColumn<TauDTO, String> colTenTau;
    @FXML private TableColumn<TauDTO, Integer> colSoToaTau;

    //private final ITauService tauService = new TauServiceImpl();
    private final ObservableList<TauDTO> danhSachTau = FXCollections.observableArrayList();
    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        colMaTau.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getMaTau()));

        colTenTau.setCellValueFactory(cell ->
                new SimpleStringProperty(cell.getValue().getTenTau()));

        colSoToaTau.setCellValueFactory(cell ->
                new SimpleObjectProperty<>(cell.getValue().getSoToaTau()));

        tblTau.setItems(danhSachTau);
        setMaEditable(false);

        tblTau.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, item) -> {
            if (item != null) {
                txtMaTau.setText(item.getMaTau());
                txtTenTau.setText(item.getTenTau());
                txtSoToaTau.setText(String.valueOf(item.getSoToaTau()));

                setMaEditable(true);
            } else {
                clearFormFieldsOnly();
                setMaEditable(false);
            }
        });

        txtMaTau.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                // toggle editable state
                setMaEditable(!txtMaTau.isEditable());
            }
        });
        txtMaTau.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.E) {
                setMaEditable(!txtMaTau.isEditable());
                e.consume();
            }
        });
        txtMaTau.setEditable(false);
        loadTau("", "", null);
    }

    private void setMaEditable(boolean editable) {
        txtMaTau.setEditable(editable);
        if (!editable) {
            txtMaTau.setStyle("-fx-control-inner-background: #F4F4F4;");
        } else {
            txtMaTau.setStyle("");
        }
    }

    private void clearFormFieldsOnly() {
        txtMaTau.clear();
        txtTenTau.clear();
        txtSoToaTau.clear();
    }


    private void loadTau(String ma, String ten, Integer soToa) {

        Task<List<TauDTO>> task = new Task<>() {
            @Override
            protected List<TauDTO> call() {

                TauRequest req = new TauRequest(ma, ten, soToa);

                Response res = socketClient.send(
                        new Request(CommandType.SEARCH_TAU, req)
                );

                if (!res.isSuccess()) return List.of();

                return (List<TauDTO>) res.getData();
            }
        };

        task.setOnSucceeded(e -> danhSachTau.setAll(task.getValue()));

        new Thread(task).start();
    }
    @FXML
    private void themTau() {
        try {
            String ten = txtTenTau.getText();
            int soToa = Integer.parseInt(txtSoToaTau.getText());

            Response resMa = socketClient.send(
                    new Request(CommandType.AUTO_GEN_MA_TAU, ten)
            );

            String ma = (String) resMa.getData();
            txtMaTau.setText(ma);

            TauDTO tau = new TauDTO(ma, ten, soToa);

            Response res = socketClient.send(
                    new Request(CommandType.ADD_TAU, tau)
            );

            if (res.isSuccess()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "OK", "Thêm thành công");
                loadTau("", "", null);
                clearForm();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @FXML
    private void capNhatTau() {
        TauDTO selected = tblTau.getSelectionModel().getSelectedItem();
        if (selected == null) return;

        TauDTO tau = new TauDTO(
                selected.getMaTau(),
                txtTenTau.getText(),
                Integer.parseInt(txtSoToaTau.getText())
        );

        Response res = socketClient.send(
                new Request(CommandType.UPDATE_TAU, tau)
        );

        if (res.isSuccess()) {
            loadTau("", "", null);
        }
    }

    @FXML
    private void timTau() {
        Integer soToa = txtSoToaTau.getText().isEmpty() ? null :
                Integer.parseInt(txtSoToaTau.getText());

        loadTau(
                txtMaTau.getText(),
                txtTenTau.getText(),
                soToa
        );
    }
    private void populateForm(TauDTO tau) {
        txtMaTau.setText(tau.getMaTau());
        txtTenTau.setText(tau.getTenTau());
        txtSoToaTau.setText(String.valueOf(tau.getSoToaTau()));
    }

    @FXML
    private void xoaTrang() {
        clearFormFieldsOnly();
        tblTau.getSelectionModel().clearSelection();
        setMaEditable(false);
        loadTau("", "", null);
    }
    private void clearForm() {
        txtMaTau.clear();
        txtTenTau.clear();
        txtSoToaTau.clear();
        tblTau.getSelectionModel().clearSelection();
    }
}

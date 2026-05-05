package controller.ql.ga;

import dto.GaDTO;
import entity.GaEntity;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.GaRequest;
import utils.GiaoDienUtils;

import java.util.List;

public class QuanLyDiaDiemGa {

    @FXML private TextField txtMaGa, txtTenGa, txtDiaChiGa;
    @FXML private TableView<GaDTO> tblGa;
    @FXML private TableColumn<GaDTO, String> colMaGa, colTenGa, colDiaChiGa;
    @FXML private Button btnTim;
    private final ObservableList<GaDTO> danhSachGa = FXCollections.observableArrayList();
    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        colMaGa.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMaGa()));
        colTenGa.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTenGa()));
        colDiaChiGa.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDiaChi()));

        tblGa.setItems(danhSachGa);

        tblGa.setOnMouseClicked(event -> {
            GaDTO selected = tblGa.getSelectionModel().getSelectedItem();
            if (selected != null) {
                populateForm(selected);
                setMaEditable(false); // luôn khóa mã
            }
        });

        txtMaGa.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                setMaEditable(true);
            }
        });

        btnTim.setOnAction(e -> timKiemGa());

        setMaEditable(false);

        loadGa("");
    }
    private void setMaEditable(boolean editable) {
        txtMaGa.setEditable(editable);
        if (!editable) {
            txtMaGa.setStyle("-fx-control-inner-background: #F4F4F4;");
        } else {
            txtMaGa.setStyle("");
        }
    }
    private void loadGa(String keyword) {
        Task<List<GaDTO>> task = new Task<>() {
            @Override
            protected List<GaDTO> call() {
                GaRequest payload = new GaRequest(keyword);
                Request request = new Request(CommandType.SEARCH_GA1, payload);

                Response response = socketClient.send(request);

                System.out.println("Response: " + response);
                System.out.println("Success: " + response.isSuccess());
                System.out.println("Data: " + response.getData());

                if (!response.isSuccess()) return List.of();

                return (List<GaDTO>) response.getData();
            }
        };
        task.setOnSucceeded(e -> {
            danhSachGa.clear();
            danhSachGa.setAll(task.getValue());
        });

        new Thread(task).start();
    }

    @FXML
    private void themGa() {
        try {
            String ten = txtTenGa.getText().trim();
            String diaChi = txtDiaChiGa.getText().trim();

            if (ten.isEmpty() || diaChi.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", "Nhập thiếu");
                return;
            }

            Response resMa = socketClient.send(
                    new Request(CommandType.AUTO_GEN_MA_GA, ten)
            );

            String ma = (String) resMa.getData();
            txtMaGa.setText(ma);

            GaEntity ga = new GaEntity(ma, ten, diaChi);

            Response res = socketClient.send(
                    new Request(CommandType.ADD_GA, ga)
            );

            if (res.isSuccess()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "OK", "Thêm thành công");
                loadGa("");
                clearForm();
                setMaEditable(false);
            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", res.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void capNhatGa() {
        try {
            GaDTO selected = tblGa.getSelectionModel().getSelectedItem();
            if (selected == null) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", "Chọn dòng");
                return;
            }

            GaEntity ga = new GaEntity(
                    selected.getMaGa(),
                    txtTenGa.getText(),
                    txtDiaChiGa.getText()
            );

            Response res = socketClient.send(
                    new Request(CommandType.UPDATE_GA, ga)
            );

            if (res.isSuccess()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "OK", "Cập nhật thành công");
                loadGa("");
            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", res.getMessage());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void populateForm(GaDTO ga) {
        txtMaGa.setText(ga.getMaGa());
        txtTenGa.setText(ga.getTenGa());
        txtDiaChiGa.setText(ga.getDiaChi());
    }
    @FXML
    private void timKiemGa() {
        String keyword = "";

        if (!txtMaGa.getText().trim().isEmpty()) {
            keyword = txtMaGa.getText().trim();
        } else if (!txtTenGa.getText().trim().isEmpty()) {
            keyword = txtTenGa.getText().trim();
        } else if (!txtDiaChiGa.getText().trim().isEmpty()) {
            keyword = txtDiaChiGa.getText().trim();
        }

        loadGa(keyword);
    }

    @FXML
    private void xoaTrang() {
        clearForm();
        setMaEditable(false);
        loadGa("");
    }

    private void clearForm() {
        txtMaGa.clear();
        txtTenGa.clear();
        txtDiaChiGa.clear();
        tblGa.getSelectionModel().clearSelection();
        txtTenGa.requestFocus();
    }
}
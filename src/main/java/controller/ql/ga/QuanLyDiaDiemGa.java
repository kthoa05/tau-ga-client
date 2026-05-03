package controller.ql.ga;

import dto.GaDTO;
import entity.GaEntity;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import service.IGaService;
import service.impl.GaServiceImpl;
import utils.GiaoDienUtils;

import java.util.Collections;
import java.util.List;

public class QuanLyDiaDiemGa {

    @FXML
    private TextField txtMaGa, txtTenGa, txtDiaChiGa;

    @FXML
    private Button btnThem, btnCapNhat, btnXoaTrang, btnTim;

    @FXML
    private TableView<GaDTO> tblGa;
    @FXML
    private TableColumn<GaDTO, String> colMaGa;
    @FXML
    private TableColumn<GaDTO, String> colTenGa;
    @FXML
    private TableColumn<GaDTO, String> colDiaChiGa;

    private final ObservableList<GaDTO> danhSachGa = FXCollections.observableArrayList();
    private final IGaService gaService = new GaServiceImpl();

    @FXML
    public void initialize() {
        // map columns
        colMaGa.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getMaGa()));
        colTenGa.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getTenGa()));
        colDiaChiGa.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(data.getValue().getDiaChi()));

        tblGa.setItems(danhSachGa);

        setMaEditable(false);

        tblGa.setRowFactory(tv -> {
            TableRow<GaDTO> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (!row.isEmpty() && event.getClickCount() == 1) {
                    populateForm(row.getItem());
                    setMaEditable(true);
                }
            });
            return row;
        });

        txtMaGa.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                setMaEditable(!txtMaGa.isEditable());
            }
        });

        txtMaGa.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.E) {
                setMaEditable(!txtMaGa.isEditable());
                e.consume();
            }
        });

        btnTim.setOnAction(e -> timKiemGa());
        btnXoaTrang.setOnAction(this::xoaTrang);

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

    private void timKiemGa() {
        String keyword = "";
        if (!txtMaGa.getText().trim().isEmpty()) keyword = txtMaGa.getText().trim();
        else if (!txtTenGa.getText().trim().isEmpty()) keyword = txtTenGa.getText().trim();
        else if (!txtDiaChiGa.getText().trim().isEmpty()) keyword = txtDiaChiGa.getText().trim();

        loadGa(keyword);
    }

    private void loadGa(String keyword) {
        Task<List<GaDTO>> task = new Task<>() {
            @Override
            protected List<GaDTO> call() {
                return gaService.timKiemGa1(keyword == null ? "" : keyword);
            }
        };

        task.setOnSucceeded(e -> {
            List<GaDTO> list = task.getValue();
            danhSachGa.setAll(list);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            Platform.runLater(() -> GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi tải dữ liệu", "Không thể tải danh sách ga: " + ex.getMessage()));
        });

        new Thread(task).start();
    }

    private void populateForm(GaDTO ga) {
        if (ga == null) return;
        txtMaGa.setText(ga.getMaGa());
        txtTenGa.setText(ga.getTenGa());
        txtDiaChiGa.setText(ga.getDiaChi());
    }

    @FXML
    private void xoaTrang(ActionEvent actionEvent) {
        clearForm();
        loadGa("");
    }

    private void clearForm() {
        txtMaGa.clear();
        txtTenGa.clear();
        txtDiaChiGa.clear();
        tblGa.getSelectionModel().clearSelection();
        setMaEditable(false);
    }

    @FXML
    private void themGa(ActionEvent actionEvent) {
        try {
            String ten = txtTenGa.getText().trim();
            String diaChi = txtDiaChiGa.getText().trim();

            if (ten.isEmpty() || diaChi.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thiếu dữ liệu", "Vui lòng nhập đầy đủ Tên ga và Địa chỉ.");
                return;
            }

            String ma = gaService.taoMaGaTuDong(ten);
            if (ma == null || ma.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", "Tạo mã Ga thất bại.");
                return;
            }
            txtMaGa.setText(ma);

            GaEntity entity = new GaEntity(ma, ten, diaChi);
            boolean ok = gaService.themGa(entity);
            if (ok) {
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thành công", "Thêm Ga thành công!");

                GaDTO shown = new GaDTO(ma, ten, diaChi, 0);
                danhSachGa.setAll(Collections.singletonList(shown));
                tblGa.getSelectionModel().select(0);
                tblGa.scrollTo(0);

            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thất bại", "Thêm Ga thất bại. Vui lòng kiểm tra dữ liệu.");
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Có lỗi", "Đã xảy ra lỗi khi thêm Ga: " + ex.getMessage());
        }
    }

    @FXML
    private void capNhatGa(ActionEvent actionEvent) {
        try {
            GaDTO selected = tblGa.getSelectionModel().getSelectedItem();

            if (selected == null) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                        "Thiếu dữ liệu", "Vui lòng chọn Ga để cập nhật.");
                return;
            }

            String oldMa = selected.getMaGa();
            String ten = txtTenGa.getText().trim();
            String diaChi = txtDiaChiGa.getText().trim();

            if (ten.isEmpty() || diaChi.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                        "Thiếu dữ liệu", "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            boolean ok = gaService.capNhatGaVaThayDoiMa(oldMa, ten, diaChi);
            if (ok) {
                String newMa = oldMa;
                try {
                    List<GaDTO> found = gaService.timKiemGa1(ten);
                    if (found != null && !found.isEmpty()) {
                        GaDTO match = null;
                        for (GaDTO g : found) {
                            if (g.getDiaChi() != null && g.getDiaChi().equalsIgnoreCase(diaChi)) {
                                match = g;
                                break;
                            }
                        }
                        if (match == null) match = found.get(0);
                        if (match != null && match.getMaGa() != null && !match.getMaGa().isEmpty()) {
                            newMa = match.getMaGa();
                        }
                    }
                } catch (Exception ignored) {}

                GaDTO shown = new GaDTO(newMa, ten, diaChi, selected.getSoChuyen());
                danhSachGa.setAll(Collections.singletonList(shown));

                tblGa.refresh();
                tblGa.getSelectionModel().select(0);
                tblGa.scrollTo(0);

                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION,
                        "Thành công", "Cập nhật Ga thành công!");
            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                        "Thất bại", "Không cập nhật được Ga. Kiểm tra dữ liệu.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi",
                    "Lỗi khi cập nhật Ga: " + e.getMessage());
        }
    }
}

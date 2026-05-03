package controller.ql.tau;

import entity.TauEntity;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import service.ITauService;
import service.impl.TauServiceImpl;
import utils.GiaoDienUtils;

import java.util.Collections;
import java.util.List;

public class QuanLyTau {

    @FXML private TextField txtMaTau;
    @FXML private TextField txtTenTau;
    @FXML private TextField txtSoToaTau;

    @FXML private TableView<TauEntity> tblTau;
    @FXML private TableColumn<TauEntity, String> colMaTau;
    @FXML private TableColumn<TauEntity, String> colTenTau;
    @FXML private TableColumn<TauEntity, Integer> colSoToaTau;

    private final ITauService tauService = new TauServiceImpl();
    private final ObservableList<TauEntity> danhSachTau = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colMaTau.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getMaTau())
        );

        colTenTau.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleStringProperty(cell.getValue().getTenTau())
        );

        colSoToaTau.setCellValueFactory(cell ->
                new javafx.beans.property.SimpleObjectProperty<>(cell.getValue().getSoToaTau())
        );

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
        loadData();
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

    private void loadData() {
        Task<List<TauEntity>> task = new Task<>() {
            @Override
            protected List<TauEntity> call() {
                return tauService.getAllTau();
            }
        };

        task.setOnSucceeded(e -> {
            List<TauEntity> list = task.getValue();
            danhSachTau.setAll(list);
        });

        task.setOnFailed(e -> {
            Throwable ex = task.getException();
            ex.printStackTrace();
            Platform.runLater(() ->
                    GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", "Không tải được danh sách tàu: " + ex.getMessage())
            );
        });

        new Thread(task).start();
    }

    @FXML
    private void themTau(ActionEvent event) {
        try {
            String tenTau = txtTenTau.getText().trim();
            String soToaStr = txtSoToaTau.getText().trim();
            if (tenTau.isEmpty() || soToaStr.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thiếu dữ liệu", "Vui lòng nhập đầy đủ Tên tàu và Số toa.");
                return;
            }

            int soToa;
            try {
                soToa = Integer.parseInt(soToaStr);
                if (soToa <= 0) {
                    GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Giá trị không hợp lệ", "Số toa phải là số nguyên dương.");
                    return;
                }
            } catch (NumberFormatException nfe) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Sai định dạng", "Số toa phải là một số nguyên (ví dụ: 10).");
                return;
            }
            String maTau;
            if (txtMaTau.isEditable() && !txtMaTau.getText().trim().isEmpty()) {
                maTau = txtMaTau.getText().trim();
            } else {
                maTau = tauService.taoMaTauTuDong(tenTau);
            }

            TauEntity tau = new TauEntity(maTau, tenTau, soToa);

            boolean ok = tauService.themTau(tau);
            if (ok) {
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thành công", "Thêm tàu thành công!");
                String actualMa = null;
                try {
                    actualMa = tauService.timMaTauTheoTen(tenTau);
                } catch (Exception ignored) {}

                if (actualMa != null && !actualMa.isEmpty()) {
                    tau.setMaTau(actualMa);
                }
                danhSachTau.setAll(Collections.singletonList(tau));
                tblTau.getSelectionModel().select(0);
                tblTau.scrollTo(0);
            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thất bại", "Thêm tàu thất bại. Vui lòng kiểm tra dữ liệu hoặc liên hệ quản trị.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Có lỗi", "Đã xảy ra lỗi khi thêm tàu: " + ex.getMessage());
        }
    }

    @FXML
    private void capNhatTau(ActionEvent event) {
        try {
            String oldMaTau = txtMaTau.getText().trim();
            String tenTau = txtTenTau.getText().trim();
            String soToaStr = txtSoToaTau.getText().trim();

            if (oldMaTau.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thiếu dữ liệu", "Vui lòng chọn mã tàu cần cập nhật.");
                return;
            }
            if (tenTau.isEmpty() || soToaStr.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thiếu dữ liệu", "Vui lòng nhập đầy đủ Tên tàu và Số toa.");
                return;
            }

            int soToa;
            try {
                soToa = Integer.parseInt(soToaStr);
                if (soToa <= 0) {
                    GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Giá trị không hợp lệ", "Số toa phải là số nguyên dương.");
                    return;
                }
            } catch (NumberFormatException nfe) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Sai định dạng", "Số toa phải là một số nguyên (ví dụ: 10).");
                return;
            }

            TauEntity tauMoi = new TauEntity(null, tenTau, soToa);

            boolean ok = tauService.capNhatTau(oldMaTau, tauMoi);
            if (ok) {
                String newMa = null;
                try {
                    newMa = tauService.timMaTauTheoTen(tenTau);
                } catch (Exception ignored) {}

                if (newMa == null || newMa.isEmpty()) newMa = oldMaTau;

                TauEntity shown = new TauEntity(newMa, tenTau, soToa);

                danhSachTau.setAll(Collections.singletonList(shown));
                tblTau.refresh();
                tblTau.getSelectionModel().select(0);
                tblTau.scrollTo(0);

                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thành công", "Cập nhật tàu thành công (mã có thể đã được điều chỉnh).");
            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thất bại", "Cập nhật tàu thất bại. Có thể trùng mã hoặc lỗi DB.");
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Có lỗi", "Đã xảy ra lỗi khi cập nhật tàu: " + ex.getMessage());
        }
    }

    @FXML
    private void timTau(ActionEvent event) {
        try {
            String ma = txtMaTau.getText().trim();
            String ten = txtTenTau.getText().trim();
            String soToaStr = txtSoToaTau.getText().trim();

            Integer soToa = null;
            if (!soToaStr.isEmpty()) {
                try {
                    soToa = Integer.parseInt(soToaStr);
                } catch (NumberFormatException nfe) {
                    GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Sai định dạng", "Số toa phải là một số nguyên (ví dụ: 10).");
                    return;
                }
            }
            if (ma.isEmpty() && ten.isEmpty() && soToaStr.isEmpty()) {
                loadData();
                return;
            }

            List<TauEntity> list = tauService.timTau(ma, ten, soToa);
            if (list == null || list.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "", "Không tìm thấy tàu phù hợp với tiêu chí.");
                danhSachTau.clear();
            } else {
                danhSachTau.setAll(list);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Có lỗi", "Đã xảy ra lỗi khi tìm tàu: " + ex.getMessage());
        }
    }

    @FXML
    private void xoaTrang() {
        clearFormFieldsOnly();
        tblTau.getSelectionModel().clearSelection();
        setMaEditable(false);
        loadData();
    }
}

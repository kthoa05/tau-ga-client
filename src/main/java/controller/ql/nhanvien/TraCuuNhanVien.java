package controller.ql.nhanvien;

import entity.NhanVienEntity;
import entity.enums.TrangThaiLamViec;
import entity.enums.VaiTroNhanVien;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import service.INhanVienService;
import service.impl.NhanVienServiceImpl;
import utils.GiaoDienUtils;
import utils.TauGaUtils;

import java.time.LocalDate;

public class TraCuuNhanVien {
    @FXML
    public TextField txtSoDienThoai;
    @FXML
    public TextField txtCCCD;
    @FXML
    private TextField txtMaNhanVien;
    @FXML
    private TableView<NhanVienEntity> tableNhanVien;
    @FXML
    private TableColumn<NhanVienEntity, String> maNVCol;
    @FXML
    private TableColumn<NhanVienEntity, String> tenNVCol;
    @FXML
    private TableColumn<NhanVienEntity, Boolean> gioiTinhCol;
    @FXML
    private TableColumn<NhanVienEntity, LocalDate> ngaySinhCol;
    @FXML
    private TableColumn<NhanVienEntity, String> cccdCol;
    @FXML
    private TableColumn<NhanVienEntity, String> emailCol;
    @FXML
    private TableColumn<NhanVienEntity, String> sdtCol;
    @FXML
    private TableColumn<NhanVienEntity, LocalDate> ngayBatDauLamViecCol;
    @FXML
    private TableColumn<NhanVienEntity, VaiTroNhanVien> vaiTroCol;
    @FXML
    private TableColumn<NhanVienEntity, TrangThaiLamViec> trangThaiCol;
    //service
    private final INhanVienService nhanVienService = new NhanVienServiceImpl();

    @FXML
    public void initialize() {
        maNVCol.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        tenNVCol.setCellValueFactory(new PropertyValueFactory<>("tenNV"));
        gioiTinhCol.setCellValueFactory(new PropertyValueFactory<>("gioiTinh"));
        ngaySinhCol.setCellValueFactory(new PropertyValueFactory<>("ngaySinh"));
        cccdCol.setCellValueFactory(new PropertyValueFactory<>("cccd"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        sdtCol.setCellValueFactory(new PropertyValueFactory<>("sdt"));
        ngayBatDauLamViecCol.setCellValueFactory(new PropertyValueFactory<>("ngayBatDauLamViec"));
        vaiTroCol.setCellValueFactory(new PropertyValueFactory<>("vaiTro"));
        trangThaiCol.setCellValueFactory(new PropertyValueFactory<>("trangThaiLamViec"));
        trangThaiCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(TrangThaiLamViec item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLabel());
                }
            }
        });
        ngaySinhCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(TauGaUtils.DateTimeUtils.convertLocalDateToString(item, TauGaUtils.FORMATTER_DATE));
                }
            }
        });
        ngayBatDauLamViecCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(TauGaUtils.DateTimeUtils.convertLocalDateToString(item, TauGaUtils.FORMATTER_DATE));
                }
            }
        });
    }

    @FXML
    public void onTraCuu(ActionEvent actionEvent) {
        String maNV = txtMaNhanVien.getText().trim();
        String sdt = txtSoDienThoai.getText().trim();
        String cccd = txtCCCD.getText().trim();

        if (maNV.isEmpty() && sdt.isEmpty() && cccd.isEmpty()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION,"Vui lòng nhập mã nhân viên!","Vui lòng nhập mã nhân viên!");
            return;
        }
        NhanVienEntity nv = nhanVienService.timKiemNhanVien(
                maNV.isEmpty() ? null : maNV,
                sdt.isEmpty() ? null : sdt,
                cccd.isEmpty() ? null : cccd);
        tableNhanVien.getItems().clear();

        if (nv == null) {
            GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION,"Không tìm thấy nhân viên!","Không tìm thấy nhân viên!");
            return;
        }
        if (!maNV.isEmpty() && !nv.getMaNV().equals(maNV)) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                    "Mã nhân viên không khớp với dữ liệu!",
                    "Mã nhân viên không khớp với dữ liệu!");
            return;
        }

        if (!sdt.isEmpty() && !nv.getSdt().equals(sdt)) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                    "Số điện thoại không khớp với dữ liệu!",
                    "Số điện thoại không khớp với dữ liệu!");
            return;
        }

        if (!cccd.isEmpty() && !nv.getCccd().equals(cccd)) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                    "CCCD không khớp với dữ liệu!",
                    "CCCD không khớp với dữ liệu!");
            return;
        }

        tableNhanVien.getItems().add(nv);
    }

    @FXML
    public void lamMoi(ActionEvent actionEvent) {
        txtMaNhanVien.clear();
        txtCCCD.clear();
        txtSoDienThoai.clear();
        tableNhanVien.getItems().clear();

    }
}

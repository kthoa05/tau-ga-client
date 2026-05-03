package controller.ql.nhanvien;

import dto.PhanCongDTO;
import entity.CaLamViecEntity;
import entity.NhanVienEntity;
import entity.enums.TrangThaiLamViec;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import service.IChiTietCaLamService;
import service.impl.ChiTietCaLamViecServiceImpl;
import utils.GiaoDienUtils;
import utils.TauGaUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

public class PhanCongCaLamViec {
    @FXML
    public TextField txtMaNV;
    @FXML
    public TextField txtTenNV;
    @FXML
    public DatePicker dpNgayLam;
    @FXML
    public ComboBox<TrangThaiLamViec> cbTrangThai;
    @FXML
    public TextField txtGioBatDau;
    @FXML
    public TextField txtGioKetThuc;
    @FXML
    public TextField txtMaCa;
    @FXML
    public Button btnCapNhat;
    @FXML
    public Button btnXoaTrang;
    @FXML
    public TableView<PhanCongDTO> tblPhanCong;
    @FXML
    public TableColumn<PhanCongDTO, String> colMaNV;
    @FXML
    public TableColumn<PhanCongDTO, String> colMaCa;
    @FXML
    public TableColumn<PhanCongDTO, String> colTenNV;
    @FXML
    public TableColumn<PhanCongDTO, LocalDate> colNgayLam;
    @FXML
    public TableColumn<PhanCongDTO, LocalTime> colGioBatDau;
    @FXML
    public TableColumn<PhanCongDTO, LocalTime> colGioKetThuc;
    @FXML
    public TableColumn<PhanCongDTO, TrangThaiLamViec> colTrangThai;
    public Button btnThem;

    private ObservableList<PhanCongDTO> phanCongList;
    private final IChiTietCaLamService chiTietCaLamService = new ChiTietCaLamViecServiceImpl();


    @FXML
    public void initialize() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        colTenNV.setCellValueFactory(new PropertyValueFactory<>("tenNV"));
        colMaCa.setCellValueFactory(new PropertyValueFactory<>("maCa"));
        colNgayLam.setCellValueFactory(new PropertyValueFactory<>("ngayLam"));

        colNgayLam.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });        dpNgayLam.setConverter(new StringConverter<LocalDate>() {
            String pattern = "dd/MM/yyyy";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        colGioBatDau.setCellValueFactory(new PropertyValueFactory<>("gioBatDau"));
        colGioBatDau.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.format(TauGaUtils.FORMATTER_TIME));
            }
        });
        colGioKetThuc.setCellValueFactory(new PropertyValueFactory<>("gioKetThuc"));
        colGioKetThuc.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.format(TauGaUtils.FORMATTER_TIME));
            }
        });

        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(TrangThaiLamViec item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setText(null);
                else
                    setText(item.getLabel());
            }
        });

        ObservableList<TrangThaiLamViec> trangThaiList = FXCollections.observableArrayList(
                TrangThaiLamViec.values()
        );
        cbTrangThai.setItems(trangThaiList);
        cbTrangThai.setConverter(new StringConverter<TrangThaiLamViec>() {
            @Override
            public String toString(TrangThaiLamViec trangThai) {
                return trangThai != null ? trangThai.getLabel() : "";
            }

            @Override
            public TrangThaiLamViec fromString(String label) {
                return Arrays.stream(TrangThaiLamViec.values())
                        .filter(tt -> tt.getLabel().equals(label))
                        .findFirst()
                        .orElse(null);
            }
        });
        loadData();
        chonRow();
    }

    public void themCa(ActionEvent actionEvent) {
        try {
            String maNV = txtMaNV.getText().trim();
            String tenNV = txtTenNV.getText().trim();
            LocalDate ngayLam = dpNgayLam.getValue();
            String gioBDStr = txtGioBatDau.getText().trim();
            String gioKTStr = txtGioKetThuc.getText().trim();
            TrangThaiLamViec trangThai = cbTrangThai.getValue();
            if (maNV.isEmpty() || tenNV.isEmpty() || ngayLam == null ||
                    gioBDStr.isEmpty() || gioKTStr.isEmpty() ) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ Mã NV, Tên NV, Ngày làm, Giờ bắt đầu và Giờ kết thúc.");
                return;
            }
            LocalTime gioBD;
            LocalTime gioKT;
            try {
                gioBD = LocalTime.parse(gioBDStr, TauGaUtils.FORMATTER_TIME);
                gioKT = LocalTime.parse(gioKTStr, TauGaUtils.FORMATTER_TIME);
            } catch (Exception e) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi Định Dạng", "Giờ Bắt đầu/Kết thúc không hợp lệ. Vui lòng nhập theo định dạng HH:mm (ví dụ: 08:00).");
                return;
            }
            if (gioKT.isBefore(gioBD)) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi Logic", "Giờ kết thúc phải lớn hơn Giờ bắt đầu.");
                return;
            }
            NhanVienEntity nv = chiTietCaLamService.timNhanVienTheoMa(maNV);
            if (nv == null) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi Nghiệp vụ", "Mã nhân viên không tồn tại.");
                return;
            }

            String maCa = chiTietCaLamService.taoMaCa(ngayLam);
            txtMaCa.setText(maCa);

            if (chiTietCaLamService.kiemTraCaTonTai(maCa)) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Ca đã tồn tại", "Ca làm việc cho ngày " + ngayLam.format(TauGaUtils.FORMATTER_DATE) + " đã được phân công. Vui lòng cập nhật thay vì thêm mới.");
                return;
            }
            CaLamViecEntity ca = new CaLamViecEntity(
                    maCa,
                    ngayLam,
                    gioBD,
                    gioKT
            );
            boolean ok = chiTietCaLamService.themPhanCong(ca, maNV);
            if (ok) {
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thành công", "Thêm ca làm việc thành công!");
                PhanCongDTO dto = new PhanCongDTO(
                        maNV,
                        tenNV,
                        maCa,
                        ngayLam,
                        gioBD,
                        gioKT,
                        trangThai
                );

                phanCongList.add(dto);
                tblPhanCong.setItems(phanCongList);

                xoaTrang(null);
            } else {
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        try {
            phanCongList = FXCollections.observableArrayList(
                    chiTietCaLamService.getAllPhanCong()
            );
            tblPhanCong.setItems(phanCongList);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void chonRow() {
        tblPhanCong.setOnMouseClicked(event -> {
            PhanCongDTO pc = tblPhanCong.getSelectionModel().getSelectedItem();
            if (pc != null) {
                txtMaNV.setText(pc.getMaNV());
                txtTenNV.setText(pc.getTenNV());
                txtMaCa.setText(pc.getMaCa());
                dpNgayLam.setValue(pc.getNgayLam());
                if (pc.getGioBatDau() != null)
                    txtGioBatDau.setText(pc.getGioBatDau().format(TauGaUtils.FORMATTER_TIME));
                if (pc.getGioKetThuc() != null)
                    txtGioKetThuc.setText(pc.getGioKetThuc().format(TauGaUtils.FORMATTER_TIME));

                cbTrangThai.setValue(pc.getTrangThai());
            }
        });
    }

    public void capNhat(ActionEvent actionEvent) {
        try {
            PhanCongDTO pc = tblPhanCong.getSelectionModel().getSelectedItem();
            if (pc == null) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING,
                        "Chưa chọn dòng", "Vui lòng chọn một phân công để cập nhật.");
                return;
            }

            String maNV = txtMaNV.getText().trim();
            String tenNV = txtTenNV.getText().trim();
            LocalDate ngayLamMoi = dpNgayLam.getValue();
            String gioBDStr = txtGioBatDau.getText().trim();
            String gioKTStr = txtGioKetThuc.getText().trim();
            TrangThaiLamViec trangThai = cbTrangThai.getValue();

            String maCaCu = pc.getMaCa();
            LocalDate ngayLamCu = pc.getNgayLam();

            if (maNV.isEmpty() || tenNV.isEmpty() || ngayLamMoi == null ||
                    gioBDStr.isEmpty() || gioKTStr.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING,
                        "Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin.");
                return;
            }

            LocalTime gioBD, gioKT;
            try {
                gioBD = LocalTime.parse(gioBDStr, TauGaUtils.FORMATTER_TIME);
                gioKT = LocalTime.parse(gioKTStr, TauGaUtils.FORMATTER_TIME);
            } catch (Exception e) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                        "Lỗi định dạng", "Giờ không hợp lệ. Vui lòng nhập HH:mm.");
                return;
            }

            if (gioKT.isBefore(gioBD)) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                        "Lỗi logic", "Giờ kết thúc phải sau giờ bắt đầu!");
                return;
            }

            NhanVienEntity nv = chiTietCaLamService.timNhanVienTheoMa(maNV);
            if (nv == null) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                        "Sai mã NV", "Mã nhân viên không tồn tại!");
                return;
            }

            boolean ok = false;
            String maCaMoi = maCaCu;

            if (!ngayLamMoi.isEqual(ngayLamCu)) {
                maCaMoi = chiTietCaLamService.taoMaCa(ngayLamMoi);
                txtMaCa.setText(maCaMoi);

                if (chiTietCaLamService.kiemTraCaTonTai(maCaMoi)) {
                    GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                            "Trùng Mã Ca", "Mã ca mới đã tồn tại. Vui lòng chọn ngày khác.");
                    return;
                }

                boolean deletedCT = chiTietCaLamService.deleteChiTietCaLam(maCaCu);
                boolean deletedCLV = chiTietCaLamService.deleteCaLamViec(maCaCu);

                if (!deletedCT || !deletedCLV) {
                    GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                            "Lỗi Xóa", "Không thể xóa ca làm việc cũ để thay đổi ngày.");
                    loadData();
                    return;
                }

                CaLamViecEntity caMoi = new CaLamViecEntity(maCaMoi, ngayLamMoi, gioBD, gioKT);
                boolean insertedCLV = chiTietCaLamService.insertCaLamViec(caMoi);
                boolean insertedCT = chiTietCaLamService.insertChiTietCaLam(maCaMoi, maNV);

                ok = insertedCLV && insertedCT;

            } else {
                CaLamViecEntity caCapNhat = new CaLamViecEntity(maCaCu, ngayLamMoi, gioBD, gioKT);
                ok = chiTietCaLamService.capNhatPhanCong(caCapNhat, maNV);
            }

            if (!ok) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                        "Lỗi cập nhật", "Không thể cập nhật ca làm!");
                return;
            }

            PhanCongDTO pcMoi = new PhanCongDTO(maNV, tenNV, maCaMoi, ngayLamMoi, gioBD, gioKT, trangThai);

            int index = tblPhanCong.getSelectionModel().getSelectedIndex();
            phanCongList.set(index, pcMoi);

            tblPhanCong.refresh();

            GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION,
                    "Thành công", "Cập nhật phân công thành công.");

            xoaTrang(null);

        } catch (Exception e) {
            e.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                    "Lỗi hệ thống", "Đã xảy ra lỗi khi cập nhật phân công.");
        }
    }
    public void xoaTrang(ActionEvent actionEvent) {
        txtMaNV.clear();
        txtTenNV.clear();
        txtMaCa.clear();
        txtGioBatDau.clear();
        txtGioKetThuc.clear();
        dpNgayLam.setValue(null);
        cbTrangThai.setValue(null);
    }

}

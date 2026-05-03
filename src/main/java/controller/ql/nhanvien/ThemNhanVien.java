package controller.ql.nhanvien;

import entity.NhanVienEntity;
import entity.enums.GioiTinh;
import entity.enums.TrangThaiLamViec;
import entity.enums.VaiTroNhanVien;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.util.StringConverter;
import service.INhanVienService;
import service.impl.NhanVienServiceImpl;
import utils.GiaoDienUtils;
import utils.TauGaUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;

public class ThemNhanVien {
    @FXML
    public TextField txtMaNhanVien;
    @FXML
    public TextField txtTenNhanVien;
    @FXML
    public RadioButton rbNam;
    @FXML
    public RadioButton rbNu;
    @FXML
    public DatePicker dpNgaySinh;
    @FXML
    public TextField txtCCCD;
    @FXML
    public TextField txtEmail;
    @FXML
    public TextField txtSdt;
    @FXML
    public ComboBox<VaiTroNhanVien> cbVaiTro;
    @FXML
    public DatePicker dpNgayBatDauLamViec;
    @FXML
    public ComboBox<TrangThaiLamViec> cbTrangThaiLamViec;

    @FXML
    public TableView<NhanVienEntity> tableNhanVien;
    @FXML
    public TableColumn<NhanVienEntity, String> maNVCol;
    @FXML
    public TableColumn<NhanVienEntity, String> tenNVCol;
    @FXML
    public TableColumn<NhanVienEntity, GioiTinh> gioiTinhCol;
    @FXML
    public TableColumn<NhanVienEntity, LocalDate> ngaySinhCol;
    @FXML
    public TableColumn<NhanVienEntity, String> cccdCol;
    @FXML
    public TableColumn<NhanVienEntity, String> emailCol;
    @FXML
    public TableColumn<NhanVienEntity, String> sdtCol;
    @FXML
    public TableColumn<NhanVienEntity, LocalDate> ngayBatDauLamViecCol;
    @FXML
    public TableColumn<NhanVienEntity, VaiTroNhanVien> vaiTroCol;
    @FXML
    public TableColumn<NhanVienEntity, TrangThaiLamViec> trangThaiCol;

    private final INhanVienService nhanVienService = new NhanVienServiceImpl();
    public Button btnChonAnh;
    public ImageView imgNhanVien;
    private File fileAnhDaChon;

    private static final String FOLDER_LUU_ANH = "src/main/resources/img/nhanvien/";
    private static final String RESOURCE_PATH = "/img/nhanvien/";
    private static final String ANH_MAC_DINH = "/tauga/img/account.png";

    @FXML
    public void initialize() {
        cbVaiTro.setItems(FXCollections.observableArrayList(VaiTroNhanVien.values()));
        cbVaiTro.setConverter(new StringConverter<VaiTroNhanVien>() {
            @Override
            public String toString(VaiTroNhanVien vaiTro) {
                return vaiTro == null ? null : vaiTro.getTen();
            }

            @Override
            public VaiTroNhanVien fromString(String string) {
                return VaiTroNhanVien.findByValue(Integer.parseInt(string));
            }
        });
        cbTrangThaiLamViec.setItems(FXCollections.observableArrayList(TrangThaiLamViec.values()));
        cbTrangThaiLamViec.setConverter(new StringConverter<TrangThaiLamViec>() {
            @Override
            public String toString(TrangThaiLamViec trangThai) {
                return trangThai == null ? null : trangThai.getLabel(); // Hiển thị nhãn tiếng Việt
            }

            @Override
            public TrangThaiLamViec fromString(String string) {
                return TrangThaiLamViec.findByValue(Integer.parseInt(string));
            }
        });
        ToggleGroup groupGioiTinh = new ToggleGroup();
        rbNam.setToggleGroup(groupGioiTinh);
        rbNu.setToggleGroup(groupGioiTinh);
        cbVaiTro.getSelectionModel().selectFirst();
        cbTrangThaiLamViec.getSelectionModel().selectFirst();

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
        gioiTinhCol.setCellFactory(tc -> new TableCell<NhanVienEntity, GioiTinh>() {
            @Override
            protected void updateItem(GioiTinh item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : (item.equals(GioiTinh.NAM) ? "Nam" : "Nữ"));
            }
        });
        tableNhanVien.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                hienThiThongTinNhanVien(newSelection);
            }
        });

        vaiTroCol.setCellFactory(tc -> new TableCell<NhanVienEntity, VaiTroNhanVien>() {
            @Override
            protected void updateItem(VaiTroNhanVien item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getTen());
            }
        });

        trangThaiCol.setCellFactory(tc -> new TableCell<NhanVienEntity, TrangThaiLamViec>() {
            @Override
            protected void updateItem(TrangThaiLamViec item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });

        ngaySinhCol.setCellFactory(tc -> new TableCell<NhanVienEntity, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.toString());
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

        StringConverter<LocalDate> dateConverter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return TauGaUtils.DateTimeUtils.convertLocalDateToString(date, TauGaUtils.FORMATTER_DATE);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return TauGaUtils.DateTimeUtils.convertStringToLocalDate1(string);
                    } catch (Exception e) {
                        return null;
                    }
                } else {
                    return null;
                }
            }
        };
        dpNgaySinh.setConverter(dateConverter);
        dpNgayBatDauLamViec.setConverter(dateConverter);
        dpNgaySinh.setPromptText("dd/MM/yyyy");
        dpNgayBatDauLamViec.setPromptText("dd/MM/yyyy");

        txtMaNhanVien.setEditable(false);
        txtMaNhanVien.setDisable(true);
        hienThiMaNhanVienMoi();
    }

    private void hienThiMaNhanVienMoi() {
        String maMoi = nhanVienService.sinhMaNhanVienMoi();
        txtMaNhanVien.setText(maMoi);
    }

    public void onThemNhanVien(ActionEvent actionEvent) {
        try {
            String maNV = txtMaNhanVien.getText().trim();
            String tenNV = txtTenNhanVien.getText().trim();
            LocalDate ngaySinh = dpNgaySinh.getValue();
            String cccd = txtCCCD.getText().trim();
            String email = txtEmail.getText().trim();
            String sdt = txtSdt.getText().trim();
            LocalDate ngayBatDauLamViec = dpNgayBatDauLamViec.getValue();
            VaiTroNhanVien vaiTro = cbVaiTro.getValue();
            TrangThaiLamViec trangThai = cbTrangThaiLamViec.getValue();

            GioiTinh gioiTinh = null;
            if (rbNam.isSelected()) {
                gioiTinh = GioiTinh.NAM;
            } else if (rbNu.isSelected()) {
                gioiTinh = GioiTinh.NU;
            }

            if (maNV.isEmpty() || tenNV.isEmpty() || gioiTinh == null || ngaySinh == null || cccd.isEmpty() || sdt.isEmpty() || ngayBatDauLamViec == null || vaiTro == null || trangThai == null) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng nhập đầy đủ thông tin");
                return;
            }
            String duongDanAnh = luuAnhNhanVien(maNV);
            NhanVienEntity nv = new NhanVienEntity();
            nv.setMaNV(maNV);
            nv.setTenNV(tenNV);
            nv.setAnh(duongDanAnh);
            nv.setGioiTinh(gioiTinh);
            nv.setNgaySinh(ngaySinh);
            nv.setCccd(cccd);
            nv.setEmail(email.isEmpty() ? null : email);
            nv.setSdt(sdt);
            nv.setNgayBatDauLamViec(ngayBatDauLamViec.atStartOfDay().toLocalDate());
            nv.setVaiTro(vaiTro);
            nv.setTrangThaiLamViec(trangThai);
            boolean success = nhanVienService.themNhanVien(nv);

            if (success) {
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION,"Thêm nhân viên thành công!","Thêm nhân viên thành công!");
                hienThiDanhSachNhanVien(null);
                onLamMoi(null);
            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,"Thêm nhân viên thất bại. Có thể Mã NV đã tồn tại.","Thêm nhân viên thất bại. Có thể Mã NV đã tồn tại");
            }

        } catch (Exception e) {
            e.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,"Lỗi hệ thống khi thêm nhân viên: ","Lỗi hệ thống khi thêm nhân viên");
        }
    }

    public void onLamMoi(ActionEvent actionEvent) {
        txtMaNhanVien.clear();
        txtTenNhanVien.clear();
        rbNam.setSelected(false);
        rbNu.setSelected(false);
        dpNgaySinh.setValue(null);
        txtCCCD.clear();
        txtEmail.clear();
        txtSdt.clear();
        dpNgayBatDauLamViec.setValue(null);
        cbVaiTro.getSelectionModel().selectFirst();
        cbTrangThaiLamViec.getSelectionModel().selectFirst();

        if (tableNhanVien != null) {
            tableNhanVien.getItems().clear();
        }

        setAnhMacDinh();
        fileAnhDaChon = null;

        hienThiMaNhanVienMoi();
    }


    public void hienThiDanhSachNhanVien(ActionEvent actionEvent) {
        try {
            List<NhanVienEntity> danhSach = nhanVienService.layTatCaNhanVien();
            tableNhanVien.getItems().clear();

            if (danhSach != null && !danhSach.isEmpty()) {
                ObservableList<NhanVienEntity> observableList = FXCollections.observableArrayList(danhSach);
                tableNhanVien.setItems(observableList);
            } else {
                GiaoDienUtils.showThongBao( Alert.AlertType.WARNING, "Không tìm thấy nhân viên nào trong cơ sở dữ liệu.","Không tìm thấy nhân viên nào trong cơ sở dữ liệu");
            }

        } catch (Exception e) {
            e.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi khi tải danh sách nhân viên: ","Lỗi khi tải danh sách nhân viên!");
        }

    }

    private void hienThiThongTinNhanVien(NhanVienEntity nv) {
        if (nv == null) return;

        txtMaNhanVien.setText(nv.getMaNV());
        txtTenNhanVien.setText(nv.getTenNV());
        dpNgaySinh.setValue(nv.getNgaySinh());
        txtCCCD.setText(nv.getCccd());
        txtEmail.setText(nv.getEmail());
        txtSdt.setText(nv.getSdt());

        if (nv.getNgayBatDauLamViec() != null) {
            dpNgayBatDauLamViec.setValue(nv.getNgayBatDauLamViec().atStartOfDay().toLocalDate());
        } else {
            dpNgayBatDauLamViec.setValue(null);
        }

        if (nv.getGioiTinh() != null) {
            if (nv.getGioiTinh().equals(GioiTinh.NAM)) {
                rbNam.setSelected(true);
                rbNu.setSelected(false);
            } else if (nv.getGioiTinh().equals(GioiTinh.NU)) {
                rbNam.setSelected(false);
                rbNu.setSelected(true);
            }
        } else {
            rbNam.setSelected(false);
            rbNu.setSelected(false);
        }

        cbVaiTro.getSelectionModel().select(nv.getVaiTro());
        cbTrangThaiLamViec.getSelectionModel().select(nv.getTrangThaiLamViec());
        try {
            if (nv.getAnh() != null && !nv.getAnh().isEmpty()) {
                var url = getClass().getResource(nv.getAnh());

                if (url != null) {
                    imgNhanVien.setImage(new Image(url.toExternalForm()));
                } else {
                    String tenFile = nv.getAnh().substring(nv.getAnh().lastIndexOf("/") + 1);
                    File fileVatLy = new File(FOLDER_LUU_ANH + tenFile);

                    if (fileVatLy.exists()) {
                        imgNhanVien.setImage(new Image(fileVatLy.toURI().toString()));
                    } else {
                        setAnhMacDinh();
                    }
                }
            } else {
                setAnhMacDinh();
            }
        } catch (Exception e) {
            setAnhMacDinh();
        }
    }

    private void setAnhMacDinh() {
        try {
            var urlMacDinh = getClass().getResource(ANH_MAC_DINH);
            if (urlMacDinh != null) {
                imgNhanVien.setImage(new Image(urlMacDinh.toExternalForm()));
            } else {
                imgNhanVien.setImage(null);
            }
        } catch (Exception e) {
            imgNhanVien.setImage(null);
        }
    }

    public void onChonAnh(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Chọn ảnh nhân viên");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg")
        );

        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            fileAnhDaChon = file;
            Image image = new Image(file.toURI().toString());
            imgNhanVien.setImage(image);
        }
    }
    private String luuAnhNhanVien(String maNV) {
        if (fileAnhDaChon == null) return null;

        try {
            File dir = new File(FOLDER_LUU_ANH);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String tenFile = fileAnhDaChon.getName();
            String duoiFile = tenFile.substring(tenFile.lastIndexOf("."));
            String tenFileMoi = maNV + duoiFile;
            File fileDich = new File(FOLDER_LUU_ANH + tenFileMoi);

            Files.copy(fileAnhDaChon.toPath(), fileDich.toPath(), StandardCopyOption.REPLACE_EXISTING);

            return RESOURCE_PATH + tenFileMoi;
        } catch (Exception e) {
            System.err.println("Lỗi lưu file: " + e.getMessage());
            return null;
        }
    }

}

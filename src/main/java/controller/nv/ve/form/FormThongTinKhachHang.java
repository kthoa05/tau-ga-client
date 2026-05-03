package controller.nv.ve.form;

import com.jfoenix.controls.JFXComboBox;
import dto.*;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.DatVeRequest;
import network.common.request.KhachHangRequest;
import network.common.request.KhuyenMaiRequest;
import network.common.request.LoaiVeRequest;
import utils.GiaoDienUtils;
import utils.TauGaUtils;
import utils.consts.CurrentUser;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class FormThongTinKhachHang {
    //panel
    @FXML
    public VBox pnDanhSachVe;
    @FXML
    public ScrollPane pnScrollPaneDanhSachVe;
    public VBox pnThongTinVeTau;
    public Button btnDatVe;
    @FXML
    private VBox pnChiTietHoaDon;
    @FXML
    private Button btnXacNhanDatVe;

    //text field thong tin kh
    @FXML
    private TextField txtHoTenKH;
    @FXML
    private TextField txtSDTKH;
    @FXML
    private TextField txtCCCDKH;
    @FXML
    private DatePicker dpNgaySinhKH;

    //txt cthd + hd
    @FXML
    private TextField txtTenKHHD;
    @FXML
    private TextField txtNhanVien;
    @FXML
    private TextField txtSdtHD;
    @FXML
    private TextField txtNgayLapHD;
    @FXML
    private TextField txtThanhTien;
    @FXML
    private TextField txtMaKhuyenMaiCTHD;
    @FXML
    private TextField txtSoTienGiamGia;
    @FXML
    private TextField txtTongTien;

    private Map<GheDTO, ThongTinDatVeDTO> mapVeTheoGhe = new HashMap<>();
    private final SocketClient socketClient = new SocketClient();

    //tmp
    private List<ThongTinDatVeDTO> danhSachThongTinVe = new ArrayList<>();
    private ThongTinChuyenTauDTO thongTinChuyenTauDaChon;
    private List<GheDTO> thongTinCacGheDaChon;
    private List<String> danhSachLoaiVe = new ArrayList<>();

    //callback
    private Consumer<Boolean> datVeListener;
    private Consumer<Boolean> xacNhanDatVeListener;

    private List<RadioButton> danhSachRad = new ArrayList<>();
    private List<GridPane> danhSachGrid = new ArrayList<>();

    @FXML
    public void initialize() {
        // Ẩn hóa đơn
        pnChiTietHoaDon.setVisible(false);
        pnChiTietHoaDon.setManaged(false);

        btnXacNhanDatVe.setVisible(false);
        btnXacNhanDatVe.setManaged(false);

        // Ẩn danh sách vé nếu FXML để visible=false
        pnScrollPaneDanhSachVe.setVisible(true);
        pnScrollPaneDanhSachVe.setManaged(true);
        pnDanhSachVe.setVisible(true);
        pnDanhSachVe.setManaged(true);
        taiDuLieuKhoiTao();

//        setupNameValidation(txtHoTenKH);
//        setupDateValidation(dpNgaySinhKH);
//        setupValidationAndClear(txtSDTKH, "^0[35789][0-9]{8}$", "Số điện thoại không hợp lệ (10 số, đầu số VN)");
//        setupValidationAndClear(txtCCCDKH, "^[0-9]{12}$", "CCCD không hợp lệ (phải đúng 12 chữ số)");
    }

    public void setDatVeListener(Consumer<Boolean> listener) {
        this.datVeListener = listener;
    }

    public void setXacNhanDatVeListener(Consumer<Boolean> listener) {
        this.xacNhanDatVeListener = listener;
    }

    public void setDanhSachThongTinVe(ThongTinChuyenTauDTO thongTinChuyenTauDaChon, List<GheDTO> thongTinCacGheDaChon) {
        this.thongTinChuyenTauDaChon = thongTinChuyenTauDaChon;
        this.thongTinCacGheDaChon = thongTinCacGheDaChon;

        pnScrollPaneDanhSachVe.setVisible(true);
        pnScrollPaneDanhSachVe.setManaged(true);
        pnDanhSachVe.setVisible(true);
        pnDanhSachVe.setManaged(true);
        pnDanhSachVe.getChildren().clear();
    }

    public void hienThiDanhSachVe(List<GheDTO> thongTinCacGheDaChon) {
        if (pnDanhSachVe == null) return;

        if (pnThongTinVeTau != null) {
            pnThongTinVeTau.setVisible(true);
            pnThongTinVeTau.setManaged(true);
        }
        if (btnDatVe != null) {
            btnDatVe.setVisible(true);
            btnDatVe.setManaged(true);
        }
        pnDanhSachVe.getChildren().clear();
        if (pnScrollPaneDanhSachVe != null) {
            pnScrollPaneDanhSachVe.setVisible(true);
            pnScrollPaneDanhSachVe.setManaged(true);
        }
        pnDanhSachVe.setVisible(true);
        pnDanhSachVe.setManaged(true);

        AtomicInteger index = new AtomicInteger(1);

        mapVeTheoGhe.clear();
        danhSachThongTinVe.clear();
        thongTinCacGheDaChon.forEach(gheDaDat -> {
            ThongTinDatVeDTO veMoi = new ThongTinDatVeDTO();
            veMoi.setGhe(gheDaDat);
            veMoi.setGiaVe(gheDaDat.getGiaVeCuoi());
            mapVeTheoGhe.put(gheDaDat, veMoi);

            Label lblTieuDe = new Label("VÉ SỐ " + index.getAndIncrement());
            lblTieuDe.setStyle("-fx-font-weight: bold; -fx-text-fill: gray; -fx-padding: 10 0 5 0; -fx-border-width: 0 0 1 0; -fx-border-color: #eee;");
            RadioButton radDienNhanh = new RadioButton("Điền nhanh thông tin");
            radDienNhanh.setStyle("-fx-font-weight: bold; -fx-padding: 10 0 5 0;");

            danhSachRad.add(radDienNhanh);


            radDienNhanh.selectedProperty().addListener((obs, oldVal, newVal) -> {
                xuLyChonDienNhanh(radDienNhanh, gheDaDat, newVal);
            });


            HBox tieuDe = new HBox(400);
            tieuDe.getChildren().addAll(lblTieuDe, radDienNhanh);

            GridPane gridVe = taoGridPaneLayout(gheDaDat);
            gridVe.setUserData(veMoi);
            danhSachGrid.add(gridVe);

            pnDanhSachVe.getChildren().addAll(tieuDe, gridVe);
        });
    }

    private void xuLyChonDienNhanh(RadioButton radDuocChon, GheDTO gheDaChon, boolean duocChon) {
        int index = danhSachRad.indexOf(radDuocChon);
        if (index < 0) return;

        if (duocChon) {
            // disable các radio khác
            for (RadioButton rad : danhSachRad) {
                if (rad != radDuocChon) {
                    rad.setDisable(true);
                }
            }

            // Lấy GridPane từ danh sách đã lưu thay vì tính toán index
            if (index >= 0 && index < danhSachGrid.size()) {
                Node node = danhSachGrid.get(index);
                if (node instanceof GridPane grid) {
                    //kh
                    KhachHangDTO thongTinKHDatVe;
                    if (CurrentUser.getKhachHangDTO() == null) {
                        thongTinKHDatVe = new KhachHangDTO();
                        thongTinKHDatVe.setTenKH(txtHoTenKH.getText());
                        thongTinKHDatVe.setCccd(txtCCCDKH.getText());
                        thongTinKHDatVe.setSoDienThoai(txtSDTKH.getText());
                        thongTinKHDatVe.setNgaySinh(dpNgaySinhKH.getValue());

                        CurrentUser.setKhachHangDTO(thongTinKHDatVe);
                        CurrentUser.setDaTonTaiKH(Boolean.FALSE);
                    } else {
                        thongTinKHDatVe = CurrentUser.getKhachHangDTO();
                    }

                    ((TextField) grid.lookup("#txtHanhKhach")).setText(thongTinKHDatVe.getTenKH());
                    ((TextField) grid.lookup("#txtCCCD")).setText(thongTinKHDatVe.getCccd());
                    ((TextField) grid.lookup("#txtSDT")).setText(thongTinKHDatVe.getSoDienThoai());
                    ((DatePicker) grid.lookup("#txtNgaySinh")).setValue(thongTinKHDatVe.getNgaySinh());

//                    ComboBox cbLoaiVe = (ComboBox) grid.lookup("#cbLoaiVe");
                    JFXComboBox cbLoaiVe = (JFXComboBox) grid.lookup("#cbLoaiVe");
                    Platform.runLater(() -> {
                        Node line = cbLoaiVe.lookup(".input-line");
                        Node focusLine = cbLoaiVe.lookup(".input-focused-line");
                        if (line != null) line.setStyle("-fx-opacity:0; -fx-pref-height:0;");
                        if (focusLine != null) focusLine.setStyle("-fx-opacity:0; -fx-pref-height:0;");
                    });
                    TextField txtKhuyenMai = (TextField) grid.lookup("#txtSoTienKM");
                    TextField txtGiaVeGoc = (TextField) grid.lookup("#txtGiaVeGoc");
                    TextField txtGiaVeCuoi = (TextField) grid.lookup("#txtGiaVeCuoi");

                    this.handleCbbLoaiVe(cbLoaiVe, thongTinKHDatVe.getNgaySinh(), txtKhuyenMai, gheDaChon, txtGiaVeGoc, txtGiaVeCuoi);
                }
            } else {
                System.out.println("index không hợp lệ: " + index);
            }

        } else {
            // enable lại tất cả radio khi bỏ chọn
            for (RadioButton rad : danhSachRad) {
                rad.setDisable(false);
            }
            // Xóa thông tin trong GridPane tương ứng
            if (index >= 0 && index < danhSachGrid.size()) {
                Node node = danhSachGrid.get(index);
                if (node instanceof GridPane grid) {
                    ((TextField) grid.lookup("#txtHanhKhach")).clear();
                    ((TextField) grid.lookup("#txtCCCD")).clear();
                    ((TextField) grid.lookup("#txtSDT")).clear();
                    ((DatePicker) grid.lookup("#txtNgaySinh")).setValue(null);
                    JFXComboBox cbLoaiVe = (JFXComboBox) grid.lookup("#cbLoaiVe");
                    if (cbLoaiVe != null) cbLoaiVe.getSelectionModel().clearSelection();
                    ((TextField) grid.lookup("#txtSoTienKM")).clear();
                    ((TextField) grid.lookup("#txtGiaVeGoc")).clear();
                    ((TextField) grid.lookup("#txtGiaVeCuoi")).clear();
                }
            }
        }
    }


    private GridPane taoGridPaneLayout(GheDTO gheDaDat) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(5.0);
        grid.setVgap(10.0);
        grid.setPadding(new Insets(5.0));
        grid.setStyle("-fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-color: white;");
        grid.setMaxWidth(Double.MAX_VALUE);


        ColumnConstraints col1 = new ColumnConstraints(110);
        ColumnConstraints col2 = new ColumnConstraints(210);
        ColumnConstraints col3 = new ColumnConstraints(70);
        ColumnConstraints col4 = new ColumnConstraints(210);
        grid.getColumnConstraints().addAll(col1, col2, col3, col4);

        //row 0
        grid.add(new Label("Hành khách:"), 0, 0);
        TextField txtHanhKhach = new TextField();
//        setupNameValidation(txtHanhKhach);
        txtHanhKhach.setId("txtHanhKhach");
        grid.add(txtHanhKhach, 1, 0);

        grid.add(new Label("SĐT:"), 2, 0);
        TextField txtSDT = new TextField();
//        setupValidationAndClear(txtSDT, "^0[35789][0-9]{8}$", "SĐT vé số " + gheDaDat.getSoGhe() + " không hợp lệ");
        txtSDT.setId("txtSDT");
        grid.add(txtSDT, 3, 0);

        // --- ROW 1 ---
        grid.add(new Label("CCCD:"), 0, 1);
        TextField txtCCCD = new TextField();
//        setupValidationAndClear(txtCCCD, "^[0-9]{12}$", "CCCD vé số " + gheDaDat.getSoGhe() + " không hợp lệ");
        txtCCCD.setId("txtCCCD");
        grid.add(txtCCCD, 1, 1);

        grid.add(new Label("Ngày sinh:"), 2, 1);
        DatePicker txtNgaySinh = new DatePicker();
//        setupNameValidation(txtHanhKhach);
        txtNgaySinh.setId("txtNgaySinh");
        grid.add(txtNgaySinh, 3, 1);

        // --- ROW 2 ---
        grid.add(new Label("Toa-khoang-ghế:"), 0, 2);
        TextField txtToaKhoangGhe = new TextField();
        txtToaKhoangGhe.setId("txtToaKhoangGhe");
        txtToaKhoangGhe.setText(
                gheDaDat.getTenToa() + "-" +
                        gheDaDat.getTenKhoang() + "-" +
                        gheDaDat.getSoGhe());
        CurrentUser.setMaToaTau(gheDaDat.getMaToa());
        CurrentUser.setMaKhoang(gheDaDat.getMaKhoang());
        txtToaKhoangGhe.setDisable(true);
        grid.add(txtToaKhoangGhe, 1, 2);

        grid.add(new Label("Loại ghế:"), 2, 2);
        TextField txtLoaiGhe = new TextField();
        txtLoaiGhe.setId("txtLoaiGhe");
        txtLoaiGhe.setText(gheDaDat.getTenLoaiGhe());
        txtLoaiGhe.setDisable(true);
        grid.add(txtLoaiGhe, 3, 2);

        // --- ROW 3 ---
        grid.add(new Label("Loại vé:"), 0, 3);
        JFXComboBox cbLoaiVe = new JFXComboBox();
        Platform.runLater(() -> {
            cbLoaiVe.lookupAll(".input-line").forEach(node -> node.setStyle("-fx-opacity: 0;"));
            cbLoaiVe.lookupAll(".input-focused-line").forEach(node -> node.setStyle("-fx-opacity: 0;"));
        });
        cbLoaiVe.setStyle("-fx-border-color: #BFBFBF; -fx-border-radius: 3; -fx-background-radius: 3; -jfx-focus-color: #3f51b5; -jfx-unfocus-color: #9e9e9e;");

        cbLoaiVe.setId("cbLoaiVe");
        grid.add(cbLoaiVe, 1, 3);

        grid.add(new Label("Số tiền KM:"), 2, 3);
        TextField txtSoTienKM = new TextField();
        txtSoTienKM.setId("txtSoTienKM");
        grid.add(txtSoTienKM, 3, 3);

        // --- ROW 4 ---
        grid.add(new Label("Giá vé gốc:"), 0, 4);
        TextField txtGiaVeGoc = new TextField();
        txtGiaVeGoc.setId("txtGiaVeGoc");
        txtGiaVeGoc.setDisable(true);
        txtGiaVeGoc.setText(TauGaUtils.NumberUtils.formatNumber(gheDaDat.getGiaVe()));
        grid.add(txtGiaVeGoc, 1, 4);

        grid.add(new Label("Giá vé cuối:"), 2, 4);
        TextField txtGiaVeCuoi = new TextField();
        txtGiaVeCuoi.setId("txtGiaVeCuoi");
        txtGiaVeCuoi.setDisable(true);
        grid.add(txtGiaVeCuoi, 3, 4);
        txtNgaySinh.setOnAction(action -> {
            this.handleCbbLoaiVe(cbLoaiVe, txtNgaySinh.getValue(), txtSoTienKM, gheDaDat, txtGiaVeGoc, txtGiaVeCuoi);
        });
        cbLoaiVe.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                // newVal là đối tượng LoaiVeDTO hoặc String tùy bạn set vào combobox
                String tenLoaiVe = newVal.toString(); // nếu là String
                // hoặc: newVal.getTenLoaiVe() nếu là DTO

                CurrentUser.setTenLoaiVe(tenLoaiVe);
            }
        });

        txtHanhKhach.setMaxWidth(Double.MAX_VALUE);
        txtSDT.setMaxWidth(Double.MAX_VALUE);
        txtCCCD.setMaxWidth(Double.MAX_VALUE);
        txtNgaySinh.setMaxWidth(Double.MAX_VALUE);
        txtToaKhoangGhe.setMaxWidth(Double.MAX_VALUE);
        txtLoaiGhe.setMaxWidth(Double.MAX_VALUE);
        cbLoaiVe.setMaxWidth(Double.MAX_VALUE);
        txtGiaVeGoc.setMaxWidth(Double.MAX_VALUE);


        return grid;
    }

    private void handleCbbLoaiVe(ComboBox cbLoaiVe, LocalDate ngaySinh, TextField txtKhuyenMai, GheDTO gheDaChon, TextField txtGiaVeGoc, TextField txtGiaVeCuoi) {
        if (ngaySinh == null) {
            return;
        }
        if (danhSachLoaiVe.isEmpty()) {
            taiDuLieuKhoiTao();
        }
        LoaiVeInfoDTO loaiVeInfo = layThongTinLoaiVe(ngaySinh, gheDaChon.getGiaVe());
        if (loaiVeInfo == null) {
            return;
        }
        if (cbLoaiVe.getItems().isEmpty()) {
            cbLoaiVe.getItems().addAll(danhSachLoaiVe);
        }
        cbLoaiVe.getSelectionModel().select(loaiVeInfo.getTenLoaiVe());
        txtKhuyenMai.setText(TauGaUtils.NumberUtils.formatNumber(loaiVeInfo.getTienKhuyenMai()));
        txtGiaVeGoc.setText(TauGaUtils.NumberUtils.formatNumber(loaiVeInfo.getGiaVeGoc()));
        txtGiaVeCuoi.setText(TauGaUtils.NumberUtils.formatNumber(loaiVeInfo.getGiaVeCuoi()));
        gheDaChon.setTenLoaiVe(loaiVeInfo.getTenLoaiVe());
        gheDaChon.setGiaVeGoc(loaiVeInfo.getGiaVeGoc());
        gheDaChon.setGiaVeCuoi(loaiVeInfo.getGiaVeCuoi());

        ThongTinDatVeDTO ve = mapVeTheoGhe.get(gheDaChon);
        if (ve != null) {
            ve.setTenLoaiVe(loaiVeInfo.getTenLoaiVe());
            ve.setGiaVe(loaiVeInfo.getGiaVeCuoi());
        }
    }

    public void timThongTinKHTheoSdt() {
        String sdt = txtSDTKH.getText().trim();
        KhachHangDTO khachHang = timKhachHang(sdt, "");

        if (khachHang != null) {
            txtHoTenKH.setText(khachHang.getTenKH());
            txtSDTKH.setText(khachHang.getSoDienThoai());
            txtCCCDKH.setText(khachHang.getCccd());
            dpNgaySinhKH.setValue(khachHang.getNgaySinh());

            CurrentUser.setKhachHangDTO(khachHang);
            CurrentUser.setDaTonTaiKH(Boolean.TRUE);
        }
    }

    public void onDatVe(ActionEvent actionEvent) {
//        if (!validateInputThongTinKH()) {
//            return;
//        }
//
//        // 2. Validate thông tin chi tiết từng vé
//        if (!validateInputThongTinKHTrenVe()) {
//            return;
//        }

        pnChiTietHoaDon.setVisible(true);
        pnChiTietHoaDon.setManaged(true);
        btnXacNhanDatVe.setVisible(true);
        btnXacNhanDatVe.setManaged(true);

        /**
         * 1. validate thoông tin kh
         */
//        validateInputThongTinKH();

        /**
         * 2. Validate thông tin kh trên vé
         */
//        validateInputThongTinKHTrenVe();

        /**
         * 3. Hiện thông tin cthd
         */

        //tong tien dua tren danh sach ve
        double tongTien = thongTinCacGheDaChon.stream()
                .mapToDouble(GheDTO::getGiaVeCuoi)
                .sum();
        CurrentUser.setThanhTien(tongTien);

        txtTenKHHD.setText(txtHoTenKH.getText());
        txtNhanVien.setText(CurrentUser.getNhanVien().getTenNV());
        txtSdtHD.setText(txtSDTKH.getText());
        txtNgayLapHD.setText(TauGaUtils.DateTimeUtils.convertToFormatDateTimeVN(LocalDateTime.now()));
        txtThanhTien.setText(TauGaUtils.NumberUtils.formatNumber(tongTien));

        fillDataForCTHD();

        if (datVeListener != null) datVeListener.accept(true);
    }

    private void fillDataForCTHD() {
        double thanhTien = CurrentUser.getThanhTien();
        double soTienGiamGia = this.onKiemTraMaKhuyenMaiCTHD();
        CurrentUser.setSoTienGiamGia(soTienGiamGia);
        double tongTienCuoiCung = thanhTien - soTienGiamGia;
        double tongTienSauCung = soTienGiamGia != 0 ? tongTienCuoiCung : thanhTien;
        CurrentUser.setTongTienSauCung(tongTienCuoiCung);
        txtSoTienGiamGia.setText(TauGaUtils.NumberUtils.formatNumber(soTienGiamGia));
        txtTongTien.setText(TauGaUtils.NumberUtils.formatNumber(tongTienSauCung));
        String maKM = txtMaKhuyenMaiCTHD.getText();
        if (maKM == null || maKM.trim().isEmpty()) {
            CurrentUser.setMaKhuyenMai(null); // hoặc "" tùy bạn
        } else {
            CurrentUser.setMaKhuyenMai(maKM.trim());
        }
    }

    public double onKiemTraMaKhuyenMaiCTHD() {
        String maKM = txtMaKhuyenMaiCTHD.getText();
        if (maKM == null) {
            return 0;
        }

        KhuyenMaiApDungDTO km = timKhuyenMaiTheoMa(maKM);
        if (km == null) {
            return 0;
        }

        return tinhTienGiamGia(km);
    }

    private double tinhTienGiamGia(KhuyenMaiApDungDTO km) {
        double thanhTien = CurrentUser.getThanhTien();
        if (km.getGiaTriTien() != 0) {
            return thanhTien - km.getGiaTriTien();
        } else if (km.getGiaTriPhanTram() != 0) {
            return thanhTien * (1 - km.getGiaTriPhanTram() / 100);
        }
        return 0;
    }

    public void onXacNhanDatVe(ActionEvent actionEvent) {
        try {
            for (GridPane grid : danhSachGrid) {
                ThongTinDatVeDTO ve = (ThongTinDatVeDTO) grid.getUserData();
                if (ve == null) continue;

                ve.setTenHanhKhach(((TextField) grid.lookup("#txtHanhKhach")).getText());
                ve.setCccd(((TextField) grid.lookup("#txtCCCD")).getText());
                ve.setSoDienThoai(((TextField) grid.lookup("#txtSDT")).getText());
                ve.setNgaySinh(((DatePicker) grid.lookup("#txtNgaySinh")).getValue());

                ComboBox<?> cbLoaiVe = (ComboBox<?>) grid.lookup("#cbLoaiVe");
                if (cbLoaiVe.getValue() != null) {
                    ve.setTenLoaiVe(cbLoaiVe.getValue().toString());
                }
            }

            // 2. Xác định người mua vé
            KhachHangDTO khachHangMuaVe;
            if (CurrentUser.isDaTonTaiKH()) {
                khachHangMuaVe = CurrentUser.getKhachHangDTO();
            } else {
                khachHangMuaVe = taoKhachHangNguoiDat();
                CurrentUser.setKhachHangDTO(khachHangMuaVe);
            }

            CurrentUser.setEmailNguoiDat(khachHangMuaVe.getEmail());

            if (khachHangMuaVe == null) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", "Không thể xử lý thông tin khách hàng mua vé!");
                return;
            }

            // 3. Chuẩn bị danh sách vé cuối cùng từ Map
            List<ThongTinDatVeDTO> dsVeFinal = new ArrayList<>(mapVeTheoGhe.values());

            // 4. Gọi Service thực hiện lưu DB
            NhanVienDTO nv = CurrentUser.getNhanVien();
            ThongTinChuyenTauDTO ct = CurrentUser.getThongTinChuyenTauDTO();
            String maKM = txtMaKhuyenMaiCTHD.getText();

            List<ThongTinDatVeDTO> ketQua = xacNhanDatVe(khachHangMuaVe, nv, dsVeFinal, ct, maKM);

            if (ketQua != null && !ketQua.isEmpty()) {
                CurrentUser.setThongTinVeDaDatDuoc(ketQua);
                if (xacNhanDatVeListener != null) xacNhanDatVeListener.accept(true);
            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thất bại", "Lưu dữ liệu thất bại, vui lòng kiểm tra lại!");
            }

        } catch (Exception e) {
            e.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", "Lỗi hệ thống: " + e.getMessage());
        }
    }

    public void anThongTinVeTau() {
        if (pnThongTinVeTau != null) {
            pnThongTinVeTau.setVisible(false);
            pnThongTinVeTau.setManaged(false);
        }
        if (btnDatVe != null) {
            btnDatVe.setVisible(false);
            btnDatVe.setManaged(false);
        }
        if (pnDanhSachVe != null) {
            pnDanhSachVe.getChildren().clear();
        }
    }

    //todo: validate
//    private boolean validateInputThongTinKH() {
//        String hoTen = txtHoTenKH.getText().trim();
//        if (hoTen.isEmpty() || hoTen.length() < 2) {
//            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Họ tên khách hàng không hợp lệ (tối thiểu 2 ký tự)");
//            txtHoTenKH.requestFocus();
//            return false;
//        }
//
//        String sdt = txtSDTKH.getText().trim();
//        if (!sdt.matches("^0[35789][0-9]{8}$")) {
//            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Số điện thoại phải gồm 10 số và bắt đầu bằng đầu số hợp lệ (03, 05, 07, 08, 09)");
//            txtSDTKH.requestFocus();
//            return false;
//        }
//
//        String cccd = txtCCCDKH.getText().trim();
//        if (!cccd.matches("^[0-9]{12}$")) {
//            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Lỗi nhập liệu", "CCCD phải gồm đúng 12 chữ số");
//            txtCCCDKH.requestFocus();
//            return false;
//        }
//
//        if (dpNgaySinhKH.getValue() == null) {
//            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Vui lòng chọn ngày sinh cho người mua");
//            dpNgaySinhKH.requestFocus();
//            return false;
//        }
//
//        if (dpNgaySinhKH.getValue().isAfter(LocalDate.now().minusYears(14))) {
//            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Lỗi nhập liệu", "Người mua vé phải từ 14 tuổi trở lên (theo quy định CCCD)");
//            dpNgaySinhKH.requestFocus();
//            return false;
//        }
//
//        return true;
//    }
//
//    //todo: validate
//    private boolean validateInputThongTinKHTrenVe() {
//        for (int i = 0; i < danhSachGrid.size(); i++) {
//            GridPane grid = danhSachGrid.get(i);
//
//            TextField txtTen = (TextField) grid.lookup("#txtHanhKhach");
//            TextField txtCCCD = (TextField) grid.lookup("#txtCCCD");
//            TextField txtSDT = (TextField) grid.lookup("#txtSDT");
//            DatePicker dpNgaySinh = (DatePicker) grid.lookup("#txtNgaySinh");
//            JFXComboBox<?> cbLoaiVe = (JFXComboBox<?>) grid.lookup("#cbLoaiVe");
//            int soVe = i + 1;
//
//            if (txtTen.getText().trim().isEmpty()) {
//                hienThiLoiVaFocus(grid, txtTen, "Vé số " + soVe + ": Chưa nhập tên hành khách");
//                return false;
//            }
//
//            if (!txtCCCD.getText().trim().matches("^[0-9]{12}$")) {
//                hienThiLoiVaFocus(grid, txtCCCD, "Vé số " + soVe + ": CCCD không hợp lệ (12 chữ số)");
//                return false;
//            }
//
//            if (!txtSDT.getText().trim().matches("^0[35789][0-9]{8}$")) {
//                hienThiLoiVaFocus(grid, txtSDT, "Vé số " + soVe + ": Số điện thoại không hợp lệ");
//                return false;
//            }
//
//            if (dpNgaySinh.getValue() == null) {
//                hienThiLoiVaFocus(grid, dpNgaySinh, "Vé số " + soVe + ": Chưa chọn ngày sinh");
//                return false;
//            }
//
//            if (cbLoaiVe.getSelectionModel().isEmpty()) {
//                hienThiLoiVaFocus(grid, cbLoaiVe, "Vé số " + soVe + ": Chưa chọn loại vé");
//                return false;
//            }
//        }
//        return true;
//    }
//    private void hienThiLoiVaFocus(GridPane grid, Node target, String message) {
//        Platform.runLater(() -> {
//            Alert alert = new Alert(Alert.AlertType.WARNING);
//            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING,"","Thông tin chưa hoàn thiện");
//            alert.showAndWait();
//
//            double vBoxHeight = pnDanhSachVe.getHeight();
//            double gridY = grid.getBoundsInParent().getMinY();
//            if (pnScrollPaneDanhSachVe != null) {
//                pnScrollPaneDanhSachVe.setVvalue(gridY / vBoxHeight);
//            }
//
//            // Focus đúng field
//            if (target != null) target.requestFocus();
//        });
//    }
//    private void setupValidationAndClear(TextField textField, String regex, String message) {
//        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
//            if (!newVal) {
//                String text = textField.getText().trim();
//                if (!text.isEmpty() && !text.matches(regex)) {
//                    Platform.runLater(() -> {
//                        GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Lỗi định dạng", message);
//                        textField.clear();
//                        textField.requestFocus();
//                    });
//                }
//            }
//        });
//    }
//    private void setupDateValidation(DatePicker datePicker) {
//        datePicker.focusedProperty().addListener((obs, oldVal, newVal) -> {
//            if (!newVal) {
//                LocalDate date = datePicker.getValue();
//                if (date != null && date.isAfter(LocalDate.now())) {
//                    GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Lỗi ngày sinh", "Ngày sinh không được là ngày ở tương lai");
//                    datePicker.setValue(null);
//                }
//            }
//        });
//    }
//    private void setupNameValidation(TextField textField) {
//        textField.focusedProperty().addListener((obs, oldVal, newVal) -> {
//            if (!newVal) {
//                String text = textField.getText().trim();
//                String regexTen = "^[A-Za-zÀ-ỹ]+(?: [A-Za-zÀ-ỹ]+)*$";
//
//                if (!text.isEmpty() && (!text.matches(regexTen) || text.length() < 2)) {
//                    GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Lỗi định dạng", "Họ tên không hợp lệ (Chỉ nhập chữ và tối thiểu 2 ký tự)");
//                    textField.clear();
//                }
//            }
//        });
//    }

    private void taiDuLieuKhoiTao() {
        Request request = new Request(CommandType.GET_DAT_VE_INIT, null);
        Response response = socketClient.send(request);
        if (response == null || !response.isSuccess() || response.getData() == null) {
            return;
        }
        DatVeInitDTO dto = (DatVeInitDTO) response.getData();
        this.danhSachLoaiVe = dto.getDanhSachLoaiVe() == null ? new ArrayList<>() : new ArrayList<>(dto.getDanhSachLoaiVe());
    }

    private KhachHangDTO timKhachHang(String sdt, String cccd) {
        KhachHangRequest payload = new KhachHangRequest(sdt, cccd);
        Request request = new Request(CommandType.LOOKUP_KHACH_HANG, payload);
        Response response = socketClient.send(request);
        if (response == null || !response.isSuccess()) {
            return null;
        }
        return (KhachHangDTO) response.getData();
    }

    private LoaiVeInfoDTO layThongTinLoaiVe(LocalDate ngaySinh, double giaVe) {
        LoaiVeRequest payload = new LoaiVeRequest(ngaySinh, giaVe);
        Request request = new Request(CommandType.GET_LOAI_VE_INFO, payload);
        Response response = socketClient.send(request);
        if (response == null || !response.isSuccess()) {
            if (response != null && response.getMessage() != null && !response.getMessage().isBlank()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Cảnh báo", response.getMessage());
            }
            return null;
        }
        return (LoaiVeInfoDTO) response.getData();
    }

    private KhuyenMaiApDungDTO timKhuyenMaiTheoMa(String maKhuyenMai) {
        KhuyenMaiRequest payload = new KhuyenMaiRequest(maKhuyenMai);
        Request request = new Request(CommandType.LOOKUP_KHUYEN_MAI, payload);
        Response response = socketClient.send(request);
        if (response == null || !response.isSuccess()) {
            return null;
        }
        return (KhuyenMaiApDungDTO) response.getData();
    }

    private KhachHangDTO taoKhachHangNguoiDat() {
        KhachHangDTO khachHangDTO = new KhachHangDTO();
        khachHangDTO.setTenKH(txtHoTenKH.getText());
        khachHangDTO.setCccd(txtCCCDKH.getText());
        khachHangDTO.setSoDienThoai(txtSDTKH.getText());
        khachHangDTO.setNgaySinh(dpNgaySinhKH.getValue());
        return khachHangDTO;
    }

    @SuppressWarnings("unchecked")
    private List<ThongTinDatVeDTO> xacNhanDatVe(
            KhachHangDTO khachHang,
            NhanVienDTO nhanVien,
            List<ThongTinDatVeDTO> dsVe,
            ThongTinChuyenTauDTO chuyenTau,
            String maKhuyenMai
    ) {
        DatVeRequest payload = new DatVeRequest(khachHang, nhanVien, dsVe, chuyenTau, maKhuyenMai);
        Request request = new Request(CommandType.CONFIRM_DAT_VE, payload);
        Response response = socketClient.send(request);
        if (response == null || !response.isSuccess() || response.getData() == null) {
            if (response != null && response.getMessage() != null && !response.getMessage().isBlank()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thất bại", response.getMessage());
            }
            return null;
        }
        return (List<ThongTinDatVeDTO>) response.getData();
    }

}

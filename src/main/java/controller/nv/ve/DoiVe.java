package controller.nv.ve;

import com.jfoenix.controls.JFXComboBox;
import dto.*;
import entity.enums.LoaiGhe;
import entity.enums.TrangThaiGhe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.*;
import service.IChuyenTauService;
import service.impl.ChuyenTauServiceImpl;
import utils.GiaoDienUtils;
import utils.QuetQrUtils;
import utils.TauGaUtils;
import utils.consts.CurrentUser;
import utils.enums.Title;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class DoiVe {
    @FXML
    private VBox pnThongTinChuyenDiMotChieu;
    @FXML
    private VBox pnDanhSachChuyenDiMotChieu;
    @FXML
    private RadioButton radMaVe;
    @FXML
    private RadioButton radThongTinKH;
    @FXML
    private HBox HBoxMaVe;
    @FXML
    private TextField txtMaVe;
    @FXML
    private TextField txtSDT;
    @FXML
    private TextField txtCCCD;
    @FXML
    private HBox HBoxThongTinKH;
    @FXML
    private TableView<ChiTietVeTraDTO> table;
    @FXML
    private TableColumn<VeTauDTO, String> colMaVe;
    @FXML
    private TableColumn<VeTauDTO, String> colTenKH;
    @FXML
    private TableColumn<VeTauDTO, String> colChuyenTau;
    @FXML
    private TableColumn<VeTauDTO, String> colNgayDat;
    @FXML
    private TableColumn<VeTauDTO, String> colGiaVe;
    @FXML
    private TableColumn<VeTauDTO, String> colTrangThaiVe;

    //txt cua thong tin khach hang
    @FXML
    private TextField txtHoTen;
    @FXML
    private TextField txtSDT_KH;
    @FXML
    private TextField txtCCCD_KH;
    @FXML
    private TextField txtNgaySinh;

    //txt thong tin ve cu
    @FXML
    private TextField txtHanhKhachCu;
    @FXML
    private TextField txtNgayKhoiHanhCu;
    @FXML
    private TextField txtToa_Khoang_GheCu;
    @FXML
    private TextField txtNgayDatCu;
    @FXML
    private TextField txtNgayDenCu;
    @FXML
    private TextField txtCCCD_VeTraCu;
    @FXML
    private TextField txtChuyenTauCu;
    @FXML
    private TextField txtLoaiGhe_VeTraCu;
    @FXML
    private TextField txtTongTien_VeTraCu;

    //txt ve moi
    @FXML
    private TextField txtHanhKhachMoi;
    @FXML
    private TextField txtNgayKhoiHanhMoi;
    @FXML
    private TextField txtToaKhoangGheMoi;
    @FXML
    private TextField txtNgayDenMoi;
    @FXML
    private TextField txtSoDienThoaiMoi;
    @FXML
    private TextField txtNgayDatMoi;
    @FXML
    private TextField txtChuyenTauMoi;
    @FXML
    private TextField txtLoaiGheMoi;
    @FXML
    private TextField txtTongTienMoi;


    //txt của chi tiet ve tra
    @FXML
    private TextField txtKhachHang;
    @FXML
    private TextField txtNguoiLapHD;
    @FXML
    private TextField txtNgayLapHD;
    @FXML
    private TextField txtThanhTien_DoiVe;
    @FXML
    private TextField txtMaKhuyenMai;
    @FXML
    private TextField txtSoTienKhuyenMai;
    @FXML
    private TextField txtTongTienCuoiCung;

    @FXML
    private SplitPane mainSplit;
    @FXML
    private VBox thongTinDoiVe;
    @FXML
    private AnchorPane main;

    //tim kiem chuyen tau
    @FXML

    private JFXComboBox<String> cbbLoaiGhe;
    @FXML
    private ComboBox<String> cbbGioKhoiHanh;
    @FXML
    private VBox vbSoDoGhe;
    @FXML
    private VBox soDoGhe;

    //thongtinve
    @FXML
    private BorderPane pnRight;
    @FXML
    private Label lblChiTietHoaDon;
    @FXML
    private GridPane pnChiTietHoaDon;
    @FXML
    private Button btnXacNhan;

    //thanh toan
    @FXML
    private VBox pnHinhThucThanhToan;
    @FXML
    private RadioButton radTienMat, radChuyenKhoan;
    @FXML
    private Button btnPhuongThucTT, btnKetThuc;
    @FXML
    private StackPane pnThongTinThanhToan;
    @FXML
    private Label lblThanhToan;
    //tong tien de hien thi thanh toanx
    private double tongTien = 0;
    private final SocketClient socketClient = new SocketClient();

    private IChuyenTauService chuyenTauService = new ChuyenTauServiceImpl();
    private final List<GheDTO> danhSachGheDangChon = new java.util.ArrayList<>();
    private final List<ThongTinDatVeDTO> danhSachThongTinVe = new ArrayList<>();
    private KhuyenMaiApDungDTO khuyenMaiApDungDTO = null;
    private ThongTinChuyenTauDTO chuyenTauMoiDangChon;
    private ThongTinChuyenTauDTO thongTinCoCuLy;


    @FXML
    public void initialize() {
        ToggleGroup group = new ToggleGroup();
        radMaVe.setToggleGroup(group);
        radThongTinKH.setToggleGroup(group);
        radMaVe.setSelected(true);
        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            if (radMaVe.isSelected()) {
                HBoxMaVe.setVisible(true);
                HBoxMaVe.setManaged(true);

                HBoxThongTinKH.setVisible(false);
                HBoxThongTinKH.setManaged(false);
            } else if (radThongTinKH.isSelected()) {
                HBoxMaVe.setVisible(false);
                HBoxMaVe.setManaged(false);

                HBoxThongTinKH.setVisible(true);
                HBoxThongTinKH.setManaged(true);
            }
        });

        if (CurrentUser.isIsFromTraCuu()){
            loadData();
        }
        colMaVe.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("maVe"));
        colTenKH.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tenKH"));
        colChuyenTau.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("maChuyenTau"));
        colNgayDat.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("ngayDatStr"));
        colGiaVe.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tienVeFormat"));
        colTrangThaiVe.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("trangThaiVeTau"));

        wrapColumn(colMaVe);
        wrapColumn(colTenKH);
        wrapColumn(colChuyenTau);
        table.setFixedCellSize(-1);

        QuetQrUtils.setOnCodeScanned(code -> {
            txtMaVe.setText(code);
        });
        txtSoTienKhuyenMai.setEditable(false);
    }

    private void loadData(){
        var veTau = CurrentUser.getMaVeFromTraCuu();
        var maVe = veTau.getMaVe();
        if (maVe == null){
            radThongTinKH.setSelected(Boolean.TRUE);
            txtSDT.setText(CurrentUser.getSdt());
            txtCCCD.setText(CurrentUser.getCccd());
            var rs = new ChiTietVeTraDTO();
            rs.setMaVe(veTau.getMaVe());
            rs.setTenChuyenTau(veTau.getTenKH());
            rs.setMaChuyenTau(veTau.getMaChuyenTau());
            rs.setNgayDatStr(veTau.getNgayDatStr());
            rs.setTienVeFormat(veTau.getTienVeFormat());
            rs.setTrangThaiVeTau(veTau.getTrangThaiVe());
            table.getItems().clear();
            table.getItems().add(rs);
        }
        txtMaVe.setText(maVe);
        ChiTietVeTraDTO veTauServiceThongTinVeTra = timVeDoiTheoMaVe(maVe);
        if (!TauGaUtils.VeUtils.isExchangeTicket(veTauServiceThongTinVeTra)) {
            return;
        }
        CurrentUser.setChiTietVeTraDTO(veTauServiceThongTinVeTra);
        table.getItems().clear();
        table.getItems().add(veTauServiceThongTinVeTra);
    }


    private void wrapColumn(TableColumn<VeTauDTO, String> col) {
        col.setCellFactory(tc -> new TableCell<VeTauDTO, String>() {
            private final Text text = new Text();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(10));
                    setGraphic(text);
                }
            }
        });
    }


    @FXML
    public void timKiem(ActionEvent actionEvent) {
        if (radMaVe.isSelected()) {
            String maVe = txtMaVe.getText().trim();
            if (TauGaUtils.StringUtils.isEmpty(maVe)) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Vui lòng nhập mã vé!");
                return;
            }

            ChiTietVeTraDTO veTau = timVeDoiTheoMaVe(maVe);
            table.getItems().clear();
            if (veTau == null) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Không tim thấy vé tàu!");
                return;
            }
            if (!TauGaUtils.VeUtils.isExchangeTicket(veTau)) {
                return;
            }
            CurrentUser.setChiTietVeTraDTO(veTau);
            table.getItems().add(veTau);

        } else if (radThongTinKH.isSelected()) {
            String sdt = txtSDT.getText().trim();
            String cccd = txtCCCD.getText().trim();
            if (TauGaUtils.StringUtils.isEmpty(sdt) && TauGaUtils.StringUtils.isEmpty(cccd)) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Vui lòng nhập số điện thoại hoặc CCCD!");
                return;
            }

            if (!TauGaUtils.StringUtils.isEmpty(sdt)) {
                ChiTietVeTraDTO veTau = timVeDoi(sdt, "SDT");
                table.getItems().clear();

                if (veTau == null) {
                    GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Không tim thấy vé tàu!");
                    return;
                }
                if (!TauGaUtils.VeUtils.isExchangeTicket(veTau)) {
                    return;
                }
                CurrentUser.setChiTietVeTraDTO(veTau);
                table.getItems().add(veTau);
            } else if (!TauGaUtils.StringUtils.isEmpty(cccd)) {

                ChiTietVeTraDTO veTau = timVeDoi(cccd, "CCCD");
                table.getItems().clear();

                if (veTau == null) {
                    GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Không tim thấy vé tàu!");
                    return;
                }
                if (!TauGaUtils.VeUtils.isExchangeTicket(veTau)) {
                    return;
                }
                CurrentUser.setChiTietVeTraDTO(veTau);
                table.getItems().add(veTau);
            }
        }
    }

    public void doiVe(javafx.event.ActionEvent actionEvent) {
        pnDanhSachChuyenDiMotChieu.getChildren().clear();
        soDoGhe.getChildren().clear();
        txtHanhKhachMoi.clear();
        txtNgayKhoiHanhMoi.clear();
        txtToaKhoangGheMoi.clear();
        txtNgayDenMoi.clear();
        txtSoDienThoaiMoi.clear();
        txtNgayDatMoi.clear();
        txtChuyenTauMoi.clear();
        txtLoaiGheMoi.clear();
        txtTongTienMoi.clear();

        ChiTietVeTraDTO ve = table.getSelectionModel().getSelectedItem();
        if (ve == null) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Vui lòng chọn vé cần đổi");
            return;
        }

        DoiVePreviewDTO preview = layPreviewDoiVe(ve.getMaVe());
        if (preview == null) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Không lấy được thông tin đổi vé");
            return;
        }

        CurrentUser.setKhachHangDTO(preview.getKhachHang());
        CurrentUser.setChiTietVeTraDTO(preview.getChiTietVeTra());

        if (preview.isCoTheDoiVe()) {
            KhachHangDTO kh = preview.getKhachHang();
            ChiTietVeTraDTO chiTietVeTraDTO = preview.getChiTietVeTra();

            // Load thông tin khách hàng lên giao diện
            if (kh != null) {
                txtHoTen.setText(kh.getTenKH());
                txtSDT_KH.setText(kh.getSoDienThoai());
                txtCCCD_KH.setText(kh.getCccd());
                txtNgaySinh.setText(kh.getNgaySinhStr());
            }

            cbbLoaiGhe.getItems().clear();
            cbbLoaiGhe.getItems().addAll(
                    LoaiGhe.GHE_NGOI_MEM.getLoaiGhe(),
                    LoaiGhe.GHE_NGOI_CUNG.getLoaiGhe(),
                    LoaiGhe.GIUONG_6.getLoaiGhe(),
                    LoaiGhe.GIUONG_4.getLoaiGhe(),
                    LoaiGhe.GIUONG_2.getLoaiGhe()
            );
            cbbLoaiGhe.getSelectionModel().selectFirst();

            if (!TauGaUtils.VeUtils.isExchangeTicket(chiTietVeTraDTO)) {
                return;
            }

            cbbGioKhoiHanh.getItems().clear();
            cbbGioKhoiHanh.getItems().addAll(preview.getGioDi());
            cbbGioKhoiHanh.getSelectionModel().selectFirst();

            if (chiTietVeTraDTO != null) {
                txtHanhKhachCu.setText(chiTietVeTraDTO.getTenKH());
                txtNgayKhoiHanhCu.setText(chiTietVeTraDTO.getNgayKhoiHanhStr());
                txtNgayDenCu.setText(chiTietVeTraDTO.getNgayDenStr());
                txtToa_Khoang_GheCu.setText(chiTietVeTraDTO.getToaKhoangGhe());
                txtCCCD_VeTraCu.setText(chiTietVeTraDTO.getCccd());
                txtNgayDatCu.setText(chiTietVeTraDTO.getNgayDatStr());
                txtChuyenTauCu.setText(chiTietVeTraDTO.getMaChuyenTau());
                txtLoaiGhe_VeTraCu.setText(chiTietVeTraDTO.getLoaiGhe());
                txtTongTien_VeTraCu.setText(TauGaUtils.NumberUtils.formatNumber(chiTietVeTraDTO.getTienVe()));
            }

            return;
        }

        GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), preview.getMessage());
    }


    public void timKiemCT(javafx.event.ActionEvent actionEvent) {
        /**
         1. Load data
         * */

        ChiTietVeTraDTO veTauDTO = CurrentUser.getChiTietVeTraDTO();

        /**
         2. Tim kiem theo field gio di + loai ghe tuong ung vs ga di, ga den, ngay khoi hanh
         * */
        String gio = cbbGioKhoiHanh.getSelectionModel().getSelectedItem();
        String ngayGioDi = veTauDTO.getNgayKhoiHanh().toString();
        String ngayGioKhoiHanh = ngayGioDi + " " + gio + ":00";
        String loaiGhe = cbbLoaiGhe.getSelectionModel().getSelectedItem();

        ChuyenTauDTO result = timChuyenTauDoiVe(veTauDTO.getGaDi(), veTauDTO.getGaDen(), loaiGhe, ngayGioKhoiHanh);
        if (result == null) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "", "Không tìm thấy chuyến đi nào phù hợp");
            return;
        }
        CurrentUser.setChuyenTauDTO(result);

        /**
         3. Hien thi len UI
         */
        if (result.getChuyenTauDi() == null || result.getChuyenTauDi().size() == 0) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "", "Không tìm thấy chuyến đi nào phù hợp");
            return;
        }
        List<ThongTinChuyenTauDTO> dsLoc = result.getChuyenTauDi().stream()
                .filter(ct -> {
                    try {
                        // ví dụ: "2025-11-04 06:00:00.0"
                        String gioDiStr = ct.getNgayGioDi().substring(11, 16);
                        return gioDiStr.equals(gio);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        if (dsLoc.isEmpty()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thông báo",
                    "Không có chuyến tàu khởi hành lúc " + gio);
            return;
        }

//        ThongTinChuyenTauDTO thongTin = chuyenTauService.getThongTinChuyenTauCoCuly(
//                veTauDTO.getGaDi(),
//                veTauDTO.getGaDen(),
//                dsLoc.get(0).getMaChuyenTau()
//        );
        ThongTinChuyenTauDTO thongTin = getThongTinChuyenTauCoCuLy(
                veTauDTO.getGaDi(),
                veTauDTO.getGaDen(),
                dsLoc.get(0).getMaChuyenTau()
        );
        this.thongTinCoCuLy = thongTin;

        CurrentUser.setThongTinChuyenTauDTO(thongTin);

        ChuyenTauDTO ketQuaLoc = new ChuyenTauDTO();
        ketQuaLoc.setChuyenTauDi(dsLoc);
        hienThiChuyenTau(ketQuaLoc, loaiGhe);
    }

    public ThongTinChuyenTauDTO getThongTinChuyenTauCoCuLy(String gaDi, String gaDen, String maChuyenTau) {
        ThongTinChuyenTauCoCuLyRequest payload = new ThongTinChuyenTauCoCuLyRequest(gaDi, gaDen, maChuyenTau);
        Request request = new Request(CommandType.GET_THONG_TIN_CHUYEN_TAU_CO_CU_LY, payload);
        Response response = socketClient.send(request);
        if (!response.isSuccess() || !(response.getData() instanceof ThongTinChuyenTauDTO dto)) {
            return null;
        }
        return (ThongTinChuyenTauDTO) dto;
    }


    private void hienThiChuyenTau(ChuyenTauDTO chuyenTauDTO, String loaiGhe) {
        pnThongTinChuyenDiMotChieu.setVisible(true);
        pnThongTinChuyenDiMotChieu.setManaged(true);
        pnDanhSachChuyenDiMotChieu.getChildren().clear();

        for (ThongTinChuyenTauDTO ct : chuyenTauDTO.getChuyenTauDi()) {
            pnDanhSachChuyenDiMotChieu.getChildren().add(taoChuyenTau(ct, loaiGhe));
        }
    }

    private VBox taoChuyenTau(ThongTinChuyenTauDTO ct, String loaiGhe) {
        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #aaa; -fx-border-radius: 8; -fx-padding: 10; -fx-background-color: #f9f9f9;");
        Label lblTau = new Label("Tàu: " + ct.getTenTau());
        Label lblToa = new Label("Số toa: " + ct.getSoToaTau());
        String ngayDiFormatted;
        String ngayGioDiRaw = ct.getNgayGioDi();

        if (ngayGioDiRaw != null && ngayGioDiRaw.contains(" ") && ngayGioDiRaw.contains("-")) {
            try {
                String[] parts = ngayGioDiRaw.split(" ");
                String datePart = parts[0];
                String timePart = parts[1];
                String[] dateParts = datePart.split("-");
                String formattedDate = dateParts[2] + "-" + dateParts[1] + "-" + dateParts[0];
                ngayDiFormatted = formattedDate + " " + timePart;

            } catch (Exception e) {
                ngayDiFormatted = ngayGioDiRaw;
            }
        } else {
            ngayDiFormatted = ngayGioDiRaw;
        }

        Label lblNgayDi = new Label("Ngày đi: " + ngayDiFormatted);
        Image anhTau = new Image(getClass().getResourceAsStream("/tauga/img/train.png"));
        ImageView tau = new ImageView(anhTau);
        tau.setFitWidth(22);
        tau.setFitHeight(22);

        Label lblGioDi = new Label(ct.getGioDiStr() != null ? ct.getGioDiStr() : "");
        Label lblGioDen = new Label(ct.getGioDenStr() != null ? ct.getGioDenStr() : "");

        HBox gioBox = new HBox(25);
        gioBox.setAlignment(Pos.CENTER);
        gioBox.getChildren().addAll(lblGioDi, tau, lblGioDen);
        Label lblMuiTen = new Label("────────────▶");
        HBox muiTenBox = new HBox();
        muiTenBox.getChildren().add(lblMuiTen);
        muiTenBox.setAlignment(Pos.CENTER);
        Label lblGaDi = new Label(ct.getGaDi());
        Label lblGaDen = new Label(ct.getGaDen());
        HBox gaBox = new HBox(35);
        gaBox.getChildren().addAll(lblGaDi, lblGaDen);
        gaBox.setAlignment(Pos.CENTER);
        Button btnChon = new Button("Chọn");
        HBox chonBox = new HBox();
        chonBox.getChildren().add(btnChon);
        chonBox.setAlignment(Pos.CENTER);
        btnChon.setOnAction(e -> {
            chuyenTauMoiDangChon = ct;
            CurrentUser.setThongTinChuyenTauDTO(ct);
            hienThiSoDoGhe(ct.getMaChuyenTau(), loaiGhe);
        });

        VBox.setVgrow(box, Priority.NEVER);
        box.getChildren().addAll(lblTau, lblToa, lblNgayDi, gioBox, muiTenBox, gaBox, chonBox);
        return box;
    }


    private GheDTO gheDangChon;
    private final Map<GheDTO, Button> nutGheMap = new HashMap<>();

    private void hienThiSoDoGhe(String maChuyenTau, String loaiGheChon) {
        soDoGhe.getChildren().clear();
        if (loaiGheChon == null) {
            Label lbl = new Label("Vui lòng chọn loại ghế trước.");
            soDoGhe.getChildren().add(lbl);
            return;
        }

        List<GheDTO> danhSachGhe = laySoDoGhe(maChuyenTau);
        if (danhSachGhe.isEmpty()) {
            Label lbl = new Label("Không có dữ liệu ghế cho chuyến " + maChuyenTau);
            soDoGhe.getChildren().add(lbl);
            return;
        }

        // Gom nhóm Toa -> Khoang -> Ghế
        Map<String, Map<String, List<GheDTO>>> soDo = danhSachGhe.stream()
                .collect(Collectors.groupingBy(GheDTO::getMaToa, LinkedHashMap::new,
                        Collectors.groupingBy(GheDTO::getMaKhoang, LinkedHashMap::new, Collectors.toList())));
        int sttToa = 1;

        for (String maToa : soDo.keySet()) {
            Map<String, List<GheDTO>> khoangs = soDo.get(maToa);

            String loaiGheToa = khoangs.values().stream()
                    .flatMap(List::stream)
                    .findFirst()
                    .map(GheDTO::getTenLoaiGhe)
                    .orElse("Không xác định");

            Label lblToa = new Label("TOA " + sttToa + " - " + loaiGheToa);
            lblToa.setStyle("-fx-font-weight: bold; -fx-font-size: 15; -fx-padding: 5 0 10 0;");
            soDoGhe.getChildren().add(lblToa);

            HBox hbKhoangs = new HBox(25);
            hbKhoangs.setAlignment(Pos.CENTER);
            hbKhoangs.setStyle("-fx-padding: 10; -fx-border-color: #EAECEE;");

            int sttKhoang = 1;
            for (String maKhoang : khoangs.keySet()) {
                VBox vbKhoang = new VBox(6);
                Label lblKhoang = new Label("Khoang " + sttKhoang++);
                lblKhoang.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
                vbKhoang.getChildren().add(lblKhoang);

                List<GheDTO> gheTrongKhoang = khoangs.get(maKhoang);
                gheTrongKhoang.sort(Comparator.comparingInt(GheDTO::getViTriGhe));

                GridPane grid = new GridPane();
                grid.setHgap(10);
                grid.setVgap(10);

                int row = 0, col = 0;
                for (GheDTO ghe : gheTrongKhoang) {
                    Button btnGhe = new Button(String.valueOf(ghe.getViTriGhe()));
                    btnGhe.setPrefSize(40, 30);

                    if (!ghe.getTenLoaiGhe().equalsIgnoreCase(loaiGheChon)) {
                        btnGhe.setDisable(true);
                        btnGhe.setStyle("-fx-background-color: #D7DBDD; -fx-opacity: 0.6;");
                        Tooltip.install(btnGhe, new Tooltip("Khác loại ghế đã chọn"));
                    } else if (ghe.getTrangThaiGhe() == TrangThaiGhe.DA_DAT.getI()) {
                        btnGhe.setDisable(true);
                        btnGhe.setStyle("-fx-background-color: #E0E0E0; -fx-opacity: 0.6;");
                        Tooltip.install(btnGhe, new Tooltip("Ghế đã được đặt"));
                    } else {
                        btnGhe.setStyle("-fx-background-color: #85C1E9;");
                        nutGheMap.put(ghe, btnGhe);
                        btnGhe.setOnAction(e -> {
                            if (gheDangChon != null && !gheDangChon.equals(ghe)) {
                                Button btnCu = nutGheMap.get(gheDangChon);
                                if (btnCu != null) {
                                    btnCu.setStyle("-fx-background-color: #85C1E9;");
                                }
                            }

                            gheDangChon = ghe;
                            danhSachGheDangChon.clear();
                            danhSachGheDangChon.add(ghe);
                            btnGhe.setStyle("-fx-background-color: #2ECC71;");
                            hienThiThongTinVe();
                        });
                    }

                    grid.add(btnGhe, col, row);
                    col++;
                    if (col == 2) {
                        col = 0;
                        row++;
                    }
                }


                vbKhoang.getChildren().add(grid);
                hbKhoangs.getChildren().add(vbKhoang);
            }

            soDoGhe.getChildren().add(hbKhoangs);
            sttToa++;
        }
    }


    //hien thi thong tin ve moi
    private void hienThiThongTinVe() {
        pnRight.setVisible(true);
        pnRight.setManaged(true);
        if (danhSachGheDangChon.isEmpty()) return;
        GheDTO gheChon = danhSachGheDangChon.get(0);
        double giaVe = gheChon.getGiaVe();
        KhachHangDTO kh = CurrentUser.getKhachHangDTO();
        ChiTietVeTraDTO veCu = CurrentUser.getChiTietVeTraDTO();
//        ThongTinChuyenTauDTO ct = CurrentUser.getThongTinChuyenTauDTO();
        double gia1Km = 1000;

        gheChon.setGiaVe(giaVe);
        txtHanhKhachMoi.setText(kh.getTenKH());
        txtNgayKhoiHanhMoi.setText(veCu.getNgayKhoiHanhStr());

        txtToaKhoangGheMoi.setText(gheChon.getTenToa() + " - " + gheChon.getTenKhoang() + " - Ghế " + gheChon.getViTriGhe());

        txtNgayDenMoi.setText(veCu.getNgayDenStr());
        txtSoDienThoaiMoi.setText(kh.getSoDienThoai());
        txtNgayDatMoi.setText(TauGaUtils.DateTimeUtils.convertLocalDateToString(LocalDate.now(), TauGaUtils.FORMATTER_DATE));
        txtChuyenTauMoi.setText(chuyenTauMoiDangChon != null ? chuyenTauMoiDangChon.getTenTau() : "");
        txtLoaiGheMoi.setText(cbbLoaiGhe.getSelectionModel().getSelectedItem());


        double tongTienMoi = (giaVe *  gheChon.getHeSoVe()) + (thongTinCoCuLy.getCuly() * gia1Km);
        txtTongTienMoi.setText(TauGaUtils.NumberUtils.formatNumber(tongTienMoi));

        //tinh tien de bo vao text field o cthd
        double tienCu = veCu.getTienVe();
//        double tienMoi = giaVe;
        double tienMoi = tongTienMoi;
        tongTien = tienMoi - tienCu;
        CurrentUser.setThanhTien(tongTien);

        HoaDonVeDTO hd = new HoaDonVeDTO();
        hd.setTenHanhKhach(kh.getTenKH());
        hd.setNgayGioKhoiHanh(veCu.getNgayKhoiHanhStr());
        hd.setGheDTO(gheChon);
        hd.setTenLoaiGhe(cbbLoaiGhe.getSelectionModel().getSelectedItem());
        hd.setTenLoaiGhe(cbbLoaiGhe.getSelectionModel().getSelectedItem());

        CurrentUser.setHoaDonVeDTO(hd);

    }

    public void chotDoiVe(ActionEvent actionEvent) {
        if (danhSachGheDangChon.isEmpty()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn 1 ghế để đổi vé!");
            return;
        }

        GheDTO gheMoi = danhSachGheDangChon.get(0);
        ChiTietVeTraDTO veCu = CurrentUser.getChiTietVeTraDTO();
        KhachHangDTO kh = CurrentUser.getKhachHangDTO();
        NhanVienDTO nv = CurrentUser.getNhanVien();

        txtKhachHang.setText(kh.getTenKH());
        txtNguoiLapHD.setText(nv.getTenNV());
        txtNgayLapHD.setText(LocalDate.now().toString());
        double tongTienLayDuoc = CurrentUser.getThanhTien();
        System.out.println("Tong tien lay duoc tu CurrentUser: " + CurrentUser.getThanhTien());
        String tt = TauGaUtils.NumberUtils.formatNumber(tongTienLayDuoc);

        txtThanhTien_DoiVe.setText(tt);
        System.out.println("TextField sau khi set: " + txtThanhTien_DoiVe.getText());

        double giaKM = onKiemTraMaKhuyenMai(actionEvent);
        txtSoTienKhuyenMai.setText(TauGaUtils.NumberUtils.formatNumber(giaKM));
        double tongTienHD = tongTienLayDuoc - giaKM;
        if (tongTienHD < 0) {
            tongTienHD = 0;
        }
        txtTongTienCuoiCung.setText(TauGaUtils.NumberUtils.formatNumber(tongTienHD));
        CurrentUser.setTongTienSauCung(tongTienHD);
        System.out.println("thanh tien " + tongTienLayDuoc);
        System.out.println("gia km " + giaKM);
        System.out.println("tong tien cuoi cung: " + tongTienHD);
    }

    public void xacNhanDoiVe(ActionEvent actionEvent) {
        /**
         1. Insert ve moi
         2. Update trang thai ve cu
         3. Insert cthd + hd cua ve moi
         4. Update status ghe cua 2 ve
         5. Hien thi tien chenh lech va frame thanh toan
         */

        try {
            if (danhSachGheDangChon.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Thông báo", "Vui lòng chọn ghế mới trước khi xác nhận đổi vé!");
                return;
            }

            //gom thong tin
            ChiTietVeTraDTO veCu = CurrentUser.getChiTietVeTraDTO();
            KhachHangDTO kh = CurrentUser.getKhachHangDTO();
            NhanVienDTO nv = CurrentUser.getNhanVien();
            GheDTO gheMoi = danhSachGheDangChon.get(0);
            ChuyenTauDTO chuyenTauDTO = CurrentUser.getChuyenTauDTO();

            ThongTinDoiVeDTO thongTinDoiVe = new ThongTinDoiVeDTO();
            thongTinDoiVe.setVeCu(veCu);
            thongTinDoiVe.setGheMoi(gheMoi);
            thongTinDoiVe.setKhachHang(kh);
            thongTinDoiVe.setNhanVien(nv);
            thongTinDoiVe.setChuyenTauDTO(chuyenTauDTO);
            thongTinDoiVe.setChuyenTauMoi(chuyenTauMoiDangChon);

            Response response = xacNhanDoiVeServer(thongTinDoiVe);
            boolean ketQuaDoi = response.isSuccess();

            if (!ketQuaDoi) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thất bại", response.getMessage());
                return;
            }

            //todo: hien thi frame vs gia tien
            pnHinhThucThanhToan.setVisible(true);
            pnHinhThucThanhToan.setManaged(true);
        } catch (Exception e) {
            e.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", "Đã xảy ra lỗi khi xác nhận đổi vé!");
        }
    }

    public void onXacNhanPhuongThucThanhToan(ActionEvent actionEvent) {
        pnThongTinThanhToan.getChildren().clear();

        ToggleGroup groupThanhToan = new ToggleGroup();
        radTienMat.setToggleGroup(groupThanhToan);
        radChuyenKhoan.setToggleGroup(groupThanhToan);

        radTienMat.setSelected(true);

        hienThiThanhToanTienMat();

        groupThanhToan.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            pnThongTinThanhToan.getChildren().clear();

            if (newToggle == radTienMat) {
                hienThiThanhToanTienMat();
            } else if (newToggle == radChuyenKhoan) {
                hienThiThanhToanChuyenKhoan();
            }
        });
    }


    private void hienThiThanhToanTienMat() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(15));

        double tongTien = CurrentUser.getThanhTien();
        Label lblTongTien = new Label("Tổng tiền phải trả: " + TauGaUtils.NumberUtils.formatNumber(tongTien));
        lblTongTien.setStyle("""
                    -fx-font-size: 15px;
                    -fx-font-weight: bold;
                    -fx-text-fill: #1b5e20;
                """);
        TextField txtKhachDua = new TextField();
        txtKhachDua.setPromptText("Nhập số tiền khách đưa");
        Label lblTienThoi = new Label("Tiền thối lại: 0 VNĐ");

        // === GỢI Ý SỐ TIỀN KHÁCH ĐƯA ===
        FlowPane boxGoiY = new FlowPane();
        boxGoiY.setHgap(10);
        boxGoiY.setVgap(10);
        boxGoiY.setPadding(new Insets(5));
        boxGoiY.setPrefWrapLength(300);

        double[] goiY = tinhGoiYSoTien(tongTien);

        for (double g : goiY) {
            Button btn = new Button(String.format("%,.0f", g));
            btn.setPrefWidth(100);
            btn.setPrefHeight(40);
            btn.setStyle("""
                        -fx-font-size: 14px;
                        -fx-padding: 8 16 8 16;
                        -fx-background-color: #f4f4f4;
                        -fx-border-color: #ccc;
                        -fx-border-radius: 8;
                        -fx-background-radius: 8;
                    """);

            btn.setOnMouseEntered(e -> btn.setStyle("""
                        -fx-font-size: 14px;
                        -fx-padding: 8 16 8 16;
                        -fx-background-color: #e0f3ff;
                        -fx-border-color: #66afe9;
                        -fx-border-radius: 8;
                        -fx-background-radius: 8;
                    """));
            btn.setOnMouseExited(e -> btn.setStyle("""
                        -fx-font-size: 14px;
                        -fx-padding: 8 16 8 16;
                        -fx-background-color: #f4f4f4;
                        -fx-border-color: #ccc;
                        -fx-border-radius: 8;
                        -fx-background-radius: 8;
                    """));

            btn.setOnAction(e -> txtKhachDua.setText(TauGaUtils.NumberUtils.formatNumber(g)));
            boxGoiY.getChildren().add(btn);
        }

        txtKhachDua.textProperty().addListener((obs, old, val) -> {
            try {
                String clean = val.replaceAll("[^\\d.]", "");
                if (clean.isEmpty()) {
                    lblTienThoi.setText("Tiền thối lại: 0 VNĐ");
                    return;
                }
                double dua = Double.parseDouble(clean);
                double thoi = dua - tongTien;
                lblTienThoi.setText("Tiền thối lại: " + TauGaUtils.NumberUtils.formatNumber(Math.max(thoi, 0)));
            } catch (NumberFormatException ex) {
                lblTienThoi.setText("Tiền thối lại: 0 VNĐ");
            }
        });

        box.getChildren().addAll(lblTongTien, new Label("Số tiền khách đưa:"), txtKhachDua, boxGoiY, lblTienThoi);
        pnThongTinThanhToan.getChildren().add(box);
    }

    /**
     * Tính gợi ý theo đúng yêu cầu:
     * - Nếu tròn 0k (bội 10k): nhảy +10k (30k,40k,...)
     * - Nếu tròn 5k  (ví dụ 35k): nhảy +5k (40k,50k,...)
     * - Nếu <5k (ví dụ 21-24k): +1k để làm tròn lên (22k), sau đó mốc 10k tiếp theo
     * - Nếu >5k và không phải 5k (ví dụ 26-29k): làm tròn lên chục (30k)...
     */
    private double[] tinhGoiYSoTien(double tongTien) {
        // Làm tròn tổng tiền để tránh lỗi số thực
        long t = Math.round(tongTien);

        // Nếu không tròn nghìn thì làm tròn lên bội của 1.000
        if (t % 1000 != 0) {
            t = ((t + 999) / 1000) * 1000;
        }

        // Danh sách mệnh giá tiền mặt phổ biến ở Việt Nam
        long[] menhGia = {10000, 20000, 50000, 100000, 200000, 500000, 1000000, 2000000};

        List<Long> goiY = new ArrayList<>();
        goiY.add(t); // Gợi ý đầu tiên là đúng tổng tiền cần trả

        // Tìm mệnh giá lớn hơn gần nhất
        for (long mg : menhGia) {
            if (mg > t) {
                goiY.add(mg);
            }
        }

        // Nếu vẫn còn ít hơn 4 gợi ý, thêm tiếp các mệnh giá cao hơn
        while (goiY.size() < 4 && goiY.size() < menhGia.length) {
            long last = goiY.get(goiY.size() - 1);
            for (long mg : menhGia) {
                if (mg > last) {
                    goiY.add(mg);
                    break;
                }
            }
        }

        // Trả về mảng double[] (để tương thích với kiểu ban đầu)
        return goiY.stream().limit(4).mapToDouble(Long::doubleValue).toArray();
    }


    private void hienThiThanhToanChuyenKhoan() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));

        Label lblThongTin = new Label("""
                Ngân hàng: MBBank
                Số tài khoản: 4134562628
                Chủ TK: LE THI KIEU THOA
                """);
        lblThongTin.setStyle("-fx-font-size: 13; -fx-text-alignment: center;");

        Label lblTien = new Label("Số tiền cần chuyển: " + TauGaUtils.NumberUtils.formatNumber(CurrentUser.getThanhTien()));
        lblTien.setStyle("-fx-font-weight: bold;");

        ImageView qrView = new ImageView(new Image(getClass().getResource("/tauga/img/qr.jpg").toExternalForm()));
        qrView.setFitHeight(200);
        qrView.setPreserveRatio(true);

        box.getChildren().addAll(lblThongTin, qrView, lblTien);
        pnThongTinThanhToan.getChildren().add(box);
    }

    public double onKiemTraMaKhuyenMai(ActionEvent actionEvent) {
        String maKM = txtMaKhuyenMai.getText().trim();

        if (maKM.isEmpty()) {
            khuyenMaiApDungDTO = null;
            txtMaKhuyenMai.setText("");
            capNhatTongTienSauKhuyenMai();
            return 0;
        }

        KhuyenMaiApDungDTO kmResult = timKhuyenMaiTheoMa(maKM);
        if (kmResult != null) {
            khuyenMaiApDungDTO = kmResult;
            txtMaKhuyenMai.setText(maKM);
            return capNhatTongTienSauKhuyenMai();
        } else {
            // khong tim thay hoac het han
            khuyenMaiApDungDTO = null;
            txtMaKhuyenMai.setText("");
            return capNhatTongTienSauKhuyenMai();
        }
    }

    private double capNhatTongTienSauKhuyenMai() {
        double giaTriGiam = 0;
        double tongTienGoc = layTongTienGoc();

        if (khuyenMaiApDungDTO != null) {
            if (khuyenMaiApDungDTO.getLoaiKhuyenMai() == 0) {
                // giam theo %
                double phanTram = khuyenMaiApDungDTO.getGiaTriPhanTram() / 100.0;
                giaTriGiam = tongTienGoc * phanTram;
            } else if (khuyenMaiApDungDTO.getLoaiKhuyenMai() == 1) {
                // giam theoso tien
                giaTriGiam = khuyenMaiApDungDTO.getGiaTriTien();
            }

        }
        return giaTriGiam;
    }


    private double layTongTienGoc() {
        double tongTienHienTai = danhSachThongTinVe.stream()
                .mapToDouble(ThongTinDatVeDTO::getGiaVe)
                .sum();

        return tongTienHienTai;
    }

    private ChiTietVeTraDTO timVeDoiTheoMaVe(String maVe) {
        return timVeDoi(maVe, "MA_VE");
    }

    private ChiTietVeTraDTO timVeDoi(String value, String searchType) {
        TraVeSearchRequest payload = new TraVeSearchRequest(value, searchType);
        Request request = new Request(CommandType.SEARCH_DOI_VE, payload);
        Response response = socketClient.send(request);

        if (!response.isSuccess() || !(response.getData() instanceof ChiTietVeTraDTO chiTietVeTraDTO)) {
            return null;
        }

        return chiTietVeTraDTO;
    }

    private DoiVePreviewDTO layPreviewDoiVe(String maVe) {
        MaVeRequest payload = new MaVeRequest(maVe);
        Request request = new Request(CommandType.GET_DOI_VE_PREVIEW, payload);
        Response response = socketClient.send(request);

        if (!response.isSuccess() || !(response.getData() instanceof DoiVePreviewDTO previewDTO)) {
            return null;
        }

        return previewDTO;
    }

    private ChuyenTauDTO timChuyenTauDoiVe(String gaDi, String gaDen, String loaiGhe, String ngayGioKhoiHanh) {
        DoiVeChuyenTauRequest payload = new DoiVeChuyenTauRequest(gaDi, gaDen, loaiGhe, ngayGioKhoiHanh);
        Request request = new Request(CommandType.SEARCH_DOI_VE_CHUYEN_TAU, payload);
        Response response = socketClient.send(request);

        if (!response.isSuccess() || !(response.getData() instanceof ChuyenTauDTO chuyenTauDTO)) {
            return null;
        }

        return chuyenTauDTO;
    }

    @SuppressWarnings("unchecked")
    private List<GheDTO> laySoDoGhe(String maChuyenTau) {
        SoDoGheRequest payload = new SoDoGheRequest(maChuyenTau);
        Request request = new Request(CommandType.GET_SO_DO_GHE, payload);
        Response response = socketClient.send(request);

        if (!response.isSuccess() || !(response.getData() instanceof List<?> list)) {
            return Collections.emptyList();
        }

        return (List<GheDTO>) list;
    }

    private KhuyenMaiApDungDTO timKhuyenMaiTheoMa(String maKhuyenMai) {
        KhuyenMaiRequest payload = new KhuyenMaiRequest(maKhuyenMai);
        Request request = new Request(CommandType.LOOKUP_KHUYEN_MAI, payload);
        Response response = socketClient.send(request);

        if (!response.isSuccess() || !(response.getData() instanceof KhuyenMaiApDungDTO khuyenMaiDTO)) {
            return null;
        }

        return khuyenMaiDTO;
    }

    private Response xacNhanDoiVeServer(ThongTinDoiVeDTO thongTinDoiVeDTO) {
        Request request = new Request(CommandType.CONFIRM_DOI_VE, thongTinDoiVeDTO);
        return socketClient.send(request);
    }

    public void ketThuc(ActionEvent actionEvent) {
        GiaoDienUtils.anHienFrame(pnHinhThucThanhToan, false, thongTinDoiVe, true);
        GiaoDienUtils.clearTextFields(main);
        soDoGhe.getChildren().clear();
        pnDanhSachChuyenDiMotChieu.getChildren().clear();
    }


    @FXML
    public void onInVe(ActionEvent actionEvent) {
        try {
            HoaDonVeDTO hd = CurrentUser.getHoaDonVeDTO();
            ChiTietVeTraDTO chiTietVeTraDTO = CurrentUser.getChiTietVeTraDTO();
            double thanhTien = CurrentUser.getThanhTien();
            double tongTienSauCung = CurrentUser.getTongTienSauCung();
            ThongTinDatVeDTO thongTinDatVeDTO = CurrentUser.getThongTinDatVeDTO();
            ThongTinChuyenTauDTO thongTinChuyenTauDTO = CurrentUser.getThongTinChuyenTauDTO();
            ChiTietVeTraDTO veTau = CurrentUser.getChiTietVeTraDTO();
            VeTauDTO vt = CurrentUser.getVeTauDTO();
//            ChuyenTauDTO chuyenTauDTO = CurrentUser.getChuyenTauDTO();

            String maVe = veTau.getMaVe();
            String tenHanhKhach = hd.getTenHanhKhach();
            String tenTau = chiTietVeTraDTO.getTenChuyenTau();
            String gaDi = chiTietVeTraDTO.getGaDi();
            String gaDen = chiTietVeTraDTO.getGaDen();
            String ngayGioKhoiHanh = hd.getNgayGioKhoiHanh();

            GheDTO ghe = hd.getGheDTO();

            String maToaTau = ghe.getMaToa();
            String tenKhoangTau = ghe.getTenKhoang();
            int viTriGhe = ghe.getViTriGhe();

            String maLoaiVe = hd.getTenLoaiVe();
            String tenLoaiGhe = hd.getTenLoaiGhe();
            double giaVeGoc = thanhTien;
            String maKhuyenMai = "Không có";
            double tongTienPhaiTra = tongTienSauCung;
//            String phuongThucThanhToan = "Chuyển khoản (Ngân hàng MBBank)";

            HoaDonVeDTO hoaDonVe = new HoaDonVeDTO();
            hoaDonVe.setMaVe(maVe);
            hoaDonVe.setTenHanhKhach(tenHanhKhach);
            hoaDonVe.setTenTau(tenTau);
            hoaDonVe.setGaDi(gaDi);
            hoaDonVe.setGaDen(gaDen);
            hoaDonVe.setNgayGioKhoiHanh(ngayGioKhoiHanh);
            hoaDonVe.setTenKhoangTau(tenKhoangTau);
            hoaDonVe.setMaToaTau(maToaTau);
//            hoaDonVe.setSoGhe(ghe);
            hoaDonVe.setTenLoaiGhe(tenLoaiGhe);
            hoaDonVe.setTenLoaiVe(maLoaiVe);
            hoaDonVe.setGiaVe(giaVeGoc);
            hoaDonVe.setMaKhuyenMai(maKhuyenMai);
            hoaDonVe.setTongTien(tongTienSauCung);


            // BƯỚC 3: MỞ CỬA SỔ VÀ TRUYỀN DỮ LIỆU
            List<HoaDonVeDTO> hoaDonVeDTOS = new ArrayList<>();
            hoaDonVeDTOS.add(hoaDonVe);
            moCuaSoHoaDonVe(hoaDonVeDTOS);

        } catch (Exception e) {
            // Xử lý lỗi nếu việc thu thập dữ liệu thất bại
            System.err.println("Lỗi khi chuẩn bị dữ liệu hoặc mở cửa sổ Hóa đơn: " + e.getMessage());
            // Hiển thị cảnh báo lỗi cho người dùng (Alert Dialog)
        }
    }


    private void moCuaSoHoaDonVe(List<HoaDonVeDTO> hoaDonVe) {
        try {

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tauga/view/nv/ve/HoaDonVeTau.fxml"));
            Parent root = loader.load();

            HoaDonVeTau controller = loader.getController();

            controller.setHoanVeData(hoaDonVe);

            Stage stage = new Stage();
            stage.setTitle("Chi Tiết Hóa Đơn Vé Tàu");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (Exception e) {
            System.err.println("Không thể tải FXML Hóa đơn hoặc Controller: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

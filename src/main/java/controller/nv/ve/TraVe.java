package controller.nv.ve;

import dto.*;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.MaVeRequest;
import network.common.request.TraVeSearchRequest;
import utils.GiaoDienUtils;
import utils.QuetQrUtils;
import utils.TauGaUtils;
import utils.consts.CurrentUser;
import utils.enums.Title;

public class TraVe {
    @FXML
    private AnchorPane main;
    @FXML
    private HBox HBoxMaVe;
    @FXML
    private HBox HBoxThongTinKH;
    @FXML
    private RadioButton radMaVe;
    @FXML
    private RadioButton radThongTinKH;
    @FXML
    private VBox thongTinTraVe;
    @FXML
    private VBox traVeThanhCong;
    @FXML
    private TextField txtMaVe;
    @FXML
    private TextField txtSDT;
    @FXML
    private TextField txtCCCD;

    @FXML
    private TableView<VeTauDTO> table;
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
    private final SocketClient socketClient = new SocketClient();
    //txt cua thong tin khach hang
    @FXML
    private TextField txtHoTen;
    @FXML
    private TextField txtSDT_KH;
    @FXML
    private TextField txtCCCD_KH;
    @FXML
    private TextField txtNgaySinh;

    //txt của chi tiet ve tra
    @FXML
    private TextField txtMaVe_VeTra;
    @FXML
    private TextField txtHanhKhach;
    @FXML
    private TextField txtNgayKhoiHanh;
    @FXML
    private TextField txtNgayDen;
    @FXML
    private TextField txtToa_Khoang_Ghe;
    @FXML
    private TextField txtNgayDat;
    @FXML
    private TextField txtCCCD_VeTra;
    @FXML
    private TextField txtChuyenTau;
    @FXML
    private TextField txtMaHoaDon;
    @FXML
    private TextField txtNguoiLapHD;
    @FXML
    private TextField txtNgayLapHD;
    @FXML
    private TextField txtGiaVe;
    @FXML
    private TextField txtTongTien;
    @FXML
    private TextField txtKhachHang;
    @FXML
    private TextField txtPhiTraVe;
    @FXML
    private TextField txtGiaVeDaTra;
    @FXML
    private TextField txtPhiTraVePhaiTra;
    @FXML
    private TextField txtSoTienCanHoanLai;
    @FXML
    private SplitPane mainSplit;

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
        colMaVe.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("maVe"));
        colTenKH.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tenKH"));
        colChuyenTau.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("maChuyenTau"));
        colNgayDat.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("ngayDatStr"));
        colGiaVe.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("tienVeFormat"));
        colTrangThaiVe.setCellValueFactory(new javafx.scene.control.cell.PropertyValueFactory<>("trangThaiVe"));


        QuetQrUtils.setOnCodeScanned(code -> {
            txtMaVe.setText(code);
            //   System.out.println("📦 Nhận mã QR ở màn hình TraVe: " + code);
        });

        if (CurrentUser.isIsFromTraCuu()) {
            loadData();
        }
    }

    private void loadData() {
        var veTau = CurrentUser.getMaVeFromTraCuu();
        var maVe = veTau.getMaVe();
        if (maVe == null) {
            radThongTinKH.setSelected(Boolean.TRUE);
            txtSDT.setText(CurrentUser.getSdt());
            txtCCCD.setText(CurrentUser.getCccd());
            table.getItems().clear();
            table.getItems().add(veTau);
        }
        txtMaVe.setText(veTau.getMaVe());
        table.getItems().clear();
        table.getItems().add(veTau);
    }


    @FXML
    public void TimKiem(ActionEvent actionEvent) {
        if (radMaVe.isSelected()) {
            String maVe = txtMaVe.getText().trim();
            if (TauGaUtils.StringUtils.isEmpty(maVe)) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Vui lòng nhập mã vé!");
                return;
            }

            VeTauDTO veTau = timVeTheoMaVe(maVe);
            table.getItems().clear();
            if (veTau != null) {
                table.getItems().add(veTau);
            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Không tim thấy vé tàu!");
            }

        } else if (radThongTinKH.isSelected()) {
            String sdt = txtSDT.getText().trim();
            String cccd = txtCCCD.getText().trim();
            if (TauGaUtils.StringUtils.isEmpty(sdt) && TauGaUtils.StringUtils.isEmpty(cccd)) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Vui lòng nhập số điện thoại hoặc CCCD!");
                return;
            }

            if (!TauGaUtils.StringUtils.isEmpty(sdt)) {
                VeTauDTO veTau = timVeTheoSdt(sdt);
                table.getItems().clear();

                if (veTau != null) {
                    table.getItems().add(veTau);
                } else {
                    GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Không tim thấy vé tàu!");
                }
            } else if (!TauGaUtils.StringUtils.isEmpty(cccd)) {

                VeTauDTO veTau = timVeTheoCccd(cccd);
                table.getItems().clear();

                if (veTau != null) {
                    table.getItems().add(veTau);
                } else {
                    GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Không tim thấy vé tàu!");
                }
            }

        }
    }

    @FXML
    public void TraVe(ActionEvent actionEvent) {
        VeTauDTO ve = table.getSelectionModel().getSelectedItem();
        if (ve == null) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Vui lòng chọn vé cần trả");
            return;
        }

        TraVePreviewDTO preview = layPreviewTraVe(ve.getMaVe());
        if (preview == null) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Không lấy được thông tin trả vé");
            return;
        }

        CurrentUser.setChiTietHoaDonDTO(preview.getChiTietHoaDon());
        CurrentUser.setChiTietVeTraDTO(preview.getChiTietVeTra());

        if (!preview.isCoTheTraVe()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), preview.getMessage());
            return;
        }

        hienThiPreviewTraVe(preview.getKhachHang(), preview.getChiTietVeTra(), preview.getChiTietHoaDon());
    }


    public void xacNhan(ActionEvent actionEvent) {
        ChiTietVeTraDTO chiTietVeTraDTO = CurrentUser.getChiTietVeTraDTO();
        Response response = xacNhanTraVe(chiTietVeTraDTO.getMaVe());
        if (!response.isSuccess()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "", response.getMessage());
            return;
        }

        GiaoDienUtils.anHienFrame(traVeThanhCong, true, thongTinTraVe, true);

        ChiTietHoaDonDTO chiTietHoaDonDTO = CurrentUser.getChiTietHoaDonDTO();

        txtGiaVeDaTra.setText(chiTietHoaDonDTO.getGiaVeStr());
        txtPhiTraVePhaiTra.setText(chiTietHoaDonDTO.getPhiTraStr());
        txtSoTienCanHoanLai.setText(chiTietHoaDonDTO.getTongTienTraLaiStr());


    }

    public void traVeKhac(ActionEvent actionEvent) {
        GiaoDienUtils.anHienFrame(traVeThanhCong, false, thongTinTraVe, true);
        mainSplit.setDividerPositions(0.5);
        GiaoDienUtils.clearTextFields(main);
    }

    private VeTauDTO timVeTheoMaVe(String maVe) {
        return timVe(maVe, "MA_VE");
    }

    private VeTauDTO timVeTheoSdt(String sdt) {
        return timVe(sdt, "SDT");
    }

    private VeTauDTO timVeTheoCccd(String cccd) {
        return timVe(cccd, "CCCD");
    }

    private VeTauDTO timVe(String value, String searchType) {
        TraVeSearchRequest payload = new TraVeSearchRequest(value, searchType);
        Request request = new Request(CommandType.SEARCH_TRA_VE, payload);
        Response response = socketClient.send(request);

        if (!response.isSuccess() || !(response.getData() instanceof VeTauDTO veTauDTO)) {
            return null;
        }

        return veTauDTO;
    }

    private TraVePreviewDTO layPreviewTraVe(String maVe) {
        MaVeRequest payload = new MaVeRequest(maVe);
        Request request = new Request(CommandType.GET_TRA_VE_PREVIEW, payload);
        Response response = socketClient.send(request);

        if (!response.isSuccess() || !(response.getData() instanceof TraVePreviewDTO previewDTO)) {
            return null;
        }

        return previewDTO;
    }

    private Response xacNhanTraVe(String maVe) {
        MaVeRequest payload = new MaVeRequest(maVe);
        Request request = new Request(CommandType.CONFIRM_TRA_VE, payload);
        return socketClient.send(request);
    }

    private void hienThiPreviewTraVe(KhachHangDTO kh, ChiTietVeTraDTO chiTietVeTraDTO, ChiTietHoaDonDTO chiTietHoaDonDTO) {
        if (kh != null) {
            txtHoTen.setText(kh.getTenKH());
            txtSDT_KH.setText(kh.getSoDienThoai());
            txtCCCD_KH.setText(kh.getCccd());
            txtNgaySinh.setText(kh.getNgaySinhStr());
        }

        if (chiTietVeTraDTO != null) {
            txtMaVe_VeTra.setText(chiTietVeTraDTO.getMaVe());
            txtHanhKhach.setText(chiTietVeTraDTO.getTenKH());
            txtNgayKhoiHanh.setText(chiTietVeTraDTO.getNgayKhoiHanhStr());
            txtNgayDen.setText(chiTietVeTraDTO.getNgayDenStr());
            txtToa_Khoang_Ghe.setText(chiTietVeTraDTO.getToaKhoangGhe());
            txtCCCD_VeTra.setText(chiTietVeTraDTO.getCccd());
            txtNgayDat.setText(chiTietVeTraDTO.getNgayDatStr());
            txtChuyenTau.setText(chiTietVeTraDTO.getMaChuyenTau());
        }

        if (chiTietHoaDonDTO != null) {
            txtMaHoaDon.setText(chiTietHoaDonDTO.getMaHD());
            txtNguoiLapHD.setText(chiTietHoaDonDTO.getNguoiLapHD());
            txtNgayLapHD.setText(chiTietHoaDonDTO.getNgayLapHDStr());
            txtGiaVe.setText(chiTietHoaDonDTO.getGiaVeStr());
            txtKhachHang.setText(chiTietHoaDonDTO.getKhachHang());
            txtTongTien.setText(chiTietHoaDonDTO.getTongTienTraLaiStr());
            txtPhiTraVe.setText(chiTietHoaDonDTO.getPhiTraStr());
        }
    }

}

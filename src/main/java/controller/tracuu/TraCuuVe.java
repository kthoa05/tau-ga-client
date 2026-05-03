package controller.tracuu;

import controller.nv.ve.HoaDonVeTau;
import dto.HoaDonVeDTO;
import dto.NhanVienDTO;
import dto.VeTauDTO;
import entity.enums.VaiTroNhanVien;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
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
import utils.enums.ManHinh;
import utils.enums.Title;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class TraCuuVe {
    @FXML
    private RadioButton radMaVe;
    @FXML private RadioButton radThongTinKH;
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
    private Button btnDoiVe;
    @FXML
    private Button btnTraVe;
    @FXML
    private Button btnInVe;
    @FXML
    private Button btnTimKiem;
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
    @FXML
    private BorderPane contentArea;

    private final SocketClient socketClient = new SocketClient();

    private QuetQrUtils scanServer;
    @FXML
    public void initialize() {
        NhanVienDTO nv = CurrentUser.getNhanVien();
        if(nv.getVaiTro() != VaiTroNhanVien.NV_BAN_VE){
            btnTimKiem.setVisible(true);
            btnDoiVe.setVisible(false);
            btnTraVe.setVisible(false);
            btnInVe.setVisible(false);
        }else{
            btnTimKiem.setVisible(true);
            btnDoiVe.setVisible(true);
            btnTraVe.setVisible(true);
            btnInVe.setVisible(true);
        }


        ToggleGroup group = new ToggleGroup();
        radMaVe.setToggleGroup(group);
        radThongTinKH.setToggleGroup(group);
        radMaVe.setSelected(true);

        updateHBoxToggle();

        group.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
            updateHBoxToggle();
        });

        colMaVe.setCellValueFactory(new PropertyValueFactory<>("maVe"));
        colTenKH.setCellValueFactory(new PropertyValueFactory<>("tenKH"));
        colChuyenTau.setCellValueFactory(new PropertyValueFactory<>("maChuyenTau"));
        colNgayDat.setCellValueFactory(new PropertyValueFactory<>("ngayDatStr"));
        colGiaVe.setCellValueFactory(new PropertyValueFactory<>("tienVeFormat"));
        colTrangThaiVe.setCellValueFactory(new PropertyValueFactory<>("trangThaiVe"));

        try {
            // ✅ Bắt đầu server HTTPS để nhận dữ liệu từ /scan.html
            QuetQrUtils.start(8000, "C:/Users/lethoa/Documents/keystore.p12", "123456");

            // ✅ Khi web gửi mã QR về -> chạy callback này
            QuetQrUtils.setOnCodeScanned(code -> {
                System.out.println("🎫 Mã vé quét được từ web: " + code);
                Platform.runLater(() -> {
                    // Hiển thị mã QR vừa nhận lên ô nhập
                    txtMaVe.setText(code);
                    radMaVe.setSelected(true);
                    updateHBoxToggle();

                    // Tự động tìm vé
                    timKiem(null);
                });
            });

            System.out.println("✅ Đã khởi động server quét QR thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("❌ Không thể khởi động server quét QR: " + e.getMessage());
        }
    }

        private void updateHBoxToggle () {
            boolean isMaVe = radMaVe.isSelected();
            HBoxMaVe.setVisible(isMaVe);
            HBoxMaVe.setManaged(isMaVe);
            HBoxThongTinKH.setVisible(!isMaVe);
            HBoxThongTinKH.setManaged(!isMaVe);
        }


    @FXML
    public void timKiem(ActionEvent actionEvent){
        if (radMaVe.isSelected()) {
            String maVe = txtMaVe.getText().trim();
            if (TauGaUtils.StringUtils.isEmpty(maVe)) {
                GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Vui lòng nhập mã vé!");
                return;
            }

            VeTauDTO veTau = timVeTheoMaVe(maVe);
            table.getItems().clear();
            if (veTau != null) {
                CurrentUser.setMaVeFromTraCuu(veTau);
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
                CurrentUser.setSdt(sdt);
                CurrentUser.setCccd(cccd);
                VeTauDTO veTau = timVeTheoSDT(sdt);
                table.getItems().clear();

                if (veTau != null) {
                    CurrentUser.setMaVeFromTraCuu(veTau);
                    table.getItems().add(veTau);
                } else {
                    GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Không tim thấy vé tàu!");
                }
            } else if (!TauGaUtils.StringUtils.isEmpty(cccd)) {

                VeTauDTO veTau = timVeTheoCCCD(cccd);
                CurrentUser.setVeTauDTO(veTau);
                table.getItems().clear();

                if (veTau != null) {
                    CurrentUser.setMaVeFromTraCuu(veTau);
                    table.getItems().add(veTau);
                } else {
                    GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Không tim thấy vé tàu!");
                }
            }

        }
    }

    @FXML
    public void xoaTrang(ActionEvent event) {
        radMaVe.setSelected(true);
        radThongTinKH.setSelected(false);
        txtMaVe.clear();
        txtSDT.clear();
        txtCCCD.clear();
        HBoxMaVe.setVisible(true);
        HBoxMaVe.setManaged(true);
        HBoxThongTinKH.setVisible(false);
        HBoxThongTinKH.setManaged(false);
        table.getItems().clear();
        table.refresh();
    }

    @FXML
    public void inVe(ActionEvent actionEvent) {

        VeTauDTO veTau = table.getSelectionModel().getSelectedItem();
        if (veTau == null) {
            GiaoDienUtils.showThongBao(
                    Alert.AlertType.WARNING,
                    Title.THONG_BAO.getTitle(),
                    "Vui lòng chọn vé cần in!"
            );
            return;
        }

        List<HoaDonVeDTO> hoaDonVeDTOS = new ArrayList<>();
        HoaDonVeDTO hoaDonVe = layHoaDonVe(veTau.getMaVe());
        if (hoaDonVe == null) {
            GiaoDienUtils.showThongBao(
                    Alert.AlertType.WARNING,
                    Title.THONG_BAO.getTitle(),
                    "Không tìm thấy thông tin vé để in!"
            );
            return;
        }
        hoaDonVeDTOS.add(hoaDonVe);
        moCuaSoHoaDonVe(hoaDonVeDTOS);
    }


    private void moCuaSoHoaDonVe(List<HoaDonVeDTO> hoaDonVe) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/tauga/view/nv/ve/HoaDonVeTau.fxml")
            );
            Parent root = loader.load();

            HoaDonVeTau controller = loader.getController();
            controller.setHoanVeData(hoaDonVe);

            Stage stage = new Stage();
            stage.setTitle("Chi Tiết Hóa Đơn Vé Tàu");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void doiVe(ActionEvent actionEvent){
        var dieuKien = this.dieuKienDoiTra();
        if (!dieuKien){
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Không thể đổi vé! Vì đã quá thời gian quy định trước giờ khởi hành tối thiểu 12 giờ)");
            return;
        }
        CurrentUser.setIsFromTraCuu(Boolean.TRUE);
        GiaoDienUtils.ManHinhUtils.loadContent(contentArea, ManHinh.DoiVe);
    }

    @FXML
    public void traVe(ActionEvent actionEvent){
        var dieuKien = this.dieuKienDoiTra();
        if (!dieuKien){
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Không thể trả vé! Vì đã quá thời gian quy định trước giờ khởi hành tối thiểu 12 giờ)");
            return;
        }
        CurrentUser.setIsFromTraCuu(Boolean.TRUE);
        GiaoDienUtils.ManHinhUtils.loadContent(contentArea, ManHinh.TraVe);
    }

    private boolean dieuKienDoiTra(){
        CurrentUser.setIsFromTraCuu(Boolean.TRUE);
        var veTau = CurrentUser.getMaVeFromTraCuu();
        if (veTau == null) {
            return false;
        }
        LocalDateTime thoiGianKhoiHanh = layThoiGianKhoiHanh(veTau.getMaChuyenTau());
        return thoiGianKhoiHanh != null && LocalDateTime.now().plusHours(12).isBefore(thoiGianKhoiHanh);
    }

    private VeTauDTO timVeTheoMaVe(String maVe) {
        return timVe(new TraVeSearchRequest("MA_VE", maVe));
    }

    private VeTauDTO timVeTheoSDT(String sdt) {
        return timVe(new TraVeSearchRequest("SDT", sdt));
    }

    private VeTauDTO timVeTheoCCCD(String cccd) {
        return timVe(new TraVeSearchRequest("CCCD", cccd));
    }

    private VeTauDTO timVe(TraVeSearchRequest payload) {
        Request request = new Request(CommandType.SEARCH_TRA_VE, payload);
        Response response = socketClient.send(request);
        if (response == null || !response.isSuccess()) {
            return null;
        }
        return (VeTauDTO) response.getData();
    }

    private HoaDonVeDTO layHoaDonVe(String maVe) {
        Request request = new Request(CommandType.GET_HOA_DON_VE, new MaVeRequest(maVe));
        Response response = socketClient.send(request);
        if (response == null || !response.isSuccess()) {
            return null;
        }
        return (HoaDonVeDTO) response.getData();
    }

    private LocalDateTime layThoiGianKhoiHanh(String maChuyenTau) {
        Request request = new Request(CommandType.GET_THOI_GIAN_KHOI_HANH, maChuyenTau);
        Response response = socketClient.send(request);
        if (response == null || !response.isSuccess()) {
            return null;
        }
        return (LocalDateTime) response.getData();
    }
}

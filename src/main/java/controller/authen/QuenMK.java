package controller.authen;

import entity.TaiKhoanEntity;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import service.ITaiKhoanService;
import service.impl.TaiKhoanServiceImpl;
import utils.GiaoDienUtils;
import utils.consts.CurrentUser;
import utils.enums.ManHinh;

public class QuenMK {
    //private final SocketClient socketClient = new SocketClient();
    private final ITaiKhoanService taiKhoanService = new TaiKhoanServiceImpl();
    @FXML
    private Button btnQuayLai;
    @FXML
    private VBox vboxOTP;
    @FXML
    private Button btnXacNhanOtp;
    @FXML
    private TextField txtTenDangNhap_Quen;
    @FXML
    private TextField txtCccd_Quen;
    @FXML
    private TextField txtSoDienThoai_Quen;
    @FXML
    private TextField otp1;
    @FXML
    private TextField otp2;
    @FXML
    private TextField otp3;
    @FXML
    private TextField otp4;
    @FXML
    private TextField otp5;
    @FXML
    private TextField otp6;

    public void quayLai(ActionEvent actionEvent) {
        GiaoDienUtils.ManHinhUtils.chuyenManHinh(btnQuayLai, ManHinh.DangNhap.getPath(), "Đăng nhập");
    }

    public void tiepTuc(ActionEvent actionEvent) {
        try {
            String tenDangNhap = txtTenDangNhap_Quen.getText().trim();
            String cccd = txtCccd_Quen.getText().trim();
            String soDienThoai = txtSoDienThoai_Quen.getText().trim();

            if (tenDangNhap.isEmpty() || cccd.isEmpty() || soDienThoai.isEmpty()) {
                throw new Exception("Vui lòng nhập đầy đủ thông tin!");
            }

            boolean ok = taiKhoanService.kiemTraThongTin(tenDangNhap, cccd, soDienThoai);
            if (!ok) {
                throw new Exception("Thông tin không đúng!");
            }

            TaiKhoanEntity taiKhoan = new TaiKhoanEntity();
            taiKhoan.setTenDangNhap(tenDangNhap);
            CurrentUser.setTaiKhoan(taiKhoan);

            vboxOTP.setVisible(true);
            vboxOTP.setManaged(true);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Lỗi");
            alert.setHeaderText("Không thể tiếp tục");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    @FXML
    private void dongOtp(ActionEvent e) {
        vboxOTP.setVisible(false); // Ẩn khung OTP
    }
    public void xacnhanOtp(ActionEvent actionEvent){
        String otpNhap = otp1.getText().trim() + otp2.getText().trim() + otp3.getText().trim() +
                otp4.getText().trim() + otp5.getText().trim() + otp6.getText().trim();

        if (otpNhap.length() != 6) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "", "Vui lòng nhập đầy đủ 6 số để xác nhận.");
            return;
        }

        String username = CurrentUser.getTaiKhoan() != null ? CurrentUser.getTaiKhoan().getTenDangNhap() : "";
        boolean ok = taiKhoanService.verifyOtp(username, otpNhap);
        if (ok)  {
            GiaoDienUtils.ManHinhUtils.chuyenManHinh(btnQuayLai, ManHinh.DoiMK.getPath(), "Đổi mật khẩu");
        } else {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "", "OTP không đúng!");        }
    }



}

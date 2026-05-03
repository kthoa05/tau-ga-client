package controller.authen;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import service.ITaiKhoanService;
import service.impl.TaiKhoanServiceImpl;
import utils.GiaoDienUtils;
import utils.consts.CurrentUser;
import utils.enums.ManHinh;


public class DoiMK {
    @FXML
    private Button btnQuayLaiDMK;
    @FXML
    private Button btnXacNhanDMK;
    @FXML
    private TextField txtMatKhauMoi;
    @FXML
    private TextField txtNhapLaiMK;
    @FXML
    private CheckBox chkHienMatKhauMoi;
    @FXML
    private TextField txtMatKhauMoiHien;
    @FXML
    private CheckBox chkHienNhapLaiMatKhau;
    @FXML
    private TextField txtNhapLaiMKHien;
    //private final SocketClient socketClient = new SocketClient();
    private final ITaiKhoanService taiKhoanService = new TaiKhoanServiceImpl();
    public void xacnhanDMK(ActionEvent actionEvent) {

        String matKhauMoi = txtMatKhauMoi.getText().trim();
        String nhapLaiMK = txtNhapLaiMK.getText().trim();

        if (matKhauMoi.isEmpty() || nhapLaiMK.isEmpty()) {
            hienThongBao("Lỗi", "Vui lòng nhập đầy đủ mật khẩu mới!");
            return;
        }

        if (!matKhauMoi.equals(nhapLaiMK)) {
            hienThongBao("Lỗi", "Mật khẩu xác nhận không khớp!");
            return;
        }

        try {
            String tenDangNhap = CurrentUser.getTaiKhoan().getTenDangNhap();
            boolean capNhat = taiKhoanService.changePassword(tenDangNhap, matKhauMoi);

            if (capNhat) {
                hienThongBao("Thành công", "Đổi mật khẩu thành công!");
                GiaoDienUtils.ManHinhUtils.chuyenManHinh(btnXacNhanDMK, ManHinh.DangNhap.getPath(), "Đăng nhập");
            } else {
                hienThongBao("Lỗi", "Đổi mật khẩu thất bại!");
            }
        } catch (Exception e) {
            e.printStackTrace();
            hienThongBao("Lỗi", "Đã xảy ra lỗi khi cập nhật mật khẩu!");
        }
    }

    public void quayLaiDMK(ActionEvent actionEvent) {
        GiaoDienUtils.ManHinhUtils.chuyenManHinh(btnQuayLaiDMK, ManHinh.QuenMK.getPath(), "Quên mật khẩu");
    }
    private void hienThongBao(String tieuDe, String noiDung) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        if (tieuDe.equalsIgnoreCase("Lỗi")) {
            alert.setAlertType(Alert.AlertType.ERROR);
        }
        alert.setTitle(tieuDe);
        alert.setHeaderText(null);
        alert.setContentText(noiDung);
        alert.showAndWait();
    }
    public void hienMatKhauMoi(ActionEvent actionEvent){
        if (chkHienMatKhauMoi.isSelected()) {
            txtMatKhauMoiHien.setText(txtMatKhauMoi.getText());
            txtMatKhauMoiHien.setVisible(true);
            txtMatKhauMoiHien.setManaged(true);
            txtMatKhauMoi.setVisible(false);
            txtMatKhauMoi.setManaged(false);
        }else {
            txtMatKhauMoi.setText(txtMatKhauMoi.getText());
            txtMatKhauMoiHien.setVisible(false);
            txtMatKhauMoiHien.setManaged(false);
            txtMatKhauMoi.setVisible(true);
            txtMatKhauMoi.setManaged(true);
        }
    }
    public void hienNhapLaiMatKhau(ActionEvent actionEvent){
        if (chkHienNhapLaiMatKhau.isSelected()) {
            txtNhapLaiMKHien.setText(txtNhapLaiMK.getText());
            txtNhapLaiMKHien.setVisible(true);
            txtNhapLaiMKHien.setManaged(true);
            txtNhapLaiMK.setVisible(false);
            txtNhapLaiMK.setManaged(false);
        }else {
            txtNhapLaiMK.setText(txtNhapLaiMK.getText());
            txtNhapLaiMKHien.setVisible(false);
            txtNhapLaiMKHien.setManaged(false);
            txtNhapLaiMK.setVisible(true);
            txtNhapLaiMK.setManaged(true);
        }
    }



}

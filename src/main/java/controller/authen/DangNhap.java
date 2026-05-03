package controller.authen;

import dto.NhanVienDTO;
import dto.TaiKhoanDTO;
import entity.enums.VaiTroNhanVien;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.LoginRequest;
import utils.GiaoDienUtils;
import utils.TauGaUtils;
import utils.consts.CurrentUser;
import utils.enums.ManHinh;
import utils.enums.Title;

public class DangNhap {
    @FXML
    private CheckBox chkHienMatKhau;

    @FXML
    private TextField txtMatKhauHien;

    @FXML
    private Button btnDangNhap;

    @FXML
    private TextField txtTenDangNhap;

    @FXML
    private PasswordField txtMatKhau;

    @FXML
    private Hyperlink btnQuenMK;

    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        txtTenDangNhap.setOnAction(this::dangNhap);
        txtMatKhau.setOnAction(this::dangNhap);
        txtMatKhauHien.setOnAction(this::dangNhap);

        txtTenDangNhap.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case DOWN:
                    if (txtMatKhau.isVisible())
                        txtMatKhau.requestFocus();
                    else
                        txtMatKhauHien.requestFocus();
                    break;
            }
        });

        if (btnDangNhap != null) {
            btnDangNhap.setDefaultButton(true);
        }
    }


    public void dangNhap(ActionEvent actionEvent) {
        String userName = txtTenDangNhap.getText() == null ? "" : txtTenDangNhap.getText().trim();
        String pass = txtMatKhau.isVisible()
                ? (txtMatKhau.getText() == null ? "" : txtMatKhau.getText().trim())
                : (txtMatKhauHien.getText() == null ? "" : txtMatKhauHien.getText().trim());

        if (TauGaUtils.StringUtils.isEmpty(userName)) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Chưa điền tên đăng nhập");
            return;
        }

        if (TauGaUtils.StringUtils.isEmpty(pass)) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Chưa điền mật khẩu");
            return;
        }

        TaiKhoanDTO taiKhoan = login(userName, pass);
        if (taiKhoan == null) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, Title.THONG_BAO.getTitle(), "Tên đăng nhập hoặc mật khẩu sai");
            return;
        }

        NhanVienDTO nv = getNhanVienById(taiKhoan.getMaNV());
        if (nv == null) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, Title.THONG_BAO.getTitle(), "Không lấy được thông tin nhân viên");
            return;
        }
        CurrentUser.setNhanVien(nv);
        Stage stage = (Stage) btnDangNhap.getScene().getWindow();

        if (nv.getVaiTro() == VaiTroNhanVien.NV_QUAN_LY) {
            GiaoDienUtils.ManHinhUtils.chuyenManHinh(stage, ManHinh.MainLayoutQL.getPath(), "Hệ thống quản lý vé tàu ");
            return;
        }

        GiaoDienUtils.ManHinhUtils.chuyenManHinh(stage, ManHinh.MainLayout.getPath(), "Hệ thống quản lý vé tàu");
    }

    private TaiKhoanDTO login(String username, String password) {
        LoginRequest loginPayload = new LoginRequest(username, password);
        Request loginRequest = new Request(CommandType.LOGIN, loginPayload);
        Response response = socketClient.send(loginRequest);

        if (!response.isSuccess() || !(response.getData() instanceof TaiKhoanDTO taiKhoanDTO)) {
            return null;
        }

        return taiKhoanDTO;
    }

    private NhanVienDTO getNhanVienById(String maNV) {
        Request request = new Request(CommandType.GET_NHAN_VIEN_BY_ID, maNV);
        Response response = socketClient.send(request);

        if (!response.isSuccess() || !(response.getData() instanceof NhanVienDTO nhanVienDTO)) {
            return null;
        }

        return nhanVienDTO;
    }

    public void quenMatKhau(ActionEvent actionEvent) {
        GiaoDienUtils.ManHinhUtils.chuyenManHinh(btnQuenMK, ManHinh.QuenMK.getPath(), "Quên mật khẩu");
    }

    public void hienMK(ActionEvent actionEvent) {
        if (chkHienMatKhau.isSelected()) {
            txtMatKhauHien.setText(txtMatKhau.getText());
            txtMatKhauHien.setVisible(true);
            txtMatKhauHien.setManaged(true);
            txtMatKhau.setVisible(false);
            txtMatKhau.setManaged(false);
        } else {
            txtMatKhau.setText(txtMatKhauHien.getText());
            txtMatKhauHien.setVisible(false);
            txtMatKhauHien.setManaged(false);
            txtMatKhau.setVisible(true);
            txtMatKhau.setManaged(true);
        }
    }
}

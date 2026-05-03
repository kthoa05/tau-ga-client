package controller.authen;

import dto.NhanVienDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.ThongTinNhanVienRequest;
import utils.GiaoDienUtils;
import utils.TauGaUtils;
import utils.consts.CurrentUser;


public class ThongTinCaNhan {

    @FXML
    private TextField txtMaNhanVien;
    @FXML
    private TextField txtTenNhanVien;
    @FXML
    private TextField txtNgaySinh;
    @FXML
    private TextField txtGioiTinh;

    @FXML
    private TextField txtCCCD;

    @FXML
    private TextField txtEmail;
    @FXML
    private TextField txtSoDienThoai;
    @FXML
    private Button btnCapNhat;

    // trạng thái: false = view-only, true = đang chỉnh sửa
    private boolean editing = true;
    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        NhanVienDTO nv = CurrentUser.getNhanVien();
        if (nv == null) return;
        txtMaNhanVien.setText(nv.getMaNV() != null ? nv.getMaNV() : "");
        txtTenNhanVien.setText(nv.getTenNV());
        txtNgaySinh.setText(nv.getNgaySinh() != null ? nv.getNgaySinh().toString() : "");
        txtCCCD.setText(nv.getCccd());
        txtGioiTinh.setText(String.valueOf(nv.getGioiTinh()));
        txtEmail.setText(nv.getEmail() != null ? nv.getEmail() : "");
        txtSoDienThoai.setText(nv.getSdt());
        setFieldsEditable(true);
        if (txtMaNhanVien != null) txtMaNhanVien.setFocusTraversable(false);
        if (btnCapNhat != null) btnCapNhat.setText("Cập nhật");

    }

    private void setFieldsEditable(boolean val) {

        txtEmail.setEditable(val);

        txtSoDienThoai.setEditable(val);
        txtMaNhanVien.setEditable(false);
        txtTenNhanVien.setEditable(false);
        txtNgaySinh.setEditable(false);
        txtGioiTinh.setEditable(false);
        txtCCCD.setEditable(false);
    }

    @FXML
    public void capNhatThongTinCaNhan(ActionEvent actionEvent) {
        NhanVienDTO nv = CurrentUser.getNhanVien();

        String email = txtEmail.getText();
        String sdt = txtSoDienThoai.getText();

        if (TauGaUtils.StringUtils.isEmpty(email)) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Dữ liệu không hợp lệ", "Vui lòng điền đầy đủ Email");
            return;
        }

        if (TauGaUtils.StringUtils.isEmpty(sdt)) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Dữ liệu không hợp lệ", "Vui lòng điền đầy đủ Số điện thoại");
            return;
        }

        Response response = updateThongTinCaNhan(nv.getMaNV(), email, sdt);
        if (!response.isSuccess()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", response.getMessage());
            return;
        }

        if (response.getData() instanceof NhanVienDTO updatedNhanVien) {
            CurrentUser.setNhanVien(updatedNhanVien);
        }

        GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thông báo", response.getMessage());


    }

    private Response updateThongTinCaNhan(String maNV, String email, String phoneNumber) {
        ThongTinNhanVienRequest payload = new ThongTinNhanVienRequest(maNV, email, phoneNumber);
        Request request = new Request(CommandType.UPDATE_NHAN_VIEN_CONTACT, payload);
        return socketClient.send(request);
    }
}



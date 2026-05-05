package controller.ql.nhanvien;

import dto.NhanVienDTO;
import entity.enums.TrangThaiLamViec;
import entity.enums.VaiTroNhanVien;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.ThongTinNhanVienRequest;
import utils.GiaoDienUtils;
import utils.TauGaUtils;

import java.time.LocalDate;
import java.util.List;

public class TraCuuNhanVien {
    @FXML
    public TextField txtSoDienThoai;
    @FXML
    public TextField txtCCCD;
    @FXML
    private TextField txtMaNhanVien;
    @FXML
    private TableView<NhanVienDTO> tableNhanVien;

    @FXML
    private TableColumn<NhanVienDTO, String> maNVCol;
    @FXML
    private TableColumn<NhanVienDTO, String> tenNVCol;
    @FXML
    private TableColumn<NhanVienDTO, Boolean> gioiTinhCol;
    @FXML
    private TableColumn<NhanVienDTO, String> ngaySinhCol;
    @FXML
    private TableColumn<NhanVienDTO, String> cccdCol;
    @FXML
    private TableColumn<NhanVienDTO, String> emailCol;
    @FXML
    private TableColumn<NhanVienDTO, String> sdtCol;
    @FXML
    private TableColumn<NhanVienDTO, LocalDate> ngayBatDauLamViecCol;
    @FXML
    private TableColumn<NhanVienDTO, VaiTroNhanVien> vaiTroCol;
    @FXML
    private TableColumn<NhanVienDTO, TrangThaiLamViec> trangThaiCol;

    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
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
        trangThaiCol.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(TrangThaiLamViec item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getLabel());
            }
        });

        ngayBatDauLamViecCol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null
                        ? null
                        : TauGaUtils.DateTimeUtils.convertLocalDateToString(item, TauGaUtils.FORMATTER_DATE));
            }
        });
    }

    @FXML
    public void onTraCuu(ActionEvent actionEvent) {
        String maNV = txtMaNhanVien.getText().trim();
        String sdt = txtSoDienThoai.getText().trim();
        String cccd = txtCCCD.getText().trim();

        if (maNV.isEmpty() && sdt.isEmpty() && cccd.isEmpty()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING,
                    "Thiếu dữ liệu",
                    "Nhập ít nhất 1 thông tin để tìm");
            return;
        }

        ThongTinNhanVienRequest req = new ThongTinNhanVienRequest(maNV, cccd, sdt);
        Response res = socketClient.send(new Request(CommandType.SEARCH_NHAN_VIEN, req));

        if (!res.isSuccess()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION,
                    "Không tìm thấy", res.getMessage());
            return;
        }

        Object data = res.getData();

        if (data instanceof List<?> list && !list.isEmpty() && list.get(0) instanceof NhanVienDTO) {
            @SuppressWarnings("unchecked")
            List<NhanVienDTO> dtoList = (List<NhanVienDTO>) list;
            tableNhanVien.getItems().setAll(dtoList);

        } else if (data instanceof NhanVienDTO dto) {
            tableNhanVien.getItems().setAll(dto);

        } else {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR,
                    "Lỗi", "Sai kiểu dữ liệu server trả về");
        }
    }

    @FXML
    public void lamMoi(ActionEvent actionEvent) {
        txtMaNhanVien.clear();
        txtCCCD.clear();
        txtSoDienThoai.clear();
        tableNhanVien.getItems().clear();
    }
}
package controller.ql.khuyenmai;

import dto.KhuyenMaiDTO;
import entity.enums.LoaiKhuyenMai;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.KhuyenMaiQLRequest;
import utils.GiaoDienUtils;
import utils.TauGaUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class QuanLyKhuyenMai {
    @FXML public TextArea taThongTinMoTa;
    @FXML public TextField txtTenKhuyenMai;
    @FXML public DatePicker dpNgayKetThuc;
    @FXML public TextField txtGiaTriPhanTram;
    @FXML public ComboBox<LoaiKhuyenMai> cbLoaiKhuyenMai;
    @FXML public TextField txtTimTheoMaKhuyenMai;
    @FXML public Button btnTim;
    @FXML public TextField txtMaKhuyenMai;
    @FXML public TextField txtGiaTriTien;
    @FXML public DatePicker dpNgayBatDau;
    @FXML public Button btnThemKhuyenMai;
    @FXML public Button btnXoaTrang;

    @FXML private TableColumn<KhuyenMaiDTO, String> colMaKhuyenMai;
    @FXML private TableColumn<KhuyenMaiDTO, String> colTenKhuyenMai;
    @FXML private TableColumn<KhuyenMaiDTO, String> colMoTa;
    @FXML private TableColumn<KhuyenMaiDTO, LocalDate> colNgayBatDau;
    @FXML private TableColumn<KhuyenMaiDTO, LocalDate> colNgayKetThuc;
    @FXML private TableColumn<KhuyenMaiDTO, Double> colGiaTriTien;
    @FXML private TableColumn<KhuyenMaiDTO, Integer> colGiaTriPhanTram;
    @FXML private TableColumn<KhuyenMaiDTO, LoaiKhuyenMai> colLoaiKhuyenMai;
    @FXML private TableView<KhuyenMaiDTO> tblKhuyenMai;

    private final SocketClient socketClient = new SocketClient();
    private ObservableList<KhuyenMaiDTO> dsKM;

    @FXML
    public void initialize() {
        dsKM = FXCollections.observableArrayList();

        txtMaKhuyenMai.setEditable(false);
        txtMaKhuyenMai.setStyle("-fx-control-inner-background: #F4F4F4;");

        colMaKhuyenMai.setCellValueFactory(new PropertyValueFactory<>("maKhuyenMai"));
        colTenKhuyenMai.setCellValueFactory(new PropertyValueFactory<>("tenKhuyenMai"));
        colMoTa.setCellValueFactory(new PropertyValueFactory<>("moTa"));
        colNgayBatDau.setCellValueFactory(new PropertyValueFactory<>("ngayBatDau"));
        colNgayKetThuc.setCellValueFactory(new PropertyValueFactory<>("ngayKetThuc"));
        colGiaTriTien.setCellValueFactory(new PropertyValueFactory<>("giaTriTien"));
        colGiaTriPhanTram.setCellValueFactory(new PropertyValueFactory<>("giaTriPhanTram"));
        colLoaiKhuyenMai.setCellValueFactory(new PropertyValueFactory<>("loaiKhuyenMai"));
        colLoaiKhuyenMai.setCellFactory(column -> new TableCell<KhuyenMaiDTO, LoaiKhuyenMai>() {
            @Override
            protected void updateItem(LoaiKhuyenMai item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : item.getLabel());
            }
        });

        cbLoaiKhuyenMai.setConverter(new StringConverter<>() {
            @Override
            public String toString(LoaiKhuyenMai loai) {
                return loai != null ? loai.getLabel() : "";
            }

            @Override
            public LoaiKhuyenMai fromString(String label) {
                return null;
            }
        });
        cbLoaiKhuyenMai.setItems(FXCollections.observableArrayList(LoaiKhuyenMai.values()));

        DateTimeFormatter formatter = TauGaUtils.FORMATTER_DATE;
        colNgayBatDau.setCellFactory(column -> new TableCell<KhuyenMaiDTO, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatter.format(item));
            }
        });
        colNgayKetThuc.setCellFactory(column -> new TableCell<KhuyenMaiDTO, LocalDate>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null) ? null : formatter.format(item));
            }
        });

        StringConverter<LocalDate> dateConverter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return (date != null) ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return LocalDate.parse(string, formatter);
                    } catch (java.time.format.DateTimeParseException e) {
                        System.err.println("Sai định dạng ngày: " + string);
                        return null;
                    }
                }
                return null;
            }
        };
        dpNgayBatDau.setConverter(dateConverter);
        dpNgayKetThuc.setConverter(dateConverter);

        hienThiDanhSachKhuyenMai();

        tblKhuyenMai.setOnMouseClicked(event -> {
            if (event.getClickCount() == 1) {
                KhuyenMaiDTO km = tblKhuyenMai.getSelectionModel().getSelectedItem();
                if (km != null) {
                    hienThiThongTinKhuyenMai(km);
                    setMaEditable(true);
                }
            }
        });

        txtMaKhuyenMai.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) setMaEditable(!txtMaKhuyenMai.isEditable());
        });

        txtMaKhuyenMai.addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, e -> {
            if (e.isControlDown() && e.getCode() == javafx.scene.input.KeyCode.E) {
                setMaEditable(!txtMaKhuyenMai.isEditable());
                e.consume();
            }
        });
    }

    private void setMaEditable(boolean editable) {
        txtMaKhuyenMai.setEditable(editable);
        txtMaKhuyenMai.setStyle(editable ? "" : "-fx-control-inner-background: #F4F4F4;");
    }

    private void hienThiThongTinKhuyenMai(KhuyenMaiDTO km) {
        txtMaKhuyenMai.setText(km.getMaKhuyenMai());
        txtTenKhuyenMai.setText(km.getTenKhuyenMai());
        taThongTinMoTa.setText(km.getMoTa());
        dpNgayBatDau.setValue(km.getNgayBatDau());
        dpNgayKetThuc.setValue(km.getNgayKetThuc());
        txtGiaTriTien.setText(String.valueOf(km.getGiaTriTien()));
        txtGiaTriPhanTram.setText(String.valueOf(km.getGiaTriPhanTram()));
        cbLoaiKhuyenMai.setValue(km.getLoaiKhuyenMai());
    }

    @SuppressWarnings("unchecked")
    private void hienThiDanhSachKhuyenMai() {
        Request request = new Request(CommandType.GET_ALL_KHUYEN_MAI, null);
        Response response = socketClient.send(request);

        if (response.isSuccess() && response.getData() instanceof List<?> list) {
            dsKM.setAll((List<KhuyenMaiDTO>) list);
        } else {
            dsKM.clear();
        }
        tblKhuyenMai.setItems(dsKM);
    }

    @FXML
    public void themKhuyenMai(ActionEvent actionEvent) {
        try {
            if (!kiemTraGiaTriHopLe()) return;

            LocalDate ngayBD = dpNgayBatDau.getValue();
            if (ngayBD == null) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thiếu thông tin!", "Vui lòng chọn ngày bắt đầu!");
                return;
            }

            KhuyenMaiDTO km = layKhuyenMaiTuForm();

            KhuyenMaiQLRequest payload = new KhuyenMaiQLRequest(km);
            Request request = new Request(CommandType.THEM_KHUYEN_MAI, payload);
            Response response = socketClient.send(request);

            if (response.isSuccess()) {
                // Lấy mã được tạo từ server trả về nếu có
                if (response.getData() instanceof KhuyenMaiDTO saved) {
                    dsKM.add(saved);
                } else {
                    hienThiDanhSachKhuyenMai();
                }
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thành công!", "Thêm khuyến mãi thành công!");
                xoaTrangNhap();
                setMaEditable(false);
            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi khi thêm", response.getMessage());
            }
        } catch (Exception e) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi khi thêm:", "Thêm thất bại");
            e.printStackTrace();
        }
    }

    @FXML
    private void capNhatKhuyenMai() {
        try {
            if (!kiemTraGiaTriHopLe()) return;

            KhuyenMaiDTO selected = tblKhuyenMai.getSelectionModel().getSelectedItem();
            String oldMa = selected != null ? selected.getMaKhuyenMai() : txtMaKhuyenMai.getText();

            KhuyenMaiDTO km = layKhuyenMaiTuForm();
            if (km.getMaKhuyenMai() == null || km.getMaKhuyenMai().isEmpty()) {
                km.setMaKhuyenMai(oldMa);
            }

            KhuyenMaiQLRequest payload = new KhuyenMaiQLRequest(km);
            Request request = new Request(CommandType.CAP_NHAT_KHUYEN_MAI, payload);
            Response response = socketClient.send(request);

            if (response.isSuccess()) {
                if (selected != null) {
                    int index = dsKM.indexOf(selected);
                    if (index >= 0) dsKM.set(index, km);
                } else {
                    hienThiDanhSachKhuyenMai();
                }
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thành công!", "Cập nhật thành công!");
                xoaTrang();
            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi khi cập nhật", response.getMessage());
            }
        } catch (Exception e) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi khi cập nhật:", "Cần chọn dòng bạn muốn cập nhật hoặc kiểm tra dữ liệu");
            e.printStackTrace();
        }
    }

    private KhuyenMaiDTO layKhuyenMaiTuForm() {
        int giaTien = txtGiaTriTien.getText().isEmpty() ? 0 : Integer.parseInt(txtGiaTriTien.getText());
        int giaPhanTram = txtGiaTriPhanTram.getText().isEmpty() ? 0 : Integer.parseInt(txtGiaTriPhanTram.getText());
        return new KhuyenMaiDTO(
                txtMaKhuyenMai.getText(),
                txtTenKhuyenMai.getText(),
                taThongTinMoTa.getText(),
                dpNgayBatDau.getValue(),
                dpNgayKetThuc.getValue(),
                giaTien,
                giaPhanTram,
                cbLoaiKhuyenMai.getValue()
        );
    }

    private void xoaTrangNhap() {
        txtMaKhuyenMai.clear();
        txtTenKhuyenMai.clear();
        taThongTinMoTa.clear();
        txtGiaTriTien.clear();
        txtGiaTriPhanTram.clear();
        dpNgayBatDau.setValue(null);
        dpNgayKetThuc.setValue(null);
        cbLoaiKhuyenMai.setValue(null);
        setMaEditable(false);
    }

    @FXML
    private void xoaTrang() {
        xoaTrangNhap();
        hienThiDanhSachKhuyenMai();
    }

    private boolean kiemTraGiaTriHopLe() {
        LoaiKhuyenMai loai = cbLoaiKhuyenMai.getValue();
        if (loai == null) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thiếu dữ liệu!", "Vui lòng chọn loại khuyến mãi!");
            return false;
        }

        int giaTriTien = 0;
        int giaTriPhanTram = 0;
        try {
            giaTriTien = txtGiaTriTien.getText().isEmpty() ? 0 : Integer.parseInt(txtGiaTriTien.getText());
            giaTriPhanTram = txtGiaTriPhanTram.getText().isEmpty() ? 0 : Integer.parseInt(txtGiaTriPhanTram.getText());
        } catch (NumberFormatException e) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Sai định dạng!", "Giá trị phải là số!");
            return false;
        }

        if (loai == LoaiKhuyenMai.KHUYEN_MAI_PHAN_TRAM) {
            if (giaTriPhanTram <= 0 || giaTriPhanTram > 100) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi!", "Vui lòng xem lại loại khuyến mãi và giá trị có phù hợp chưa!");
                return false;
            }
            if (giaTriTien != 0) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi!", "Loại phần trăm thì giá trị tiền phải bằng 0!");
                return false;
            }
        }

        if (loai == LoaiKhuyenMai.KHUYEN_MAI_TIEN) {
            if (giaTriTien <= 0) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi!", "Vui lòng xem lại loại khuyến mãi và giá trị có phù hợp chưa!");
                return false;
            }
            if (giaTriPhanTram != 0) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi!", "Loại giảm tiền thì phần trăm phải bằng 0!");
                return false;
            }
        }

        return true;
    }
}
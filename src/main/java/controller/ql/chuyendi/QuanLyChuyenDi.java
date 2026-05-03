package controller.ql.chuyendi;

import com.jfoenix.controls.JFXComboBox;
import dto.DanhSachGaTauDTO;
import dto.ThongTinChuyenTauDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.util.StringConverter;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.ChuyenTauRequest;
import utils.GiaoDienUtils;
import utils.TauGaUtils;
import utils.ui.AutoCompleteComboBoxListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

public class QuanLyChuyenDi {
    @FXML
    public Button btnTimChuyenDi;
    @FXML
    public TextField txtMaChuyenTau;
    @FXML
    public JFXComboBox<String> cbGaDi;
    @FXML
    public JFXComboBox<String> cbGaDen;
    @FXML
    public JFXComboBox<String> txtThoiGianDuTinh;
    @FXML
    public JFXComboBox<String> txtThoiGianKhoiHanh;
    @FXML
    public Button btnXoaTrang;
    @FXML
    public Button btnCapNhat;
    @FXML
    public Button btnThemChuyenTau;
    @FXML
    public JFXComboBox<String> cbTenTau;
    @FXML
    public DatePicker dpNgayDuTinh;
    @FXML
    private DatePicker dpNgayKhoiHanh;
    @FXML
    private TableView<ThongTinChuyenTauDTO> tblChuyenTau;
    @FXML
    private TableColumn<ThongTinChuyenTauDTO, String> colMaChuyenTau;
    @FXML
    private TableColumn<ThongTinChuyenTauDTO, String> colThoiGianKhoiHanh;
    @FXML
    private TableColumn<ThongTinChuyenTauDTO, String> colThoiGianDuTinh;
    @FXML
    private TableColumn<ThongTinChuyenTauDTO, String> colGaDi;
    @FXML
    private TableColumn<ThongTinChuyenTauDTO, String> colGaDen;
    @FXML
    private TableColumn<ThongTinChuyenTauDTO, String> colTenTau;

    private List<ThongTinChuyenTauDTO> dsChuyenTauDaCo;
    private ObservableList<ThongTinChuyenTauDTO> dsChuyenTauTbl = FXCollections.observableArrayList();

    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        colMaChuyenTau.setCellValueFactory(new PropertyValueFactory<>("maChuyenTau"));
        colTenTau.setCellValueFactory(new PropertyValueFactory<>("tenTau"));
        colThoiGianKhoiHanh.setCellValueFactory(new PropertyValueFactory<>("thoiGianKhoiHanhStr"));
        colThoiGianDuTinh.setCellValueFactory(new PropertyValueFactory<>("thoiGianDuTinhStr"));
        colGaDi.setCellValueFactory(new PropertyValueFactory<>("gaDi"));
        colGaDen.setCellValueFactory(new PropertyValueFactory<>("gaDen"));
        txtThoiGianKhoiHanh.setItems(taoGio());
        txtThoiGianDuTinh.setItems(taoGio());
        hienThiTatCaChuyenTau();
        hienThiDanhSachGa();
        hienThiDanhSachTau();

        setMaChuyenTauEditable(false);

        tblChuyenTau.setOnMouseClicked(event -> {
            ThongTinChuyenTauDTO selected = tblChuyenTau.getSelectionModel().getSelectedItem();
            if (selected != null) {
                hienThiThongTinLenInput(selected);
                setMaChuyenTauEditable(true);
            }
        });

        txtMaChuyenTau.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) {
                setMaChuyenTauEditable(!txtMaChuyenTau.isEditable());
            }
        });

        txtMaChuyenTau.addEventFilter(KeyEvent.KEY_PRESSED, e -> {
            if (e.isControlDown() && e.getCode() == KeyCode.E) {
                setMaChuyenTauEditable(!txtMaChuyenTau.isEditable());
                e.consume();
            }
        });

        StringConverter<LocalDate> dateConverter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? date.format(TauGaUtils.FORMATTER_DATE) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    try {
                        return LocalDate.parse(string, TauGaUtils.FORMATTER_DATE);
                    } catch (DateTimeParseException e) {
                        System.err.println("Sai định dạng ngày: " + string);
                        return null;
                    }
                }
                return null;
            }
        };
        dpNgayKhoiHanh.setConverter(dateConverter);
        dpNgayDuTinh.setConverter(dateConverter);

        new AutoCompleteComboBoxListener<>(cbGaDi);
        new AutoCompleteComboBoxListener<>(cbGaDen);
        new AutoCompleteComboBoxListener<>(cbTenTau);
        new AutoCompleteComboBoxListener<>(txtThoiGianKhoiHanh);
        new AutoCompleteComboBoxListener<>(txtThoiGianDuTinh);

        GiaoDienUtils.removeUnderlineHighlight(cbGaDi);
        GiaoDienUtils.removeUnderlineHighlight(cbGaDen);
        GiaoDienUtils.removeUnderlineHighlight(cbTenTau);
        GiaoDienUtils.removeUnderlineHighlight(txtThoiGianKhoiHanh);
        GiaoDienUtils.removeUnderlineHighlight(txtThoiGianDuTinh);

        GiaoDienUtils.apDungStyleInput(cbGaDi, cbGaDen, cbTenTau, txtThoiGianKhoiHanh, txtThoiGianDuTinh);
    }

    private void setMaChuyenTauEditable(boolean editable) {
        txtMaChuyenTau.setEditable(editable);
        txtMaChuyenTau.setStyle(editable ? "" : "-fx-control-inner-background: #F4F4F4;");
    }

    private void hienThiDanhSachGa() {
        DanhSachGaTauDTO result = layDanhSachGaTau();
        if (result != null) {
            cbGaDi.getItems().clear();
            cbGaDen.getItems().clear();
            cbGaDi.getItems().addAll(result.getDanhSachTenGa());
            cbGaDen.getItems().addAll(result.getDanhSachTenGa());
        }
    }

    private void hienThiDanhSachTau() {
        DanhSachGaTauDTO result = layDanhSachGaTau();
        if (result != null) {
            cbTenTau.getItems().clear();
            cbTenTau.getItems().addAll(result.getDanhSachTenTau());
        }
    }

    private void hienThiThongTinLenInput(ThongTinChuyenTauDTO ct) {
        txtMaChuyenTau.setText(ct.getMaChuyenTau());
        cbGaDi.setValue(ct.getGaDi());
        cbGaDen.setValue(ct.getGaDen());
        cbTenTau.setValue(ct.getTenTau());
        if (ct.getThoiGianKhoiHanh() != null) {
            dpNgayKhoiHanh.setValue(ct.getThoiGianKhoiHanh().toLocalDate());
            txtThoiGianKhoiHanh.setValue(TauGaUtils.DateTimeUtils.convertDTToTime(ct.getThoiGianKhoiHanh()));
            if (ct.getThoiGianDuTinh() != null) {
                dpNgayDuTinh.setValue(ct.getThoiGianDuTinh().toLocalDate());
                txtThoiGianDuTinh.setValue(TauGaUtils.DateTimeUtils.convertDTToTime(ct.getThoiGianDuTinh()));
            } else {
                txtThoiGianDuTinh.setValue(null);
            }
        } else {
            dpNgayKhoiHanh.setValue(null);
            txtThoiGianKhoiHanh.setValue(null);
            txtThoiGianDuTinh.setValue(null);
        }
    }

    @SuppressWarnings("unchecked")
    private void hienThiTatCaChuyenTau() {
        Request request = new Request(CommandType.GET_ALL_CHUYEN_TAU, null);
        Response response = socketClient.send(request);

        tblChuyenTau.getItems().clear();
        dsChuyenTauTbl.clear();

        if (response.isSuccess() && response.getData() instanceof List<?> list) {
            dsChuyenTauDaCo = (List<ThongTinChuyenTauDTO>) list;
            dsChuyenTauTbl.addAll(dsChuyenTauDaCo);
        }
        tblChuyenTau.setItems(dsChuyenTauTbl);
    }

    @FXML
    private void timChuyenDi(ActionEvent actionEvent) {
        String maChuyenTau = txtMaChuyenTau.getText().trim().toLowerCase();
        String tenTau = cbTenTau.getValue() != null ? cbTenTau.getValue().toLowerCase() : "";
        String gaDi = cbGaDi.getValue() != null ? cbGaDi.getValue().toLowerCase() : "";
        String gaDen = cbGaDen.getValue() != null ? cbGaDen.getValue().toLowerCase() : "";
        LocalDate ngayKH = dpNgayKhoiHanh.getValue();
        String thoiGianKHStr = txtThoiGianKhoiHanh.getValue() != null ? txtThoiGianKhoiHanh.getValue().toLowerCase() : "";
        String thoiGianDTStr = txtThoiGianDuTinh.getValue() != null ? txtThoiGianDuTinh.getValue().toLowerCase() : "";

        LocalTime gioKH = null;
        LocalTime gioDT = null;
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("H:mm");
            if (!thoiGianKHStr.isEmpty()) gioKH = LocalTime.parse(thoiGianKHStr, fmt);
            if (!thoiGianDTStr.isEmpty()) gioDT = LocalTime.parse(thoiGianDTStr, fmt);
        } catch (DateTimeParseException e) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi định dạng",
                    "Sai định dạng Giờ khởi hành! Vui lòng nhập HH:mm (ví dụ 08:00)");
            return;
        }

        LocalTime finalGioKH = gioKH;
        LocalTime finalGioDT = gioDT;

        ObservableList<ThongTinChuyenTauDTO> ketQuaTim = dsChuyenTauDaCo.stream()
                .filter(ct -> {
                    boolean match = true;
                    if (!maChuyenTau.isEmpty() && !ct.getMaChuyenTau().toLowerCase().contains(maChuyenTau)) match = false;
                    if (match && !tenTau.isEmpty() && !ct.getTenTau().toLowerCase().contains(tenTau)) match = false;
                    if (match && !gaDi.isEmpty() && !ct.getGaDi().toLowerCase().contains(gaDi)) match = false;
                    if (match && !gaDen.isEmpty() && !ct.getGaDen().toLowerCase().contains(gaDen)) match = false;
                    if (match && ngayKH != null) {
                        if (ct.getThoiGianKhoiHanh() == null || !ct.getThoiGianKhoiHanh().toLocalDate().isEqual(ngayKH))
                            match = false;
                    }
                    if (match && finalGioKH != null) {
                        if (ct.getThoiGianKhoiHanh() == null || !ct.getThoiGianKhoiHanh().toLocalTime().equals(finalGioKH))
                            match = false;
                    }
                    if (match && finalGioDT != null) {
                        if (ct.getThoiGianDuTinh() == null || !ct.getThoiGianDuTinh().toLocalTime().equals(finalGioDT))
                            match = false;
                    }
                    return match;
                }).collect(Collectors.toCollection(FXCollections::observableArrayList));

        if (ketQuaTim.isEmpty()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "",
                    "Không tìm thấy chuyến tàu nào phù hợp với các tiêu chí tìm kiếm.");
            tblChuyenTau.setItems(dsChuyenTauTbl);
        } else {
            tblChuyenTau.setItems(ketQuaTim);
        }
    }

    @FXML
    public void themChuyenDi(ActionEvent actionEvent) {
        try {
            String tenTau = cbTenTau.getValue() != null ? cbTenTau.getValue() : "";
            String gaDi = cbGaDi.getValue() != null ? cbGaDi.getValue() : "";
            String gaDen = cbGaDen.getValue() != null ? cbGaDen.getValue() : "";
            LocalDate ngayKH = dpNgayKhoiHanh.getValue();
            String thoiGianKH = txtThoiGianKhoiHanh.getValue() != null ? txtThoiGianKhoiHanh.getValue() : "";
            String thoiGianDuTinh = txtThoiGianDuTinh.getValue() != null ? txtThoiGianDuTinh.getValue() : "";

            if (tenTau.isEmpty() || gaDi.isEmpty() || gaDen.isEmpty() ||
                    ngayKH == null || thoiGianKH.isEmpty() || thoiGianDuTinh.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "", "Vui lòng nhập đầy đủ thông tin trước khi thêm!");
                return;
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("H:mm");
            LocalTime gioKH = LocalTime.parse(thoiGianKH, fmt);
            LocalTime gioDuTinh = LocalTime.parse(thoiGianDuTinh, fmt);
            LocalDateTime tgKhoiHanh = LocalDateTime.of(ngayKH, gioKH);
            LocalDate ngayDen = gioDuTinh.isBefore(gioKH) ? ngayKH.plusDays(1) : ngayKH;
            LocalDateTime tgDuTinh = LocalDateTime.of(ngayDen, gioDuTinh);

            if (!tgDuTinh.isAfter(tgKhoiHanh)) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "", "Thời gian đến dự tính phải sau thời gian khởi hành!");
                return;
            }

            ThongTinChuyenTauDTO ct = new ThongTinChuyenTauDTO(null, tenTau, tgKhoiHanh, tgDuTinh, gaDi, gaDen);

            ChuyenTauRequest payload = new ChuyenTauRequest(ct);
            Request request = new Request(CommandType.THEM_CHUYEN_TAU, payload);
            Response response = socketClient.send(request);

            if (response.isSuccess()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thành công", "Thêm chuyến tàu thành công!");
                hienThiTatCaChuyenTau();
                clearInputFields();
            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Thêm chuyến tàu thất bại!", response.getMessage());
            }
        } catch (DateTimeParseException e) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Sai định dạng", "Sai định dạng giờ! Vui lòng nhập HH:mm (ví dụ 08:00)");
        } catch (Exception e) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Có lỗi", "Có lỗi khi thêm chuyến tàu");
            e.printStackTrace();
        }
    }

    @FXML
    public void capNhatChuyenDi(ActionEvent actionEvent) {
        try {
            ThongTinChuyenTauDTO selected = tblChuyenTau.getSelectionModel().getSelectedItem();
            String maChuyenTau = selected != null ? selected.getMaChuyenTau() : txtMaChuyenTau.getText().trim();

            String tenTau = cbTenTau.getValue() != null ? cbTenTau.getValue() : "";
            String gaDi = cbGaDi.getValue() != null ? cbGaDi.getValue() : "";
            String gaDen = cbGaDen.getValue() != null ? cbGaDen.getValue() : "";
            LocalDate ngayKH = dpNgayKhoiHanh.getValue();
            String thoiGianKH = txtThoiGianKhoiHanh.getValue() != null ? txtThoiGianKhoiHanh.getValue() : "";
            String thoiGianDuTinh = txtThoiGianDuTinh.getValue() != null ? txtThoiGianDuTinh.getValue() : "";

            if (maChuyenTau.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "", "Vui lòng nhập mã chuyến tàu cần cập nhật!");
                return;
            }
            if (gaDi.isEmpty() || gaDen.isEmpty() || ngayKH == null ||
                    thoiGianKH.isEmpty() || thoiGianDuTinh.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "", "Vui lòng nhập đầy đủ thông tin để cập nhật!");
                return;
            }

            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("H:mm");
            LocalTime gioKH = LocalTime.parse(thoiGianKH, fmt);
            LocalTime gioDuTinh = LocalTime.parse(thoiGianDuTinh, fmt);
            LocalDateTime tgKhoiHanh = LocalDateTime.of(ngayKH, gioKH);
            LocalDate ngayDen = gioDuTinh.isBefore(gioKH) ? ngayKH.plusDays(1) : ngayKH;
            LocalDateTime tgDuTinh = LocalDateTime.of(ngayDen, gioDuTinh);

            ThongTinChuyenTauDTO ct = new ThongTinChuyenTauDTO(maChuyenTau, tenTau, tgKhoiHanh, tgDuTinh, gaDi, gaDen);

            ChuyenTauRequest payload = new ChuyenTauRequest(ct);
            Request request = new Request(CommandType.CAP_NHAT_CHUYEN_TAU, payload);
            Response response = socketClient.send(request);

            if (response.isSuccess()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "", "Cập nhật chuyến tàu thành công!");
                hienThiTatCaChuyenTau();
                xoaTrang(actionEvent);
            } else {
                GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "", "Cập nhật chuyến tàu thất bại! " + response.getMessage());
            }
        } catch (DateTimeParseException e) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "", "Sai định dạng giờ! Vui lòng nhập đúng dạng HH:mm (ví dụ: 08:00)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void xoaTrang(ActionEvent actionEvent) {
        clearInputFields();
        dsChuyenTauTbl.clear();
        hienThiTatCaChuyenTau();
    }

    private void clearInputFields() {
        txtMaChuyenTau.clear();
        txtThoiGianKhoiHanh.setValue(null);
        txtThoiGianDuTinh.setValue(null);
        dpNgayKhoiHanh.setValue(null);
        dpNgayDuTinh.setValue(null);
        cbGaDi.getSelectionModel().clearSelection();
        cbGaDi.setValue(null);
        cbGaDen.getSelectionModel().clearSelection();
        cbGaDen.setValue(null);
        cbTenTau.getSelectionModel().clearSelection();
        cbTenTau.setValue(null);
        txtMaChuyenTau.requestFocus();
        setMaChuyenTauEditable(false);
    }

    private DanhSachGaTauDTO layDanhSachGaTau() {
        Request request = new Request(CommandType.GET_DANH_SACH_GA_TAU, null);
        Response response = socketClient.send(request);
        if (response.isSuccess() && response.getData() instanceof DanhSachGaTauDTO dto) {
            return dto;
        }
        return null;
    }

    private ObservableList<String> taoGio() {
        ObservableList<String> list = FXCollections.observableArrayList();
        for (int h = 0; h < 24; h++) {
            for (int m = 0; m < 60; m += 15) {
                list.add(String.format("%02d:%02d", h, m));
            }
        }
        return list;
    }
}
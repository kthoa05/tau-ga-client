package controller.nv.ve.form;

import com.jfoenix.controls.JFXComboBox;
import dto.ChuyenTauDTO;
import dto.DatVeInitDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.TimKiemChuyenTauRequest;
import utils.GiaoDienUtils;
import utils.ui.AutoCompleteComboBoxListener;

import java.time.LocalDate;
import java.util.function.Consumer;

public class FormTimKiemChuyenTau {
    // tim kiem
    @FXML
    private RadioButton radMotChieu, radKhuHoi;
    @FXML
    private JFXComboBox<String> cbbGaDi;
    @FXML
    private JFXComboBox<String> cbbGaDen;
    @FXML
    private JFXComboBox<String> cbboLoaiGheGiuong;
    @FXML
    private DatePicker dpNgayDi, dpNgayVe;

    private final SocketClient socketClient = new SocketClient();

    //callback
    private Consumer<ChuyenTauDTO> onTimKiemListener;

    public void setOnTimKiemListener(Consumer<ChuyenTauDTO> listener) {
        this.onTimKiemListener = listener;
    }

    @FXML
    public void initialize() {
        DatVeInitDTO duLieuKhoiTao = layDuLieuKhoiTao();
        ObservableList<String> allGa = FXCollections.observableArrayList();
        if (duLieuKhoiTao != null && duLieuKhoiTao.getDanhSachGa() != null) {
            allGa.addAll(duLieuKhoiTao.getDanhSachGa());
        }
        if (duLieuKhoiTao != null && duLieuKhoiTao.getDanhSachLoaiGhe() != null) {
            cbboLoaiGheGiuong.getItems().addAll(duLieuKhoiTao.getDanhSachLoaiGhe());
        }
        cbbGaDi.setItems(allGa);
        new AutoCompleteComboBoxListener<>(cbbGaDi);
        cbbGaDen.setItems(allGa);
        new AutoCompleteComboBoxListener<>(cbbGaDen);

        //mac dinh chon cai dau tien
        cbbGaDi.getSelectionModel().selectFirst();
        cbbGaDen.getSelectionModel().selectFirst();

        //chon tat ca khi click cbb
        enableAutoSelectAll(cbbGaDi);
        enableAutoSelectAll(cbbGaDen);
        enableAutoSelectAll(cbboLoaiGheGiuong);

        selectAllOnValueChange(cbbGaDi);
        selectAllOnValueChange(cbbGaDen);
        selectAllOnValueChange(cbboLoaiGheGiuong);

        enableTabAutoComplete(cbbGaDi);
        enableTabAutoComplete(cbbGaDen);
        enableTabAutoComplete(cbboLoaiGheGiuong);

        //rad
        ToggleGroup group = new ToggleGroup();
        radMotChieu.setToggleGroup(group);
        radKhuHoi.setToggleGroup(group);
        radMotChieu.setSelected(true);
        dpNgayVe.disableProperty().bind(radMotChieu.selectedProperty());

        GiaoDienUtils.removeUnderlineHighlight(cbbGaDi);
        GiaoDienUtils.removeUnderlineHighlight(cbbGaDen);
        GiaoDienUtils.removeUnderlineHighlight(cbboLoaiGheGiuong);
        GiaoDienUtils.apDungStyleInput(cbbGaDi, cbbGaDen, cbboLoaiGheGiuong);

    }

    public void onTimKiem(ActionEvent actionEvent) {
        String gaDi = cbbGaDi.getValue();
        boolean loaiVe = radKhuHoi.isSelected() ? true : false;
        String gaDen = cbbGaDen.getValue();
        LocalDate ngayDi = dpNgayDi.getValue();
        LocalDate ngayDen = dpNgayVe.getValue();
        String loaiGheGiuong = cbboLoaiGheGiuong.getValue();

        if (gaDen.equalsIgnoreCase(gaDi)) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "", "Ga đến phải khác ga đi");
            return;
        }

        if (ngayDi.isBefore(LocalDate.now())) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "", "Ngày đi phải lớn hơn hoặc bằng ngày hiện tại");
            return;
        }

        if (loaiVe && (ngayDen == null || ngayDen.isBefore(ngayDi))) {
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "", "Ngày về phải sau ngày khởi hành");
            return;
        }

        ChuyenTauDTO result = timChuyenTau(gaDi, gaDen, loaiVe, ngayDi, ngayDen);
        if (result == null) {
            GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "", "Không tìm thấy chuyến tàu phù hợp");
            return;
        }
        if (onTimKiemListener != null) {
            onTimKiemListener.accept(result);
        }
    }

    public String getLoaiGheGiuongDangChon() {
        return cbboLoaiGheGiuong.getValue();
    }

    public String getGaDiDangChon() {
        return cbbGaDi.getValue();
    }

    public String getGaDenDangChon() {
        return cbbGaDen.getValue();
    }

    public boolean isKhuHoiDangChon() {
        return radKhuHoi.isSelected();
    }

    public LocalDate getNgayDiDangChon() {
        return dpNgayDi.getValue();
    }

    public LocalDate getNgayVeDangChon() {
        return dpNgayVe.getValue();
    }

    public void apDungTrangThaiTimKiem(
            String gaDi,
            String gaDen,
            String loaiGhe,
            boolean khuHoi,
            LocalDate ngayDi,
            LocalDate ngayVe
    ) {
        if (gaDi != null) {
            cbbGaDi.setValue(gaDi);
        }
        if (gaDen != null) {
            cbbGaDen.setValue(gaDen);
        }
        if (loaiGhe != null) {
            cbboLoaiGheGiuong.setValue(loaiGhe);
        }
        radKhuHoi.setSelected(khuHoi);
        radMotChieu.setSelected(!khuHoi);
        dpNgayDi.setValue(ngayDi);
        dpNgayVe.setValue(ngayVe);
    }


    private void enableAutoSelectAll(JFXComboBox<String> comboBox) {
        comboBox.setEditable(true);
        comboBox.getEditor().focusedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                comboBox.getEditor().selectAll();
            }
        });
        comboBox.getEditor().setOnMouseClicked(event -> {
            comboBox.getEditor().selectAll();
        });
    }
    private void selectAllOnValueChange(JFXComboBox<String> comboBox) {
        comboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && comboBox.isFocused()) {
                Platform.runLater(() -> {
                    comboBox.getEditor().selectAll();
                });
            }
        });
    }
    private void enableTabAutoComplete(JFXComboBox<String> comboBox) {
        comboBox.setEditable(true);

        comboBox.getEditor().addEventFilter(javafx.scene.input.KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.TAB) {
                String text = comboBox.getEditor().getText().toLowerCase();
                String match = comboBox.getItems().stream()
                        .filter(item -> item.toLowerCase().startsWith(text))
                        .findFirst()
                        .orElse(null);

                if (match != null) {
                    comboBox.getEditor().setText(match);
                    comboBox.setValue(match);

                    Platform.runLater(() -> comboBox.getEditor().selectAll());
                }
            }
        });
    }

    private DatVeInitDTO layDuLieuKhoiTao() {
        Request request = new Request(CommandType.GET_DAT_VE_INIT, null);
        Response response = socketClient.send(request);
        if (response == null || !response.isSuccess()) {
            return null;
        }
        return (DatVeInitDTO) response.getData();
    }

    private ChuyenTauDTO timChuyenTau(String gaDi, String gaDen, boolean khuHoi, LocalDate ngayDi, LocalDate ngayVe) {
        TimKiemChuyenTauRequest payload = new TimKiemChuyenTauRequest(gaDi, gaDen, khuHoi, ngayDi, ngayVe);
        Request request = new Request(CommandType.SEARCH_DAT_VE_CHUYEN_TAU, payload);
        Response response = socketClient.send(request);
        if (response == null || !response.isSuccess()) {
            return null;
        }
        return (ChuyenTauDTO) response.getData();
    }





}

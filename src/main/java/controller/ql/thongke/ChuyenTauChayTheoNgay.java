package controller.ql.thongke;

import com.jfoenix.controls.JFXComboBox;
import dto.ThongKeDTO;
import dto.ThongKeQuanLyDTO;
import dto.ThongKeSoLuongTauTheoCaDTO;
import entity.enums.CaLamViec;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.ThongKeChuyenTauRequest;
import utils.GiaoDienUtils;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ChuyenTauChayTheoNgay {
    @FXML
    private LineChart<String, Number> lineChartChuyenTau;

    @FXML
    private JFXComboBox<String> cbbChuyenTau;

    @FXML
    private DatePicker ngayThongKe;

    @FXML
    private TableView<ThongKeQuanLyDTO> tableView;

    @FXML
    private TableColumn<ThongKeDTO, String> tblMaChuyenTau;

    @FXML
    private TableColumn<ThongKeDTO, String> tblTenTau;

    @FXML
    private TableColumn<ThongKeDTO, String> tblNgayGioKhoiHanh;

    @FXML
    private TableColumn<ThongKeDTO, String> tblCaChay;

    @FXML
    private TableColumn<ThongKeDTO, String> tblTongSoVe;

    @FXML
    private TableColumn<ThongKeDTO, String> tblSoVeBanDuoc;

    @FXML
    private TableColumn<ThongKeDTO, String> tblSoVeConLai;

    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        GiaoDienUtils.apDungStyleInput(cbbChuyenTau);
        GiaoDienUtils.removeUnderlineHighlight(cbbChuyenTau);
        loadCbbTenTau();
        loadDataDashboard();
        loadFieldTbl();
        GiaoDienUtils.wrapColumn(tblMaChuyenTau);
        GiaoDienUtils.wrapColumn(tblTenTau);
    }

    private void loadFieldTbl() {
        tblMaChuyenTau.setCellValueFactory(new PropertyValueFactory<>("maChuyenTau"));
        tblTenTau.setCellValueFactory(new PropertyValueFactory<>("tenTau"));
        tblNgayGioKhoiHanh.setCellValueFactory(new PropertyValueFactory<>("ngayGioKhoiHanh"));
        tblCaChay.setCellValueFactory(new PropertyValueFactory<>("caChay"));
        tblTongSoVe.setCellValueFactory(new PropertyValueFactory<>("tongSoVe"));
        tblSoVeBanDuoc.setCellValueFactory(new PropertyValueFactory<>("soVeBanDuoc"));
        tblSoVeConLai.setCellValueFactory(new PropertyValueFactory<>("soVeConLai"));
    }

    @SuppressWarnings("unchecked")
    private void loadCbbTenTau() {
        Request request = new Request(CommandType.GET_ALL_TEN_TAU, null);
        Response response = socketClient.send(request);
        if (response.isSuccess() && response.getData() instanceof List<?> list) {
            cbbChuyenTau.setItems(FXCollections.observableArrayList((List<String>) list));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadDataDashboard() {
        ngayThongKe.setValue(LocalDate.now());

        // Load table
        ThongKeChuyenTauRequest tblPayload = new ThongKeChuyenTauRequest(LocalDate.now(), null);
        Request tblRequest = new Request(CommandType.THONG_KE_CHUYEN_TAU_THEO_NGAY, tblPayload);
        Response tblResponse = socketClient.send(tblRequest);
        if (tblResponse.isSuccess() && tblResponse.getData() instanceof List<?> list) {
            tableView.setItems(FXCollections.observableArrayList((List<ThongKeQuanLyDTO>) list));
        }

        // Load chart
        Request chartRequest = new Request(CommandType.THONG_KE_SO_LUONG_TAU_THEO_CA, null);
        Response chartResponse = socketClient.send(chartRequest);
        if (chartResponse.isSuccess() && chartResponse.getData() instanceof ThongKeSoLuongTauTheoCaDTO dto) {
            showChart(dto.getResult());
        }
    }

    private void showChart(Map<CaLamViec, Long> result) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng chuyến tàu");
        for (CaLamViec ca : List.of(CaLamViec.CA1, CaLamViec.CA2, CaLamViec.CA3)) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(
                    ca.getTenCa(),
                    result.getOrDefault(ca, 0L)
            );
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Tooltip tooltip = new Tooltip(String.format("%,.0f", data.getYValue().doubleValue()) + " chuyến");
                    Tooltip.install(newNode, tooltip);
                    GiaoDienUtils.Chart.ganHieuUngPhongTo(data);
                }
            });
            series.getData().add(data);
        }
        lineChartChuyenTau.getData().clear();
        lineChartChuyenTau.getData().add(series);
        GiaoDienUtils.Chart.veMuiTenTrucXY(lineChartChuyenTau);
    }

    @SuppressWarnings("unchecked")
    public void thongKe(MouseEvent mouseEvent) {
        String chuyenTau = cbbChuyenTau.getSelectionModel().getSelectedItem();
        LocalDate ngay = ngayThongKe.getValue();

        // Load table
        ThongKeChuyenTauRequest tblPayload = new ThongKeChuyenTauRequest(ngay, chuyenTau);
        Request tblRequest = new Request(CommandType.THONG_KE_CHUYEN_TAU_THEO_NGAY, tblPayload);
        Response tblResponse = socketClient.send(tblRequest);
        if (tblResponse.isSuccess() && tblResponse.getData() instanceof List<?> list) {
            tableView.setItems(FXCollections.observableArrayList((List<ThongKeQuanLyDTO>) list));
        }

        // Load chart
        ThongKeChuyenTauRequest chartPayload = new ThongKeChuyenTauRequest(ngay, chuyenTau);
        Request chartRequest = new Request(CommandType.THONG_KE_SO_LUONG_TAU_THEO_CA_CO_FILTER, chartPayload);
        Response chartResponse = socketClient.send(chartRequest);
        if (chartResponse.isSuccess() && chartResponse.getData() instanceof ThongKeSoLuongTauTheoCaDTO dto) {
            showChart(dto.getResult());
        }
    }

    @SuppressWarnings("unchecked")
    public void lamMoi(MouseEvent mouseEvent) {
        cbbChuyenTau.getSelectionModel().clearSelection();
        ngayThongKe.setValue(LocalDate.now());
        tableView.getItems().clear();
        lineChartChuyenTau.getData().clear();

        ThongKeChuyenTauRequest tblPayload = new ThongKeChuyenTauRequest(LocalDate.now(), null);
        Request tblRequest = new Request(CommandType.THONG_KE_CHUYEN_TAU_THEO_NGAY, tblPayload);
        Response tblResponse = socketClient.send(tblRequest);
        if (tblResponse.isSuccess() && tblResponse.getData() instanceof List<?> list) {
            tableView.setItems(FXCollections.observableArrayList((List<ThongKeQuanLyDTO>) list));
        }

        Request chartRequest = new Request(CommandType.THONG_KE_SO_LUONG_TAU_THEO_CA, null);
        Response chartResponse = socketClient.send(chartRequest);
        if (chartResponse.isSuccess() && chartResponse.getData() instanceof ThongKeSoLuongTauTheoCaDTO dto) {
            showChart(dto.getResult());
        }
    }
}
package controller.ql.thongke;

import com.jfoenix.controls.JFXComboBox;
import dto.DashboardQLDTO;
import dto.ThongKeDTO;
import dto.ThongKeQuanLyDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
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

public class SoLuongVeTheoChuyenDi {

    @FXML private BarChart<String, Number> soLuongVeTheoChuyen;
    @FXML private JFXComboBox<String> cbchuyenTau;
    @FXML private DatePicker ngayThongKe;

    @FXML private TableView<ThongKeQuanLyDTO> tableView;
    @FXML private TableColumn<ThongKeDTO, String> tblMaChuyenTau;
    @FXML private TableColumn<ThongKeDTO, String> tblTenTau;
    @FXML private TableColumn<ThongKeDTO, String> tblNgayGioKhoiHanh;
    @FXML private TableColumn<ThongKeDTO, String> tblTongSoVe;
    @FXML private TableColumn<ThongKeDTO, String> tblSoVeBanDuoc;
    @FXML private TableColumn<ThongKeDTO, String> tblSoVeConLai;

    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        GiaoDienUtils.apDungStyleInput(cbchuyenTau);
        GiaoDienUtils.removeUnderlineHighlight(cbchuyenTau);
        loadCbbTenTau();
        loadDataDashboard();
        loadFieldTbl();
        xoayTrucX(soLuongVeTheoChuyen);
        GiaoDienUtils.wrapColumn(tblMaChuyenTau);
        GiaoDienUtils.wrapColumn(tblTenTau);
    }

    private void loadFieldTbl() {
        tblMaChuyenTau.setCellValueFactory(new PropertyValueFactory<>("maChuyenTau"));
        tblTenTau.setCellValueFactory(new PropertyValueFactory<>("tenTau"));
        tblNgayGioKhoiHanh.setCellValueFactory(new PropertyValueFactory<>("ngayGioKhoiHanh"));
        tblTongSoVe.setCellValueFactory(new PropertyValueFactory<>("tongSoVe"));
        tblSoVeBanDuoc.setCellValueFactory(new PropertyValueFactory<>("soVeBanDuoc"));
        tblSoVeConLai.setCellValueFactory(new PropertyValueFactory<>("soVeConLai"));
    }

    @SuppressWarnings("unchecked")
    private void loadCbbTenTau() {
        Request request = new Request(CommandType.GET_ALL_TEN_TAU, null);
        Response response = socketClient.send(request);
        if (response.isSuccess() && response.getData() instanceof List<?> list) {
            cbchuyenTau.setItems(FXCollections.observableArrayList((List<String>) list));
        }
    }

    @SuppressWarnings("unchecked")
    private void loadDataDashboard() {
        ngayThongKe.setValue(LocalDate.now());

        // Load table
        Request tblRequest = new Request(CommandType.THONG_KE_SO_LUONG_VE_TBL_DASHBOARD, null);
        Response tblResponse = socketClient.send(tblRequest);
        if (tblResponse.isSuccess() && tblResponse.getData() instanceof List<?> list) {
            tableView.setItems(FXCollections.observableArrayList((List<ThongKeQuanLyDTO>) list));
        }

        // Load chart
        Request chartRequest = new Request(CommandType.THONG_KE_SO_LUONG_VE_CHART_DASHBOARD, null);
        Response chartResponse = socketClient.send(chartRequest);
        if (chartResponse.isSuccess() && chartResponse.getData() instanceof List<?> list) {
            showChart((List<ThongKeQuanLyDTO>) list);
        }
    }

    @SuppressWarnings("unchecked")
    private void showChartThongKe(List<DashboardQLDTO> soLuongVe) {
        soLuongVeTheoChuyen.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng vé");
        soLuongVe.forEach(rs -> {
            XYChart.Data<String, Number> data = new XYChart.Data<>(
                    String.valueOf(rs.getTenChuyenTau()),
                    rs.getSoLuongVeTuongUng()
            );
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    GiaoDienUtils.Chart.hienGiaTriTrenCot(data);
                    Tooltip tooltip = new Tooltip(data.getXValue());
                    Tooltip.install(newNode, tooltip);
                }
            });
            series.getData().add(data);
        });

        CategoryAxis xAxis = (CategoryAxis) soLuongVeTheoChuyen.getXAxis();
        xAxis.setAutoRanging(true);
        ObservableList<String> categories = FXCollections.observableArrayList();
        soLuongVe.forEach(rs -> categories.add(rs.getTenChuyenTau()));
        xAxis.setCategories(categories);
        xAxis.setTickLabelRotation(0.1);
        xAxis.setTickLabelFont(javafx.scene.text.Font.font(13));

        soLuongVeTheoChuyen.getData().clear();
        soLuongVeTheoChuyen.getData().add(series);

        int size = soLuongVe.size();
        soLuongVeTheoChuyen.setCategoryGap(Math.max(20, size > 0 ? 400 / size : 20));
        soLuongVeTheoChuyen.setBarGap(10);
        GiaoDienUtils.Chart.veMuiTenTrucXY(soLuongVeTheoChuyen);

        double max = series.getData().stream()
                .mapToDouble(d -> d.getYValue().doubleValue()).max().orElse(0);
        GiaoDienUtils.Chart.tangTrucY(soLuongVeTheoChuyen, max);
    }

    private void showChart(List<ThongKeQuanLyDTO> soLuongVeTheoChuyenRs) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng vé");
        soLuongVeTheoChuyenRs.forEach(rs -> {
            XYChart.Data<String, Number> data = new XYChart.Data<>(
                    String.valueOf(rs.getTenTau()),
                    rs.getSoLuongVe()
            );
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    GiaoDienUtils.Chart.hienGiaTriTrenCot(data);
                    Tooltip tooltip = new Tooltip(data.getXValue());
                    Tooltip.install(newNode, tooltip);
                }
            });
            series.getData().add(data);
        });

        soLuongVeTheoChuyen.getData().clear();
        soLuongVeTheoChuyen.getData().add(series);
        kichThuocGiaTriTrucXSLVe(soLuongVeTheoChuyen);

        GiaoDienUtils.Chart.veMuiTenTrucXY(soLuongVeTheoChuyen);
        double max = series.getData().stream()
                .mapToDouble(d -> d.getYValue().doubleValue()).max().orElse(0);
        GiaoDienUtils.Chart.tangTrucY(soLuongVeTheoChuyen, max);
    }

    @SuppressWarnings("unchecked")
    public void thongKe(MouseEvent mouseEvent) {
        String chuyenTau = cbchuyenTau.getSelectionModel().getSelectedItem();
        LocalDate ngay = ngayThongKe.getValue();

        // Load table
        ThongKeChuyenTauRequest tblPayload = new ThongKeChuyenTauRequest(ngay, chuyenTau);
        Request tblRequest = new Request(CommandType.THONG_KE_LOAI_VE_THEO_CHUYEN_TAU, tblPayload);
        Response tblResponse = socketClient.send(tblRequest);
        if (tblResponse.isSuccess() && tblResponse.getData() instanceof List<?> list) {
            tableView.setItems(FXCollections.observableArrayList((List<ThongKeQuanLyDTO>) list));
        }

        // Load chart
        ThongKeChuyenTauRequest chartPayload = new ThongKeChuyenTauRequest(ngay, chuyenTau);
        Request chartRequest = new Request(CommandType.THONG_KE_SO_LUONG_VE_CHART_CO_FILTER, chartPayload);
        Response chartResponse = socketClient.send(chartRequest);
        if (chartResponse.isSuccess() && chartResponse.getData() instanceof List<?> list) {
            showChartThongKe((List<DashboardQLDTO>) list);
        }
    }

    private void xoayTrucX(BarChart<String, Number> chart) {
        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setTickLabelRotation(0.1);
        xAxis.setTickLabelGap(5);
    }

    private void kichThuocGiaTriTrucXSLVe(BarChart<String, Number> chart) {
        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setTickLabelFont(javafx.scene.text.Font.font(6));
        xAxis.setTickLabelGap(2);
    }

    @SuppressWarnings("unchecked")
    public void lamMoi(MouseEvent mouseEvent) {
        cbchuyenTau.getSelectionModel().clearSelection();
        ngayThongKe.setValue(LocalDate.now());
        tableView.getItems().clear();
        soLuongVeTheoChuyen.getData().clear();

        CategoryAxis xAxis = (CategoryAxis) soLuongVeTheoChuyen.getXAxis();
        xAxis.setAutoRanging(true);
        xAxis.getCategories().clear();
        xAxis.setTickLabelRotation(0);
        xAxis.setTickLabelFont(javafx.scene.text.Font.font(10));
        xAxis.setTickLabelGap(5);
        soLuongVeTheoChuyen.setCategoryGap(10);
        soLuongVeTheoChuyen.setBarGap(10);

        Request tblRequest = new Request(CommandType.THONG_KE_SO_LUONG_VE_TBL_DASHBOARD, null);
        Response tblResponse = socketClient.send(tblRequest);
        if (tblResponse.isSuccess() && tblResponse.getData() instanceof List<?> list) {
            tableView.setItems(FXCollections.observableArrayList((List<ThongKeQuanLyDTO>) list));
        }

        Request chartRequest = new Request(CommandType.THONG_KE_SO_LUONG_VE_CHART_DASHBOARD, null);
        Response chartResponse = socketClient.send(chartRequest);
        if (chartResponse.isSuccess() && chartResponse.getData() instanceof List<?> list) {
            showChart((List<ThongKeQuanLyDTO>) list);
        }
    }
}
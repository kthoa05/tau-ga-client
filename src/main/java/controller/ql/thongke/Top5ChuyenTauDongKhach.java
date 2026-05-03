package controller.ql.thongke;

import dto.DashboardQLDTO;
import dto.ThongKeQuanLyDTO;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.ThongKeChuyenTauRequest;
import utils.GiaoDienUtils;

import java.time.LocalDate;
import java.util.List;

public class Top5ChuyenTauDongKhach {
    @FXML private BarChart<String, Number> barChartTop5;
    @FXML private DatePicker ngayThongKe;

    @FXML private TableView<ThongKeQuanLyDTO> tableView;
    @FXML private TableColumn<ThongKeQuanLyDTO, String> tblMaChuyenTau;
    @FXML private TableColumn<ThongKeQuanLyDTO, String> tblTenTau;
    @FXML private TableColumn<ThongKeQuanLyDTO, String> tblNgayGioKhoiHanh;
    @FXML private TableColumn<ThongKeQuanLyDTO, String> tblTyLeLapDay;
    @FXML private TableColumn<ThongKeQuanLyDTO, String> tblTongSoVe;
    @FXML private TableColumn<ThongKeQuanLyDTO, String> tblSoLuongKhach;

    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        loadFieldTbl();
        wrapColumn(tblMaChuyenTau);
        wrapColumn(tblTenTau);
        loadDataDashboard();
    }

    private void wrapColumn(TableColumn<ThongKeQuanLyDTO, String> col) {
        col.setCellFactory(tc -> new TableCell<ThongKeQuanLyDTO, String>() {
            private final Text text = new Text();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    text.wrappingWidthProperty().bind(getTableColumn().widthProperty().subtract(10));
                    setGraphic(text);
                }
            }
        });
    }

    private void loadFieldTbl() {
        tblMaChuyenTau.setCellValueFactory(new PropertyValueFactory<>("maChuyenTau"));
        tblTenTau.setCellValueFactory(new PropertyValueFactory<>("tenTau"));
        tblNgayGioKhoiHanh.setCellValueFactory(new PropertyValueFactory<>("ngayGioKhoiHanhDate"));
        tblTyLeLapDay.setCellValueFactory(new PropertyValueFactory<>("tyLeLapDay"));
        tblTongSoVe.setCellValueFactory(new PropertyValueFactory<>("tongSoVe"));
        tblSoLuongKhach.setCellValueFactory(new PropertyValueFactory<>("soVeBanDuoc"));
    }

    @SuppressWarnings("unchecked")
    private void loadDataDashboard() {
        ngayThongKe.setValue(LocalDate.now());

        // Load table
        ThongKeChuyenTauRequest tblPayload = new ThongKeChuyenTauRequest(LocalDate.now(), null);
        Request tblRequest = new Request(CommandType.THONG_KE_CHUYEN_TAU_DONG_KHACH_TRONG_NGAY, tblPayload);
        Response tblResponse = socketClient.send(tblRequest);
        if (tblResponse.isSuccess() && tblResponse.getData() instanceof List<?> list) {
            tableView.setItems(FXCollections.observableArrayList((List<ThongKeQuanLyDTO>) list));
        }

        // Load chart
        Request chartRequest = new Request(CommandType.THONG_KE_TOP5_CHUYEN_TAU_DONG_KHACH, null);
        Response chartResponse = socketClient.send(chartRequest);
        System.out.println("Response success: " + chartResponse.isSuccess());
        System.out.println("Response message: " + chartResponse.getMessage());
        System.out.println("Response data type: " + (chartResponse.getData() == null ? "null" : chartResponse.getData().getClass().getName()));
        if (chartResponse.isSuccess() && chartResponse.getData() instanceof List<?> list) {
            showChart((List<DashboardQLDTO>) list);
        }
    }

    @SuppressWarnings("unchecked")
    public void lamMoi(MouseEvent mouseEvent) {
        ngayThongKe.setValue(LocalDate.now());
        tableView.getItems().clear();
        barChartTop5.getData().clear();

        ThongKeChuyenTauRequest tblPayload = new ThongKeChuyenTauRequest(LocalDate.now(), null);
        Request tblRequest = new Request(CommandType.THONG_KE_CHUYEN_TAU_DONG_KHACH_TRONG_NGAY, tblPayload);
        Response tblResponse = socketClient.send(tblRequest);
        if (tblResponse.isSuccess() && tblResponse.getData() instanceof List<?> list) {
            tableView.setItems(FXCollections.observableArrayList((List<ThongKeQuanLyDTO>) list));
        }

        Request chartRequest = new Request(CommandType.THONG_KE_TOP5_CHUYEN_TAU_DONG_KHACH, null);
        Response chartResponse = socketClient.send(chartRequest);
        if (chartResponse.isSuccess() && chartResponse.getData() instanceof List<?> list) {
            showChart((List<DashboardQLDTO>) list);
        }
    }

    @SuppressWarnings("unchecked")
    public void thongKe(MouseEvent mouseEvent) {
        LocalDate ngay = ngayThongKe.getValue();

        // Load table
        ThongKeChuyenTauRequest tblPayload = new ThongKeChuyenTauRequest(ngay, null);
        Request tblRequest = new Request(CommandType.THONG_KE_CHUYEN_TAU_DONG_KHACH_TRONG_NGAY, tblPayload);
        Response tblResponse = socketClient.send(tblRequest);
        if (tblResponse.isSuccess() && tblResponse.getData() instanceof List<?> list) {
            tableView.setItems(FXCollections.observableArrayList((List<ThongKeQuanLyDTO>) list));
        }

        // Load chart
        ThongKeChuyenTauRequest chartPayload = new ThongKeChuyenTauRequest(ngay, null);
        Request chartRequest = new Request(CommandType.THONG_KE_TOP5_CHUYEN_TAU_DONG_KHACH_THEO_NGAY, chartPayload);
        Response chartResponse = socketClient.send(chartRequest);
        if (chartResponse.isSuccess() && chartResponse.getData() instanceof List<?> list) {
            showChart((List<DashboardQLDTO>) list);
        }
    }

    private void showChart(List<DashboardQLDTO> top5CTDongKhachResult) {
        System.out.println("Chart data size: " + top5CTDongKhachResult.size());
        top5CTDongKhachResult.forEach(rs ->
                System.out.println("Ten: [" + rs.getTenChuyenTau() + "] - SoLuong: " + rs.getSoLuongVeTuongUng())
        );
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng khách");
        top5CTDongKhachResult.forEach(rs -> {
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

        barChartTop5.getData().clear();
        barChartTop5.getData().add(series);
        barChartTop5.getYAxis().setLabel("Số lượng khách");
        barChartTop5.getYAxis().setStyle("-fx-font-size: 11px;");
        GiaoDienUtils.Chart.veMuiTenTrucXY(barChartTop5);

        double max = series.getData().stream()
                .mapToDouble(d -> d.getYValue().doubleValue()).max().orElse(0);
        GiaoDienUtils.Chart.tangTrucY(barChartTop5, max);
    }
}
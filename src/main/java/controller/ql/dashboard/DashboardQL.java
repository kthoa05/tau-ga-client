package controller.ql.dashboard;

import dto.DashboardQLDTO;
import entity.enums.CaLamViec;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.scene.text.Font;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import utils.GiaoDienUtils;
import utils.consts.CurrentUser;
import utils.enums.ManHinh;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DashboardQL {
    @FXML
    private AnchorPane contentArea;

    @FXML
    private LineChart<String, Number> doanhThuTheoNgay;

    @FXML
    private LineChart<String, Number> lineChartChuyenTau;

    @FXML
    private BarChart<String, Number> soLuongVeTheoChuyen;

    @FXML
    private BarChart<String, Number> barChartTop5;

    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        xoayTrucX(soLuongVeTheoChuyen);
        xoayTrucX(barChartTop5);
        kichThuocGiaTriTrucXSLVe(soLuongVeTheoChuyen);
        kichThuocGiaTriTrucXTop5(barChartTop5);

        doanhThuTheoNgay();
        chuyenTauTheoNgay();
        soLuongVeTheoChuyen();
        top5ChuyenTau();
    }

    private void xoayTrucX(BarChart<String, Number> chart) {
        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setTickLabelRotation(0.1);
        xAxis.setTickLabelGap(5);
    }

    private void kichThuocGiaTriTrucXSLVe(BarChart<String, Number> chart) {
        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setTickLabelFont(Font.font(5));
        xAxis.setTickLabelGap(2);
    }

    private void kichThuocGiaTriTrucXTop5(BarChart<String, Number> chart) {
        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setTickLabelFont(Font.font(9));
        xAxis.setTickLabelGap(2);
    }

    /**
     * Số lượng vé theo chuyến đi: biểu đồ cột
     */
    private void soLuongVeTheoChuyen() {
        Response res = socketClient.send(new Request(CommandType.THONG_KE_SO_LUONG_VE_CHART_DASHBOARD, null));

        List<DashboardQLDTO> data = Collections.emptyList();
        if (res.isSuccess() && res.getData() instanceof List<?> list
                && !list.isEmpty() && list.get(0) instanceof DashboardQLDTO) {
            //noinspection unchecked
            data = (List<DashboardQLDTO>) list;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng vé");

        for (DashboardQLDTO rs : data) {
            XYChart.Data<String, Number> item = new XYChart.Data<>(
                    rs.getTenChuyenTau(), rs.getSoLuongVeTuongUng()
            );
            item.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    hienGiaTriTrenCot(item);
                    Tooltip.install(newNode, new Tooltip(item.getXValue()));
                }
            });
            series.getData().add(item);
        }

        soLuongVeTheoChuyen.getData().clear();
        soLuongVeTheoChuyen.getData().add(series);
        veMuiTenTrucXY(soLuongVeTheoChuyen);

        double max = series.getData().stream()
                .mapToDouble(d -> d.getYValue().doubleValue())
                .max().orElse(0);
        tangTrucY(soLuongVeTheoChuyen, max);
    }

    /**
     * Doanh thu theo ca: biểu đồ đường
     */
    private void doanhThuTheoNgay() {
        Response res = socketClient.send(new Request(CommandType.THONG_KE_DOANH_THU_DASHBOARD, null));

        Map<CaLamViec, Double> result = Collections.emptyMap();
        if (res.isSuccess() && res.getData() instanceof Map<?, ?> map) {
            //noinspection unchecked
            result = (Map<CaLamViec, Double>) map;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");

        for (CaLamViec ca : List.of(CaLamViec.CA1, CaLamViec.CA2, CaLamViec.CA3)) {
            XYChart.Data<String, Number> item = new XYChart.Data<>(
                    ca.getTenCa(), result.getOrDefault(ca, 0.0)
            );
            item.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Tooltip.install(newNode, new Tooltip(String.format("%,.0f", item.getYValue().doubleValue())));
                    ganHieuUngPhongTo(item);
                }
            });
            series.getData().add(item);
        }

        doanhThuTheoNgay.getData().clear();
        doanhThuTheoNgay.getData().add(series);
        veMuiTenTrucXY(doanhThuTheoNgay);
    }

    /**
     * Top 5 chuyến tàu đông khách: biểu đồ cột
     */
    private void top5ChuyenTau() {
        Response res = socketClient.send(new Request(CommandType.THONG_KE_TOP5_CHUYEN_TAU_DONG_KHACH, null));

        List<DashboardQLDTO> data = Collections.emptyList();
        if (res.isSuccess() && res.getData() instanceof List<?> list
                && !list.isEmpty() && list.get(0) instanceof DashboardQLDTO) {
            //noinspection unchecked
            data = (List<DashboardQLDTO>) list;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng khách");

        for (DashboardQLDTO rs : data) {
            XYChart.Data<String, Number> item = new XYChart.Data<>(
                    rs.getTenChuyenTau(), rs.getSoLuongVeTuongUng()
            );
            item.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    hienGiaTriTrenCot(item);
                    Tooltip.install(newNode, new Tooltip(item.getXValue()));
                }
            });
            series.getData().add(item);
        }

        barChartTop5.getData().clear();
        barChartTop5.getData().add(series);
        barChartTop5.getYAxis().setLabel("Số lượng khách");
        barChartTop5.getYAxis().setStyle("-fx-font-size: 11px;");
        veMuiTenTrucXY(barChartTop5);

        double max = series.getData().stream()
                .mapToDouble(d -> d.getYValue().doubleValue())
                .max().orElse(0);
        tangTrucY(barChartTop5, max);
    }

    /**
     * Số chuyến tàu theo ca: biểu đồ đường
     */
    private void chuyenTauTheoNgay() {
        Response res = socketClient.send(new Request(CommandType.THONG_KE_SO_LUONG_TAU_THEO_CA, null));

        Map<CaLamViec, Long> result = Collections.emptyMap();
        if (res.isSuccess() && res.getData() instanceof Map<?, ?> map) {
            //noinspection unchecked
            result = (Map<CaLamViec, Long>) map;
        }

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng chuyến tàu");

        for (CaLamViec ca : List.of(CaLamViec.CA1, CaLamViec.CA2, CaLamViec.CA3)) {
            XYChart.Data<String, Number> item = new XYChart.Data<>(
                    ca.getTenCa(), result.getOrDefault(ca, 0L)
            );
            item.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Tooltip.install(newNode, new Tooltip(
                            String.format("%,.0f", item.getYValue().doubleValue()) + " chuyến"));
                    ganHieuUngPhongTo(item);
                }
            });
            series.getData().add(item);
        }

        lineChartChuyenTau.getData().clear();
        lineChartChuyenTau.getData().add(series);
        veMuiTenTrucXY(lineChartChuyenTau);
    }

    private void hienGiaTriTrenCot(XYChart.Data<String, Number> data) {
        Node node = data.getNode();
        Label label = new Label(dinhDangTien(data.getYValue()));
        label.setStyle("-fx-font-size: 9; -fx-font-weight: bold;");
        StackPane parent = (StackPane) node;
        parent.getChildren().add(label);
        StackPane.setAlignment(label, Pos.TOP_CENTER);
        label.setTranslateY(-11);
    }

    private String dinhDangTien(Number value) {
        return String.format("%,.0f", value.doubleValue());
    }

    private void ganHieuUngPhongTo(XYChart.Data<String, Number> data) {
        Node node = data.getNode();
        if (node == null) return;
        node.setOnMouseEntered(e -> { node.setScaleX(1.6); node.setScaleY(1.6); });
        node.setOnMouseExited(e ->  { node.setScaleX(1);   node.setScaleY(1);   });
    }

    private void veMuiTenTrucXY(Chart chart) {
        Platform.runLater(() -> {
            Node plotArea = chart.lookup(".chart-plot-background");
            if (plotArea == null) return;

            Pane parent = (Pane) plotArea.getParent();

            Polygon arrowX = new Polygon(0, 0, -8, -5, -8, 5);
            arrowX.setStyle("-fx-fill: gray;");
            arrowX.setTranslateX(5);
            StackPane arrowXPane = new StackPane(arrowX);
            parent.getChildren().add(arrowXPane);
            arrowXPane.setLayoutX(plotArea.getLayoutX() + plotArea.getBoundsInParent().getWidth());
            arrowXPane.setLayoutY(plotArea.getLayoutY() + plotArea.getBoundsInParent().getHeight());

            Polygon arrowY = new Polygon(0, 0, -5, 8, 5, 8);
            arrowY.setStyle("-fx-fill: gray;");
            arrowY.setTranslateY(-5);
            StackPane arrowYPane = new StackPane(arrowY);
            parent.getChildren().add(arrowYPane);
            arrowYPane.setLayoutX(plotArea.getLayoutX());
            arrowYPane.setLayoutY(plotArea.getLayoutY());
        });
    }

    private void tangTrucY(XYChart<?, ?> chart, double maxValue) {
        NumberAxis yAxis = (NumberAxis) chart.getYAxis();
        yAxis.setAutoRanging(false);
        yAxis.setLowerBound(0);
        double upper = lamTronLen(maxValue * 1.2);
        yAxis.setUpperBound(upper);
        yAxis.setTickUnit(upper / 5);
    }

    private double lamTronLen(double value) {
        double base = Math.pow(10, Math.floor(Math.log10(value)));
        return Math.ceil(value / base) * base;
    }

    public void linkToSoLuongVeTheoChuyen(MouseEvent mouseEvent) {
        CurrentUser.setIsFromDashboard(Boolean.TRUE);
        GiaoDienUtils.ManHinhUtils.loadContent(contentArea, ManHinh.ThongKeSoLuongVeTheoChuyenDi);
    }

    public void linkToTop5ChuyenTauDongKhach(MouseEvent mouseEvent) {
        CurrentUser.setIsFromDashboard(Boolean.TRUE);
        GiaoDienUtils.getMainLayoutQL().goToFromDashboard(ManHinh.ThongKeTop5ChuyenTauDongKhach);
    }

    public void linkToDoanhThu(MouseEvent e) {
        CurrentUser.setIsFromDashboard(Boolean.TRUE);
        GiaoDienUtils.getMainLayoutQL().goToFromDashboard(ManHinh.ThongKeDoanhThuTheoNgay);
    }

    public void linkToChuyenTauTheoNgay(MouseEvent e) {
        CurrentUser.setIsFromDashboard(Boolean.TRUE);
        GiaoDienUtils.getMainLayoutQL().goToFromDashboard(ManHinh.ThongKeChuyenTauChayTheoNgay);
    }


}
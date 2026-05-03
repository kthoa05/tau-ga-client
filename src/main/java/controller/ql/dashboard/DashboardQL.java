package controller.ql.dashboard;

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
import service.IDashboardQuanLyService;
import service.impl.DashboardQuanLyServiceImpl;
import utils.GiaoDienUtils;
import utils.consts.CurrentUser;
import utils.enums.ManHinh;

import java.util.List;

public class DashboardQL {
    @FXML
    private AnchorPane contentArea;

    @FXML
    private LineChart<String, Number> doanhThuTheoNgay;

    @FXML
    private LineChart<String, Number> lineChartChuyenTau;

    @FXML
    private BarChart<String, Number> soLuongVeTheoChuyen;

    //service
    private final IDashboardQuanLyService iDashboardQuanLyService = new DashboardQuanLyServiceImpl();

    @FXML
    private BarChart<String, Number> barChartTop5;

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
        xAxis.setTickLabelFont(
                Font.font(5)
        );
        xAxis.setTickLabelGap(2);
    }
    private void kichThuocGiaTriTrucXTop5(BarChart<String, Number> chart) {
        CategoryAxis xAxis = (CategoryAxis) chart.getXAxis();
        xAxis.setTickLabelFont(
                Font.font(9)
        );
        xAxis.setTickLabelGap(2);
    }

    /**
     Số lượng vé theo chuyến đi: biểu đồ cột, cột x là tên tàu chạy trong ngày, cột y là số lượng vé
     */
    private void soLuongVeTheoChuyen(){
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        var soLuongVeTheoChuyenRs = iDashboardQuanLyService.soLuongVeTheoChuyen();
        series.setName("Số lượng vé");
        soLuongVeTheoChuyenRs.forEach(rs -> {
            XYChart.Data<String, Number> data = new XYChart.Data<>(
                    String.valueOf(rs.getTenChuyenTau()),
                    rs.getSoLuongVeTuongUng()
            );

            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    hienGiaTriTrenCot(data);
                    Tooltip tooltip = new Tooltip( data.getXValue());
                    Tooltip.install(newNode, tooltip);
                }
            });

            series.getData().add(data);
        });


        soLuongVeTheoChuyen.getData().clear();
        soLuongVeTheoChuyen.getData().add(series);
        veMuiTenTrucXY(soLuongVeTheoChuyen);
        double max = series.getData()
                .stream()
                .mapToDouble(d -> d.getYValue().doubleValue())
                .max()
                .orElse(0);

        tangTrucY(soLuongVeTheoChuyen, max);
    }

    /**
     Doanh thu: biểu đổ đường, cột x là ngày, cột y là doanh thu
     */
    private void doanhThuTheoNgay(){
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        var result = iDashboardQuanLyService.doanhThuTheoNgay();
        for (CaLamViec ca : List.of(CaLamViec.CA1, CaLamViec.CA2, CaLamViec.CA3)) {
            XYChart.Data<String, Number> data = new XYChart.Data<>(
                    ca.getTenCa(),
                    result.getOrDefault(ca, 0.0)
            );
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    Tooltip tooltip = new Tooltip(String.format("%,.0f", data.getYValue().doubleValue()));
                    Tooltip.install(newNode, tooltip);
                    ganHieuUngPhongTo(data);
                }
            });
            series.getData().add(data);

        }
        doanhThuTheoNgay.getData().clear();
        doanhThuTheoNgay.getData().add(series);
        veMuiTenTrucXY(doanhThuTheoNgay);
    }

    /**
    Top 5 chuyến tàu: biểu đồ cột, cột x là (mã tàu + giờ khởi hành) (VD: SE1-001 - 06:00) chạy trong ngày, cột y là số lượng khách
     */
    private void top5ChuyenTau() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Số lượng khách");
        var top5CTDongKhachResult = iDashboardQuanLyService.top5ChuyenTauDongKhach();
        top5CTDongKhachResult.forEach(rs -> {
            XYChart.Data<String, Number> data = new XYChart.Data<>(
                    String.valueOf(rs.getTenChuyenTau()),
                    rs.getSoLuongVeTuongUng()
            );

            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    hienGiaTriTrenCot(data);
                    Tooltip tooltip = new Tooltip( data.getXValue());
                    Tooltip.install(newNode, tooltip);
                }
            });

            series.getData().add(data);
        });

        barChartTop5.getData().clear();
        barChartTop5.getData().add(series);
        barChartTop5.getYAxis().setLabel("Số lượng khách");
        barChartTop5.getYAxis().setStyle("-fx-font-size: 11px;");
        veMuiTenTrucXY(barChartTop5);
        double max = series.getData()
                .stream()
                .mapToDouble(d -> d.getYValue().doubleValue())
                .max()
                .orElse(0);

        tangTrucY(barChartTop5, max);
    }

    /**
    Chuyến tàu chạy trong ngày: biểu đồ đường, cột x là ca làm (ca sáng, ca chiều, ca tối: chia theo 0-8, 8-16, 16-24), cột y là số chuyến của ca trong ngày
     */
    private void chuyenTauTheoNgay() {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        var result = iDashboardQuanLyService.soLuongTauTheoCa();
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
                    ganHieuUngPhongTo(data);
                }
            });
            series.getData().add(data);
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

        node.setOnMouseEntered(e -> {
            node.setScaleX(1.6);
            node.setScaleY(1.6);
        });

        node.setOnMouseExited(e -> {
            node.setScaleX(1);
            node.setScaleY(1);
        });
    }

    private void veMuiTenTrucXY(Chart chart) {
        Platform.runLater(() -> {
            Node plotArea = chart.lookup(".chart-plot-background");
            if (plotArea == null) return;

            Pane parent = (Pane) plotArea.getParent();

            Polygon arrowX = new Polygon(
                    0, 0,
                    -8, -5,
                    -8, 5
            );
            arrowX.setStyle("-fx-fill: gray;");
            arrowX.setTranslateX(5);

            StackPane arrowXPane = new StackPane(arrowX);
            parent.getChildren().add(arrowXPane);

            arrowXPane.setLayoutX(
                    plotArea.getLayoutX() + plotArea.getBoundsInParent().getWidth()
            );
            arrowXPane.setLayoutY(
                    plotArea.getLayoutY() + plotArea.getBoundsInParent().getHeight()
            );

            Polygon arrowY = new Polygon(
                    0, 0,
                    -5, 8,
                    5, 8
            );
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
        GiaoDienUtils.getMainLayoutQL()
                .goToFromDashboard(ManHinh.ThongKeTop5ChuyenTauDongKhach);
    }

    public void linkToDoanhThu(MouseEvent e) {
        CurrentUser.setIsFromDashboard(Boolean.TRUE);
        GiaoDienUtils.getMainLayoutQL()
                .goToFromDashboard(ManHinh.ThongKeDoanhThuTheoNgay);
    }

    public void linkToChuyenTauTheoNgay(MouseEvent e) {
        CurrentUser.setIsFromDashboard(Boolean.TRUE);
        GiaoDienUtils.getMainLayoutQL()
                .goToFromDashboard(ManHinh.ThongKeChuyenTauChayTheoNgay);
    }
}

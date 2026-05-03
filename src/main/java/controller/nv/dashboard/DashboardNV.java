package controller.nv.dashboard;

import dto.DashboardDTO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import service.IDashboardNVService;
import service.impl.DashboardServiceImpl;
import utils.GiaoDienUtils;
import utils.TauGaUtils;
import utils.consts.CurrentUser;
import utils.enums.ManHinh;

import java.util.List;


public class DashboardNV {
    @FXML
    private PieChart soLuongVe;
    @FXML
    private BarChart<String, Number> doanhThuCa;
    @FXML
    private Label lblNoDataDoanhThu;

    /**
     * txt
     */
    @FXML
    private Label txtSoVeBanDuocTrongCaLam;
    @FXML
    private Label txtSoGheVaGiuong;
    @FXML
    private Label txtTongDoanhThu;

    //service
    private final IDashboardNVService dashboardNVService = new DashboardServiceImpl();


    @FXML
    public void initialize() {
        loadDashboardData();
    }

    private void loadDashboardData() {
        DashboardDTO dataDashboard = dashboardNVService.getThongTinDashboard();
        if (dataDashboard == null) return;

        //lay field can thiet
        int tongVe = dataDashboard.getTongSoVeBanDuoc();
        int ghe = dataDashboard.getTongSoGhe();
        int giuong = dataDashboard.getTongSoGiuong();
        double doanhThu = dataDashboard.getTongDoanhThu();

        //set len txt field
        txtSoVeBanDuocTrongCaLam.setText(tongVe + " vé");
        txtSoGheVaGiuong.setText("Ghế: " + ghe + ", Giường: " + giuong);
        txtTongDoanhThu.setText(String.format("%s VND", TauGaUtils.NumberUtils.formatNumber(doanhThu)));

        //lay data de ve chart
        List<DashboardDTO> tongHopLoaiGhe = dashboardNVService.dashboardForLoaiGhe();
        List<DashboardDTO> tongHopTheoDoanhThu = dashboardNVService.dashboardForDoanhThu();

        if (tongHopLoaiGhe == null || tongHopLoaiGhe.isEmpty()) {
            //todo: neu ko co data
            return;
        }

        if (tongHopTheoDoanhThu == null || tongHopTheoDoanhThu.isEmpty()) {
            doanhThuCa.setVisible(false);
            lblNoDataDoanhThu.setVisible(true);
            return;
        }

        doanhThuCa.setVisible(true);
        lblNoDataDoanhThu.setVisible(false);
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        double tong = tongHopLoaiGhe.stream()
                .mapToDouble(DashboardDTO::getTongSoGhe)
                .sum();

        for (DashboardDTO dto : tongHopLoaiGhe) {
            double value = dto.getTongSoGhe();
            double percent = (value / tong) * 100;

            PieChart.Data data = new PieChart.Data(
                    dto.getTenLoaiGhe() + " (" + String.format("%.1f%%", percent) + ")",
                    value);
            pieData.add(data);
        }

        soLuongVe.setData(pieData);

        soLuongVe.setLegendVisible(true);
        soLuongVe.setLabelsVisible(false);
        ganTooltipChoPieChart(soLuongVe);



        //todo: hiển thị theo % từng loại vé


        //bieu do cot
        doanhThuCa.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        tongHopTheoDoanhThu.forEach(rs -> {
            XYChart.Data<String, Number> data =
                    new XYChart.Data<>(String.valueOf(rs.getGio()), rs.getDoanhThuTheoGio());

            series.getData().add(data);

            // Khi node của cột được tạo
            data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                if (newNode != null) {
                    hienGiaTriTrenCot(data);
                }
            });
        });

        doanhThuCa.getData().add(series);
        doanhThuCa.setScaleX(1.1);
        doanhThuCa.setScaleY(1.1);
        veMuiTenTrucX(doanhThuCa);
        veMuiTenTrucY(doanhThuCa);
    }

    private void ganTooltipChoPieChart(PieChart pieChart) {

        double tong = pieChart.getData()
                .stream()
                .mapToDouble(PieChart.Data::getPieValue)
                .sum();

        Platform.runLater(() -> {

            pieChart.applyCss();
            pieChart.layout();

            for (PieChart.Data data : pieChart.getData()) {

                Node node = data.getNode();
                if (node == null) continue;

                double percent = data.getPieValue() / tong * 100;

                Tooltip tooltip = new Tooltip(
                                String.format("%.1f%%", percent)
                );
                tooltip.setStyle("-fx-font-size: 12; -fx-font-weight: bold;");
                Tooltip.install(node, tooltip);

                DropShadow shadow = new DropShadow();
                shadow.setRadius(14);
                shadow.setColor(javafx.scene.paint.Color.rgb(0, 0, 0, 0.6));

                node.setOnMouseEntered(e -> node.setEffect(shadow));
                node.setOnMouseExited(e -> node.setEffect(null));
            }
        });
    }

    private void hienGiaTriTrenCot(XYChart.Data<String, Number> data) {
        Node node = data.getNode();

        Label label = new Label(dinhDangTien(data.getYValue()));
        label.setStyle("-fx-font-size: 9;");

        StackPane parent = (StackPane) node;
        parent.getChildren().add(label);

        StackPane.setAlignment(label, Pos.TOP_CENTER);
        label.setTranslateY(-11);
    }
    private String dinhDangTien(Number value) {
        return String.format("%,.0f", value.doubleValue());
    }


    private void veMuiTenTrucX(BarChart<String, Number> chart) {
        Platform.runLater(() -> {
            Node plotArea = chart.lookup(".chart-plot-background");
            if (plotArea == null) return;

            Polygon arrow = new Polygon(
                    0, 0,
                    -8, -5,
                    -8, 5
            );
            arrow.setStyle("-fx-fill: gray;");
            arrow.setTranslateX(5);

            StackPane arrowPane = new StackPane(arrow);

            Pane parent = (Pane) plotArea.getParent();
            parent.getChildren().add(arrowPane);

            arrowPane.setLayoutX(
                    plotArea.getLayoutX() + plotArea.getBoundsInParent().getWidth()
            );
            arrowPane.setLayoutY(
                    plotArea.getLayoutY() + plotArea.getBoundsInParent().getHeight()
            );
        });
    }


    private void veMuiTenTrucY(BarChart<String, Number> chart) {
        Platform.runLater(() -> {
            Node plotArea = chart.lookup(".chart-plot-background");
            if (plotArea == null) return;
            Polygon arrow = new Polygon(
                    0, 0,
                    -5, 8,
                    5, 8
            );
            arrow.setStyle("-fx-fill: gray;");
            arrow.setTranslateY(-5);

            StackPane arrowPane = new StackPane(arrow);

            Pane parent = (Pane) plotArea.getParent();
            parent.getChildren().add(arrowPane);

            arrowPane.setLayoutX(plotArea.getLayoutX());
            arrowPane.setLayoutY(plotArea.getLayoutY());
        });
    }

    public void soLuongVeBanTheoGhe(MouseEvent mouseEvent) {
        CurrentUser.setIsFromDashboard(Boolean.TRUE);
        GiaoDienUtils.getMainLayoutNV().goToFromDashboard(ManHinh.SoLuongVe);
    }

    public void doanhThuTheoGioTrongCa(MouseEvent mouseEvent) {
        CurrentUser.setIsFromDashboard(Boolean.TRUE);
        GiaoDienUtils.getMainLayoutNV().goToFromDashboard(ManHinh.DoanhThu);
    }


}

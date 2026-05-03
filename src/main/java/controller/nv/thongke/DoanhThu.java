package controller.nv.thongke;

import com.jfoenix.controls.JFXComboBox;
import dto.ThongKeDTO;
import entity.enums.CaLamViec;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.shape.Polygon;
import javafx.util.StringConverter;
import service.IThongKeService;
import service.impl.ThongKeServiceImpl;
import utils.GiaoDienUtils;
import utils.TauGaUtils;
import utils.consts.CurrentUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Comparator;
import java.util.List;

public class DoanhThu {
    @FXML
    private JFXComboBox<Integer> cbcaLamViec;


    @FXML
    private DatePicker dateTimeNgayThongKe;

    //chart
    @FXML
    private BarChart<String, Number> doanhThuCa;

    //tbl
    @FXML
    private TableView<ThongKeDTO> tableView;

    @FXML
    private TableColumn<ThongKeDTO, Integer> tblSttCa;

    @FXML
    private TableColumn<ThongKeDTO, String> tblGioLam;

    @FXML
    private TableColumn<ThongKeDTO, String> tblTongDoanhThu;

    private ObservableList<Integer> allCaLamViec;
    private FilteredList<Integer> filteredCa;


    private final IThongKeService thongKeService = new ThongKeServiceImpl();


    @FXML
    public void initialize() {

        GiaoDienUtils.removeUnderlineHighlight(cbcaLamViec);
        GiaoDienUtils.apDungStyleInput(cbcaLamViec);

        if (CurrentUser.isIsFromDashboard()) {
            this.loadDataTuDashboard();
        }
        this.loadFieldTbl();
        this.loadCbb();
        configAutoCompleteCbb();

        cbcaLamViec.setConverter(new StringConverter<>() {
            @Override
            public String toString(Integer value) {
                return value == null ? "" : value.toString();
            }

            @Override
            public Integer fromString(String string) {
                return Integer.valueOf(string);
            }
        });
    }

    private void configAutoCompleteCbb() {

        TextField editor = cbcaLamViec.getEditor();

        editor.textProperty().addListener((obs, oldText, newText) -> {
            if (newText == null || newText.isBlank()) {
                filteredCa.setPredicate(p -> true);
                cbcaLamViec.hide();
                return;
            }

            filteredCa.setPredicate(item ->
                    item.toString().startsWith(newText)
            );

            if (!filteredCa.isEmpty()) {
                cbcaLamViec.show();
            } else {
                cbcaLamViec.hide();
            }
        });

        editor.setOnKeyPressed(event -> {
            if (event.getCode() == javafx.scene.input.KeyCode.ENTER) {

                String text = editor.getText();

                try {
                    Integer value = Integer.parseInt(text);

                    if (allCaLamViec.contains(value)) {
                        cbcaLamViec.setValue(value);
                    } else {
                        cbcaLamViec.setValue(null);
                    }

                } catch (NumberFormatException e) {
                    cbcaLamViec.setValue(null);
                }

                cbcaLamViec.hide();
                event.consume();
            }
        });
    }

    private void loadCbb() {

        allCaLamViec = FXCollections.observableArrayList();

        for (CaLamViec ca : CaLamViec.values()) {
            if (ca.getSoThuTuCa() != 0) {
                allCaLamViec.add(ca.getSoThuTuCa());
            }
        }

        filteredCa = new FilteredList<>(allCaLamViec, p -> true);

        cbcaLamViec.setItems(filteredCa);
        cbcaLamViec.setEditable(true);
    }



    private void loadFieldTbl(){
        tblSttCa.setCellValueFactory(new PropertyValueFactory<>("sttCa"));
        tblGioLam.setCellValueFactory(new PropertyValueFactory<>("gioLam"));
        tblTongDoanhThu.setCellValueFactory(new PropertyValueFactory<>("tongDoanhThu"));
    }



    private void loadDataTuDashboard(){
        int stt = CurrentUser.getSoThuTuCa();
        LocalDate ngayLam = CurrentUser.getNgayLamCuaCa();

        if (stt != 0){
            cbcaLamViec.setValue(stt);
        }

        if (ngayLam != null){
            dateTimeNgayThongKe.setPromptText(TauGaUtils.DateTimeUtils.convertLocalDateToString(ngayLam, TauGaUtils.FORMATTER_DATE));
        }

        //load data table
        var resultDataTbl = thongKeService.showTblFromDashboardForDT(stt);
        ObservableList<ThongKeDTO> data = FXCollections.observableArrayList(resultDataTbl);
        tableView.setItems(data);

        //ve chart
        List<ThongKeDTO> tongHopTheoDoanhThu = thongKeService.thongKeDoanhThuTheoMaCa(CurrentUser.getMaCaLamViec());
        this.showChart(tongHopTheoDoanhThu);
    }


    public void lamMoi(MouseEvent mouseEvent) {
        var sttCa = CaLamViec.getByTimeNow(LocalTime.now());
        cbcaLamViec.getSelectionModel().select(sttCa.getSoThuTuCa());
        dateTimeNgayThongKe.setPromptText(TauGaUtils.DateTimeUtils.convertLocalDateToString(LocalDate.now(), TauGaUtils.FORMATTER_DATE));
    }

    public void thongKe(MouseEvent mouseEvent) {

        Integer soThuTuCa = cbcaLamViec.getSelectionModel().getSelectedItem();

        if (soThuTuCa == null) {
            GiaoDienUtils.showThongBao(
                    Alert.AlertType.ERROR, "", "Vui lòng chọn ca làm việc");
            return;
        }

        LocalDate ngayLam = dateTimeNgayThongKe.getValue();
        if (ngayLam == null) {
            GiaoDienUtils.showThongBao(
                    Alert.AlertType.ERROR, "", "Vui lòng chọn ngày thống kê");
            return;
        }

        showChart(thongKeService.thongKeDoanhThuVeChart(soThuTuCa, ngayLam));
        tableView.setItems(FXCollections.observableArrayList(
                thongKeService.thongKeDoanhThuForTable(soThuTuCa, ngayLam)
        ));
    }

    private void showChart(List<ThongKeDTO> tongHopTheoDoanhThu) {
        doanhThuCa.getData().clear();

        CategoryAxis axis = (CategoryAxis) doanhThuCa.getXAxis();

        List<String> gioCoDoanhThu = tongHopTheoDoanhThu.stream()
                .filter(rs -> rs.getDoanhThuTungGio() > 0)
                .sorted(Comparator.comparingInt(ThongKeDTO::getGioLamInt))
                .map(ThongKeDTO::getGioLamHienThi)
                .distinct()
                .toList();

        axis.setCategories(FXCollections.observableArrayList(gioCoDoanhThu));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");

        tongHopTheoDoanhThu.forEach(rs -> {
            if (rs.getDoanhThuTungGio() > 0) {
                XYChart.Data<String, Number> data =
                        new XYChart.Data<>(
                                rs.getGioLamHienThi(),
                                rs.getDoanhThuTungGio()
                        );

                series.getData().add(data);
                data.nodeProperty().addListener((obs, oldNode, newNode) -> {
                    if (newNode != null) {
                        hienGiaTriTrenCot(data);
                    }
                });
            }
        });

        doanhThuCa.getData().add(series);
        veMuiTenTrucX(doanhThuCa);
        veMuiTenTrucY(doanhThuCa);
    }

    private void hienGiaTriTrenCot(XYChart.Data<String, Number> data) {
        Node node = data.getNode();
        Label label = new Label(dinhDangTien(data.getYValue()));
        label.setStyle("-fx-font-size: 10;");

        StackPane parent = (StackPane) node;
        parent.getChildren().add(label);

        StackPane.setAlignment(label, Pos.TOP_CENTER);
        label.setTranslateY(-13);
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
}

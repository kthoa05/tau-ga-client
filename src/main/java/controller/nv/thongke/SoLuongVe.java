package controller.nv.thongke;

import com.jfoenix.controls.JFXComboBox;
import dto.ThongKeDTO;
import entity.enums.CaLamViec;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.PieChart;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import service.IThongKeService;
import service.impl.ThongKeServiceImpl;
import utils.TauGaUtils;
import utils.consts.CurrentUser;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public class SoLuongVe {
    @FXML
    public JFXComboBox cbcaLamViec;


    @FXML
    private DatePicker dateTimeNgayThongKe;

    //chart
    @FXML
    private PieChart soLuongVe;

    //table
    @FXML
    private TableView<ThongKeDTO> tableView;

    //field tbl
    @FXML
    private TableColumn<ThongKeDTO, Integer> tblSttCa;

    @FXML
    private TableColumn<ThongKeDTO, String> tblGioLam;

    @FXML
    private TableColumn<ThongKeDTO, Double> tblTongSoGhe;

    @FXML
    private TableColumn<ThongKeDTO, Double> tblTongSoGiuong;

    @FXML
    private TableColumn<ThongKeDTO, Double> tblTongSoVe;

    //service
    private final IThongKeService thongKeService = new ThongKeServiceImpl();

    private ObservableList<Integer> allCaLamViec;


    @FXML
    public void initialize() {
        cbcaLamViec.setStyle("-fx-border-color: #BFBFBF; -fx-border-radius: 3; -fx-background-radius: 3; -fx-padding: 0 0 0 4; -jfx-focus-color: #3f51b5; -jfx-unfocus-color: #9e9e9e;");
        removeUnderlineHighlight(cbcaLamViec);
        this.hienThiGiaTriMacDinh();
        var isFromDashboard = CurrentUser.isIsFromDashboard();
        if (isFromDashboard){
            this.loadDataTuDashboard();
        }
        this.loadCbb();
        this.loadFieldTbl();
        configAutoCompleteCbb();
    }

    private void hienThiGiaTriMacDinh(){
        dateTimeNgayThongKe.setValue(LocalDate.now());
        var timeNow = CaLamViec.getByTimeNow(LocalTime.now());
        cbcaLamViec.setValue(String.valueOf(timeNow.getSoThuTuCa()));
    }

    private void loadFieldTbl(){
        tblSttCa.setCellValueFactory(new PropertyValueFactory<>("sttCa"));
        tblGioLam.setCellValueFactory(new PropertyValueFactory<>("gioLam"));
        tblTongSoGhe.setCellValueFactory(new PropertyValueFactory<>("tongSoGhe"));
        tblTongSoGiuong.setCellValueFactory(new PropertyValueFactory<>("tongSoGiuong"));
        tblTongSoVe.setCellValueFactory(new PropertyValueFactory<>("tongSoVe"));

    }

    private void configAutoCompleteCbb() {

        var editor = cbcaLamViec.getEditor();

        editor.textProperty().addListener((obs, oldText, newText) -> {

            if (newText == null || newText.isBlank()) {
                cbcaLamViec.setItems(allCaLamViec);
                cbcaLamViec.hide();
                return;
            }

            ObservableList<Integer> filtered = FXCollections.observableArrayList();

            for (Integer i : allCaLamViec) {
                if (i.toString().startsWith(newText)) {
                    filtered.add(i);
                }
            }

            cbcaLamViec.setItems(filtered);

            if (!filtered.isEmpty()) {
                cbcaLamViec.show();
            } else {
                cbcaLamViec.hide();
            }
        });

        editor.setOnKeyPressed(e -> {
            if (e.getCode() == javafx.scene.input.KeyCode.ENTER) {

                String text = editor.getText();
                if (text == null || text.isBlank()) return;

                for (Integer i : allCaLamViec) {
                    if (i.toString().equals(text)) {
                        cbcaLamViec.setValue(i);
                        break;
                    }
                }

                cbcaLamViec.hide();
                e.consume();
            }
        });
    }


//    private void loadCbb(){
//        ObservableList<Integer> sttList = FXCollections.observableArrayList();
//        for (CaLamViec ca : CaLamViec.values()) {
//            if (ca.getSoThuTuCa() != 0) {
//                sttList.add(ca.getSoThuTuCa());
//            }
//        }
//        cbcaLamViec.setItems(sttList);
//    }

    private void loadCbb(){
        allCaLamViec = FXCollections.observableArrayList();

        for (CaLamViec ca : CaLamViec.values()) {
            if (ca.getSoThuTuCa() != 0) {
                allCaLamViec.add(ca.getSoThuTuCa());
            }
        }

        cbcaLamViec.setItems(allCaLamViec);
        cbcaLamViec.setEditable(true);
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
        var resultDataTbl = thongKeService.showTblFromDashboardForLV(stt);
        ObservableList<ThongKeDTO> data = FXCollections.observableArrayList(resultDataTbl);
        tableView.setItems(data);

        //ve chart
        List<ThongKeDTO> tongHopTheoDoanhThu = thongKeService.thongKeLoaiVeTuDashboard(CurrentUser.getMaCaLamViec());
        this.showChart(tongHopTheoDoanhThu);
    }

    private void removeUnderlineHighlight(JFXComboBox<?> combo) {
        combo.setEditable(true);

        Platform.runLater(() -> {
            combo.lookupAll(".input-line").forEach(n ->
                    n.setStyle("-fx-opacity: 0; -fx-background-color: transparent;"));
            combo.lookupAll(".input-focused-line").forEach(n ->
                    n.setStyle("-fx-opacity: 0; -fx-background-color: transparent;"));
        });

        combo.getEditor().focusedProperty().addListener((obs, oldVal, newVal) -> {
            Platform.runLater(() -> {
                combo.lookupAll(".input-line").forEach(n ->
                        n.setStyle("-fx-opacity: 0; -fx-background-color: transparent;"));
                combo.lookupAll(".input-focused-line").forEach(n ->
                        n.setStyle("-fx-opacity: 0; -fx-background-color: transparent;"));
            });
        });
    }

    public void thongKe(MouseEvent mouseEvent) {
        var soThuTuCa = Integer.parseInt(cbcaLamViec.getSelectionModel().getSelectedItem().toString());
        var ngayLam = dateTimeNgayThongKe.getValue();

        //ve chart
        var tongHopTheoDoanhThuChart = thongKeService.thongKeLoaiVeVeChart(soThuTuCa, ngayLam);
        this.showChart(tongHopTheoDoanhThuChart);

        //fill data tbl
        var resultDataTbl = thongKeService.thongKeLoaiVeForTbl(soThuTuCa, ngayLam);
        ObservableList<ThongKeDTO> data = FXCollections.observableArrayList(resultDataTbl);
        tableView.setItems(data);
    }

    public void lamMoi(MouseEvent mouseEvent) {
        var sttCa = CaLamViec.getByTimeNow(LocalTime.now());
        cbcaLamViec.getSelectionModel().select(sttCa.getSoThuTuCa());
        dateTimeNgayThongKe.setPromptText(TauGaUtils.DateTimeUtils.convertLocalDateToString(LocalDate.now(), TauGaUtils.FORMATTER_DATE));
    }

    private void showChart(List<ThongKeDTO> tongHopLoaiGhe){
        ObservableList<PieChart.Data> pieData = FXCollections.observableArrayList();
        double tong = tongHopLoaiGhe.stream()
                .mapToInt(ThongKeDTO::getTongSoGheTungLoai)
                .sum();

        for (ThongKeDTO dto : tongHopLoaiGhe) {
            double value = dto.getTongSoGheTungLoai();
            double percent = (value / tong) * 100;

            PieChart.Data data = new PieChart.Data(
                    dto.getTenLoaiGhe() + " (" + String.format("%.1f%%", percent) + ")",
                    value);
            pieData.add(data);
        }

        soLuongVe.setData(pieData);
        soLuongVe.setLegendVisible(true);
        soLuongVe.setLabelsVisible(false);
        soLuongVe.setScaleX(0.85);
        soLuongVe.setScaleY(0.85);
    }
}

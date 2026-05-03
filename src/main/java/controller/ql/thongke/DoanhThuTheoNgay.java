package controller.ql.thongke;

import dto.DoanhThuDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.ThongKeDoanhThuRequest;
import utils.GiaoDienUtils;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class DoanhThuTheoNgay {

    @FXML private RadioButton rdNgay;
    @FXML private RadioButton rdThang;
    @FXML private RadioButton rdNam;
    @FXML private RadioButton rdTuyChon;
    @FXML private DatePicker dpNgay;
    @FXML private TextField txtThang;
    @FXML private TextField txtThangNam;
    @FXML private TextField txtNam;
    @FXML private VBox boxNgay;
    @FXML private VBox boxThang;
    @FXML private VBox boxNam;
    @FXML private VBox boxTuyChon;

    private ToggleGroup tgThongKe;

    @FXML private TableView<DoanhThuDTO> tableNgay;
    @FXML private TableColumn<DoanhThuDTO, LocalDate> colNgay;
    @FXML private TableColumn<DoanhThuDTO, Double> colDoanhThu;
    @FXML private BarChart<String, Number> barChartNgay;

    @FXML private BarChart<String, Number> barChartThang;
    @FXML private TableView<DoanhThuDTO> tableThang;
    @FXML private TableColumn<DoanhThuDTO, LocalDate> colThang;
    @FXML private TableColumn<DoanhThuDTO, Double> colDoanhThuThang;

    @FXML private BarChart<String, Number> barChartNam;
    @FXML private TableView<DoanhThuDTO> tableNam;
    @FXML private TableColumn<DoanhThuDTO, LocalDate> colNam;
    @FXML private TableColumn<DoanhThuDTO, Double> colDoanhThuNam;

    @FXML public CategoryAxis xAxisThang;
    @FXML public DatePicker dpTuNgay;
    @FXML public DatePicker dpDenNgay;
    @FXML public TableView<DoanhThuDTO> tableTuyChon;
    @FXML private TableColumn<DoanhThuDTO, LocalDate> colTuNgay;
    @FXML private TableColumn<DoanhThuDTO, LocalDate> colDenNgay;
    @FXML private TableColumn<DoanhThuDTO, Double> colDoanhThuTuyChon;
    @FXML private BarChart<String, Number> barChartTuyChon;

    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        tgThongKe = new ToggleGroup();
        rdNgay.setToggleGroup(tgThongKe);
        rdThang.setToggleGroup(tgThongKe);
        rdNam.setToggleGroup(tgThongKe);
        rdTuyChon.setToggleGroup(tgThongKe);

        rdNgay.setSelected(true);
        hienThi(boxNgay);

        tgThongKe.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal == rdNgay) hienThi(boxNgay);
            else if (newVal == rdThang) hienThi(boxThang);
            else if (newVal == rdNam) hienThi(boxNam);
            else if (newVal == rdTuyChon) hienThi(boxTuyChon);
        });

        colNgay.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));
        colNgay.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected void updateItem(LocalDate value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : value.format(fmt));
            }
        });
        colDoanhThu.setCellValueFactory(new PropertyValueFactory<>("doanhThu"));
        colDoanhThu.setCellFactory(column -> new TableCell<>() {
            private final DecimalFormat df = new DecimalFormat("#,###");
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : df.format(value));
            }
        });

        colThang.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));
        colThang.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM");
            @Override
            protected void updateItem(LocalDate value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : value.format(fmt));
            }
        });
        colDoanhThuThang.setCellValueFactory(new PropertyValueFactory<>("doanhThu"));
        colDoanhThuThang.setCellFactory(column -> new TableCell<>() {
            private final DecimalFormat df = new DecimalFormat("#,###");
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : df.format(value));
            }
        });

        colNam.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));
        colNam.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy");
            @Override
            protected void updateItem(LocalDate value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : value.format(fmt));
            }
        });
        colDoanhThuNam.setCellValueFactory(new PropertyValueFactory<>("doanhThu"));
        colDoanhThuNam.setCellFactory(column -> new TableCell<>() {
            private final DecimalFormat df = new DecimalFormat("#,###");
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : df.format(value));
            }
        });

        colTuNgay.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));
        colTuNgay.setText("Từ ngày");
        colTuNgay.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected void updateItem(LocalDate value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : value.format(fmt));
            }
        });
        colDenNgay.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            @Override
            protected void updateItem(LocalDate value, boolean empty) {
                super.updateItem(value, empty);
                if (empty) {
                    setText(null);
                } else {
                    LocalDate den = dpDenNgay.getValue();
                    setText(den != null ? den.format(fmt) : null);
                }
            }
        });
        colDenNgay.setCellValueFactory(new PropertyValueFactory<>("thoiGian"));
        colDoanhThuTuyChon.setCellValueFactory(new PropertyValueFactory<>("doanhThu"));
        colDoanhThuTuyChon.setCellFactory(column -> new TableCell<>() {
            private final DecimalFormat df = new DecimalFormat("#,### VNĐ");
            @Override
            protected void updateItem(Double value, boolean empty) {
                super.updateItem(value, empty);
                setText(empty || value == null ? null : df.format(value));
            }
        });

        tgThongKe.selectedToggleProperty().addListener((obs, old, now) -> {
            boxNgay.setVisible(rdNgay.isSelected());
            boxNgay.setManaged(rdNgay.isSelected());
            boxThang.setVisible(rdThang.isSelected());
            boxThang.setManaged(rdThang.isSelected());
            boxNam.setVisible(rdNam.isSelected());
            boxNam.setManaged(rdNam.isSelected());
            boxTuyChon.setVisible(rdTuyChon.isSelected());
            boxTuyChon.setManaged(rdTuyChon.isSelected());
        });
    }

    private void hienThi(VBox box) {
        for (VBox b : new VBox[]{boxNgay, boxThang, boxNam, boxTuyChon}) {
            b.setVisible(false);
            b.setManaged(false);
        }
        box.setVisible(true);
        box.setManaged(true);
    }

    @FXML
    public void btnThongKeNgay(ActionEvent actionEvent) {
        LocalDate ngay = dpNgay.getValue();
        if (ngay == null) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng chọn ngày để thống kê!");
            return;
        }

        ThongKeDoanhThuRequest payload = new ThongKeDoanhThuRequest(ngay, null, null, null, null);
        Request request = new Request(CommandType.THONG_KE_DOANH_THU_THEO_NGAY, payload);
        Response response = socketClient.send(request);

        if (response.isSuccess() && response.getData() instanceof List<?> list) {
            @SuppressWarnings("unchecked")
            ObservableList<DoanhThuDTO> data = FXCollections.observableArrayList((List<DoanhThuDTO>) list);
            tableNgay.setItems(data);
            loadChart(barChartNgay, data);
        }
    }

    private void loadChart(BarChart<String, Number> chart, ObservableList<DoanhThuDTO> data) {
        chart.getData().clear();
        if (data == null || data.isEmpty()) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        for (DoanhThuDTO dto : data) {
            if (dto.getThoiGian() != null) {
                series.getData().add(new XYChart.Data<>(dto.getThoiGian().format(fmt), dto.getDoanhThu()));
            }
        }
        chart.getData().add(series);
    }

    public void btnThongKeThang(ActionEvent actionEvent) {
        if (txtThang.getText().isEmpty() || txtThangNam.getText().isEmpty()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "", "Vui lòng nhập tháng và năm");
            return;
        }

        int thang = Integer.parseInt(txtThang.getText());
        int nam = Integer.parseInt(txtThangNam.getText());

        ThongKeDoanhThuRequest payload = new ThongKeDoanhThuRequest(null, thang, nam, null, null);
        Request request = new Request(CommandType.THONG_KE_DOANH_THU_THEO_THANG, payload);
        Response response = socketClient.send(request);

        if (response.isSuccess() && response.getData() instanceof List<?> list) {
            @SuppressWarnings("unchecked")
            ObservableList<DoanhThuDTO> data = FXCollections.observableArrayList((List<DoanhThuDTO>) list);
            tableThang.setItems(data);
            loadChartThang(barChartThang, data);
        }

    }

    private void loadChartThang(BarChart<String, Number> chart, ObservableList<DoanhThuDTO> data) {
        chart.getData().clear();
        if (data == null || data.isEmpty()) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd");

        for (DoanhThuDTO dto : data) {
            if (dto.getThoiGian() != null) {
                series.getData().add(new XYChart.Data<>(dto.getThoiGian().format(fmt), dto.getDoanhThu()));
            }
        }
        chart.getData().add(series);
    }

    @FXML
    public void btnThongKeNam(ActionEvent e) {
        if (txtNam.getText().isEmpty()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "", "Vui lòng nhập năm");
            return;
        }

        int nam = Integer.parseInt(txtNam.getText());

        ThongKeDoanhThuRequest payload = new ThongKeDoanhThuRequest(null, null, nam, null, null);
        Request request = new Request(CommandType.THONG_KE_DOANH_THU_THEO_NAM, payload);
        Response response = socketClient.send(request);

        if (response.isSuccess() && response.getData() instanceof List<?> list) {
            @SuppressWarnings("unchecked")
            ObservableList<DoanhThuDTO> data = FXCollections.observableArrayList((List<DoanhThuDTO>) list);
            tableNam.setItems(data);
            loadChartNam(barChartNam, data);
        }
    }

    private void loadChartNam(BarChart<String, Number> chart, ObservableList<DoanhThuDTO> data) {
        chart.getData().clear();
        if (data.isEmpty()) return;

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Doanh thu");
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("MM");

        for (DoanhThuDTO dto : data) {
            series.getData().add(new XYChart.Data<>(dto.getThoiGian().format(fmt), dto.getDoanhThu()));
        }
        chart.getData().add(series);
    }

    public void btnThongKeTuyChon(ActionEvent actionEvent) {
        LocalDate tuNgay = dpTuNgay.getValue();
        LocalDate denNgay = dpDenNgay.getValue();

        if (tuNgay == null || denNgay == null) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Thiếu thông tin", "Vui lòng chọn đầy đủ từ ngày và đến ngày");
            return;
        }
        if (denNgay.isBefore(tuNgay)) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Ngày không hợp lệ", "Ngày đến phải lớn hơn hoặc bằng ngày bắt đầu");
            return;
        }

        ThongKeDoanhThuRequest payload = new ThongKeDoanhThuRequest(null, null, null, tuNgay, denNgay);
        Request request = new Request(CommandType.THONG_KE_DOANH_THU_TUY_CHON, payload);
        Response response = socketClient.send(request);

        if (response.isSuccess() && response.getData() instanceof List<?> list) {
            @SuppressWarnings("unchecked")
            ObservableList<DoanhThuDTO> data = FXCollections.observableArrayList((List<DoanhThuDTO>) list);
            tableTuyChon.setItems(data);

            barChartTuyChon.getData().clear();
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            String label = tuNgay.format(DateTimeFormatter.ofPattern("dd/MM"))
                    + " - "
                    + denNgay.format(DateTimeFormatter.ofPattern("dd/MM"));
            series.setName("Tổng doanh thu giai đoạn");
            if (!data.isEmpty()) {
                series.getData().add(new XYChart.Data<>(label, data.get(0).getDoanhThu()));
            }
            barChartTuyChon.getData().add(series);
        }
    }
}
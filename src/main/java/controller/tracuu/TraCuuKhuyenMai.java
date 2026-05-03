package controller.tracuu;

import dto.KhuyenMaiDTO;
import entity.enums.LoaiKhuyenMai;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.util.StringConverter;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import utils.GiaoDienUtils;
import utils.TauGaUtils;

import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class TraCuuKhuyenMai {

    @FXML private DatePicker dpNgayBD;
    @FXML private DatePicker dpNgayKT;

    @FXML private ComboBox<String> cbTrangThai;
    @FXML private ComboBox<LoaiKhuyenMai> cbLoaiKhuyenMai;

    @FXML private Button btnTimKiem;

    @FXML private TableView<KhuyenMaiDTO> tableKhuyenMai;

    @FXML private TableColumn<KhuyenMaiDTO, String> colMaKM;
    @FXML private TableColumn<KhuyenMaiDTO, String> colTenKM;
    @FXML private TableColumn<KhuyenMaiDTO, String> colMoTa;

    @FXML private TableColumn<KhuyenMaiDTO, LocalDate> colNgayBD;
    @FXML private TableColumn<KhuyenMaiDTO, LocalDate> colNgayKT;

    @FXML private TableColumn<KhuyenMaiDTO, Integer> colGiaTriTien;
    @FXML private TableColumn<KhuyenMaiDTO, Integer> colGiaTriPT;

    @FXML private TableColumn<KhuyenMaiDTO, LoaiKhuyenMai> colLoaiKM;
    @FXML private TableColumn<KhuyenMaiDTO, String> colTrangThai;

    private final ObservableList<KhuyenMaiDTO> khuyenMaiList =
            FXCollections.observableArrayList();

    private final SocketClient socketClient = new SocketClient();

    private final NumberFormat currencyFormatter =
            NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {

        cbTrangThai.setItems(FXCollections.observableArrayList(
                "Tất cả",
                "Sắp diễn ra",
                "Đang diễn ra",
                "Đã kết thúc"
        ));

        cbTrangThai.getSelectionModel().selectFirst();

        cbLoaiKhuyenMai.setItems(
                FXCollections.observableArrayList(LoaiKhuyenMai.values())
        );

        cbLoaiKhuyenMai.setConverter(new StringConverter<>() {
            @Override
            public String toString(LoaiKhuyenMai loai) {
                return loai != null ? loai.getLabel() : "";
            }

            @Override
            public LoaiKhuyenMai fromString(String string) {
                return null;
            }
        });

        DateTimeFormatter formatter = TauGaUtils.FORMATTER_DATE;

        StringConverter<LocalDate> dateConverter = new StringConverter<>() {
            @Override
            public String toString(LocalDate date) {
                return date != null ? formatter.format(date) : "";
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, formatter);
                }
                return null;
            }
        };

        dpNgayBD.setConverter(dateConverter);
        dpNgayKT.setConverter(dateConverter);

        colMaKM.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMaKhuyenMai()));

        colTenKM.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getTenKhuyenMai()));

        colMoTa.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getMoTa()));

        colNgayBD.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getNgayBatDau()));

        colNgayKT.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getNgayKetThuc()));

        colNgayBD.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null)
                        ? null
                        : formatter.format(item));
            }
        });

        colNgayKT.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                setText((empty || item == null)
                        ? null
                        : formatter.format(item));
            }
        });

        colGiaTriTien.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getGiaTriTien()));

        colGiaTriTien.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item == 0) {
                    setText(null);
                } else {
                    setText(currencyFormatter.format(item));
                }

                setStyle("-fx-alignment: CENTER-RIGHT;");
            }
        });

        colGiaTriPT.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getGiaTriPhanTram()));

        colGiaTriPT.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null || item == 0) {
                    setText(null);
                } else {
                    setText(item + " %");
                }

                setStyle("-fx-alignment: CENTER;");
            }
        });

        colLoaiKM.setCellValueFactory(cellData ->
                new SimpleObjectProperty<>(cellData.getValue().getLoaiKhuyenMai()));

        colLoaiKM.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LoaiKhuyenMai item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getLabel());
                }
            }
        });

        colTrangThai.setCellValueFactory(cellData ->
                new SimpleStringProperty(
                        tinhTrangThai(
                                cellData.getValue().getNgayBatDau(),
                                cellData.getValue().getNgayKetThuc()
                        )
                )
        );

        colTenKM.setCellFactory(col -> createWrapTextCell(col));
        colMoTa.setCellFactory(col -> createWrapTextCell(col));

        tableKhuyenMai.setItems(khuyenMaiList);

        loadData();
    }

    private TableCell<KhuyenMaiDTO, String> createWrapTextCell(
            TableColumn<KhuyenMaiDTO, String> col
    ) {

        return new TableCell<>() {

            private final Text text = new Text();

            {
                text.wrappingWidthProperty().bind(col.widthProperty().subtract(10));
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    text.setText(item);
                    setGraphic(text);
                }
            }
        };
    }

    @FXML
    public void onTimKiem(ActionEvent actionEvent) {
        loadData();
    }

    @SuppressWarnings("unchecked")
    private void loadData() {

        try {

            Request request =
                    new Request(CommandType.GET_ALL_KHUYEN_MAI, null);

            Response response = socketClient.send(request);

            khuyenMaiList.clear();

            if (response.isSuccess()
                    && response.getData() instanceof List<?> list) {

                List<KhuyenMaiDTO> ds =
                        (List<KhuyenMaiDTO>) list;

                LocalDate ngayBD = dpNgayBD.getValue();
                LocalDate ngayKT = dpNgayKT.getValue();

                String trangThai = cbTrangThai.getValue();

                LoaiKhuyenMai loaiKM = cbLoaiKhuyenMai.getValue();

                for (KhuyenMaiDTO km : ds) {

                    boolean hopLe = true;

                    if (ngayBD != null &&
                            km.getNgayBatDau().isBefore(ngayBD)) {
                        hopLe = false;
                    }

                    if (ngayKT != null &&
                            km.getNgayKetThuc().isAfter(ngayKT)) {
                        hopLe = false;
                    }

                    if (loaiKM != null &&
                            km.getLoaiKhuyenMai() != loaiKM) {
                        hopLe = false;
                    }

                    String tt = tinhTrangThai(
                            km.getNgayBatDau(),
                            km.getNgayKetThuc()
                    );

                    if (!"Tất cả".equals(trangThai)
                            && !tt.equals(trangThai)) {
                        hopLe = false;
                    }

                    if (hopLe) {
                        khuyenMaiList.add(km);
                    }
                }
            }

            if (khuyenMaiList.isEmpty()) {
                GiaoDienUtils.showThongBao(
                        Alert.AlertType.INFORMATION,
                        "Thông báo",
                        "Không tìm thấy khuyến mãi phù hợp!"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            GiaoDienUtils.showThongBao(
                    Alert.AlertType.ERROR,
                    "Lỗi",
                    "Không thể tải dữ liệu khuyến mãi!"
            );
        }
    }

    private String tinhTrangThai(LocalDate ngayBD, LocalDate ngayKT) {

        LocalDate now = LocalDate.now();

        if (now.isBefore(ngayBD)) {
            return "Sắp diễn ra";
        }

        if ((now.isEqual(ngayBD) || now.isAfter(ngayBD))
                && (now.isEqual(ngayKT) || now.isBefore(ngayKT))) {
            return "Đang diễn ra";
        }

        return "Đã kết thúc";
    }

    @FXML
    public void onXoaTrang(ActionEvent event) {

        dpNgayBD.setValue(null);
        dpNgayKT.setValue(null);

        cbLoaiKhuyenMai.setValue(null);

        cbTrangThai.getSelectionModel().selectFirst();

        khuyenMaiList.clear();

        loadData();
    }
}

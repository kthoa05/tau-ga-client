package controller.ql.nhanvien;

import dao.impl.PhanCongDaoImpl;
import dto.TauDTO;
import entity.CaLamViecEntity;
import entity.NhanVienEntity;
import dto.PhanCongDTO;
import entity.enums.TrangThaiLamViec;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.StringConverter;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.TauRequest;
import service.IChiTietCaLamService;
import service.impl.ChiTietCaLamViecServiceImpl;
import utils.TauGaUtils;
import utils.GiaoDienUtils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class PhanCongCaLamViec {
    @FXML
    public TextField txtMaNV;
    @FXML
    public TextField txtTenNV;
    @FXML
    public DatePicker dpNgayLam;
    @FXML
    public ComboBox<TrangThaiLamViec> cbTrangThai;
    @FXML
    public TextField txtGioBatDau;
    @FXML
    public TextField txtGioKetThuc;
    @FXML
    public TextField txtMaCa;
    @FXML
    public Button btnCapNhat;
    @FXML
    public Button btnXoaTrang;
    @FXML
    public TableView<PhanCongDTO> tblPhanCong;
    @FXML
    public TableColumn<PhanCongDTO, String> colMaNV;
    @FXML
    public TableColumn<PhanCongDTO, String> colMaCa;
    @FXML
    public TableColumn<PhanCongDTO, String> colTenNV;
    @FXML
    public TableColumn<PhanCongDTO, LocalDate> colNgayLam;
    @FXML
    public TableColumn<PhanCongDTO, LocalTime> colGioBatDau;
    @FXML
    public TableColumn<PhanCongDTO, LocalTime> colGioKetThuc;
    @FXML
    public TableColumn<PhanCongDTO, TrangThaiLamViec> colTrangThai;
    public Button btnThem;

    private ObservableList<PhanCongDTO> phanCongList;
    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        colMaNV.setCellValueFactory(new PropertyValueFactory<>("maNV"));
        colTenNV.setCellValueFactory(new PropertyValueFactory<>("tenNV"));
        colMaCa.setCellValueFactory(new PropertyValueFactory<>("maCa"));
        colNgayLam.setCellValueFactory(new PropertyValueFactory<>("ngayLam"));

        colNgayLam.setCellFactory(column -> new TableCell<>() {
            private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });        dpNgayLam.setConverter(new StringConverter<LocalDate>() {
            String pattern = "dd/MM/yyyy";
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }
            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        });
        colGioBatDau.setCellValueFactory(new PropertyValueFactory<>("gioBatDau"));
        colGioBatDau.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.format(TauGaUtils.FORMATTER_TIME));
            }
        });
        colGioKetThuc.setCellValueFactory(new PropertyValueFactory<>("gioKetThuc"));
        colGioKetThuc.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) setText(null);
                else setText(item.format(TauGaUtils.FORMATTER_TIME));
            }
        });

        colTrangThai.setCellValueFactory(new PropertyValueFactory<>("trangThai"));
        colTrangThai.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(TrangThaiLamViec item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null)
                    setText(null);
                else
                    setText(item.getLabel());
            }
        });

        ObservableList<TrangThaiLamViec> trangThaiList = FXCollections.observableArrayList(
                TrangThaiLamViec.values()
        );
        cbTrangThai.setItems(trangThaiList);
        cbTrangThai.setConverter(new StringConverter<TrangThaiLamViec>() {
            @Override
            public String toString(TrangThaiLamViec trangThai) {
                return trangThai != null ? trangThai.getLabel() : "";
            }

            @Override
            public TrangThaiLamViec fromString(String label) {
                return Arrays.stream(TrangThaiLamViec.values())
                        .filter(tt -> tt.getLabel().equals(label))
                        .findFirst()
                        .orElse(null);
            }
        });
        loadData();
        chonRow();
    }

    public void themCa(ActionEvent event) {
        try {
            if (txtGioBatDau.getText().isEmpty() || txtGioKetThuc.getText().isEmpty()) {
                GiaoDienUtils.showThongBao(
                        Alert.AlertType.WARNING,
                        "Thiếu dữ liệu",
                        "Vui lòng nhập giờ bắt đầu và giờ kết thúc"
                );
                return;
            }

            LocalTime gioBD;
            LocalTime gioKT;

            try {
                gioBD = LocalTime.parse(txtGioBatDau.getText(), TauGaUtils.FORMATTER_TIME);
                gioKT = LocalTime.parse(txtGioKetThuc.getText(), TauGaUtils.FORMATTER_TIME);
            } catch (Exception ex) {
                GiaoDienUtils.showThongBao(
                        Alert.AlertType.ERROR,
                        "Sai định dạng",
                        "Giờ phải có dạng HH:mm (vd: 08:30)"
                );
                return;
            }

            PhanCongDTO dto = new PhanCongDTO(
                    txtMaNV.getText(),
                    txtTenNV.getText(),
                    txtMaCa.getText(),
                    dpNgayLam.getValue(),
                    gioBD,
                    gioKT,
                    cbTrangThai.getValue()
            );

            for (PhanCongDTO pc : tblPhanCong.getItems()) {

                boolean trungMaCa = pc.getMaCa().equals(txtMaCa.getText());
                boolean trungMaNV = pc.getMaNV().equals(txtMaNV.getText());

                if (trungMaCa && trungMaNV) {

                    GiaoDienUtils.showThongBao(
                            Alert.AlertType.WARNING,
                            "Trùng phân công",
                            "Nhân viên đã được phân công vào ca này!"
                    );

                    return;
                }
            }

            Response res = socketClient.send(
                    new Request(CommandType.ADD_PHAN_CONG, dto)
            );

            if (res.isSuccess()) {

                GiaoDienUtils.showThongBao(
                        Alert.AlertType.INFORMATION,
                        "Thành công",
                        "Phân công thành công"
                );
                phanCongList.add(dto);
                xoaTrang(null);

            } else {

                GiaoDienUtils.showThongBao(
                        Alert.AlertType.ERROR,
                        "Lỗi",
                        res.getMessage()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {

        Task<List<PhanCongDTO>> task = new Task<>() {
            @Override
            protected List<PhanCongDTO> call() {

                Response res = socketClient.send(
                        new Request(CommandType.GET_ALL_PHAN_CONG, null)
                );

                if (res == null || !res.isSuccess() || res.getData() == null) {
                    return List.of();
                }

                try {
                    List<?> rawList = (List<?>) res.getData();
                    return rawList.stream()
                            .filter(item -> item instanceof PhanCongDTO)
                            .map(item -> (PhanCongDTO) item)
                            .toList();

                } catch (Exception e) {
                    e.printStackTrace();
                    return List.of();
                }
            }
        };

        task.setOnSucceeded(e -> {
            List<PhanCongDTO> result = task.getValue();

            if (phanCongList == null) {
                phanCongList = FXCollections.observableArrayList();
                tblPhanCong.setItems(phanCongList);
            }

            phanCongList.clear();
            phanCongList.addAll(result);
        });

        new Thread(task).start();
    }


    private void chonRow() {
        tblPhanCong.setOnMouseClicked(event -> {
            PhanCongDTO pc = tblPhanCong.getSelectionModel().getSelectedItem();
            if (pc != null) {
                txtMaNV.setText(pc.getMaNV());
                txtTenNV.setText(pc.getTenNV());
                txtMaCa.setText(pc.getMaCa());
                dpNgayLam.setValue(pc.getNgayLam());
                if (pc.getGioBatDau() != null)
                    txtGioBatDau.setText(pc.getGioBatDau().format(TauGaUtils.FORMATTER_TIME));
                if (pc.getGioKetThuc() != null)
                    txtGioKetThuc.setText(pc.getGioKetThuc().format(TauGaUtils.FORMATTER_TIME));

                cbTrangThai.setValue(pc.getTrangThai());
            }
        });
    }

    public void capNhat(ActionEvent event) {

        try {

            PhanCongDTO selected =
                    tblPhanCong.getSelectionModel().getSelectedItem();

            if (selected == null) {

                GiaoDienUtils.showThongBao(
                        Alert.AlertType.WARNING,
                        "Thông báo",
                        "Vui lòng chọn phân công cần cập nhật"
                );

                return;
            }

            PhanCongDTO dto = new PhanCongDTO(
                    txtMaNV.getText(),
                    txtTenNV.getText(),
                    selected.getMaCa(),
                    dpNgayLam.getValue(),
                    LocalTime.parse(
                            txtGioBatDau.getText(),
                            TauGaUtils.FORMATTER_TIME
                    ),
                    LocalTime.parse(
                            txtGioKetThuc.getText(),
                            TauGaUtils.FORMATTER_TIME
                    ),
                    cbTrangThai.getValue()
            );

            Response res = socketClient.send(
                    new Request(CommandType.UPDATE_PHAN_CONG, dto)
            );

            if (res != null && res.isSuccess()) {

                GiaoDienUtils.showThongBao(
                        Alert.AlertType.INFORMATION,
                        "Thành công",
                        "Cập nhật phân công thành công"
                );

                int index = phanCongList.indexOf(selected);
                phanCongList.set(index, dto);
                tblPhanCong.getSelectionModel().clearSelection();

                xoaTrang(null);

            } else {

                GiaoDienUtils.showThongBao(
                        Alert.AlertType.ERROR,
                        "Lỗi",
                        res != null ? res.getMessage()
                                : "Không thể cập nhật"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            GiaoDienUtils.showThongBao(
                    Alert.AlertType.ERROR,
                    "Lỗi",
                    "Dữ liệu không hợp lệ"
            );
        }
    }

    public void xoaTrang(ActionEvent actionEvent) {
        txtMaNV.clear();
        txtTenNV.clear();
        txtMaCa.clear();
        txtGioBatDau.clear();
        txtGioKetThuc.clear();
        dpNgayLam.setValue(null);
        cbTrangThai.setValue(null);
    }

}

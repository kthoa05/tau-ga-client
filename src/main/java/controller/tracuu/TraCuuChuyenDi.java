package controller.tracuu;

import dto.TaiKhoanDTO;
import javafx.fxml.FXML;
import dao.impl.ChuyenTauDaoImpl;
import dto.ThongTinChuyenTauDTO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.ChuyenDiRequest;
import network.common.request.TimKiemChuyenTauRequest;
import service.IChuyenTauService;
import service.impl.ChuyenTauServiceImpl;
import utils.GiaoDienUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class TraCuuChuyenDi {
    @FXML private ComboBox<String> cbGaDi;
    @FXML private ComboBox<String> cbGaDen;
    @FXML private DatePicker dpNgayDi;
    @FXML private Button btnTimKiem;
    @FXML private TableView<ThongTinChuyenTauDTO> tableChuyenDi;
    @FXML private TableColumn<ThongTinChuyenTauDTO, Integer> colSTT;
    @FXML private TableColumn<ThongTinChuyenTauDTO, String> colMaChuyen;
    @FXML private TableColumn<ThongTinChuyenTauDTO, String> colMaTau;
    @FXML private TableColumn<ThongTinChuyenTauDTO, String> colGaDi;
    @FXML private TableColumn<ThongTinChuyenTauDTO, String> colGaDen;
    @FXML private TableColumn<ThongTinChuyenTauDTO, String> colThoiGianDi;
    @FXML private TableColumn<ThongTinChuyenTauDTO, String> colThoiGianDen;
    @FXML TableColumn<ThongTinChuyenTauDTO, Number> colCuLy;

    private final IChuyenTauService chuyenTauService = new ChuyenTauServiceImpl();
    private final ObservableList<ThongTinChuyenTauDTO> chuyenDiList = FXCollections.observableArrayList();
    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        try {
//            List<String> dsGaDi = chuyenTauService.layTatCaGa(true);
//            List<String> dsGaDen = chuyenTauService.layTatCaGa(false);
            List<String> dsGaDi = layTatCaGa(true);
            List<String> dsGaDen = layTatCaGa(false);

            cbGaDi.setItems(FXCollections.observableArrayList(dsGaDi));
            cbGaDen.setItems(FXCollections.observableArrayList(dsGaDen));
            cbGaDi.getItems().add(0, "Tất cả");
            cbGaDen.getItems().add(0, "Tất cả");
            cbGaDi.getSelectionModel().selectFirst();
            cbGaDen.getSelectionModel().selectFirst();

            tableChuyenDi.setItems(chuyenDiList);

            colSTT.setCellFactory(col -> new TableCell<>() {
                @Override
                protected void updateItem(Integer item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                        setText(null);
                    } else {
                        setText(String.valueOf(getIndex() + 1));
                    }
                }
            });

            colMaChuyen.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getMaChuyenDi()));
            colMaTau.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getMaTau()));
            colGaDi.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getGaDi()));
            colGaDen.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getGaDen()));

            DateTimeFormatter outFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
            DateTimeFormatter daoSrcFmt = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            DateTimeFormatter ngayDiFmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");

            colThoiGianDi.setCellValueFactory(cellData -> {
                ThongTinChuyenTauDTO d = cellData.getValue();
                if (d == null) return new SimpleStringProperty("");
                String s = "";
                if (d.getThoiGianKhoiHanh() != null) {
                    s = d.getThoiGianKhoiHanh().format(outFmt);
                } else if (d.getThoiGianKhoiHanhStr() != null && !d.getThoiGianKhoiHanhStr().isBlank()) {
                    s = d.getThoiGianKhoiHanhStr();
                } else if (d.getGioDiStr() != null && !d.getGioDiStr().isBlank()) {
                    try {
                        LocalDateTime parsed = LocalDateTime.parse(d.getGioDiStr(), daoSrcFmt);
                        s = parsed.format(outFmt);
                    } catch (Exception ex) {
                        s = d.getGioDiStr();
                    }
                } else if (d.getGioDi() != null) {
                    LocalDate date = d.getNgayKhoiHanh();
                    if (date == null && d.getNgayDi() != null && !d.getNgayDi().isBlank()) {
                        try {
                            date = LocalDate.parse(d.getNgayDi(), ngayDiFmt);
                        } catch (Exception ex) {}
                    }
                    if (date != null) {
                        LocalDateTime dt = LocalDateTime.of(date, d.getGioDi());
                        s = dt.format(outFmt);
                    } else {
                        s = d.getGioDi().toString();
                    }
                }
                return new SimpleStringProperty(s == null ? "" : s);
            });

            colThoiGianDen.setCellValueFactory(cellData -> {
                ThongTinChuyenTauDTO d = cellData.getValue();
                if (d == null) return new SimpleStringProperty("");
                String s = "";
                if (d.getThoiGianDuTinh() != null) {
                    s = d.getThoiGianDuTinh().format(outFmt);
                } else if (d.getThoiGianDuTinhStr() != null && !d.getThoiGianDuTinhStr().isBlank()) {
                    s = d.getThoiGianDuTinhStr();
                } else if (d.getGioDenStr() != null && !d.getGioDenStr().isBlank()) {
                    try {
                        LocalDateTime parsed = LocalDateTime.parse(d.getGioDenStr(), daoSrcFmt);
                        s = parsed.format(outFmt);
                    } catch (Exception ex) {
                        s = d.getGioDenStr();
                    }
                } else if (d.getGioDen() != null) {
                    LocalDate date = d.getNgayKhoiHanh();
                    if (date == null && d.getNgayDi() != null && !d.getNgayDi().isBlank()) {
                        try {
                            date = LocalDate.parse(d.getNgayDi(), ngayDiFmt);
                        } catch (Exception ex) {}
                    }
                    if (date != null) {
                        LocalDateTime dt = LocalDateTime.of(date, d.getGioDen());
                        s = dt.format(outFmt);
                    } else {
                        s = d.getGioDen().toString();
                    }
                }
                return new SimpleStringProperty(s == null ? "" : s);
            });

            colCuLy.setCellValueFactory(cellData ->
                    new SimpleDoubleProperty(cellData.getValue().getCuly()));

        } catch (Exception e) {
            e.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách ga: " + e.getMessage());
        }
    }

    public List<String> layTatCaGa(boolean layGa) {
        ChuyenDiRequest chuyenDiRequest = new ChuyenDiRequest(layGa);
        Request request = new Request(CommandType.SEARCH_ALL_GA, chuyenDiRequest);
        Response response = socketClient.send(request);
        if(!response.isSuccess() || !(response.getData() instanceof List<?> list)) {
            return null;
        }
        return (List<String>) list;
    }

    @FXML
    private void onTimKiem(ActionEvent event) {
        String gaDiRaw = cbGaDi.getValue();
        String gaDenRaw = cbGaDen.getValue();
        LocalDate ngayDi = dpNgayDi.getValue();

        String gaDi = (gaDiRaw == null || gaDiRaw.equals("Tất cả")) ? null : gaDiRaw;
        String gaDen = (gaDenRaw == null || gaDenRaw.equals("Tất cả")) ? null : gaDenRaw;

        try {
//            List<ThongTinChuyenTauDTO> danhSachChuyenDi =
//                    chuyenTauService.timKiem(gaDi, gaDen, ngayDi);
            List<ThongTinChuyenTauDTO> danhSachChuyenDi = timKiemChuyenDi(gaDi, gaDen, ngayDi);
            chuyenDiList.clear();
            if (danhSachChuyenDi == null || danhSachChuyenDi.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thông báo", "Không tìm thấy chuyến đi phù hợp.");
            } else {
                chuyenDiList.addAll(danhSachChuyenDi);
            }
            tableChuyenDi.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu chuyến đi: " + e.getMessage());
        }
    }

    private List<ThongTinChuyenTauDTO> timKiemChuyenDi(String gaDi, String gaDen, LocalDate ngayDi) {
        TimKiemChuyenTauRequest payload = new TimKiemChuyenTauRequest(gaDi, gaDen, false, ngayDi, null);
        Request request = new Request(CommandType.SEARCH_CHUYEN_DI, payload);
        Response response = socketClient.send(request);
        if (!response.isSuccess() || !(response.getData() instanceof List<?> list)) {
            return null;
        }
        return (List<ThongTinChuyenTauDTO>) list;
    }
    @FXML
    public void onXoaTrang(ActionEvent event) {
        cbGaDi.getSelectionModel().selectFirst();
        cbGaDen.getSelectionModel().selectFirst();
        dpNgayDi.setValue(null);
        chuyenDiList.clear();
        tableChuyenDi.refresh();
    }
}

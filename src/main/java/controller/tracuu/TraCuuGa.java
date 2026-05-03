package controller.tracuu;

import dao.impl.GaDaoImpl;
import dto.GaDTO;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.control.Label;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.TimKiemGaRequest;
import service.IGaService;
import service.impl.GaServiceImpl;
import utils.GiaoDienUtils;

import java.util.List;

public class TraCuuGa {
    @FXML
    private ComboBox<String> cbTenGa;
    @FXML
    private Button btnTimKiem;
    @FXML
    private TableView<GaDTO> tableGa;
    @FXML
    private TableColumn<GaDTO, String> colMaGa;
    @FXML
    private TableColumn<GaDTO, String> colTenGa;
    @FXML
    private TableColumn<GaDTO, String> colDiaChi;
    @FXML
    private TableColumn<GaDTO, Number> colSoChuyen;
    @FXML
    private TextField txtDiaChi;
    private IGaService gaService = new GaServiceImpl();
    private ObservableList<GaDTO> gaList = FXCollections.observableArrayList();
    private final SocketClient socketClient = new SocketClient();

    @FXML
    public void initialize() {
        try {
//            List<String> danhSachTenGa = gaService.getAllTenGa();
            List<String> danhSachTenGa = getAllTenGa();

            cbTenGa.setItems(FXCollections.observableArrayList(danhSachTenGa));
            cbTenGa.getItems().add(0, "Tất cả");
            cbTenGa.getSelectionModel().selectFirst();

            colMaGa.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getMaGa()));
            colMaGa.setCellFactory(col -> new TableCell<GaDTO, String>() {
                private final Label lbl = new Label();
                {
                    lbl.setStyle("-fx-font-size: 11px; -fx-padding: 0 4 0 4;");
                    lbl.setWrapText(false);
                }
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        lbl.setText(item);
                        setGraphic(lbl);
                    }
                }
            });

            colTenGa.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getTenGa()));
            colTenGa.setCellFactory(col -> new TableCell<GaDTO, String>() {
                private final Label lbl = new Label();
                {
                    lbl.setStyle("-fx-font-size: 12px; -fx-padding: 0 4 0 4;");
                    lbl.setWrapText(false);
                }
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setGraphic(null);
                    } else {
                        lbl.setText(item);
                        setGraphic(lbl);
                    }
                }
            });

            colDiaChi.setCellValueFactory(cellData ->
                    new SimpleStringProperty(cellData.getValue().getDiaChi()));
            colDiaChi.setCellFactory(col -> {
                TableCell<GaDTO, String> cell = new TableCell<>() {
                    private final Text text = new Text();
                    {
                        text.wrappingWidthProperty().bind(col.widthProperty().subtract(10));
                        text.getStyleClass().add("table-text");
                    }
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty || item == null) {
                            setGraphic(null);
                        } else {
                            text.setText(item);
                            setGraphic(text);
                            // allow computed height
                            setPrefHeight(Control.USE_COMPUTED_SIZE);
                        }
                    }
                };
                return cell;
            });

            colSoChuyen.setCellValueFactory(cellData ->
                    new SimpleIntegerProperty(cellData.getValue().getSoChuyen()));

            tableGa.setItems(gaList);
            tableGa.setFixedCellSize(-1);

        } catch (Exception e) {
            e.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", "Không thể tải danh sách ga: " + e.getMessage());
        }
    }

    public List<String> getAllTenGa(){
        Request request = new Request(CommandType.GET_ALL_TEN_GA, null);
        Response response = socketClient.send(request);
        if(!response.isSuccess() || !(response.getData() instanceof List<?> list)){
            return null;
        }
        return (List<String>) list;
    }

    @FXML
    private void onTimKiem(ActionEvent event) {
        gaList.clear();
        String tenGa = cbTenGa.getValue();
        String diaChi = txtDiaChi.getText();

//        List<GaDTO> results = gaService.timKiem(tenGa, diaChi);
        List<GaDTO> results = timKiemGa(tenGa, diaChi);
        if (results == null || results.isEmpty()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thông báo", "Không tìm thấy ga phù hợp.");
        } else {
            gaList.addAll(results);
        }
        tableGa.refresh();
    }

    public List<GaDTO> timKiemGa(String tenGa, String diaChi){
        TimKiemGaRequest payload = new TimKiemGaRequest(tenGa, diaChi);
        Request request = new Request(CommandType.SEARCH_GA, payload);
        Response response = socketClient.send(request);
        if (!response.isSuccess() || !(response.getData() instanceof List<?> list)) {
            return null;
        }
        return (List<GaDTO>) list;
    }

    private void loadData() {
        gaList.clear();
        String tenGa = cbTenGa.getValue();

        try {
            List<GaDTO> results = gaService.timKiemTheoTen(tenGa);            if (results == null || results.isEmpty()) {
                GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thông báo", "Không tìm thấy ga phù hợp.");
            }
            gaList.addAll(results);
            tableGa.refresh();
        } catch (Exception e) {
            e.printStackTrace();
            GiaoDienUtils.showThongBao(Alert.AlertType.ERROR, "Lỗi", "Không thể tải dữ liệu ga: " + e.getMessage());
        }
    }

    @FXML
    public void onXoaTrang(ActionEvent event) {
        cbTenGa.getSelectionModel().selectFirst();
        txtDiaChi.clear();
        gaList.clear();
        tableGa.refresh();
    }
}

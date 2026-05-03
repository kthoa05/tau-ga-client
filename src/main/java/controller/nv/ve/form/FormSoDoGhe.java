package controller.nv.ve.form;

import dto.GheDTO;
import dto.ThongTinChuyenTauDTO;
import entity.enums.TrangThaiGhe;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.SoDoGheRequest;
import utils.GiaoDienUtils;
import utils.consts.CurrentUser;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FormSoDoGhe {
    @FXML
    private VBox soDoGhe;

    private final SocketClient socketClient = new SocketClient();

    private Consumer<List<GheDTO>> onChonGheListener;
    private final List<GheDTO> danhSachGheDangChon = new ArrayList<>();

    private final Map<GheDTO, String> mauGocGhe = new HashMap<>();
    private final Map<GheDTO, Button> nutGheMap = new HashMap<>();

    private String maTauDangChon;

    private String loaiGheFilter = null; //Lọc ghế
    private ThongTinChuyenTauDTO chuyenTauDangChon;


    // callback setter
    public void setOnChonGheListener(Consumer<List<GheDTO>> listener) {
        this.onChonGheListener = listener;
    }

    /**
     * Hiển thị sơ đồ ghế theo mã tàu
     */
    public void hienThiSoDoGhe(ThongTinChuyenTauDTO ct, String loaiGheFilter) {
        soDoGhe.getChildren().clear();
//        this.maTauDangChon = ct.getMaTau();
        this.chuyenTauDangChon = ct;
//        danhSachGheDangChon.clear();
        mauGocGhe.clear();
        nutGheMap.clear();

        List<GheDTO> danhSachGhe = laySoDoGhe(ct.getMaChuyenTau());
        System.out.println("Số ghế lấy được: " + danhSachGhe.size());


        if (danhSachGhe.isEmpty()) {
            Label lbl = new Label("Không có dữ liệu ghế cho tàu " + ct.getMaTau());
            soDoGhe.getChildren().add(lbl);
            return;
        }

        Map<String, Map<String, List<GheDTO>>> soDo = danhSachGhe.stream()
                .collect(Collectors.groupingBy(GheDTO::getMaToa, LinkedHashMap::new,
                        Collectors.groupingBy(GheDTO::getMaKhoang, LinkedHashMap::new, Collectors.toList())));

        int sttToa = 1;
        for (String maToa : soDo.keySet()) {
            Map<String, List<GheDTO>> khoangs = soDo.get(maToa);
            String loaiGhe = khoangs.values().stream()
                    .flatMap(List::stream)
                    .findFirst()
                    .map(GheDTO::getTenLoaiGhe)
                    .orElse("Không xác định");

            Label lblToa = new Label("Toa " + sttToa + " - " + loaiGhe);
            lblToa.setStyle("-fx-font-weight: bold; -fx-font-size: 16; -fx-padding: 10 0 10 0;");
            soDoGhe.getChildren().add(lblToa);

            HBox hbKhoangs = new HBox(30);
            hbKhoangs.setAlignment(Pos.CENTER);
            hbKhoangs.setFillHeight(true);
            hbKhoangs.setStyle("-fx-padding: 10; -fx-border-color: #52CCF6;");

            boolean toaCoGhePhuHop = false;

            int sttKhoang = 1;
            for (String maKhoang : khoangs.keySet()) {
                List<GheDTO> gheTrongKhoang = khoangs.get(maKhoang);
                gheTrongKhoang.sort(Comparator.comparingInt(GheDTO::getViTriGhe));

                VBox vbKhoang = new VBox(5);
                Label lblKhoang = new Label("Khoang " + sttKhoang);
                lblKhoang.setStyle("-fx-font-weight: bold; -fx-font-size: 13;");
                vbKhoang.getChildren().add(lblKhoang);

                HBox.setHgrow(vbKhoang, Priority.ALWAYS);
                vbKhoang.setMaxWidth(Double.MAX_VALUE);


                GridPane grid = new GridPane();
                grid.setHgap(15);
                grid.setVgap(15);

                boolean khoangCoGhePhuHop = false;

                int row = 0, col = 0;
                for (GheDTO ghe : gheTrongKhoang) {
                    Button btnGhe = new Button(String.valueOf(ghe.getViTriGhe()));
                    btnGhe.setPrefSize(40, 30);

                    btnGhe.setOnAction(e -> {
                        int viTri = Integer.parseInt(btnGhe.getText());
                        CurrentUser.setViTriGheDangChon(String.valueOf(viTri));
                    });

                    boolean hienGhe = (loaiGheFilter == null || ghe.getTenLoaiGhe().equals(loaiGheFilter));

                    //Cập nhật trạng thái phù hợp
                    if (hienGhe) {
                        khoangCoGhePhuHop = true;
                        toaCoGhePhuHop = true;
                    }
                    btnGhe.setVisible(hienGhe);
                    btnGhe.setManaged(hienGhe);

                    String mauBanDau = switch (ghe.getTenLoaiGhe()) {
                        case "Giường nằm khoang 6 điều hòa" -> "-fx-background-color: #FF7E29;";
                        case "Giường nằm khoang 4 điều hòa" -> "-fx-background-color: #FFDD70;";
                        case "Giường nằm khoang 2 điều hòa VIP" -> "-fx-background-color: #F52827;";
                        case "Ghế ngồi mềm" -> "-fx-background-color: #A7DAB1;";
                        case "Ghế ngồi cứng" -> "-fx-background-color: #CAAE88;";

                        default -> "-fx-background-color: #D5DBDB;";
                    };

                    if (ghe.getTrangThaiGhe() == TrangThaiGhe.DA_DAT.getI()) {
                        btnGhe.setDisable(true);
                        btnGhe.setStyle("-fx-background-color: #E0E0E0;");
                        Tooltip.install(btnGhe, new Tooltip("Ghế đã được đặt"));
                    } else {
                        btnGhe.setStyle(mauBanDau);
                        mauGocGhe.put(ghe, mauBanDau);
                        nutGheMap.put(ghe, btnGhe);

                        btnGhe.setOnAction(e -> xuLyChonGhe(ghe, btnGhe));
                    }

                    grid.add(btnGhe, col, row);
                    col++;
                    if (col == 2) { col = 0; row++; }
                }

                //Ẩn khoang nếu không có ghế phù hợp
                vbKhoang.setVisible(khoangCoGhePhuHop);
                vbKhoang.setManaged(khoangCoGhePhuHop);


                vbKhoang.getChildren().add(grid);
                hbKhoangs.getChildren().add(vbKhoang);
                sttKhoang++;
            }

            //Ẩn toa nếu không có ghế phù hợp
            hbKhoangs.setVisible(toaCoGhePhuHop);
            hbKhoangs.setManaged(toaCoGhePhuHop);
            lblToa.setVisible(toaCoGhePhuHop);
            lblToa.setManaged(toaCoGhePhuHop);

            soDoGhe.getChildren().add(hbKhoangs);
            sttToa++;
        }
    }

    /**
     * Xử lý chọn hoặc bỏ chọn ghế
     */
    private void xuLyChonGhe(GheDTO ghe, Button btnGhe) {

        if (danhSachGheDangChon.contains(ghe)) {
            danhSachGheDangChon.remove(ghe);
            btnGhe.setStyle(mauGocGhe.get(ghe));
        } else {
            danhSachGheDangChon.add(ghe);
            btnGhe.setStyle("-fx-background-color: #559DEA;"); // xanh khi chọn
        }

        //callback: gửi danh sách ghế đã chọn về DatVeController
        if (onChonGheListener != null) {
            onChonGheListener.accept(Collections.unmodifiableList(danhSachGheDangChon));
            if (danhSachGheDangChon.size() > 0) {
                CurrentUser.setTongSoVeDaDat(danhSachGheDangChon.size());
            }
        }
    }


    @FXML
    public void boChonTatCa(ActionEvent actionEvent) {
        if (danhSachGheDangChon.isEmpty()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thông báo", "Chưa có ghế nào được chọn!");
            return;
        }

        for (GheDTO ghe : danhSachGheDangChon) {
            Button btnGhe = nutGheMap.get(ghe);
            String mauGoc = mauGocGhe.get(ghe);
            if (btnGhe != null && mauGoc != null) {
                btnGhe.setStyle(mauGoc);
            }
        }

        danhSachGheDangChon.clear();

        if (onChonGheListener != null) {
            onChonGheListener.accept(Collections.emptyList()); // gửi danh sách rỗng
        }
    }

    private void locTheoLoaiGhe(String loai) {
        this.loaiGheFilter = loai;
        hienThiSoDoGhe(chuyenTauDangChon, loai);
    }

    @FXML
    public void locGheNgoiCung() {
        locTheoLoaiGhe("Ghế ngồi cứng");
    }

    @FXML
    public void locGheNgoiMem() {
        locTheoLoaiGhe("Ghế ngồi mềm");
    }

    @FXML
    public void locKhoang6() {
        locTheoLoaiGhe("Giường nằm khoang 6 điều hòa");
    }

    @FXML
    public void locKhoang4() {
        locTheoLoaiGhe("Giường nằm khoang 4 điều hòa");
    }

    @FXML
    public void locKhoang2VIP() {
        locTheoLoaiGhe("Giường nằm khoang 2 điều hòa VIP");
    }


    @FXML
    public void hienTatCaGhe() {
        if (chuyenTauDangChon == null) return;
        hienThiSoDoGhe(chuyenTauDangChon, null);
    }

    @SuppressWarnings("unchecked")
    private List<GheDTO> laySoDoGhe(String maChuyenTau) {
        SoDoGheRequest payload = new SoDoGheRequest(maChuyenTau);
        Request request = new Request(CommandType.GET_SO_DO_GHE, payload);
        Response response = socketClient.send(request);
        if (response == null || !response.isSuccess() || response.getData() == null) {
            return Collections.emptyList();
        }
        return (List<GheDTO>) response.getData();
    }


}

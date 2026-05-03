package controller.nv.ve.form;

import controller.nv.ve.HoaDonVeTau;
import dto.HoaDonVeDTO;
import dto.ThongTinChuyenTauDTO;
import dto.ThongTinDatVeDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.client.SocketClient;
import network.common.CommandType;
import network.common.Request;
import network.common.Response;
import network.common.request.TrackingDatVeRequest;
import utils.GiaoDienUtils;
import utils.TauGaUtils;
import utils.consts.CurrentUser;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;


public class FormHinhThucThanhToan {
    //rad
    @FXML
    private RadioButton radTienMat, radChuyenKhoan;

    @FXML
    private StackPane pnThongTinThanhToan;
    @FXML
    private BorderPane pnHinhThucThanhToan;

    private final SocketClient socketClient = new SocketClient();

    //tong tien de hien thi thanh toan
    private double tongTien = 0;
    private String maTauDangChon = null;


    //thong tin de in ve
    private List<ThongTinDatVeDTO> danhSachVe = new ArrayList<>();

    private BiConsumer<String, Double> onThanhToanXong;
    private Consumer<Boolean> ketThucListener;
    private Consumer<HoaDonVeDTO> onInVeXong;

    public void setOnInVeXong(Consumer<HoaDonVeDTO> callback) {
        this.onInVeXong = callback;
    }

    public void setOnThanhToanXong(BiConsumer<String, Double> callback) {
        this.onThanhToanXong = callback;
    }

    public void setKetThucListener(Consumer<Boolean> ketThucListener) {
        this.ketThucListener = ketThucListener;
    }

    public void hienThiMacDinh() {
        if (pnHinhThucThanhToan != null) {
            pnHinhThucThanhToan.setManaged(true);
            pnHinhThucThanhToan.setVisible(true);
        }

        ToggleGroup groupThanhToan = new ToggleGroup();
        radTienMat.setToggleGroup(groupThanhToan);
        radChuyenKhoan.setToggleGroup(groupThanhToan);
        radTienMat.setSelected(true);

        // set mac dinh hinh thuc tien mat
        radTienMat.setSelected(true);
        pnThongTinThanhToan.getChildren().clear();
    }

    public void onXacNhanPhuongThucThanhToan(ActionEvent actionEvent) {
        pnThongTinThanhToan.getChildren().clear();
        if (radTienMat.isSelected()) {
            hienThiThanhToanTienMat();
        } else if (radChuyenKhoan.isSelected()) {
            hienThiThanhToanChuyenKhoan();
        } else {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING,
                    "Chưa chọn hình thức", "Vui lòng chọn hình thức thanh toán");
            return;
        }
        trackingNhanVienDatVe();
//        if (radTienMat.isSelected()) {
//            hienThiThanhToanTienMat();
//        } else if (radChuyenKhoan.isSelected()) {
//            hienThiThanhToanChuyenKhoan();
//        } else {
//            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Chưa chọn hình thức", "Vui lòng chọn hình thức thanh toán");
//        }
    }

    public void trackingNhanVienDatVe() {
        TrackingDatVeRequest payload = new TrackingDatVeRequest(CurrentUser.getNhanVien().getMaNV(), danhSachVe);
        Request request = new Request(CommandType.TRACK_DAT_VE, payload);
        Response response = socketClient.send(request);
        if (response == null || !response.isSuccess()) {
            String message = response != null && response.getMessage() != null && !response.getMessage().isBlank()
                    ? response.getMessage()
                    : "Không thể cập nhật theo dõi đặt vé";
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "Cảnh báo", message);
        }
    }

    private void hienThiThanhToanTienMat() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER_LEFT);
        box.setPadding(new Insets(15));

        this.tongTien = CurrentUser.getTongTienSauCung();
        Label lblTongTien = new Label("Tổng tiền phải trả: " + TauGaUtils.NumberUtils.formatNumber(tongTien));
        lblTongTien.setStyle("""
                    -fx-font-size: 15px;
                    -fx-font-weight: bold;
                    -fx-text-fill: #1b5e20;
                """);


        TextField txtKhachDua = new TextField();
        txtKhachDua.setPromptText("Nhập số tiền khách đưa");
        Label lblTienThoi = new Label("Tiền thối lại: 0 VNĐ");

        // === GỢI Ý SỐ TIỀN KHÁCH ĐƯA ===
        FlowPane boxGoiY = new FlowPane();
        boxGoiY.setHgap(10);
        boxGoiY.setVgap(10);
        boxGoiY.setPadding(new Insets(5));
        boxGoiY.setPrefWrapLength(300); // Tự xuống dòng nếu chiều rộng ko đủ

        double[] goiY = tinhGoiYSoTien(tongTien);

        for (double g : goiY) {
            Button btn = new Button(String.format("%,.0f", g));
            btn.setPrefWidth(100);
            btn.setPrefHeight(40);
            btn.setStyle("""
                        -fx-font-size: 14px;
                        -fx-padding: 8 16 8 16;
                        -fx-background-color: #f4f4f4;
                        -fx-border-color: #ccc;
                        -fx-border-radius: 8;
                        -fx-background-radius: 8;
                    """);

            // Hiệu ứng hover cho nút
            btn.setOnMouseEntered(e -> btn.setStyle("""
                        -fx-font-size: 14px;
                        -fx-padding: 8 16 8 16;
                        -fx-background-color: #e0f3ff;
                        -fx-border-color: #66afe9;
                        -fx-border-radius: 8;
                        -fx-background-radius: 8;
                    """));
            btn.setOnMouseExited(e -> btn.setStyle("""
                        -fx-font-size: 14px;
                        -fx-padding: 8 16 8 16;
                        -fx-background-color: #f4f4f4;
                        -fx-border-color: #ccc;
                        -fx-border-radius: 8;
                        -fx-background-radius: 8;
                    """));

            btn.setOnAction(e -> txtKhachDua.setText(TauGaUtils.NumberUtils.formatNumber(g)));
            boxGoiY.getChildren().add(btn);
        }

        // === Lắng nghe nhập tiền để tính tiền thối ===
//        txtKhachDua.textProperty().addListener((obs, old, val) -> {
//            try {
//                String clean = val.replaceAll("[^\\d.]", "");
//                if (clean.isEmpty()) {
//
//                    lblTienThoi.setText("Tiền thối lại: 0 VNĐ");
//                    return;
//                }
//                double dua = Double.parseDouble(clean);
//                double thoi = dua - tongTien;
//                lblTienThoi.setText("Tiền thối lại: " + TauGaUtils.NumberUtils.formatNumber(Math.max(thoi, 0)));
//            } catch (NumberFormatException ex) {
//                lblTienThoi.setText("Tiền thối lại: 0 VNĐ");
//            }
//        });
        txtKhachDua.textProperty().addListener((obs, old, val) -> {
            double thoi = 0;
            try {
                String clean = val.replaceAll("[^\\d.]", "");
                if (!clean.isEmpty()) {
                    double dua = Double.parseDouble(clean);
                    thoi = dua - tongTien;
                }
            } catch (NumberFormatException ex) {
                thoi = 0;
            }
            lblTienThoi.setText("Tiền thối lại: " + TauGaUtils.NumberUtils.formatNumber(Math.max(thoi, 0)) + " VNĐ");
        });

        box.getChildren().addAll(lblTongTien, new Label("Số tiền khách đưa:"), txtKhachDua, boxGoiY, lblTienThoi);
        pnThongTinThanhToan.getChildren().add(box);
    }

    /**
     * Tính gợi ý theo đúng yêu cầu:
     * - Nếu tròn 0k (bội 10k): nhảy +10k (30k,40k,...)
     * - Nếu tròn 5k  (ví dụ 35k): nhảy +5k (40k,50k,...)
     * - Nếu <5k (ví dụ 21-24k): +1k để làm tròn lên (22k), sau đó mốc 10k tiếp theo
     * - Nếu >5k và không phải 5k (ví dụ 26-29k): làm tròn lên chục (30k)...
     */
    private double[] tinhGoiYSoTien(double tongTien) {

        long t = (long) Math.ceil(tongTien / 1000.0) * 1000;

        long[] buTien = {5000, 10000, 50000, 100000, 500000};

        List<Long> goiY = new ArrayList<>();
        goiY.add(t); // đúng tiền

        for (long bu : buTien) {
            long goiYMoi = ((t + bu - 1) / bu) * bu;
            if (goiYMoi > t && !goiY.contains(goiYMoi)) {
                goiY.add(goiYMoi);
            }
            if (goiY.size() == 4) break;
        }

        return goiY.stream().mapToDouble(Long::doubleValue).toArray();
    }



    private void hienThiThanhToanChuyenKhoan() {
        VBox box = new VBox(10);
        box.setAlignment(Pos.CENTER);
        box.setPadding(new Insets(15));

        Label lblThongTin = new Label("""
                Ngân hàng: MBBank
                Số tài khoản: 4134562628
                Chủ TK: LE THI KIEU THOA
                """);
        lblThongTin.setStyle("-fx-font-size: 13; -fx-text-alignment: center;");

        Label lblTien = new Label("Số tiền cần chuyển: " + TauGaUtils.NumberUtils.formatNumber(tongTien));
        lblTien.setStyle("-fx-font-weight: bold;");

        ImageView qrView = new ImageView(new Image(getClass().getResource("/tauga/img/QR.jpg").toExternalForm()));
        qrView.setFitHeight(200);
        qrView.setPreserveRatio(true);

        box.getChildren().addAll(lblThongTin, qrView, lblTien);
        pnThongTinThanhToan.getChildren().add(box);
    }

    public void setThongTinToInVe(List<ThongTinDatVeDTO> danhSachVe) {
        this.danhSachVe = danhSachVe;
    }

    public void onInVe(ActionEvent actionEvent) {
        try {

//

            ThongTinChuyenTauDTO chuyenTau = CurrentUser.getThongTinChuyenTauDTO();

            List<HoaDonVeDTO> danhSachHoaDon = new ArrayList<>();

            danhSachVe.forEach(rs -> {
                HoaDonVeDTO ve = new HoaDonVeDTO();

                ve.setMaVe(rs.getMaVe());
                ve.setTenHanhKhach(rs.getTenHanhKhach());
                ve.setTenTau(chuyenTau.getTenTau());
                ve.setGaDi(chuyenTau.getGaDi());
                ve.setGaDen(chuyenTau.getGaDen());
                ve.setNgayGioKhoiHanh(
                        TauGaUtils.formatNgayVaGio(chuyenTau.getNgayGioDi())
                );

                ve.setMaToaTau(rs.getTenToa());
                ve.setTenKhoangTau(rs.getTenKhoangTau());
                ve.setSoGhe(rs.getSoGhe());
                ve.setTenLoaiGhe(rs.getTenLoaiGhe());
                ve.setTenLoaiVe(rs.getTenLoaiVe());

                ve.setGiaVe(CurrentUser.getThanhTien());
                ve.setMaKhuyenMai(
                        CurrentUser.getMaKhuyenMai() == null
                                ? "Không"
                                : CurrentUser.getMaKhuyenMai()
                );
                ve.setSoTienGiamGia(CurrentUser.getSoTienGiamGia());
                ve.setGiaVe(rs.getGiaVe());
                ve.setTongTien(CurrentUser.getTongTienSauCung());

                ve.setPhuongThucThanhToan(
                        radTienMat.isSelected() ? "Tiền mặt" : "Chuyển khoản"
                );

                danhSachHoaDon.add(ve);
            });


            moCuaSoHoaDonVeNhieuVe(danhSachHoaDon);

        } catch (Exception e) {
            e.printStackTrace();
            GiaoDienUtils.showThongBao(
                    Alert.AlertType.ERROR,
                    "Lỗi",
                    "Không thể in vé"
            );
        }
    }


    private void moCuaSoHoaDonVeNhieuVe(List<HoaDonVeDTO> danhSachHoaDon) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/tauga/view/nv/ve/HoaDonVeTau.fxml")
            );
            Parent root = loader.load();

            HoaDonVeTau controller = loader.getController();

            controller.setHoanVeData(danhSachHoaDon);

            Stage stage = new Stage();
            stage.setTitle("In vé tàu (" + danhSachHoaDon.size() + " vé)");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void ketThuc(ActionEvent actionEvent) {
        if (ketThucListener != null) {
            ketThucListener.accept(Boolean.TRUE);
        }
    }
}

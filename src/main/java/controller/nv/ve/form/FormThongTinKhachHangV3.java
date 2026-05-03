package controller.nv.ve.form;//package controller.nv.ve.form;
//
//import dto.ThongTinDatVeDTO;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.geometry.HPos;
//import javafx.geometry.Insets;
//import javafx.scene.control.*;
//import javafx.scene.layout.ColumnConstraints;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.VBox;
//import service.IKhachHangService;
//import service.impl.KhachHangServiceImpl;
//import java.util.List;
//import java.util.function.Consumer;
//
//public class FormThongTinKhachHangV3 {
//    @FXML
//    public VBox pnDanhSachVe;
//    @FXML
//    public ScrollPane pnScrollPaneDanhSachVe;
//    public VBox pnThongTinVeTau;
//    public Button btnDatVe;
//    @FXML
//    private TextField txtHoTenKH;
//    @FXML
//    private TextField txtSDTKH;
//    @FXML
//    private TextField txtCCCDKH;
//    @FXML
//    private DatePicker dpNgaySinhKH;
//    @FXML
//    private VBox pnChiTietHoaDon;
//    @FXML
//    private Button btnXacNhanDatVe;
//
//
//    private final IKhachHangService khachHangService = new KhachHangServiceImpl();
//    private List<ThongTinDatVeDTO> danhSachThongTinVe;
//    private Consumer<Boolean> onXacNhanListener;
//
//    @FXML
//    public void initialize() {
//
//        // Ẩn hóa đơn
//        pnChiTietHoaDon.setVisible(false);
//        pnChiTietHoaDon.setManaged(false);
//
//        btnXacNhanDatVe.setVisible(false);
//        btnXacNhanDatVe.setManaged(false);
//
//        // Ẩn danh sách vé nếu FXML để visible=false
//        pnScrollPaneDanhSachVe.setVisible(true);
//        pnScrollPaneDanhSachVe.setManaged(true);
//        pnDanhSachVe.setVisible(true);
//        pnDanhSachVe.setManaged(true);
//    }
//
//    public void setDanhSachThongTinVe(List<ThongTinDatVeDTO> danhSach) {
//
//        this.danhSachThongTinVe = danhSach;
//
//        pnScrollPaneDanhSachVe.setVisible(true);
//        pnScrollPaneDanhSachVe.setManaged(true);
//        pnDanhSachVe.setVisible(true);
//        pnDanhSachVe.setManaged(true);
//
//        pnDanhSachVe.getChildren().clear();
//
//        if (danhSach == null || danhSach.isEmpty()) {
//            pnDanhSachVe.getChildren().add(new Label("Chưa chọn vé nào."));
//            return;
//        }
//
//        int stt = 1;
//
//        for (ThongTinDatVeDTO ve : danhSach) {
//
//            String info = String.format(
//                    "Vé %d:\n- Ghế: %s (Toa %s)\n- Loại: %s\n- Giá: %,.0f VND",
//                    stt++,
//                    ve.getGhe().getSoGhe(),
//                    ve.getGhe().getMaToa(),
//                    ve.getLoaiGhe(),
//                    ve.getGiaVe()
//            );
//
//            Label lbl = new Label(info);
//            lbl.setWrapText(true);
//            lbl.setStyle("""
//                    -fx-padding: 10;
//                    -fx-background-color: #f5f5f5;
//                    -fx-border-color: #e0e0e0;
//                    -fx-border-width: 0 0 1 0;
//                    -fx-font-size: 14px;
//                    """);
//
//            pnDanhSachVe.getChildren().add(lbl);
//        }
//        loadThongTinVe();
//
//    }
//
//    private void loadThongTinVe() {
//
//    }
//
//    @FXML
//    public void onDatVe(ActionEvent event) {
//
//    }
//
//
//    @FXML
//    public void onXacNhanDatVe(ActionEvent event) {
//        if (onXacNhanListener != null)
//            onXacNhanListener.accept(true);
//    }
//
//    public void (Consumer<Boolean> listener) {
//        this.onXacNhanListener = listener;
//    }
//
//    @FXML
//    public void timThongTinKHTheoSdt(ActionEvent event) {
//
//    }
//
//
//    public void anThongTinVeTau() {
//        if (pnThongTinVeTau != null) {
//            pnThongTinVeTau.setVisible(false);
//            pnThongTinVeTau.setManaged(false);
//        }
//        if (btnDatVe != null) {
//            btnDatVe.setVisible(false);
//            btnDatVe.setManaged(false);
//        }
//        if (pnDanhSachVe != null) {
//            pnDanhSachVe.getChildren().clear();
//        }
//    }
//
//    private GridPane taoGridPaneLayout() {
//        GridPane grid = new GridPane();
//        grid.setAlignment(javafx.geometry.Pos.CENTER);
//        grid.setHgap(5.0);
//        grid.setVgap(10.0);
//        grid.setPadding(new Insets(5.0));
//        grid.setStyle("-fx-border-color: #ccc; -fx-border-radius: 10; -fx-background-color: white;");
//
//        // Định nghĩa Column Constraints
//        ColumnConstraints col1 = new ColumnConstraints(100, 100, Double.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT, true);
//        ColumnConstraints col2 = new ColumnConstraints(150, 150, Double.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT, true);
//        ColumnConstraints col3 = new ColumnConstraints(100, 100, Double.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT, true);
//        ColumnConstraints col4 = new ColumnConstraints(150, 150, Double.MAX_VALUE, Priority.SOMETIMES, HPos.LEFT, true);
//        grid.getColumnConstraints().addAll(col1, col2, col3, col4);
//
//        // Thêm các thành phần con vào grid (Layout rỗng)
//        grid.add(new Label("Hành khách:"), 0, 0);
//        grid.add(new TextField(), 1, 0);
//
//        grid.add(new Label("CCCD:"), 2, 0);
//        grid.add(new TextField(), 3, 0);
//
//        grid.add(new Label("Ngày khởi hành:"), 0, 1);
//        grid.add(new TextField(), 1, 1);
//
//        grid.add(new Label("Ngày đến:"), 2, 1);
//        grid.add(new TextField(), 3, 1);
//
//        grid.add(new Label("Toa-khoang-ghế:"), 0, 2);
//        grid.add(new TextField(), 1, 2);
//
//        grid.add(new Label("Ngày đặt:"), 2, 2);
//        grid.add(new TextField(), 3, 2);
//
//        grid.add(new Label("Chuyến tàu:"), 0, 3);
//        grid.add(new TextField(), 1, 3);
//
//        grid.add(new Label("Loại ghế:"), 2, 3);
//        grid.add(new TextField(), 3, 3);
//
//        grid.add(new Label("Tổng tiền:"), 0, 4);
//        TextField txtTongTien = new TextField();
//        grid.add(txtTongTien, 1, 4, 3, 1); // columnSpan="3"
//        return grid;
//    }
//
//    public void hienThiDanhSachVe(List<ThongTinDatVeDTO> danhSach) {
//        if (pnDanhSachVe == null) return;
//
//        if (pnThongTinVeTau != null) {
//            pnThongTinVeTau.setVisible(true);
//            pnThongTinVeTau.setManaged(true);
//        }
//        if (btnDatVe != null) {
//            btnDatVe.setVisible(true);
//            btnDatVe.setManaged(true);
//        }
//        pnDanhSachVe.getChildren().clear();
//        if (pnScrollPaneDanhSachVe != null) {
//            pnScrollPaneDanhSachVe.setVisible(true);
//            pnScrollPaneDanhSachVe.setManaged(true);
//        }
//        pnDanhSachVe.setVisible(true);
//        pnDanhSachVe.setManaged(true);
//
//        for (int i = 0; i < danhSach.size(); i++) {
//            Label lblTieuDe = new Label("VÉ SỐ " + (i + 1));
//            lblTieuDe.setStyle("-fx-font-weight: bold; -fx-text-fill: #gray; -fx-padding: 10 0 5 0; -fx-border-width: 0 0 1 0; -fx-border-color: #eee;");
//            pnDanhSachVe.getChildren().add(lblTieuDe);
//            GridPane gridVe = taoGridPaneLayout();
//            pnDanhSachVe.getChildren().add(gridVe);
//        }
//    }
//
//    public void onKiemTraMaKhuyenMai(ActionEvent actionEvent) {
//    }
//}
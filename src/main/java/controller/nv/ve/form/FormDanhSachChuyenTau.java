package controller.nv.ve.form;

import dto.ChuyenTauDTO;
import dto.ThongTinChuyenTauDTO;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class FormDanhSachChuyenTau {
    @FXML
    private VBox pnThongTinChuyenDiMotChieu;
    @FXML
    private VBox pnDanhSachChuyenDiMotChieu;
    @FXML
    private VBox pnThongTinChuyenDiKhuHoi;
    @FXML
    private VBox pnDanhSachChuyenDi;
    @FXML
    private VBox pnDanhSachChuyenVe;

    private Consumer<ThongTinChuyenTauDTO> onDanhSachChuyenTauListener;

    public void setOnChonChuyenTauListener(Consumer<ThongTinChuyenTauDTO> listener) {
        this.onDanhSachChuyenTauListener = listener;
    }

    public void hienThiChuyenTau(ChuyenTauDTO chuyenTauDTO) {
        boolean loaiVe = chuyenTauDTO.getChuyenTauVe() != null && !chuyenTauDTO.getChuyenTauVe().isEmpty();

        if (!loaiVe) {
            pnThongTinChuyenDiMotChieu.setVisible(true);
            pnThongTinChuyenDiMotChieu.setManaged(true);
            pnThongTinChuyenDiKhuHoi.setVisible(false);
            pnThongTinChuyenDiKhuHoi.setManaged(false);

            pnDanhSachChuyenDiMotChieu.getChildren().clear();

            for (ThongTinChuyenTauDTO ct : chuyenTauDTO.getChuyenTauDi()) {
                pnDanhSachChuyenDiMotChieu.getChildren().add(taoChuyenTau(ct));
            }

        } else {
            pnThongTinChuyenDiMotChieu.setVisible(false);
            pnThongTinChuyenDiMotChieu.setManaged(false);
            pnThongTinChuyenDiKhuHoi.setVisible(true);
            pnThongTinChuyenDiKhuHoi.setManaged(true);

            pnDanhSachChuyenDi.getChildren().clear();
            pnDanhSachChuyenVe.getChildren().clear();

            for (ThongTinChuyenTauDTO ctDi : chuyenTauDTO.getChuyenTauDi()) {
                pnDanhSachChuyenDi.getChildren().add(taoChuyenTau(ctDi));
            }
            for (ThongTinChuyenTauDTO ctVe : chuyenTauDTO.getChuyenTauVe()) {
                pnDanhSachChuyenVe.getChildren().add(taoChuyenTau(ctVe));
            }
        }
    }

    /**
     * Tạo 1 box hiển thị thông tin chuyến tàu + nút "Chọn"
     */
    private VBox taoChuyenTau(ThongTinChuyenTauDTO ct) {
        VBox box = new VBox(5);
        box.setStyle("-fx-border-color: #aaa; -fx-border-radius: 8; -fx-padding: 10; -fx-background-color: #f9f9f9;");

        Label lblTau = new Label("Tàu: " + ct.getTenTau());
        Label lblToa = new Label("Số toa: " + ct.getSoToaTau());
        Label lblNgayDi = new Label("Ngày đi: " + ct.getNgayDi());

        ImageView tau = new ImageView(new Image(getClass().getResourceAsStream("/tauga/img/train.png")));
        tau.setFitWidth(20);
        tau.setFitHeight(20);

        Label lblGioDi = new Label(ct.getGioDiStr() != null ? ct.getGioDiStr() : "");
        Label lblGioDen = new Label(ct.getGioDenStr() != null ? ct.getGioDenStr() : "");

        HBox gioBox = new HBox(100, lblGioDi, tau, lblGioDen);
        gioBox.setAlignment(Pos.CENTER);

        Label lblMuiTen = new Label("──────────────────────▶");
        HBox muiTenBox = new HBox(lblMuiTen);
        muiTenBox.setAlignment(Pos.CENTER);

        Label lblGaDi = new Label(ct.getGaDi());
        Label lblGaDen = new Label(ct.getGaDen());
        HBox gaBox = new HBox(150, lblGaDi, lblGaDen);
        gaBox.setAlignment(Pos.CENTER);

        Button btnChon = new Button("Chọn");
        btnChon.setOnAction(e -> {
            if (onDanhSachChuyenTauListener != null)
            /**
             * callback cho nay
             */
                onDanhSachChuyenTauListener.accept(ct); //callback qua DatVeController
        });

        HBox chonBox = new HBox(btnChon);
        chonBox.setAlignment(Pos.CENTER);

        box.getChildren().addAll(lblTau, lblToa, lblNgayDi, gioBox, muiTenBox, gaBox, chonBox);

        return box;
    }
}

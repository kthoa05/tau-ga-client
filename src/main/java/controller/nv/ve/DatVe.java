package controller.nv.ve;

import controller.nv.ve.form.*;
import dto.ChuyenTauDTO;
import dto.GheDTO;
import dto.ThongTinChuyenTauDTO;
import dto.ThongTinDatVeDTO;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.*;
import utils.GiaoDienUtils;
import utils.consts.CurrentUser;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DatVe {

    public SplitPane spTimKiem_DanhSach;
    public AnchorPane apTren;
    public AnchorPane rootStack;
    public VBox pnTimKiemTop;
    public StackPane pnDanhSachChuyenTauTop;

    // panel
    @FXML
    private VBox pnTimKiemFull;
    @FXML
    private SplitPane spTimKiem_ChuyenTau;
    @FXML
    private VBox pnTimKiemLeft;
    @FXML
    private StackPane pnDanhSachChuyenTau;
    @FXML
    private SplitPane spThongTin_SoDo;
    @FXML
    private VBox pnSoDoGhe;
    @FXML
    private BorderPane bpThongTinCuoi;
    @FXML
    private VBox pnThanhToan;
    @FXML
    private VBox pnThongTinKhachHang;

    //controller form
    @FXML
    private FormTimKiemChuyenTau formTimKiemChuyenTau;
    @FXML
    private FormDanhSachChuyenTau formDanhSachChuyenTau;
    @FXML
    private FormSoDoGhe formSoDoGhe;
    @FXML
    private FormThongTinKhachHang formThongTinKhachHang;
    @FXML
    private FormHinhThucThanhToan formHinhThucThanhToan;
    @FXML
    private VBox pnChiTietHoaDon;

    // === BIẾN LƯU TRẠNG THÁI ===
    // Dùng để lưu thông tin qua các bước
    private String loaiGheDangChon;
    private ChuyenTauDTO ketQuaTimKiem; // Lưu kết quả tìm kiếm (chứa ngày đi, ngày đến)
    private ThongTinChuyenTauDTO tauDaChon; // Lưu chuyến tàu đã chọn (chứa tên tàu)
    private List<GheDTO> thongTinCacGheDaChon = new ArrayList<>();
    ;
    private TrangThaiTimKiem trangThaiTimKiem = new TrangThaiTimKiem();

    private List<ThongTinDatVeDTO> danhSachThongTinVe = new ArrayList<>();

    @FXML
    public void initialize() {
        pnTimKiemLeft.getChildren().clear();
        pnTimKiemTop.getChildren().clear();
        pnDanhSachChuyenTau.getChildren().clear();
        pnDanhSachChuyenTauTop.getChildren().clear();
        pnSoDoGhe.getChildren().clear();
        pnThongTinKhachHang.getChildren().clear();
        pnThanhToan.getChildren().clear();
        hienThiTimKiemChuyenTau();
        loadFormTimKiemChuyenTau(pnTimKiemFull);
    }

    // Giai đoạn 1: lúc đầu full 100% màn hình khi baấm tìm kiếm
    public void hienThiTimKiemChuyenTau() {
        duaSoDoVeRootNeuCan();
        pnTimKiemFull.setVisible(true);
        pnTimKiemFull.setManaged(true);
        spTimKiem_ChuyenTau.setVisible(false);
        spTimKiem_ChuyenTau.setManaged(false);
        spThongTin_SoDo.setVisible(false);
        spThongTin_SoDo.setManaged(false);
        bpThongTinCuoi.setVisible(false);
        bpThongTinCuoi.setManaged(false);
    }

    // Giai đoạn 2: Có kết quả chuyến tàu
    public void hienThiDanhSachChuyenTau(ChuyenTauDTO result) {
        duaSoDoVeRootNeuCan();
        loadFormTimKiemChuyenTau(pnTimKiemLeft);
        loadFormDanhSachChuyenTau(result, pnDanhSachChuyenTau);

        pnTimKiemFull.setVisible(false);
        pnTimKiemFull.setManaged(false);

        spTimKiem_ChuyenTau.setVisible(true);
        spTimKiem_ChuyenTau.setManaged(true);

        spThongTin_SoDo.setVisible(false);
        spThongTin_SoDo.setManaged(false);

        bpThongTinCuoi.setVisible(false);
        bpThongTinCuoi.setManaged(false);
        spTimKiem_ChuyenTau.setDividerPositions(0.5);
    }

    // Giai đoạn 3: Chọn chuyến tàu -> Hiển thị sơ đồ ghế
    public void hienThiSoDoGhe(ThongTinChuyenTauDTO ct) {
        duaSoDoVeRootNeuCan();
        loadFormTimKiemChuyenTau(pnTimKiemTop);
        loadFormDanhSachChuyenTau(ketQuaTimKiem, pnDanhSachChuyenTauTop);
        loadFormSoDoGhe(ct);
        resetThongTinVaThanhToan();

        spTimKiem_ChuyenTau.setVisible(false);
        spTimKiem_ChuyenTau.setManaged(false);

        spThongTin_SoDo.setVisible(true);
        spThongTin_SoDo.setManaged(true);

        bpThongTinCuoi.setVisible(false);
        bpThongTinCuoi.setManaged(false);
        spTimKiem_ChuyenTau.setDividerPositions(0.5);
    }

    // Giai đoạn 4: chọn ghế → hiển thị thông tin KH + vé tương ứng
    public void hienThiThongTinKHVaVe() {
        loadFormThongTinKhachHang();

        if (formThongTinKhachHang != null) {
            formThongTinKhachHang.hienThiDanhSachVe(thongTinCacGheDaChon);
        }
        bpThongTinCuoi.setLeft(spThongTin_SoDo);
        spThongTin_SoDo.setVisible(true);
        spThongTin_SoDo.setManaged(true);
        spThongTin_SoDo.setPrefWidth(1000);
        bpThongTinCuoi.setVisible(true);
        bpThongTinCuoi.setManaged(true);


    }

    // Giai đoạn 5: hiển thị hình thức thanh toán
    public void hienThiHinhThucThanhToan(List<ThongTinDatVeDTO> veDTOS) {
        this.loadFormHinhThucThanhToan(veDTOS);

        bpThongTinCuoi.setVisible(true);
        bpThongTinCuoi.setManaged(true);
        pnThanhToan.setMinWidth(400);
        pnThanhToan.setMinHeight(670);
    }

    private void loadFormTimKiemChuyenTau(Pane targetPane) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tauga/view/nv/ve/form/FormTimKiemChuyenTau.fxml"));
            Node node = loader.load();
            formTimKiemChuyenTau = loader.getController();
            formTimKiemChuyenTau.apDungTrangThaiTimKiem(
                    trangThaiTimKiem.gaDi,
                    trangThaiTimKiem.gaDen,
                    trangThaiTimKiem.loaiGhe,
                    trangThaiTimKiem.khuHoi,
                    trangThaiTimKiem.ngayDi,
                    trangThaiTimKiem.ngayVe
            );

            formTimKiemChuyenTau.setOnTimKiemListener(result -> {
                capNhatTrangThaiTimKiem();
                this.ketQuaTimKiem = result;
                this.loaiGheDangChon = formTimKiemChuyenTau.getLoaiGheGiuongDangChon();
                System.out.println("Có " + result.getChuyenTauDi().size() + " chuyến đi phù hợp");
                hienThiDanhSachChuyenTau(result);
            });

            targetPane.getChildren().setAll(node);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFormDanhSachChuyenTau(ChuyenTauDTO result, Pane targetPane) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tauga/view/nv/ve/form/FormDanhSachChuyenTau.fxml"));
            Node node = loader.load();
            formDanhSachChuyenTau = loader.getController();

            // callback khi user chọn chuyến tàu
            formDanhSachChuyenTau.setOnChonChuyenTauListener(ct -> {
                this.tauDaChon = ct;
                System.out.println("Chọn chuyến tàu: " + ct.getMaTau());
                hienThiSoDoGhe(ct);
                CurrentUser.setThongTinChuyenTauDTO(ct);
            });

            targetPane.getChildren().setAll(node);
            formDanhSachChuyenTau.hienThiChuyenTau(result);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadFormSoDoGhe(ThongTinChuyenTauDTO ct) {
        try {
            formSoDoGhe = null;
            pnSoDoGhe.getChildren().clear();
            thongTinCacGheDaChon.clear();
            danhSachThongTinVe.clear();
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/tauga/view/nv/ve/form/FormSoDoGhe.fxml")
            );
            Node node = loader.load();
            formSoDoGhe = loader.getController();

            formSoDoGhe.setOnChonGheListener(danhSachGhe -> {
                thongTinCacGheDaChon.clear();
                danhSachThongTinVe.clear();

                if (danhSachGhe.isEmpty()) {
                    if (formThongTinKhachHang != null) {
                        formThongTinKhachHang.anThongTinVeTau();
                    }
                    return;
                }

                for (GheDTO ghe : danhSachGhe) {
                    thongTinCacGheDaChon.add(ghe);

                    ThongTinDatVeDTO ve = new ThongTinDatVeDTO();
                    ve.setGhe(ghe);
                    ve.setGiaVe(ghe.getGiaVeCuoi());

                    danhSachThongTinVe.add(ve);
                }

                hienThiThongTinKHVaVe();
            });

            pnSoDoGhe.getChildren().add(node);
            formSoDoGhe.hienThiSoDoGhe(ct, loaiGheDangChon);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void loadFormThongTinKhachHang() {
        if (formThongTinKhachHang == null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/tauga/view/nv/ve/form/FormThongTinKhachHang.fxml"));
                Node node = loader.load();
                formThongTinKhachHang = loader.getController();

                // danh sach ve qua form thong tin kh de xu ly
                formThongTinKhachHang.setDanhSachThongTinVe(tauDaChon, thongTinCacGheDaChon);

                // call back dat ve
                formThongTinKhachHang.setDatVeListener(success -> {
                    if (success) {
                        System.out.println("Xác nhận thành công, tiếp tục hiển thị chi tiết hoá đơn");
                    }

                });

                formThongTinKhachHang.setXacNhanDatVeListener(resultXacNhanDatVe -> {
                    if (resultXacNhanDatVe) {
                        System.out.println("Đặt vé thành công, hiển thị hình thức thanh toán");
                        GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "", "Đặt vé thành công!");
                        hienThiHinhThucThanhToan(CurrentUser.getThongTinVeDaDatDuoc());
                        return;
                    }
                });

                pnThongTinKhachHang.getChildren().clear();
                pnThongTinKhachHang.getChildren().add(node);

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // Cập nhật danh sách vé mỗi lần load form
        if (formThongTinKhachHang != null) {
            formThongTinKhachHang.setDanhSachThongTinVe(tauDaChon, thongTinCacGheDaChon);
        }

    }

    private void loadFormHinhThucThanhToan(List<ThongTinDatVeDTO> veDTOS) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/tauga/view/nv/ve/form/FormHinhThucThanhToan.fxml"));
            Node node = loader.load();
            FormHinhThucThanhToan controller = loader.getController();
            // Truyền dữ liệu vé + ghế đã chọn sang form thanh toán
            controller.setThongTinToInVe(CurrentUser.getThongTinVeDaDatDuoc());

            controller.hienThiMacDinh();
            controller.setOnThanhToanXong((phuongThuc, soTien) -> {
                System.out.println(">>> Thanh toán thành công bằng " + phuongThuc);
                System.out.println(">>> Tổng tiền: " + soTien);
            });

            controller.setOnInVeXong(inVe -> {
                // Xử lý sau khi in vé xong (nếu cần)
            });

            controller.setKetThucListener(done -> {
                if (!done) return;

                System.out.println("Kết thúc — reset về form tìm kiếm");

                Platform.runLater(() -> {

                    // 1) XÓA TẤT CẢ NỘI DUNG TRONG VÙNG THÔNG TIN DƯỚI
                    pnThanhToan.getChildren().clear();
                    pnSoDoGhe.getChildren().clear();
                    pnThongTinKhachHang.getChildren().clear();
                    pnDanhSachChuyenTau.getChildren().clear();
                    pnDanhSachChuyenTauTop.getChildren().clear();
                    pnTimKiemLeft.getChildren().clear();
                    pnTimKiemTop.getChildren().clear();

                    // 2) RESET CÁC CONTROLLER FORM CON
                    formSoDoGhe = null;
                    formDanhSachChuyenTau = null;
                    formThongTinKhachHang = null;

                    danhSachThongTinVe.clear();
                    thongTinCacGheDaChon.clear();

                    // 3) HIỆN LẠI GIAI ĐOẠN 1 (full màn hình form tìm kiếm)
                    hienThiTimKiemChuyenTau();

                    // 4) LOAD LẠI FORM TÌM KIẾM
                    pnTimKiemFull.getChildren().clear();
                    formTimKiemChuyenTau = null;
                    trangThaiTimKiem = new TrangThaiTimKiem();
                    loadFormTimKiemChuyenTau(pnTimKiemFull);

                    System.out.println(">>> Đã quay lại form tìm kiếm!");
                });
            });

            pnThanhToan.getChildren().clear();
            pnThanhToan.getChildren().add(node);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void capNhatTrangThaiTimKiem() {
        if (formTimKiemChuyenTau == null) {
            return;
        }
        trangThaiTimKiem.gaDi = formTimKiemChuyenTau.getGaDiDangChon();
        trangThaiTimKiem.gaDen = formTimKiemChuyenTau.getGaDenDangChon();
        trangThaiTimKiem.loaiGhe = formTimKiemChuyenTau.getLoaiGheGiuongDangChon();
        trangThaiTimKiem.khuHoi = formTimKiemChuyenTau.isKhuHoiDangChon();
        trangThaiTimKiem.ngayDi = formTimKiemChuyenTau.getNgayDiDangChon();
        trangThaiTimKiem.ngayVe = formTimKiemChuyenTau.getNgayVeDangChon();
    }

    private void resetThongTinVaThanhToan() {
        pnThongTinKhachHang.getChildren().clear();
        pnThanhToan.getChildren().clear();
        formThongTinKhachHang = null;
        thongTinCacGheDaChon.clear();
        danhSachThongTinVe.clear();
    }

    private void duaSoDoVeRootNeuCan() {
        if (spThongTin_SoDo.getParent() == rootStack) {
            return;
        }

        bpThongTinCuoi.setLeft(null);
        rootStack.getChildren().remove(spThongTin_SoDo);
        rootStack.getChildren().add(2, spThongTin_SoDo);
        AnchorPane.setTopAnchor(spThongTin_SoDo, 0.0);
        AnchorPane.setBottomAnchor(spThongTin_SoDo, 0.0);
        AnchorPane.setLeftAnchor(spThongTin_SoDo, 0.0);
        AnchorPane.setRightAnchor(spThongTin_SoDo, 0.0);
    }

    private static class TrangThaiTimKiem {
        private String gaDi;
        private String gaDen;
        private String loaiGhe;
        private boolean khuHoi;
        private LocalDate ngayDi;
        private LocalDate ngayVe;
    }

}

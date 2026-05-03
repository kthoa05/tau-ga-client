package controller.nv.ve;

import dto.HoaDonVeDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.print.*;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import service.IEmailService;
import service.IVeTauService;
import service.impl.EmailServiceImpl;
import service.impl.VeTauServiceImpl;
import utils.QRCodeUtils;

import java.io.File;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;


public class HoaDonVeTau {
    @FXML
    private Label txtVeTauDemSo;
    public ImageView imgQRCode;
    @FXML
    public Label lblSoTienKhuyeMai;
    @FXML
    private Label lblMaVe;
    @FXML
    private Label lblGaDi;
    @FXML
    private Label lblGaDen;
    @FXML
    private Label lblMaToaTau;
    @FXML
    private Label lblTenKhoangTau;
    @FXML
    private Label lblSoGhe;
    @FXML
    private Label lblGiaVe;
    @FXML
    private Label lblMaKhuyenMai;
    @FXML
    private Label lblTongTien;
    @FXML
    private Label lblPhuongThucThanhToan;
    @FXML
    private Label lblTenHanhKhach;
    @FXML
    private Label lblNgayGioKhoiHanh;
    @FXML
    private Label lblTenTau;
    @FXML
    private Label lblLoaiVe;
    @FXML
    private Label lblLoaiGhe;
    @FXML
    private VBox root;


    @FXML
    private Label lblMaVeNhan;
    @FXML
    private Label lblGaDiNhan;
    @FXML
    private Label lblGaDenNhan;
    @FXML
    private Label lblMaToaTauNhan;
    @FXML
    private Label lblTenKhoangTauNhan;
    @FXML
    private Label lblSoGheNhan;
    @FXML
    private Label lblGiaVeNhan;
    @FXML
    private Label lblMaKhuyenMaiNhan;
    @FXML
    private Label lblTongTienNhan;
    @FXML
    private Label lblPhuongThucThanhToanNhan;
    @FXML
    private Label lblTenHanhKhachNhan;
    @FXML
    private Label lblNgayGioKhoiHanhNhan;
    @FXML
    private Label lblTenTauNhan;
    @FXML
    private Label lblLoaiVeNhan;
    @FXML
    private Label lblLoaiGheNhan;

    //service
    private final IVeTauService veTauService = new VeTauServiceImpl();
    private final IEmailService emailService = new EmailServiceImpl();

    private List<HoaDonVeDTO> hoaDonVe;
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

    @FXML
    public void initialize() {
    }

    public void setHoanVeData(List<HoaDonVeDTO> ve) {
        this.hoaDonVe = ve;
        hienThiThongTinHoaDon(ve);
    }

    private void hienThiThongTinHoaDon(List<HoaDonVeDTO> hoaDonVeDTO1) {
        var tongSoVe = hoaDonVeDTO1.size();
        if (tongSoVe == 1) {
            this.hienThiVeTruocKhiIn(hoaDonVeDTO1.get(0), true, 0);
            return;
        }
        for (int i = 0; i < hoaDonVeDTO1.size(); i++) {
            var hoaDonVeDTO = hoaDonVeDTO1.get(i);
            this.hienThiVeTruocKhiIn(hoaDonVeDTO, false, i);
        }
    }

    private void hienThiVeTruocKhiIn(HoaDonVeDTO hoaDonVeDTO, boolean chiMotVe, int stt) {
        if (chiMotVe) {
            txtVeTauDemSo.setText("Vé tàu xe lửa");
            this.hienThiVeTruocKhiIn(hoaDonVeDTO);
            return;
        }

        txtVeTauDemSo.setText("Vé tàu số " + stt);
        this.hienThiVeTruocKhiIn(hoaDonVeDTO);
    }


    private void hienThiVeTruocKhiIn(HoaDonVeDTO hoaDonVeDTO) {
        lblMaVe.setText(hoaDonVeDTO.getMaVe());
        lblTenHanhKhach.setText(hoaDonVeDTO.getTenHanhKhach());
        lblTenTau.setText(hoaDonVeDTO.getTenTau());
        lblGaDi.setText(hoaDonVeDTO.getGaDi());
        lblGaDen.setText(hoaDonVeDTO.getGaDen());
        lblNgayGioKhoiHanh.setText(hoaDonVeDTO.getNgayGioKhoiHanh());

        lblMaToaTau.setText(hoaDonVeDTO.getMaToaTau());
        lblTenKhoangTau.setText(hoaDonVeDTO.getTenKhoangTau());
        lblSoGhe.setText(String.valueOf(hoaDonVeDTO.getSoGhe()));

        lblLoaiVe.setText(hoaDonVeDTO.getTenLoaiVe() != null ? hoaDonVeDTO.getTenLoaiVe() : "N/A");
        lblLoaiGhe.setText(hoaDonVeDTO.getTenLoaiGhe() != null ? hoaDonVeDTO.getTenLoaiGhe() : "N/A");

        lblGiaVe.setText(currencyFormat.format(hoaDonVeDTO.getGiaVe()));
        lblMaKhuyenMai.setText(hoaDonVeDTO.getMaKhuyenMai() != null && !hoaDonVeDTO.getMaKhuyenMai().isEmpty() ? hoaDonVeDTO.getMaKhuyenMai() : "Không có");
        lblSoTienKhuyeMai.setText(currencyFormat.format(hoaDonVeDTO.getSoTienGiamGia()));
        lblTongTien.setText(currencyFormat.format(hoaDonVeDTO.getTongTien()));
        lblPhuongThucThanhToan.setText(hoaDonVeDTO.getPhuongThucThanhToan());

        String outputDir = "C:/ve/qr";
        new File(outputDir).mkdirs();
        String qrPath = QRCodeUtils.generateQRCode(hoaDonVeDTO.getMaVe(), outputDir);

        if (qrPath != null) {
            Image qrImage = new Image(new File(qrPath).toURI().toString());
            imgQRCode.setImage(qrImage);
        }
    }

    private void addBold(TextFlow tf, String text, Font font) {
        Text t = new Text(text);
        t.setFont(font);
        tf.getChildren().add(t);
    }

    private void addNormal(TextFlow tf, String text, Font font) {
        Text t = new Text(text);
        t.setFont(font);
        tf.getChildren().add(t);
    }


    public void InVe(List<HoaDonVeDTO> danhSachVe) {

        Printer printer = Printer.getDefaultPrinter();
        if (printer == null) {
            System.out.println("Không tìm thấy máy in!");
            return;
        }

        PrinterJob job = PrinterJob.createPrinterJob(printer);
        if (job == null) return;

        PageLayout pageLayout = printer.createPageLayout(
                Paper.NA_LETTER,
                PageOrientation.PORTRAIT,
                Printer.MarginType.HARDWARE_MINIMUM
        );
        job.getJobSettings().setPageLayout(pageLayout);

        double widthPoints = 58 / 25.4 * 72; // giấy 58mm

        for (HoaDonVeDTO ve : danhSachVe) {

            VBox receipt = taoNodeInVe(ve, widthPoints);

            receipt.applyCss();
            receipt.layout();

            boolean ok = job.printPage(receipt);
            if (!ok) {
                System.out.println("Lỗi khi in vé: " + ve.getMaVe());
                break;
            }
        }

        job.endJob();
    }

    @FXML
    public void InVe(ActionEvent event) {
        if (hoaDonVe == null) {
            System.out.println("Không có vé để in!");
            return;
        }
        InVe(hoaDonVe);
    }

    private VBox taoNodeInVe(HoaDonVeDTO hoaDonVe, double widthPoints) {

        VBox receipt = new VBox(4);
        receipt.setPadding(new Insets(2));
        receipt.setPrefWidth(widthPoints);

        Text title = new Text("PTUD_Nhom20_IUH");
        title.setFont(Font.font("Arial", 10));

        Text title2 = new Text("VÉ TÀU XE LỬA");
        title2.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        Text title3 = new Text("Cảm ơn quý khách!\n");

        title3.setFont(Font.font("Arial", FontWeight.BOLD, 11));

        HBox titleBox = new HBox(title);
        titleBox.setAlignment(Pos.CENTER);
        HBox title2Box = new HBox(title2);
        title2Box.setAlignment(Pos.CENTER);
        HBox title3Box = new HBox(title3);
        title3Box.setAlignment(Pos.CENTER);

        ImageView qrPrint = new ImageView();
        qrPrint.setFitWidth(95);
        qrPrint.setFitHeight(95);
        qrPrint.setPreserveRatio(true);

        String qrPath = QRCodeUtils.generateQRCode(hoaDonVe.getMaVe(), "C:/ve/qr");
        if (qrPath != null) {
            qrPrint.setImage(new Image(new File(qrPath).toURI().toString()));
        }

        HBox qrBox = new HBox(qrPrint);
        qrBox.setAlignment(Pos.CENTER);

        TextFlow info = new TextFlow();
        info.setPrefWidth(widthPoints);

        Font bold = Font.font("Arial", FontWeight.BOLD, 10);
        Font normal = Font.font("Arial", 10);
        Font maVeFont = Font.font("Arial", 8);

        addBold(info, "Mã vé:\n", bold);
        addNormal(info, hoaDonVe.getMaVe() + "\n", maVeFont);

        addBold(info, "Hành khách:\n", bold);
        addNormal(info, hoaDonVe.getTenHanhKhach() + "\n", normal);

        addBold(info, "Tàu:\n", bold);
        addNormal(info, hoaDonVe.getTenTau() + "\n", normal);

        addBold(info, "Ga đi: ", bold);
        addNormal(info, hoaDonVe.getGaDi() + "\n", normal);

        addBold(info, "Ga đến: ", bold);
        addNormal(info, hoaDonVe.getGaDen() + "\n", normal);

        addBold(info, "Ngày giờ khởi hành:\n", bold);
        addNormal(info, hoaDonVe.getNgayGioKhoiHanh() + "\n", normal);

        addBold(info, "Toa: ", bold);
        addNormal(info, hoaDonVe.getMaToaTau() + "\n", normal);

        addBold(info, "Khoang: ", bold);
        addNormal(info, hoaDonVe.getTenKhoangTau() + "\n", normal);

        addBold(info, "Ghế: ", bold);
            addNormal(info, String.valueOf(hoaDonVe.getSoGhe()) + "\n", normal);


        addBold(info, "Loại ghế:\n", bold);
        addNormal(info, String.valueOf(hoaDonVe.getTenLoaiGhe()) + "\n", normal);


        addBold(info, "Loại vé:\n", bold);
        addNormal(info, String.valueOf(hoaDonVe.getTenLoaiVe()) + "\n", normal);

        addBold(info, "Giá vé: ", bold);
        addNormal(info, currencyFormat.format(hoaDonVe.getGiaVe()) + "\n", normal);

        receipt.getChildren().addAll(
                titleBox,
                title2Box,
                qrBox,
                info,
                title3Box
        );

        return receipt;
    }


}
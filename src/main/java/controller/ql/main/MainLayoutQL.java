package controller.ql.main;

import dto.NhanVienDTO;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import utils.GiaoDienUtils;
import utils.consts.CurrentUser;
import utils.enums.ManHinh;

public class MainLayoutQL {
    @FXML
    private VBox nhanVienMenu;
    @FXML
    private ImageView arrowIconNhanVien;
    private boolean veTauMenuVisible = false;
    @FXML
    private VBox traCuuMenu;
    @FXML
    private ImageView arrowIconTraCuu;
    private boolean traCuuMenuVisible = false;
    @FXML
    private VBox thongKeMenu;
    @FXML
    private ImageView arrowIconThongKe;
    private boolean thongKeMenuVisible = false;
    @FXML
    private VBox pnDangXuat;
    @FXML
    private Button btnCoDangXuat;
    @FXML
    private AnchorPane contentArea;
    @FXML
    private Label lblXinChao;
    @FXML
    private javafx.scene.layout.HBox btnDashboard;
    private javafx.scene.layout.HBox currentSelectedHBox;
    private javafx.scene.layout.HBox currentSelectedParent;
    @FXML
    private javafx.scene.layout.HBox btnTraCuu;
    @FXML
    private javafx.scene.layout.HBox btnNhanVien;
    @FXML
    private javafx.scene.layout.HBox btnThongKe;

    @FXML
    private ImageView imgUser;
    private ContextMenu userMenu;

    @FXML
    public void initialize() {
        GiaoDienUtils.setMainLayoutQL(this);
        GiaoDienUtils.ManHinhUtils.loadContent(contentArea, ManHinh.DashboardQL);
        btnDashboard.getStyleClass().add("menu-item-active");
        currentSelectedHBox = btnDashboard;
        nhanVienMenu.setVisible(veTauMenuVisible);
        nhanVienMenu.setManaged(veTauMenuVisible);
        traCuuMenu.setVisible(traCuuMenuVisible);
        traCuuMenu.setManaged(traCuuMenuVisible);

        thongKeMenu.setVisible(thongKeMenuVisible);
        thongKeMenu.setManaged(thongKeMenuVisible);

        NhanVienDTO nv = CurrentUser.getNhanVien();
        GiaoDienUtils.loadHeader(nv, lblXinChao);

        MenuItem taiKhoanItem = new MenuItem("Tài khoản");
        MenuItem quenMatKhauItem = new MenuItem("Quên mật khẩu");
        MenuItem dangXuatItem = new MenuItem("Đăng xuất");

        taiKhoanItem.setOnAction(e -> {
            GiaoDienUtils.ManHinhUtils.loadContent(contentArea, ManHinh.ThongTinCaNhan);
        });

        quenMatKhauItem.setOnAction(e ->{
            GiaoDienUtils.ManHinhUtils.loadContent(contentArea, ManHinh.QuenMK);
        });

        dangXuatItem.setOnAction(e -> {
            GiaoDienUtils.ManHinhUtils.manHinhDangXuat(pnDangXuat, true);
        });

        userMenu = new ContextMenu(taiKhoanItem, quenMatKhauItem, dangXuatItem);
    }

    @FXML
    private void toggleVeTauMenu() {
        veTauMenuVisible = !veTauMenuVisible;
        GiaoDienUtils.hienThiMenu(veTauMenuVisible, nhanVienMenu, arrowIconNhanVien);
    }

    @FXML
    private void toggleTraCuuMenu() {
        traCuuMenuVisible = !traCuuMenuVisible;
        GiaoDienUtils.hienThiMenu(traCuuMenuVisible, traCuuMenu, arrowIconTraCuu);
    }

    public void toggleThongKe(MouseEvent mouseEvent) {
        thongKeMenuVisible = !thongKeMenuVisible;
        GiaoDienUtils.hienThiMenu(thongKeMenuVisible, thongKeMenu, arrowIconThongKe);
    }

//    @FXML
//    public void chuyenManHinh(MouseEvent event) {
//        Label lbl = (Label) event.getSource();
//        String text = lbl.getText().trim();
//        ManHinh manHinh = ManHinh.findScreencreByTitle(text);
//
//        if (manHinh != ManHinh.UNKNOW) {
//            GiaoDienUtils.ManHinhUtils.loadContent(contentArea, manHinh);
//        }
//    }
@FXML
public void chuyenManHinh(MouseEvent event) {
    Object source = event.getSource();
    javafx.scene.layout.HBox clickedHBox;
    if (source instanceof Label) {
        clickedHBox = (javafx.scene.layout.HBox) ((Label) source).getParent();
    } else {
        clickedHBox = (javafx.scene.layout.HBox) source;
    }
    if (currentSelectedHBox != null && currentSelectedHBox != clickedHBox) {
        currentSelectedHBox.getStyleClass().remove("menu-item-active");
    }
    if (!clickedHBox.getStyleClass().contains("menu-item-active")) {
        clickedHBox.getStyleClass().add("menu-item-active");
    }
    currentSelectedHBox = clickedHBox;
    String text = "";
    if (source instanceof Label) {
        text = ((Label) source).getText().trim();
    } else {
        text = clickedHBox.getChildren().stream()
                .filter(node -> node instanceof Label)
                .map(node -> ((Label) node).getText().trim())
                .findFirst().orElse("");
    }

    ManHinh manHinh = ManHinh.findScreencreByTitle(text);
    if (manHinh != ManHinh.UNKNOW) {
        GiaoDienUtils.ManHinhUtils.loadContent(contentArea, manHinh);
    }
}


    public void hienThiFrameDangXuat(MouseEvent mouseEvent) {
        GiaoDienUtils.ManHinhUtils.manHinhDangXuat(pnDangXuat, true);
    }

    public void khongDangXuat(ActionEvent actionEvent) {
        GiaoDienUtils.ManHinhUtils.manHinhDangXuat(pnDangXuat, false);
    }

    public void dangXuat(ActionEvent actionEvent) {
        GiaoDienUtils.ManHinhUtils.chuyenManHinh(btnCoDangXuat, ManHinh.DangNhap.getPath(), ManHinh.DoiVe.getTitle());
    }

    public void dongHopThoai(ActionEvent actionEvent) {
        GiaoDienUtils.ManHinhUtils.manHinhDangXuat(pnDangXuat, false);
    }

    public void goToFromDashboard(ManHinh manHinh) {
        GiaoDienUtils.ManHinhUtils.loadContent(contentArea, manHinh);
        if (currentSelectedHBox != null)
            currentSelectedHBox.getStyleClass().remove("menu-item-active");

        if (currentSelectedParent != null)
            currentSelectedParent.getStyleClass().remove("menu-item-active");
        btnThongKe.getStyleClass().add("menu-item-active");
        currentSelectedParent = btnThongKe;
        if (!thongKeMenuVisible) {
            thongKeMenuVisible = true;
            GiaoDienUtils.hienThiMenu(true, thongKeMenu, arrowIconThongKe);
        }
        for (var node : thongKeMenu.getChildren()) {
            if (node instanceof javafx.scene.layout.HBox hBox) {
                String title = hBox.getChildren().stream()
                        .filter(n -> n instanceof Label)
                        .map(n -> ((Label) n).getText().trim())
                        .findFirst().orElse("");

                if (ManHinh.findScreencreByTitle(title) == manHinh) {
                    hBox.getStyleClass().add("menu-item-active");
                    currentSelectedHBox = hBox;
                    break;
                }
            }
        }
    }

    public void onUserClick(MouseEvent mouseEvent) {
        if (userMenu == null) return;

        if (userMenu.isShowing()) {
            userMenu.hide();
        } else {
            userMenu.show(
                    imgUser,
                    mouseEvent.getScreenX(),
                    mouseEvent.getScreenY()
            );
        }
    }
}



package controller.nv.main;

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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import utils.GiaoDienUtils;
import utils.consts.CurrentUser;
import utils.enums.ManHinh;

public class MainLayout {
    @FXML
    private AnchorPane contentArea;
    @FXML
    private VBox veTauMenu, traCuuMenu, thongKeMenu;
    @FXML
    private ImageView arrowIconVeTau, arrowIconTraCuu, arrowIconThongKe;
    @FXML
    private VBox pnDangXuat;
    @FXML
    private Button btnCoDangXuat;
    @FXML
    private Label lblXinChao;

    @FXML
    private HBox btnDashboard;
    @FXML
    private HBox btnTraCuu;
    @FXML
    private HBox btnVeTau;
    @FXML
    private HBox btnThongKe;
    @FXML
    private ImageView imgUser;
    private ContextMenu userMenu;

    private HBox currentSelectedHBox;
    private HBox currentSelectedParent;

    private boolean veTauMenuVisible = false;
    private boolean traCuuMenuVisible = false;
    private boolean thongKeMenuVisible = false;

    @FXML
    public void initialize() {
        GiaoDienUtils.setMainLayoutNV(this);
        GiaoDienUtils.ManHinhUtils.loadContent(contentArea, ManHinh.DashboardNV);
        btnDashboard.getStyleClass().add("menu-item-active");
        currentSelectedHBox = btnDashboard;
        veTauMenu.setVisible(veTauMenuVisible);
        veTauMenu.setManaged(veTauMenuVisible);

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

        quenMatKhauItem.setOnAction(e -> {
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
        GiaoDienUtils.hienThiMenu(veTauMenuVisible, veTauMenu, arrowIconVeTau);
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

    @FXML
    public void chuyenManHinh(MouseEvent event) {
        Object source = event.getSource();
        HBox clickedHBox;

        if (source instanceof Label) {
            clickedHBox = (HBox) ((Label) source).getParent();
        } else {
            clickedHBox = (HBox) source;
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
            if (node instanceof HBox hBox) {
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


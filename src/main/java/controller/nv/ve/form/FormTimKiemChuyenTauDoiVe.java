package controller.nv.ve.form;

import dto.ChiTietVeTraDTO;
import dto.ChuyenTauDTO;
import dto.ThongTinChuyenTauDTO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import service.IChuyenTauService;
import service.IVeTauService;
import service.impl.ChuyenTauServiceImpl;
import service.impl.VeTauServiceImpl;
import utils.GiaoDienUtils;
import utils.consts.CurrentUser;

import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class FormTimKiemChuyenTauDoiVe {
    @FXML
    private ComboBox<String> cbbLoaiGhe;
    @FXML
    private ComboBox<String> cbbGioKhoiHanh;


    private IVeTauService veTauService = new VeTauServiceImpl();
    private IChuyenTauService chuyenTauService = new ChuyenTauServiceImpl();

    //callback
    private Consumer<ChuyenTauDTO> onTimKiemListener;

    public void setOnTimKiemListener(Consumer<ChuyenTauDTO> listener) {
        this.onTimKiemListener = listener;
    }

    public void timKiemCT(javafx.event.ActionEvent actionEvent) {
        /**
         ===== DONE =====
         1. Load data
         * */
        ChiTietVeTraDTO veTauDTO = CurrentUser.getChiTietVeTraDTO();

        /**
         ======= DONE ======
         2. Tim kiem theo field gio di + loai ghe tuong ung vs ga di, ga den, ngay khoi hanh
         * */
        String gio = cbbGioKhoiHanh.getSelectionModel().getSelectedItem();
        String ngayGioDi = veTauDTO.getNgayKhoiHanh().toString();
        String ngayGioKhoiHanh = ngayGioDi + " " + gio + ":00";
        String loaiGhe = cbbLoaiGhe.getSelectionModel().getSelectedItem();

        ChuyenTauDTO result = chuyenTauService.getThongTinChuyenTauForDoiVe(veTauDTO.getGaDi(), veTauDTO.getGaDen(), loaiGhe, ngayGioKhoiHanh);
        CurrentUser.setChuyenTauDTO(result);
        /**
         3. Hien thi len UI
         */
        if (result.getChuyenTauDi() == null || result.getChuyenTauDi().size() == 0) {
            GiaoDienUtils.showThongBao(Alert.AlertType.WARNING, "", "Không tìm thấy chuyến đi nào phù hợp");
            return;
        }
        // 4️⃣ Lọc theo giờ khởi hành (dựa trên chuỗi)
        List<ThongTinChuyenTauDTO> dsLoc = result.getChuyenTauDi().stream()
                .filter(ct -> {
                    try {
                        // ví dụ: "2025-11-04 06:00:00.0"
                        String gioDiStr = ct.getNgayGioDi().substring(11, 16); // lấy "06:00"
                        return gioDiStr.equals(gio);
                    } catch (Exception e) {
                        return false;
                    }
                })
                .collect(Collectors.toList());

        if (dsLoc.isEmpty()) {
            GiaoDienUtils.showThongBao(Alert.AlertType.INFORMATION, "Thông báo",
                    "Không có chuyến tàu khởi hành lúc " + gio);
            return;
        }

        // 5️⃣ Gán lại danh sách đã lọc
        ChuyenTauDTO ketQuaLoc = new ChuyenTauDTO();
        ketQuaLoc.setChuyenTauDi(dsLoc);
//        hienThiChuyenTau(result, loaiGhe);
    }



}

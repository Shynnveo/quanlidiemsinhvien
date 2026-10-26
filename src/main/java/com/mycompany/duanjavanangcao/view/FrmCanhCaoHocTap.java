package com.mycompany.duanjavanangcao.view;

import com.mycompany.duanjavanangcao.dao.CanhCaoHocTapDAO;
import com.mycompany.duanjavanangcao.dao.HocKyDAO;
import com.mycompany.duanjavanangcao.dao.LopDAO;
import com.mycompany.duanjavanangcao.dao.SinhVienDAO;
import com.mycompany.duanjavanangcao.dao.TieuChiCanhCaoDAO;
import com.mycompany.duanjavanangcao.model.CanhCaoHocTap;
import com.mycompany.duanjavanangcao.model.HocKy;
import com.mycompany.duanjavanangcao.model.Lop;
import com.mycompany.duanjavanangcao.model.SinhVien;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.model.TieuChiCanhCao;
import com.mycompany.duanjavanangcao.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Form Quản lý Cảnh báo học tập & Xử lý học vụ - Thiết kế chuẩn NetBeans Swing GUI Builder
 */
public class FrmCanhCaoHocTap extends javax.swing.JFrame {

    private final CanhCaoHocTapDAO canhCaoDAO;
    private final TieuChiCanhCaoDAO tieuChiDAO;
    private final HocKyDAO hocKyDAO;
    private final LopDAO lopDAO;
    private final SinhVienDAO sinhVienDAO;

    private TaiKhoan currentUser;
    private SinhVien currentSinhVien;
    private List<CanhCaoHocTap> danhSachCanhCao;
    private CanhCaoHocTap canhCaoDangChon;

    /**
     * Khởi tạo form FrmCanhCaoHocTap
     */
    public FrmCanhCaoHocTap() {
        initComponents();
        getContentPane().setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.BG_LIGHT);
        pnlBoLoc.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlDanhSach.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlChiTiet.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatTable(tblCanhCao);
        btnQuetTuDong.setBackground(new java.awt.Color(242, 140, 40)); // Cam UTT nổi bật
        this.setLocationRelativeTo(null);

        this.canhCaoDAO = new CanhCaoHocTapDAO();
        this.tieuChiDAO = new TieuChiCanhCaoDAO();
        this.hocKyDAO = new HocKyDAO();
        this.lopDAO = new LopDAO();
        this.sinhVienDAO = new SinhVienDAO();
        this.danhSachCanhCao = new ArrayList<>();

        khoiTaoDuLieu();
    }

    /**
     * Khởi tạo combobox bộ lọc, phân quyền người dùng và nạp dữ liệu ban đầu
     */
    private void khoiTaoDuLieu() {
        currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng đăng nhập hệ thống để tiếp tục!",
                    "Chưa đăng nhập",
                    JOptionPane.WARNING_MESSAGE);
            new FrmDangNhap().setVisible(true);
            this.dispose();
            return;
        }

        // 1. Nạp học kỳ vào bộ lọc
        DefaultComboBoxModel<HocKy> modelHk = new DefaultComboBoxModel<>();
        HocKy allHk = new HocKy();
        allHk.setHocKyId(0);
        allHk.setTenHocKy("-- Tất cả học kỳ --");
        modelHk.addElement(allHk);

        List<HocKy> listHk = hocKyDAO.findAll();
        for (HocKy hk : listHk) {
            modelHk.addElement(hk);
        }
        cboHocKy.setModel(modelHk);

        // Nạp học kỳ vào panel chi tiết
        DefaultComboBoxModel<HocKy> modelHkXet = new DefaultComboBoxModel<>();
        for (HocKy hk : listHk) {
            modelHkXet.addElement(hk);
        }
        cboHocKyXet.setModel(modelHkXet);

        // 2. Nạp lớp vào bộ lọc
        DefaultComboBoxModel<Lop> modelLop = new DefaultComboBoxModel<>();
        Lop allLop = new Lop();
        allLop.setLopId(0);
        allLop.setTenLop("-- Tất cả lớp --");
        modelLop.addElement(allLop);

        List<Lop> listLop = lopDAO.findAll();
        for (Lop l : listLop) {
            modelLop.addElement(l);
        }
        cboLop.setModel(modelLop);

        // 3. Nạp tiêu chí cảnh báo
        DefaultComboBoxModel<TieuChiCanhCao> modelTc = new DefaultComboBoxModel<>();
        List<TieuChiCanhCao> listTc = tieuChiDAO.findActive();
        for (TieuChiCanhCao tc : listTc) {
            modelTc.addElement(tc);
        }
        cboTieuChi.setModel(modelTc);

        // 4. Nạp danh sách sinh viên vào panel chi tiết
        DefaultComboBoxModel<SinhVien> modelSv = new DefaultComboBoxModel<>();
        List<SinhVien> listSv = sinhVienDAO.findAll();
        for (SinhVien sv : listSv) {
            modelSv.addElement(sv);
        }
        cboSinhVien.setModel(modelSv);

        // 5. Phân quyền người dùng
        boolean isSinhVien = "SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro());

        if (isSinhVien) {
            currentSinhVien = sinhVienDAO.findByTaiKhoanId(currentUser.getTaiKhoanId());
            if (currentSinhVien != null) {
                lblTieuDe.setText("TÌNH TRẠNG CẢNH BÁO HỌC TẬP - SV: " + currentSinhVien.getHoTen()
                        + " (" + currentSinhVien.getMaSinhVien() + ")");
            }

            cboLop.setEnabled(false);
            btnQuetTuDong.setEnabled(false);
            btnThem.setEnabled(false);
            btnCapNhat.setEnabled(false);
            btnXoa.setEnabled(false);

            cboSinhVien.setEnabled(false);
            cboHocKyXet.setEnabled(false);
            cboTieuChi.setEnabled(false);
            cboMucCanhCaoDetail.setEnabled(false);
            cboTrangThaiDetail.setEnabled(false);
            txtLyDo.setEditable(false);
            txtGhiChu.setEditable(false);

            lblHuongDan.setText("LƯU Ý HỌC VỤ: Sinh viên bị cảnh báo cần liên hệ Cố vấn học tập để giảm tải số tín chỉ (tối đa 14 TC/kỳ) và cải thiện điểm!");
        } else {
            lblTieuDe.setText("QUẢN LÝ CẢNH BÁO HỌC TẬP & XỬ LÝ HỌC VỤ (" + currentUser.getVaiTro() + ")");
            lblHuongDan.setText("Lưu ý quy chế: Sinh viên bị cảnh báo học tập 3 lần liên tiếp sẽ bị xem xét buộc thôi học theo quy định.");
        }

        taiDanhSachCanhCao();
    }

    /**
     * Tải danh sách cảnh báo học tập từ CSDL lên bảng JTable
     */
    private void taiDanhSachCanhCao() {
        HocKy selHk = (HocKy) cboHocKy.getSelectedItem();
        int hocKyId = (selHk != null) ? selHk.getHocKyId() : 0;

        Lop selLop = (Lop) cboLop.getSelectedItem();
        int lopId = (selLop != null) ? selLop.getLopId() : 0;

        String mucCanhCao = (String) cboMucCanhCao.getSelectedItem();
        String trangThai = (String) cboTrangThai.getSelectedItem();
        String keyword = txtTimKiem.getText().trim();

        if (currentUser != null && "SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro())) {
            if (currentSinhVien != null) {
                List<CanhCaoHocTap> fullList = canhCaoDAO.findBySinhVien(currentSinhVien.getSinhVienId());
                danhSachCanhCao = new ArrayList<>();
                for (CanhCaoHocTap cc : fullList) {
                    boolean matchHk = (hocKyId <= 0 || cc.getHocKyId() == hocKyId);
                    boolean matchMuc = (mucCanhCao == null || "Tất cả".equalsIgnoreCase(mucCanhCao)
                            || cc.getMucCanhCao().equalsIgnoreCase(mucCanhCao));
                    boolean matchTt = (trangThai == null || "Tất cả".equalsIgnoreCase(trangThai)
                            || cc.getTrangThai().equalsIgnoreCase(trangThai));

                    if (matchHk && matchMuc && matchTt) {
                        danhSachCanhCao.add(cc);
                    }
                }
            }
        } else {
            danhSachCanhCao = canhCaoDAO.findByFilter(hocKyId, lopId, mucCanhCao, trangThai, keyword);
        }

        hienThiLenTable(danhSachCanhCao);
    }

    /**
     * Hiển thị danh sách cảnh báo học tập lên JTable
     */
    private void hienThiLenTable(List<CanhCaoHocTap> list) {
        DefaultTableModel model = (DefaultTableModel) tblCanhCao.getModel();
        model.setRowCount(0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        int stt = 1;

        for (CanhCaoHocTap cc : list) {
            String ngayXet = (cc.getNgayXet() != null) ? sdf.format(cc.getNgayXet()) : "";
            String gpaHK = (cc.getGpaHocKy() != null) ? String.format("%.2f", cc.getGpaHocKy()) : "--";
            String cpaTL = (cc.getGpaTichLuy() != null) ? String.format("%.2f", cc.getGpaTichLuy()) : "--";
            String nguoiXet = (cc.getTenNguoiXet() != null) ? cc.getTenNguoiXet() : "Hệ thống";

            model.addRow(new Object[]{
                stt++,
                cc.getCanhCaoId(),
                cc.getMaSinhVien(),
                cc.getHoTenSinhVien(),
                cc.getTenLop() != null ? cc.getTenLop() : "",
                cc.getTenHocKy(),
                cc.getTenTieuChi() != null ? cc.getTenTieuChi() : cc.getMaTieuChi(),
                cc.getMucCanhCao(),
                gpaHK,
                cpaTL,
                ngayXet,
                cc.getTrangThai(),
                nguoiXet
            });
        }

        canhCaoDangChon = null;
    }

    /**
     * Đồng bộ thông tin cảnh báo khi click chọn hàng trên bảng
     */
    private void napChiTietCanhCao(CanhCaoHocTap cc) {
        if (cc == null) return;

        // 1. Chọn sinh viên
        for (int i = 0; i < cboSinhVien.getItemCount(); i++) {
            SinhVien sv = cboSinhVien.getItemAt(i);
            if (sv.getSinhVienId() == cc.getSinhVienId()) {
                cboSinhVien.setSelectedIndex(i);
                break;
            }
        }

        // 2. Chọn học kỳ xét
        for (int i = 0; i < cboHocKyXet.getItemCount(); i++) {
            HocKy hk = cboHocKyXet.getItemAt(i);
            if (hk.getHocKyId() == cc.getHocKyId()) {
                cboHocKyXet.setSelectedIndex(i);
                break;
            }
        }

        // 3. Chọn tiêu chí
        for (int i = 0; i < cboTieuChi.getItemCount(); i++) {
            TieuChiCanhCao tc = cboTieuChi.getItemAt(i);
            if (tc.getTieuChiId() == cc.getTieuChiId()) {
                cboTieuChi.setSelectedIndex(i);
                break;
            }
        }

        // 4. Mức cảnh báo & trạng thái
        cboMucCanhCaoDetail.setSelectedItem(cc.getMucCanhCao());
        cboTrangThaiDetail.setSelectedItem(cc.getTrangThai());

        // 5. Lý do & ghi chú
        txtLyDo.setText(cc.getLyDo());
        txtGhiChu.setText(cc.getGhiChu() != null ? cc.getGhiChu() : "");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTieuDe = new javax.swing.JLabel();
        pnlBoLoc = new javax.swing.JPanel();
        lblHocKy = new javax.swing.JLabel();
        cboHocKy = new javax.swing.JComboBox<>();
        lblLop = new javax.swing.JLabel();
        cboLop = new javax.swing.JComboBox<>();
        lblMucCanhCao = new javax.swing.JLabel();
        cboMucCanhCao = new javax.swing.JComboBox<>();
        lblTrangThai = new javax.swing.JLabel();
        cboTrangThai = new javax.swing.JComboBox<>();
        lblTimKiem = new javax.swing.JLabel();
        txtTimKiem = new javax.swing.JTextField();
        btnLoc = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnQuetTuDong = new javax.swing.JButton();
        btnDong = new javax.swing.JButton();
        pnlDanhSach = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCanhCao = new javax.swing.JTable();
        pnlChiTiet = new javax.swing.JPanel();
        lblSinhVien = new javax.swing.JLabel();
        cboSinhVien = new javax.swing.JComboBox<>();
        lblHocKyXet = new javax.swing.JLabel();
        cboHocKyXet = new javax.swing.JComboBox<>();
        lblTieuChi = new javax.swing.JLabel();
        cboTieuChi = new javax.swing.JComboBox<>();
        lblMucCanhCaoDetail = new javax.swing.JLabel();
        cboMucCanhCaoDetail = new javax.swing.JComboBox<>();
        lblLyDo = new javax.swing.JLabel();
        txtLyDo = new javax.swing.JTextField();
        lblTrangThaiDetail = new javax.swing.JLabel();
        cboTrangThaiDetail = new javax.swing.JComboBox<>();
        lblGhiChu = new javax.swing.JLabel();
        txtGhiChu = new javax.swing.JTextField();
        btnThem = new javax.swing.JButton();
        btnCapNhat = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnLamMoiForm = new javax.swing.JButton();
        lblHuongDan = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("QUẢN LÝ CẢNH BÁO HỌC TẬP & XỬ LÝ HỌC VỤ - UTT");

        lblTieuDe.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTieuDe.setForeground(new java.awt.Color(30, 58, 138));
        lblTieuDe.setText("CẢNH BÁO HỌC TẬP & XỬ LÝ HỌC VỤ - UTT");

        pnlBoLoc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bộ lọc và tìm kiếm cảnh báo học tập", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblHocKy.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblHocKy.setForeground(new java.awt.Color(31, 41, 55));
        lblHocKy.setText("Học kỳ:");

        cboHocKy.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        cboHocKy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboHocKyActionPerformed(evt);
            }
        });

        lblLop.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblLop.setForeground(new java.awt.Color(31, 41, 55));
        lblLop.setText("Lớp:");

        cboLop.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        cboLop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLopActionPerformed(evt);
            }
        });

        lblMucCanhCao.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblMucCanhCao.setForeground(new java.awt.Color(31, 41, 55));
        lblMucCanhCao.setText("Mức cảnh báo:");

        cboMucCanhCao.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        cboMucCanhCao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Cảnh báo mức 1", "Cảnh báo mức 2", "Cảnh báo mức 3 (Buộc thôi học)" }));
        cboMucCanhCao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMucCanhCaoActionPerformed(evt);
            }
        });

        lblTrangThai.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblTrangThai.setForeground(new java.awt.Color(31, 41, 55));
        lblTrangThai.setText("Trạng thái:");

        cboTrangThai.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        cboTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Đang áp dụng", "Đã khắc phục", "Đã hủy" }));
        cboTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTrangThaiActionPerformed(evt);
            }
        });

        lblTimKiem.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblTimKiem.setForeground(new java.awt.Color(31, 41, 55));
        lblTimKiem.setText("Từ khóa:");

        txtTimKiem.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        btnLoc.setBackground(new java.awt.Color(37, 99, 235));
        btnLoc.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLoc.setForeground(new java.awt.Color(255, 255, 255));
        btnLoc.setText("Lọc");
        btnLoc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLoc.setFocusPainted(false);
        btnLoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLocActionPerformed(evt);
            }
        });

        btnLamMoi.setBackground(new java.awt.Color(226, 232, 240));
        btnLamMoi.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLamMoi.setForeground(new java.awt.Color(31, 41, 55));
        btnLamMoi.setText("Làm mới");
        btnLamMoi.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLamMoi.setFocusPainted(false);
        btnLamMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiActionPerformed(evt);
            }
        });

        btnQuetTuDong.setBackground(new java.awt.Color(220, 38, 38));
        btnQuetTuDong.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnQuetTuDong.setForeground(new java.awt.Color(255, 255, 255));
        btnQuetTuDong.setText("Quét xét tự động");
        btnQuetTuDong.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQuetTuDong.setFocusPainted(false);
        btnQuetTuDong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuetTuDongActionPerformed(evt);
            }
        });

        btnDong.setBackground(new java.awt.Color(226, 232, 240));
        btnDong.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDong.setForeground(new java.awt.Color(31, 41, 55));
        btnDong.setText("Đóng");
        btnDong.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDong.setFocusPainted(false);
        btnDong.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDongActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlBoLocLayout = new javax.swing.GroupLayout(pnlBoLoc);
        pnlBoLoc.setLayout(pnlBoLocLayout);
        pnlBoLocLayout.setHorizontalGroup(
            pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoLocLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBoLocLayout.createSequentialGroup()
                        .addComponent(lblHocKy)
                        .addGap(8, 8, 8)
                        .addComponent(cboHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(lblLop)
                        .addGap(8, 8, 8)
                        .addComponent(cboLop, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(lblMucCanhCao)
                        .addGap(8, 8, 8)
                        .addComponent(cboMucCanhCao, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(lblTrangThai)
                        .addGap(8, 8, 8)
                        .addComponent(cboTrangThai, 0, 130, Short.MAX_VALUE))
                    .addGroup(pnlBoLocLayout.createSequentialGroup()
                        .addComponent(lblTimKiem)
                        .addGap(8, 8, 8)
                        .addComponent(txtTimKiem)
                        .addGap(10, 10, 10)
                        .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnQuetTuDong, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        pnlBoLocLayout.setVerticalGroup(
            pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoLocLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHocKy)
                    .addComponent(cboHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLop)
                    .addComponent(cboLop, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMucCanhCao)
                    .addComponent(cboMucCanhCao, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTrangThai)
                    .addComponent(cboTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTimKiem)
                    .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnQuetTuDong, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        pnlDanhSach.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách sinh viên bị cảnh báo học tập", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        tblCanhCao.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblCanhCao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã CB", "Mã SV", "Họ và tên", "Lớp", "Học kỳ", "Tiêu chí", "Mức cảnh báo", "GPA HK", "CPA TL", "Ngày xét", "Trạng thái", "Người xét"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblCanhCao.setRowHeight(24);
        tblCanhCao.setSelectionBackground(new java.awt.Color(37, 99, 235));
        tblCanhCao.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblCanhCao.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblCanhCao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCanhCaoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblCanhCao);

        javax.swing.GroupLayout pnlDanhSachLayout = new javax.swing.GroupLayout(pnlDanhSach);
        pnlDanhSach.setLayout(pnlDanhSachLayout);
        pnlDanhSachLayout.setHorizontalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDanhSachLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1)
                .addGap(10, 10, 10))
        );
        pnlDanhSachLayout.setVerticalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDanhSachLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 192, Short.MAX_VALUE)
                .addGap(8, 8, 8))
        );

        pnlChiTiet.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chi tiết cảnh báo & Xử lý học vụ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblSinhVien.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblSinhVien.setForeground(new java.awt.Color(31, 41, 55));
        lblSinhVien.setText("Sinh viên:");

        cboSinhVien.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        lblHocKyXet.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblHocKyXet.setForeground(new java.awt.Color(31, 41, 55));
        lblHocKyXet.setText("Học kỳ:");

        cboHocKyXet.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        lblTieuChi.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblTieuChi.setForeground(new java.awt.Color(31, 41, 55));
        lblTieuChi.setText("Tiêu chí:");

        cboTieuChi.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        lblMucCanhCaoDetail.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblMucCanhCaoDetail.setForeground(new java.awt.Color(31, 41, 55));
        lblMucCanhCaoDetail.setText("Mức cảnh báo:");

        cboMucCanhCaoDetail.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        cboMucCanhCaoDetail.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Cảnh báo mức 1", "Cảnh báo mức 2", "Cảnh báo mức 3 (Buộc thôi học)" }));

        lblLyDo.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblLyDo.setForeground(new java.awt.Color(31, 41, 55));
        lblLyDo.setText("Lý do cảnh báo:");

        txtLyDo.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        lblTrangThaiDetail.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblTrangThaiDetail.setForeground(new java.awt.Color(31, 41, 55));
        lblTrangThaiDetail.setText("Trạng thái:");

        cboTrangThaiDetail.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        cboTrangThaiDetail.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Đang áp dụng", "Đã khắc phục", "Đã hủy" }));

        lblGhiChu.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblGhiChu.setForeground(new java.awt.Color(31, 41, 55));
        lblGhiChu.setText("Ghi chú / Xử lý:");

        txtGhiChu.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        btnThem.setBackground(new java.awt.Color(21, 128, 61));
        btnThem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThem.setForeground(new java.awt.Color(255, 255, 255));
        btnThem.setText("Thêm cảnh báo");
        btnThem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnThem.setFocusPainted(false);
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnCapNhat.setBackground(new java.awt.Color(30, 58, 138));
        btnCapNhat.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCapNhat.setForeground(new java.awt.Color(255, 255, 255));
        btnCapNhat.setText("Cập nhật xử lý");
        btnCapNhat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCapNhat.setFocusPainted(false);
        btnCapNhat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCapNhatActionPerformed(evt);
            }
        });

        btnXoa.setBackground(new java.awt.Color(220, 38, 38));
        btnXoa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXoa.setForeground(new java.awt.Color(255, 255, 255));
        btnXoa.setText("Xóa");
        btnXoa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXoa.setFocusPainted(false);
        btnXoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXoaActionPerformed(evt);
            }
        });

        btnLamMoiForm.setBackground(new java.awt.Color(226, 232, 240));
        btnLamMoiForm.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLamMoiForm.setForeground(new java.awt.Color(31, 41, 55));
        btnLamMoiForm.setText("Nhập lại");
        btnLamMoiForm.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLamMoiForm.setFocusPainted(false);
        btnLamMoiForm.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiFormActionPerformed(evt);
            }
        });

        lblHuongDan.setFont(new java.awt.Font("Segoe UI", 2, 11)); // NOI18N
        lblHuongDan.setForeground(new java.awt.Color(220, 38, 38));
        lblHuongDan.setText("Lưu ý quy chế: Sinh viên bị cảnh báo học tập 3 lần liên tiếp sẽ bị xem xét buộc thôi học theo quy định của Nhà trường.");

        javax.swing.GroupLayout pnlChiTietLayout = new javax.swing.GroupLayout(pnlChiTiet);
        pnlChiTiet.setLayout(pnlChiTietLayout);
        pnlChiTietLayout.setHorizontalGroup(
            pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChiTietLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHuongDan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlChiTietLayout.createSequentialGroup()
                        .addComponent(lblSinhVien)
                        .addGap(8, 8, 8)
                        .addComponent(cboSinhVien, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(lblHocKyXet)
                        .addGap(8, 8, 8)
                        .addComponent(cboHocKyXet, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(lblTieuChi)
                        .addGap(8, 8, 8)
                        .addComponent(cboTieuChi, 0, 160, Short.MAX_VALUE)
                        .addGap(15, 15, 15)
                        .addComponent(lblMucCanhCaoDetail)
                        .addGap(8, 8, 8)
                        .addComponent(cboMucCanhCaoDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(pnlChiTietLayout.createSequentialGroup()
                        .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblLyDo)
                            .addComponent(lblGhiChu))
                        .addGap(8, 8, 8)
                        .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtGhiChu)
                            .addGroup(pnlChiTietLayout.createSequentialGroup()
                                .addComponent(txtLyDo)
                                .addGap(15, 15, 15)
                                .addComponent(lblTrangThaiDetail)
                                .addGap(8, 8, 8)
                                .addComponent(cboTrangThaiDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addGroup(pnlChiTietLayout.createSequentialGroup()
                        .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnLamMoiForm, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        pnlChiTietLayout.setVerticalGroup(
            pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChiTietLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSinhVien)
                    .addComponent(cboSinhVien, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHocKyXet)
                    .addComponent(cboHocKyXet, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTieuChi)
                    .addComponent(cboTieuChi, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMucCanhCaoDetail)
                    .addComponent(cboMucCanhCaoDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLyDo)
                    .addComponent(txtLyDo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTrangThaiDetail)
                    .addComponent(cboTrangThaiDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGhiChu)
                    .addComponent(txtGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCapNhat, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoiForm, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(lblHuongDan)
                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTieuDe)
                    .addComponent(pnlBoLoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlChiTiet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblTieuDe)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlBoLoc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlChiTiet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cboHocKyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboHocKyActionPerformed
        taiDanhSachCanhCao();
    }//GEN-LAST:event_cboHocKyActionPerformed

    private void cboLopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLopActionPerformed
        taiDanhSachCanhCao();
    }//GEN-LAST:event_cboLopActionPerformed

    private void cboMucCanhCaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMucCanhCaoActionPerformed
        taiDanhSachCanhCao();
    }//GEN-LAST:event_cboMucCanhCaoActionPerformed

    private void cboTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTrangThaiActionPerformed
        taiDanhSachCanhCao();
    }//GEN-LAST:event_cboTrangThaiActionPerformed

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocActionPerformed
        taiDanhSachCanhCao();
    }//GEN-LAST:event_btnLocActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        txtTimKiem.setText("");
        cboHocKy.setSelectedIndex(0);
        cboLop.setSelectedIndex(0);
        cboMucCanhCao.setSelectedIndex(0);
        cboTrangThai.setSelectedIndex(0);
        taiDanhSachCanhCao();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnDongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDongActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnDongActionPerformed

    private void tblCanhCaoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCanhCaoMouseClicked
        int selectedRow = tblCanhCao.getSelectedRow();
        if (selectedRow < 0 || danhSachCanhCao == null || selectedRow >= danhSachCanhCao.size()) {
            return;
        }

        canhCaoDangChon = danhSachCanhCao.get(selectedRow);
        napChiTietCanhCao(canhCaoDangChon);
    }//GEN-LAST:event_tblCanhCaoMouseClicked

    private void btnQuetTuDongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuetTuDongActionPerformed
        if (currentUser == null || "SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro())) {
            JOptionPane.showMessageDialog(this,
                    "Chức năng quét xét cảnh báo tự động chỉ dành cho Giảng viên và Quản trị viên!",
                    "Từ chối quyền",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        HocKy selHk = (HocKy) cboHocKy.getSelectedItem();
        if (selHk == null || selHk.getHocKyId() <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một học kỳ cụ thể trong ô 'Học kỳ' trên thanh bộ lọc để thực hiện quét xét cảnh báo!",
                    "Chưa chọn học kỳ",
                    JOptionPane.WARNING_MESSAGE);
            cboHocKy.requestFocus();
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận quét xét cảnh báo học tập tự động cho: " + selHk.getTenHocKy() + "?\n"
                + "- Hệ thống sẽ kiểm tra GPA học kỳ và số tín chỉ nợ của tất cả sinh viên.\n"
                + "- Tự động đối chiếu quy chế UTT để phát hành cảnh báo Mức 1, Mức 2 hoặc Mức 3.\n"
                + "- Tự động gửi thông báo trực tiếp đến tài khoản các sinh viên bị cảnh báo.",
                "Xác nhận quét xét cảnh báo",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        int countIssued = canhCaoDAO.quetCanhCaoTuDong(selHk.getHocKyId(), currentUser.getTaiKhoanId());

        if (countIssued > 0) {
            JOptionPane.showMessageDialog(this,
                    "Quá trình quét xét hoàn tất!\nĐã phát hành mới " + countIssued + " quyết định cảnh báo học tập.\n"
                    + "Thông báo cảnh báo đã được gửi tự động tới các sinh viên vi phạm.",
                    "Kết quả quét xét",
                    JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Quá trình quét xét hoàn tất! Không có trường hợp vi phạm mới nào trong học kỳ này.",
                    "Kết quả quét xét",
                    JOptionPane.INFORMATION_MESSAGE);
        }

        taiDanhSachCanhCao();
    }//GEN-LAST:event_btnQuetTuDongActionPerformed

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        if (currentUser == null || "SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro())) {
            JOptionPane.showMessageDialog(this,
                    "Bạn không có quyền thêm cảnh báo học tập!",
                    "Từ chối quyền",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        SinhVien sv = (SinhVien) cboSinhVien.getSelectedItem();
        HocKy hk = (HocKy) cboHocKyXet.getSelectedItem();
        TieuChiCanhCao tc = (TieuChiCanhCao) cboTieuChi.getSelectedItem();
        String mucCanhCao = (String) cboMucCanhCaoDetail.getSelectedItem();
        String trangThai = (String) cboTrangThaiDetail.getSelectedItem();
        String lyDo = txtLyDo.getText().trim();
        String ghiChu = txtGhiChu.getText().trim();

        if (sv == null || hk == null || tc == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn đầy đủ Sinh viên, Học kỳ và Tiêu chí cảnh báo!",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (lyDo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập lý do cảnh báo học tập!",
                    "Thiếu lý do",
                    JOptionPane.WARNING_MESSAGE);
            txtLyDo.requestFocus();
            return;
        }

        if (canhCaoDAO.exists(sv.getSinhVienId(), hk.getHocKyId(), tc.getTieuChiId())) {
            JOptionPane.showMessageDialog(this,
                    "Sinh viên này đã bị cảnh báo với tiêu chí '" + tc.getTenTieuChi()
                    + "' trong học kỳ '" + hk.getTenHocKy() + "' rồi!",
                    "Trùng lặp cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        CanhCaoHocTap cc = new CanhCaoHocTap(
                sv.getSinhVienId(),
                hk.getHocKyId(),
                tc.getTieuChiId(),
                lyDo,
                mucCanhCao,
                trangThai,
                currentUser.getTaiKhoanId(),
                ghiChu
        );
        cc.setTaiKhoanSinhVienId(sv.getTaiKhoanId());

        boolean ok = canhCaoDAO.insert(cc);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Thêm cảnh báo học tập thành công!\nThông báo đã được gửi đến tài khoản sinh viên.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            btnLamMoiFormActionPerformed(evt);
            taiDanhSachCanhCao();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Thêm cảnh báo thất bại! Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnCapNhatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCapNhatActionPerformed
        if (currentUser == null || "SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro())) {
            JOptionPane.showMessageDialog(this,
                    "Bạn không có quyền cập nhật cảnh báo học tập!",
                    "Từ chối quyền",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (canhCaoDangChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một cảnh báo trên bảng để cập nhật xử lý!",
                    "Chưa chọn cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String mucCanhCao = (String) cboMucCanhCaoDetail.getSelectedItem();
        String trangThai = (String) cboTrangThaiDetail.getSelectedItem();
        String lyDo = txtLyDo.getText().trim();
        String ghiChu = txtGhiChu.getText().trim();

        if (lyDo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Lý do cảnh báo không được để trống!",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            txtLyDo.requestFocus();
            return;
        }

        canhCaoDangChon.setMucCanhCao(mucCanhCao);
        canhCaoDangChon.setTrangThai(trangThai);
        canhCaoDangChon.setLyDo(lyDo);
        canhCaoDangChon.setGhiChu(ghiChu);
        canhCaoDangChon.setNguoiXetId(currentUser.getTaiKhoanId());

        boolean ok = canhCaoDAO.update(canhCaoDangChon);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Cập nhật thông tin cảnh báo và xử lý học vụ thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            taiDanhSachCanhCao();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Cập nhật thất bại! Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnCapNhatActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        if (currentUser == null || "SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro())) {
            JOptionPane.showMessageDialog(this,
                    "Bạn không có quyền xóa cảnh báo học tập!",
                    "Từ chối quyền",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (canhCaoDangChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một cảnh báo trên bảng để xóa!",
                    "Chưa chọn cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa cảnh báo #" + canhCaoDangChon.getCanhCaoId()
                + " của sinh viên " + canhCaoDangChon.getHoTenSinhVien() + "?",
                "Xác nhận xóa",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = canhCaoDAO.delete(canhCaoDangChon.getCanhCaoId());
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Đã xóa cảnh báo học tập thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                btnLamMoiFormActionPerformed(evt);
                taiDanhSachCanhCao();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Xóa thất bại! Vui lòng thử lại.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnLamMoiFormActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiFormActionPerformed
        canhCaoDangChon = null;
        if (cboSinhVien.getItemCount() > 0) cboSinhVien.setSelectedIndex(0);
        if (cboHocKyXet.getItemCount() > 0) cboHocKyXet.setSelectedIndex(0);
        if (cboTieuChi.getItemCount() > 0) cboTieuChi.setSelectedIndex(0);
        cboMucCanhCaoDetail.setSelectedIndex(0);
        cboTrangThaiDetail.setSelectedIndex(0);
        txtLyDo.setText("");
        txtGhiChu.setText("");
        tblCanhCao.clearSelection();
    }//GEN-LAST:event_btnLamMoiFormActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCapNhat;
    private javax.swing.JButton btnDong;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnLamMoiForm;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnQuetTuDong;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.HocKy> cboHocKy;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.HocKy> cboHocKyXet;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.Lop> cboLop;
    private javax.swing.JComboBox<String> cboMucCanhCao;
    private javax.swing.JComboBox<String> cboMucCanhCaoDetail;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.SinhVien> cboSinhVien;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.TieuChiCanhCao> cboTieuChi;
    private javax.swing.JComboBox<String> cboTrangThai;
    private javax.swing.JComboBox<String> cboTrangThaiDetail;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGhiChu;
    private javax.swing.JLabel lblHocKy;
    private javax.swing.JLabel lblHocKyXet;
    private javax.swing.JLabel lblHuongDan;
    private javax.swing.JLabel lblLop;
    private javax.swing.JLabel lblLyDo;
    private javax.swing.JLabel lblMucCanhCao;
    private javax.swing.JLabel lblMucCanhCaoDetail;
    private javax.swing.JLabel lblSinhVien;
    private javax.swing.JLabel lblTieuChi;
    private javax.swing.JLabel lblTieuDe;
    private javax.swing.JLabel lblTimKiem;
    private javax.swing.JLabel lblTrangThai;
    private javax.swing.JLabel lblTrangThaiDetail;
    private javax.swing.JPanel pnlBoLoc;
    private javax.swing.JPanel pnlChiTiet;
    private javax.swing.JPanel pnlDanhSach;
    private javax.swing.JTable tblCanhCao;
    private javax.swing.JTextField txtGhiChu;
    private javax.swing.JTextField txtLyDo;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}


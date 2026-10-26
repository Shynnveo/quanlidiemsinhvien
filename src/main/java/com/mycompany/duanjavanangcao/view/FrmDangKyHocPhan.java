package com.mycompany.duanjavanangcao.view;

import com.mycompany.duanjavanangcao.dao.DangKyHocPhanDAO;
import com.mycompany.duanjavanangcao.dao.HocKyDAO;
import com.mycompany.duanjavanangcao.dao.SinhVienDAO;
import com.mycompany.duanjavanangcao.model.DangKyHocPhan;
import com.mycompany.duanjavanangcao.model.HocKy;
import com.mycompany.duanjavanangcao.model.LopHocPhan;
import com.mycompany.duanjavanangcao.model.SinhVien;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.util.SessionManager;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Đăng ký học phần cho Sinh viên và Quản trị viên
 */
public class FrmDangKyHocPhan extends javax.swing.JFrame {

    private final DangKyHocPhanDAO dangKyDAO = new DangKyHocPhanDAO();
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final HocKyDAO hocKyDAO = new HocKyDAO();

    private List<LopHocPhan> danhSachLopHocPhan = new ArrayList<>();
    private List<DangKyHocPhan> danhSachDangKy = new ArrayList<>();
    private SinhVien currentSinhVien = null;
    private boolean isDataReady = false;

    public FrmDangKyHocPhan() {
        initComponents();
        khoiTaoDuLieu();
    }

    private void khoiTaoDuLieu() {
        // 1. Nạp danh sách học kỳ vào ComboBox
        DefaultComboBoxModel<HocKy> modelHocKy = new DefaultComboBoxModel<>();
        HocKy allHk = new HocKy();
        allHk.setHocKyId(0);
        allHk.setTenHocKy("-- Tất cả học kỳ --");
        modelHocKy.addElement(allHk);

        List<HocKy> listHk = hocKyDAO.findAll();
        for (HocKy hk : listHk) {
            modelHocKy.addElement(hk);
        }
        cboHocKy.setModel(modelHocKy);

        // 2. Xác định vai trò người dùng hiện tại
        TaiKhoan currentUser = SessionManager.getCurrentUser();
        DefaultComboBoxModel<SinhVien> modelSinhVien = new DefaultComboBoxModel<>();

        if (currentUser != null && "SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro())) {
            // Sinh viên đăng nhập: chỉ thấy thông tin của chính mình
            SinhVien sv = sinhVienDAO.findByTaiKhoanId(currentUser.getTaiKhoanId());
            if (sv != null) {
                currentSinhVien = sv;
                modelSinhVien.addElement(sv);
                cboSinhVien.setModel(modelSinhVien);
                cboSinhVien.setEnabled(false); // Khóa không cho chọn sinh viên khác
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy hồ sơ sinh viên tương ứng với tài khoản này!",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            // Quản trị viên (ADMIN) hoặc Giảng viên: được chọn sinh viên để đăng ký hộ
            List<SinhVien> listSv = sinhVienDAO.findAll();
            for (SinhVien sv : listSv) {
                modelSinhVien.addElement(sv);
            }
            cboSinhVien.setModel(modelSinhVien);
            cboSinhVien.setEnabled(true);
            if (!listSv.isEmpty()) {
                currentSinhVien = listSv.get(0);
            }
        }

        isDataReady = true;

        // 3. Tải danh sách lớp học phần đang mở và danh sách đã đăng ký
        taiDanhSachLopHocPhan();
        taiDanhSachDangKy();
    }

    private int getSelectedHocKyId() {
        HocKy selectedHk = (HocKy) cboHocKy.getSelectedItem();
        return (selectedHk != null) ? selectedHk.getHocKyId() : 0;
    }

    /**
     * Tải danh sách các lớp học phần đang mở
     */
    private void taiDanhSachLopHocPhan() {
        if (!isDataReady) {
            return;
        }

        int hocKyId = getSelectedHocKyId();
        String keyword = txtTimKiem.getText().trim();
        danhSachLopHocPhan = dangKyDAO.getAvailableClasses(hocKyId, keyword);

        DefaultTableModel model = (DefaultTableModel) tblLopHocPhan.getModel();
        model.setRowCount(0);

        for (LopHocPhan lhp : danhSachLopHocPhan) {
            String gv = (lhp.getTenGiangVien() != null) ? lhp.getTenGiangVien() : "Chưa phân công";
            String phong = (lhp.getPhongHoc() != null) ? lhp.getPhongHoc() : "";
            String lich = (lhp.getLichHoc() != null) ? lhp.getLichHoc() : "";
            int max = (lhp.getSiSoToiDa() != null) ? lhp.getSiSoToiDa() : 0;
            String siSo = lhp.getSiSoHienTai() + " / " + max;

            model.addRow(new Object[]{
                lhp.getMaLopHocPhan(),
                lhp.getTenMonHoc(),
                lhp.getSoTinChi(),
                gv,
                lich,
                phong,
                siSo,
                lhp.getTrangThai()
            });
        }
    }

    /**
     * Tải danh sách học phần sinh viên đang chọn đã đăng ký
     */
    private void taiDanhSachDangKy() {
        if (!isDataReady || currentSinhVien == null) {
            DefaultTableModel model = (DefaultTableModel) tblDangKy.getModel();
            model.setRowCount(0);
            lblTongTinChi.setText("Tổng số tín chỉ đã đăng ký: 0 TC");
            return;
        }

        int hocKyId = getSelectedHocKyId();
        danhSachDangKy = dangKyDAO.findBySinhVienAndHocKy(currentSinhVien.getSinhVienId(), hocKyId);

        DefaultTableModel model = (DefaultTableModel) tblDangKy.getModel();
        model.setRowCount(0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");

        for (DangKyHocPhan dk : danhSachDangKy) {
            String ngayDk = (dk.getNgayDangKy() != null) ? sdf.format(dk.getNgayDangKy()) : "";
            String gv = (dk.getTenGiangVien() != null) ? dk.getTenGiangVien() : "Chưa phân công";
            String phong = (dk.getPhongHoc() != null) ? dk.getPhongHoc() : "";
            String lich = (dk.getLichHoc() != null) ? dk.getLichHoc() : "";

            model.addRow(new Object[]{
                dk.getDangKyId(),
                dk.getMaLopHocPhan(),
                dk.getTenMonHoc(),
                dk.getSoTinChi(),
                gv,
                lich,
                phong,
                ngayDk,
                dk.getTrangThai()
            });
        }

        int tongTinChi = dangKyDAO.calculateTotalCredits(currentSinhVien.getSinhVienId(), hocKyId);
        lblTongTinChi.setText("Tổng số tín chỉ đã đăng ký: " + tongTinChi + " TC");
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTieuDe = new javax.swing.JLabel();
        pnlBoLoc = new javax.swing.JPanel();
        lblSinhVien = new javax.swing.JLabel();
        cboSinhVien = new javax.swing.JComboBox<>();
        lblHocKy = new javax.swing.JLabel();
        cboHocKy = new javax.swing.JComboBox<>();
        lblTimKiem = new javax.swing.JLabel();
        txtTimKiem = new javax.swing.JTextField();
        btnTimKiem = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        pnlLopHocPhan = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblLopHocPhan = new javax.swing.JTable();
        btnDangKy = new javax.swing.JButton();
        lblGhiChu1 = new javax.swing.JLabel();
        pnlDaDangKy = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblDangKy = new javax.swing.JTable();
        lblTongTinChi = new javax.swing.JLabel();
        btnHuyDangKy = new javax.swing.JButton();
        btnDong = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("ĐĂNG KÝ HỌC PHẦN - HỆ THỐNG QUẢN LÝ ĐIỂM UTT");

        lblTieuDe.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTieuDe.setForeground(new java.awt.Color(30, 58, 138));
        lblTieuDe.setText("HỆ THỐNG ĐĂNG KÝ HỌC PHẦN - UTT");

        pnlBoLoc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông tin sinh viên & Bộ lọc", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblSinhVien.setText("Sinh viên:");

        cboSinhVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboSinhVienActionPerformed(evt);
            }
        });

        lblHocKy.setText("Học kỳ:");

        cboHocKy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboHocKyActionPerformed(evt);
            }
        });

        lblTimKiem.setText("Tìm kiếm:");

        btnTimKiem.setBackground(new java.awt.Color(37, 99, 235));
        btnTimKiem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTimKiem.setForeground(new java.awt.Color(255, 255, 255));
        btnTimKiem.setText("Tìm");
        btnTimKiem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTimKiem.setFocusPainted(false);
        btnTimKiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTimKiemActionPerformed(evt);
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

        javax.swing.GroupLayout pnlBoLocLayout = new javax.swing.GroupLayout(pnlBoLoc);
        pnlBoLoc.setLayout(pnlBoLocLayout);
        pnlBoLocLayout.setHorizontalGroup(
            pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoLocLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblSinhVien)
                .addGap(8, 8, 8)
                .addComponent(cboSinhVien, javax.swing.GroupLayout.PREFERRED_SIZE, 250, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblHocKy)
                .addGap(8, 8, 8)
                .addComponent(cboHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblTimKiem)
                .addGap(8, 8, 8)
                .addComponent(txtTimKiem, javax.swing.GroupLayout.DEFAULT_SIZE, 200, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        pnlBoLocLayout.setVerticalGroup(
            pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoLocLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSinhVien)
                    .addComponent(cboSinhVien, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHocKy)
                    .addComponent(cboHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTimKiem)
                    .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        pnlLopHocPhan.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "1. DANH SÁCH LỚP HỌC PHẦN ĐANG MỞ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        tblLopHocPhan.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblLopHocPhan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã LHP", "Tên Môn Học", "Số TC", "Giảng Viên", "Lịch Học", "Phòng Học", "Sĩ Số", "Trạng Thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblLopHocPhan.setRowHeight(24);
        tblLopHocPhan.setSelectionBackground(new java.awt.Color(37, 99, 235));
        tblLopHocPhan.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblLopHocPhan.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tblLopHocPhan);

        btnDangKy.setBackground(new java.awt.Color(21, 128, 61));
        btnDangKy.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDangKy.setForeground(new java.awt.Color(255, 255, 255));
        btnDangKy.setText("ĐĂNG KÝ HỌC PHẦN ĐÃ CHỌN");
        btnDangKy.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDangKy.setFocusPainted(false);
        btnDangKy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDangKyActionPerformed(evt);
            }
        });

        lblGhiChu1.setForeground(new java.awt.Color(100, 116, 139));
        lblGhiChu1.setText("* Hướng dẫn: Chọn 1 dòng lớp học phần trong bảng trên, sau đó bấm nút 'ĐĂNG KÝ HỌC PHẦN ĐÃ CHỌN'.");

        javax.swing.GroupLayout pnlLopHocPhanLayout = new javax.swing.GroupLayout(pnlLopHocPhan);
        pnlLopHocPhan.setLayout(pnlLopHocPhanLayout);
        pnlLopHocPhanLayout.setHorizontalGroup(
            pnlLopHocPhanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLopHocPhanLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlLopHocPhanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(pnlLopHocPhanLayout.createSequentialGroup()
                        .addComponent(btnDangKy, javax.swing.GroupLayout.PREFERRED_SIZE, 220, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblGhiChu1)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        pnlLopHocPhanLayout.setVerticalGroup(
            pnlLopHocPhanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLopHocPhanLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlLopHocPhanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnDangKy, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblGhiChu1))
                .addGap(10, 10, 10))
        );

        pnlDaDangKy.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "2. HỌC PHẦN SINH VIÊN ĐÃ ĐĂNG KÝ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        tblDangKy.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblDangKy.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Mã ĐK", "Mã LHP", "Tên Môn Học", "Số TC", "Giảng Viên", "Lịch Học", "Phòng Học", "Ngày ĐK", "Trạng Thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblDangKy.setRowHeight(24);
        tblDangKy.setSelectionBackground(new java.awt.Color(37, 99, 235));
        tblDangKy.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblDangKy.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane2.setViewportView(tblDangKy);

        lblTongTinChi.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        lblTongTinChi.setForeground(new java.awt.Color(21, 128, 61));
        lblTongTinChi.setText("Tổng số tín chỉ đã đăng ký: 0 TC");

        btnHuyDangKy.setBackground(new java.awt.Color(220, 38, 38));
        btnHuyDangKy.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHuyDangKy.setForeground(new java.awt.Color(255, 255, 255));
        btnHuyDangKy.setText("HỦY ĐĂNG KÝ");
        btnHuyDangKy.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHuyDangKy.setFocusPainted(false);
        btnHuyDangKy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuyDangKyActionPerformed(evt);
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

        javax.swing.GroupLayout pnlDaDangKyLayout = new javax.swing.GroupLayout(pnlDaDangKy);
        pnlDaDangKy.setLayout(pnlDaDangKyLayout);
        pnlDaDangKyLayout.setHorizontalGroup(
            pnlDaDangKyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDaDangKyLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlDaDangKyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(pnlDaDangKyLayout.createSequentialGroup()
                        .addComponent(lblTongTinChi, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnHuyDangKy, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(12, 12, 12)
                        .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        pnlDaDangKyLayout.setVerticalGroup(
            pnlDaDangKyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDaDangKyLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 170, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlDaDangKyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTongTinChi)
                    .addComponent(btnHuyDangKy, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
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
                    .addComponent(pnlLopHocPhan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlDaDangKy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(pnlLopHocPhan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlDaDangKy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cboSinhVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSinhVienActionPerformed
        if (!isDataReady) {
            return;
        }
        currentSinhVien = (SinhVien) cboSinhVien.getSelectedItem();
        taiDanhSachDangKy();
    }//GEN-LAST:event_cboSinhVienActionPerformed

    private void cboHocKyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboHocKyActionPerformed
        if (!isDataReady) {
            return;
        }
        taiDanhSachLopHocPhan();
        taiDanhSachDangKy();
    }//GEN-LAST:event_cboHocKyActionPerformed

    private void btnTimKiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTimKiemActionPerformed
        taiDanhSachLopHocPhan();
    }//GEN-LAST:event_btnTimKiemActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        txtTimKiem.setText("");
        if (cboHocKy.getItemCount() > 0) {
            cboHocKy.setSelectedIndex(0);
        }
        taiDanhSachLopHocPhan();
        taiDanhSachDangKy();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnDangKyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDangKyActionPerformed
        if (currentSinhVien == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn sinh viên cần đăng ký học phần!",
                    "Chưa chọn sinh viên", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int selectedRow = tblLopHocPhan.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= danhSachLopHocPhan.size()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một lớp học phần trong danh sách đang mở (Bảng 1) để đăng ký!",
                    "Chưa chọn lớp học phần", JOptionPane.WARNING_MESSAGE);
            return;
        }

        LopHocPhan lhp = danhSachLopHocPhan.get(selectedRow);

        // Kiểm tra điều kiện đăng ký
        String checkMsg = dangKyDAO.checkRegistrationEligibility(currentSinhVien.getSinhVienId(), lhp.getLopHocPhanId());
        if (checkMsg != null) {
            JOptionPane.showMessageDialog(this, checkMsg, "Không đủ điều kiện đăng ký", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận đăng ký học phần:\n"
                + "- Lớp: " + lhp.getMaLopHocPhan() + " (" + lhp.getTenMonHoc() + " - " + lhp.getSoTinChi() + " TC)\n"
                + "- Sinh viên: " + currentSinhVien.getMaSinhVien() + " - " + currentSinhVien.getHoTen(),
                "Xác nhận đăng ký",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = dangKyDAO.register(currentSinhVien.getSinhVienId(), lhp.getLopHocPhanId());
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Đăng ký học phần thành công!",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    taiDanhSachLopHocPhan();
                    taiDanhSachDangKy();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Đăng ký thất bại, vui lòng thử lại!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        "Lỗi cơ sở dữ liệu: " + e.getMessage(),
                        "Lỗi", JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnDangKyActionPerformed

    private void btnHuyDangKyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyDangKyActionPerformed
        int selectedRow = tblDangKy.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= danhSachDangKy.size()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một học phần trong danh sách đã đăng ký (Bảng 2) để hủy!",
                    "Chưa chọn học phần", JOptionPane.WARNING_MESSAGE);
            return;
        }

        DangKyHocPhan dk = danhSachDangKy.get(selectedRow);
        if ("Đã hủy".equalsIgnoreCase(dk.getTrangThai())) {
            JOptionPane.showMessageDialog(this,
                    "Học phần này đã được hủy trước đó!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy đăng ký học phần:\n"
                + "- Lớp: " + dk.getMaLopHocPhan() + " (" + dk.getTenMonHoc() + ")\n"
                + "- Sinh viên: " + currentSinhVien.getHoTen() + "?",
                "Xác nhận hủy đăng ký",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                boolean success = dangKyDAO.cancelRegistration(dk.getDangKyId());
                if (success) {
                    JOptionPane.showMessageDialog(this,
                            "Hủy đăng ký học phần thành công!",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                    taiDanhSachLopHocPhan();
                    taiDanhSachDangKy();
                } else {
                    JOptionPane.showMessageDialog(this,
                            "Hủy đăng ký không thành công!",
                            "Lỗi", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(this,
                        e.getMessage(),
                        "Không thể hủy", JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHuyDangKyActionPerformed

    private void btnDongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDongActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnDongActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception ex) {
            java.util.logging.Logger.getLogger(FrmDangKyHocPhan.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FrmDangKyHocPhan().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDangKy;
    private javax.swing.JButton btnDong;
    private javax.swing.JButton btnHuyDangKy;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnTimKiem;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.HocKy> cboHocKy;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.SinhVien> cboSinhVien;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JLabel lblGhiChu1;
    private javax.swing.JLabel lblHocKy;
    private javax.swing.JLabel lblSinhVien;
    private javax.swing.JLabel lblTieuDe;
    private javax.swing.JLabel lblTimKiem;
    private javax.swing.JLabel lblTongTinChi;
    private javax.swing.JPanel pnlBoLoc;
    private javax.swing.JPanel pnlDaDangKy;
    private javax.swing.JPanel pnlLopHocPhan;
    private javax.swing.JTable tblDangKy;
    private javax.swing.JTable tblLopHocPhan;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}

package com.mycompany.duanjavanangcao.view;

import com.mycompany.duanjavanangcao.dao.HocKyDAO;
import com.mycompany.duanjavanangcao.dao.KetQuaHocTapDAO;
import com.mycompany.duanjavanangcao.dao.LopDAO;
import com.mycompany.duanjavanangcao.dao.SinhVienDAO;
import com.mycompany.duanjavanangcao.model.HocKy;
import com.mycompany.duanjavanangcao.model.KetQuaHocTap;
import com.mycompany.duanjavanangcao.model.Lop;
import com.mycompany.duanjavanangcao.model.SinhVien;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.util.SessionManager;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Kết quả học tập & Điểm trung bình tích lũy (GPA)
 */
public class FrmKetQuaGPA extends javax.swing.JFrame {

    private final KetQuaHocTapDAO ketQuaDAO = new KetQuaHocTapDAO();
    private final HocKyDAO hocKyDAO = new HocKyDAO();
    private final LopDAO lopDAO = new LopDAO();
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();

    private List<KetQuaHocTap> danhSachKetQua = new ArrayList<>();
    private KetQuaHocTap selectedKetQua = null;
    private SinhVien currentSinhVien = null;
    private boolean isDataReady = false;

    public FrmKetQuaGPA() {
        initComponents();
        getContentPane().setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.BG_LIGHT);
        pnlBoLoc.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlChiTiet.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatTable(tblKetQua);
        btnTinhGPA.setBackground(new java.awt.Color(242, 140, 40)); // Cam UTT nổi bật
        com.mycompany.duanjavanangcao.util.UIThemeUtil.applyThemeToContainer(this);
        this.setLocationRelativeTo(null);
        khoiTaoDuLieu();
    }

    private void khoiTaoDuLieu() {
        // 1. Nạp danh sách học kỳ
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

        // 2. Nạp danh sách lớp
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

        // 3. Phân quyền người dùng
        TaiKhoan currentUser = SessionManager.getCurrentUser();
        if (currentUser != null && "SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro())) {
            SinhVien sv = sinhVienDAO.findByTaiKhoanId(currentUser.getTaiKhoanId());
            if (sv != null) {
                currentSinhVien = sv;
                cboLop.setEnabled(false);
                btnTinhGPA.setVisible(false); // Sinh viên không có quyền tính GPA hàng loạt
            }
        }

        isDataReady = true;
        taiDanhSachKetQua();
    }

    private void taiDanhSachKetQua() {
        if (!isDataReady) {
            return;
        }

        DefaultTableModel model = (DefaultTableModel) tblKetQua.getModel();
        model.setRowCount(0);

        HocKy hk = (HocKy) cboHocKy.getSelectedItem();
        int hocKyId = (hk != null) ? hk.getHocKyId() : 0;

        if (currentSinhVien != null) {
            // Xem kết quả học tập của chính sinh viên đang đăng nhập
            if (hocKyId > 0) {
                KetQuaHocTap single = ketQuaDAO.findBySinhVienAndHocKy(currentSinhVien.getSinhVienId(), hocKyId);
                danhSachKetQua = new ArrayList<>();
                if (single != null) danhSachKetQua.add(single);
            } else {
                danhSachKetQua = ketQuaDAO.findBySinhVien(currentSinhVien.getSinhVienId());
            }
        } else {
            // Quản trị viên hoặc Giảng viên: lọc theo lớp và học kỳ
            Lop lop = (Lop) cboLop.getSelectedItem();
            int lopId = (lop != null) ? lop.getLopId() : 0;
            danhSachKetQua = ketQuaDAO.findByLopAndHocKy(lopId, hocKyId);
        }

        int stt = 1;
        for (KetQuaHocTap kq : danhSachKetQua) {
            String gpaHk = (kq.getGpaHocKy() != null) ? String.valueOf(kq.getGpaHocKy()) : "--";
            String gpaTl = (kq.getGpaTichLuy() != null) ? String.valueOf(kq.getGpaTichLuy()) : "--";

            model.addRow(new Object[]{
                stt++,
                kq.getMaSinhVien(),
                kq.getHoTenSinhVien(),
                kq.getTenLop() != null ? kq.getTenLop() : "",
                kq.getTenHocKy() != null ? kq.getTenHocKy() : "",
                kq.getSoTinChiDangKy(),
                kq.getSoTinChiDat(),
                gpaHk,
                kq.getXepLoaiHocKy(),
                kq.getTongTinChiTichLuy(),
                gpaTl,
                kq.getXepLoaiTichLuy()
            });
        }

        xoaChiTiet();
    }

    private void xoaChiTiet() {
        selectedKetQua = null;
        lblChiTietSV.setText("Sinh viên: (Chọn một dòng trên bảng để xem chi tiết)");
        lblThongKeNhanh.setText("GPA Học kỳ: -- | Xếp loại kỳ: -- | GPA Tích lũy: -- | Xếp loại toàn khóa: --");
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
        lblHocKy = new javax.swing.JLabel();
        cboHocKy = new javax.swing.JComboBox<>();
        lblLop = new javax.swing.JLabel();
        cboLop = new javax.swing.JComboBox<>();
        btnLoc = new javax.swing.JButton();
        btnTinhGPA = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnDong = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblKetQua = new javax.swing.JTable();
        pnlChiTiet = new javax.swing.JPanel();
        lblChiTietSV = new javax.swing.JLabel();
        lblThongKeNhanh = new javax.swing.JLabel();
        btnTinhLaiSV = new javax.swing.JButton();
        btnXemBangDiem = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("KẾT QUẢ HỌC TẬP & ĐIỂM TRUNG BÌNH TÍCH LŨY (GPA)");

        lblTieuDe.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTieuDe.setForeground(new java.awt.Color(30, 58, 138));
        lblTieuDe.setText("KẾT QUẢ HỌC TẬP & ĐIỂM TRUNG BÌNH TÍCH LŨY (GPA)");

        pnlBoLoc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bộ lọc dữ liệu & Tính toán", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

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

        btnTinhGPA.setBackground(new java.awt.Color(21, 128, 61));
        btnTinhGPA.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTinhGPA.setForeground(new java.awt.Color(255, 255, 255));
        btnTinhGPA.setText("TÍNH / CẬP NHẬT GPA TỰ ĐỘNG");
        btnTinhGPA.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTinhGPA.setFocusPainted(false);
        btnTinhGPA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTinhGPAActionPerformed(evt);
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
                .addComponent(lblHocKy)
                .addGap(8, 8, 8)
                .addComponent(cboHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(lblLop)
                .addGap(8, 8, 8)
                .addComponent(cboLop, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnTinhGPA, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        pnlBoLocLayout.setVerticalGroup(
            pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoLocLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHocKy)
                    .addComponent(cboHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLop)
                    .addComponent(cboLop, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTinhGPA, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        tblKetQua.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblKetQua.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã SV", "Họ và Tên", "Lớp", "Học Kỳ", "TC ĐK", "TC Đạt", "GPA Học Kỳ", "Xếp Loại Kỳ", "Tổng TC TL", "GPA Tích Lũy", "Xếp Loại TL"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblKetQua.setRowHeight(24);
        tblKetQua.setSelectionBackground(new java.awt.Color(37, 99, 235));
        tblKetQua.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblKetQua.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblKetQua.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblKetQuaMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblKetQua);

        pnlChiTiet.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chi tiết sinh viên & Hành động nhanh", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblChiTietSV.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblChiTietSV.setForeground(new java.awt.Color(30, 58, 138));
        lblChiTietSV.setText("Sinh viên: (Chọn một dòng trên bảng để xem chi tiết)");

        lblThongKeNhanh.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblThongKeNhanh.setForeground(new java.awt.Color(21, 128, 61));
        lblThongKeNhanh.setText("GPA Học kỳ: -- | Xếp loại kỳ: -- | GPA Tích lũy: -- | Xếp loại toàn khóa: --");

        btnTinhLaiSV.setBackground(new java.awt.Color(37, 99, 235));
        btnTinhLaiSV.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTinhLaiSV.setForeground(new java.awt.Color(255, 255, 255));
        btnTinhLaiSV.setText("Tính lại GPA cho SV này");
        btnTinhLaiSV.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTinhLaiSV.setFocusPainted(false);
        btnTinhLaiSV.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTinhLaiSVActionPerformed(evt);
            }
        });

        btnXemBangDiem.setBackground(new java.awt.Color(30, 58, 138));
        btnXemBangDiem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXemBangDiem.setForeground(new java.awt.Color(255, 255, 255));
        btnXemBangDiem.setText("Xem bảng điểm chi tiết");
        btnXemBangDiem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXemBangDiem.setFocusPainted(false);
        btnXemBangDiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXemBangDiemActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlChiTietLayout = new javax.swing.GroupLayout(pnlChiTiet);
        pnlChiTiet.setLayout(pnlChiTietLayout);
        pnlChiTietLayout.setHorizontalGroup(
            pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChiTietLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblChiTietSV, javax.swing.GroupLayout.PREFERRED_SIZE, 450, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblThongKeNhanh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(btnTinhLaiSV, javax.swing.GroupLayout.PREFERRED_SIZE, 190, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnXemBangDiem, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );
        pnlChiTietLayout.setVerticalGroup(
            pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChiTietLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblChiTietSV)
                    .addComponent(btnTinhLaiSV, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXemBangDiem, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(lblThongKeNhanh)
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
                    .addComponent(jScrollPane1)
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlChiTiet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cboHocKyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboHocKyActionPerformed
        if (!isDataReady) {
            return;
        }
        taiDanhSachKetQua();
    }//GEN-LAST:event_cboHocKyActionPerformed

    private void cboLopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLopActionPerformed
        if (!isDataReady) {
            return;
        }
        taiDanhSachKetQua();
    }//GEN-LAST:event_cboLopActionPerformed

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocActionPerformed
        taiDanhSachKetQua();
    }//GEN-LAST:event_btnLocActionPerformed

    private void btnTinhGPAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTinhGPAActionPerformed
        HocKy hk = (HocKy) cboHocKy.getSelectedItem();
        if (hk == null || hk.getHocKyId() <= 0) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một học kỳ cụ thể trong ComboBox 'Học kỳ' để tính toán GPA hàng loạt!",
                    "Chưa chọn học kỳ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn tự động tính toán và cập nhật lại GPA\n"
                + "cho tất cả sinh viên có điểm trong " + hk.getTenHocKy() + "?",
                "Xác nhận tính toán GPA",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            int count = ketQuaDAO.calculateAndSaveForSemester(hk.getHocKyId());
            JOptionPane.showMessageDialog(this,
                    "Đã tính toán và cập nhật thành công kết quả học tập cho " + count + " sinh viên!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            taiDanhSachKetQua();
        }
    }//GEN-LAST:event_btnTinhGPAActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        if (cboHocKy.getItemCount() > 0) cboHocKy.setSelectedIndex(0);
        if (cboLop.getItemCount() > 0) cboLop.setSelectedIndex(0);
        taiDanhSachKetQua();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnDongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDongActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnDongActionPerformed

    private void tblKetQuaMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblKetQuaMouseClicked
        int selectedRow = tblKetQua.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= danhSachKetQua.size()) {
            return;
        }

        selectedKetQua = danhSachKetQua.get(selectedRow);
        lblChiTietSV.setText("Sinh viên: " + selectedKetQua.getMaSinhVien()
                + " - " + selectedKetQua.getHoTenSinhVien()
                + " (Lớp: " + (selectedKetQua.getTenLop() != null ? selectedKetQua.getTenLop() : "") + ")");

        String gpaHk = (selectedKetQua.getGpaHocKy() != null) ? String.valueOf(selectedKetQua.getGpaHocKy()) : "--";
        String gpaTl = (selectedKetQua.getGpaTichLuy() != null) ? String.valueOf(selectedKetQua.getGpaTichLuy()) : "--";

        lblThongKeNhanh.setText("GPA Học kỳ: " + gpaHk + " (" + selectedKetQua.getXepLoaiHocKy() + ")"
                + " | GPA Tích lũy: " + gpaTl + " (" + selectedKetQua.getXepLoaiTichLuy() + ")");
    }//GEN-LAST:event_tblKetQuaMouseClicked

    private void btnTinhLaiSVActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTinhLaiSVActionPerformed
        if (selectedKetQua == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một sinh viên trên bảng để tính toán lại GPA!",
                    "Chưa chọn sinh viên", JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean success = ketQuaDAO.calculateAndSave(selectedKetQua.getSinhVienId(), selectedKetQua.getHocKyId());
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Đã tính toán và cập nhật lại GPA cho sinh viên " + selectedKetQua.getHoTenSinhVien() + "!",
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);
            int keepIndex = tblKetQua.getSelectedRow();
            taiDanhSachKetQua();
            if (keepIndex >= 0 && keepIndex < tblKetQua.getRowCount()) {
                tblKetQua.setRowSelectionInterval(keepIndex, keepIndex);
                selectedKetQua = danhSachKetQua.get(keepIndex);
            }
        } else {
            JOptionPane.showMessageDialog(this,
                    "Tính toán GPA không thành công!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnTinhLaiSVActionPerformed

    private void btnXemBangDiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXemBangDiemActionPerformed
        new FrmTraCuuDiem().setVisible(true);
    }//GEN-LAST:event_btnXemBangDiemActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set UTT Theme and Look and Feel */
        com.mycompany.duanjavanangcao.util.UIThemeUtil.applyGlobalTheme();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FrmKetQuaGPA().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDong;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnTinhGPA;
    private javax.swing.JButton btnTinhLaiSV;
    private javax.swing.JButton btnXemBangDiem;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.HocKy> cboHocKy;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.Lop> cboLop;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblChiTietSV;
    private javax.swing.JLabel lblHocKy;
    private javax.swing.JLabel lblLop;
    private javax.swing.JLabel lblThongKeNhanh;
    private javax.swing.JLabel lblTieuDe;
    private javax.swing.JPanel pnlBoLoc;
    private javax.swing.JPanel pnlChiTiet;
    private javax.swing.JTable tblKetQua;
    // End of variables declaration//GEN-END:variables
}


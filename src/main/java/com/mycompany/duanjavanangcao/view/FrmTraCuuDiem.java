package com.mycompany.duanjavanangcao.view;

import com.mycompany.duanjavanangcao.dao.BangDiemDAO;
import com.mycompany.duanjavanangcao.dao.HocKyDAO;
import com.mycompany.duanjavanangcao.dao.SinhVienDAO;
import com.mycompany.duanjavanangcao.model.BangDiem;
import com.mycompany.duanjavanangcao.model.HocKy;
import com.mycompany.duanjavanangcao.model.SinhVien;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.util.SessionManager;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Tra cứu bảng điểm cá nhân của sinh viên
 */
public class FrmTraCuuDiem extends javax.swing.JFrame {

    private final BangDiemDAO bangDiemDAO = new BangDiemDAO();
    private final SinhVienDAO sinhVienDAO = new SinhVienDAO();
    private final HocKyDAO hocKyDAO = new HocKyDAO();

    private List<BangDiem> danhSachDiem = new ArrayList<>();
    private SinhVien currentSinhVien = null;
    private boolean isDataReady = false;

    public FrmTraCuuDiem() {
        initComponents();
        getContentPane().setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.BG_LIGHT);
        pnlThongTin.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlTongKet.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatTable(tblBangDiem);
        this.setLocationRelativeTo(null);
        khoiTaoDuLieu();
    }

    private void khoiTaoDuLieu() {
        // 1. Nạp học kỳ
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

        // 2. Nạp sinh viên theo phân quyền
        TaiKhoan currentUser = SessionManager.getCurrentUser();
        DefaultComboBoxModel<SinhVien> modelSv = new DefaultComboBoxModel<>();

        if (currentUser != null && "SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro())) {
            // Sinh viên đăng nhập: chỉ thấy thông tin của chính mình
            SinhVien sv = sinhVienDAO.findByTaiKhoanId(currentUser.getTaiKhoanId());
            if (sv != null) {
                currentSinhVien = sv;
                modelSv.addElement(sv);
                cboSinhVien.setModel(modelSv);
                cboSinhVien.setEnabled(false);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không tìm thấy hồ sơ sinh viên tương ứng với tài khoản này!",
                        "Cảnh báo", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            // Quản trị viên (ADMIN) hoặc Giảng viên: chọn bất kỳ sinh viên nào
            List<SinhVien> listSv = sinhVienDAO.findAll();
            for (SinhVien sv : listSv) {
                modelSv.addElement(sv);
            }
            cboSinhVien.setModel(modelSv);
            cboSinhVien.setEnabled(true);
            if (!listSv.isEmpty()) {
                currentSinhVien = listSv.get(0);
            }
        }

        isDataReady = true;
        taiBangDiem();
    }

    private void taiBangDiem() {
        if (!isDataReady || currentSinhVien == null) {
            DefaultTableModel model = (DefaultTableModel) tblBangDiem.getModel();
            model.setRowCount(0);
            resetThongKe();
            return;
        }

        HocKy selectedHk = (HocKy) cboHocKy.getSelectedItem();
        int hocKyId = (selectedHk != null) ? selectedHk.getHocKyId() : 0;

        danhSachDiem = bangDiemDAO.findBySinhVienAndHocKy(currentSinhVien.getSinhVienId(), hocKyId);

        DefaultTableModel model = (DefaultTableModel) tblBangDiem.getModel();
        model.setRowCount(0);

        int stt = 1;
        int tongTinChi = 0;
        int tinChiDat = 0;
        double tongDiemHe10NhanTC = 0;
        double tongDiemHe4NhanTC = 0;
        int tongTCTinhDiem = 0;

        for (BangDiem bd : danhSachDiem) {
            String cc = (bd.getDiemCC() != null) ? String.valueOf(bd.getDiemCC()) : "";
            String gk = (bd.getDiemGK() != null) ? String.valueOf(bd.getDiemGK()) : "";
            String ck = (bd.getDiemCK() != null) ? String.valueOf(bd.getDiemCK()) : "";
            String tk = (bd.getDiemTongKet() != null) ? String.valueOf(bd.getDiemTongKet()) : "";
            String dc = (bd.getDiemChu() != null) ? bd.getDiemChu() : "";
            String h4 = (bd.getDiemHe4() != null) ? String.valueOf(bd.getDiemHe4()) : "";
            String gv = (bd.getTenGiangVien() != null) ? bd.getTenGiangVien() : "Chưa rõ";
            String kq = (bd.getKetQua() != null) ? bd.getKetQua() : "";

            int tc = bd.getSoTinChi();
            tongTinChi += tc;

            if (bd.getDiemTongKet() != null) {
                if (bd.getDiemTongKet() >= 4.0) {
                    tinChiDat += tc;
                }
                tongDiemHe10NhanTC += bd.getDiemTongKet() * tc;
                if (bd.getDiemHe4() != null) {
                    tongDiemHe4NhanTC += bd.getDiemHe4() * tc;
                }
                tongTCTinhDiem += tc;
            }

            model.addRow(new Object[]{
                stt++,
                bd.getMaLopHocPhan(),
                bd.getTenMonHoc(),
                bd.getSoTinChi(),
                gv,
                cc,
                gk,
                ck,
                tk,
                dc,
                h4,
                kq
            });
        }

        // Tính toán thống kê GPA & Xếp loại
        lblTongTinChi.setText("Tổng tín chỉ: " + tongTinChi + " TC");
        lblTinChiDat.setText("Tín chỉ đạt: " + tinChiDat + " TC");

        if (tongTCTinhDiem > 0) {
            double gpa10 = Math.round((tongDiemHe10NhanTC / tongTCTinhDiem) * 100.0) / 100.0;
            double gpa4 = Math.round((tongDiemHe4NhanTC / tongTCTinhDiem) * 100.0) / 100.0;

            lblGPA10.setText("Điểm TB (Hệ 10): " + gpa10);
            lblGPA4.setText("GPA (Hệ 4): " + gpa4);
            lblXepLoai.setText("Xếp loại: " + xepLoaiHocLuc(gpa4));
        } else {
            lblGPA10.setText("Điểm TB (Hệ 10): --");
            lblGPA4.setText("GPA (Hệ 4): --");
            lblXepLoai.setText("Xếp loại: --");
        }
    }

    private String xepLoaiHocLuc(double gpa4) {
        if (gpa4 >= 3.6) return "Xuất sắc";
        if (gpa4 >= 3.2) return "Giỏi";
        if (gpa4 >= 2.5) return "Khá";
        if (gpa4 >= 2.0) return "Trung bình";
        if (gpa4 >= 1.0) return "Yếu";
        return "Kém";
    }

    private void resetThongKe() {
        lblTongTinChi.setText("Tổng tín chỉ: 0 TC");
        lblTinChiDat.setText("Tín chỉ đạt: 0 TC");
        lblGPA10.setText("Điểm TB (Hệ 10): 0.0");
        lblGPA4.setText("GPA (Hệ 4): 0.0");
        lblXepLoai.setText("Xếp loại: --");
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
        pnlThongTin = new javax.swing.JPanel();
        lblSinhVien = new javax.swing.JLabel();
        cboSinhVien = new javax.swing.JComboBox<>();
        lblHocKy = new javax.swing.JLabel();
        cboHocKy = new javax.swing.JComboBox<>();
        btnTraCuu = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnDong = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBangDiem = new javax.swing.JTable();
        pnlTongKet = new javax.swing.JPanel();
        lblTongTinChi = new javax.swing.JLabel();
        lblTinChiDat = new javax.swing.JLabel();
        lblGPA10 = new javax.swing.JLabel();
        lblGPA4 = new javax.swing.JLabel();
        lblXepLoai = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("TRA CỨU BẢNG ĐIỂM SINH VIÊN");

        lblTieuDe.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTieuDe.setForeground(new java.awt.Color(30, 58, 138));
        lblTieuDe.setText("TRA CỨU BẢNG ĐIỂM SINH VIÊN");

        pnlThongTin.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông tin tra cứu", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

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

        btnTraCuu.setBackground(new java.awt.Color(37, 99, 235));
        btnTraCuu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTraCuu.setForeground(new java.awt.Color(255, 255, 255));
        btnTraCuu.setText("Tra cứu");
        btnTraCuu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTraCuu.setFocusPainted(false);
        btnTraCuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTraCuuActionPerformed(evt);
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

        javax.swing.GroupLayout pnlThongTinLayout = new javax.swing.GroupLayout(pnlThongTin);
        pnlThongTin.setLayout(pnlThongTinLayout);
        pnlThongTinLayout.setHorizontalGroup(
            pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongTinLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblSinhVien)
                .addGap(8, 8, 8)
                .addComponent(cboSinhVien, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblHocKy)
                .addGap(8, 8, 8)
                .addComponent(cboHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(btnTraCuu, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        pnlThongTinLayout.setVerticalGroup(
            pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongTinLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlThongTinLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSinhVien)
                    .addComponent(cboSinhVien, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHocKy)
                    .addComponent(cboHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTraCuu, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        tblBangDiem.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblBangDiem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã LHP", "Tên Môn Học", "Số TC", "Giảng Viên", "Điểm CC (20%)", "Điểm GK (30%)", "Điểm CK (50%)", "Tổng Kết", "Điểm Chữ", "Hệ 4", "Kết Quả"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBangDiem.setRowHeight(24);
        tblBangDiem.setSelectionBackground(new java.awt.Color(37, 99, 235));
        tblBangDiem.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblBangDiem.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tblBangDiem);

        pnlTongKet.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Tổng kết học tập", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblTongTinChi.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTongTinChi.setForeground(new java.awt.Color(31, 41, 55));
        lblTongTinChi.setText("Tổng tín chỉ: 0 TC");

        lblTinChiDat.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblTinChiDat.setForeground(new java.awt.Color(21, 128, 61));
        lblTinChiDat.setText("Tín chỉ đạt: 0 TC");

        lblGPA10.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblGPA10.setForeground(new java.awt.Color(37, 99, 235));
        lblGPA10.setText("Điểm TB (Hệ 10): 0.0");

        lblGPA4.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblGPA4.setForeground(new java.awt.Color(37, 99, 235));
        lblGPA4.setText("GPA (Hệ 4): 0.0");

        lblXepLoai.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblXepLoai.setForeground(new java.awt.Color(21, 128, 61));
        lblXepLoai.setText("Xếp loại: --");

        javax.swing.GroupLayout pnlTongKetLayout = new javax.swing.GroupLayout(pnlTongKet);
        pnlTongKet.setLayout(pnlTongKetLayout);
        pnlTongKetLayout.setHorizontalGroup(
            pnlTongKetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTongKetLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblTongTinChi, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblTinChiDat, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblGPA10, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblGPA4, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblXepLoai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );
        pnlTongKetLayout.setVerticalGroup(
            pnlTongKetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTongKetLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlTongKetLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTongTinChi)
                    .addComponent(lblTinChiDat)
                    .addComponent(lblGPA10)
                    .addComponent(lblGPA4)
                    .addComponent(lblXepLoai))
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
                    .addComponent(pnlThongTin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1)
                    .addComponent(pnlTongKet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblTieuDe)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlThongTin, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 260, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlTongKet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        pnlTongKet.getAccessibleContext().setAccessibleName("Tổng kết học tập");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cboSinhVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboSinhVienActionPerformed
        if (!isDataReady) {
            return;
        }
        currentSinhVien = (SinhVien) cboSinhVien.getSelectedItem();
        taiBangDiem();
    }//GEN-LAST:event_cboSinhVienActionPerformed

    private void cboHocKyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboHocKyActionPerformed
        if (!isDataReady) {
            return;
        }
        taiBangDiem();
    }//GEN-LAST:event_cboHocKyActionPerformed

    private void btnTraCuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTraCuuActionPerformed
        taiBangDiem();
    }//GEN-LAST:event_btnTraCuuActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        if (cboHocKy.getItemCount() > 0) {
            cboHocKy.setSelectedIndex(0);
        }
        taiBangDiem();
    }//GEN-LAST:event_btnLamMoiActionPerformed

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
            java.util.logging.Logger.getLogger(FrmTraCuuDiem.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FrmTraCuuDiem().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDong;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnTraCuu;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.HocKy> cboHocKy;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.SinhVien> cboSinhVien;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblGPA10;
    private javax.swing.JLabel lblGPA4;
    private javax.swing.JLabel lblHocKy;
    private javax.swing.JLabel lblSinhVien;
    private javax.swing.JLabel lblTieuDe;
    private javax.swing.JLabel lblTinChiDat;
    private javax.swing.JLabel lblTongTinChi;
    private javax.swing.JLabel lblXepLoai;
    private javax.swing.JPanel pnlThongTin;
    private javax.swing.JPanel pnlTongKet;
    private javax.swing.JTable tblBangDiem;
    // End of variables declaration//GEN-END:variables
}

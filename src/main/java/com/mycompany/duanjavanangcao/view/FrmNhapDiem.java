package com.mycompany.duanjavanangcao.view;

import com.mycompany.duanjavanangcao.dao.BangDiemDAO;
import com.mycompany.duanjavanangcao.dao.GiangVienDAO;
import com.mycompany.duanjavanangcao.dao.HocKyDAO;
import com.mycompany.duanjavanangcao.dao.LichSuDiemDAO;
import com.mycompany.duanjavanangcao.dao.LopHocPhanDAO;
import com.mycompany.duanjavanangcao.model.BangDiem;
import com.mycompany.duanjavanangcao.model.GiangVien;
import com.mycompany.duanjavanangcao.model.HocKy;
import com.mycompany.duanjavanangcao.model.LichSuDiem;
import com.mycompany.duanjavanangcao.model.LopHocPhan;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.util.DiemUtil;
import com.mycompany.duanjavanangcao.util.SessionManager;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Màn hình Nhập điểm học phần dành cho Giảng viên và Quản trị viên
 */
public class FrmNhapDiem extends javax.swing.JFrame {

    private final BangDiemDAO bangDiemDAO = new BangDiemDAO();
    private final LopHocPhanDAO lopHocPhanDAO = new LopHocPhanDAO();
    private final HocKyDAO hocKyDAO = new HocKyDAO();
    private final GiangVienDAO giangVienDAO = new GiangVienDAO();
    private final LichSuDiemDAO lichSuDAO = new LichSuDiemDAO();

    private List<BangDiem> danhSachBangDiem = new ArrayList<>();
    private BangDiem selectedBangDiem = null;
    private boolean isDataReady = false;

    public FrmNhapDiem() {
        initComponents();
        getContentPane().setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.BG_LIGHT);
        pnlBoLoc.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlNhapDiem.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatTable(tblBangDiem);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.applyThemeToContainer(this);
        this.setLocationRelativeTo(null);
        khoiTaoDuLieu();
    }

    private void khoiTaoDuLieu() {
        // 1. Nạp danh sách học kỳ
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

        // 2. Nạp danh sách lớp học phần theo phân quyền
        napDanhSachLopHocPhan();

        isDataReady = true;
        taiDanhSachBangDiem();
    }

    private void napDanhSachLopHocPhan() {
        TaiKhoan currentUser = SessionManager.getCurrentUser();
        List<LopHocPhan> listLhp;

        if (currentUser != null && "GIANGVIEN".equalsIgnoreCase(currentUser.getVaiTro())) {
            // Giảng viên đăng nhập: chỉ tải các lớp do chính mình phụ trách
            GiangVien gv = giangVienDAO.findByTaiKhoanId(currentUser.getTaiKhoanId());
            if (gv != null) {
                listLhp = lopHocPhanDAO.findByGiangVien(gv.getGiangVienId());
            } else {
                listLhp = new ArrayList<>();
            }
        } else {
            // Quản trị viên (ADMIN): tải toàn bộ các lớp học phần
            listLhp = lopHocPhanDAO.findAll();
        }

        DefaultComboBoxModel<LopHocPhan> modelLhp = new DefaultComboBoxModel<>();
        HocKy selectedHk = (HocKy) cboHocKy.getSelectedItem();
        int selectedHkId = (selectedHk != null) ? selectedHk.getHocKyId() : 0;

        for (LopHocPhan lhp : listLhp) {
            if (selectedHkId <= 0 || lhp.getHocKyId() == selectedHkId) {
                modelLhp.addElement(lhp);
            }
        }
        cboLopHocPhan.setModel(modelLhp);
    }

    private void taiDanhSachBangDiem() {
        if (!isDataReady) {
            return;
        }

        LopHocPhan selectedLhp = (LopHocPhan) cboLopHocPhan.getSelectedItem();
        DefaultTableModel model = (DefaultTableModel) tblBangDiem.getModel();
        model.setRowCount(0);

        if (selectedLhp == null) {
            lblSiSo.setText("Sĩ số: 0 sinh viên");
            xoaFormNhap();
            return;
        }

        danhSachBangDiem = bangDiemDAO.findByLopHocPhan(selectedLhp.getLopHocPhanId());
        lblSiSo.setText("Sĩ số: " + danhSachBangDiem.size() + " sinh viên");

        int stt = 1;
        for (BangDiem bd : danhSachBangDiem) {
            String cc = (bd.getDiemCC() != null) ? String.valueOf(bd.getDiemCC()) : "";
            String gk = (bd.getDiemGK() != null) ? String.valueOf(bd.getDiemGK()) : "";
            String ck = (bd.getDiemCK() != null) ? String.valueOf(bd.getDiemCK()) : "";
            String tk = (bd.getDiemTongKet() != null) ? String.valueOf(bd.getDiemTongKet()) : "";
            String dc = (bd.getDiemChu() != null) ? bd.getDiemChu() : "";
            String h4 = (bd.getDiemHe4() != null) ? String.valueOf(bd.getDiemHe4()) : "";
            String kq = (bd.getKetQua() != null) ? bd.getKetQua() : "";
            String lop = (bd.getTenLop() != null) ? bd.getTenLop() : "";

            model.addRow(new Object[]{
                stt++,
                bd.getMaSinhVien(),
                bd.getHoTenSinhVien(),
                lop,
                cc,
                gk,
                ck,
                tk,
                dc,
                h4,
                kq
            });
        }

        xoaFormNhap();
    }

    private void xoaFormNhap() {
        selectedBangDiem = null;
        lblSinhVienDangChon.setText("Sinh viên: (Vui lòng chọn 1 dòng trên bảng)");
        lblKetQuaPreview.setText("Tổng kết: -- | Điểm chữ: -- | Hệ 4: -- | Kết quả: --");
        txtDiemCC.setText("");
        txtDiemGK.setText("");
        txtDiemCK.setText("");
        txtLyDo.setText("Nhập điểm định kỳ");
        txtGhiChu.setText("");
    }

    private void capNhatXemTruoc() {
        Double cc = parseDiem(txtDiemCC.getText());
        Double gk = parseDiem(txtDiemGK.getText());
        Double ck = parseDiem(txtDiemCK.getText());

        if (cc != null && (cc < 0 || cc > 10) ||
            gk != null && (gk < 0 || gk > 10) ||
            ck != null && (ck < 0 || ck > 10)) {
            lblKetQuaPreview.setText("Điểm số phải từ 0.0 đến 10.0!");
            return;
        }

        Double tk = DiemUtil.tinhDiemTongKet(cc, gk, ck);
        if (tk != null) {
            String dc = DiemUtil.quyDoiDiemChu(tk);
            Double h4 = DiemUtil.quyDoiDiemHe4(tk);
            String kq = DiemUtil.quyDoiKetQua(cc, tk);
            lblKetQuaPreview.setText("Tổng kết: " + tk + " | Điểm chữ: " + dc + " | Hệ 4: " + h4 + " | Kết quả: " + kq);
        } else {
            lblKetQuaPreview.setText("Tổng kết: -- | Điểm chữ: -- | Hệ 4: -- | Kết quả: --");
        }
    }

    private Double parseDiem(String str) {
        if (str == null || str.trim().isEmpty()) {
            return null;
        }
        try {
            return Double.parseDouble(str.trim().replace(',', '.'));
        } catch (NumberFormatException e) {
            return null;
        }
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
        lblLopHocPhan = new javax.swing.JLabel();
        cboLopHocPhan = new javax.swing.JComboBox<>();
        btnTaiDanhSach = new javax.swing.JButton();
        lblSiSo = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblBangDiem = new javax.swing.JTable();
        pnlNhapDiem = new javax.swing.JPanel();
        lblSinhVienDangChon = new javax.swing.JLabel();
        lblKetQuaPreview = new javax.swing.JLabel();
        lblDiemCC = new javax.swing.JLabel();
        txtDiemCC = new javax.swing.JTextField();
        lblDiemGK = new javax.swing.JLabel();
        txtDiemGK = new javax.swing.JTextField();
        lblDiemCK = new javax.swing.JLabel();
        txtDiemCK = new javax.swing.JTextField();
        lblLyDo = new javax.swing.JLabel();
        txtLyDo = new javax.swing.JTextField();
        lblGhiChu = new javax.swing.JLabel();
        txtGhiChu = new javax.swing.JTextField();
        btnLuuDiem = new javax.swing.JButton();
        btnXemLichSu = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnDong = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("QUẢN LÝ VÀ NHẬP ĐIỂM HỌC PHẦN");

        lblTieuDe.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTieuDe.setForeground(new java.awt.Color(30, 58, 138));
        lblTieuDe.setText("QUẢN LÝ VÀ NHẬP ĐIỂM HỌC PHẦN");

        pnlBoLoc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chọn Lớp học phần cần vào điểm", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblHocKy.setText("Học Kỳ:");

        cboHocKy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboHocKyActionPerformed(evt);
            }
        });

        lblLopHocPhan.setText("Lớp Học Phần:");

        cboLopHocPhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLopHocPhanActionPerformed(evt);
            }
        });

        btnTaiDanhSach.setBackground(new java.awt.Color(37, 99, 235));
        btnTaiDanhSach.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTaiDanhSach.setForeground(new java.awt.Color(255, 255, 255));
        btnTaiDanhSach.setText("Tải danh sách");
        btnTaiDanhSach.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTaiDanhSach.setFocusPainted(false);
        btnTaiDanhSach.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTaiDanhSachActionPerformed(evt);
            }
        });

        lblSiSo.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        lblSiSo.setForeground(new java.awt.Color(21, 128, 61));
        lblSiSo.setText("Sĩ số: 0 sinh viên");

        javax.swing.GroupLayout pnlBoLocLayout = new javax.swing.GroupLayout(pnlBoLoc);
        pnlBoLoc.setLayout(pnlBoLocLayout);
        pnlBoLocLayout.setHorizontalGroup(
            pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoLocLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addComponent(lblHocKy)
                .addGap(8, 8, 8)
                .addComponent(cboHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblLopHocPhan)
                .addGap(8, 8, 8)
                .addComponent(cboLopHocPhan, javax.swing.GroupLayout.PREFERRED_SIZE, 340, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(btnTaiDanhSach, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(lblSiSo, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );
        pnlBoLocLayout.setVerticalGroup(
            pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoLocLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHocKy)
                    .addComponent(cboHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLopHocPhan)
                    .addComponent(cboLopHocPhan, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnTaiDanhSach, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSiSo))
                .addGap(10, 10, 10))
        );

        tblBangDiem.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblBangDiem.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã SV", "Họ và Tên", "Lớp", "Điểm CC (20%)", "Điểm GK (30%)", "Điểm CK (50%)", "Tổng Kết", "Điểm Chữ", "Hệ 4", "Kết Quả"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblBangDiem.setRowHeight(24);
        tblBangDiem.setSelectionBackground(new java.awt.Color(37, 99, 235));
        tblBangDiem.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblBangDiem.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblBangDiem.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblBangDiemMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblBangDiem);

        pnlNhapDiem.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chi tiết & Cập nhật điểm sinh viên", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblSinhVienDangChon.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblSinhVienDangChon.setForeground(new java.awt.Color(30, 58, 138));
        lblSinhVienDangChon.setText("Sinh viên: (Vui lòng chọn 1 dòng trên bảng)");

        lblKetQuaPreview.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblKetQuaPreview.setForeground(new java.awt.Color(21, 128, 61));
        lblKetQuaPreview.setText("Tổng kết: -- | Điểm chữ: -- | Hệ 4: -- | Kết quả: --");

        lblDiemCC.setText("Điểm CC (20%):");

        txtDiemCC.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDiemKeyReleased(evt);
            }
        });

        lblDiemGK.setText("Điểm GK (30%):");

        txtDiemGK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDiemKeyReleased(evt);
            }
        });

        lblDiemCK.setText("Điểm CK (50%):");

        txtDiemCK.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtDiemKeyReleased(evt);
            }
        });

        lblLyDo.setText("Lý do:");

        txtLyDo.setText("Nhập điểm định kỳ");

        lblGhiChu.setText("Ghi chú:");

        btnLuuDiem.setBackground(new java.awt.Color(21, 128, 61));
        btnLuuDiem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuuDiem.setForeground(new java.awt.Color(255, 255, 255));
        btnLuuDiem.setText("LƯU ĐIỂM");
        btnLuuDiem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLuuDiem.setFocusPainted(false);
        btnLuuDiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuDiemActionPerformed(evt);
            }
        });

        btnXemLichSu.setBackground(new java.awt.Color(37, 99, 235));
        btnXemLichSu.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXemLichSu.setForeground(new java.awt.Color(255, 255, 255));
        btnXemLichSu.setText("Xem lịch sử sửa điểm");
        btnXemLichSu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXemLichSu.setFocusPainted(false);
        btnXemLichSu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXemLichSuActionPerformed(evt);
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

        javax.swing.GroupLayout pnlNhapDiemLayout = new javax.swing.GroupLayout(pnlNhapDiem);
        pnlNhapDiem.setLayout(pnlNhapDiemLayout);
        pnlNhapDiemLayout.setHorizontalGroup(
            pnlNhapDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNhapDiemLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlNhapDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlNhapDiemLayout.createSequentialGroup()
                        .addComponent(lblSinhVienDangChon, javax.swing.GroupLayout.PREFERRED_SIZE, 400, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblKetQuaPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(pnlNhapDiemLayout.createSequentialGroup()
                        .addComponent(lblDiemCC)
                        .addGap(6, 6, 6)
                        .addComponent(txtDiemCC, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(lblDiemGK)
                        .addGap(6, 6, 6)
                        .addComponent(txtDiemGK, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(lblDiemCK)
                        .addGap(6, 6, 6)
                        .addComponent(txtDiemCK, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(20, 20, 20)
                        .addComponent(lblLyDo)
                        .addGap(6, 6, 6)
                        .addComponent(txtLyDo, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE))
                    .addGroup(pnlNhapDiemLayout.createSequentialGroup()
                        .addComponent(lblGhiChu)
                        .addGap(6, 6, 6)
                        .addComponent(txtGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, Short.MAX_VALUE)
                        .addComponent(btnLuuDiem, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnXemLichSu, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );
        pnlNhapDiemLayout.setVerticalGroup(
            pnlNhapDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNhapDiemLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlNhapDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblSinhVienDangChon)
                    .addComponent(lblKetQuaPreview))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlNhapDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblDiemCC)
                    .addComponent(txtDiemCC, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDiemGK)
                    .addComponent(txtDiemGK, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDiemCK)
                    .addComponent(txtDiemCK, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLyDo)
                    .addComponent(txtLyDo, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlNhapDiemLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblGhiChu)
                    .addComponent(txtGhiChu, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLuuDiem, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXemLichSu, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12))
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
                    .addComponent(pnlNhapDiem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlNhapDiem, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cboHocKyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboHocKyActionPerformed
        if (!isDataReady) {
            return;
        }
        napDanhSachLopHocPhan();
        taiDanhSachBangDiem();
    }//GEN-LAST:event_cboHocKyActionPerformed

    private void cboLopHocPhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLopHocPhanActionPerformed
        if (!isDataReady) {
            return;
        }
        taiDanhSachBangDiem();
    }//GEN-LAST:event_cboLopHocPhanActionPerformed

    private void btnTaiDanhSachActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTaiDanhSachActionPerformed
        taiDanhSachBangDiem();
    }//GEN-LAST:event_btnTaiDanhSachActionPerformed

    private void tblBangDiemMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblBangDiemMouseClicked
        int selectedRow = tblBangDiem.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= danhSachBangDiem.size()) {
            return;
        }

        selectedBangDiem = danhSachBangDiem.get(selectedRow);
        lblSinhVienDangChon.setText("Sinh viên: " + selectedBangDiem.getMaSinhVien()
                + " - " + selectedBangDiem.getHoTenSinhVien()
                + " (Lớp: " + (selectedBangDiem.getTenLop() != null ? selectedBangDiem.getTenLop() : "") + ")");

        txtDiemCC.setText(selectedBangDiem.getDiemCC() != null ? String.valueOf(selectedBangDiem.getDiemCC()) : "");
        txtDiemGK.setText(selectedBangDiem.getDiemGK() != null ? String.valueOf(selectedBangDiem.getDiemGK()) : "");
        txtDiemCK.setText(selectedBangDiem.getDiemCK() != null ? String.valueOf(selectedBangDiem.getDiemCK()) : "");
        txtGhiChu.setText(selectedBangDiem.getGhiChu() != null ? selectedBangDiem.getGhiChu() : "");
        txtLyDo.setText("Nhập điểm định kỳ");

        capNhatXemTruoc();
    }//GEN-LAST:event_tblBangDiemMouseClicked

    private void txtDiemKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtDiemKeyReleased
        capNhatXemTruoc();
    }//GEN-LAST:event_txtDiemKeyReleased

    private void btnLuuDiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuDiemActionPerformed
        if (selectedBangDiem == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một sinh viên trên bảng để nhập hoặc sửa điểm!",
                    "Chưa chọn sinh viên", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String strCC = txtDiemCC.getText().trim();
        String strGK = txtDiemGK.getText().trim();
        String strCK = txtDiemCK.getText().trim();

        Double cc = parseDiem(strCC);
        Double gk = parseDiem(strGK);
        Double ck = parseDiem(strCK);

        if (!strCC.isEmpty() && (cc == null || cc < 0.0 || cc > 10.0)) {
            JOptionPane.showMessageDialog(this, "Điểm chuyên cần không hợp lệ! Vui lòng nhập từ 0.0 đến 10.0", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtDiemCC.requestFocus();
            return;
        }

        if (!strGK.isEmpty() && (gk == null || gk < 0.0 || gk > 10.0)) {
            JOptionPane.showMessageDialog(this, "Điểm giữa kỳ không hợp lệ! Vui lòng nhập từ 0.0 đến 10.0", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtDiemGK.requestFocus();
            return;
        }

        if (!strCK.isEmpty() && (ck == null || ck < 0.0 || ck > 10.0)) {
            JOptionPane.showMessageDialog(this, "Điểm cuối kỳ không hợp lệ! Vui lòng nhập từ 0.0 đến 10.0", "Lỗi nhập liệu", JOptionPane.ERROR_MESSAGE);
            txtDiemCK.requestFocus();
            return;
        }

        selectedBangDiem.setDiemCC(cc);
        selectedBangDiem.setDiemGK(gk);
        selectedBangDiem.setDiemCK(ck);
        selectedBangDiem.setGhiChu(txtGhiChu.getText().trim());

        TaiKhoan currentUser = SessionManager.getCurrentUser();
        int nguoiCapNhatId = (currentUser != null) ? currentUser.getTaiKhoanId() : 1;
        String lyDo = txtLyDo.getText().trim();

        boolean success = bangDiemDAO.updateDiem(selectedBangDiem, nguoiCapNhatId, lyDo);
        if (success) {
            JOptionPane.showMessageDialog(this,
                    "Cập nhật điểm thành công cho sinh viên: " + selectedBangDiem.getHoTenSinhVien() + "!",
                    "Thông báo", JOptionPane.INFORMATION_MESSAGE);
            int keepIndex = tblBangDiem.getSelectedRow();
            taiDanhSachBangDiem();
            if (keepIndex >= 0 && keepIndex < tblBangDiem.getRowCount()) {
                tblBangDiem.setRowSelectionInterval(keepIndex, keepIndex);
                selectedBangDiem = danhSachBangDiem.get(keepIndex);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Cập nhật điểm thất bại, vui lòng kiểm tra kết nối!", "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnLuuDiemActionPerformed

    private void btnXemLichSuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXemLichSuActionPerformed
        if (selectedBangDiem == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một sinh viên trên bảng để xem lịch sử chỉnh sửa điểm!",
                    "Chưa chọn sinh viên", JOptionPane.WARNING_MESSAGE);
            return;
        }

        List<LichSuDiem> list = lichSuDAO.findByBangDiem(selectedBangDiem.getBangDiemId());
        if (list.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Sinh viên này chưa có lượt chỉnh sửa điểm nào được ghi nhận!",
                    "Lịch sử điểm", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        StringBuilder sb = new StringBuilder();
        sb.append("LỊCH SỬ CHỈNH SỬA ĐIỂM - SINH VIÊN: ")
          .append(selectedBangDiem.getMaSinhVien()).append(" - ").append(selectedBangDiem.getHoTenSinhVien())
          .append("\n------------------------------------------------------------\n");

        int count = 1;
        for (LichSuDiem lsd : list) {
            String time = (lsd.getThoiGianThayDoi() != null) ? sdf.format(lsd.getThoiGianThayDoi()) : "Chưa rõ";
            sb.append(count++).append(". Thời gian: ").append(time)
              .append(" | Người sửa: ").append(lsd.getTenNguoiThayDoi() != null ? lsd.getTenNguoiThayDoi() : "Admin").append("\n")
              .append("   - Điểm cũ : CC=").append(lsd.getDiemCCCu()).append(", GK=").append(lsd.getDiemGKCu())
              .append(", CK=").append(lsd.getDiemCKCu()).append(" => TK=").append(lsd.getDiemTongKetCu())
              .append(" (").append(lsd.getDiemChuCu()).append(")\n")
              .append("   - Điểm mới: CC=").append(lsd.getDiemCCMoi()).append(", GK=").append(lsd.getDiemGKMoi())
              .append(", CK=").append(lsd.getDiemCKMoi()).append(" => TK=").append(lsd.getDiemTongKetMoi())
              .append(" (").append(lsd.getDiemChuMoi()).append(")\n")
              .append("   - Lý do   : ").append(lsd.getLyDo()).append("\n\n");
        }

        javax.swing.JTextArea textArea = new javax.swing.JTextArea(sb.toString());
        textArea.setEditable(false);
        textArea.setFont(new java.awt.Font("Monospaced", java.awt.Font.PLAIN, 12));
        javax.swing.JScrollPane scrollPane = new javax.swing.JScrollPane(textArea);
        scrollPane.setPreferredSize(new java.awt.Dimension(580, 320));

        JOptionPane.showMessageDialog(this, scrollPane, "Lịch sử chỉnh sửa điểm", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnXemLichSuActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        taiDanhSachBangDiem();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnDongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDongActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnDongActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set UTT Theme and Look and Feel */
        com.mycompany.duanjavanangcao.util.UIThemeUtil.applyGlobalTheme();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FrmNhapDiem().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDong;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnLuuDiem;
    private javax.swing.JButton btnTaiDanhSach;
    private javax.swing.JButton btnXemLichSu;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.HocKy> cboHocKy;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.LopHocPhan> cboLopHocPhan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblDiemCC;
    private javax.swing.JLabel lblDiemCK;
    private javax.swing.JLabel lblDiemGK;
    private javax.swing.JLabel lblGhiChu;
    private javax.swing.JLabel lblHocKy;
    private javax.swing.JLabel lblKetQuaPreview;
    private javax.swing.JLabel lblLopHocPhan;
    private javax.swing.JLabel lblLyDo;
    private javax.swing.JLabel lblSiSo;
    private javax.swing.JLabel lblSinhVienDangChon;
    private javax.swing.JLabel lblTieuDe;
    private javax.swing.JPanel pnlBoLoc;
    private javax.swing.JPanel pnlNhapDiem;
    private javax.swing.JTable tblBangDiem;
    private javax.swing.JTextField txtDiemCC;
    private javax.swing.JTextField txtDiemCK;
    private javax.swing.JTextField txtDiemGK;
    private javax.swing.JTextField txtGhiChu;
    private javax.swing.JTextField txtLyDo;
    // End of variables declaration//GEN-END:variables
}


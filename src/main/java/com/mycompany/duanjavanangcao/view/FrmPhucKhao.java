package com.mycompany.duanjavanangcao.view;

import com.mycompany.duanjavanangcao.dao.PhucKhaoDAO;
import com.mycompany.duanjavanangcao.dao.SinhVienDAO;
import com.mycompany.duanjavanangcao.model.BangDiem;
import com.mycompany.duanjavanangcao.model.PhucKhao;
import com.mycompany.duanjavanangcao.model.SinhVien;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Form Quản lý Phúc khảo Điểm học phần - Thiết kế chuẩn NetBeans Swing GUI Builder
 */
public class FrmPhucKhao extends javax.swing.JFrame {

    private final PhucKhaoDAO phucKhaoDAO;
    private final SinhVienDAO sinhVienDAO;
    private SinhVien currentSinhVien;
    private TaiKhoan currentUser;
    private List<PhucKhao> danhSachPhucKhao;
    private PhucKhao donDangChon;

    /**
     * Khởi tạo form FrmPhucKhao
     */
    public FrmPhucKhao() {
        initComponents();
        getContentPane().setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.BG_LIGHT);
        pnlBoLoc.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlDanhSach.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlThaoTac.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlGuiDon.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlXuLy.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatTable(tblPhucKhao);
        btnGuiDon.setBackground(new java.awt.Color(242, 140, 40)); // Cam UTT nổi bật
        this.setLocationRelativeTo(null);
        this.phucKhaoDAO = new PhucKhaoDAO();
        this.sinhVienDAO = new SinhVienDAO();
        this.danhSachPhucKhao = new ArrayList<>();

        khoiTaoDuLieu();
    }

    /**
     * Khởi tạo dữ liệu người dùng, phân quyền và nạp danh sách ban đầu
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

        boolean isSinhVien = "SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro());

        if (isSinhVien) {
            currentSinhVien = sinhVienDAO.findByTaiKhoanId(currentUser.getTaiKhoanId());
            if (currentSinhVien != null) {
                lblTieuDe.setText("PHÚC KHẢO ĐIỂM - SINH VIÊN: " + currentSinhVien.getHoTen()
                        + " (" + currentSinhVien.getMaSinhVien() + ")");
                taiDanhSachMonCoThePhucKhao(currentSinhVien.getSinhVienId());
            }

            // Sinh viên: Bật panel gửi đơn, vô hiệu hóa panel xử lý của cán bộ
            pnlGuiDon.setEnabled(true);
            btnGuiDon.setEnabled(true);
            btnHuyDon.setEnabled(true);

            cboTrangThaiMoi.setEnabled(false);
            txtDiemCKMoi.setEnabled(false);
            txaKetQuaXuLy.setEnabled(false);
            btnLuuXuLy.setEnabled(false);
        } else {
            lblTieuDe.setText("QUẢN LÝ VÀ XỬ LÝ ĐƠN PHÚC KHẢO ĐIỂM (" + currentUser.getVaiTro() + ")");

            // Cán bộ / Giảng viên: Bật panel xử lý, tắt panel gửi đơn cá nhân
            cboMonHoc.setEnabled(false);
            txaLyDoGui.setEnabled(false);
            btnGuiDon.setEnabled(false);
            btnHuyDon.setEnabled(true); // Cán bộ có thể xóa đơn nếu cần

            cboTrangThaiMoi.setEnabled(true);
            txtDiemCKMoi.setEnabled(false); // Chỉ bật khi chọn Chấp thuận
            txaKetQuaXuLy.setEnabled(true);
            btnLuuXuLy.setEnabled(true);
        }

        taiDanhSachPhucKhao();
    }

    /**
     * Tải danh sách môn học của sinh viên đã có điểm cuối kỳ để cho phép phúc khảo
     */
    private void taiDanhSachMonCoThePhucKhao(int sinhVienId) {
        List<BangDiem> listMon = phucKhaoDAO.findMonHocCoThePhucKhao(sinhVienId);
        DefaultComboBoxModel<BangDiem> model = new DefaultComboBoxModel<>();
        for (BangDiem bd : listMon) {
            model.addElement(bd);
        }
        cboMonHoc.setModel(model);

        if (!listMon.isEmpty()) {
            hienThiDiemHienTai(listMon.get(0));
        } else {
            lblDiemHienTai.setText("Điểm hiện tại: (Bạn chưa có học phần nào đủ điều kiện phúc khảo)");
        }
    }

    /**
     * Hiển thị điểm số hiện tại của học phần được chọn trong combobox
     */
    private void hienThiDiemHienTai(BangDiem bd) {
        if (bd == null) {
            lblDiemHienTai.setText("Điểm hiện tại: CC = -- | GK = -- | CK = -- | Tổng kết = -- | Điểm chữ: --");
            return;
        }

        String strCC = (bd.getDiemCC() != null) ? String.valueOf(bd.getDiemCC()) : "--";
        String strGK = (bd.getDiemGK() != null) ? String.valueOf(bd.getDiemGK()) : "--";
        String strCK = (bd.getDiemCK() != null) ? String.valueOf(bd.getDiemCK()) : "--";
        String strTK = (bd.getDiemTongKet() != null) ? String.valueOf(bd.getDiemTongKet()) : "--";
        String strChu = (bd.getDiemChu() != null) ? bd.getDiemChu() : "--";

        lblDiemHienTai.setText(String.format("Điểm hiện tại: CC = %s | GK = %s | CK = %s | Tổng kết = %s | Điểm chữ: %s",
                strCC, strGK, strCK, strTK, strChu));
    }

    /**
     * Tải danh sách đơn phúc khảo từ CSDL lên bảng JTable
     */
    private void taiDanhSachPhucKhao() {
        String trangThai = (String) cboLocTrangThai.getSelectedItem();
        String keyword = txtTimKiem.getText().trim();

        if (currentUser != null && "SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro())) {
            if (currentSinhVien != null) {
                List<PhucKhao> fullList = phucKhaoDAO.findBySinhVien(currentSinhVien.getSinhVienId());
                danhSachPhucKhao = new ArrayList<>();
                for (PhucKhao pk : fullList) {
                    boolean matchStatus = (trangThai == null || "Tất cả".equalsIgnoreCase(trangThai)
                            || pk.getTrangThai().equalsIgnoreCase(trangThai));
                    boolean matchKeyword = (keyword.isEmpty()
                            || pk.getTenMonHoc().toLowerCase().contains(keyword.toLowerCase())
                            || pk.getMaLopHocPhan().toLowerCase().contains(keyword.toLowerCase()));

                    if (matchStatus && matchKeyword) {
                        danhSachPhucKhao.add(pk);
                    }
                }
            }
        } else {
            danhSachPhucKhao = phucKhaoDAO.findByFilter(trangThai, 0, keyword);
        }

        hienThiLenTable(danhSachPhucKhao);
    }

    /**
     * Hiển thị danh sách đơn phúc khảo lên JTable
     */
    private void hienThiLenTable(List<PhucKhao> list) {
        DefaultTableModel model = (DefaultTableModel) tblPhucKhao.getModel();
        model.setRowCount(0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        int stt = 1;

        for (PhucKhao pk : list) {
            String ngayGui = (pk.getNgayGui() != null) ? sdf.format(pk.getNgayGui()) : "";
            String strCC = (pk.getDiemCC() != null) ? String.valueOf(pk.getDiemCC()) : "--";
            String strGK = (pk.getDiemGK() != null) ? String.valueOf(pk.getDiemGK()) : "--";
            String strCK = (pk.getDiemCK() != null) ? String.valueOf(pk.getDiemCK()) : "--";
            String strTK = (pk.getDiemTongKet() != null) ? String.valueOf(pk.getDiemTongKet()) : "--";
            String nguoiXuLy = (pk.getTenNguoiXuLy() != null) ? pk.getTenNguoiXuLy() : "--";

            model.addRow(new Object[]{
                stt++,
                pk.getPhucKhaoId(),
                pk.getMaSinhVien(),
                pk.getHoTenSinhVien(),
                pk.getTenLop() != null ? pk.getTenLop() : "",
                pk.getTenMonHoc(),
                strCC,
                strGK,
                strCK,
                strTK,
                ngayGui,
                pk.getTrangThai(),
                nguoiXuLy
            });
        }

        donDangChon = null;
        lblDonDangChon.setText("Đơn đang chọn: (Chưa chọn đơn nào)");
        txtDiemCKMoi.setText("");
        txaKetQuaXuLy.setText("");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTieuDe = new javax.swing.JLabel();
        pnlBoLoc = new javax.swing.JPanel();
        lblLocTrangThai = new javax.swing.JLabel();
        cboLocTrangThai = new javax.swing.JComboBox<>();
        lblTimKiem = new javax.swing.JLabel();
        txtTimKiem = new javax.swing.JTextField();
        btnLoc = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnDong = new javax.swing.JButton();
        pnlDanhSach = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblPhucKhao = new javax.swing.JTable();
        pnlThaoTac = new javax.swing.JPanel();
        pnlGuiDon = new javax.swing.JPanel();
        lblChonMon = new javax.swing.JLabel();
        cboMonHoc = new javax.swing.JComboBox<>();
        lblDiemHienTai = new javax.swing.JLabel();
        lblNhapLyDo = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        txaLyDoGui = new javax.swing.JTextArea();
        btnGuiDon = new javax.swing.JButton();
        btnHuyDon = new javax.swing.JButton();
        pnlXuLy = new javax.swing.JPanel();
        lblDonDangChon = new javax.swing.JLabel();
        lblTrangThaiMoi = new javax.swing.JLabel();
        cboTrangThaiMoi = new javax.swing.JComboBox<>();
        lblDiemCKMoi = new javax.swing.JLabel();
        txtDiemCKMoi = new javax.swing.JTextField();
        lblGhiChuXuLy = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txaKetQuaXuLy = new javax.swing.JTextArea();
        btnLuuXuLy = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("QUẢN LÝ PHÚC KHẢO ĐIỂM HỌC PHẦN - UTT");

        lblTieuDe.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTieuDe.setForeground(new java.awt.Color(30, 58, 138));
        lblTieuDe.setText("QUẢN LÝ PHÚC KHẢO ĐIỂM HỌC PHẦN - UTT");

        pnlBoLoc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bộ lọc và tìm kiếm đơn phúc khảo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblLocTrangThai.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblLocTrangThai.setForeground(new java.awt.Color(31, 41, 55));
        lblLocTrangThai.setText("Trạng thái:");

        cboLocTrangThai.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        cboLocTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Chờ xử lý", "Đang xử lý", "Đã chấp nhận", "Không chấp nhận" }));
        cboLocTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocTrangThaiActionPerformed(evt);
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
                .addComponent(lblLocTrangThai)
                .addGap(8, 8, 8)
                .addComponent(cboLocTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20)
                .addComponent(lblTimKiem)
                .addGap(8, 8, 8)
                .addComponent(txtTimKiem, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        pnlBoLocLayout.setVerticalGroup(
            pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoLocLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLocTrangThai)
                    .addComponent(cboLocTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTimKiem)
                    .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        pnlDanhSach.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách đơn phúc khảo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        tblPhucKhao.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblPhucKhao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã đơn", "Mã SV", "Họ và tên", "Lớp", "Môn học", "Đ.CC", "Đ.GK", "Đ.CK cũ", "Đ.TK cũ", "Ngày gửi", "Trạng thái", "Người xử lý"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblPhucKhao.setRowHeight(24);
        tblPhucKhao.setSelectionBackground(new java.awt.Color(37, 99, 235));
        tblPhucKhao.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblPhucKhao.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblPhucKhao.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblPhucKhaoMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblPhucKhao);

        javax.swing.GroupLayout pnlDanhSachLayout = new javax.swing.GroupLayout(pnlDanhSach);
        pnlDanhSach.setLayout(pnlDanhSachLayout);
        pnlDanhSachLayout.setHorizontalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDanhSachLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 978, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        pnlDanhSachLayout.setVerticalGroup(
            pnlDanhSachLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlDanhSachLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addGap(8, 8, 8))
        );

        pnlGuiDon.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Dành cho Sinh viên: Gửi đơn phúc khảo mới", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblChonMon.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblChonMon.setForeground(new java.awt.Color(31, 41, 55));
        lblChonMon.setText("Chọn học phần:");

        cboMonHoc.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        cboMonHoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboMonHocActionPerformed(evt);
            }
        });

        lblDiemHienTai.setFont(new java.awt.Font("Segoe UI", 2, 11)); // NOI18N
        lblDiemHienTai.setForeground(new java.awt.Color(30, 58, 138));
        lblDiemHienTai.setText("Điểm hiện tại: CC = -- | GK = -- | CK = -- | Tổng kết = -- | Điểm chữ: --");

        lblNhapLyDo.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblNhapLyDo.setForeground(new java.awt.Color(31, 41, 55));
        lblNhapLyDo.setText("Lý do phúc khảo (ghi rõ câu/phần bài thi nghi ngờ):");

        txaLyDoGui.setColumns(20);
        txaLyDoGui.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        txaLyDoGui.setLineWrap(true);
        txaLyDoGui.setRows(3);
        txaLyDoGui.setWrapStyleWord(true);
        jScrollPane2.setViewportView(txaLyDoGui);

        btnGuiDon.setBackground(new java.awt.Color(21, 128, 61));
        btnGuiDon.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnGuiDon.setForeground(new java.awt.Color(255, 255, 255));
        btnGuiDon.setText("Gửi đơn phúc khảo");
        btnGuiDon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnGuiDon.setFocusPainted(false);
        btnGuiDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGuiDonActionPerformed(evt);
            }
        });

        btnHuyDon.setBackground(new java.awt.Color(220, 38, 38));
        btnHuyDon.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnHuyDon.setForeground(new java.awt.Color(255, 255, 255));
        btnHuyDon.setText("Hủy đơn đã chọn");
        btnHuyDon.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnHuyDon.setFocusPainted(false);
        btnHuyDon.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnHuyDonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlGuiDonLayout = new javax.swing.GroupLayout(pnlGuiDon);
        pnlGuiDon.setLayout(pnlGuiDonLayout);
        pnlGuiDonLayout.setHorizontalGroup(
            pnlGuiDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGuiDonLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlGuiDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane2)
                    .addGroup(pnlGuiDonLayout.createSequentialGroup()
                        .addComponent(lblChonMon)
                        .addGap(8, 8, 8)
                        .addComponent(cboMonHoc, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(lblDiemHienTai, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlGuiDonLayout.createSequentialGroup()
                        .addGroup(pnlGuiDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(pnlGuiDonLayout.createSequentialGroup()
                                .addComponent(btnGuiDon, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(btnHuyDon, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(lblNhapLyDo))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        pnlGuiDonLayout.setVerticalGroup(
            pnlGuiDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlGuiDonLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlGuiDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblChonMon)
                    .addComponent(cboMonHoc, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(lblDiemHienTai)
                .addGap(6, 6, 6)
                .addComponent(lblNhapLyDo)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addGroup(pnlGuiDonLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnGuiDon, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnHuyDon, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        pnlXuLy.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Dành cho GV / Quản trị: Xử lý đơn phúc khảo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblDonDangChon.setFont(new java.awt.Font("Segoe UI", 1, 11)); // NOI18N
        lblDonDangChon.setForeground(new java.awt.Color(30, 58, 138));
        lblDonDangChon.setText("Đơn đang chọn: (Chưa chọn đơn nào)");

        lblTrangThaiMoi.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblTrangThaiMoi.setForeground(new java.awt.Color(31, 41, 55));
        lblTrangThaiMoi.setText("Trạng thái:");

        cboTrangThaiMoi.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        cboTrangThaiMoi.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Đang xử lý", "Đã chấp nhận", "Không chấp nhận" }));
        cboTrangThaiMoi.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboTrangThaiMoiActionPerformed(evt);
            }
        });

        lblDiemCKMoi.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblDiemCKMoi.setForeground(new java.awt.Color(31, 41, 55));
        lblDiemCKMoi.setText("Điểm CK mới:");

        txtDiemCKMoi.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N

        lblGhiChuXuLy.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblGhiChuXuLy.setForeground(new java.awt.Color(31, 41, 55));
        lblGhiChuXuLy.setText("Nhận xét / Kết quả chấm lại:");

        txaKetQuaXuLy.setColumns(20);
        txaKetQuaXuLy.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        txaKetQuaXuLy.setLineWrap(true);
        txaKetQuaXuLy.setRows(3);
        txaKetQuaXuLy.setWrapStyleWord(true);
        jScrollPane3.setViewportView(txaKetQuaXuLy);

        btnLuuXuLy.setBackground(new java.awt.Color(21, 128, 61));
        btnLuuXuLy.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnLuuXuLy.setForeground(new java.awt.Color(255, 255, 255));
        btnLuuXuLy.setText("Lưu kết quả xử lý");
        btnLuuXuLy.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLuuXuLy.setFocusPainted(false);
        btnLuuXuLy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLuuXuLyActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlXuLyLayout = new javax.swing.GroupLayout(pnlXuLy);
        pnlXuLy.setLayout(pnlXuLyLayout);
        pnlXuLyLayout.setHorizontalGroup(
            pnlXuLyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlXuLyLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlXuLyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblDonDangChon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(pnlXuLyLayout.createSequentialGroup()
                        .addComponent(lblTrangThaiMoi)
                        .addGap(8, 8, 8)
                        .addComponent(cboTrangThaiMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(lblDiemCKMoi)
                        .addGap(8, 8, 8)
                        .addComponent(txtDiemCKMoi, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE))
                    .addComponent(jScrollPane3)
                    .addGroup(pnlXuLyLayout.createSequentialGroup()
                        .addGroup(pnlXuLyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblGhiChuXuLy)
                            .addComponent(btnLuuXuLy, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(12, 12, 12))
        );
        pnlXuLyLayout.setVerticalGroup(
            pnlXuLyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlXuLyLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(lblDonDangChon)
                .addGap(8, 8, 8)
                .addGroup(pnlXuLyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTrangThaiMoi)
                    .addComponent(cboTrangThaiMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblDiemCKMoi)
                    .addComponent(txtDiemCKMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addComponent(lblGhiChuXuLy)
                .addGap(4, 4, 4)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnLuuXuLy, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8))
        );

        javax.swing.GroupLayout pnlThaoTacLayout = new javax.swing.GroupLayout(pnlThaoTac);
        pnlThaoTac.setLayout(pnlThaoTacLayout);
        pnlThaoTacLayout.setHorizontalGroup(
            pnlThaoTacLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThaoTacLayout.createSequentialGroup()
                .addComponent(pnlGuiDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(15, 15, 15)
                .addComponent(pnlXuLy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlThaoTacLayout.setVerticalGroup(
            pnlThaoTacLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlXuLy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnlGuiDon, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
                    .addComponent(pnlThaoTac, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(pnlThaoTac, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cboLocTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocTrangThaiActionPerformed
        taiDanhSachPhucKhao();
    }//GEN-LAST:event_cboLocTrangThaiActionPerformed

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocActionPerformed
        taiDanhSachPhucKhao();
    }//GEN-LAST:event_btnLocActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        txtTimKiem.setText("");
        cboLocTrangThai.setSelectedIndex(0);
        taiDanhSachPhucKhao();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnDongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDongActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnDongActionPerformed

    private void tblPhucKhaoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblPhucKhaoMouseClicked
        int selectedRow = tblPhucKhao.getSelectedRow();
        if (selectedRow < 0 || danhSachPhucKhao == null || selectedRow >= danhSachPhucKhao.size()) {
            return;
        }

        donDangChon = danhSachPhucKhao.get(selectedRow);

        // Hiển thị thông tin tóm tắt đơn
        lblDonDangChon.setText(String.format("Đơn #%d - SV: %s (%s) - Môn: %s",
                donDangChon.getPhucKhaoId(),
                donDangChon.getHoTenSinhVien(),
                donDangChon.getMaSinhVien(),
                donDangChon.getTenMonHoc()));

        // Nạp lý do của sinh viên vào ô lý do (để cán bộ tiện đọc)
        txaLyDoGui.setText(donDangChon.getLyDo());

        // Cập nhật trạng thái và ghi chú xử lý
        cboTrangThaiMoi.setSelectedItem(donDangChon.getTrangThai());
        txaKetQuaXuLy.setText(donDangChon.getKetQuaXuLy() != null ? donDangChon.getKetQuaXuLy() : "");

        boolean isChapThuan = "Đã chấp nhận".equalsIgnoreCase(donDangChon.getTrangThai());
        boolean isCanBo = currentUser != null && !"SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro());
        txtDiemCKMoi.setEnabled(isChapThuan && isCanBo);

        if (donDangChon.getDiemCK() != null) {
            txtDiemCKMoi.setText(String.valueOf(donDangChon.getDiemCK()));
        } else {
            txtDiemCKMoi.setText("");
        }
    }//GEN-LAST:event_tblPhucKhaoMouseClicked

    private void cboMonHocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboMonHocActionPerformed
        BangDiem selectedBd = (BangDiem) cboMonHoc.getSelectedItem();
        hienThiDiemHienTai(selectedBd);
    }//GEN-LAST:event_cboMonHocActionPerformed

    private void btnGuiDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGuiDonActionPerformed
        if (currentUser == null || !"SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro())) {
            JOptionPane.showMessageDialog(this,
                    "Chức năng gửi đơn phúc khảo chỉ dành cho tài khoản Sinh viên!",
                    "Không có quyền",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (currentSinhVien == null) {
            JOptionPane.showMessageDialog(this,
                    "Không tìm thấy hồ sơ sinh viên của tài khoản này!",
                    "Lỗi dữ liệu",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        BangDiem selectedBd = (BangDiem) cboMonHoc.getSelectedItem();
        if (selectedBd == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn học phần cần phúc khảo điểm!",
                    "Chưa chọn môn",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String lyDo = txaLyDoGui.getText().trim();
        if (lyDo.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập lý do xin phúc khảo điểm bài thi!",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            txaLyDoGui.requestFocus();
            return;
        }

        // Kiểm tra xem đã có đơn đang chờ xử lý cho môn này chưa
        if (phucKhaoDAO.hasPendingAppeal(selectedBd.getBangDiemId())) {
            JOptionPane.showMessageDialog(this,
                    "Bạn đã có đơn phúc khảo cho học phần '" + selectedBd.getTenMonHoc()
                    + "' đang ở trạng thái 'Chờ duyệt' hoặc 'Đang xử lý'!\nVui lòng chờ kết quả từ bộ phận chuyên môn.",
                    "Đơn đã tồn tại",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận gửi đơn phúc khảo môn: " + selectedBd.getTenMonHoc() + "?\n"
                + "Điểm cuối kỳ hiện tại: " + selectedBd.getDiemCK() + "\n"
                + "Lý do: " + lyDo,
                "Xác nhận gửi đơn",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        PhucKhao pk = new PhucKhao(selectedBd.getBangDiemId(), currentSinhVien.getSinhVienId(), lyDo);
        boolean thanhCong = phucKhaoDAO.guiDonPhucKhao(pk);

        if (thanhCong) {
            JOptionPane.showMessageDialog(this,
                    "Gửi đơn phúc khảo thành công!\nBộ phận phụ trách sẽ kiểm tra và phản hồi kết quả sớm nhất.",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            txaLyDoGui.setText("");
            taiDanhSachPhucKhao();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Gửi đơn phúc khảo thất bại! Vui lòng thử lại.",
                    "Lỗi hệ thống",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnGuiDonActionPerformed

    private void btnHuyDonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnHuyDonActionPerformed
        if (donDangChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng bấm chọn một đơn phúc khảo trên bảng trước!",
                    "Chưa chọn đơn",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!"Chờ xử lý".equalsIgnoreCase(donDangChon.getTrangThai())) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ có thể hủy đơn phúc khảo khi đang ở trạng thái 'Chờ xử lý'!\n"
                    + "Đơn hiện tại có trạng thái: " + donDangChon.getTrangThai(),
                    "Không thể hủy",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Nếu là sinh viên thì chỉ được hủy đơn của chính mình
        if ("SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro())
                && currentSinhVien != null
                && donDangChon.getSinhVienId() != currentSinhVien.getSinhVienId()) {
            JOptionPane.showMessageDialog(this,
                    "Bạn chỉ có quyền hủy đơn phúc khảo của chính mình!",
                    "Từ chối truy cập",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn hủy đơn phúc khảo #" + donDangChon.getPhucKhaoId()
                + " (Môn: " + donDangChon.getTenMonHoc() + ")?",
                "Xác nhận hủy đơn",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean thanhCong = phucKhaoDAO.huyDonPhucKhao(donDangChon.getPhucKhaoId());
            if (thanhCong) {
                JOptionPane.showMessageDialog(this,
                        "Đã hủy đơn phúc khảo thành công!",
                        "Thông báo",
                        JOptionPane.INFORMATION_MESSAGE);
                taiDanhSachPhucKhao();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Hủy đơn thất bại! Vui lòng thử lại.",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnHuyDonActionPerformed

    private void cboTrangThaiMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboTrangThaiMoiActionPerformed
        String selectedStatus = (String) cboTrangThaiMoi.getSelectedItem();
        boolean isChapThuan = "Đã chấp nhận".equalsIgnoreCase(selectedStatus);
        boolean isCanBo = currentUser != null && !"SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro());

        txtDiemCKMoi.setEnabled(isChapThuan && isCanBo);
        if (!isChapThuan) {
            txtDiemCKMoi.setText("");
        }
    }//GEN-LAST:event_cboTrangThaiMoiActionPerformed

    private void btnLuuXuLyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLuuXuLyActionPerformed
        if (currentUser == null || "SINHVIEN".equalsIgnoreCase(currentUser.getVaiTro())) {
            JOptionPane.showMessageDialog(this,
                    "Chức năng xử lý đơn phúc khảo chỉ dành cho Giảng viên và Quản trị viên!",
                    "Từ chối quyền",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (donDangChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một đơn phúc khảo trên bảng để xử lý!",
                    "Chưa chọn đơn",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String trangThaiMoi = (String) cboTrangThaiMoi.getSelectedItem();
        String ketQuaXuLy = txaKetQuaXuLy.getText().trim();
        Double diemCKMoi = null;

        if ("Đã chấp nhận".equalsIgnoreCase(trangThaiMoi)) {
            String strDiem = txtDiemCKMoi.getText().trim();
            if (!strDiem.isEmpty()) {
                try {
                    diemCKMoi = Double.parseDouble(strDiem.replace(',', '.'));
                    if (diemCKMoi < 0.0 || diemCKMoi > 10.0) {
                        JOptionPane.showMessageDialog(this,
                                "Điểm cuối kỳ mới phải nằm trong thang điểm từ 0.0 đến 10.0!",
                                "Sai thang điểm",
                                JOptionPane.WARNING_MESSAGE);
                        txtDiemCKMoi.requestFocus();
                        return;
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                            "Điểm cuối kỳ mới phải là số thực hợp lệ (VD: 7.5)!",
                            "Lỗi định dạng số",
                            JOptionPane.WARNING_MESSAGE);
                    txtDiemCKMoi.requestFocus();
                    return;
                }
            }
        }

        StringBuilder confirmMsg = new StringBuilder();
        confirmMsg.append("Xác nhận cập nhật xử lý đơn #").append(donDangChon.getPhucKhaoId()).append(":\n")
                  .append("- Sinh viên: ").append(donDangChon.getHoTenSinhVien()).append(" (").append(donDangChon.getMaSinhVien()).append(")\n")
                  .append("- Môn học: ").append(donDangChon.getTenMonHoc()).append("\n")
                  .append("- Trạng thái mới: ").append(trangThaiMoi).append("\n");

        if (diemCKMoi != null) {
            confirmMsg.append("- Điểm CK mới: ").append(diemCKMoi).append(" (Điểm cũ: ").append(donDangChon.getDiemCK()).append(")\n")
                      .append("=> Hệ thống sẽ tự động cập nhật lại Bảng điểm, Lịch sử sửa điểm và GPA!\n");
        }
        confirmMsg.append("- Nhận xét / Phản hồi: ").append(ketQuaXuLy.isEmpty() ? "(Không có)" : ketQuaXuLy);

        int confirm = JOptionPane.showConfirmDialog(this,
                confirmMsg.toString(),
                "Xác nhận lưu kết quả xử lý",
                JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        boolean thanhCong = phucKhaoDAO.xuLyPhucKhao(
                donDangChon.getPhucKhaoId(),
                trangThaiMoi,
                ketQuaXuLy,
                currentUser.getTaiKhoanId(),
                diemCKMoi
        );

        if (thanhCong) {
            JOptionPane.showMessageDialog(this,
                    "Cập nhật kết quả phúc khảo thành công!\nThông báo đã được tự động gửi đến tài khoản sinh viên.",
                    "Xử lý thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            taiDanhSachPhucKhao();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Cập nhật xử lý thất bại! Vui lòng kiểm tra lại kết nối cơ sở dữ liệu.",
                    "Lỗi xử lý",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnLuuXuLyActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDong;
    private javax.swing.JButton btnGuiDon;
    private javax.swing.JButton btnHuyDon;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnLuuXuLy;
    private javax.swing.JComboBox<String> cboLocTrangThai;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.BangDiem> cboMonHoc;
    private javax.swing.JComboBox<String> cboTrangThaiMoi;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JLabel lblChonMon;
    private javax.swing.JLabel lblDiemCKMoi;
    private javax.swing.JLabel lblDiemHienTai;
    private javax.swing.JLabel lblDonDangChon;
    private javax.swing.JLabel lblGhiChuXuLy;
    private javax.swing.JLabel lblLocTrangThai;
    private javax.swing.JLabel lblNhapLyDo;
    private javax.swing.JLabel lblTieuDe;
    private javax.swing.JLabel lblTimKiem;
    private javax.swing.JLabel lblTrangThaiMoi;
    private javax.swing.JPanel pnlBoLoc;
    private javax.swing.JPanel pnlDanhSach;
    private javax.swing.JPanel pnlGuiDon;
    private javax.swing.JPanel pnlThaoTac;
    private javax.swing.JPanel pnlXuLy;
    private javax.swing.JTable tblPhucKhao;
    private javax.swing.JTextArea txaKetQuaXuLy;
    private javax.swing.JTextArea txaLyDoGui;
    private javax.swing.JTextField txtDiemCKMoi;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}

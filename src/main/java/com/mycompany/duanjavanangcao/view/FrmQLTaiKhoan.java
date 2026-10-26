package com.mycompany.duanjavanangcao.view;

import com.mycompany.duanjavanangcao.dao.TaiKhoanDAO;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.util.SessionManager;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Form Quản lý Tài khoản & Phân quyền người dùng - Thiết kế chuẩn NetBeans Swing GUI Builder
 */
public class FrmQLTaiKhoan extends javax.swing.JFrame {

    private final TaiKhoanDAO taiKhoanDAO;
    private List<TaiKhoan> danhSachTaiKhoan;
    private TaiKhoan taiKhoanDangChon;

    /**
     * Khởi tạo form FrmQLTaiKhoan
     */
    public FrmQLTaiKhoan() {
        initComponents();
        getContentPane().setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.BG_LIGHT);
        pnlBoLoc.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlDanhSach.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlChiTiet.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatTable(tblTaiKhoan);
        this.setLocationRelativeTo(null);
        this.taiKhoanDAO = new TaiKhoanDAO();
        this.danhSachTaiKhoan = new ArrayList<>();

        kiemTraQuyenVaNapDuLieu();
    }

    /**
     * Kiểm tra quyền quản trị viên và tải dữ liệu ban đầu
     */
    private void kiemTraQuyenVaNapDuLieu() {
        TaiKhoan currentUser = SessionManager.getCurrentUser();
        if (currentUser == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng đăng nhập hệ thống để tiếp tục!",
                    "Chưa đăng nhập",
                    JOptionPane.WARNING_MESSAGE);
            new FrmDangNhap().setVisible(true);
            this.dispose();
            return;
        }

        if (!"ADMIN".equalsIgnoreCase(currentUser.getVaiTro())) {
            JOptionPane.showMessageDialog(this,
                    "Chức năng Quản lý Tài khoản & Phân quyền chỉ dành riêng cho Quản trị viên (ADMIN)!",
                    "Từ chối quyền truy cập",
                    JOptionPane.ERROR_MESSAGE);
            this.dispose();
            return;
        }

        taiDanhSachTaiKhoan();
    }

    /**
     * Tải danh sách tài khoản theo bộ lọc lên bảng
     */
    private void taiDanhSachTaiKhoan() {
        String vaiTro = (String) cboLocVaiTro.getSelectedItem();
        String trangThai = (String) cboLocTrangThai.getSelectedItem();
        String keyword = txtTimKiem.getText().trim();

        danhSachTaiKhoan = taiKhoanDAO.findByFilter(vaiTro, trangThai, keyword);
        hienThiLenTable(danhSachTaiKhoan);
    }

    /**
     * Hiển thị danh sách tài khoản lên JTable
     */
    private void hienThiLenTable(List<TaiKhoan> list) {
        DefaultTableModel model = (DefaultTableModel) tblTaiKhoan.getModel();
        model.setRowCount(0);

        int stt = 1;
        for (TaiKhoan tk : list) {
            String strTrangThai = tk.isTrangThai() ? "Hoạt động" : "Đã khóa";
            model.addRow(new Object[]{
                stt++,
                tk.getTaiKhoanId(),
                tk.getTenDangNhap(),
                tk.getHoTen(),
                tk.getEmail() != null ? tk.getEmail() : "",
                tk.getSoDienThoai() != null ? tk.getSoDienThoai() : "",
                tk.getVaiTro(),
                strTrangThai
            });
        }

        taiKhoanDangChon = null;
    }

    /**
     * Nạp thông tin tài khoản được chọn lên các ô nhập liệu
     */
    private void napChiTiet(TaiKhoan tk) {
        if (tk == null) return;
        txtTenDangNhap.setText(tk.getTenDangNhap());
        txtTenDangNhap.setEditable(false); // Không sửa tên đăng nhập đã có
        txtMatKhau.setText("");
        txtHoTen.setText(tk.getHoTen());
        txtEmail.setText(tk.getEmail() != null ? tk.getEmail() : "");
        txtSoDienThoai.setText(tk.getSoDienThoai() != null ? tk.getSoDienThoai() : "");
        cboVaiTro.setSelectedItem(tk.getVaiTro());
        cboTrangThaiDetail.setSelectedItem(tk.isTrangThai() ? "Hoạt động" : "Đã khóa");
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblTieuDe = new javax.swing.JLabel();
        pnlBoLoc = new javax.swing.JPanel();
        lblLocVaiTro = new javax.swing.JLabel();
        cboLocVaiTro = new javax.swing.JComboBox<>();
        lblLocTrangThai = new javax.swing.JLabel();
        cboLocTrangThai = new javax.swing.JComboBox<>();
        lblTimKiem = new javax.swing.JLabel();
        txtTimKiem = new javax.swing.JTextField();
        btnLoc = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnDong = new javax.swing.JButton();
        pnlDanhSach = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblTaiKhoan = new javax.swing.JTable();
        pnlChiTiet = new javax.swing.JPanel();
        lblTenDangNhap = new javax.swing.JLabel();
        txtTenDangNhap = new javax.swing.JTextField();
        lblMatKhau = new javax.swing.JLabel();
        txtMatKhau = new javax.swing.JTextField();
        lblVaiTro = new javax.swing.JLabel();
        cboVaiTro = new javax.swing.JComboBox<>();
        lblTrangThaiDetail = new javax.swing.JLabel();
        cboTrangThaiDetail = new javax.swing.JComboBox<>();
        lblHoTen = new javax.swing.JLabel();
        txtHoTen = new javax.swing.JTextField();
        lblEmail = new javax.swing.JLabel();
        txtEmail = new javax.swing.JTextField();
        lblSoDienThoai = new javax.swing.JLabel();
        txtSoDienThoai = new javax.swing.JTextField();
        btnThem = new javax.swing.JButton();
        btnSua = new javax.swing.JButton();
        btnResetMatKhau = new javax.swing.JButton();
        btnKhoaMoKhoa = new javax.swing.JButton();
        btnXoa = new javax.swing.JButton();
        btnNhapLai = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("QUẢN LÝ TÀI KHOẢN & PHÂN QUYỀN - UTT");

        lblTieuDe.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTieuDe.setForeground(new java.awt.Color(30, 58, 138));
        lblTieuDe.setText("QUẢN LÝ TÀI KHOẢN & PHÂN QUYỀN NGƯỜI DÙNG");

        pnlBoLoc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Bộ lọc và tìm kiếm tài khoản", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblLocVaiTro.setText("Vai trò:");

        cboLocVaiTro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "ADMIN", "GIANGVIEN", "SINHVIEN" }));
        cboLocVaiTro.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocVaiTroActionPerformed(evt);
            }
        });

        lblLocTrangThai.setText("Trạng thái:");

        cboLocTrangThai.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Tất cả", "Hoạt động", "Đã khóa" }));
        cboLocTrangThai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLocTrangThaiActionPerformed(evt);
            }
        });

        lblTimKiem.setText("Từ khóa:");

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
                .addComponent(lblLocVaiTro)
                .addGap(8, 8, 8)
                .addComponent(cboLocVaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(lblLocTrangThai)
                .addGap(8, 8, 8)
                .addComponent(cboLocTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(lblTimKiem)
                .addGap(8, 8, 8)
                .addComponent(txtTimKiem, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addGap(10, 10, 10)
                .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(8, 8, 8)
                .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15)
                .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12))
        );
        pnlBoLocLayout.setVerticalGroup(
            pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoLocLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLocVaiTro)
                    .addComponent(cboLocVaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLocTrangThai)
                    .addComponent(cboLocTrangThai, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTimKiem)
                    .addComponent(txtTimKiem, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLoc, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        pnlDanhSach.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Danh sách tài khoản", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        tblTaiKhoan.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblTaiKhoan.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Mã TK", "Tên đăng nhập", "Họ và tên", "Email", "Số điện thoại", "Vai trò", "Trạng thái"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblTaiKhoan.setRowHeight(24);
        tblTaiKhoan.setSelectionBackground(new java.awt.Color(37, 99, 235));
        tblTaiKhoan.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblTaiKhoan.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        tblTaiKhoan.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTaiKhoanMouseClicked(evt);
            }
        });
        jScrollPane1.setViewportView(tblTaiKhoan);

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
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                .addGap(8, 8, 8))
        );

        pnlChiTiet.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Thông tin tài khoản & Phân quyền", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblTenDangNhap.setText("Tên đăng nhập:");

        lblMatKhau.setText("Mật khẩu:");

        lblVaiTro.setText("Vai trò:");

        cboVaiTro.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "ADMIN", "GIANGVIEN", "SINHVIEN" }));

        lblTrangThaiDetail.setText("Trạng thái:");

        cboTrangThaiDetail.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Hoạt động", "Đã khóa" }));

        lblHoTen.setText("Họ và tên:");

        lblEmail.setText("Email:");

        lblSoDienThoai.setText("Số điện thoại:");

        btnThem.setBackground(new java.awt.Color(21, 128, 61));
        btnThem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThem.setForeground(new java.awt.Color(255, 255, 255));
        btnThem.setText("Thêm tài khoản");
        btnThem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnThem.setFocusPainted(false);
        btnThem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThemActionPerformed(evt);
            }
        });

        btnSua.setBackground(new java.awt.Color(30, 58, 138));
        btnSua.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnSua.setForeground(new java.awt.Color(255, 255, 255));
        btnSua.setText("Cập nhật thông tin");
        btnSua.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnSua.setFocusPainted(false);
        btnSua.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSuaActionPerformed(evt);
            }
        });

        btnResetMatKhau.setBackground(new java.awt.Color(37, 99, 235));
        btnResetMatKhau.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnResetMatKhau.setForeground(new java.awt.Color(255, 255, 255));
        btnResetMatKhau.setText("Đặt lại MK (123456)");
        btnResetMatKhau.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnResetMatKhau.setFocusPainted(false);
        btnResetMatKhau.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnResetMatKhauActionPerformed(evt);
            }
        });

        btnKhoaMoKhoa.setBackground(new java.awt.Color(242, 140, 40));
        btnKhoaMoKhoa.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnKhoaMoKhoa.setForeground(new java.awt.Color(255, 255, 255));
        btnKhoaMoKhoa.setText("Khóa / Mở khóa");
        btnKhoaMoKhoa.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnKhoaMoKhoa.setFocusPainted(false);
        btnKhoaMoKhoa.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKhoaMoKhoaActionPerformed(evt);
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

        btnNhapLai.setBackground(new java.awt.Color(226, 232, 240));
        btnNhapLai.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNhapLai.setForeground(new java.awt.Color(31, 41, 55));
        btnNhapLai.setText("Nhập lại");
        btnNhapLai.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNhapLai.setFocusPainted(false);
        btnNhapLai.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNhapLaiActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlChiTietLayout = new javax.swing.GroupLayout(pnlChiTiet);
        pnlChiTiet.setLayout(pnlChiTietLayout);
        pnlChiTietLayout.setHorizontalGroup(
            pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChiTietLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlChiTietLayout.createSequentialGroup()
                        .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnResetMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnKhoaMoKhoa, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(10, 10, 10)
                        .addComponent(btnNhapLai, javax.swing.GroupLayout.PREFERRED_SIZE, 90, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(pnlChiTietLayout.createSequentialGroup()
                        .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblTenDangNhap)
                            .addComponent(lblHoTen))
                        .addGap(8, 8, 8)
                        .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtTenDangNhap, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(txtHoTen))
                        .addGap(15, 15, 15)
                        .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblMatKhau)
                            .addComponent(lblEmail))
                        .addGap(8, 8, 8)
                        .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtMatKhau, javax.swing.GroupLayout.DEFAULT_SIZE, 180, Short.MAX_VALUE)
                            .addComponent(txtEmail))
                        .addGap(15, 15, 15)
                        .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblVaiTro)
                            .addComponent(lblSoDienThoai))
                        .addGap(8, 8, 8)
                        .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(cboVaiTro, 0, 140, Short.MAX_VALUE)
                            .addComponent(txtSoDienThoai))
                        .addGap(15, 15, 15)
                        .addComponent(lblTrangThaiDetail)
                        .addGap(8, 8, 8)
                        .addComponent(cboTrangThaiDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        pnlChiTietLayout.setVerticalGroup(
            pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlChiTietLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTenDangNhap)
                    .addComponent(txtTenDangNhap, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblMatKhau)
                    .addComponent(txtMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblVaiTro)
                    .addComponent(cboVaiTro, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblTrangThaiDetail)
                    .addComponent(cboTrangThaiDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHoTen)
                    .addComponent(txtHoTen, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblEmail)
                    .addComponent(txtEmail, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblSoDienThoai)
                    .addComponent(txtSoDienThoai, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(pnlChiTietLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnThem, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSua, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnResetMatKhau, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnKhoaMoKhoa, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXoa, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnNhapLai, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
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
                .addComponent(pnlDanhSach, javax.swing.GroupLayout.DEFAULT_SIZE, 220, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlChiTiet, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(15, 15, 15))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cboLocVaiTroActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocVaiTroActionPerformed
        taiDanhSachTaiKhoan();
    }//GEN-LAST:event_cboLocVaiTroActionPerformed

    private void cboLocTrangThaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLocTrangThaiActionPerformed
        taiDanhSachTaiKhoan();
    }//GEN-LAST:event_cboLocTrangThaiActionPerformed

    private void btnLocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLocActionPerformed
        taiDanhSachTaiKhoan();
    }//GEN-LAST:event_btnLocActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        txtTimKiem.setText("");
        cboLocVaiTro.setSelectedIndex(0);
        cboLocTrangThai.setSelectedIndex(0);
        taiDanhSachTaiKhoan();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnDongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDongActionPerformed
        this.dispose();
    }//GEN-LAST:event_btnDongActionPerformed

    private void tblTaiKhoanMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTaiKhoanMouseClicked
        int selectedRow = tblTaiKhoan.getSelectedRow();
        if (selectedRow < 0 || danhSachTaiKhoan == null || selectedRow >= danhSachTaiKhoan.size()) {
            return;
        }

        taiKhoanDangChon = danhSachTaiKhoan.get(selectedRow);
        napChiTiet(taiKhoanDangChon);
    }//GEN-LAST:event_tblTaiKhoanMouseClicked

    private void btnThemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThemActionPerformed
        String username = txtTenDangNhap.getText().trim();
        String password = txtMatKhau.getText().trim();
        String fullName = txtHoTen.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtSoDienThoai.getText().trim();
        String role = (String) cboVaiTro.getSelectedItem();
        boolean isActive = "Hoạt động".equalsIgnoreCase((String) cboTrangThaiDetail.getSelectedItem());

        if (username.isEmpty() || fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng nhập đầy đủ Tên đăng nhập và Họ tên!",
                    "Thiếu dữ liệu",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (taiKhoanDAO.existsByTenDangNhap(username)) {
            JOptionPane.showMessageDialog(this,
                    "Tên đăng nhập '" + username + "' đã được sử dụng! Vui lòng chọn tên khác.",
                    "Trùng tên đăng nhập",
                    JOptionPane.WARNING_MESSAGE);
            txtTenDangNhap.requestFocus();
            return;
        }

        TaiKhoan tk = new TaiKhoan();
        tk.setTenDangNhap(username);
        tk.setHoTen(fullName);
        tk.setEmail(email.isEmpty() ? null : email);
        tk.setSoDienThoai(phone.isEmpty() ? null : phone);
        tk.setVaiTro(role);
        tk.setTrangThai(isActive);

        boolean ok = taiKhoanDAO.insert(tk, password.isEmpty() ? "123456" : password);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Thêm tài khoản mới thành công! (Mật khẩu mặc định: " + (password.isEmpty() ? "123456" : password) + ")",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            btnNhapLaiActionPerformed(evt);
            taiDanhSachTaiKhoan();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Thêm tài khoản thất bại! Vui lòng kiểm tra lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnThemActionPerformed

    private void btnSuaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSuaActionPerformed
        if (taiKhoanDangChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một tài khoản trên bảng để sửa thông tin!",
                    "Chưa chọn tài khoản",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        String fullName = txtHoTen.getText().trim();
        String email = txtEmail.getText().trim();
        String phone = txtSoDienThoai.getText().trim();
        String role = (String) cboVaiTro.getSelectedItem();
        boolean isActive = "Hoạt động".equalsIgnoreCase((String) cboTrangThaiDetail.getSelectedItem());

        if (fullName.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Họ tên không được để trống!",
                    "Thiếu thông tin",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        taiKhoanDangChon.setHoTen(fullName);
        taiKhoanDangChon.setEmail(email.isEmpty() ? null : email);
        taiKhoanDangChon.setSoDienThoai(phone.isEmpty() ? null : phone);
        taiKhoanDangChon.setVaiTro(role);
        taiKhoanDangChon.setTrangThai(isActive);

        boolean ok = taiKhoanDAO.update(taiKhoanDangChon);
        if (ok) {
            JOptionPane.showMessageDialog(this,
                    "Cập nhật thông tin tài khoản thành công!",
                    "Thành công",
                    JOptionPane.INFORMATION_MESSAGE);
            taiDanhSachTaiKhoan();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Cập nhật thất bại! Vui lòng thử lại.",
                    "Lỗi",
                    JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnSuaActionPerformed

    private void btnResetMatKhauActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnResetMatKhauActionPerformed
        if (taiKhoanDangChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một tài khoản trên bảng để đặt lại mật khẩu!",
                    "Chưa chọn tài khoản",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận đặt lại mật khẩu cho tài khoản '" + taiKhoanDangChon.getTenDangNhap()
                + "' về mật khẩu mặc định '123456'?",
                "Xác nhận đặt lại mật khẩu",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = taiKhoanDAO.resetMatKhau(taiKhoanDangChon.getTaiKhoanId(), "123456");
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Đặt lại mật khẩu thành công! Mật khẩu mới là: 123456",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Đặt lại mật khẩu thất bại!",
                        "Lỗi",
                        JOptionPane.ERROR_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnResetMatKhauActionPerformed

    private void btnKhoaMoKhoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKhoaMoKhoaActionPerformed
        if (taiKhoanDangChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một tài khoản trên bảng để thay đổi trạng thái!",
                    "Chưa chọn tài khoản",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Không cho phép tự khóa chính tài khoản ADMIN đang đăng nhập
        TaiKhoan current = SessionManager.getCurrentUser();
        if (current != null && current.getTaiKhoanId() == taiKhoanDangChon.getTaiKhoanId()) {
            JOptionPane.showMessageDialog(this,
                    "Bạn không thể tự khóa tài khoản quản trị đang đăng nhập của chính mình!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        boolean trangThaiMoi = !taiKhoanDangChon.isTrangThai();
        String actionName = trangThaiMoi ? "Mở khóa" : "Khóa";

        int confirm = JOptionPane.showConfirmDialog(this,
                "Xác nhận " + actionName.toLowerCase() + " tài khoản '" + taiKhoanDangChon.getTenDangNhap() + "'?",
                "Xác nhận",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            taiKhoanDangChon.setTrangThai(trangThaiMoi);
            boolean ok = taiKhoanDAO.update(taiKhoanDangChon);
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Đã " + actionName.toLowerCase() + " tài khoản thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                taiDanhSachTaiKhoan();
            }
        }
    }//GEN-LAST:event_btnKhoaMoKhoaActionPerformed

    private void btnXoaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXoaActionPerformed
        if (taiKhoanDangChon == null) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một tài khoản trên bảng để xóa!",
                    "Chưa chọn tài khoản",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        TaiKhoan current = SessionManager.getCurrentUser();
        if (current != null && current.getTaiKhoanId() == taiKhoanDangChon.getTaiKhoanId()) {
            JOptionPane.showMessageDialog(this,
                    "Bạn không thể tự xóa tài khoản quản trị đang đăng nhập của chính mình!",
                    "Cảnh báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn xóa tài khoản '" + taiKhoanDangChon.getTenDangNhap()
                + "'?\nLưu ý: Nếu tài khoản đã có liên kết học vụ, bạn nên chọn chức năng 'Khóa' thay vì xóa.",
                "Xác nhận xóa tài khoản",
                JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            boolean ok = taiKhoanDAO.delete(taiKhoanDangChon.getTaiKhoanId());
            if (ok) {
                JOptionPane.showMessageDialog(this,
                        "Xóa tài khoản thành công!",
                        "Thành công",
                        JOptionPane.INFORMATION_MESSAGE);
                btnNhapLaiActionPerformed(evt);
                taiDanhSachTaiKhoan();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Không thể xóa tài khoản này do ràng buộc dữ liệu liên kết (Sinh viên, Giảng viên hoặc Lịch sử điểm)!\n"
                        + "Gợi ý: Hãy bấm nút 'Khóa / Mở khóa' để vô hiệu hóa tài khoản một cách an toàn.",
                        "Ràng buộc CSDL",
                        JOptionPane.WARNING_MESSAGE);
            }
        }
    }//GEN-LAST:event_btnXoaActionPerformed

    private void btnNhapLaiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNhapLaiActionPerformed
        taiKhoanDangChon = null;
        txtTenDangNhap.setText("");
        txtTenDangNhap.setEditable(true);
        txtMatKhau.setText("");
        txtHoTen.setText("");
        txtEmail.setText("");
        txtSoDienThoai.setText("");
        cboVaiTro.setSelectedIndex(0);
        cboTrangThaiDetail.setSelectedIndex(0);
        tblTaiKhoan.clearSelection();
    }//GEN-LAST:event_btnNhapLaiActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDong;
    private javax.swing.JButton btnKhoaMoKhoa;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnLoc;
    private javax.swing.JButton btnNhapLai;
    private javax.swing.JButton btnResetMatKhau;
    private javax.swing.JButton btnSua;
    private javax.swing.JButton btnThem;
    private javax.swing.JButton btnXoa;
    private javax.swing.JComboBox<String> cboLocTrangThai;
    private javax.swing.JComboBox<String> cboLocVaiTro;
    private javax.swing.JComboBox<String> cboTrangThaiDetail;
    private javax.swing.JComboBox<String> cboVaiTro;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblEmail;
    private javax.swing.JLabel lblHoTen;
    private javax.swing.JLabel lblLocTrangThai;
    private javax.swing.JLabel lblLocVaiTro;
    private javax.swing.JLabel lblMatKhau;
    private javax.swing.JLabel lblSoDienThoai;
    private javax.swing.JLabel lblTenDangNhap;
    private javax.swing.JLabel lblTieuDe;
    private javax.swing.JLabel lblTimKiem;
    private javax.swing.JLabel lblTrangThaiDetail;
    private javax.swing.JLabel lblVaiTro;
    private javax.swing.JPanel pnlBoLoc;
    private javax.swing.JPanel pnlChiTiet;
    private javax.swing.JPanel pnlDanhSach;
    private javax.swing.JTable tblTaiKhoan;
    private javax.swing.JTextField txtEmail;
    private javax.swing.JTextField txtHoTen;
    private javax.swing.JTextField txtMatKhau;
    private javax.swing.JTextField txtSoDienThoai;
    private javax.swing.JTextField txtTenDangNhap;
    private javax.swing.JTextField txtTimKiem;
    // End of variables declaration//GEN-END:variables
}


package com.mycompany.duanjavanangcao.view;

import com.mycompany.duanjavanangcao.dao.ThongBaoDAO;
import com.mycompany.duanjavanangcao.dao.ThongKeDAO;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.model.ThongBao;
import com.mycompany.duanjavanangcao.util.SessionManager;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableModel;

/**
 * Form Trang chủ hệ thống - Thiết kế chuẩn NetBeans Swing GUI Builder
 * Điều hướng chức năng và phân quyền theo vai trò (ADMIN, GIANGVIEN, SINHVIEN)
 */
public class FrmTrangChu extends javax.swing.JFrame {

    private final ThongKeDAO thongKeDAO;
    private final ThongBaoDAO thongBaoDAO;
    private List<ThongBao> danhSachThongBao;

    /**
     * Khởi tạo form FrmTrangChu
     */
    public FrmTrangChu() {
        initComponents();
        getContentPane().setBackground(new java.awt.Color(244, 247, 251));

        // Tùy chỉnh trực quan cho các thẻ thống kê tổng quan (Stat Cards)
        javax.swing.border.Border cardBorder = javax.swing.BorderFactory.createCompoundBorder(
                javax.swing.BorderFactory.createLineBorder(new java.awt.Color(216, 224, 234), 1),
                javax.swing.BorderFactory.createEmptyBorder(4, 6, 4, 6));
        java.awt.Color cardBg = new java.awt.Color(248, 250, 252);

        for (javax.swing.JLabel lbl : new javax.swing.JLabel[]{lblStatSV, lblStatGV, lblStatLop, lblStatMon, lblStatLHP}) {
            lbl.setOpaque(true);
            lbl.setBackground(cardBg);
            lbl.setBorder(cardBorder);
            lbl.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        }

        // Định dạng bảng thông báo chuẩn phong cách UTT
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatTable(tblThongBao);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.applyThemeToContainer(this);

        this.setLocationRelativeTo(null);
        this.thongKeDAO = new ThongKeDAO();
        this.thongBaoDAO = new ThongBaoDAO();

        khoiTaoDuLieuVaPhanQuyen();
    }

    /**
     * Kiểm tra phiên đăng nhập, hiển thị thông tin và phân quyền người dùng
     */
    private void khoiTaoDuLieuVaPhanQuyen() {
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

        // 1. Hiển thị thông tin người dùng
        lblNguoiDung.setText("Xin chào: " + currentUser.getHoTen()
                + " (" + currentUser.getTenDangNhap() + ") | Quyền hạn: " + currentUser.getVaiTro());

        // 2. Thực hiện phân quyền chức năng
        phanQuyen(currentUser.getVaiTro());

        // 3. Tải số liệu thống kê tổng quan
        taiThongKe();

        // 4. Tải danh sách thông báo
        taiThongBao();
    }

    /**
     * Phân quyền hiển thị theo vai trò người dùng
     * @param vaiTro ADMIN, GIANGVIEN, SINHVIEN
     */
    private void phanQuyen(String vaiTro) {
        if ("ADMIN".equalsIgnoreCase(vaiTro)) {
            // Quản trị viên: Toàn quyền truy cập
            pnlQuanLy.setVisible(true);
            btnNhapDiem.setEnabled(true);
            btnDangKyHocPhan.setEnabled(true);
            btnTraCuuDiem.setEnabled(true);
            btnKetQuaGPA.setEnabled(true);
            btnPhucKhao.setEnabled(true);
            btnCanhCao.setEnabled(true);
            btnThongKe.setEnabled(true);

        } else if ("GIANGVIEN".equalsIgnoreCase(vaiTro)) {
            // Giảng viên: Ẩn quản trị danh mục, tập trung nhập điểm và học vụ
            pnlQuanLy.setVisible(false);

            btnNhapDiem.setEnabled(true);
            btnDangKyHocPhan.setEnabled(false); // Giảng viên không đăng ký môn
            btnTraCuuDiem.setEnabled(true);
            btnKetQuaGPA.setEnabled(false);
            btnPhucKhao.setEnabled(true);       // Xử lý phúc khảo
            btnCanhCao.setEnabled(true);       // Xem danh sách cảnh báo
            btnThongKe.setEnabled(true);

        } else if ("SINHVIEN".equalsIgnoreCase(vaiTro)) {
            // Sinh viên: Ẩn quản trị danh mục, cấm nhập điểm
            pnlQuanLy.setVisible(false);

            btnNhapDiem.setEnabled(false);      // Sinh viên tuyệt đối không thể sửa điểm
            btnDangKyHocPhan.setEnabled(true);  // Đăng ký môn
            btnTraCuuDiem.setEnabled(true);     // Xem điểm
            btnKetQuaGPA.setEnabled(true);      // Xem GPA
            btnPhucKhao.setEnabled(true);       // Gửi đơn phúc khảo
            btnCanhCao.setEnabled(true);       // Xem cảnh báo của bản thân
            btnThongKe.setEnabled(false);
        }
    }

    /**
     * Tải số liệu thống kê từ CSDL
     */
    public void taiThongKe() {
        Map<String, Integer> stats = thongKeDAO.getThongKeTongQuan();
        lblStatSV.setText("<html><center><div style='font-size:10px;color:#64748B;'>SINH VIÊN</div><div style='font-size:15px;font-weight:bold;color:#2563EB;margin-top:2px;'>" + stats.getOrDefault("SoSV", 0) + "</div></center></html>");
        lblStatGV.setText("<html><center><div style='font-size:10px;color:#64748B;'>GIẢNG VIÊN</div><div style='font-size:15px;font-weight:bold;color:#15803D;margin-top:2px;'>" + stats.getOrDefault("SoGV", 0) + "</div></center></html>");
        lblStatLop.setText("<html><center><div style='font-size:10px;color:#64748B;'>LỚP HỌC</div><div style='font-size:15px;font-weight:bold;color:#17365D;margin-top:2px;'>" + stats.getOrDefault("SoLop", 0) + "</div></center></html>");
        lblStatMon.setText("<html><center><div style='font-size:10px;color:#64748B;'>MÔN HỌC</div><div style='font-size:15px;font-weight:bold;color:#D97706;margin-top:2px;'>" + stats.getOrDefault("SoMon", 0) + "</div></center></html>");
        lblStatLHP.setText("<html><center><div style='font-size:10px;color:#64748B;'>LỚP HỌC PHẦN</div><div style='font-size:15px;font-weight:bold;color:#F28C28;margin-top:2px;'>" + stats.getOrDefault("SoLHP", 0) + "</div></center></html>");
    }

    /**
     * Tải thông báo vào JTable
     */
    public void taiThongBao() {
        TaiKhoan user = SessionManager.getCurrentUser();
        int userId = (user != null) ? user.getTaiKhoanId() : 0;

        danhSachThongBao = thongBaoDAO.findRecentByTaiKhoan(userId);
        DefaultTableModel model = (DefaultTableModel) tblThongBao.getModel();
        model.setRowCount(0);

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        int stt = 1;
        for (ThongBao tb : danhSachThongBao) {
            String thoiGian = (tb.getThoiGianTao() != null) ? sdf.format(tb.getThoiGianTao()) : "";
            model.addRow(new Object[]{
                stt++,
                tb.getTieuDe(),
                tb.getLoaiThongBao() != null ? tb.getLoaiThongBao() : "Chung",
                thoiGian
            });
        }
    }

    /**
     * Thông báo mở module sắp tới
     */
    private void thongBaoDieuHuong(String tenChucNang, String giaiDoanKeTiep) {
        JOptionPane.showMessageDialog(this,
                "Chức năng: [" + tenChucNang + "]\n"
                + "Đã sẵn sàng điều hướng và sẽ được hoàn thiện trong " + giaiDoanKeTiep + "!",
                "Điều hướng chức năng",
                JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlHeader = new javax.swing.JPanel();
        lblTieuDe = new javax.swing.JLabel();
        lblNguoiDung = new javax.swing.JLabel();
        btnDangXuat = new javax.swing.JButton();
        pnlQuanLy = new javax.swing.JPanel();
        btnQLSinhVien = new javax.swing.JButton();
        btnQLGiangVien = new javax.swing.JButton();
        btnQLLop = new javax.swing.JButton();
        btnQLMonHoc = new javax.swing.JButton();
        btnQLHocKy = new javax.swing.JButton();
        btnQLLopHocPhan = new javax.swing.JButton();
        btnQLTaiKhoan = new javax.swing.JButton();
        pnlNghiepVu = new javax.swing.JPanel();
        btnNhapDiem = new javax.swing.JButton();
        btnDangKyHocPhan = new javax.swing.JButton();
        btnTraCuuDiem = new javax.swing.JButton();
        btnKetQuaGPA = new javax.swing.JButton();
        btnPhucKhao = new javax.swing.JButton();
        btnCanhCao = new javax.swing.JButton();
        btnThongKe = new javax.swing.JButton();
        pnlThongKe = new javax.swing.JPanel();
        lblStatSV = new javax.swing.JLabel();
        lblStatGV = new javax.swing.JLabel();
        lblStatLop = new javax.swing.JLabel();
        lblStatMon = new javax.swing.JLabel();
        lblStatLHP = new javax.swing.JLabel();
        pnlThongBao = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblThongBao = new javax.swing.JTable();
        btnXemThongBao = new javax.swing.JButton();
        btnLamMoiThongBao = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TRANG CHỦ - HỆ THỐNG QUẢN LÝ ĐIỂM SINH VIÊN");

        pnlHeader.setBackground(new java.awt.Color(23, 54, 93));

        lblTieuDe.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTieuDe.setForeground(new java.awt.Color(255, 255, 255));
        lblTieuDe.setText("TRƯỜNG ĐẠI HỌC CÔNG NGHỆ GTVT - CỔNG QUẢN LÝ ĐÀO TẠO");

        lblNguoiDung.setFont(new java.awt.Font("Segoe UI", 2, 13)); // NOI18N
        lblNguoiDung.setForeground(new java.awt.Color(219, 234, 254));
        lblNguoiDung.setText("Xin chào: ... | Vai trò: ...");

        btnDangXuat.setBackground(new java.awt.Color(220, 38, 38));
        btnDangXuat.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnDangXuat.setForeground(new java.awt.Color(255, 255, 255));
        btnDangXuat.setText("Đăng xuất");
        btnDangXuat.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDangXuat.setFocusPainted(false);
        btnDangXuat.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDangXuatActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlHeaderLayout = new javax.swing.GroupLayout(pnlHeader);
        pnlHeader.setLayout(pnlHeaderLayout);
        pnlHeaderLayout.setHorizontalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblTieuDe)
                    .addComponent(lblNguoiDung))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(btnDangXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTieuDe)
                    .addComponent(btnDangXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNguoiDung)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pnlQuanLy.setBackground(new java.awt.Color(23, 54, 93));
        pnlQuanLy.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "QUẢN LÝ DANH MỤC (ADMIN)", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(242, 140, 40))); // NOI18N

        btnQLSinhVien.setBackground(new java.awt.Color(255, 255, 255));
        btnQLSinhVien.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnQLSinhVien.setForeground(new java.awt.Color(23, 54, 93));
        btnQLSinhVien.setText("Quản lý Sinh viên");
        btnQLSinhVien.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQLSinhVien.setFocusPainted(false);
        btnQLSinhVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQLSinhVienActionPerformed(evt);
            }
        });

        btnQLGiangVien.setBackground(new java.awt.Color(255, 255, 255));
        btnQLGiangVien.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnQLGiangVien.setForeground(new java.awt.Color(23, 54, 93));
        btnQLGiangVien.setText("Quản lý Giảng viên");
        btnQLGiangVien.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQLGiangVien.setFocusPainted(false);
        btnQLGiangVien.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQLGiangVienActionPerformed(evt);
            }
        });

        btnQLLop.setBackground(new java.awt.Color(255, 255, 255));
        btnQLLop.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnQLLop.setForeground(new java.awt.Color(23, 54, 93));
        btnQLLop.setText("Quản lý Lớp học");
        btnQLLop.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQLLop.setFocusPainted(false);
        btnQLLop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQLLopActionPerformed(evt);
            }
        });

        btnQLMonHoc.setBackground(new java.awt.Color(255, 255, 255));
        btnQLMonHoc.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnQLMonHoc.setForeground(new java.awt.Color(23, 54, 93));
        btnQLMonHoc.setText("Quản lý Môn học");
        btnQLMonHoc.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQLMonHoc.setFocusPainted(false);
        btnQLMonHoc.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQLMonHocActionPerformed(evt);
            }
        });

        btnQLHocKy.setBackground(new java.awt.Color(255, 255, 255));
        btnQLHocKy.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnQLHocKy.setForeground(new java.awt.Color(23, 54, 93));
        btnQLHocKy.setText("Quản lý Học kỳ");
        btnQLHocKy.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQLHocKy.setFocusPainted(false);
        btnQLHocKy.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQLHocKyActionPerformed(evt);
            }
        });

        btnQLLopHocPhan.setBackground(new java.awt.Color(255, 255, 255));
        btnQLLopHocPhan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnQLLopHocPhan.setForeground(new java.awt.Color(23, 54, 93));
        btnQLLopHocPhan.setText("Quản lý Lớp học phần");
        btnQLLopHocPhan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQLLopHocPhan.setFocusPainted(false);
        btnQLLopHocPhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQLLopHocPhanActionPerformed(evt);
            }
        });

        btnQLTaiKhoan.setBackground(new java.awt.Color(255, 255, 255));
        btnQLTaiKhoan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnQLTaiKhoan.setForeground(new java.awt.Color(23, 54, 93));
        btnQLTaiKhoan.setText("Quản lý Tài khoản");
        btnQLTaiKhoan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQLTaiKhoan.setFocusPainted(false);
        btnQLTaiKhoan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQLTaiKhoanActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlQuanLyLayout = new javax.swing.GroupLayout(pnlQuanLy);
        pnlQuanLy.setLayout(pnlQuanLyLayout);
        pnlQuanLyLayout.setHorizontalGroup(
            pnlQuanLyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQuanLyLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlQuanLyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnQLSinhVien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQLGiangVien, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQLLop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQLMonHoc, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQLHocKy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQLLopHocPhan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQLTaiKhoan, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        pnlQuanLyLayout.setVerticalGroup(
            pnlQuanLyLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlQuanLyLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnQLSinhVien, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnQLGiangVien, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnQLLop, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnQLMonHoc, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnQLHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnQLLopHocPhan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnQLTaiKhoan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        pnlNghiepVu.setBackground(new java.awt.Color(23, 54, 93));
        pnlNghiepVu.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "NGHIỆP VỤ HỌC VỤ & ĐIỂM", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(242, 140, 40))); // NOI18N

        btnNhapDiem.setBackground(new java.awt.Color(255, 255, 255));
        btnNhapDiem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnNhapDiem.setForeground(new java.awt.Color(23, 54, 93));
        btnNhapDiem.setText("Nhập điểm học phần");
        btnNhapDiem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnNhapDiem.setFocusPainted(false);
        btnNhapDiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnNhapDiemActionPerformed(evt);
            }
        });

        btnDangKyHocPhan.setBackground(new java.awt.Color(255, 255, 255));
        btnDangKyHocPhan.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnDangKyHocPhan.setForeground(new java.awt.Color(23, 54, 93));
        btnDangKyHocPhan.setText("Đăng ký học phần");
        btnDangKyHocPhan.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnDangKyHocPhan.setFocusPainted(false);
        btnDangKyHocPhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnDangKyHocPhanActionPerformed(evt);
            }
        });

        btnTraCuuDiem.setBackground(new java.awt.Color(255, 255, 255));
        btnTraCuuDiem.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnTraCuuDiem.setForeground(new java.awt.Color(23, 54, 93));
        btnTraCuuDiem.setText("Tra cứu điểm sinh viên");
        btnTraCuuDiem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnTraCuuDiem.setFocusPainted(false);
        btnTraCuuDiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnTraCuuDiemActionPerformed(evt);
            }
        });

        btnKetQuaGPA.setBackground(new java.awt.Color(255, 255, 255));
        btnKetQuaGPA.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnKetQuaGPA.setForeground(new java.awt.Color(23, 54, 93));
        btnKetQuaGPA.setText("Kết quả học tập & GPA");
        btnKetQuaGPA.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnKetQuaGPA.setFocusPainted(false);
        btnKetQuaGPA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnKetQuaGPAActionPerformed(evt);
            }
        });

        btnPhucKhao.setBackground(new java.awt.Color(255, 255, 255));
        btnPhucKhao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnPhucKhao.setForeground(new java.awt.Color(23, 54, 93));
        btnPhucKhao.setText("Phúc khảo điểm");
        btnPhucKhao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnPhucKhao.setFocusPainted(false);
        btnPhucKhao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPhucKhaoActionPerformed(evt);
            }
        });

        btnCanhCao.setBackground(new java.awt.Color(255, 255, 255));
        btnCanhCao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnCanhCao.setForeground(new java.awt.Color(23, 54, 93));
        btnCanhCao.setText("Cảnh báo học tập");
        btnCanhCao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnCanhCao.setFocusPainted(false);
        btnCanhCao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCanhCaoActionPerformed(evt);
            }
        });

        btnThongKe.setBackground(new java.awt.Color(255, 255, 255));
        btnThongKe.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnThongKe.setForeground(new java.awt.Color(23, 54, 93));
        btnThongKe.setText("Thống kê & Báo cáo");
        btnThongKe.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnThongKe.setFocusPainted(false);
        btnThongKe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnThongKeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlNghiepVuLayout = new javax.swing.GroupLayout(pnlNghiepVu);
        pnlNghiepVu.setLayout(pnlNghiepVuLayout);
        pnlNghiepVuLayout.setHorizontalGroup(
            pnlNghiepVuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNghiepVuLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlNghiepVuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNhapDiem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDangKyHocPhan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnTraCuuDiem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnKetQuaGPA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnPhucKhao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnCanhCao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnThongKe, javax.swing.GroupLayout.DEFAULT_SIZE, 238, Short.MAX_VALUE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        pnlNghiepVuLayout.setVerticalGroup(
            pnlNghiepVuLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlNghiepVuLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnNhapDiem, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnDangKyHocPhan, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnTraCuuDiem, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnKetQuaGPA, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnPhucKhao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnCanhCao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(6, 6, 6)
                .addComponent(btnThongKe, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(10, Short.MAX_VALUE))
        );

        pnlThongKe.setBackground(new java.awt.Color(255, 255, 255));
        pnlThongKe.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CHỈ SỐ THỐNG KÊ TỔNG QUAN HỆ THỐNG", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(23, 54, 93))); // NOI18N

        lblStatSV.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblStatSV.setForeground(new java.awt.Color(37, 99, 235));
        lblStatSV.setText("Sinh viên: 0");

        lblStatGV.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblStatGV.setForeground(new java.awt.Color(37, 99, 235));
        lblStatGV.setText("Giảng viên: 0");

        lblStatLop.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblStatLop.setForeground(new java.awt.Color(37, 99, 235));
        lblStatLop.setText("Lớp học: 0");

        lblStatMon.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblStatMon.setForeground(new java.awt.Color(37, 99, 235));
        lblStatMon.setText("Môn học: 0");

        lblStatLHP.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblStatLHP.setForeground(new java.awt.Color(37, 99, 235));
        lblStatLHP.setText("Lớp HP: 0");

        javax.swing.GroupLayout pnlThongKeLayout = new javax.swing.GroupLayout(pnlThongKe);
        pnlThongKe.setLayout(pnlThongKeLayout);
        pnlThongKeLayout.setHorizontalGroup(
            pnlThongKeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongKeLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblStatSV, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblStatGV, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblStatLop, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblStatMon, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblStatLHP, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        pnlThongKeLayout.setVerticalGroup(
            pnlThongKeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongKeLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addGroup(pnlThongKeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatSV, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatGV, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatLop, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatMon, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatLHP, javax.swing.GroupLayout.PREFERRED_SIZE, 54, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pnlThongBao.setBackground(new java.awt.Color(255, 255, 255));
        pnlThongBao.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "BẢNG TIN THÔNG BÁO ĐÀO TẠO & HỌC VỤ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(23, 54, 93))); // NOI18N

        tblThongBao.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        tblThongBao.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Tiêu đề", "Loại", "Thời gian"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblThongBao.setRowHeight(26);
        tblThongBao.setSelectionBackground(new java.awt.Color(37, 99, 235));
        tblThongBao.setSelectionForeground(new java.awt.Color(255, 255, 255));
        jScrollPane1.setViewportView(tblThongBao);

        btnXemThongBao.setBackground(new java.awt.Color(37, 99, 235));
        btnXemThongBao.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnXemThongBao.setForeground(new java.awt.Color(255, 255, 255));
        btnXemThongBao.setText("Xem chi tiết");
        btnXemThongBao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXemThongBao.setFocusPainted(false);
        btnXemThongBao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXemThongBaoActionPerformed(evt);
            }
        });

        btnLamMoiThongBao.setBackground(new java.awt.Color(241, 245, 249));
        btnLamMoiThongBao.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnLamMoiThongBao.setForeground(new java.awt.Color(31, 41, 55));
        btnLamMoiThongBao.setText("Làm mới");
        btnLamMoiThongBao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnLamMoiThongBao.setFocusPainted(false);
        btnLamMoiThongBao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnLamMoiThongBaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlThongBaoLayout = new javax.swing.GroupLayout(pnlThongBao);
        pnlThongBao.setLayout(pnlThongBaoLayout);
        pnlThongBaoLayout.setHorizontalGroup(
            pnlThongBaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongBaoLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(pnlThongBaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1)
                    .addGroup(pnlThongBaoLayout.createSequentialGroup()
                        .addComponent(btnXemThongBao, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLamMoiThongBao, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(15, 15, 15))
        );
        pnlThongBaoLayout.setVerticalGroup(
            pnlThongBaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongBaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 385, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlThongBaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXemThongBao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoiThongBao, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(pnlHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(pnlQuanLy, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlNghiepVu, javax.swing.GroupLayout.DEFAULT_SIZE, 280, Short.MAX_VALUE))
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlThongKe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlThongBao, javax.swing.GroupLayout.DEFAULT_SIZE, 680, Short.MAX_VALUE))))
                .addGap(20, 20, 20))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(pnlHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlThongKe, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlThongBao, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(pnlQuanLy, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(pnlNghiepVu, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(20, 20, 20))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void btnDangXuatActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDangXuatActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this,
                "Bạn có chắc chắn muốn đăng xuất?",
                "Xác nhận", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            SessionManager.logout();
            new FrmDangNhap().setVisible(true);
            this.dispose();
        }
    }//GEN-LAST:event_btnDangXuatActionPerformed

    private void btnQLSinhVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQLSinhVienActionPerformed
        new FrmQLSinhVien().setVisible(true);
    }//GEN-LAST:event_btnQLSinhVienActionPerformed

    private void btnQLGiangVienActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQLGiangVienActionPerformed
        new FrmQLGiangVien().setVisible(true);
    }//GEN-LAST:event_btnQLGiangVienActionPerformed

    private void btnQLLopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQLLopActionPerformed
        new FrmQLLop().setVisible(true);
    }//GEN-LAST:event_btnQLLopActionPerformed

    private void btnQLMonHocActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQLMonHocActionPerformed
        new FrmQLMonHoc().setVisible(true);
    }//GEN-LAST:event_btnQLMonHocActionPerformed

    private void btnQLHocKyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQLHocKyActionPerformed
        new FrmQLHocKy().setVisible(true);
    }//GEN-LAST:event_btnQLHocKyActionPerformed

    private void btnQLLopHocPhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQLLopHocPhanActionPerformed
        new FrmQLLopHocPhan().setVisible(true);
    }//GEN-LAST:event_btnQLLopHocPhanActionPerformed

    private void btnQLTaiKhoanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQLTaiKhoanActionPerformed
        new FrmQLTaiKhoan().setVisible(true);
    }//GEN-LAST:event_btnQLTaiKhoanActionPerformed

    private void btnNhapDiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnNhapDiemActionPerformed
        new FrmNhapDiem().setVisible(true);
    }//GEN-LAST:event_btnNhapDiemActionPerformed

    private void btnDangKyHocPhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDangKyHocPhanActionPerformed
        new FrmDangKyHocPhan().setVisible(true);
    }//GEN-LAST:event_btnDangKyHocPhanActionPerformed

    private void btnTraCuuDiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnTraCuuDiemActionPerformed
        new FrmTraCuuDiem().setVisible(true);
    }//GEN-LAST:event_btnTraCuuDiemActionPerformed

    private void btnKetQuaGPAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnKetQuaGPAActionPerformed
        new FrmKetQuaGPA().setVisible(true);
    }//GEN-LAST:event_btnKetQuaGPAActionPerformed

    private void btnPhucKhaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPhucKhaoActionPerformed
        new FrmPhucKhao().setVisible(true);
    }//GEN-LAST:event_btnPhucKhaoActionPerformed

    private void btnCanhCaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnCanhCaoActionPerformed
        new FrmCanhCaoHocTap().setVisible(true);
    }//GEN-LAST:event_btnCanhCaoActionPerformed

    private void btnThongKeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnThongKeActionPerformed
        new FrmThongKeBaoCao().setVisible(true);
    }//GEN-LAST:event_btnThongKeActionPerformed

    private void btnXemThongBaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXemThongBaoActionPerformed
        int selectedRow = tblThongBao.getSelectedRow();
        if (selectedRow < 0 || danhSachThongBao == null || selectedRow >= danhSachThongBao.size()) {
            JOptionPane.showMessageDialog(this,
                    "Vui lòng chọn một thông báo trên bảng để xem chi tiết!",
                    "Chưa chọn thông báo",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        ThongBao tb = danhSachThongBao.get(selectedRow);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        String thoiGian = (tb.getThoiGianTao() != null) ? sdf.format(tb.getThoiGianTao()) : "Chưa rõ";

        String chiTiet = "Tiêu đề: " + tb.getTieuDe() + "\n"
                + "Loại: " + tb.getLoaiThongBao() + "\n"
                + "Thời gian: " + thoiGian + "\n\n"
                + "Nội dung:\n" + tb.getNoiDung();

        JOptionPane.showMessageDialog(this, chiTiet, "Chi tiết thông báo", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnXemThongBaoActionPerformed

    private void btnLamMoiThongBaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiThongBaoActionPerformed
        taiThongKe();
        taiThongBao();
        JOptionPane.showMessageDialog(this, "Đã làm mới dữ liệu thống kê và thông báo!", "Thông báo", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_btnLamMoiThongBaoActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set UTT Theme and Look and Feel */
        com.mycompany.duanjavanangcao.util.UIThemeUtil.applyGlobalTheme();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FrmTrangChu().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCanhCao;
    private javax.swing.JButton btnDangKyHocPhan;
    private javax.swing.JButton btnDangXuat;
    private javax.swing.JButton btnKetQuaGPA;
    private javax.swing.JButton btnLamMoiThongBao;
    private javax.swing.JButton btnNhapDiem;
    private javax.swing.JButton btnPhucKhao;
    private javax.swing.JButton btnQLGiangVien;
    private javax.swing.JButton btnQLHocKy;
    private javax.swing.JButton btnQLLop;
    private javax.swing.JButton btnQLLopHocPhan;
    private javax.swing.JButton btnQLMonHoc;
    private javax.swing.JButton btnQLSinhVien;
    private javax.swing.JButton btnQLTaiKhoan;
    private javax.swing.JButton btnThongKe;
    private javax.swing.JButton btnTraCuuDiem;
    private javax.swing.JButton btnXemThongBao;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblNguoiDung;
    private javax.swing.JLabel lblStatGV;
    private javax.swing.JLabel lblStatLHP;
    private javax.swing.JLabel lblStatLop;
    private javax.swing.JLabel lblStatMon;
    private javax.swing.JLabel lblStatSV;
    private javax.swing.JLabel lblTieuDe;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlNghiepVu;
    private javax.swing.JPanel pnlQuanLy;
    private javax.swing.JPanel pnlThongBao;
    private javax.swing.JPanel pnlThongKe;
    private javax.swing.JTable tblThongBao;
    // End of variables declaration//GEN-END:variables
}


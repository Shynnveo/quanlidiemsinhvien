/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.duanjavanangcao.view;

import com.mycompany.duanjavanangcao.dao.ThongBaoDAO;
import com.mycompany.duanjavanangcao.dao.ThongKeDAO;
import com.mycompany.duanjavanangcao.model.TaiKhoan;
import com.mycompany.duanjavanangcao.model.ThongBao;
import com.mycompany.duanjavanangcao.util.SessionManager;
import java.awt.Color;
import java.awt.Font;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Map;
import javax.swing.JOptionPane;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;

/**
 * Màn hình Trang chủ - Cổng quản lý đào tạo & điểm sinh viên
 * Chuẩn phong cách thiết kế UTT Academic Portal
 * 
 * @author Admin
 */
public class FrmTrangChu extends javax.swing.JFrame {

    private final ThongKeDAO thongKeDAO;
    private final ThongBaoDAO thongBaoDAO;
    private List<ThongBao> danhSachThongBao;
    private final java.util.Map<String, javax.swing.JFrame> activeChildForms = new java.util.HashMap<>();

    /**
     * Creates new form FrmTrangChu
     */
    public FrmTrangChu() {
        initComponents();

        // 1. Áp dụng giao diện Trang chủ toàn diện theo chuẩn UTT Design System
        com.mycompany.duanjavanangcao.util.UIThemeUtil.applyTrangChuTheme(
                this,
                pnlHeader,
                pnlMenuTree,
                pnlThongKe,
                pnlThongBao,
                pnlThaoTacNhanh,
                lblAvatar,
                btnDangXuat,
                btnQuickNhapDiem,
                btnQuickTraCuu,
                btnQuickGPA,
                btnQuickBaoCao,
                treeMenu,
                tblThongBao,
                btnXemThongBao,
                btnLamMoiThongBao
        );

        // 2. Định dạng các thẻ thống kê tổng quan với nền bo góc và icon vector sắc nét
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatStatCard(lblStatSV, com.mycompany.duanjavanangcao.util.UIThemeUtil.StatCardType.SINH_VIEN, 0);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatStatCard(lblStatGV, com.mycompany.duanjavanangcao.util.UIThemeUtil.StatCardType.GIANG_VIEN, 0);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatStatCard(lblStatLop, com.mycompany.duanjavanangcao.util.UIThemeUtil.StatCardType.LOP_HOC, 0);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatStatCard(lblStatMon, com.mycompany.duanjavanangcao.util.UIThemeUtil.StatCardType.MON_HOC, 0);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatStatCard(lblStatLHP, com.mycompany.duanjavanangcao.util.UIThemeUtil.StatCardType.LOP_HOC_PHAN, 0);

        // 3. Chuẩn hóa độ rộng cột bảng thông báo
        if (tblThongBao.getColumnModel().getColumnCount() >= 4) {
            tblThongBao.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblThongBao.getColumnModel().getColumn(0).setMaxWidth(60);
            tblThongBao.getColumnModel().getColumn(1).setPreferredWidth(220);
            tblThongBao.getColumnModel().getColumn(2).setPreferredWidth(90);
            tblThongBao.getColumnModel().getColumn(3).setPreferredWidth(120);

            DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
            tblThongBao.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            tblThongBao.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            tblThongBao.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        }

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
     * Phân quyền hiển thị theo 3 nhóm chức năng phù hợp vai trò người dùng
     * @param vaiTro ADMIN, GIANGVIEN, SINHVIEN
     */
    private void phanQuyen(String vaiTro) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("MENU CHỨC NĂNG");

        // Nhóm 1: QUẢN LÝ DANH MỤC (Chỉ dành cho ADMIN)
        if ("ADMIN".equalsIgnoreCase(vaiTro)) {
            DefaultMutableTreeNode nhom1 = new DefaultMutableTreeNode("QUẢN LÝ DANH MỤC (ADMIN)");
            nhom1.add(new DefaultMutableTreeNode("Quản lý Sinh viên"));
            nhom1.add(new DefaultMutableTreeNode("Quản lý Giảng viên"));
            nhom1.add(new DefaultMutableTreeNode("Quản lý Lớp học"));
            nhom1.add(new DefaultMutableTreeNode("Quản lý Môn học"));
            nhom1.add(new DefaultMutableTreeNode("Quản lý Học kỳ"));
            nhom1.add(new DefaultMutableTreeNode("Quản lý Lớp học phần"));
            nhom1.add(new DefaultMutableTreeNode("Quản lý Tài khoản"));
            root.add(nhom1);
        }

        // Nhóm 2: NGHIỆP VỤ ĐÀO TẠO & ĐIỂM
        DefaultMutableTreeNode nhom2 = new DefaultMutableTreeNode("NGHIỆP VỤ ĐÀO TẠO & ĐIỂM");
        if ("ADMIN".equalsIgnoreCase(vaiTro)) {
            nhom2.add(new DefaultMutableTreeNode("Nhập điểm học phần"));
            nhom2.add(new DefaultMutableTreeNode("Đăng ký học phần"));
            nhom2.add(new DefaultMutableTreeNode("Tra cứu điểm sinh viên"));
            nhom2.add(new DefaultMutableTreeNode("Phúc khảo điểm"));
        } else if ("GIANGVIEN".equalsIgnoreCase(vaiTro)) {
            nhom2.add(new DefaultMutableTreeNode("Nhập điểm học phần"));
            nhom2.add(new DefaultMutableTreeNode("Tra cứu điểm sinh viên"));
            nhom2.add(new DefaultMutableTreeNode("Phúc khảo điểm"));
        } else if ("SINHVIEN".equalsIgnoreCase(vaiTro)) {
            nhom2.add(new DefaultMutableTreeNode("Đăng ký học phần"));
            nhom2.add(new DefaultMutableTreeNode("Tra cứu điểm sinh viên"));
            nhom2.add(new DefaultMutableTreeNode("Phúc khảo điểm"));
        }
        root.add(nhom2);

        // Nhóm 3: HỌC VỤ & BÁO CÁO
        DefaultMutableTreeNode nhom3 = new DefaultMutableTreeNode("HỌC VỤ & BÁO CÁO");
        if ("ADMIN".equalsIgnoreCase(vaiTro)) {
            nhom3.add(new DefaultMutableTreeNode("Kết quả học tập & GPA"));
            nhom3.add(new DefaultMutableTreeNode("Cảnh báo học tập"));
            nhom3.add(new DefaultMutableTreeNode("Thống kê & Báo cáo"));
        } else if ("GIANGVIEN".equalsIgnoreCase(vaiTro)) {
            nhom3.add(new DefaultMutableTreeNode("Cảnh báo học tập"));
            nhom3.add(new DefaultMutableTreeNode("Thống kê & Báo cáo"));
        } else if ("SINHVIEN".equalsIgnoreCase(vaiTro)) {
            nhom3.add(new DefaultMutableTreeNode("Kết quả học tập & GPA"));
            nhom3.add(new DefaultMutableTreeNode("Cảnh báo học tập"));
        }
        root.add(nhom3);

        treeMenu.setModel(new DefaultTreeModel(root));

        // Mở rộng toàn bộ các nhánh của cây chức năng
        for (int i = 0; i < treeMenu.getRowCount(); i++) {
            treeMenu.expandRow(i);
        }
    }

    /**
     * Tải số liệu thống kê từ CSDL và định dạng rõ ràng
     */
    public void taiThongKe() {
        Map<String, Integer> stats = thongKeDAO.getThongKeTongQuan();
        com.mycompany.duanjavanangcao.util.UIThemeUtil.updateStatCard(lblStatSV, com.mycompany.duanjavanangcao.util.UIThemeUtil.StatCardType.SINH_VIEN, stats.getOrDefault("SoSV", 0));
        com.mycompany.duanjavanangcao.util.UIThemeUtil.updateStatCard(lblStatGV, com.mycompany.duanjavanangcao.util.UIThemeUtil.StatCardType.GIANG_VIEN, stats.getOrDefault("SoGV", 0));
        com.mycompany.duanjavanangcao.util.UIThemeUtil.updateStatCard(lblStatLop, com.mycompany.duanjavanangcao.util.UIThemeUtil.StatCardType.LOP_HOC, stats.getOrDefault("SoLop", 0));
        com.mycompany.duanjavanangcao.util.UIThemeUtil.updateStatCard(lblStatMon, com.mycompany.duanjavanangcao.util.UIThemeUtil.StatCardType.MON_HOC, stats.getOrDefault("SoMon", 0));
        com.mycompany.duanjavanangcao.util.UIThemeUtil.updateStatCard(lblStatLHP, com.mycompany.duanjavanangcao.util.UIThemeUtil.StatCardType.LOP_HOC_PHAN, stats.getOrDefault("SoLHP", 0));
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
        lblAvatar = new javax.swing.JLabel();
        pnlMenuTree = new javax.swing.JPanel();
        scrollTree = new javax.swing.JScrollPane();
        treeMenu = new javax.swing.JTree();
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
        pnlThaoTacNhanh = new javax.swing.JPanel();
        btnQuickNhapDiem = new javax.swing.JButton();
        btnQuickTraCuu = new javax.swing.JButton();
        btnQuickGPA = new javax.swing.JButton();
        btnQuickBaoCao = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("TRANG CHỦ - HỆ THỐNG QUẢN LÝ ĐIỂM SINH VIÊN");

        pnlHeader.setBackground(new java.awt.Color(23, 54, 93));

        lblTieuDe.setFont(new java.awt.Font("Segoe UI", 1, 18)); // NOI18N
        lblTieuDe.setForeground(new java.awt.Color(255, 255, 255));
        lblTieuDe.setText("TRƯỜNG ĐẠI HỌC CÔNG NGHỆ GTVT - CỔNG QUẢN LÝ ĐÀO TẠO");

        lblNguoiDung.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        lblNguoiDung.setForeground(new java.awt.Color(219, 234, 254));
        lblNguoiDung.setText("Xin chào: ... | Vai trò: ...");

        btnDangXuat.setBackground(new java.awt.Color(239, 68, 68));
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

        lblAvatar.setFont(new java.awt.Font("Segoe UI Symbol", 1, 22)); // NOI18N
        lblAvatar.setForeground(new java.awt.Color(219, 234, 254));
        lblAvatar.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblAvatar.setText("👤");
        lblAvatar.setToolTipText("Tài khoản người dùng");

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
                .addComponent(btnDangXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(12, 12, 12)
                .addComponent(lblAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 36, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
        );
        pnlHeaderLayout.setVerticalGroup(
            pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlHeaderLayout.createSequentialGroup()
                .addGap(12, 12, 12)
                .addGroup(pnlHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblTieuDe)
                    .addComponent(btnDangXuat, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAvatar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblNguoiDung)
                .addContainerGap(12, Short.MAX_VALUE))
        );

        pnlMenuTree.setBackground(new java.awt.Color(255, 255, 255));
        pnlMenuTree.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ĐIỀU HƯỚNG CHỨC NĂNG", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(23, 54, 93))); // NOI18N

        scrollTree.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        treeMenu.setFont(new java.awt.Font("Segoe UI", 0, 13)); // NOI18N
        javax.swing.tree.DefaultMutableTreeNode treeNode1 = new javax.swing.tree.DefaultMutableTreeNode("MENU CHỨC NĂNG");
        javax.swing.tree.DefaultMutableTreeNode treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("QUẢN LÝ DANH MỤC (ADMIN)");
        javax.swing.tree.DefaultMutableTreeNode treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Quản lý Sinh viên");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Quản lý Giảng viên");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Quản lý Lớp học");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Quản lý Môn học");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Quản lý Học kỳ");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Quản lý Lớp học phần");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Quản lý Tài khoản");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("NGHIỆP VỤ ĐÀO TẠO & ĐIỂM");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Nhập điểm học phần");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Đăng ký học phần");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Tra cứu điểm sinh viên");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Phúc khảo điểm");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("HỌC VỤ & BÁO CÁO");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Kết quả học tập & GPA");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Cảnh báo học tập");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Thống kê & Báo cáo");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeMenu.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeMenu.setRootVisible(false);
        treeMenu.setRowHeight(36);
        treeMenu.setShowsRootHandles(true);
        treeMenu.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                treeMenuMouseClicked(evt);
            }
        });
        treeMenu.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                treeMenuKeyPressed(evt);
            }
        });
        scrollTree.setViewportView(treeMenu);

        javax.swing.GroupLayout pnlMenuTreeLayout = new javax.swing.GroupLayout(pnlMenuTree);
        pnlMenuTree.setLayout(pnlMenuTreeLayout);
        pnlMenuTreeLayout.setHorizontalGroup(
            pnlMenuTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuTreeLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(scrollTree, javax.swing.GroupLayout.DEFAULT_SIZE, 230, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        pnlMenuTreeLayout.setVerticalGroup(
            pnlMenuTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuTreeLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(scrollTree, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
                .addGap(12, 12, 12))
        );

        pnlThongKe.setBackground(new java.awt.Color(255, 255, 255));
        pnlThongKe.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "CHỈ SỐ THỐNG KÊ TỔNG QUAN HỆ THỐNG", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(23, 54, 93))); // NOI18N

        lblStatSV.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblStatSV.setForeground(new java.awt.Color(37, 99, 235));
        lblStatSV.setText("Sinh viên: 0");

        lblStatGV.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblStatGV.setForeground(new java.awt.Color(22, 163, 74));
        lblStatGV.setText("Giảng viên: 0");

        lblStatLop.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblStatLop.setForeground(new java.awt.Color(23, 54, 93));
        lblStatLop.setText("Lớp học: 0");

        lblStatMon.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblStatMon.setForeground(new java.awt.Color(217, 119, 6));
        lblStatMon.setText("Môn học: 0");

        lblStatLHP.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblStatLHP.setForeground(new java.awt.Color(242, 140, 40));
        lblStatLHP.setText("Lớp HP: 0");

        javax.swing.GroupLayout pnlThongKeLayout = new javax.swing.GroupLayout(pnlThongKe);
        pnlThongKe.setLayout(pnlThongKeLayout);
        pnlThongKeLayout.setHorizontalGroup(
            pnlThongKeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongKeLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(lblStatSV, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(lblStatGV, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(lblStatLop, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(lblStatMon, javax.swing.GroupLayout.DEFAULT_SIZE, 125, Short.MAX_VALUE)
                .addGap(12, 12, 12)
                .addComponent(lblStatLHP, javax.swing.GroupLayout.DEFAULT_SIZE, 129, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );
        pnlThongKeLayout.setVerticalGroup(
            pnlThongKeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongKeLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(pnlThongKeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblStatSV, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatGV, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatLop, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatMon, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblStatLHP, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE))
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
        tblThongBao.setRowHeight(30);
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
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addGroup(pnlThongBaoLayout.createSequentialGroup()
                        .addComponent(btnXemThongBao, javax.swing.GroupLayout.PREFERRED_SIZE, 130, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(btnLamMoiThongBao, javax.swing.GroupLayout.PREFERRED_SIZE, 110, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addGap(15, 15, 15))
        );
        pnlThongBaoLayout.setVerticalGroup(
            pnlThongBaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThongBaoLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 408, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(pnlThongBaoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnXemThongBao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoiThongBao, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        pnlThaoTacNhanh.setBackground(new java.awt.Color(255, 255, 255));
        pnlThaoTacNhanh.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "THAO TÁC NHANH", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(23, 54, 93))); // NOI18N

        btnQuickNhapDiem.setBackground(new java.awt.Color(224, 242, 254));
        btnQuickNhapDiem.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnQuickNhapDiem.setForeground(new java.awt.Color(2, 132, 199));
        btnQuickNhapDiem.setText("  Nhập điểm");
        btnQuickNhapDiem.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQuickNhapDiem.setFocusPainted(false);
        btnQuickNhapDiem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuickNhapDiemActionPerformed(evt);
            }
        });

        btnQuickTraCuu.setBackground(new java.awt.Color(220, 252, 231));
        btnQuickTraCuu.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnQuickTraCuu.setForeground(new java.awt.Color(22, 163, 74));
        btnQuickTraCuu.setText("Tra cứu điểm");
        btnQuickTraCuu.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQuickTraCuu.setFocusPainted(false);
        btnQuickTraCuu.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuickTraCuuActionPerformed(evt);
            }
        });

        btnQuickGPA.setBackground(new java.awt.Color(224, 231, 255));
        btnQuickGPA.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnQuickGPA.setForeground(new java.awt.Color(79, 70, 229));
        btnQuickGPA.setText("Kết quả GPA");
        btnQuickGPA.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQuickGPA.setFocusPainted(false);
        btnQuickGPA.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuickGPAActionPerformed(evt);
            }
        });

        btnQuickBaoCao.setBackground(new java.awt.Color(255, 237, 213));
        btnQuickBaoCao.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        btnQuickBaoCao.setForeground(new java.awt.Color(234, 88, 12));
        btnQuickBaoCao.setText("Thống kê & Báo cáo");
        btnQuickBaoCao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnQuickBaoCao.setFocusPainted(false);
        btnQuickBaoCao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnQuickBaoCaoActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout pnlThaoTacNhanhLayout = new javax.swing.GroupLayout(pnlThaoTacNhanh);
        pnlThaoTacNhanh.setLayout(pnlThaoTacNhanhLayout);
        pnlThaoTacNhanhLayout.setHorizontalGroup(
            pnlThaoTacNhanhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThaoTacNhanhLayout.createSequentialGroup()
                .addGap(14, 14, 14)
                .addGroup(pnlThaoTacNhanhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(btnQuickNhapDiem, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQuickTraCuu, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQuickGPA, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnQuickBaoCao, javax.swing.GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE))
                .addGap(14, 14, 14))
        );
        pnlThaoTacNhanhLayout.setVerticalGroup(
            pnlThaoTacNhanhLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlThaoTacNhanhLayout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(btnQuickNhapDiem, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnQuickTraCuu, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnQuickGPA, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnQuickBaoCao, javax.swing.GroupLayout.PREFERRED_SIZE, 48, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                        .addComponent(pnlMenuTree, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlThongKe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(pnlThongBao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addGap(18, 18, 18)
                                .addComponent(pnlThaoTacNhanh, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))))
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
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(pnlThongBao, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(pnlThaoTacNhanh, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addComponent(pnlMenuTree, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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

    private void treeMenuMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_treeMenuMouseClicked
        if (evt.getClickCount() == 2 && javax.swing.SwingUtilities.isLeftMouseButton(evt)) {
            xuLyKichHoatNode();
        }
    }//GEN-LAST:event_treeMenuMouseClicked

    private void treeMenuKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_treeMenuKeyPressed
        if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
            xuLyKichHoatNode();
        }
    }//GEN-LAST:event_treeMenuKeyPressed

    private void btnQuickNhapDiemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuickNhapDiemActionPerformed
        moChucNangTheoTen("Nhập điểm học phần");
    }//GEN-LAST:event_btnQuickNhapDiemActionPerformed

    private void btnQuickTraCuuActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuickTraCuuActionPerformed
        moChucNangTheoTen("Tra cứu điểm sinh viên");
    }//GEN-LAST:event_btnQuickTraCuuActionPerformed

    private void btnQuickGPAActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuickGPAActionPerformed
        moChucNangTheoTen("Kết quả học tập & GPA");
    }//GEN-LAST:event_btnQuickGPAActionPerformed

    private void btnQuickBaoCaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnQuickBaoCaoActionPerformed
        moChucNangTheoTen("Thống kê & Báo cáo");
    }//GEN-LAST:event_btnQuickBaoCaoActionPerformed

    private void xuLyKichHoatNode() {
        javax.swing.tree.TreePath path = treeMenu.getSelectionPath();
        if (path == null) {
            return;
        }
        javax.swing.tree.DefaultMutableTreeNode selectedNode =
                (javax.swing.tree.DefaultMutableTreeNode) path.getLastPathComponent();
        if (!selectedNode.isLeaf()) {
            if (treeMenu.isExpanded(path)) {
                treeMenu.collapsePath(path);
            } else {
                treeMenu.expandPath(path);
            }
            return;
        }
        String tenChucNang = selectedNode.getUserObject().toString().trim();
        moChucNangTheoTen(tenChucNang);
    }

    /**
     * Mở một form con duy nhất, nếu form đang mở thì đưa lên phía trước (toFront).
     * Khi form đóng thì tự động dọn dẹp khỏi danh sách quản lý.
     */
    private void moFormCon(String maChucNang, java.util.function.Supplier<javax.swing.JFrame> formSupplier) {
        javax.swing.JFrame existing = activeChildForms.get(maChucNang);
        if (existing != null && existing.isDisplayable()) {
            if (existing.getState() == java.awt.Frame.ICONIFIED) {
                existing.setState(java.awt.Frame.NORMAL);
            }
            existing.toFront();
            existing.requestFocus();
            return;
        }

        javax.swing.JFrame formMoi = formSupplier.get();
        formMoi.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosed(java.awt.event.WindowEvent e) {
                activeChildForms.remove(maChucNang);
            }
        });
        activeChildForms.put(maChucNang, formMoi);
        formMoi.setVisible(true);
    }

    private void moChucNangTheoTen(String tenChucNang) {
        TaiKhoan currentUser = SessionManager.getCurrentUser();
        String vaiTro = (currentUser != null) ? currentUser.getVaiTro() : "";

        switch (tenChucNang) {
            case "Quản lý Sinh viên":
                if (kiemTraQuyenAdmin(vaiTro)) moFormCon(tenChucNang, FrmQLSinhVien::new);
                break;
            case "Quản lý Giảng viên":
                if (kiemTraQuyenAdmin(vaiTro)) moFormCon(tenChucNang, FrmQLGiangVien::new);
                break;
            case "Quản lý Lớp học":
                if (kiemTraQuyenAdmin(vaiTro)) moFormCon(tenChucNang, FrmQLLop::new);
                break;
            case "Quản lý Môn học":
                if (kiemTraQuyenAdmin(vaiTro)) moFormCon(tenChucNang, FrmQLMonHoc::new);
                break;
            case "Quản lý Học kỳ":
                if (kiemTraQuyenAdmin(vaiTro)) moFormCon(tenChucNang, FrmQLHocKy::new);
                break;
            case "Quản lý Lớp học phần":
                if (kiemTraQuyenAdmin(vaiTro)) moFormCon(tenChucNang, FrmQLLopHocPhan::new);
                break;
            case "Quản lý Tài khoản":
                if (kiemTraQuyenAdmin(vaiTro)) moFormCon(tenChucNang, FrmQLTaiKhoan::new);
                break;
            case "Nhập điểm học phần":
                if ("SINHVIEN".equalsIgnoreCase(vaiTro)) {
                    JOptionPane.showMessageDialog(this,
                            "Sinh viên không có quyền truy cập Nhập điểm!",
                            "Từ chối", JOptionPane.WARNING_MESSAGE);
                } else {
                    moFormCon(tenChucNang, FrmNhapDiem::new);
                }
                break;
            case "Đăng ký học phần":
                if ("GIANGVIEN".equalsIgnoreCase(vaiTro)) {
                    JOptionPane.showMessageDialog(this,
                            "Giảng viên không sử dụng chức năng Đăng ký học phần!",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    moFormCon(tenChucNang, FrmDangKyHocPhan::new);
                }
                break;
            case "Tra cứu điểm sinh viên":
                moFormCon(tenChucNang, FrmTraCuuDiem::new);
                break;
            case "Kết quả học tập & GPA":
                if ("GIANGVIEN".equalsIgnoreCase(vaiTro)) {
                    JOptionPane.showMessageDialog(this,
                            "Chức năng này dành cho Sinh viên và Quản trị viên!",
                            "Thông báo", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    moFormCon(tenChucNang, FrmKetQuaGPA::new);
                }
                break;
            case "Phúc khảo điểm":
                moFormCon(tenChucNang, FrmPhucKhao::new);
                break;
            case "Cảnh báo học tập":
                moFormCon(tenChucNang, FrmCanhCaoHocTap::new);
                break;
            case "Thống kê & Báo cáo":
                if ("SINHVIEN".equalsIgnoreCase(vaiTro)) {
                    JOptionPane.showMessageDialog(this,
                            "Sinh viên không có quyền truy cập Thống kê & Báo cáo!",
                            "Từ chối", JOptionPane.WARNING_MESSAGE);
                } else {
                    moFormCon(tenChucNang, FrmThongKeBaoCao::new);
                }
                break;
            default:
                break;
        }
    }

    private boolean kiemTraQuyenAdmin(String vaiTro) {
        if (!"ADMIN".equalsIgnoreCase(vaiTro)) {
            JOptionPane.showMessageDialog(this,
                    "Chỉ Quản trị viên (ADMIN) mới có quyền truy cập danh mục này!",
                    "Từ chối truy cập", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

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
    private javax.swing.JButton btnDangXuat;
    private javax.swing.JButton btnLamMoiThongBao;
    private javax.swing.JButton btnQuickBaoCao;
    private javax.swing.JButton btnQuickGPA;
    private javax.swing.JButton btnQuickNhapDiem;
    private javax.swing.JButton btnQuickTraCuu;
    private javax.swing.JButton btnXemThongBao;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblAvatar;
    private javax.swing.JLabel lblNguoiDung;
    private javax.swing.JLabel lblStatGV;
    private javax.swing.JLabel lblStatLHP;
    private javax.swing.JLabel lblStatLop;
    private javax.swing.JLabel lblStatMon;
    private javax.swing.JLabel lblStatSV;
    private javax.swing.JLabel lblTieuDe;
    private javax.swing.JPanel pnlHeader;
    private javax.swing.JPanel pnlMenuTree;
    private javax.swing.JPanel pnlThaoTacNhanh;
    private javax.swing.JPanel pnlThongBao;
    private javax.swing.JPanel pnlThongKe;
    private javax.swing.JScrollPane scrollTree;
    private javax.swing.JTable tblThongBao;
    private javax.swing.JTree treeMenu;
    // End of variables declaration//GEN-END:variables
}

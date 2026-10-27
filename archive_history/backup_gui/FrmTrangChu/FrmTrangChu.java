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

    // Quản lý các cửa sổ con đang mở để tránh mở nhiều bản sao
    private final java.util.Map<String, javax.swing.JFrame> activeChildForms = new java.util.HashMap<>();

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
        if (tblThongBao.getColumnModel().getColumnCount() >= 4) {
            tblThongBao.getColumnModel().getColumn(0).setPreferredWidth(50);
            tblThongBao.getColumnModel().getColumn(0).setMaxWidth(60);
            tblThongBao.getColumnModel().getColumn(1).setPreferredWidth(320);
            tblThongBao.getColumnModel().getColumn(2).setPreferredWidth(120);
            tblThongBao.getColumnModel().getColumn(3).setPreferredWidth(140);

            javax.swing.table.DefaultTableCellRenderer centerRenderer = new javax.swing.table.DefaultTableCellRenderer();
            centerRenderer.setHorizontalAlignment(javax.swing.JLabel.CENTER);
            tblThongBao.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
            tblThongBao.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
            tblThongBao.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        }
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
     * Phân quyền hiển thị theo vai trò người dùng trên JTree
     * @param vaiTro ADMIN, GIANGVIEN, SINHVIEN
     */
    private void phanQuyen(String vaiTro) {
        javax.swing.tree.DefaultMutableTreeNode root = new javax.swing.tree.DefaultMutableTreeNode("MENU CHỨC NĂNG");

        // Nhóm 1: Quản lý danh mục (chỉ dành cho Quản trị viên ADMIN)
        if ("ADMIN".equalsIgnoreCase(vaiTro)) {
            javax.swing.tree.DefaultMutableTreeNode nhom1 = new javax.swing.tree.DefaultMutableTreeNode("QUẢN LÝ DANH MỤC (ADMIN)");
            nhom1.add(new javax.swing.tree.DefaultMutableTreeNode("Quản lý Sinh viên"));
            nhom1.add(new javax.swing.tree.DefaultMutableTreeNode("Quản lý Giảng viên"));
            nhom1.add(new javax.swing.tree.DefaultMutableTreeNode("Quản lý Lớp học"));
            nhom1.add(new javax.swing.tree.DefaultMutableTreeNode("Quản lý Môn học"));
            nhom1.add(new javax.swing.tree.DefaultMutableTreeNode("Quản lý Học kỳ"));
            nhom1.add(new javax.swing.tree.DefaultMutableTreeNode("Quản lý Lớp học phần"));
            nhom1.add(new javax.swing.tree.DefaultMutableTreeNode("Quản lý Tài khoản"));
            root.add(nhom1);
        }

        // Nhóm 2: Nghiệp vụ học vụ & điểm
        javax.swing.tree.DefaultMutableTreeNode nhom2 = new javax.swing.tree.DefaultMutableTreeNode("NGHIỆP VỤ HỌC VỤ & ĐIỂM");
        if ("ADMIN".equalsIgnoreCase(vaiTro)) {
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Nhập điểm học phần"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Đăng ký học phần"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Tra cứu điểm sinh viên"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Kết quả học tập & GPA"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Phúc khảo điểm"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Cảnh báo học tập"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Thống kê & Báo cáo"));
        } else if ("GIANGVIEN".equalsIgnoreCase(vaiTro)) {
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Nhập điểm học phần"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Tra cứu điểm sinh viên"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Phúc khảo điểm"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Cảnh báo học tập"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Thống kê & Báo cáo"));
        } else if ("SINHVIEN".equalsIgnoreCase(vaiTro)) {
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Đăng ký học phần"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Tra cứu điểm sinh viên"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Kết quả học tập & GPA"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Phúc khảo điểm"));
            nhom2.add(new javax.swing.tree.DefaultMutableTreeNode("Cảnh báo học tập"));
        }
        root.add(nhom2);

        treeMenu.setModel(new javax.swing.tree.DefaultTreeModel(root));

        // Mở rộng toàn bộ các nhánh của cây chức năng
        for (int i = 0; i < treeMenu.getRowCount(); i++) {
            treeMenu.expandRow(i);
        }
    }

    /**
     * Tải số liệu thống kê từ CSDL
     */
    public void taiThongKe() {
        Map<String, Integer> stats = thongKeDAO.getThongKeTongQuan();
        lblStatSV.setText("<html><center><div style='font-size:11px;font-weight:bold;color:#64748B;'>SINH VIÊN</div><div style='font-size:18px;font-weight:bold;color:#2563EB;margin-top:3px;'>" + stats.getOrDefault("SoSV", 0) + "</div></center></html>");
        lblStatGV.setText("<html><center><div style='font-size:11px;font-weight:bold;color:#64748B;'>GIẢNG VIÊN</div><div style='font-size:18px;font-weight:bold;color:#15803D;margin-top:3px;'>" + stats.getOrDefault("SoGV", 0) + "</div></center></html>");
        lblStatLop.setText("<html><center><div style='font-size:11px;font-weight:bold;color:#64748B;'>LỚP HỌC</div><div style='font-size:18px;font-weight:bold;color:#17365D;margin-top:3px;'>" + stats.getOrDefault("SoLop", 0) + "</div></center></html>");
        lblStatMon.setText("<html><center><div style='font-size:11px;font-weight:bold;color:#64748B;'>MÔN HỌC</div><div style='font-size:18px;font-weight:bold;color:#D97706;margin-top:3px;'>" + stats.getOrDefault("SoMon", 0) + "</div></center></html>");
        lblStatLHP.setText("<html><center><div style='font-size:11px;font-weight:bold;color:#64748B;'>LỚP HỌC PHẦN</div><div style='font-size:18px;font-weight:bold;color:#F28C28;margin-top:3px;'>" + stats.getOrDefault("SoLHP", 0) + "</div></center></html>");
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

        pnlMenuTree.setBackground(new java.awt.Color(255, 255, 255));
        pnlMenuTree.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "ĐIỀU HƯỚNG CHỨC NĂNG", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 13), new java.awt.Color(23, 54, 93))); // NOI18N

        scrollTree.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(216, 224, 234)));

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
        treeNode2 = new javax.swing.tree.DefaultMutableTreeNode("NGHIỆP VỤ HỌC VỤ & ĐIỂM");
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Nhập điểm học phần");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Đăng ký học phần");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Tra cứu điểm sinh viên");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Kết quả học tập & GPA");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Phúc khảo điểm");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Cảnh báo học tập");
        treeNode2.add(treeNode3);
        treeNode3 = new javax.swing.tree.DefaultMutableTreeNode("Thống kê & Báo cáo");
        treeNode2.add(treeNode3);
        treeNode1.add(treeNode2);
        treeMenu.setModel(new javax.swing.tree.DefaultTreeModel(treeNode1));
        treeMenu.setRootVisible(false);
        treeMenu.setRowHeight(32);
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
                .addComponent(scrollTree, javax.swing.GroupLayout.DEFAULT_SIZE, 248, Short.MAX_VALUE)
                .addGap(10, 10, 10))
        );
        pnlMenuTreeLayout.setVerticalGroup(
            pnlMenuTreeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlMenuTreeLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(scrollTree)
                .addGap(12, 12, 12))
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
                        .addComponent(pnlMenuTree, javax.swing.GroupLayout.PREFERRED_SIZE, 280, javax.swing.GroupLayout.PREFERRED_SIZE)
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
    private javax.swing.JPanel pnlMenuTree;
    private javax.swing.JPanel pnlThongBao;
    private javax.swing.JPanel pnlThongKe;
    private javax.swing.JScrollPane scrollTree;
    private javax.swing.JTable tblThongBao;
    private javax.swing.JTree treeMenu;
    // End of variables declaration//GEN-END:variables
}


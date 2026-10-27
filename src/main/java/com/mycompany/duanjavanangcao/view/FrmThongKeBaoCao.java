package com.mycompany.duanjavanangcao.view;

import com.mycompany.duanjavanangcao.dao.HocKyDAO;
import com.mycompany.duanjavanangcao.dao.LopDAO;
import com.mycompany.duanjavanangcao.dao.LopHocPhanDAO;
import com.mycompany.duanjavanangcao.dao.ThongKeDAO;
import com.mycompany.duanjavanangcao.model.HocKy;
import com.mycompany.duanjavanangcao.model.Lop;
import com.mycompany.duanjavanangcao.model.LopHocPhan;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Màn hình Thống kê & Báo cáo kết quả học tập, học phần và cảnh báo học vụ
 */
public class FrmThongKeBaoCao extends javax.swing.JFrame {

    private final ThongKeDAO thongKeDAO = new ThongKeDAO();
    private final HocKyDAO hocKyDAO = new HocKyDAO();
    private final LopDAO lopDAO = new LopDAO();
    private final LopHocPhanDAO lopHocPhanDAO = new LopHocPhanDAO();

    private boolean isDataReady = false;

    public FrmThongKeBaoCao() {
        initComponents();
        getContentPane().setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.BG_LIGHT);
        pnlBoLoc.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlTongQuan.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        pnlBangThongKe.setBackground(com.mycompany.duanjavanangcao.util.UIThemeUtil.PANEL_WHITE);
        com.mycompany.duanjavanangcao.util.UIThemeUtil.formatTable(tblThongKe);
        btnXuatBaoCao.setBackground(new java.awt.Color(242, 140, 40)); // Cam UTT nổi bật
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

        // 3. Nạp danh sách lớp học phần
        napDanhSachLopHocPhan();

        isDataReady = true;
        capNhatDieuKhienBoLoc();
        thucHienThongKe();
    }

    private void napDanhSachLopHocPhan() {
        HocKy selectedHk = (HocKy) cboHocKy.getSelectedItem();
        int hocKyId = (selectedHk != null) ? selectedHk.getHocKyId() : 0;

        DefaultComboBoxModel<LopHocPhan> modelLhp = new DefaultComboBoxModel<>();
        LopHocPhan defaultLhp = new LopHocPhan();
        defaultLhp.setLopHocPhanId(0);
        defaultLhp.setMaLopHocPhan("-- Chọn lớp học phần --");
        defaultLhp.setTenMonHoc("");
        modelLhp.addElement(defaultLhp);

        List<LopHocPhan> listLhp;
        if (hocKyId > 0) {
            listLhp = lopHocPhanDAO.findByHocKy(hocKyId);
        } else {
            listLhp = lopHocPhanDAO.findAll();
        }

        for (LopHocPhan lhp : listLhp) {
            modelLhp.addElement(lhp);
        }
        cboLopHocPhan.setModel(modelLhp);
    }

    private void capNhatDieuKhienBoLoc() {
        int loaiBaoCao = cboLoaiBaoCao.getSelectedIndex();
        switch (loaiBaoCao) {
            case 0: // Thống kê xếp loại học lực (GPA)
                cboHocKy.setEnabled(true);
                cboLop.setEnabled(true);
                cboLopHocPhan.setEnabled(false);
                break;
            case 1: // Thống kê phổ điểm học phần
                cboHocKy.setEnabled(true);
                cboLop.setEnabled(false);
                cboLopHocPhan.setEnabled(true);
                break;
            case 2: // Thống kê cảnh báo học tập
                cboHocKy.setEnabled(true);
                cboLop.setEnabled(false);
                cboLopHocPhan.setEnabled(false);
                break;
            default:
                break;
        }
    }

    private void thucHienThongKe() {
        if (!isDataReady) {
            return;
        }

        int loaiBaoCao = cboLoaiBaoCao.getSelectedIndex();
        btnXemThongKe.setEnabled(false);
        setCursor(java.awt.Cursor.getPredefinedCursor(java.awt.Cursor.WAIT_CURSOR));

        // Chuẩn bị tham số trên EDT trước khi chuyển xuống luồng nền
        HocKy hk = (HocKy) cboHocKy.getSelectedItem();
        Lop lop = (Lop) cboLop.getSelectedItem();
        LopHocPhan lhp = (LopHocPhan) cboLopHocPhan.getSelectedItem();

        int hocKyId = (hk != null) ? hk.getHocKyId() : 0;
        int lopId = (lop != null) ? lop.getLopId() : 0;
        int lhpId = (lhp != null) ? lhp.getLopHocPhanId() : 0;

        // Sử dụng SwingWorker để thực thi truy vấn SQL dưới nền, không chặn giao diện (EDT)
        javax.swing.SwingWorker<Object, Void> worker = new javax.swing.SwingWorker<Object, Void>() {
            @Override
            protected Object doInBackground() throws Exception {
                // Tác vụ nền: Gọi truy vấn DAO lấy dữ liệu từ SQL Server
                switch (loaiBaoCao) {
                    case 0:
                        return thongKeDAO.thongKeXepLoaiHocLuc(hocKyId, lopId);
                    case 1:
                        if (lhpId <= 0) return null;
                        return thongKeDAO.thongKeDiemHocPhan(lhpId);
                    case 2:
                        return thongKeDAO.thongKeCanhCaoHocTap(hocKyId);
                    default:
                        return null;
                }
            }

            @Override
            protected void done() {
                try {
                    Object ketQua = get();
                    switch (loaiBaoCao) {
                        case 0:
                            @SuppressWarnings("unchecked")
                            Map<String, Integer> stats0 = (Map<String, Integer>) ketQua;
                            hienThiXepLoaiGPA(stats0);
                            break;
                        case 1:
                            @SuppressWarnings("unchecked")
                            Map<String, Object> stats1 = (Map<String, Object>) ketQua;
                            hienThiPhoDiemHocPhan(stats1);
                            break;
                        case 2:
                            @SuppressWarnings("unchecked")
                            Map<String, Integer> stats2 = (Map<String, Integer>) ketQua;
                            hienThiCanhCaoHocVu(stats2);
                            break;
                        default:
                            break;
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(FrmThongKeBaoCao.this,
                            "Lỗi khi lấy dữ liệu thống kê: " + ex.getMessage(),
                            "Lỗi truy vấn", JOptionPane.ERROR_MESSAGE);
                } finally {
                    btnXemThongKe.setEnabled(true);
                    setCursor(java.awt.Cursor.getDefaultCursor());
                }
            }
        };
        worker.execute();
    }

    /**
     * Báo cáo 1: Cập nhật giao diện thống kê xếp loại học lực theo thang điểm GPA hệ 4
     */
    private void hienThiXepLoaiGPA(Map<String, Integer> stats) {
        if (stats == null) stats = new java.util.HashMap<>();
        int xs = stats.getOrDefault("Xuất sắc (>= 3.60)", 0);
        int gioi = stats.getOrDefault("Giỏi (3.20 - 3.59)", 0);
        int kha = stats.getOrDefault("Khá (2.50 - 3.19)", 0);
        int tb = stats.getOrDefault("Trung bình (2.00 - 2.49)", 0);
        int yeuKem = stats.getOrDefault("Yếu / Kém (< 2.00)", 0);
        int tongSV = stats.getOrDefault("Tổng số sinh viên", 0);

        int dat = xs + gioi + kha + tb;
        double tyLeDat = (tongSV > 0) ? (dat * 100.0 / tongSV) : 0.0;

        // Cập nhật chỉ số tổng quan
        lblChiSo1.setText("Tổng số sinh viên: " + tongSV);
        lblChiSo2.setText(String.format("Tỷ lệ Đạt (>= 2.0): %.1f%%", tyLeDat));
        lblChiSo3.setText("Xuất sắc & Giỏi: " + (xs + gioi) + " SV");
        lblChiSo4.setText("Yếu / Kém: " + yeuKem + " SV");

        // Cập nhật bảng chi tiết
        String[] columns = {"STT", "Xếp loại học lực", "Tiêu chuẩn GPA (hệ 4)", "Số lượng SV", "Tỷ lệ (%)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        themDongThongKe(model, 1, "Xuất sắc", "GPA >= 3.60", xs, tongSV);
        themDongThongKe(model, 2, "Giỏi", "3.20 <= GPA < 3.60", gioi, tongSV);
        themDongThongKe(model, 3, "Khá", "2.50 <= GPA < 3.20", kha, tongSV);
        themDongThongKe(model, 4, "Trung bình", "2.00 <= GPA < 2.50", tb, tongSV);
        themDongThongKe(model, 5, "Yếu / Kém", "GPA < 2.00", yeuKem, tongSV);

        model.addRow(new Object[]{"", "TỔNG CỘNG", "Toàn bộ sinh viên được xếp loại", tongSV, tongSV > 0 ? "100.0%" : "0.0%"});
        tblThongKe.setModel(model);
    }

    /**
     * Báo cáo 2: Cập nhật giao diện thống kê phổ điểm học phần (A, B+, B, C+, C, D+, D, F)
     */
    private void hienThiPhoDiemHocPhan(Map<String, Object> stats) {
        String[] columns = {"STT", "Điểm chữ", "Thang điểm 10 quy đổi", "Số lượng SV", "Tỷ lệ (%)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        if (stats == null) {
            lblChiSo1.setText("Tổng sinh viên: 0");
            lblChiSo2.setText("Tỷ lệ đạt: 0.0%");
            lblChiSo3.setText("Điểm TB: 0.00");
            lblChiSo4.setText("Cao nhất / Thấp nhất: 0.0 / 0.0");
            tblThongKe.setModel(model);
            return;
        }

        int tongSV = (int) stats.getOrDefault("TongSV", 0);
        double tyLeDat = (double) stats.getOrDefault("TyLeDat", 0.0);
        double diemTB = (double) stats.getOrDefault("DiemTB", 0.0);
        double maxDiem = (double) stats.getOrDefault("DiemCaoNhat", 0.0);
        double minDiem = (double) stats.getOrDefault("DiemThapNhat", 0.0);

        lblChiSo1.setText("Tổng sinh viên: " + tongSV);
        lblChiSo2.setText(String.format("Tỷ lệ đạt (>= 4.0): %.1f%%", tyLeDat));
        lblChiSo3.setText(String.format("Điểm TB: %.2f", diemTB));
        lblChiSo4.setText(String.format("Cao nhất / Thấp nhất: %.1f / %.1f", maxDiem, minDiem));

        @SuppressWarnings("unchecked")
        Map<String, Integer> phoDiem = (Map<String, Integer>) stats.get("PhoDiem");

        if (phoDiem != null) {
            themDongThongKe(model, 1, "A", "8.5 - 10.0 (Giỏi / Xuất sắc)", phoDiem.getOrDefault("A", 0), tongSV);
            themDongThongKe(model, 2, "B+", "8.0 - 8.4 (Khá giỏi)", phoDiem.getOrDefault("B+", 0), tongSV);
            themDongThongKe(model, 3, "B", "7.0 - 7.9 (Khá)", phoDiem.getOrDefault("B", 0), tongSV);
            themDongThongKe(model, 4, "C+", "6.5 - 6.9 (Trung bình khá)", phoDiem.getOrDefault("C+", 0), tongSV);
            themDongThongKe(model, 5, "C", "5.5 - 6.4 (Trung bình)", phoDiem.getOrDefault("C", 0), tongSV);
            themDongThongKe(model, 6, "D+", "5.0 - 5.4 (Trung bình yếu)", phoDiem.getOrDefault("D+", 0), tongSV);
            themDongThongKe(model, 7, "D", "4.0 - 4.9 (Đạt mức tối thiểu)", phoDiem.getOrDefault("D", 0), tongSV);
            themDongThongKe(model, 8, "F", "< 4.0 (Không đạt / Học lại)", phoDiem.getOrDefault("F", 0), tongSV);
        }

        model.addRow(new Object[]{"", "TỔNG CỘNG", "Toàn bộ sinh viên dự thi", tongSV, tongSV > 0 ? "100.0%" : "0.0%"});
        tblThongKe.setModel(model);
    }

    /**
     * Báo cáo 3: Cập nhật giao diện thống kê sinh viên bị cảnh báo học tập theo học kỳ
     */
    private void hienThiCanhCaoHocVu(Map<String, Integer> stats) {
        if (stats == null) stats = new java.util.HashMap<>();
        int tong = stats.getOrDefault("Tổng số cảnh báo", 0);
        int m1 = stats.getOrDefault("Cảnh báo mức 1", 0);
        int m2 = stats.getOrDefault("Cảnh báo mức 2", 0);
        int m3 = stats.getOrDefault("Cảnh báo mức 3 (Buộc thôi học)", 0);

        lblChiSo1.setText("Tổng số cảnh báo: " + tong);
        lblChiSo2.setText("Mức 1 (Nhắc nhở): " + m1);
        lblChiSo3.setText("Mức 2 (Giảm tín chỉ): " + m2);
        lblChiSo4.setText("Mức 3 (Buộc thôi học): " + m3);

        String[] columns = {"STT", "Mức độ cảnh báo", "Biện pháp xử lý học vụ", "Số lượng SV", "Tỷ lệ (%)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        themDongThongKe(model, 1, "Cảnh báo mức 1", "Nhắc nhở học tập & thông báo về gia đình", m1, tong);
        themDongThongKe(model, 2, "Cảnh báo mức 2", "Giới hạn đăng ký tối đa 14 tín chỉ trong kỳ tiếp theo", m2, tong);
        themDongThongKe(model, 3, "Cảnh báo mức 3", "Đề nghị Hội đồng Kỷ luật xem xét Buộc thôi học", m3, tong);

        model.addRow(new Object[]{"", "TỔNG CỘNG", "Tổng số trường hợp cảnh báo học tập", tong, tong > 0 ? "100.0%" : "0.0%"});
        tblThongKe.setModel(model);
    }

    private void themDongThongKe(DefaultTableModel model, int stt, String chiTieu, String moTa, int soLuong, int tong) {
        double tyLe = (tong > 0) ? (soLuong * 100.0 / tong) : 0.0;
        model.addRow(new Object[]{
            stt,
            chiTieu,
            moTa,
            soLuong,
            String.format("%.1f%%", tyLe)
        });
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
        lblLoaiBaoCao = new javax.swing.JLabel();
        cboLoaiBaoCao = new javax.swing.JComboBox<>();
        lblHocKy = new javax.swing.JLabel();
        cboHocKy = new javax.swing.JComboBox<>();
        lblLop = new javax.swing.JLabel();
        cboLop = new javax.swing.JComboBox<>();
        lblLopHocPhan = new javax.swing.JLabel();
        cboLopHocPhan = new javax.swing.JComboBox<>();
        btnXemThongKe = new javax.swing.JButton();
        btnXuatBaoCao = new javax.swing.JButton();
        btnLamMoi = new javax.swing.JButton();
        btnDong = new javax.swing.JButton();
        pnlTongQuan = new javax.swing.JPanel();
        lblChiSo1 = new javax.swing.JLabel();
        lblChiSo2 = new javax.swing.JLabel();
        lblChiSo3 = new javax.swing.JLabel();
        lblChiSo4 = new javax.swing.JLabel();
        pnlBangThongKe = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblThongKe = new javax.swing.JTable();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("THỐNG KÊ & BÁO CÁO KẾT QUẢ HỌC TẬP - UTT");

        lblTieuDe.setFont(new java.awt.Font("Segoe UI", 1, 20)); // NOI18N
        lblTieuDe.setForeground(new java.awt.Color(30, 58, 138));
        lblTieuDe.setText("BÁO CÁO THỐNG KÊ KẾT QUẢ HỌC TẬP & HỌC VỤ");

        pnlBoLoc.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Lựa chọn tiêu chí báo cáo & Bộ lọc", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblLoaiBaoCao.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblLoaiBaoCao.setForeground(new java.awt.Color(31, 41, 55));
        lblLoaiBaoCao.setText("Loại báo cáo:");

        cboLoaiBaoCao.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        cboLoaiBaoCao.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "1. Thống kê xếp loại học lực (GPA)", "2. Thống kê phổ điểm học phần", "3. Thống kê cảnh báo học tập" }));
        cboLoaiBaoCao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLoaiBaoCaoActionPerformed(evt);
            }
        });

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

        lblLopHocPhan.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        lblLopHocPhan.setForeground(new java.awt.Color(31, 41, 55));
        lblLopHocPhan.setText("Lớp học phần:");

        cboLopHocPhan.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        cboLopHocPhan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cboLopHocPhanActionPerformed(evt);
            }
        });

        btnXemThongKe.setBackground(new java.awt.Color(37, 99, 235));
        btnXemThongKe.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXemThongKe.setForeground(new java.awt.Color(255, 255, 255));
        btnXemThongKe.setText("Xem thống kê");
        btnXemThongKe.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXemThongKe.setFocusPainted(false);
        btnXemThongKe.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXemThongKeActionPerformed(evt);
            }
        });

        btnXuatBaoCao.setBackground(new java.awt.Color(21, 128, 61));
        btnXuatBaoCao.setFont(new java.awt.Font("Segoe UI", 1, 12)); // NOI18N
        btnXuatBaoCao.setForeground(new java.awt.Color(255, 255, 255));
        btnXuatBaoCao.setText("Xuất báo cáo (CSV)");
        btnXuatBaoCao.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        btnXuatBaoCao.setFocusPainted(false);
        btnXuatBaoCao.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnXuatBaoCaoActionPerformed(evt);
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
                .addGroup(pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlBoLocLayout.createSequentialGroup()
                        .addComponent(lblLoaiBaoCao)
                        .addGap(8, 8, 8)
                        .addComponent(cboLoaiBaoCao, javax.swing.GroupLayout.PREFERRED_SIZE, 260, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(lblHocKy)
                        .addGap(8, 8, 8)
                        .addComponent(cboHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15)
                        .addComponent(lblLop)
                        .addGap(8, 8, 8)
                        .addComponent(cboLop, 0, 150, Short.MAX_VALUE))
                    .addGroup(pnlBoLocLayout.createSequentialGroup()
                        .addComponent(lblLopHocPhan)
                        .addGap(8, 8, 8)
                        .addComponent(cboLopHocPhan, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(15, 15, 15)
                        .addComponent(btnXemThongKe, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnXuatBaoCao, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(8, 8, 8)
                        .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 75, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(12, 12, 12))
        );
        pnlBoLocLayout.setVerticalGroup(
            pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBoLocLayout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addGroup(pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLoaiBaoCao)
                    .addComponent(cboLoaiBaoCao, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblHocKy)
                    .addComponent(cboHocKy, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblLop)
                    .addComponent(cboLop, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(pnlBoLocLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblLopHocPhan)
                    .addComponent(cboLopHocPhan, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXemThongKe, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnXuatBaoCao, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnLamMoi, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDong, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8))
        );

        pnlTongQuan.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "TỔNG QUAN CHỈ SỐ THỐNG KÊ", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        lblChiSo1.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblChiSo1.setForeground(new java.awt.Color(30, 58, 138));
        lblChiSo1.setText("Tổng số sinh viên: 0");

        lblChiSo2.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblChiSo2.setForeground(new java.awt.Color(21, 128, 61));
        lblChiSo2.setText("Tỷ lệ đạt: 0.0%");

        lblChiSo3.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblChiSo3.setForeground(new java.awt.Color(37, 99, 235));
        lblChiSo3.setText("Điểm trung bình: 0.00");

        lblChiSo4.setFont(new java.awt.Font("Segoe UI", 1, 13)); // NOI18N
        lblChiSo4.setForeground(new java.awt.Color(220, 38, 38));
        lblChiSo4.setText("Số cảnh báo: 0");

        javax.swing.GroupLayout pnlTongQuanLayout = new javax.swing.GroupLayout(pnlTongQuan);
        pnlTongQuan.setLayout(pnlTongQuanLayout);
        pnlTongQuanLayout.setHorizontalGroup(
            pnlTongQuanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTongQuanLayout.createSequentialGroup()
                .addGap(25, 25, 25)
                .addComponent(lblChiSo1, 0, 200, Short.MAX_VALUE)
                .addGap(30, 30, 30)
                .addComponent(lblChiSo2, 0, 200, Short.MAX_VALUE)
                .addGap(30, 30, 30)
                .addComponent(lblChiSo3, 0, 200, Short.MAX_VALUE)
                .addGap(30, 30, 30)
                .addComponent(lblChiSo4, 0, 200, Short.MAX_VALUE)
                .addGap(25, 25, 25))
        );
        pnlTongQuanLayout.setVerticalGroup(
            pnlTongQuanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlTongQuanLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addGroup(pnlTongQuanLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblChiSo1)
                    .addComponent(lblChiSo2)
                    .addComponent(lblChiSo3)
                    .addComponent(lblChiSo4))
                .addGap(10, 10, 10))
        );

        pnlBangThongKe.setBorder(javax.swing.BorderFactory.createTitledBorder(null, "Chi tiết số liệu thống kê báo cáo", javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Segoe UI", 1, 12), new java.awt.Color(30, 58, 138))); // NOI18N

        tblThongKe.setFont(new java.awt.Font("Segoe UI", 0, 12)); // NOI18N
        tblThongKe.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "STT", "Chỉ tiêu thống kê", "Mô tả tiêu chuẩn", "Số lượng", "Tỷ lệ (%)"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        tblThongKe.setRowHeight(24);
        tblThongKe.setSelectionBackground(new java.awt.Color(37, 99, 235));
        tblThongKe.setSelectionForeground(new java.awt.Color(255, 255, 255));
        tblThongKe.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        jScrollPane1.setViewportView(tblThongKe);

        javax.swing.GroupLayout pnlBangThongKeLayout = new javax.swing.GroupLayout(pnlBangThongKe);
        pnlBangThongKe.setLayout(pnlBangThongKeLayout);
        pnlBangThongKeLayout.setHorizontalGroup(
            pnlBangThongKeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBangThongKeLayout.createSequentialGroup()
                .addGap(10, 10, 10)
                .addComponent(jScrollPane1)
                .addGap(10, 10, 10))
        );
        pnlBangThongKeLayout.setVerticalGroup(
            pnlBangThongKeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBangThongKeLayout.createSequentialGroup()
                .addGap(8, 8, 8)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 212, Short.MAX_VALUE)
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
                    .addComponent(pnlTongQuan, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(pnlBangThongKe, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
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
                .addComponent(pnlTongQuan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlBangThongKe, javax.swing.GroupLayout.DEFAULT_SIZE, 250, Short.MAX_VALUE)
                .addGap(15, 15, 15))
        );

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void cboLoaiBaoCaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLoaiBaoCaoActionPerformed
        capNhatDieuKhienBoLoc();
        thucHienThongKe();
    }//GEN-LAST:event_cboLoaiBaoCaoActionPerformed

    private void cboHocKyActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboHocKyActionPerformed
        if (cboLopHocPhan.isEnabled()) {
            napDanhSachLopHocPhan();
        }
        thucHienThongKe();
    }//GEN-LAST:event_cboHocKyActionPerformed

    private void cboLopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLopActionPerformed
        if (cboLop.isEnabled()) {
            thucHienThongKe();
        }
    }//GEN-LAST:event_cboLopActionPerformed

    private void cboLopHocPhanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cboLopHocPhanActionPerformed
        if (cboLopHocPhan.isEnabled()) {
            thucHienThongKe();
        }
    }//GEN-LAST:event_cboLopHocPhanActionPerformed

    private void btnXemThongKeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXemThongKeActionPerformed
        thucHienThongKe();
    }//GEN-LAST:event_btnXemThongKeActionPerformed

    private void btnXuatBaoCaoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnXuatBaoCaoActionPerformed
        if (tblThongKe.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this,
                    "Không có dữ liệu để xuất báo cáo!", "Thông báo", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Lưu file báo cáo thống kê");
        chooser.setFileFilter(new FileNameExtensionFilter("CSV (*.csv)", "csv"));

        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultName = "BaoCao_ThongKe_" + sdf.format(new Date()) + ".csv";
        chooser.setSelectedFile(new File(defaultName));

        int userSelection = chooser.showSaveDialog(this);
        if (userSelection != JFileChooser.APPROVE_OPTION) {
            return;
        }

        File fileToSave = chooser.getSelectedFile();
        if (!fileToSave.getName().toLowerCase().endsWith(".csv")) {
            fileToSave = new File(fileToSave.getParentFile(), fileToSave.getName() + ".csv");
        }

        try (PrintWriter writer = new PrintWriter(new OutputStreamWriter(new FileOutputStream(fileToSave), StandardCharsets.UTF_8))) {
            // Ghi ký tự nhận diện UTF-8 BOM để Excel hiển thị đúng dấu tiếng Việt
            writer.write('\uFEFF');

            writer.println("TRƯỜNG ĐẠI HỌC CÔNG NGHỆ GIAO THÔNG VẬN TẢI - UTT");
            writer.println("HỆ THỐNG QUẢN LÝ ĐIỂM SINH VIÊN");
            writer.println("=================================================");
            writer.println("BÁO CÁO: " + cboLoaiBaoCao.getSelectedItem().toString());
            writer.println("Thời gian xuất: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()));

            HocKy hk = (HocKy) cboHocKy.getSelectedItem();
            writer.println("Học kỳ: " + (hk != null ? hk.getTenHocKy() : "Tất cả"));

            if (cboLop.isEnabled()) {
                Lop lop = (Lop) cboLop.getSelectedItem();
                writer.println("Lớp: " + (lop != null ? lop.getTenLop() : "Tất cả"));
            }
            if (cboLopHocPhan.isEnabled()) {
                LopHocPhan lhp = (LopHocPhan) cboLopHocPhan.getSelectedItem();
                writer.println("Lớp học phần: " + (lhp != null ? lhp.getMaLopHocPhan() + " - " + lhp.getTenMonHoc() : ""));
            }
            writer.println("-------------------------------------------------");
            writer.println("CHỈ SỐ TỔNG QUAN:");
            writer.println(lblChiSo1.getText());
            writer.println(lblChiSo2.getText());
            writer.println(lblChiSo3.getText());
            writer.println(lblChiSo4.getText());
            writer.println("-------------------------------------------------");
            writer.println("CHI TIẾT SỐ LIỆU:");

            // Ghi tiêu đề cột
            DefaultTableModel model = (DefaultTableModel) tblThongKe.getModel();
            StringBuilder headerLine = new StringBuilder();
            for (int col = 0; col < model.getColumnCount(); col++) {
                if (col > 0) {
                    headerLine.append(",");
                }
                headerLine.append("\"").append(model.getColumnName(col)).append("\"");
            }
            writer.println(headerLine.toString());

            // Ghi từng dòng dữ liệu
            for (int row = 0; row < model.getRowCount(); row++) {
                StringBuilder rowLine = new StringBuilder();
                for (int col = 0; col < model.getColumnCount(); col++) {
                    if (col > 0) {
                        rowLine.append(",");
                    }
                    Object val = model.getValueAt(row, col);
                    String text = (val != null) ? val.toString() : "";
                    rowLine.append("\"").append(text.replace("\"", "\"\"")).append("\"");
                }
                writer.println(rowLine.toString());
            }

            JOptionPane.showMessageDialog(this,
                    "Xuất báo cáo thành công!\nĐường dẫn: " + fileToSave.getAbsolutePath(),
                    "Thành công", JOptionPane.INFORMATION_MESSAGE);

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                    "Lỗi khi xuất file báo cáo: " + ex.getMessage(),
                    "Lỗi", JOptionPane.ERROR_MESSAGE);
        }
    }//GEN-LAST:event_btnXuatBaoCaoActionPerformed

    private void btnLamMoiActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnLamMoiActionPerformed
        cboLoaiBaoCao.setSelectedIndex(0);
        cboHocKy.setSelectedIndex(0);
        cboLop.setSelectedIndex(0);
        capNhatDieuKhienBoLoc();
        thucHienThongKe();
    }//GEN-LAST:event_btnLamMoiActionPerformed

    private void btnDongActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnDongActionPerformed
        dispose();
    }//GEN-LAST:event_btnDongActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set UTT Theme and Look and Feel */
        com.mycompany.duanjavanangcao.util.UIThemeUtil.applyGlobalTheme();

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> {
            new FrmThongKeBaoCao().setVisible(true);
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnDong;
    private javax.swing.JButton btnLamMoi;
    private javax.swing.JButton btnXemThongKe;
    private javax.swing.JButton btnXuatBaoCao;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.HocKy> cboHocKy;
    private javax.swing.JComboBox<String> cboLoaiBaoCao;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.Lop> cboLop;
    private javax.swing.JComboBox<com.mycompany.duanjavanangcao.model.LopHocPhan> cboLopHocPhan;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JLabel lblChiSo1;
    private javax.swing.JLabel lblChiSo2;
    private javax.swing.JLabel lblChiSo3;
    private javax.swing.JLabel lblChiSo4;
    private javax.swing.JLabel lblHocKy;
    private javax.swing.JLabel lblLoaiBaoCao;
    private javax.swing.JLabel lblLop;
    private javax.swing.JLabel lblLopHocPhan;
    private javax.swing.JLabel lblTieuDe;
    private javax.swing.JPanel pnlBangThongKe;
    private javax.swing.JPanel pnlBoLoc;
    private javax.swing.JPanel pnlTongQuan;
    private javax.swing.JTable tblThongKe;
    // End of variables declaration//GEN-END:variables
}


package org.delcom.app.views;

import org.delcom.app.controllers.KoleksiController;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.Map;
import java.util.UUID;

public class KoleksiView extends JFrame {

    private KoleksiController controller = new KoleksiController();
    private UUID currentUserId; // Menggunakan UUID sesuai entitas User

    // Komponen UI
    private JTable table;
    private JTextField txtNama, txtHarga;
    private JTextArea txtDeskripsi;
    private JComboBox<String> cmbKategori, cmbKondisi;
    private JLabel lblGambarPreview;
    
    // State Variables
    private File selectedImageFile = null;
    private String selectedId = null; // Disimpan sebagai String agar mudah

    // Panel Chart
    private JPanel chartPanel;

    public KoleksiView(UUID userId) {
        this.currentUserId = userId;
        
        // Setup Window
        setTitle("Aplikasi Manajemen Barang Koleksi");
        setSize(1100, 700); // Ukuran sedikit diperbesar
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // --- 1. Form Input (Panel Kiri) ---
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBorder(BorderFactory.createTitledBorder(" Form Input Data "));
        formPanel.setPreferredSize(new Dimension(350, 0));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Inisialisasi Komponen Form
        txtNama = new JTextField(15);
        txtHarga = new JTextField(15);
        txtDeskripsi = new JTextArea(3, 15);
        txtDeskripsi.setLineWrap(true);
        
        String[] kats = {"Action Figure", "Elektronik", "Kartu", "Kendaraan", "Buku", "Lainnya"};
        cmbKategori = new JComboBox<>(kats);
        
        String[] kons = {"Baru (Mint)", "Bekas (Good)", "Rusak (Damaged)"};
        cmbKondisi = new JComboBox<>(kons);
        
        JButton btnUpload = new JButton("Pilih Gambar...");
        lblGambarPreview = new JLabel("No Image", SwingConstants.CENTER);
        lblGambarPreview.setPreferredSize(new Dimension(120, 120));
        lblGambarPreview.setBorder(BorderFactory.createLineBorder(Color.GRAY));

        // Menyusun Layout Form
        // Baris 0
        addComp(formPanel, new JLabel("Nama Barang:"), 0, 0, gbc);
        addComp(formPanel, txtNama, 1, 0, gbc);
        // Baris 1
        addComp(formPanel, new JLabel("Kategori:"), 0, 1, gbc);
        addComp(formPanel, cmbKategori, 1, 1, gbc);
        // Baris 2
        addComp(formPanel, new JLabel("Kondisi:"), 0, 2, gbc);
        addComp(formPanel, cmbKondisi, 1, 2, gbc);
        // Baris 3
        addComp(formPanel, new JLabel("Taksiran Harga:"), 0, 3, gbc);
        addComp(formPanel, txtHarga, 1, 3, gbc);
        // Baris 4
        addComp(formPanel, new JLabel("Deskripsi:"), 0, 4, gbc);
        addComp(formPanel, new JScrollPane(txtDeskripsi), 1, 4, gbc);
        // Baris 5
        addComp(formPanel, new JLabel("Gambar:"), 0, 5, gbc);
        addComp(formPanel, btnUpload, 1, 5, gbc);
        // Baris 6 - Preview Gambar
        gbc.gridx = 1; gbc.gridy = 6;
        formPanel.add(lblGambarPreview, gbc);

        // Tombol Aksi (Simpan, Hapus, Reset)
        JPanel btnPanel = new JPanel(new FlowLayout());
        JButton btnSimpan = new JButton("Simpan");
        btnSimpan.setBackground(new Color(50, 205, 50)); // Hijau
        btnSimpan.setForeground(Color.WHITE);
        
        JButton btnHapus = new JButton("Hapus");
        btnHapus.setBackground(new Color(220, 20, 60)); // Merah
        btnHapus.setForeground(Color.WHITE);
        
        JButton btnReset = new JButton("Reset");
        
        btnPanel.add(btnSimpan);
        btnPanel.add(btnHapus);
        btnPanel.add(btnReset);
        
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        formPanel.add(btnPanel, gbc);

        add(formPanel, BorderLayout.WEST);

        // --- 2. Tabel Data (Panel Tengah) ---
        table = new JTable();
        table.setRowHeight(25); // Supaya tidak terlalu rapat
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createTitledBorder(" Daftar Barang Koleksi "));
        add(scrollPane, BorderLayout.CENTER);

        // --- 3. Chart Statistik (Panel Bawah) ---
        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChart(g);
            }
        };
        chartPanel.setPreferredSize(new Dimension(800, 180));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createTitledBorder(" Statistik Total Nilai Aset per Kategori "));
        add(chartPanel, BorderLayout.SOUTH);

        // --- Logic / Event Listeners ---
        
        // 1. Load Data Awal
        refreshTable();

        // 2. Event Upload Gambar
        btnUpload.addActionListener(e -> {
            JFileChooser fc = new JFileChooser();
            if (fc.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
                selectedImageFile = fc.getSelectedFile();
                tampilkanPreview(selectedImageFile.getAbsolutePath());
            }
        });

        // 3. Event Simpan Data
        btnSimpan.addActionListener(e -> {
            try {
                // Validasi sederhana
                if (txtNama.getText().isEmpty() || txtHarga.getText().isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Nama dan Harga wajib diisi!");
                    return;
                }

                controller.simpanData(
                    selectedId, // Jika null = Insert, Jika ada = Update
                    currentUserId,
                    txtNama.getText(),
                    (String) cmbKategori.getSelectedItem(),
                    (String) cmbKondisi.getSelectedItem(),
                    txtHarga.getText(),
                    txtDeskripsi.getText(),
                    selectedImageFile // File gambar baru (bisa null jika edit tanpa ganti gambar)
                );
                
                JOptionPane.showMessageDialog(this, "Data berhasil disimpan!");
                clearForm();
                refreshTable();
                chartPanel.repaint(); // Update grafik
                
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Gagal menyimpan: " + ex.getMessage());
                ex.printStackTrace();
            }
        });

        // 4. Event Klik Tabel (Tampilkan Detail ke Form)
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int row = table.getSelectedRow();
                
                // Ambil data dari tabel (urutan kolom sesuai Controller)
                // Kolom: 0=ID, 1=Nama, 2=Kategori, 3=Kondisi, 4=Harga, 5=Path, 6=Deskripsi
                
                // UUID dikonversi ke String
                selectedId = table.getValueAt(row, 0).toString(); 
                
                txtNama.setText(table.getValueAt(row, 1).toString());
                cmbKategori.setSelectedItem(table.getValueAt(row, 2));
                cmbKondisi.setSelectedItem(table.getValueAt(row, 3));
                txtHarga.setText(table.getValueAt(row, 4).toString());
                
                String path = (String) table.getValueAt(row, 5);
                tampilkanPreview(path);
                
                Object descObj = table.getValueAt(row, 6);
                txtDeskripsi.setText(descObj != null ? descObj.toString() : "");
            }
        });

        // 5. Event Hapus
        btnHapus.addActionListener(e -> {
            if (selectedId == null) {
                JOptionPane.showMessageDialog(this, "Pilih data di tabel terlebih dahulu!");
                return;
            }
            
            int confirm = JOptionPane.showConfirmDialog(this, "Yakin ingin menghapus data ini?", "Konfirmasi", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                controller.hapusData(selectedId);
                clearForm();
                refreshTable();
                chartPanel.repaint();
            }
        });

        // 6. Event Reset
        btnReset.addActionListener(e -> clearForm());
    }

    // --- Helper Methods ---

    private void addComp(JPanel p, Component c, int x, int y, GridBagConstraints gbc) {
        gbc.gridx = x; gbc.gridy = y;
        p.add(c, gbc);
    }

    private void tampilkanPreview(String path) {
        if (path != null && !path.isEmpty()) {
            File f = new File(path);
            if (f.exists()) {
                ImageIcon icon = new ImageIcon(new ImageIcon(path).getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));
                lblGambarPreview.setIcon(icon);
                lblGambarPreview.setText("");
            } else {
                lblGambarPreview.setIcon(null);
                lblGambarPreview.setText("File Hilang");
            }
        } else {
            lblGambarPreview.setIcon(null);
            lblGambarPreview.setText("No Image");
        }
    }

    private void refreshTable() {
        // Ambil model dari controller
        table.setModel(controller.getTableData(currentUserId));
        
        // Sembunyikan kolom ID (0) dan Path Gambar (5) agar rapi
        hideColumn(0);
        hideColumn(5);
    }
    
    private void hideColumn(int index) {
        if (index < table.getColumnCount()) {
            table.getColumnModel().getColumn(index).setMinWidth(0);
            table.getColumnModel().getColumn(index).setMaxWidth(0);
            table.getColumnModel().getColumn(index).setWidth(0);
        }
    }

    private void clearForm() {
        txtNama.setText("");
        txtHarga.setText("");
        txtDeskripsi.setText("");
        cmbKategori.setSelectedIndex(0);
        cmbKondisi.setSelectedIndex(0);
        lblGambarPreview.setIcon(null);
        lblGambarPreview.setText("No Image");
        
        selectedId = null;
        selectedImageFile = null;
        table.clearSelection();
    }

    // --- Logika Grafik (Manual Bar Chart) ---
    private void drawChart(Graphics g) {
        // Ambil data statistik dari Controller
        Map<String, Double> stats = controller.getStats(currentUserId);
        
        if (stats.isEmpty()) {
            g.drawString("Belum ada data untuk ditampilkan grafik.", 50, 50);
            return;
        }

        // Setup koordinat grafik
        int startX = 50;
        int bottomY = 140; // Garis dasar (sumbu X)
        int barWidth = 60;
        int gap = 40;
        int maxHeight = 100; // Tinggi grafik maksimal dalam pixel
        
        // Mencari nilai tertinggi untuk skala
        double maxValue = stats.values().stream().mapToDouble(v -> v).max().orElse(1.0);

        // Gambar Sumbu
        g.setColor(Color.BLACK);
        g.drawLine(30, bottomY, 750, bottomY); // Sumbu X
        g.drawLine(30, bottomY, 30, 20);      // Sumbu Y

        int x = startX;
        Color[] colors = {Color.RED, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.GREEN, Color.CYAN};
        int colorIdx = 0;

        for (Map.Entry<String, Double> entry : stats.entrySet()) {
            double val = entry.getValue();
            
            // Hitung tinggi batang berdasarkan persentase terhadap nilai max
            int barHeight = (int) ((val / maxValue) * maxHeight);
            
            // Gambar Batang
            g.setColor(colors[colorIdx % colors.length]);
            g.fillRect(x, bottomY - barHeight, barWidth, barHeight);
            
            // Gambar Border Batang
            g.setColor(Color.BLACK);
            g.drawRect(x, bottomY - barHeight, barWidth, barHeight);
            
            // Teks Kategori (di bawah batang)
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            String kat = entry.getKey();
            if (kat.length() > 8) kat = kat.substring(0, 8) + ".."; // Potong jika kepanjangan
            g.drawString(kat, x, bottomY + 15);
            
            // Teks Nilai (di atas batang)
            // Format angka agar tidak ada .0 jika bulat
            String valStr = (val % 1 == 0) ? String.format("%.0f", val) : String.valueOf(val);
            g.drawString(valStr, x + 10, bottomY - barHeight - 5);
            
            x += barWidth + gap;
            colorIdx++;
        }
    }
}
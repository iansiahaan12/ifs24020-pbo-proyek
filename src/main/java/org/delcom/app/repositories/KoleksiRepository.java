package org.delcom.app.repositories;

import org.delcom.app.configs.DatabaseConnection; // Sesuaikan import ini
import org.delcom.app.entities.KoleksiItem;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;

public class KoleksiRepository {
    private Connection conn;

    public KoleksiRepository() {
        this.conn = DatabaseConnection.getConnection(); // Pastikan method ini benar
    }

    public List<KoleksiItem> findAllByUserId(UUID userId) {
        List<KoleksiItem> list = new ArrayList<>();
        String sql = "SELECT * FROM koleksi_items WHERE user_id = ?";
        
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId.toString()); // Convert UUID to String
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                KoleksiItem item = new KoleksiItem();
                item.setId(UUID.fromString(rs.getString("id")));
                item.setUserId(UUID.fromString(rs.getString("user_id")));
                item.setNamaItem(rs.getString("nama_item"));
                item.setKategori(rs.getString("kategori"));
                item.setKondisi(rs.getString("kondisi"));
                item.setHargaTaksiran(rs.getDouble("harga_taksiran"));
                item.setGambarPath(rs.getString("gambar_path"));
                item.setDeskripsi(rs.getString("deskripsi"));
                
                // Convert Timestamp DB ke LocalDateTime Java
                if (rs.getTimestamp("created_at") != null)
                    item.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                if (rs.getTimestamp("updated_at") != null)
                    item.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
                
                list.add(item);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // Insert Baru
    public void insert(KoleksiItem item) {
        String sql = "INSERT INTO koleksi_items (id, user_id, nama_item, kategori, kondisi, harga_taksiran, gambar_path, deskripsi, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getId().toString());
            stmt.setString(2, item.getUserId().toString());
            stmt.setString(3, item.getNamaItem());
            stmt.setString(4, item.getKategori());
            stmt.setString(5, item.getKondisi());
            stmt.setDouble(6, item.getHargaTaksiran());
            stmt.setString(7, item.getGambarPath());
            stmt.setString(8, item.getDeskripsi());
            stmt.setTimestamp(9, Timestamp.valueOf(item.getCreatedAt()));
            stmt.setTimestamp(10, Timestamp.valueOf(item.getUpdatedAt()));
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update Data
    public void update(KoleksiItem item) {
        String sql = "UPDATE koleksi_items SET nama_item=?, kategori=?, kondisi=?, harga_taksiran=?, gambar_path=?, deskripsi=?, updated_at=? WHERE id=?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, item.getNamaItem());
            stmt.setString(2, item.getKategori());
            stmt.setString(3, item.getKondisi());
            stmt.setDouble(4, item.getHargaTaksiran());
            stmt.setString(5, item.getGambarPath());
            stmt.setString(6, item.getDeskripsi());
            stmt.setTimestamp(7, Timestamp.valueOf(item.getUpdatedAt())); // Update waktu
            stmt.setString(8, item.getId().toString()); // Where clause
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(UUID id) {
        String sql = "DELETE FROM koleksi_items WHERE id = ?";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, id.toString());
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> getChartData(UUID userId) {
        Map<String, Double> stats = new HashMap<>();
        String sql = "SELECT kategori, SUM(harga_taksiran) as total FROM koleksi_items WHERE user_id = ? GROUP BY kategori";
        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, userId.toString());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                stats.put(rs.getString("kategori"), rs.getDouble("total"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return stats;
    }
}
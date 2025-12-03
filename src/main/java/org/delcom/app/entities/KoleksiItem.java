package org.delcom.app.entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class KoleksiItem {
    // --- 4 Atribut Wajib ---
    private UUID id;
    private UUID userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // --- 6 Atribut Tambahan (Topik Proyek) ---
    private String namaItem;
    private String kategori;
    private String kondisi;
    private Double hargaTaksiran;
    private String gambarPath;
    private String deskripsi; // Tambahan untuk detail

    public KoleksiItem() {}

    // Constructor lengkap
    public KoleksiItem(UUID id, UUID userId, String namaItem, String kategori, String kondisi, 
                       Double hargaTaksiran, String gambarPath, String deskripsi) {
        this.id = id;
        this.userId = userId;
        this.namaItem = namaItem;
        this.kategori = kategori;
        this.kondisi = kondisi;
        this.hargaTaksiran = hargaTaksiran;
        this.gambarPath = gambarPath;
        this.deskripsi = deskripsi;
    }

    // --- Getter Setter ---
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getNamaItem() { return namaItem; }
    public void setNamaItem(String namaItem) { this.namaItem = namaItem; }

    public String getKategori() { return kategori; }
    public void setKategori(String kategori) { this.kategori = kategori; }

    public String getKondisi() { return kondisi; }
    public void setKondisi(String kondisi) { this.kondisi = kondisi; }

    public Double getHargaTaksiran() { return hargaTaksiran; }
    public void setHargaTaksiran(Double hargaTaksiran) { this.hargaTaksiran = hargaTaksiran; }

    public String getGambarPath() { return gambarPath; }
    public void setGambarPath(String gambarPath) { this.gambarPath = gambarPath; }

    public String getDeskripsi() { return deskripsi; }
    public void setDeskripsi(String deskripsi) { this.deskripsi = deskripsi; }
}
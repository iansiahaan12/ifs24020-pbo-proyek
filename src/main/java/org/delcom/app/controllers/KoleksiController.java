package org.delcom.app.controllers;

import org.delcom.app.entities.KoleksiItem;
import org.delcom.app.services.KoleksiService;
import javax.swing.table.DefaultTableModel;
import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KoleksiController {
    
    private KoleksiService service = new KoleksiService();

    public DefaultTableModel getTableData(UUID userId) {
        List<KoleksiItem> items = service.getAllItems(userId);
        // Tambah kolom deskripsi
        String[] columns = {"ID", "Nama", "Kategori", "Kondisi", "Harga", "Path", "Deskripsi"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);

        for (KoleksiItem item : items) {
            Object[] row = {
                item.getId(), // Object UUID
                item.getNamaItem(),
                item.getKategori(),
                item.getKondisi(),
                item.getHargaTaksiran(),
                item.getGambarPath(),
                item.getDeskripsi()
            };
            model.addRow(row);
        }
        return model;
    }
    
    public Map<String, Double> getStats(UUID userId) {
        return service.getStats(userId);
    }

    public void simpanData(String idStr, UUID userId, String nama, String kategori, 
                           String kondisi, String hargaStr, String deskripsi, File gambar) {
        
        KoleksiItem item = new KoleksiItem();
        item.setUserId(userId);
        item.setNamaItem(nama);
        item.setKategori(kategori);
        item.setKondisi(kondisi);
        item.setDeskripsi(deskripsi);
        item.setHargaTaksiran(Double.parseDouble(hargaStr));
        
        boolean isEdit = false;
        if (idStr != null && !idStr.isEmpty()) {
            item.setId(UUID.fromString(idStr)); // Convert String kembali ke UUID
            // Kita perlu mengambil data lama untuk path gambar jika gambar baru null
            // (Disederhanakan: diasumsikan service menanganinya atau ditangani di View)
            isEdit = true;
        }

        service.saveItem(item, gambar, isEdit);
    }

    public void hapusData(String idStr) {
        service.deleteItem(UUID.fromString(idStr));
    }
}
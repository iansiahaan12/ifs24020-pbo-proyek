package org.delcom.app.services;

import org.delcom.app.entities.KoleksiItem;
import org.delcom.app.repositories.KoleksiRepository;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class KoleksiService {

    private KoleksiRepository repo = new KoleksiRepository();
    private static final String IMAGE_DIR = "storage/images/"; 

    public KoleksiService() {
        new File(IMAGE_DIR).mkdirs();
    }

    public List<KoleksiItem> getAllItems(UUID userId) {
        return repo.findAllByUserId(userId);
    }
    
    public Map<String, Double> getStats(UUID userId) {
        return repo.getChartData(userId);
    }

    // Method Save menangani Insert maupun Update
    public void saveItem(KoleksiItem item, File sourceImage, boolean isEdit) {
        // 1. Handle Gambar
        if (sourceImage != null) {
            try {
                String newFileName = UUID.randomUUID().toString() + "_" + sourceImage.getName();
                File dest = new File(IMAGE_DIR + newFileName);
                Files.copy(sourceImage.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
                item.setGambarPath(dest.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        // 2. Handle Insert vs Update
        if (!isEdit) {
            // INSERT BARU
            item.setId(UUID.randomUUID()); // Generate UUID Baru
            item.setCreatedAt(LocalDateTime.now());
            item.setUpdatedAt(LocalDateTime.now());
            repo.insert(item);
        } else {
            // UPDATE EXISTING
            // id sudah diset dari controller
            item.setUpdatedAt(LocalDateTime.now()); // Update waktu edit
            repo.update(item);
        }
    }

    public void deleteItem(UUID id) {
        repo.delete(id);
    }
}
package org.delcom.app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class PersonalCollectionApplication {

	public static void main(String[] args) {
		SpringApplication.run(PersonalCollectionApplication.class, args);
		System.out.println("=== Aplikasi Koleksi Barang Pribadi Berhasil Dijalankan ===");
	}

}
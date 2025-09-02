# Math-Quiz-Game_Java

Math Quiz Game - Aplikasi Edukasi Matematika Berbasis Java
Sebuah mini game edukasi interaktif bernama Math Quiz Game yang dirancang untuk memberikan pengalaman belajar matematika dasar yang menyenangkan. Aplikasi ini dibangun menggunakan bahasa pemrograman Java dengan antarmuka grafis (GUI) berbasis Java Swing.       

Deskripsi Program
Math Quiz Game adalah aplikasi desktop yang menyajikan soal-soal matematika dasar (penjumlahan, pengurangan, perkalian, dan pembagian) secara acak kepada pengguna. Setiap soal memiliki empat pilihan jawaban, dan pemain harus memilih jawaban yang benar untuk mendapatkan skor. Di akhir permainan, skor total akan ditampilkan sebagai umpan balik.    
Aplikasi ini ditujukan untuk anak-anak sekolah dasar atau siapa saja yang ingin melatih kemampuan matematika dasar dengan cara yang ringan dan menghibur.    

Fitur Utama
  Kuis Interaktif: Menampilkan 10 soal matematika dasar pilihan ganda secara acak.  
  Batas Waktu: Setiap soal memiliki batas waktu 30 detik untuk dijawab.    
  Sistem Skor: Jawaban yang benar akan mendapatkan 10 poin.    
  Leaderboard: Menyimpan dan menampilkan 10 skor tertinggi secara lokal menggunakan database SQLite.    
  Antarmuka User-Friendly: Desain GUI yang menarik dan mudah digunakan, dibangun dengan Java Swing.    
  Input Nama Pemain: Personalisasi pengalaman bermain dengan meminta nama pemain sebelum kuis dimulai.    

Penerapan Konsep OOP
  Aplikasi ini dibangun dengan menerapkan empat pilar utama Pemrograman Berorientasi Objek (OOP) untuk meningkatkan modularitas, keterbacaan, dan kemudahan pengembangan.    
  Encapsulation: Atribut penting seperti skor pemain (Player), daftar soal (SoalMatematika), dan lainnya disembunyikan menggunakan akses private. Akses ke atribut ini hanya dapat dilakukan melalui method publik (   getter dan setter).    
  Inheritance: Kelas SoalMatematika mewarisi sifat dan struktur dari kelas abstrak Soal, memungkinkan pengembangan jenis soal baru dengan lebih mudah.    
  Polymorphism: Objek dengan tipe Soal dapat mereferensikan berbagai jenis soal turunan (seperti SoalMatematika), sehingga method seperti checkAnswer() dapat dipanggil secara seragam meskipun implementasinya berbeda.    
  Abstraction: Kelas Soal dibuat sebagai kelas abstrak yang mendefinisikan struktur umum sebuah soal tanpa memberikan implementasi detail, yang kemudian diimplementasikan oleh kelas turunannya.    

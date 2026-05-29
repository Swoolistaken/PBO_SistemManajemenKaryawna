-- ============================================================
-- DATABASE SETUP: Sistem Manajemen Karyawan
-- Jalankan script ini di MySQL/phpMyAdmin sebelum menjalankan aplikasi
-- ============================================================

CREATE DATABASE IF NOT EXISTS db_karyawan
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_karyawan;

-- ============================================================
-- Tabel Karyawan
-- ============================================================
CREATE TABLE IF NOT EXISTS karyawan (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    nik                 VARCHAR(20) NOT NULL UNIQUE,
    nama                VARCHAR(100) NOT NULL,
    email               VARCHAR(100),
    no_telp             VARCHAR(20),
    alamat              TEXT,
    tanggal_lahir       DATE,
    jenis_kelamin       VARCHAR(15),
    tanggal_masuk       DATE,
    jabatan             VARCHAR(80) NOT NULL,
    departemen          VARCHAR(50) NOT NULL,
    gaji_pokok          DOUBLE NOT NULL DEFAULT 0,
    tunjangan_transport DOUBLE DEFAULT 0,
    tunjangan_makan     DOUBLE DEFAULT 0,
    tunjangan_kesehatan DOUBLE DEFAULT 0,
    level_jabatan       INT DEFAULT 1,
    pendidikan_terakhir VARCHAR(20),
    keahlian            TEXT,
    status              VARCHAR(15) DEFAULT 'AKTIF',
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- ============================================================
-- Tabel KPI
-- ============================================================
CREATE TABLE IF NOT EXISTS kpi (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    karyawan_id         INT NOT NULL,
    nik_karyawan        VARCHAR(20),
    nama_karyawan       VARCHAR(100),
    periode             INT NOT NULL,
    bulan               INT NOT NULL,
    nilai_produktivitas DOUBLE DEFAULT 0,
    nilai_kualitas      DOUBLE DEFAULT 0,
    nilai_kehadiran     DOUBLE DEFAULT 0,
    nilai_teamwork      DOUBLE DEFAULT 0,
    nilai_inovasi       DOUBLE DEFAULT 0,
    catatan_atasan      TEXT,
    target_berikutnya   TEXT,
    tanggal_penilaian   DATE,
    penilai             VARCHAR(100),
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (karyawan_id) REFERENCES karyawan(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Tabel Absensi
-- ============================================================
CREATE TABLE IF NOT EXISTS absensi (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    karyawan_id      INT NOT NULL,
    nik_karyawan     VARCHAR(20),
    nama_karyawan    VARCHAR(100),
    tanggal          DATE NOT NULL,
    jam_masuk        VARCHAR(10),
    jam_keluar       VARCHAR(10),
    status           VARCHAR(20) DEFAULT 'HADIR',
    keterangan       VARCHAR(255),
    terlambat        BOOLEAN DEFAULT FALSE,
    menit_terlambat  INT DEFAULT 0,
    pulang_awal      BOOLEAN DEFAULT FALSE,
    created_at       TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (karyawan_id) REFERENCES karyawan(id) ON DELETE CASCADE
) ENGINE=InnoDB;

-- ============================================================
-- Data contoh (opsional, hapus jika tidak diperlukan)
-- ============================================================
INSERT INTO karyawan (nik, nama, email, no_telp, jabatan, departemen, gaji_pokok,
    tunjangan_transport, tunjangan_makan, tunjangan_kesehatan, level_jabatan,
    jenis_kelamin, pendidikan_terakhir, status, tanggal_masuk)
VALUES
('EMP001', 'Budi Santoso',    'budi@company.com',   '081234567001', 'Manager',       'IT',        12000000, 1500000, 1000000, 2000000, 3, 'Laki-laki',  'S1', 'AKTIF', '2020-01-15'),
('EMP002', 'Siti Rahayu',     'siti@company.com',   '081234567002', 'Programmer',    'IT',         8000000,  500000,  500000, 1000000, 1, 'Perempuan',  'S1', 'AKTIF', '2021-03-10'),
('EMP003', 'Ahmad Fauzi',     'ahmad@company.com',  '081234567003', 'Staff HRD',     'HRD',        5500000,  500000,  500000, 1000000, 1, 'Laki-laki',  'S1', 'AKTIF', '2022-07-01'),
('EMP004', 'Dewi Lestari',    'dewi@company.com',   '081234567004', 'Akuntan',       'Finance',    7000000,  500000,  500000, 1000000, 1, 'Perempuan',  'S1', 'AKTIF', '2021-11-20'),
('EMP005', 'Reza Pratama',    'reza@company.com',   '081234567005', 'Sales Executive','Marketing', 6000000,  500000,  500000, 1000000, 1, 'Laki-laki',  'S1', 'AKTIF', '2023-02-14'),
('EMP006', 'Nina Kusuma',     'nina@company.com',   '081234567006', 'Supervisor',    'Operations', 9000000, 1000000,  750000, 1500000, 2, 'Perempuan',  'S2', 'AKTIF', '2019-08-05'),
('EMP007', 'Hendra Wijaya',   'hendra@company.com', '081234567007', 'DBA',           'IT',         8500000,  500000,  500000, 1000000, 1, 'Laki-laki',  'S1', 'CUTI',  '2020-05-12'),
('EMP008', 'Rina Handayani',  'rina@company.com',   '081234567008', 'Training Officer','HRD',      6000000,  500000,  500000, 1000000, 1, 'Perempuan',  'S1', 'AKTIF', '2022-01-03');

SELECT '✅ Database berhasil dibuat dan data contoh ditambahkan!' AS status;
SELECT COUNT(*) AS total_karyawan FROM karyawan;

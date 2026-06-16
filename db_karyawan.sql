-- DATABASE: Employee Management System

CREATE DATABASE IF NOT EXISTS db_karyawan
    CHARACTER SET utf8mb4
    COLLATE utf8mb4_unicode_ci;

USE db_karyawan;


CREATE TABLE IF NOT EXISTS karyawan (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    nik                 VARCHAR(20)  NOT NULL UNIQUE,
    nama                VARCHAR(100) NOT NULL,
    email               VARCHAR(100),
    no_telp             VARCHAR(20),
    alamat              TEXT,
    tanggal_lahir       DATE,
    jenis_kelamin       VARCHAR(15),
    tanggal_masuk       DATE,
    jabatan             VARCHAR(80)  NOT NULL,
    departemen          VARCHAR(50)  NOT NULL,
    gaji_pokok          DOUBLE       NOT NULL DEFAULT 0,
    tunjangan_transport DOUBLE                DEFAULT 0,
    tunjangan_makan     DOUBLE                DEFAULT 0,
    tunjangan_kesehatan DOUBLE                DEFAULT 0,
    level_jabatan       INT                   DEFAULT 1,
    pendidikan_terakhir VARCHAR(20),
    keahlian            TEXT,
    status              VARCHAR(15)           DEFAULT 'AKTIF',
    created_at          TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    updated_at          TIMESTAMP             DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS kpi (
    id                  INT AUTO_INCREMENT PRIMARY KEY,
    karyawan_id         INT          NOT NULL,
    nik_karyawan        VARCHAR(20),
    nama_karyawan       VARCHAR(100),
    periode             INT          NOT NULL,
    bulan               INT          NOT NULL,
    nilai_produktivitas DOUBLE                DEFAULT 0,
    nilai_kualitas      DOUBLE                DEFAULT 0,
    nilai_kehadiran     DOUBLE                DEFAULT 0,
    nilai_teamwork      DOUBLE                DEFAULT 0,
    nilai_inovasi       DOUBLE                DEFAULT 0,
    catatan_atasan      TEXT,
    target_berikutnya   TEXT,
    tanggal_penilaian   DATE,
    penilai             VARCHAR(100),
    created_at          TIMESTAMP             DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (karyawan_id) REFERENCES karyawan(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS absensi (
    id               INT AUTO_INCREMENT PRIMARY KEY,
    karyawan_id      INT         NOT NULL,
    nik_karyawan     VARCHAR(20),
    nama_karyawan    VARCHAR(100),
    tanggal          DATE        NOT NULL,
    jam_masuk        VARCHAR(10),
    jam_keluar       VARCHAR(10),
    status           VARCHAR(20)          DEFAULT 'HADIR',
    keterangan       VARCHAR(255),
    terlambat        BOOLEAN              DEFAULT FALSE,
    menit_terlambat  INT                  DEFAULT 0,
    pulang_awal      BOOLEAN              DEFAULT FALSE,
    created_at       TIMESTAMP            DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (karyawan_id) REFERENCES karyawan(id) ON DELETE CASCADE
) ENGINE=InnoDB;

CREATE TABLE IF NOT EXISTS users (
    id           INT AUTO_INCREMENT PRIMARY KEY,
    username     VARCHAR(50)  NOT NULL UNIQUE,
    password     VARCHAR(32)  NOT NULL,
    nama_lengkap VARCHAR(100) NOT NULL,
    role         VARCHAR(20)  NOT NULL DEFAULT 'HRD',
    aktif        BOOLEAN               DEFAULT TRUE,
    last_login   TIMESTAMP    NULL,
    created_at   TIMESTAMP             DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;


-- Data dummy: users
-- password: admin123

INSERT INTO users (username, password, nama_lengkap, role) VALUES
('admin',   '0192023a7bbd73250516f069df18b500', 'Administrator',  'ADMIN'),
('hrd',     '0192023a7bbd73250516f069df18b500', 'Staff HRD',      'HRD'),
('manager', '0192023a7bbd73250516f069df18b500', 'Manager IT',     'MANAGER');

INSERT INTO karyawan (nik, nama, email, no_telp, alamat, tanggal_lahir, jenis_kelamin,
    tanggal_masuk, jabatan, departemen, gaji_pokok, tunjangan_transport,
    tunjangan_makan, tunjangan_kesehatan, level_jabatan, pendidikan_terakhir, status)
VALUES
('EMP001', 'Budi Santoso',   'budi@company.com',   '081234567001',
    'Jl. Merdeka No.1, Jakarta', '1990-05-15', 'Laki-laki',
    '2020-01-15', 'Manager', 'IT',
    12000000, 1500000, 1000000, 2000000, 3, 'S1', 'AKTIF'),

('EMP002', 'Siti Rahayu',    'siti@company.com',   '081234567002',
    'Jl. Sudirman No.5, Jakarta', '1995-08-20', 'Perempuan',
    '2021-03-10', 'Programmer', 'IT',
    8000000, 500000, 500000, 1000000, 1, 'S1', 'AKTIF'),

('EMP003', 'Ahmad Fauzi',    'ahmad@company.com',  '081234567003',
    'Jl. Gatot Subroto No.10, Jakarta', '1993-02-28', 'Laki-laki',
    '2022-07-01', 'Staff HRD', 'HRD',
    5500000, 500000, 500000, 1000000, 1, 'S1', 'AKTIF'),

('EMP004', 'Dewi Lestari',   'dewi@company.com',   '081234567004',
    'Jl. Thamrin No.3, Jakarta', '1992-11-10', 'Perempuan',
    '2021-11-20', 'Akuntan', 'Finance',
    7000000, 500000, 500000, 1000000, 1, 'S1', 'AKTIF'),

('EMP005', 'Reza Pratama',   'reza@company.com',   '081234567005',
    'Jl. Rasuna Said No.7, Jakarta', '1997-04-05', 'Laki-laki',
    '2023-02-14', 'Sales Executive', 'Marketing',
    6000000, 500000, 500000, 1000000, 1, 'S1', 'AKTIF'),

('EMP006', 'Nina Kusuma',    'nina@company.com',   '081234567006',
    'Jl. HR Rasuna No.2, Jakarta', '1988-07-22', 'Perempuan',
    '2019-08-05', 'Supervisor', 'Operations',
    9000000, 1000000, 750000, 1500000, 2, 'S2', 'AKTIF'),

('EMP007', 'Hendra Wijaya',  'hendra@company.com', '081234567007',
    'Jl. Kuningan No.15, Jakarta', '1991-09-30', 'Laki-laki',
    '2020-05-12', 'DBA', 'IT',
    8500000, 500000, 500000, 1000000, 1, 'S1', 'CUTI'),

('EMP008', 'Rina Handayani', 'rina@company.com',   '081234567008',
    'Jl. Casablanca No.9, Jakarta', '1996-01-18', 'Perempuan',
    '2022-01-03', 'Training Officer', 'HRD',
    6000000, 500000, 500000, 1000000, 1, 'S1', 'AKTIF'),

('EMP009', 'Dimas Aryo',     'dimas@company.com',  '081234567009',
    'Jl. Pluit No.4, Jakarta', '1994-06-12', 'Laki-laki',
    '2021-06-15', 'Brand Manager', 'Marketing',
    8000000, 1000000, 750000, 1500000, 2, 'S1', 'AKTIF'),

('EMP010', 'Laras Wulandari','laras@company.com',  '081234567010',
    'Jl. Kebayoran No.8, Jakarta', '1998-03-25', 'Perempuan',
    '2023-08-01', 'Staff Finance', 'Finance',
    5000000, 500000, 500000, 1000000, 1, 'D3', 'AKTIF');

INSERT INTO kpi (karyawan_id, nik_karyawan, nama_karyawan, periode, bulan,
    nilai_produktivitas, nilai_kualitas, nilai_kehadiran,
    nilai_teamwork, nilai_inovasi, penilai, tanggal_penilaian)
VALUES
(1, 'EMP001', 'Budi Santoso',   2025, 1, 90, 85, 95, 88, 80, 'HR Manager', '2025-02-01'),
(2, 'EMP002', 'Siti Rahayu',    2025, 1, 85, 90, 92, 80, 75, 'HR Manager', '2025-02-01'),
(3, 'EMP003', 'Ahmad Fauzi',    2025, 1, 78, 80, 88, 85, 70, 'HR Manager', '2025-02-01'),
(4, 'EMP004', 'Dewi Lestari',   2025, 1, 88, 92, 90, 87, 78, 'HR Manager', '2025-02-01'),
(5, 'EMP005', 'Reza Pratama',   2025, 1, 72, 75, 85, 78, 65, 'HR Manager', '2025-02-01'),
(6, 'EMP006', 'Nina Kusuma',    2025, 1, 92, 88, 97, 90, 85, 'HR Manager', '2025-02-01'),
(1, 'EMP001', 'Budi Santoso',   2025, 2, 88, 87, 93, 90, 82, 'HR Manager', '2025-03-01'),
(2, 'EMP002', 'Siti Rahayu',    2025, 2, 87, 91, 90, 82, 77, 'HR Manager', '2025-03-01');


INSERT INTO absensi (karyawan_id, nik_karyawan, nama_karyawan, tanggal,
    jam_masuk, jam_keluar, status, terlambat, menit_terlambat)
VALUES
(1, 'EMP001', 'Budi Santoso',   '2025-01-02', '08:00', '17:00', 'HADIR',  FALSE, 0),
(1, 'EMP001', 'Budi Santoso',   '2025-01-03', '08:15', '17:00', 'HADIR',  TRUE,  15),
(1, 'EMP001', 'Budi Santoso',   '2025-01-04', '08:00', '17:00', 'HADIR',  FALSE, 0),
(2, 'EMP002', 'Siti Rahayu',    '2025-01-02', '08:00', '17:00', 'HADIR',  FALSE, 0),
(2, 'EMP002', 'Siti Rahayu',    '2025-01-03', NULL,    NULL,    'SAKIT',  FALSE, 0),
(2, 'EMP002', 'Siti Rahayu',    '2025-01-04', '08:30', '17:00', 'HADIR',  TRUE,  30),
(3, 'EMP003', 'Ahmad Fauzi',    '2025-01-02', '08:00', '17:00', 'HADIR',  FALSE, 0),
(3, 'EMP003', 'Ahmad Fauzi',    '2025-01-03', '08:00', '17:00', 'HADIR',  FALSE, 0),
(3, 'EMP003', 'Ahmad Fauzi',    '2025-01-04', NULL,    NULL,    'ALPHA',  FALSE, 0),
(4, 'EMP004', 'Dewi Lestari',   '2025-01-02', '08:00', '17:00', 'HADIR',  FALSE, 0),
(5, 'EMP005', 'Reza Pratama',   '2025-01-02', '09:00', '17:00', 'HADIR',  TRUE,  60),
(6, 'EMP006', 'Nina Kusuma',    '2025-01-02', '08:00', '17:00', 'HADIR',  FALSE, 0);

-- ============================================================
SELECT '✅ Database berhasil dibuat!' AS status;
SELECT COUNT(*) AS total_karyawan FROM karyawan;
SELECT COUNT(*) AS total_users    FROM users;
SELECT COUNT(*) AS total_kpi      FROM kpi;
SELECT COUNT(*) AS total_absensi  FROM absensi;

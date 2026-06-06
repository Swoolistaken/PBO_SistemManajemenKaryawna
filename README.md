# 🏢 Employee Management System
**Tugas Praktikum PBO — Sistem Manajemen Karyawan**

---
```
## 📁 Struktur Projectsrc/
src/
├── com/mycompany/employeemanagement/
│   └── Main.java
│
├── controller/
│   ├── ControllerKaryawan.java
│   ├── ControllerKPI.java
│   ├── ControllerAbsensi.java
│   └── ControllerAuth.java
│
├── model/
│   ├── Connector.java
│   ├── InterfaceDAO.java
│   ├── karyawan/
│   │   ├── Karyawan.java
│   │   ├── ModelKaryawan.java
│   │   ├── DAOKaryawan.java
│   │   └── ModelTable.java
│   ├── kpi/
│   │   ├── ModelKPI.java
│   │   └── DAOKPI.java
│   ├── absensi/
│   │   ├── ModelAbsensi.java
│   │   └── DAOAbsensi.java
│   └── user/
│       ├── ModelUser.java
│       ├── ModelAdmin.java
│       ├── ModelHRD.java
│       ├── ModelManager.java
│       └── DAOUser.java
│
└── view/
├── MainFrame.java
├── auth/
│   └── LoginForm.java
├── karyawan/
│   ├── Dashboard.java
│   ├── ViewData.java
│   ├── InputData.java
│   └── EditData.java
└── Absensi/
└── ViewAbsensi.java
---
```
## 🚀 Cara Menjalankan di NetBeans

### 1. Setup Database
Buka MySQL (XAMPP/phpMyAdmin), jalankan file `db_karyawan.sql`.

### 2. Import Project ke NetBeans
1. Buka NetBeans IDE
2. `File → Open Project` → pilih folder project
3. Pastikan struktur package sesuai struktur di atas

### 3. Tambahkan Library MySQL Connector
1. Klik kanan project → **Properties**
2. `Libraries → Add JAR/Folder`
3. Tambahkan `mysql-connector-java-8.x.x.jar`
   - Download dari: https://dev.mysql.com/downloads/connector/j/

### 4. Konfigurasi Database
Edit `src/model/Connector.java`:
```javaprivate static final String HOST     = "localhost";
private static final String PORT     = "3306";
private static final String DATABASE = "db_karyawan";
private static final String USERNAME = "root";
private static final String PASSWORD = ""; // sesuaikan password MySQL Anda

### 5. Jalankan
Klik kanan `Main.java` → **Run File**, atau tekan **F6**.

### 6. Default Login
| Username | Password | Role |
|----------|----------|------|
| admin | admin123 | Administrator |
| hrd | admin123 | HRD |
| manager | admin123 | Manager |

---

## 🎯 Fitur Aplikasi

### 🔐 Login & Role
- Form login dengan validasi username dan password
- Password di-hash MD5 sebelum disimpan
- 3 role dengan hak akses berbeda: Admin, HRD, Manager
- Menu dan tombol tampil/sembunyi otomatis sesuai role
- Fitur logout

### 👤 Data Karyawan (CRUD)
- Tambah, edit, hapus karyawan
- Pencarian realtime by nama/NIK/jabatan
- Filter by departemen
- Status karyawan: AKTIF / NONAKTIF / CUTI
- Right-click context menu
- Kalkulasi tunjangan otomatis berdasarkan level jabatan

### 📊 Penilaian KPI
- 5 kategori penilaian dengan bobot berbeda: Produktivitas (30%), Kualitas (25%), Kehadiran (20%), Teamwork (15%), Inovasi (10%)
- Input nilai 0-100 via JSpinner
- Kalkulasi otomatis nilai akhir & grade (A+ s/d E)
- Estimasi bonus berdasarkan nilai KPI dan gaji pokok
- Riwayat penilaian per karyawan
- Muat semua penilaian lintas periode

### 📋 Absensi
- Catat absensi harian
- Status: HADIR, IZIN, SAKIT, ALPHA, CUTI, DINAS_LUAR, WORK_FROM_HOME
- Jam masuk/keluar otomatis di-disable jika status bukan HADIR/DINAS_LUAR/WFH
- Validasi format jam HH:mm dan tanggal dd/MM/yyyy
- Checkbox terlambat dan pulang awal bersifat mutual exclusive
- Menit terlambat hanya bisa diisi angka

### 🏠 Dashboard
- 4 kartu statistik: total karyawan, aktif, nonaktif, total KPI
- Jam dan tanggal real-time
- Tabel distribusi karyawan per departemen
- Tabel 10 penilaian KPI terbaru
- Auto-refresh setiap 30 detik

### 💰 Penggajian
- Pilih periode bulan dan tahun
- Kalkulasi gaji otomatis: gaji pokok + tunjangan + bonus KPI
- Potongan otomatis berdasarkan keterlambatan dan alpha
- Rekap total pengeluaran gaji semua karyawan aktif

---

## 👥 Hak Akses per Role

| Fitur | Admin | HRD | Manager |
|-------|-------|-----|---------|
| Dashboard | ✅ | ✅ | ✅ |
| Lihat Karyawan | ✅ | ✅ | ✅ |
| Tambah Karyawan | ✅ | ✅ | ❌ |
| Edit Karyawan | ✅ | ✅ | ❌ |
| Hapus Karyawan | ✅ | ❌ | ❌ |
| Penilaian KPI | ✅ | ✅ | ✅ |
| Absensi | ✅ | ✅ | ❌ |
| Penggajian | ✅ | ❌ | ✅ |

---

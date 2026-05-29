# 🏢 Sistem Manajemen Karyawan (Employee Management System)
**Tugas Praktikum PBO — Tema: Sistem Manajemen Karyawan**

---

## 📁 Struktur Project (MVC Pattern)

```
EmployeeManagement/
│
├── src/
│   ├── model/
│   │   ├── Connector.java              ← Singleton DB connector
│   │   └── karyawan/
│   │       ├── AbstractKaryawan.java   ← Abstract class (Abstraction)
│   │       ├── ModelKaryawan.java      ← Entity + Business logic
│   │       ├── ModelKPI.java           ← Entity KPI
│   │       ├── ModelAbsensi.java       ← Entity Absensi
│   │       ├── InterfaceDAO.java       ← Interface generic CRUD
│   │       ├── DAO.java                ← JDBC Karyawan
│   │       ├── DAOKPI.java             ← JDBC KPI
│   │       ├── DAOAbsensi.java         ← JDBC Absensi
│   │       └── ModelTable.java         ← JTable model
│   │
│   ├── controller/
│   │   └── ControllerKaryawan.java     ← Business logic + MultiThread
│   │
│   ├── view/
│   │   └── karyawan/
│   │       ├── Dashboard.java          ← Statistik & grafik
│   │       ├── ViewData.java           ← List karyawan (CRUD)
│   │       ├── InputData.java          ← Form tambah/edit karyawan
│   │       ├── EditData.java           ← Penilaian KPI
│   │       └── ViewAbsensi.java        ← Rekap absensi
│   │
│   └── main/
│       └── Main.java                   ← Entry point + Main Frame
│
└── db_karyawan.sql                     ← Script database MySQL
```

---

## ✅ Implementasi Pilar-Pilar OOP

| Pilar | Implementasi |
|-------|-------------|
| **Encapsulation** | Semua field `private` dengan getter/setter di `AbstractKaryawan`, `ModelKaryawan`, `ModelKPI`, `ModelAbsensi` |
| **Inheritance** | `ModelKaryawan extends AbstractKaryawan`, `DAO implements InterfaceDAO`, `ModelTable extends AbstractTableModel` |
| **Abstraction** | `AbstractKaryawan` (abstract class) dengan method abstrak `hitungGajiPokok()`, `hitungTunjangan()`, `getJabatan()`, `getDepartemen()`. `InterfaceDAO<T>` (interface generic) |
| **Polymorphism** | Override `hitungTotalGaji()` dan `getStatusKaryawan()` di `ModelKaryawan`. Method `simpan/update/hapus/getAll` diimplementasikan berbeda di `DAO`, `DAOKPI`, `DAOAbsensi` |

---

## ✅ Materi Wajib Lainnya

| Materi | Implementasi |
|--------|-------------|
| **Exception Handling** | `try-catch SQLException`, `IllegalArgumentException` di validasi bisnis, `SQLIntegrityConstraintViolationException` untuk duplikat NIK |
| **JDBC** | `Connector.java` (Singleton), semua DAO menggunakan `PreparedStatement`, `ResultSet`, `Connection` |
| **MultiThread** | Semua operasi database berjalan di background thread (`new Thread(...).start()`), hasil dikembalikan ke EDT via `SwingUtilities.invokeLater()` |
| **GUI Swing** | `JFrame`, `JPanel`, `JTable`, `JDialog`, `JComboBox`, `JSlider`, `JScrollPane`, `CardLayout`, `BoxLayout`, `GridLayout`, `BorderLayout`, `Timer` |

---

## 🚀 Cara Menjalankan di NetBeans

### 1. Setup Database
```sql
-- Buka MySQL (XAMPP/phpMyAdmin), jalankan file:
db_karyawan.sql
```

### 2. Import Project ke NetBeans
1. Buka **NetBeans IDE**
2. `File → New Project → Java → Java Application`
3. Nama project: `EmployeeManagement`
4. Copy semua file ke folder `src/` sesuai struktur di atas

### 3. Tambahkan Library MySQL Connector
1. Klik kanan project → **Properties**
2. `Libraries → Add JAR/Folder`
3. Tambahkan `mysql-connector-java-8.x.x.jar`
   - Download dari: https://dev.mysql.com/downloads/connector/j/

### 4. Jalankan
- Klik kanan `Main.java` → **Run File**
- Atau tekan **F6**

---

## 🎯 Fitur Aplikasi

### 👤 Data Karyawan (CRUD)
- Tambah, edit, hapus karyawan
- Pencarian realtime (by nama/NIK/jabatan)
- Filter by departemen
- Status karyawan: AKTIF / NONAKTIF / CUTI
- Right-click context menu

### 📊 Penilaian KPI
- 5 kategori penilaian dengan bobot berbeda:
  - Produktivitas (30%), Kualitas (25%), Kehadiran (20%), Teamwork (15%), Inovasi (10%)
- Slider interaktif 0-100
- Kalkulasi otomatis nilai akhir & grade (A+ s/d E)
- Estimasi bonus berdasarkan nilai KPI
- Riwayat penilaian per karyawan

### 📋 Absensi
- Catat absensi harian
- Status: HADIR, IZIN, SAKIT, ALPHA, CUTI, DINAS LUAR, WFH
- Deteksi keterlambatan & pulang awal
- Rekap tabel semua absensi

### 🏠 Dashboard
- Kartu statistik: total karyawan, aktif, total KPI
- Grafik batang distribusi karyawan per departemen
- Jam real-time
- Auto-refresh setiap 30 detik

### 💰 Penggajian
- Kalkulasi gaji otomatis (gaji pokok + semua tunjangan)
- Tunjangan otomatis by level jabatan
- Rekap total pengeluaran gaji semua karyawan aktif

---

## 🔧 Konfigurasi Database
Edit file `src/model/Connector.java`:
```java
private static final String HOST     = "localhost";
private static final String PORT     = "3306";
private static final String DATABASE = "db_karyawan";
private static final String USERNAME = "root";
private static final String PASSWORD = "";  // sesuaikan password MySQL Anda
```

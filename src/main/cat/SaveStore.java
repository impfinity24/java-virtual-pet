package cat;
// SaveStore.java
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Properties;

public final class SaveStore {
    public static final class SaveState {
        public LocalDate lastDate;
        public int dayCount;
        public long lastTs; // 新增：上次更新的毫秒时间戳
    }

    private final Path file;

    public SaveStore(Path file) { this.file = file; }

    public SaveState read() throws IOException {
        Properties p = new Properties();
        SaveState s = new SaveState();
        if (Files.exists(file)) {
            try (Reader r = Files.newBufferedReader(file, StandardCharsets.UTF_8)) {
                p.load(r);
            }
            String last = p.getProperty("lastDate");
            String dc   = p.getProperty("dayCount");
            String ts   = p.getProperty("lastTs"); // 可能为空（兼容老存档）
            // s.lastTs = (ts != null) 
            //     ? Long.parseLong(ts) 
            //     : System.currentTimeMillis();

            // // s.lastDate = (last == null || last.isBlank()) ? LocalDate.now() : LocalDate.parse(last);
            // s.dayCount = Integer.parseInt(dc != null ? dc : "0");
            // s.lastTs = Long.parseLong(ts != null ? ts : "0");
            s.lastDate = (last == null || last.isBlank())
                ? LocalDate.now()
                : LocalDate.parse(last);

            s.dayCount = (dc == null || dc.isBlank())
                ? 0
                : Integer.parseInt(dc);

            s.lastTs   = (ts == null || ts.isBlank())
                ? System.currentTimeMillis()
                : Long.parseLong(ts);
        } else {
            s.lastDate = LocalDate.now();
            s.dayCount = 0;
            s.lastTs   = System.currentTimeMillis();
            write(s); // 初始化
        }
        return s;
    }

    public void write(SaveState s) throws IOException {
        Properties p = new Properties();
        p.setProperty("lastDate", s.lastDate.toString());
        p.setProperty("dayCount", Integer.toString(s.dayCount));
        p.setProperty("lastTs", Long.toString(s.lastTs)); // 保存时间戳
        // Files.createDirectories(file.getParent());
        // try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
        //     p.store(w, "pet-cat save");
        // }
        Path parent = file.getParent();
        if (parent != null) {
        Files.createDirectories(parent);
        }
        try (Writer w = Files.newBufferedWriter(file, StandardCharsets.UTF_8)) {
        p.store(w, "pet-cat save");
        }

    }
}
package cat;

// CatConditionFile.java
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.List;
import java.util.regex.*;

public final class CatConditionFile {
    private static final Pattern DAY_LINE =
        Pattern.compile("^\\s*天数[:：]\\s*(\\d+).*?$");

    /** 将文件中“天数: N”改为新值；若不存在该行则追加一行 */
    public static void writeDay(Path file, int day) throws IOException {
        List<String> lines = Files.exists(file)
            ? Files.readAllLines(file, StandardCharsets.UTF_8)
            : new java.util.ArrayList<>();

        boolean replaced = false;
        for (int i = 0; i < lines.size(); i++) {
            Matcher m = DAY_LINE.matcher(lines.get(i));
            if (m.find()) {
                lines.set(i, "天数: " + day);
                replaced = true;
                break;
            }
        }
        if (!replaced) lines.add("天数: " + day);

        Files.createDirectories(file.getParent());
        Files.write(file, lines, StandardCharsets.UTF_8,
                   StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    /** 若工作副本不存在，从资源复制一份出来（资源路径示例：/cat_condition.txt） */
    public static void ensureWorkCopy(Path workFile, String resourcePath) throws IOException {
        if (Files.exists(workFile)) return;
        try (var in = CatConditionFile.class.getResourceAsStream(resourcePath)) {
            if (in == null) throw new IOException("资源不存在: " + resourcePath);
            Files.createDirectories(workFile.getParent());
            Files.copy(in, workFile);
        }
    }
    //     /** 按照 steps(每3小时为一步) 衰减属性 */
    // public static void degrade(Path condPath, int steps) throws IOException {
    //     if (steps <= 0) return;

    //     // 先把文件内容读进来
    //     List<String> lines = Files.readAllLines(condPath, StandardCharsets.UTF_8);

    //     // 这里只是最简单的实现：追加一行提示
    //     lines.add("已衰减 " + steps + " 步 (每步=3小时)");

    //     // 写回文件
    //     Files.write(condPath, lines, StandardCharsets.UTF_8,
    //                 StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    // }
    // 按 steps(每步=3小时) 衰减：饥饿/口渴/亲密/兴奋 各 -steps 个 ♥；清洁度 -steps*6 个 *
public static void degrade(java.nio.file.Path file, int steps) throws java.io.IOException {
    if (steps <= 0) return;

    java.util.List<String> lines = java.nio.file.Files.readAllLines(file, java.nio.charset.StandardCharsets.UTF_8);
    for (int i = 0; i < lines.size(); i++) {
        String line = lines.get(i);

        line = decHearts(line, "饥饿度：", steps, 6);
        line = decHearts(line, "口渴度：", steps, 4);
        line = decHearts(line, "亲密度：", steps, 4);
        line = decHearts(line, "兴奋度：", steps, 4);

        // if (line.stripLeading().startsWith("清洁度")) {
        // line = decAster(line, steps * 6);
        // }
        if (line.stripLeading().startsWith("清洁度")) {
            line = decAster(line, steps * 6);
        }

        lines.set(i, line);
    }
    java.nio.file.Files.write(file, lines, java.nio.charset.StandardCharsets.UTF_8);
}

// 兼容三种格式：
// 1) 饥饿度：♥♥♥   (3/6)
// 2) 亲密度： (♥♥♥♥)   (4/4)
// 3) 允许前后空格/♥︎（带变体）等
private static String decHearts(String line, String label, int step, int max) {
    if (!line.contains(label)) return line;

    // 找第一个括号与其右括号
    int p1 = line.indexOf('(');
    int p2 = (p1 >= 0) ? line.indexOf(')', p1 + 1) : -1;

    // 标签结束位置
    int afterLabel = line.indexOf(label) + label.length();

    // 标签后到括号前的区域（饥饿/口渴用）
    String zoneBefore = (p1 >= 0 ? line.substring(afterLabel, p1) : line.substring(afterLabel));
    // 第一个括号内的区域（亲密/兴奋用）
    String zoneInParen = (p1 >= 0 && p2 > p1) ? line.substring(p1 + 1, p2) : "";

    int heartsBefore = countHeart(zoneBefore);
    int heartsInParen = countHeart(zoneInParen);
    boolean useParen = (heartsBefore == 0 && heartsInParen > 0);

    int current = useParen ? heartsInParen : heartsBefore;
    // int next = Math.max(0, current - step);
    // ☆ 关键改动：不再 Math.max，而是先算出 next，若 <0 直接游戏结束
    int next = current - step;
    if (next < 0) {
        gameOverByLabel(label); // 见“新增方法”小节
        // System.exit 已退出；下面 return 只是语法需要
        return line;
    }

    String newHearts = "♥".repeat(next);
    // String countText = " (" + next + "/" + max + ")";

    if (useParen && p1 >= 0 && p2 > p1) {
        // 爱心在第一个括号里（亲密/兴奋）
        String prefix = line.substring(0, p1 + 1);
        String suffix = line.substring(p2 + 1); // 保留后面的排版
        return prefix + newHearts + ")" + suffix;
    } else {
        // 爱心在标签后（饥饿/口渴）
        String after = (p1 >= 0 && p2 > p1) ? line.substring(p2 + 1) : "";
        return line.substring(0, afterLabel) + newHearts + after;
    }
}


// —— 辅助：清洁度星号递减 —— 
private static String decAster(String line, int k) {
    int idx = line.indexOf('：');
    if (idx < 0) idx = line.indexOf(':');
    if (idx < 0) return line;

    String prefix = line.substring(0, idx + 1);  // 含“：”
    String stars = line.substring(idx + 1).trim();

    long have = stars.chars().filter(ch -> ch == '*').count();
    long next = have - k;
    if (next < 0) {
        // 清洁度为负 → 感染病菌
        System.out.println("猫咪感染病菌！游戏结束。");
        System.exit(0);
        return line;
    }
    return prefix + "*".repeat((int) next);
}

// 统计 ♥ 个数（兼容 “♥︎” 带变体选择符：先去掉变体符号）
private static int countHeart(String s) {
    String cleaned = s.replace("︎", ""); // 去掉 VARIATION SELECTOR-16
    int c = 0;
    for (int i = 0; i < cleaned.length(); i++) if (cleaned.charAt(i) == '♥') c++;
    return c;
}

private static void gameOverByLabel(String label) {
    String msg;
    switch (label) {
        case "饥饿度:" -> msg = "猫咪饿死了！游戏结束。";
        case "口渴度:" -> msg = "猫咪渴死了！游戏结束。";
        case "亲密度:" -> msg = "猫咪离家出走了！游戏结束。";
        case "兴奋度:" -> msg = "猫咪抑郁生病了！游戏结束。";
        case "清洁度:" -> msg = "猫咪感染病菌！游戏结束。";
        default -> msg = "猫咪状态恶化！游戏结束。";
    }
    System.out.println(msg);
    System.exit(0);
}

}
package cat;

import java.util.Scanner;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        Scanner in = new Scanner(System.in);

        // SaveStore saveStore = new SaveStore(Paths.get("savefile.properties"));
        // 以当前工作目录（out/）为基准，回到上一级，指向 ../data/save.properties
        var savePath = java.nio.file.Paths.get(System.getProperty("user.dir"))
                 .resolve("../data/save.properties")
                 .normalize();
        var saveStore = new SaveStore(savePath);

        SaveStore.SaveState state = saveStore.read(); // 读取游戏进度
        // Timekeeper timekeeper = new Timekeeper();

        // int days = timekeeper.syncDays(saveStore, state);
        int days = Timekeeper.syncDays(saveStore, state); // 改为静态调用
        var dataDir  = java.nio.file.Paths.get(System.getProperty("user.dir"))
                  .resolve("../data").normalize();
        var condPath = dataDir.resolve("cat_condition.txt");

        int steps = Timekeeper.decayBy3Hours(saveStore, state, condPath);
        if (steps > 0) System.out.println("已按 3 小时衰减了 " + steps + " 次");


        if (days > 0) {
            System.out.println("游戏已推进 " + days + " 天");
        }
        // 把“天数: N”写回到 src/main/data/cat_condition.txt
        // var condPath = java.nio.file.Paths.get(System.getProperty("user.dir"))
            // .resolve("../data/cat_condition.txt")
            // .normalize();

// 若你担心文件被删掉，可先解开这一行：把 resources 里的模板拷到 data（若已存在则跳过）
// CatConditionFile.ensureWorkCopy(condPath, "/cat_condition.txt");

        CatConditionFile.writeDay(condPath, state.dayCount);


            System.out.println("**************************************************");
            System.out.println("**                欢迎光临宠物商店              **");
            System.out.println("**************************************************");
            System.out.println("  这是一家只卖“文字的小猫”的小店。");
            System.out.println("  在这里，你可以领养、抚摸、喂食、清洁、陪玩，");
            System.out.println("  用每日几分钟，养大一只可爱的小猫。");
            System.out.println();
            System.out.println("  输入h 帮助/说明");
            System.out.println("  输入exit 退出");
            System.out.println("存档路径: " + savePath.toAbsolutePath());

        while (true) 
        {
            System.out.print("  请输入选项编号：");
            String x = in.nextLine();
            if (x.equalsIgnoreCase("h")) {
                // 从 resources 里读取
                try (BufferedReader br = new BufferedReader(
                        new InputStreamReader(
                                Main.class.getResourceAsStream("/how_to_play.txt"), "UTF-8"))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        System.out.println(line);
                    }
                } catch (IOException e) {
                    System.out.println("无法读取帮助文件: " + e.getMessage());
                }
            } 
            else if (x.equalsIgnoreCase("exit")) {
                System.out.println("退出程序");
                break;
            } else if (x.equalsIgnoreCase("z")) {
                System.out.println("你进入了动作处理菜单");
            } else if (x.equalsIgnoreCase("x")) {
                System.out.println("你进入状态显示菜单，可查看宠物状态和物品状态。");
                 // 路径：src/main/data/cat_condition.txt
            var catPath = java.nio.file.Paths.get(System.getProperty("user.dir"))
                .resolve("../data/cat_condition.txt")
                .normalize();
            var goodsPath = java.nio.file.Paths.get(System.getProperty("user.dir"))
                .resolve("../data/goods_condition.txt")
                .normalize();

            try {
                System.out.println("===== 宠物状态 =====");
                java.nio.file.Files.lines(catPath).forEach(System.out::println);

                System.out.println("===== 物品状态 =====");
                java.nio.file.Files.lines(goodsPath).forEach(System.out::println);
            } catch (java.io.IOException e) {
                System.out.println("读取状态文件时出错: " + e.getMessage());
            }
            } else if (x.equalsIgnoreCase("c")) {
                System.out.println("你可重新领养宠物。");
            } else if (x.equalsIgnoreCase("v")) {
                System.out.println("你可触发天使猫，恢复所有点数到最大值，如果猫咪出走，按此键可张贴寻猫启事，连续张贴7天猫咪回家。");
            } else {
                System.out.println("无效输入，请重新输入。");
            }
            System.out.println();
        }
        in.close();
    }
}
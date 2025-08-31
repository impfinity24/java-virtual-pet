# Java Virtual Pet（宠物世界）

一个用 Java 实现的命令行“电子小猫”游戏：喂食、饮水、清洁、陪玩；每 **3 小时** 自动衰减状态；任意一项 < 0 会触发对应结局（饿死/渴死/离家出走/抑郁生病/感染病菌）。

## 运行方式

### macOS / Linux
```bash
bash run.sh
### Windows
### 2. Windows 命令的路径符号
Windows 下建议用反斜杠 `\`，否则会让 Windows 用户迷惑。你写的 `src/main/cat/Main.java` 在 Windows 命令行是要写成：
1. 双击 `run.bat`  
2. 或手动执行：
   ```bash
   javac -d out src\main\cat\Main.java
   java -cp out cat.Main

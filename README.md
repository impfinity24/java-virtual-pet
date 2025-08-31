# Java Virtual Pet（宠物世界）

一个用 Java 实现的命令行“电子小猫”游戏：喂食、饮水、清洁、陪玩；每 **3 小时** 自动衰减状态；任意一项 < 0 会触发对应结局（饿死/渴死/离家出走/抑郁生病/感染病菌）。

## 运行方式

### macOS / Linux
```bash
bash run.sh
javac -d out src\main\cat\Main.java
java -cp out cat.Main

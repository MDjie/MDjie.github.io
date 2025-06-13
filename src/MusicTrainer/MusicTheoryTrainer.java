package MusicTrainer;
import java.util.*;
import java.io.*;

public class MusicTheoryTrainer {
    // C大调音阶
    private static final String[] MAJOR_SCALE = {"C", "D", "E", "F", "G", "A", "B"};
    // 1~7级与音名映射
    private static final Map<Integer, String> DEGREE_TO_NOTE = new HashMap<>();
    static {
        for (int i = 0; i < 7; i++) {
            DEGREE_TO_NOTE.put(i + 1, MAJOR_SCALE[i]);
        }
    }
    // 每个度数的半音数（大调）
    private static final int[] MAJOR_SCALE_SEMITONES = {0, 2, 4, 5, 7, 9, 11};
    // 训练统计文件
    private static final String ERROR_FILE = "error_rate_file.txt";

    // 题目结构体
    private static class IntervalQuestion {
        int startDegree;
        boolean isAscending;
        int interval;
        IntervalQuestion(int startDegree, boolean isAscending, int interval) {
            this.startDegree = startDegree;
            this.isAscending = isAscending;
            this.interval = interval;
        }
        // 题目唯一标识（如：1-上行-3度）
        String getKey() {
            return startDegree + "-" + (isAscending ? "上行" : "下行") + "-" + interval + "度";
        }
    }

    // 统计信息 [总次数, 错误次数]
    private Map<String, int[]> stats = new HashMap<>();

    // 读取历史统计
    private void loadStats() {
        File file = new File(ERROR_FILE);
        if (!file.exists()) return;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    String key = parts[0];
                    int total = Integer.parseInt(parts[1]);
                    int wrong = Integer.parseInt(parts[2]);
                    stats.put(key, new int[]{total, wrong});
                }
            }
        } catch (Exception e) {
            System.out.println("读取统计文件出错: " + e.getMessage());
        }
    }

    // 写入统计到文件
    private void saveStats() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ERROR_FILE))) {
            for (Map.Entry<String, int[]> entry : stats.entrySet()) {
                String key = entry.getKey();
                int[] arr = entry.getValue();
                pw.println(key + "," + arr[0] + "," + arr[1]);
            }
        } catch (Exception e) {
            System.out.println("写入统计文件出错: " + e.getMessage());
        }
    }

    // 计算错误率并分配训练次数
    private int getExtraTimes(String key) {
        int[] arr = stats.getOrDefault(key, new int[]{0, 0});
        int total = arr[0];
        int wrong = arr[1];
        if (total == 0) return 0;
        double rate = (double) wrong / total;
        // 0~100% -> 0~4
        if (rate >= 0.8) return 4;
        if (rate >= 0.6) return 3;
        if (rate >= 0.4) return 2;
        if (rate >= 0.2) return 1;
        return 0;
    }

    // 支持限时输入
    private String timedInput(int seconds) {
        final String[] result = new String[1];
        Thread inputThread = new Thread(() -> {
            try {
                BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
                result[0] = reader.readLine();
            } catch (IOException e) {
                // ignore
            }
        });
        inputThread.start();
        try {
            inputThread.join(seconds * 1000L);
        } catch (InterruptedException e) {
            // ignore
        }
        if (inputThread.isAlive()) {
            inputThread.interrupt();
            return null; // 超时
        }
        return result[0];
    }

    public void intervalMemoryPractice() {
        Scanner scanner = new Scanner(System.in);
        loadStats();
        // 让用户选择要训练的度数
        Set<Integer> selectedIntervals = new HashSet<>();
        while (selectedIntervals.isEmpty()) {
            System.out.println("请输入要训练的度数（如3,4,5或3-5，多个用逗号分隔，支持范围）：");
            String intervalInput = scanner.nextLine().trim();
            String[] parts = intervalInput.split(",");
            for (String part : parts) {
                part = part.trim();
                if (part.matches("\\d+")) {
                    int val = Integer.parseInt(part);
                    if (val >= 3 && val <= 7) selectedIntervals.add(val);
                } else if (part.matches("\\d+-\\d+")) {
                    String[] range = part.split("-");
                    int start = Integer.parseInt(range[0]);
                    int end = Integer.parseInt(range[1]);
                    if (start > end) { int tmp = start; start = end; end = tmp; }
                    for (int i = start; i <= end; i++) {
                        if (i >= 3 && i <= 7) selectedIntervals.add(i);
                    }
                }
            }
            if (selectedIntervals.isEmpty()) {
                System.out.println("输入无效，请重新输入。");
            }
        }
        // 生成所有基础组合
        List<IntervalQuestion> baseQuestions = new ArrayList<>();
        for (int startDegree = 1; startDegree <= 7; startDegree++) {
            for (boolean isAscending : new boolean[]{true, false}) {
                for (int interval = 3; interval <= 7; interval++) {
                    if (!selectedIntervals.contains(interval)) continue;
                    baseQuestions.add(new IntervalQuestion(startDegree, isAscending, interval));
                }
            }
        }
        // 统计每种题型（包含startDegree）
        Map<String, List<IntervalQuestion>> typeMap = new HashMap<>();
        for (IntervalQuestion q : baseQuestions) {
            typeMap.computeIfAbsent(q.getKey(), k -> new ArrayList<>()).add(q);
        }

        // 选择出题顺序
        int mode = 2;
        while (true) {
            System.out.println("请选择出题顺序：1.三度-四度-五度-六度-七度顺序  2.乱序（默认2）");
            String input = scanner.nextLine().trim();
            if (input.equals("1")) { mode = 1; break; }
            if (input.equals("2") || input.isEmpty()) { mode = 2; break; }
            System.out.println("输入无效，请重新输入。");
        }

        // 选择是否限时
        boolean useTimer = false;
        int timeLimit = 0;
        while (true) {
            System.out.println("是否开启限时答题？(y/n，默认n)");
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("y")) {
                useTimer = true;
                while (true) {
                    System.out.print("请输入每题限时时间（秒，正整数）：");
                    String t = scanner.nextLine().trim();
                    try {
                        timeLimit = Integer.parseInt(t);
                        if (timeLimit > 0) break;
                    } catch (Exception e) {}
                    System.out.println("输入无效，请重新输入。");
                }
                break;
            } else if (input.equals("n") || input.isEmpty()) {
                useTimer = false;
                break;
            } else {
                System.out.println("输入无效，请重新输入。");
            }
        }

        boolean keepPracticing = true;
        while (keepPracticing) {
            // 生成本轮题库
            List<IntervalQuestion> questions = new ArrayList<>();
            for (String key : typeMap.keySet()) {
                List<IntervalQuestion> pool = typeMap.get(key);
                Collections.shuffle(pool);
                questions.add(pool.get(0));
                int extra = getExtraTimes(key);
                for (int i = 0; i < extra; i++) {
                    questions.add(pool.get((i + 1) % pool.size()));
                }
            }
            // 排序或乱序
            if (mode == 1) {
                // 顺序：三度-四度-五度-六度-七度
                Map<Integer, List<IntervalQuestion>> intervalMap = new HashMap<>();
                for (IntervalQuestion q : questions) {
                    intervalMap.computeIfAbsent(q.interval, k -> new ArrayList<>()).add(q);
                }
                questions.clear();
                for (int interval = 3; interval <= 7; interval++) {
                    List<IntervalQuestion> group = intervalMap.getOrDefault(interval, new ArrayList<>());
                    Collections.shuffle(group); // 每组内部打乱
                    questions.addAll(group);
                }
            } else {
                Collections.shuffle(questions);
            }
            for (IntervalQuestion q : questions) {
                int targetDegree = q.isAscending ? (q.startDegree + q.interval - 1) : (q.startDegree - q.interval + 1);
                while (targetDegree < 1) targetDegree += 7;
                while (targetDegree > 7) targetDegree -= 7;
                int startIndex = q.startDegree - 1;
                int targetIndex = targetDegree - 1;
                int semitones;
                if (q.isAscending) {
                    semitones = MAJOR_SCALE_SEMITONES[targetIndex] - MAJOR_SCALE_SEMITONES[startIndex];
                    if (semitones < 0) semitones += 12;
                } else {
                    semitones = MAJOR_SCALE_SEMITONES[startIndex] - MAJOR_SCALE_SEMITONES[targetIndex];
                    if (semitones < 0) semitones += 12;
                }
                String correctQuality = getIntervalQuality(q.interval, semitones);
                String correctAnswer = targetDegree + " " + correctQuality;
                String key = q.getKey();
                System.out.printf("请输出%d的%s%d度音程 (格式: 目标级数 音程属性，如 1 小三度): ",
                        q.startDegree, q.isAscending ? "上行" : "下行", q.interval);
                String userInput;
                if (useTimer) {
                    System.out.printf("（限时%d秒）", timeLimit);
                    userInput = timedInput(timeLimit);
                } else {
                    userInput = scanner.nextLine();
                }
                int[] arr = stats.getOrDefault(key, new int[]{0, 0});
                arr[0]++;
                boolean wrong = false;
                if (useTimer && userInput == null) {
                    System.out.println("超时，正确答案是：" + correctAnswer + "\n");
                    // 清理输入缓冲区，防止残留换行符
                    if (scanner.hasNextLine()) scanner.nextLine();
                    wrong = true;
                } else if (!correctAnswer.equals(userInput != null ? userInput.trim() : "")) {
                    System.out.println("回答错误。正确答案是：" + correctAnswer + "\n");
                    wrong = true;
                } else {
                    System.out.println("回答正确！\n");
                }
                if (wrong) arr[1]++;
                stats.put(key, arr);
            }
            saveStats();
            showStats();
            System.out.print("本轮已覆盖所有音程，是否再来一轮？(y/n): ");
            if (!scanner.nextLine().trim().equalsIgnoreCase("y")) keepPracticing = false;
        }
    }

    // 显示统计信息
    private void showStats() {
        System.out.println("\n题型\t总次数\t错误次数\t错误率");
        for (String key : stats.keySet()) {
            int[] arr = stats.get(key);
            int total = arr[0];
            int wrong = arr[1];
            double rate = total == 0 ? 0 : (double) wrong / total * 100;
            System.out.printf("%s\t%d\t%d\t%.1f%%\n", key, total, wrong, rate);
        }
        System.out.println();
    }

    // 根据度数和半音数判断音程属性
    private String getIntervalQuality(int interval, int semitones) {
        switch (interval) {
            case 3:
                if (semitones == 3) return "小三度";
                if (semitones == 4) return "大三度";
                break;
            case 4:
                if (semitones == 5) return "纯四度";
                if (semitones == 6) return "增四度";
                if (semitones == 4) return "减四度";
                break;
            case 5:
                if (semitones == 7) return "纯五度";
                if (semitones == 8) return "增五度";
                if (semitones == 6) return "减五度";
                break;
            case 6:
                if (semitones == 8) return "小六度";
                if (semitones == 9) return "大六度";
                break;
            case 7:
                if (semitones == 10) return "小七度";
                if (semitones == 11) return "大七度";
                break;
        }
        return "未知";
    }

    public static void main(String[] args) {
        MusicTheoryTrainer trainer = new MusicTheoryTrainer();
        trainer.intervalMemoryPractice();
    }
} 
package MusicTrainer;

public class FretTrainer {
    // 标准6弦吉他空弦音（从6弦到1弦）
    private static final String[] OPEN_STRINGS = {"E", "A", "D", "G", "B", "E"};
    // 十二平均律音名
    private static final String[] NOTES = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
    // C大调音名
    private static final String[] C_MAJOR = {"C", "D", "E", "F", "G", "A", "B"};

    // 获取音名在十二平均律中的序号
    private static int noteIndex(String note) {
        for (int i = 0; i < NOTES.length; i++) {
            if (NOTES[i].equals(note)) return i;
        }
        return -1;
    }

    // 判断是否为C大调音
    private static boolean isCMajor(String note) {
        for (String n : C_MAJOR) {
            if (n.equals(note)) return true;
        }
        return false;
    }

    // 打印12品内指板（只显示C大调音名，其他用-表示）
    public static void printFretboard() {
        System.out.println("\n吉他指板（12品内，C大调）：");
        System.out.print("弦\\品\t");
        for (int fret = 0; fret <= 12; fret++) {
            System.out.print(fret + "\t");
        }
        System.out.println();
        for (int string = 0; string < 6; string++) {
            System.out.print((6 - string) + "弦\t");
            int openIdx = noteIndex(OPEN_STRINGS[string]);
            for (int fret = 0; fret <= 12; fret++) {
                String note = NOTES[(openIdx + fret) % 12];
                if (isCMajor(note)) {
                    System.out.print(note + "\t");
                } else {
                    System.out.print("-\t");
                }
            }
            System.out.println();
        }
    }

    public static void main(String[] args) {
        printFretboard();
    }
}

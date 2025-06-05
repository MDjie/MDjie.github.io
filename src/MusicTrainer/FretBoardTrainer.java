package MusicTrainer;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.util.*;
import java.util.List;
import javax.swing.Timer;
import java.util.HashSet;
import java.util.Set;
import javax.swing.Box;
import javax.swing.BoxLayout;

public class FretBoardTrainer {
    private static final int FRET_COUNT = 12;
    private static final int STRING_COUNT = 6;
    private static final int FRET_WIDTH = 60;
    private static final int STRING_HEIGHT = 40;
    private static final int MARGIN = 50;
    
    private String[][] fretboardNotes;
    private Point selectedPosition = null;
    private String currentQuestion = "";
    private Random random = new Random();
    private Map<String, Integer> mistakeStats = new HashMap<>();
    private JFrame frame;
    private JLabel questionLabel;
    private boolean showNotes = false;
    private boolean isTraining = false;
    private javax.swing.Timer trainingTimer;
    private int trainingTimeLeft = 0;
    private JButton noteToPosBtn;
    private String lastQuestionText = "";
    private String currentNoteWithOctave = "";
    private Set<Point> correctPositions = new HashSet<>();
    private Set<Point> answeredPositions = new HashSet<>();
    private Point lastWrongPosition = null;
    private int lastTrainingSeconds = 20;
    private boolean showAllCorrect = false;
    private javax.swing.Timer showAllCorrectTimer;
    // 位置找音训练相关变量
    private boolean isPosToNoteTraining = false;
    private javax.swing.Timer posToNoteTimer;
    private int posToNoteTimeLeft = 0;
    private int lastPosToNoteSeconds = 20;
    private Point currentPosToNote = null;
    private boolean posToNoteShowResult = false;
    private boolean posToNoteLastCorrect = false;
    private String posToNoteCorrectNote = "";
    private JTextField posToNoteInputField = null;
    private JPanel mainPanel;
    private JPanel bottomPanel;
    
    public FretBoardTrainer() {
        initializeFretboard();
        createAndShowGUI();
    }
    
    private void initializeFretboard() {
        // 国际标准音高：6弦(E2)、5弦(A2)、4弦(D3)、3弦(G3)、2弦(B3)、1弦(E4)
        String[] openStringsStd = {"E", "B", "G", "D", "A", "E"};
        int[] openOctavesStd = {4, 3, 3, 3, 2, 2}; // 1弦E4, 2弦B3, 3弦G3, 4弦D3, 5弦A2, 6弦E2
        String[] notes = {"C", "C#", "D", "D#", "E", "F", "F#", "G", "G#", "A", "A#", "B"};
        
        fretboardNotes = new String[STRING_COUNT][FRET_COUNT + 1];
        for (int s = 0; s < STRING_COUNT; s++) {
            int startIndex = indexOfNote(notes, openStringsStd[s]);
            int octave = openOctavesStd[s];
            for (int f = 0; f <= FRET_COUNT; f++) {
                int noteIndex = (startIndex + f) % 12;
                String note = notes[noteIndex];
                int noteOctave = octave + (startIndex + f) / 12;
                fretboardNotes[s][f] = note + noteOctave;
            }
        }
    }
    
    private int indexOfNote(String[] notes, String note) {
        for (int i = 0; i < notes.length; i++) {
            if (notes[i].equals(note)) {
                return i;
            }
        }
        return -1;
    }
    
    private void createAndShowGUI() {
        frame = new JFrame("吉他指板练习");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new FretboardPanel(), BorderLayout.CENTER);
        
        // 新增底部面板，垂直布局
        bottomPanel = new JPanel();
        bottomPanel.setLayout(new BoxLayout(bottomPanel, BoxLayout.Y_AXIS));
        bottomPanel.add(Box.createVerticalStrut(10));
        questionLabel = new JLabel("点击按钮开始练习", JLabel.CENTER);
        questionLabel.setFont(new Font("宋体", Font.BOLD, 16));
        bottomPanel.add(questionLabel);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        // 控制面板
        JPanel controlPanel = new JPanel(new FlowLayout());
        noteToPosBtn = new JButton("音找位置");
        JButton posToNoteBtn = new JButton("位置找音");
        JButton statsBtn = new JButton("错误统计");
        JButton closeBtn = new JButton("关闭");
        JButton showNotesBtn = new JButton("显示音名");
        
        noteToPosBtn.addActionListener(e -> {
            if (!isTraining) {
                // 弹窗输入限时时长
                String input = JOptionPane.showInputDialog(frame, "请输入限时时长（秒）", "20");
                int seconds = 20;
                try {
                    if (input != null && !input.isEmpty()) {
                        seconds = Integer.parseInt(input);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "输入无效，使用默认20秒。", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
                startNoteToPositionExerciseWithTimer(seconds);
                noteToPosBtn.setText("停止训练");
                isTraining = true;
            } else {
                stopNoteToPositionExercise();
                noteToPosBtn.setText("音找位置");
                isTraining = false;
            }
        });
        posToNoteBtn.addActionListener(e -> {
            if (!isPosToNoteTraining) {
                String input = JOptionPane.showInputDialog(frame, "请输入限时时长（秒）", "20");
                int seconds = 20;
                try {
                    if (input != null && !input.isEmpty()) {
                        seconds = Integer.parseInt(input);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "输入无效，使用默认20秒。", "提示", JOptionPane.INFORMATION_MESSAGE);
                }
                startPosToNoteExerciseWithTimer(seconds);
                posToNoteBtn.setText("停止训练");
                isPosToNoteTraining = true;
            } else {
                stopPosToNoteExercise();
                posToNoteBtn.setText("位置找音");
                isPosToNoteTraining = false;
            }
        });
        statsBtn.addActionListener(e -> showStatistics());
        showNotesBtn.addActionListener(e -> {
            showNotes = !showNotes;
            showNotesBtn.setText(showNotes ? "隐藏音名" : "显示音名");
            frame.repaint();
        });
        closeBtn.addActionListener(e -> frame.dispose());
        
        controlPanel.add(noteToPosBtn);
        controlPanel.add(posToNoteBtn);
        controlPanel.add(statsBtn);
        controlPanel.add(showNotesBtn);
        controlPanel.add(closeBtn);
        
        mainPanel.add(controlPanel, BorderLayout.NORTH);
        
        frame.add(mainPanel);
        frame.pack();
        frame.setSize(FRET_WIDTH * (FRET_COUNT + 1) + 2 * MARGIN, 
                     STRING_HEIGHT * STRING_COUNT + 2 * MARGIN + 150);
        frame.setVisible(true);
    }
    
    private void startNoteToPositionExercise() {
        // 1. 随机选一个C大调自然音（带八度，且在前12品有出现）
        String[] cMajorNotes = {"C", "D", "E", "F", "G", "A", "B"};
        List<String> allNotesWithOctave = new ArrayList<>();
        for (int s = 0; s < STRING_COUNT; s++) {
            for (int f = 0; f <= FRET_COUNT; f++) {
                String noteWithOctave = fretboardNotes[s][f];
                String note = noteWithOctave.replaceAll("\\d", "");
                if (note.length() == 1 && Arrays.asList(cMajorNotes).contains(note)) {
                    if (!allNotesWithOctave.contains(noteWithOctave)) {
                        allNotesWithOctave.add(noteWithOctave);
                    }
                }
            }
        }
        // 随机选一个音高
        currentNoteWithOctave = allNotesWithOctave.get(random.nextInt(allNotesWithOctave.size()));
        currentQuestion = currentNoteWithOctave;
        questionLabel.setText("找出所有的 " + currentNoteWithOctave + " 音位置 (点击指板上的位置)");
        lastQuestionText = questionLabel.getText();
        selectedPosition = null;
        showNotes = false;
        // 2. 记录所有正确位置
        correctPositions.clear();
        answeredPositions.clear();
        for (int s = 0; s < STRING_COUNT; s++) {
            for (int f = 0; f <= FRET_COUNT; f++) {
                if (fretboardNotes[s][f].equals(currentNoteWithOctave)) {
                    correctPositions.add(new Point(f, s));
                }
            }
        }
        frame.repaint();
    }
    
    private void startPositionToNoteExercise() {
        int string = random.nextInt(STRING_COUNT);
        int fret = random.nextInt(FRET_COUNT + 1);
        currentQuestion = "(" + (6 - string) + "弦," + fret + "品)";
        questionLabel.setText(currentQuestion + " 是什么音? (点击指板上任意位置查看答案)");
        selectedPosition = new Point(fret, string);
        showNotes = false;
        frame.repaint();
    }
    
    private void showStatistics() {
        if (mistakeStats.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "暂无错误记录", "统计", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        StringBuilder stats = new StringBuilder("高频错误:\n");
        mistakeStats.entrySet().stream()
            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
            .forEach(entry -> stats.append(entry.getKey()).append(": ").append(entry.getValue()).append("次\n"));
        
        JOptionPane.showMessageDialog(frame, stats.toString(), "错误统计", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void recordMistake(String mistake) {
        mistakeStats.put(mistake, mistakeStats.getOrDefault(mistake, 0) + 1);
    }
    
    private String getPureQuestionText() {
        String text = questionLabel.getText();
        if (text.startsWith("剩余时间：")) {
            int idx = text.indexOf("秒  ");
            if (idx != -1 && idx + 3 < text.length()) {
                return text.substring(idx + 3);
            }
        }
        return text;
    }
    
    private void startNoteToPositionExerciseWithTimer(int seconds) {
        lastTrainingSeconds = seconds;
        startNoteToPositionExercise();
        lastQuestionText = getPureQuestionText();
        trainingTimeLeft = seconds;
        if (trainingTimer != null) trainingTimer.stop();
        trainingTimer = new javax.swing.Timer(1000, e -> {
            trainingTimeLeft--;
            questionLabel.setText("剩余时间：" + trainingTimeLeft + "秒  " + lastQuestionText);
            if (trainingTimeLeft <= 0) {
                // 时间到，先高亮所有正确答案3秒
                showAllCorrect = true;
                frame.repaint();
                if (showAllCorrectTimer != null) showAllCorrectTimer.stop();
                showAllCorrectTimer = new javax.swing.Timer(3000, ev -> {
                    showAllCorrect = false;
                    answeredPositions.clear();
                    lastWrongPosition = null;
                    frame.repaint();
                    // 重新开始同一个音高的训练
                    lastQuestionText = getPureQuestionText();
                    trainingTimeLeft = lastTrainingSeconds;
                    if (trainingTimer != null) trainingTimer.stop();
                    // 重新创建新的timer，防止多timer叠加
                    trainingTimer = new javax.swing.Timer(1000, e2 -> {
                        trainingTimeLeft--;
                        questionLabel.setText("剩余时间：" + trainingTimeLeft + "秒  " + lastQuestionText);
                        if (trainingTimeLeft <= 0) {
                            showAllCorrect = true;
                            frame.repaint();
                            if (showAllCorrectTimer != null) showAllCorrectTimer.stop();
                            showAllCorrectTimer = new javax.swing.Timer(3000, ev2 -> {
                                showAllCorrect = false;
                                answeredPositions.clear();
                                lastWrongPosition = null;
                                frame.repaint();
                                lastQuestionText = getPureQuestionText();
                                trainingTimeLeft = lastTrainingSeconds;
                                if (trainingTimer != null) trainingTimer.stop();
                                // 递归调用，继续新timer
                                startNoteToPositionExerciseWithTimer(lastTrainingSeconds);
                            });
                            showAllCorrectTimer.setRepeats(false);
                            showAllCorrectTimer.start();
                            trainingTimer.stop();
                        }
                    });
                    questionLabel.setText("剩余时间：" + trainingTimeLeft + "秒  " + lastQuestionText);
                    trainingTimer.start();
                });
                showAllCorrectTimer.setRepeats(false);
                showAllCorrectTimer.start();
                trainingTimer.stop();
            }
        });
        questionLabel.setText("剩余时间：" + trainingTimeLeft + "秒  " + lastQuestionText);
        trainingTimer.start();
    }
    
    private void stopNoteToPositionExercise() {
        if (trainingTimer != null) trainingTimer.stop();
        questionLabel.setText("训练已停止");
    }
    
    private void startPosToNoteExerciseWithTimer(int seconds) {
        lastPosToNoteSeconds = seconds;
        // 随机选一个位置
        Random rand = new Random();
        int string = rand.nextInt(STRING_COUNT);
        int fret = rand.nextInt(FRET_COUNT + 1);
        currentPosToNote = new Point(fret, string);
        posToNoteShowResult = false;
        posToNoteLastCorrect = false;
        posToNoteCorrectNote = fretboardNotes[string][fret];
        posToNoteTimeLeft = seconds;
        if (posToNoteTimer != null) posToNoteTimer.stop();
        questionLabel.setText("剩余时间：" + posToNoteTimeLeft + "秒  请输入" + (6-string) + "弦" + fret + "品的音高（如C4）：");
        // 添加输入框到bottomPanel，先移除再添加，保证显示
        if (posToNoteInputField == null) {
            posToNoteInputField = new JTextField(10);
            posToNoteInputField.setFont(new Font("宋体", Font.PLAIN, 16));
            posToNoteInputField.addActionListener(e -> handlePosToNoteInput());
        }
        posToNoteInputField.setText("");
        posToNoteInputField.setEnabled(true);
        bottomPanel.remove(posToNoteInputField);
        bottomPanel.add(posToNoteInputField);
        bottomPanel.revalidate();
        bottomPanel.repaint();
        frame.revalidate();
        frame.repaint();
        posToNoteTimer = new javax.swing.Timer(1000, e -> {
            posToNoteTimeLeft--;
            questionLabel.setText("剩余时间：" + posToNoteTimeLeft + "秒  请输入" + (6-string) + "弦" + fret + "品的音高（如C4）：");
            if (posToNoteTimeLeft <= 0) {
                // 超时，显示红色，3秒后再来一次
                posToNoteShowResult = true;
                posToNoteLastCorrect = false;
                posToNoteInputField.setEnabled(false);
                frame.repaint();
                if (posToNoteTimer != null) posToNoteTimer.stop();
                new javax.swing.Timer(3000, ev -> {
                    posToNoteShowResult = false;
                    frame.repaint();
                    startPosToNoteExerciseWithTimer(lastPosToNoteSeconds);
                }) {{ setRepeats(false); }}.start();
            }
        });
        posToNoteTimer.start();
        frame.repaint();
        posToNoteInputField.requestFocusInWindow();
    }
    
    private void handlePosToNoteInput() {
        if (posToNoteInputField == null || currentPosToNote == null) return;
        if (!posToNoteInputField.isEnabled()) return;
        posToNoteInputField.setEnabled(false);
        String inputNote = posToNoteInputField.getText();
        posToNoteShowResult = true;
        if (inputNote.trim().equalsIgnoreCase(posToNoteCorrectNote)) {
            posToNoteLastCorrect = true;
            frame.repaint();
            if (posToNoteTimer != null) posToNoteTimer.stop();
            new javax.swing.Timer(3000, ev -> {
                posToNoteShowResult = false;
                frame.repaint();
                startPosToNoteExerciseWithTimer(lastPosToNoteSeconds);
            }) {{ setRepeats(false); }}.start();
        } else {
            posToNoteLastCorrect = false;
            frame.repaint();
            if (posToNoteTimer != null) posToNoteTimer.stop();
            new javax.swing.Timer(3000, ev -> {
                posToNoteShowResult = false;
                frame.repaint();
                startPosToNoteExerciseWithTimer(lastPosToNoteSeconds);
            }) {{ setRepeats(false); }}.start();
        }
    }
    
    private void stopPosToNoteExercise() {
        if (posToNoteTimer != null) posToNoteTimer.stop();
        questionLabel.setText("训练已停止");
        currentPosToNote = null;
        posToNoteShowResult = false;
        if (posToNoteInputField != null && bottomPanel != null) {
            bottomPanel.remove(posToNoteInputField);
            bottomPanel.revalidate();
            bottomPanel.repaint();
            frame.revalidate();
            frame.repaint();
        }
        frame.repaint();
    }
    
    class FretboardPanel extends JPanel implements MouseListener {
        public FretboardPanel() {
            addMouseListener(this);
            setPreferredSize(new Dimension(
                FRET_WIDTH * (FRET_COUNT + 1) + 2 * MARGIN,
                STRING_HEIGHT * STRING_COUNT + 2 * MARGIN
            ));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                               RenderingHints.VALUE_ANTIALIAS_ON);
            
            // 绘制背景
            g2.setColor(new Color(240, 220, 180));
            g2.fillRect(MARGIN, MARGIN, 
                       FRET_WIDTH * (FRET_COUNT + 1), 
                       STRING_HEIGHT * STRING_COUNT);
            
            // 绘制琴弦
            g2.setColor(Color.BLACK);
            for (int s = 0; s < STRING_COUNT; s++) {
                int y = MARGIN + s * STRING_HEIGHT + STRING_HEIGHT / 2;
                g2.setStroke(new BasicStroke(s < 3 ? 2 : 1));
                g2.draw(new Line2D.Double(MARGIN, y, 
                                         MARGIN + FRET_WIDTH * (FRET_COUNT + 1), y));
            }
            
            // 绘制品柱
            g2.setColor(Color.DARK_GRAY);
            g2.setStroke(new BasicStroke(3));
            for (int f = 0; f <= FRET_COUNT; f++) {
                int x = MARGIN + f * FRET_WIDTH;
                g2.draw(new Line2D.Double(x, MARGIN, x, 
                                         MARGIN + STRING_HEIGHT * (STRING_COUNT - 1) + STRING_HEIGHT / 2));
            }
            
            // 绘制音符标记
            g2.setColor(new Color(100, 100, 100, 150));
            int[] markedFrets = {3, 5, 7, 9, 12};
            for (int f : markedFrets) {
                int x = MARGIN + f * FRET_WIDTH - FRET_WIDTH / 2;
                int y = MARGIN + STRING_HEIGHT * STRING_COUNT / 2;
                
                if (f == 12) {
                    g2.fillOval(x - 15, y - 20, 10, 10);
                    g2.fillOval(x - 15, y + 10, 10, 10);
                } else {
                    g2.fillOval(x - 5, y - 5, 10, 10);
                }
            }
            
            // 绘制音符标签（只在showNotes时显示全部C大调音）
            if (showNotes) {
                g2.setFont(new Font("宋体", Font.BOLD, 14));
                for (int s = 0; s < STRING_COUNT; s++) {
                    for (int f = 0; f <= FRET_COUNT; f++) {
                        String noteWithOctave = fretboardNotes[s][f];
                        String note = noteWithOctave.replaceAll("\\d", "");
                        if (note.length() == 1 && "CDEFGAB".contains(note)) {
                            int x = MARGIN + f * FRET_WIDTH - FRET_WIDTH / 2;
                            int y = MARGIN + s * STRING_HEIGHT + STRING_HEIGHT / 2;
                            g2.setColor(Color.BLUE);
                            g2.drawString(noteWithOctave, x - 15, y + 5);
                        }
                    }
                }
            }
            // 音找位置训练时的高亮和音名显示（独立于showNotes）
            if ((isTraining || showAllCorrect) && currentNoteWithOctave != null && !currentNoteWithOctave.isEmpty()) {
                g2.setFont(new Font("宋体", Font.BOLD, 14));
                // 绿色高亮所有已答对或全部正确答案
                Set<Point> greenSet = showAllCorrect ? correctPositions : answeredPositions;
                for (Point p : greenSet) {
                    int f = p.x, s = p.y;
                    int x = MARGIN + f * FRET_WIDTH - FRET_WIDTH / 2;
                    int y = MARGIN + s * STRING_HEIGHT + STRING_HEIGHT / 2;
                    g2.setColor(Color.GREEN);
                    g2.fillOval(x - 15, y - 15, 30, 30);
                    g2.setColor(Color.WHITE);
                    g2.drawString(fretboardNotes[s][f], x - 15, y + 5);
                }
                // 红色高亮最后一次错误（仅在非showAllCorrect时）
                if (!showAllCorrect && lastWrongPosition != null) {
                    int f = lastWrongPosition.x, s = lastWrongPosition.y;
                    int x = MARGIN + f * FRET_WIDTH - FRET_WIDTH / 2;
                    int y = MARGIN + s * STRING_HEIGHT + STRING_HEIGHT / 2;
                    g2.setColor(Color.RED);
                    g2.fillOval(x - 15, y - 15, 30, 30);
                    g2.setColor(Color.WHITE);
                    g2.drawString(fretboardNotes[s][f], x - 15, y + 5);
                }
            }
            
            // 位置找音训练的高亮
            if (isPosToNoteTraining && currentPosToNote != null) {
                int f = currentPosToNote.x, s = currentPosToNote.y;
                int x = MARGIN + f * FRET_WIDTH - FRET_WIDTH / 2;
                int y = MARGIN + s * STRING_HEIGHT + STRING_HEIGHT / 2;
                if (posToNoteShowResult) {
                    if (posToNoteLastCorrect) {
                        g2.setColor(Color.GREEN);
                    } else {
                        g2.setColor(Color.RED);
                    }
                } else {
                    g2.setColor(Color.GRAY);
                }
                g2.fillOval(x - 15, y - 15, 30, 30);
                g2.setColor(Color.WHITE);
                if (posToNoteShowResult) {
                    if (posToNoteLastCorrect) {
                        g2.drawString(posToNoteCorrectNote, x - 15, y + 5);
                    } else {
                        g2.drawString(posToNoteCorrectNote, x - 15, y + 5);
                    }
                }
            }
            
            // 绘制弦号和品号
            g2.setColor(Color.BLACK);
            g2.setFont(new Font("宋体", Font.PLAIN, 12));
            
            for (int s = 0; s < STRING_COUNT; s++) {
                int y = MARGIN + s * STRING_HEIGHT + STRING_HEIGHT / 2;
                g2.drawString(Integer.toString(s+1), MARGIN - 45, y + 5);
            }
            
            for (int f = 0; f <= FRET_COUNT; f++) {
                int x = MARGIN + f * FRET_WIDTH;
                g2.drawString(Integer.toString(f), x - 5, MARGIN - 10);
            }
        }
        
        @Override
        public void mouseClicked(MouseEvent e) {
            if (currentQuestion == null || currentQuestion.isEmpty()) return;
            int x = e.getX() - MARGIN;
            int y = e.getY() - MARGIN;
            if (y < 0) return;
            int fret;
            if(x<0) fret=0;
            else 
             fret = x / FRET_WIDTH+1;
            int string = y / STRING_HEIGHT;
            if (fret <= FRET_COUNT && string < STRING_COUNT) {
                // 音找位置模式
                if (isTraining && currentNoteWithOctave != null && !currentNoteWithOctave.isEmpty()) {
                    Point p = new Point(fret, string);
                    String clickedNote = fretboardNotes[string][fret];
                    if (correctPositions.contains(p)) {
                        if (!answeredPositions.contains(p)) {
                            answeredPositions.add(p);
                            lastWrongPosition = null;
                            frame.repaint();
                            // 判断是否全部答完
                            if (answeredPositions.containsAll(correctPositions)) {
                                JOptionPane.showMessageDialog(frame, "全部答对，进入下一个音！", "提示", JOptionPane.INFORMATION_MESSAGE);
                                startNoteToPositionExerciseWithTimer(lastTrainingSeconds);
                            }
                        }
                    } else {
                        lastWrongPosition = p;
                        recordMistake("找" + currentNoteWithOctave + "音错误");
                        JOptionPane.showMessageDialog(frame, "这个位置不是 " + currentNoteWithOctave + " 音，实际为 " + clickedNote, "错误", JOptionPane.WARNING_MESSAGE);
                        frame.repaint();
                    }
                } else {
                    // 位置找音模式（原有逻辑）
                    String answer = fretboardNotes[string][fret];
                    String correctAnswer = fretboardNotes[selectedPosition.y][selectedPosition.x];
                    if (answer.equals(correctAnswer)) {
                        JOptionPane.showMessageDialog(frame, "正确! " + currentQuestion + " 的音是: " + correctAnswer, "答案", JOptionPane.INFORMATION_MESSAGE);
                    } else {
                        recordMistake(currentQuestion + "错误");
                        JOptionPane.showMessageDialog(frame, "错误! " + currentQuestion + " 的音是: " + correctAnswer, "答案", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        }
        
        @Override public void mousePressed(MouseEvent e) {}
        @Override public void mouseReleased(MouseEvent e) {}
        @Override public void mouseEntered(MouseEvent e) {}
        @Override public void mouseExited(MouseEvent e) {}
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("启动FretTrainer...");
            FretBoardTrainer trainer = new FretBoardTrainer();

        });

        // 保持主线程存活
        try {
            Thread.currentThread().join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}


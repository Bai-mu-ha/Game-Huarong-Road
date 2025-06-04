package com.hengbai.ui;

import com.hengbai.game.GameBoard;
import com.hengbai.game.GameEventListener;
import com.hengbai.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class GameScreen implements GameEventListener {
    private final JLayeredPane layeredPane;
    private JLabel background;
    private JLabel timerLabel;
    private GameBoard gameBoard;
    private PauseOverlay pauseOverlay;
    private Timer gameTimer;
    private int secondsElapsed = 0;
    private boolean isPaused = false;
    private boolean gameWon = false;

    private JPanel winPanel;
    private JLabel winTimeLabel; // 显示胜利用时
    private JButton confirmButton; // 确定按钮

    private final JFrame frame;

    public GameScreen(JFrame frame) {
        this.frame = frame;

        layeredPane = new JLayeredPane();
        layeredPane.setPreferredSize(new Dimension(450, 484));
        layeredPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));

        // 使用绝对定位但封装到布局方法中
        setupBackground();
        setupGameBoard(frame);
        setupTimer();
        setupPauseOverlay(frame);
        setupWinLabel();
        setupKeyListener();

        // 设置监听器
        gameBoard.setGameEventListener(this);
    }

    private void setupBackground() {
        background = new JLabel(new ImageIcon(ImageLoader.getImage("image/background.png")));
        background.setBounds(-2, 0, 450, 484);
        layeredPane.add(background, JLayeredPane.DEFAULT_LAYER);
    }

    private void setupGameBoard(JFrame frame) {
        gameBoard = new GameBoard();
        JPanel boardPanel = gameBoard.getPanel();
        boardPanel.setBounds(15, 50, 420, 420);
        layeredPane.add(boardPanel, JLayeredPane.PALETTE_LAYER);
    }

    private void setupTimer() {
        // 创建带背景的 JLabel 用于显示计时器
        timerLabel = new JLabel("00:00") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                // 半透明背景
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);
                super.paintComponent(g);
            }
        };

        // 设置字体、颜色等样式
        timerLabel.setFont(new Font("Digital-7", Font.BOLD, 24));
        timerLabel.setForeground(new Color(255, 215, 0)); // 金色文字
        timerLabel.setOpaque(false); // 禁用默认背景
        timerLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        timerLabel.setBounds(background.getWidth() - 120, 15, 100, 30);

        // 添加到和 background 同一层级，但层级更高（MODAL_LAYER）
        layeredPane.add(timerLabel, JLayeredPane.MODAL_LAYER);

        initGameTimer();
    }

    private void setupWinLabel() {
        winPanel = new JPanel(null);
        winPanel.setOpaque(false);
        winPanel.setPreferredSize(new Dimension(266, 150));

        // 胜利图片
        JLabel winLabel = new JLabel(new ImageIcon(ImageLoader.getImage("image/win.png")));
        winLabel.setBounds(0, 0, 266, 88);
        winPanel.add(winLabel);

        // 时间标签
        winTimeLabel = new JLabel();
        winTimeLabel.setFont(new Font("微软雅黑", Font.BOLD, 20));
        winTimeLabel.setForeground(Color.WHITE);
        winTimeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        winTimeLabel.setBounds(0, 90, 266, 30);
        winPanel.add(winTimeLabel);

        // 确定按钮
        confirmButton = new JButton("确定");
        confirmButton.setFont(new Font("微软雅黑", Font.PLAIN, 16));
        confirmButton.setBackground(new Color(70, 130, 180));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        confirmButton.setBounds(90, 125, 80, 30);
        confirmButton.setVisible(false);
        confirmButton.addActionListener(e -> returnToMainMenu());
        winPanel.add(confirmButton);

        layeredPane.add(winPanel, JLayeredPane.POPUP_LAYER);
        winPanel.setBounds(0, 0, 266, 150);
        winPanel.setVisible(false);
        //winPanel.setEnabled(false);
    }


    private void setupPauseOverlay(JFrame frame) {
        pauseOverlay = new PauseOverlay(this);
        layeredPane.add(pauseOverlay.getPanel(), JLayeredPane.DRAG_LAYER);
        pauseOverlay.getPanel().setVisible(false);
    }

    private void setupKeyListener() {
        layeredPane.setFocusable(true);
        layeredPane.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    togglePause();
                }
            }
        });
        // 使用 SwingUtilities.invokeLater 延迟请求焦点，确保组件已加载完成
        SwingUtilities.invokeLater(layeredPane::requestFocusInWindow);
    }

    private void initGameTimer() {
        gameTimer = new Timer(1000, e -> {
            if (!isPaused) {
                secondsElapsed++;
                updateTimerDisplay();
            }
        });
        gameTimer.start();
    }

    private void updateTimerDisplay() {
        int minutes = secondsElapsed / 60;
        int seconds = secondsElapsed % 60;
        timerLabel.setText(String.format("%02d:%02d", minutes, seconds));
    }

    private void togglePause() {
        isPaused = !isPaused;
        pauseOverlay.getPanel().setVisible(isPaused);
        gameBoard.getPanel().setEnabled(!isPaused);

        if (isPaused) {
            pauseOverlay.getPanel().requestFocus();
            timerLabel.setForeground(Color.RED);
        } else {
            timerLabel.setForeground(new Color(255, 215, 0));
            layeredPane.requestFocus();
        }
    }

    public JLayeredPane getPanel() {
        return layeredPane;
    }

    @Override
    public void onWin() {
        showWin();
    }

    @Override
    public boolean isPaused() {
        return isPaused;
    }

    @Override
    public boolean isGameWon() {
        return gameWon;
    }

    public void showWin() {
        gameWon = true; // 标记游戏已胜利
        gameTimer.stop();

        // 1. 确保面板可见和可交互
        winPanel.setEnabled(true);
        confirmButton.setVisible(true);

        int x = (layeredPane.getWidth() - 266) / 2;
        int y = (layeredPane.getHeight() - 150) / 2;
        winPanel.setLocation(x, y);

        // 3. 更新用时显示
        winTimeLabel.setText("用时：" + timerLabel.getText());

        // 4. 确保在最上层显示
        layeredPane.setLayer(winPanel, JLayeredPane.POPUP_LAYER); // 改为使用系统高层级
        winPanel.setVisible(true);

        // 5. 强制刷新
        layeredPane.revalidate();
        layeredPane.repaint();
    }


    public void resumeGame() {
        togglePause();
        layeredPane.requestFocusInWindow();
    }

    public void restartGame() {
        gameWon = false;
        gameBoard.reset(true);
        resetTimer();
        togglePause();
        layeredPane.requestFocusInWindow();
    }

    private void resetTimer() {
        secondsElapsed = 0;
        updateTimerDisplay();
    }

    public void returnToMainMenu() {
        gameWon = false;
        togglePause(); // 暂停游戏

        // 停止所有计时器
        if (gameTimer != null && gameTimer.isRunning()) {
            gameTimer.stop();
        }

        // 切换回主界面
        SwingUtilities.invokeLater(() -> {
            MainScreen mainScreen = new MainScreen(frame); // 传入当前 frame
            frame.setContentPane(mainScreen.getPanel());
            frame.revalidate();
            frame.repaint();
        });
    }
}

package com.hengbai.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PauseOverlay {
    private JPanel panel;
    private final GameScreen gameScreen;

    public PauseOverlay(GameScreen gameScreen) {
        this.gameScreen = gameScreen;
        initUI();
    }

    private void initUI() {
        panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setColor(new Color(0, 0, 0, 180));
                g2d.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        panel.setBounds(0, 0, 450, 484);
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.insets = new Insets(15, 0, 15, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // 标题
        JLabel title = new JLabel("游戏暂停", SwingConstants.CENTER);
        title.setFont(new Font("微软雅黑", Font.BOLD, 36));
        title.setForeground(Color.WHITE);
        panel.add(title, gbc);

        // 继续按钮
        JButton resumeButton = createMenuButton("继续游戏");
        resumeButton.addActionListener(this::handleResume);
        panel.add(resumeButton, gbc);

        // 重新开始按钮
        JButton restartButton = createMenuButton("重新开始");
        restartButton.addActionListener(this::handleRestart);
        panel.add(restartButton, gbc);

        // 返回主菜单按钮
        JButton menuButton = createMenuButton("返回主菜单");
        menuButton.addActionListener(this::handleMenu);
        panel.add(menuButton, gbc);
    }

    private JButton createMenuButton(String text) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(200, 50));
        btn.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        btn.setBackground(new Color(70, 130, 180));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return btn;
    }

    private void handleResume(ActionEvent e) {
        gameScreen.resumeGame();
        gameScreen.getPanel().requestFocusInWindow(); // 恢复焦点
    }

    private void handleRestart(ActionEvent e) {
        gameScreen.restartGame();
        gameScreen.getPanel().requestFocusInWindow(); // 恢复焦点
    }

    private void handleMenu(ActionEvent e) {
        gameScreen.returnToMainMenu();
    }

    public JPanel getPanel() {
        return panel;
    }
}

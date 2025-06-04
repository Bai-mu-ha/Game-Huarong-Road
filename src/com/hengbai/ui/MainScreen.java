package com.hengbai.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class MainScreen {
    private final JPanel panel;

    public MainScreen(JFrame frame) {
        panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                int width = getWidth();
                int height = getHeight();

                GradientPaint gp = new GradientPaint(0, 0, new Color(70, 130, 180), 0, height, new Color(100, 149, 237));
                g2d.setPaint(gp);
                g2d.fillRect(0, 0, 450, 484);
            }
        };

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.anchor = GridBagConstraints.NORTH;

        JLabel titleLabel = new JLabel("数字华容道", SwingConstants.CENTER);
        titleLabel.setFont(new Font("微软雅黑", Font.BOLD, 36));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        panel.add(titleLabel, gbc);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 1, 20, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setPreferredSize(new Dimension(250, 120));

        JButton startButton = createStyledButton("开始游戏");
        startButton.addActionListener((ActionEvent e) -> {
            GameScreen gameScreen = new GameScreen(frame);
            frame.setContentPane(gameScreen.getPanel());
            frame.revalidate();
        });

        JButton exitButton = createStyledButton("退出");
        exitButton.addActionListener((ActionEvent e) -> {
            System.exit(0);
        });

        buttonPanel.add(startButton);
        buttonPanel.add(exitButton);

        gbc.anchor = GridBagConstraints.CENTER;
        panel.add(buttonPanel, gbc);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("微软雅黑", Font.PLAIN, 20));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(0, 150, 255));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createLineBorder(Color.WHITE, 2, true));
        button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBorder(BorderFactory.createLineBorder(new Color(255, 215, 0), 2));
            }
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBorder(BorderFactory.createEmptyBorder());
            }
        });

        return button;
    }

    public JPanel getPanel() {
        return panel;
    }
}

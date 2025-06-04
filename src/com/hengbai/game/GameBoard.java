package com.hengbai.game;

import com.hengbai.util.ImageLoader;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameBoard {
    private final JPanel panel;
    private final Tile[][] tiles = new Tile[4][4];
    private int emptyRow = 3, emptyCol = 3;
    private GameEventListener listener;

    public GameBoard() {
        panel = new JPanel(new GridLayout(4, 4, 2, 2)) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;

                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);

                g2d.setStroke(new BasicStroke(1.5f));
                g2d.setColor(new Color(255, 255, 255, 30));
                for (int i = 1; i < 4; i++) {
                    g2d.drawLine(i * 105, 0, i * 105, getHeight());
                    g2d.drawLine(0, i * 105, getWidth(), i * 105);
                }
            }
        };
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(420, 420));

        initializeTiles();
        shuffleTiles(100);
        drawTiles();
    }

    public void setGameEventListener(GameEventListener listener) {
        this.listener = listener;
    }

    public void reset(boolean shuffle) {
        initializeTiles();
        this.emptyRow = 3;     // ✅ 强制重置空格位置
        this.emptyCol = 3;
        if (shuffle) {
            shuffleTiles(100);
        }
        drawTiles();
    }

    private void initializeTiles() {
        int number = 1;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (row == 3 && col == 3) {
                    tiles[row][col] = null;
                } else {
                    tiles[row][col] = new Tile(number++);
                }
            }
        }
    }

    public void shuffleTiles(int moves) {
        Random rand = new Random();
        for (int i = 0; i < moves; i++) {
            List<Point> movable = getMovablePoints();
            Point move = movable.get(rand.nextInt(movable.size()));
            swapTiles(move.x, move.y, emptyRow, emptyCol);
            emptyRow = move.x;
            emptyCol = move.y;
        }
        drawTiles();
    }

    private List<Point> getMovablePoints() {
        List<Point> points = new ArrayList<>();
        if (emptyRow > 0) points.add(new Point(emptyRow - 1, emptyCol));
        if (emptyRow < 3) points.add(new Point(emptyRow + 1, emptyCol));
        if (emptyCol > 0) points.add(new Point(emptyRow, emptyCol - 1));
        if (emptyCol < 3) points.add(new Point(emptyRow, emptyCol + 1));
        return points;
    }

    private void checkWin() {
        int expected = 1;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                if (row == 3 && col == 3) continue;
                if (tiles[row][col] == null || tiles[row][col].getNumber() != expected++) {
                    return;
                }
            }
        }
        if (listener != null) {
            listener.onWin();
        }
    }

    private void drawTiles() {
        panel.removeAll();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 4; col++) {
                Tile tile = tiles[row][col];
                if (tile != null) {
                    JButton button = createTileButton(tile, row, col);
                    panel.add(button);
                } else {
                    panel.add(new JLabel()); // 空白格
                }
            }
        }
        panel.revalidate();
        panel.repaint();
    }

    private JButton createTileButton(Tile tile, int row, int col) {
        ImageIcon icon = loadTileImage(tile.getNumber());

        JButton button = new JButton(icon) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D)g;
                g2d.setColor(new Color(0, 0, 0, 150));
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 20, 20);
                g2d.setStroke(new BasicStroke(1.5f));
                g2d.setColor(new Color(255, 255, 255, 30));
                for (int i = 1; i < 4; i++) {
                    g2d.drawLine(i * 105, 0, i * 105, getHeight());
                    g2d.drawLine(0, i * 105, getWidth(), i * 105);
                }
            }
        };

        button.setBorder(BorderFactory.createEmptyBorder());
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(100, 100));
        button.addActionListener(e -> tryMoveTile(row, col));
        return button;
    }

    private ImageIcon loadTileImage(int number) {
        try {
            BufferedImage img = ImageLoader.getImage("image/" + number + ".png");
            if (img != null) {
                return new ImageIcon(img.getScaledInstance(96, 96, Image.SCALE_SMOOTH));
            }
        } catch (Exception e) {
            System.err.println("加载图片失败: " + number);
        }
        return createFallbackIcon(number);
    }

    private ImageIcon createFallbackIcon(int number) {
        BufferedImage img = new BufferedImage(96, 96, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        GradientPaint gp = new GradientPaint(0, 0, new Color(70, 130, 180), 100, 100, new Color(32, 78, 128));
        g.setPaint(gp);
        g.fillRoundRect(0, 0, 96, 96, 15, 15);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String text = String.valueOf(number);
        FontMetrics fm = g.getFontMetrics();
        int x = (96 - fm.stringWidth(text)) / 2;
        int y = ((96 - fm.getHeight()) / 2) + fm.getAscent();
        g.drawString(text, x, y);

        g.dispose();
        return new ImageIcon(img);
    }

    private void tryMoveTile(int row, int col) {
        // 添加游戏状态检查
        if (listener != null && (listener.isPaused() || listener.isGameWon())) {
            return;
        }

        if (Math.abs(row - emptyRow) + Math.abs(col - emptyCol) == 1) {
            swapTiles(row, col, emptyRow, emptyCol);
            emptyRow = row;
            emptyCol = col;
            drawTiles();
            checkWin();
        }
    }

    private void swapTiles(int r1, int c1, int r2, int c2) {
        Tile temp = tiles[r1][c1];
        tiles[r1][c1] = tiles[r2][c2];
        tiles[r2][c2] = temp;
    }

    public JPanel getPanel() {
        return panel;
    }
}

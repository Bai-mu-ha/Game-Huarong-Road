package com.hengbai.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.InputStream;

public class ImageLoader {
    // 统一资源路径格式为 "image/xxx.png"
    public static BufferedImage getImage(String filename) {
        try {
            InputStream is = ImageLoader.class.getClassLoader().getResourceAsStream(filename);
            if (is != null) {
                return ImageIO.read(is);
            }
            System.err.println("❌ 资源不存在: " + filename);
        } catch (Exception e) {
            System.err.println("⚠️ 加载异常: " + filename);
            e.printStackTrace();
        }
        return createDefaultImage();
    }

    // 提供默认图像以应对资源缺失情况
    private static BufferedImage createDefaultImage() {
        BufferedImage img = new BufferedImage(100, 100, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.RED);
        g.fillRect(0, 0, 100, 100);
        g.dispose();
        return img;
    }
}

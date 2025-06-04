import com.hengbai.util.ImageLoader;

import java.net.URL;

public class Test {
    public static void main(String[] args) {
        URL url = ImageLoader.class.getClassLoader().getResource("image/background.png");
        if (url == null) {
            System.err.println("❌ 资源不存在，请检查路径！");
        } else {
            System.out.println("✅ 资源存在：" + url);
        }
    }
}

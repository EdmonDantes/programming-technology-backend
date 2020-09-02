package ru.loginov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import java.io.IOException;

@SpringBootApplication
public class Main {

    public static void main(String[] args) throws IOException {
        SpringApplication.run(Main.class, args);
//        var colorSpace = new LABColorSpace();
//        var colorSettings = new LABColorSettings(0.02f, 0.2f, colorSpace);
//        Class<? extends Figure> clazz = PointFigure.class;
//        Class<? extends ImagePart> clazz1 = DefaultImagePart.class;
//        var manager = new DefaultImageManager<LABColor>(64, 64, colorSpace, colorSettings, (Class<? extends Figure<LABColor>>) clazz, (Class<? extends ImagePart<LABColor>>) clazz1);
//        var image = ImageIO.read(new File("./image3.jpg"));
//        manager.init(image.getWidth(), image.getHeight(), (x, y) -> image.getRGB(x, y), (n,m) -> null);
    }


}

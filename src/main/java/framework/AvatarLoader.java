package framework;

import com.example.tictactoe2.MainController;
import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

import java.io.File;
import java.io.InputStream;

public class AvatarLoader {

    private static final String DEFAULT_AVATAR = "/avatar/hello.png";

    /** returns saved avatar*/
    public static Image loadImage(String username) {
        String path = MainController.loadAvatar(username);

        if (path != null) {
            File file = new File(path);
            if (file.exists()) {
                Image img = new Image(file.toURI().toString(), 200, 200, false, true);
                if (!img.isError()) return img;
            }
        }
        return loadDefault();
    }
    /** load default image (the cat (hello.png))*/
    private static Image loadDefault() {
        InputStream stream = AvatarLoader.class.getResourceAsStream(DEFAULT_AVATAR);
        return stream != null ? new Image(stream) : null;
    }

    /** fills a circle with avatar. */
    public static void applyTo(Circle circle, String username) {
        Image img = loadImage(username);
        if (img != null) {
            circle.setFill(new ImagePattern(img));
        }
    }
}

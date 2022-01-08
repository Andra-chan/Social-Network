package socialnetwork.Util.imageHelper;

import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import socialnetwork.App;
import socialnetwork.domain.ImageHolder;
import socialnetwork.domain.Utilizator;

public class Helpers {
    public static void setProfileImage(ImageHolder holder, ImageView view){
        if (holder.getImagePath() == null) {
            String path = App.defaultImagePath;
            view.setImage(new Image(path));
            view.setBlendMode(BlendMode.DARKEN);
            holder.setImagePath(path);
        } else {
            view.setImage(new Image(holder.getImagePath()));
            if(holder.getImagePath().equals(App.defaultImagePath))
                view.setBlendMode(BlendMode.DARKEN);
            else
                view.setBlendMode(BlendMode.SRC_OVER);

        }
    }
}

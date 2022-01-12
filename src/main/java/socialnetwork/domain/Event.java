package socialnetwork.domain;

import java.time.LocalDateTime;

public class Event extends Entity<Long> implements ImageHolder{
    String image_path;
    String title;
    String description;
    LocalDateTime date;

    public Event(String image_path, String title, String description, LocalDateTime date) {
        this.image_path = image_path;
        this.title = title;
        this.description = description;
        this.date = date;
    }

    @Override
    public String getImagePath() {
        return image_path;
    }

    @Override
    public void setImagePath(String path) {
        this.image_path = path;
    }

    public String getImage_path() {
        return image_path;
    }

    public void setImage_path(String image_path) {
        this.image_path = image_path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
}

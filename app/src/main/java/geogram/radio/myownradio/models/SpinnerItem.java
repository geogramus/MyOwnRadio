package geogram.radio.myownradio.models;

/**
 * Created by geogr on 14.03.2018.
 */

public class SpinnerItem {
   private int image;

    public int getImage() {
        return image;
    }

    public String getText() {
        return text;
    }

    private String text;

    public SpinnerItem(int image, String text) {
        this.image = image;
        this.text = text;
    }
}

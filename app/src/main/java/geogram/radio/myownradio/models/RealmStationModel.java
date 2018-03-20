package geogram.radio.myownradio.models;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by geogr on 12.03.2018.
 */

public class RealmStationModel extends RealmObject{
    @PrimaryKey
    private String streamChanelUrl;
    private String chanelName;
    private int chanelImage;


    private String chanelImageBitmap;
    private int mainButtonImage;




    public String getStreamChanelUrl() {
        return streamChanelUrl;
    }

    public String getChanelName() {
        return chanelName;
    }

    public int getChanelImage() {
        return chanelImage;
    }

    public int getMainButtonImage() {
        return mainButtonImage;
    }
    public void setMainButtonImage(int mainButtonImage) {
        this.mainButtonImage = mainButtonImage;
    }
    public void setStreamChanelUrl(String streamChanelUrl) {
        this.streamChanelUrl = streamChanelUrl;
    }

    public void setChanelName(String chanelName) {
        this.chanelName = chanelName;
    }

    public void setChanelImage(int chanelImage) {
        this.chanelImage = chanelImage;
    }
    public String getChanelImageBitmap() {
        return chanelImageBitmap;
    }

    public void setChanelImageBitmap(String chanelImageBitmap) {
        this.chanelImageBitmap = chanelImageBitmap;
    }

}

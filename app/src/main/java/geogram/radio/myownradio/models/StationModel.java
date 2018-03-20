package geogram.radio.myownradio.models;

import android.os.Parcel;
import android.os.Parcelable;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by geogr on 06.03.2018.
 */

public class StationModel implements Parcelable {



    private String streamChanelUrl;
    private String chanelName;
    private int chanelImage;



    private String chanelImageBitmap;
    private int mainButtonImage;

    public StationModel(String streamChanelUrl, String chanelName,String chanelImageBitmap, int chanelImage, int mainButtonImage) {
        this.streamChanelUrl = streamChanelUrl;
        this.chanelName = chanelName;
        this.chanelImage = chanelImage;
        this.mainButtonImage = mainButtonImage;
        this.chanelImageBitmap=chanelImageBitmap;
    }


    protected StationModel(Parcel in) {
        streamChanelUrl = in.readString();
        chanelName = in.readString();
        chanelImage = in.readInt();
        mainButtonImage = in.readInt();
        chanelImageBitmap=in.readString();
    }

    public static final Creator<StationModel> CREATOR = new Creator<StationModel>() {
        @Override
        public StationModel createFromParcel(Parcel in) {
            return new StationModel(in);
        }

        @Override
        public StationModel[] newArray(int size) {
            return new StationModel[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
       parcel.writeString(streamChanelUrl);
       parcel.writeString(chanelName);
       parcel.writeInt(chanelImage);
       parcel.writeInt(mainButtonImage);
       parcel.writeString(chanelImageBitmap);
    }
}

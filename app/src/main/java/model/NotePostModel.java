package model;

import android.os.Parcel;
import android.os.Parcelable;

public class NotePostModel implements Parcelable {

    private String title;
    private String content, key, mImageUrl, record;
    private long timeLengthMinutes, timeLengthSeconds;
    private String selectColorImg;
    private String selectinput, labelTag;
    private String date, recordedVoiceUrl;
    String labelKey;
    private boolean selected = false;

//

    public NotePostModel(String title, String content, String key, String mImageUrl, String record, long timeLengthMinutes, long timeLengthSeconds, String selectColorImg, String selectinput, String labelTag, String date, String labelKey, String recordedVoiceUrl) {
        this.title = title;
        this.content = content;
        this.key = key;
        this.mImageUrl = mImageUrl;
        this.record = record;
        this.timeLengthMinutes = timeLengthMinutes;
        this.timeLengthSeconds = timeLengthSeconds;
        this.selectColorImg = selectColorImg;
        this.selectinput = selectinput;
        this.labelTag = labelTag;
        this.date = date;
        this.selected = selected;
        this.labelKey = labelKey;
        this.recordedVoiceUrl = recordedVoiceUrl;
    }

//    public NotePostModel(String title, String key, String record, long timeLengthMinutes, long timeLengthSeconds, String selectinput, String labelTag, String date, String labelKey, boolean selected) {
//        this.title = title;
//        this.key = key;
//        this.record = record;
//        this.timeLengthMinutes = timeLengthMinutes;
//        this.timeLengthSeconds = timeLengthSeconds;
//        this.selectinput = selectinput;
//        this.labelTag = labelTag;
//        this.date = date;
//        this.labelKey = labelKey;
//        this.selected = selected;
//    }

    public NotePostModel() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }


    public NotePostModel(Parcel in) {
        title = in.readString();
        content = in.readString();
        key = in.readString();
        mImageUrl = in.readString();
        record = in.readString();
        timeLengthMinutes = in.readLong();
        timeLengthSeconds = in.readLong();
        selectColorImg = in.readString();
        labelTag = in.readString();
        date = in.readString();
        recordedVoiceUrl = in.readString();
        recordedVoiceUrl = in.readString();

    }

    public static final Creator<NotePostModel> CREATOR = new Creator<NotePostModel>() {
        @Override
        public NotePostModel createFromParcel(Parcel in) {
            return new NotePostModel(in);
        }

        @Override
        public NotePostModel[] newArray(int size) {
            return new NotePostModel[size];
        }
    };

    public long getTimeLengthMinutes() {
        return timeLengthMinutes;
    }

    public void setTimeLengthMinutes(long timeLengthMinutes) {
        this.timeLengthMinutes = timeLengthMinutes;
    }

    public long getTimeLengthSeconds() {
        return timeLengthSeconds;
    }

    public void setTimeLengthSeconds(long timeLengthSeconds) {
        this.timeLengthSeconds = timeLengthSeconds;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getmImageUrl() {
        return mImageUrl;
    }

    public void setmImageUrl(String mImageUrl) {
        this.mImageUrl = mImageUrl;
    }

    public String getRecord() {
        return record;
    }

    public void setRecord(String record) {
        this.record = record;
    }

    public String getSelectColorImg() {
        return selectColorImg;
    }

    public void setSelectColorImg(String selectColorImg) {
        this.selectColorImg = selectColorImg;
    }


    public String getSelectinput() {
        return selectinput;
    }

    public void setSelectinput(String selectinput) {
        this.selectinput = selectinput;
    }

    public String getLabelTag() {
        return labelTag;
    }

    public void setLabelTag(String labelTag) {
        this.labelTag = labelTag;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getLabelKey() {
        return labelKey;
    }

    public void setLabelKey(String labelKey) {
        this.labelKey = labelKey;
    }


    public String getRecordedVoiceUrl() {
        return recordedVoiceUrl;
    }

    public void setRecordedVoiceUrl(String recordedVoiceUrl) {
        this.recordedVoiceUrl = recordedVoiceUrl;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(title);
        dest.writeString(content);
        dest.writeString(key);
        dest.writeString(mImageUrl);
        dest.writeLong(timeLengthMinutes);
        dest.writeLong(timeLengthSeconds);
        dest.writeString(selectColorImg);
        dest.writeString(labelTag);
        dest.writeString(date);
        dest.writeString(recordedVoiceUrl);

    }
}
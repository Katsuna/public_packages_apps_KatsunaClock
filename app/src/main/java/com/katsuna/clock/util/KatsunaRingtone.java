package com.katsuna.clock.util;

public class KatsunaRingtone {

    private long mId;
    private String mUri;
    private String mTitle;

    public KatsunaRingtone(long id, String title, String uri) {
        setId(id);
        setTitle(title);
        setUri(uri);
    }

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getUri() {
        return mUri;
    }

    public void setUri(String uri) {
        this.mUri = uri;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        this.mTitle = title;
    }
}

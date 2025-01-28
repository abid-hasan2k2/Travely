package com.example.travely;

public class BannerItem {
    private int imageResource;
    private String eventStart;
    private String eventName;
    private String eventLocation;
    private String url;

    public BannerItem(int imageResource, String eventStart, String eventName, String eventLocation, String url) {
        this.imageResource = imageResource;
        this.eventStart = eventStart;
        this.eventName = eventName;
        this.eventLocation = eventLocation;
        this.url = url;
    }

    public int getImageResource() {
        return imageResource;
    }

    public String getEventStart() {
        return eventStart;
    }

    public String getEventName() {
        return eventName;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getUrl() {
        return url;
    }
}

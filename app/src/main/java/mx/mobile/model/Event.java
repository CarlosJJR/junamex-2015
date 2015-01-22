package mx.mobile.model;

import com.google.android.gms.maps.model.LatLng;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by desarrollo16 on 20/01/15.
 */
public class Event {

    private String id;
    private String eventName;
    private String eventAbstract;
    private String locationName;
    private LatLng location;
    private Date startTime;
    private Date endTime;

    public String getId() {
        return id;
    }

    public Event setId(String id) {
        this.id = id;
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public Event setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public String getEventAbstract() {
        return eventAbstract;
    }

    public Event setEventAbstract(String eventAbstract) {
        this.eventAbstract = eventAbstract;
        return this;
    }

    public String getLocationName() {
        return locationName;
    }

    public Event setLocationName(String locationName) {
        this.locationName = locationName;
        return this;
    }

    public LatLng getLocation() {
        return location;
    }

    public Event setLocation(LatLng location) {
        this.location = location;
        return this;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Event setStartTime(Date startTime) {
        this.startTime = startTime;
        return this;
    }

    public Date getEndTime() {
        return endTime;
    }

    public Event setEndTime(Date endTime) {
        this.endTime = endTime;
        return this;
    }

    public String getStartTimeString() {

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm", Locale.US);
        return dateFormat.format(this.startTime);
    }
}

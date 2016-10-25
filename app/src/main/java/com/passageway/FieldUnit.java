package com.passageway;

import java.io.Serializable;

/**
 * Field Unit.
 * Represents all the data associated with each unit for the project.
 */
public class FieldUnit implements Serializable {
    private String building;
    private String cid;
    private String ip;
    private int direction;
    private int floor;
    private double lat;
    private double lon;
    private String name;
    private String wing;
    private String key;

    public FieldUnit(String key, String building, String cid, String ip, int direction, int floor, double lat, double lon, String name, String wing) {
        this.key = key;
        this.building = building;
        this.cid = cid;
        this.ip = ip;
        this.direction = direction;
        this.floor = floor;
        this.lat = lat;
        this.lon = lon;
        this.name = name;
        this.wing = wing;
    }


    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getWing() {
        return wing;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public String getBuilding() {
        return building;
    }

    public void setBuilding(String building) {
        this.building = building;
    }

    public String getCid() {
        return cid;
    }

    public void setCid(String cid) {
        this.cid = cid;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public int getFloor() {
        return floor;
    }

    public void setFloor(int floor) {
        this.floor = floor;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}

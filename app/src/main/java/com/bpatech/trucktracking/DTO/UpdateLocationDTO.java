package com.bpatech.trucktracking.DTO;

/**
 * Created by Anita on 1/20/2016.
 */
public class UpdateLocationDTO {
    int update_id;
    String driver_phone_no;
    String location_latitude;
    String location_longitude;
    String location;
    String fulladdress;
    String updatetime;

    public UpdateLocationDTO() {
    }


    public UpdateLocationDTO(int update_id, String driver_phone_no, String location_latitude,
                String location_longitude,String location,String fulladdress,String updatetime) {
        super();
        this.update_id = update_id;
        this.driver_phone_no = driver_phone_no;
        this.location_latitude = location_latitude;
        this.location_longitude = location_longitude;
        this.location = location;
        this.fulladdress = fulladdress;
        this.updatetime = updatetime;
    }


    public String getLocation_longitude() {
        return location_longitude;
    }

    public void setLocation_longitude(String location_longitude) {
        this.location_longitude = location_longitude;
    }

    public int getUpdate_id() {

        return update_id;
    }

    public void setUpdate_id(int update_id) {
        this.update_id = update_id;
    }

    public String getLocation_latitude() {
        return location_latitude;
    }

    public void setLocation_latitude(String location_latitude) {
        this.location_latitude = location_latitude;
    }

    public String getDriver_phone_no() {
        return driver_phone_no;
    }

    public void setDriver_phone_no(String driver_phone_no) {
        this.driver_phone_no = driver_phone_no;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getFulladdress() {
        return fulladdress;
    }

    public void setFulladdress(String fulladdress) {
        this.fulladdress = fulladdress;
    }
    public String getUpdatetime() {
        return updatetime;
    }

    public void setUpdatetime(String updatetime) {
        this.updatetime = updatetime;
    }
}

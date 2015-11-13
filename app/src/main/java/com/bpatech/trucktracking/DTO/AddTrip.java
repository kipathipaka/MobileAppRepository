package com.bpatech.trucktracking.DTO;

public class AddTrip {
	int vehicle_trip_id;
	String destination;
	String truckno;
	String driver_phone_no;
	String owner_phone_no;
	boolean owner_status;
	String customer_company;
	String customer_name;
	String customer_phoneno;
	String start_end_Trip;
	boolean startstatus;
	String source;
	String latitude;
	String location;
	String longitude;
	String update_time;
	String create_time;
	String last_sync_time;

	public AddTrip()
	{
		
	}
	public AddTrip(int vehicle_trip_id,String destination, String truckno, String driver_phone_no,
				   String customer_company, String customer_name, String customer_phoneno,String source,
				   boolean startstatus,String start_end_Trip,String latitude, String location, String longitude, String update_time,
						   String create_time,String last_sync_time) {
		this.vehicle_trip_id = vehicle_trip_id;
		this.destination = destination;
		this.truckno = truckno;
		this.driver_phone_no = driver_phone_no;
		this.customer_company = customer_company;
		this.customer_name = customer_name;
		this.customer_phoneno = customer_phoneno;
		this.source = source;
		this.startstatus = startstatus;
		this.start_end_Trip = start_end_Trip;
		this.latitude = latitude;
		this.location = location;
		this.longitude = longitude;
		this.update_time = update_time;
		this.create_time = create_time;
		this.last_sync_time = last_sync_time;
	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}

	public String getTruckno()
	{
		return truckno;
	}
	public void setTruckno(String truckno) {
		this.truckno = truckno;
	}
	public String getDriver_phone_no() {
		return driver_phone_no;
	}
	public void setDriver_phone_no(String driver_phone_no) {
		this.driver_phone_no = driver_phone_no;
	}
	public String getCustomer_company() {
		return customer_company;
	}
	public void setCustomer_company(String customer_company) {
		this.customer_company = customer_company;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getCustomer_phoneno() {
		return customer_phoneno;
	}
	public void setCustomer_phoneno(String customer_phoneno) {
		this.customer_phoneno = customer_phoneno;
	}

	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getStart_end_Trip() {
		return start_end_Trip;
	}
	public void setStart_end_Trip(String start_end_Trip) {
		this.start_end_Trip = start_end_Trip;
	}
	public int getVehicle_trip_id() {

		return vehicle_trip_id;
	}

	public void setVehicle_trip_id(int vehicle_trip_id) {
		this.vehicle_trip_id = vehicle_trip_id;
	}

	public boolean isStartstatus() {
		return startstatus;
	}
	public void setStartstatus(boolean startstatus) {
		this.startstatus = startstatus;
	}
	public boolean isOwner_status() {
		return owner_status;
	}
	public void setOwner_status(boolean owner_status) {
		this.owner_status = owner_status;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {

		this.longitude = longitude;
	}
	public String getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(String update_time) {

		this.update_time = update_time;
	}

	public String getLast_sync_time() {
		return last_sync_time;
	}
	public void setLast_sync_time(String last_sync_time) {
		this.last_sync_time = last_sync_time;
	}
	public String getCreate_time() {
		return create_time;
	}
	public void setCreate_time(String create_time) {
		this.create_time = create_time;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}

	public String getOwner_phone_no() {
		return owner_phone_no;
	}
	public void setOwner_phone_no(String owner_phone_no) {
		this.owner_phone_no = owner_phone_no;
	}
}

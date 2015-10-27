package com.bpatech.trucktracking.DTO;

public class AddTrip {
	int vehicle_trip_id;
	String destination;
	String truckno;
	String driver_phone_no;
	String customer_company;
	String customer_name;
	String customer_phoneno;
	String source;
	public AddTrip()
	{
		
	}
	public AddTrip(int vehicle_trip_id,String destination, String truckno, String driver_phone_no,
				   String customer_company, String customer_name, String customer_phoneno,String source) {
		this.vehicle_trip_id = vehicle_trip_id;
		this.destination = destination;
		this.truckno = truckno;
		this.driver_phone_no = driver_phone_no;
		this.customer_company = customer_company;
		this.customer_name = customer_name;
		this.customer_phoneno = customer_phoneno;
		this.source = source;

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
	public int getVehicle_trip_id() {

		return vehicle_trip_id;
	}

	public void setVehicle_trip_id(int vehicle_trip_id) {
		this.vehicle_trip_id = vehicle_trip_id;
	}

}

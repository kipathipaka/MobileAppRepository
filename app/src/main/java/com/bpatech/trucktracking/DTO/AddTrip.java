package com.bpatech.trucktracking.DTO;

public class AddTrip {
	
	String destination;
	String truckno;
	String phone_no;
	String customer;
	String customer_name;
	String customer_no;
	String source;
	public AddTrip()
	{
		
	}
	public AddTrip(String destination, String truckno, String phone_no,
				   String customer, String customer_name, String customer_no,String source) {

		this.destination = destination;
		this.truckno = truckno;
		this.phone_no = phone_no;
		this.customer = customer;
		this.customer_name = customer_name;
		this.customer_no = customer_no;
		this.source = source;

	}
	public String getDestination() {
		return destination;
	}
	public void setDestination(String destination) {
		this.destination = destination;
	}
	public String getTruckno() {
		return truckno;
	}
	public void setTruckno(String truckno) {
		this.truckno = truckno;
	}
	public String getPhone_no() {
		return phone_no;
	}
	public void setPhone_no(String phone_no) {
		this.phone_no = phone_no;
	}
	public String getCustomer() {
		return customer;
	}
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	public String getCustomer_name() {
		return customer_name;
	}
	public void setCustomer_name(String customer_name) {
		this.customer_name = customer_name;
	}
	public String getCustomer_no() {
		return customer_no;
	}
	public void setCustomer_no(String customer_no) {
		this.customer_no = customer_no;
	}

	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}

}

package com.bpatech.trucktracking.DTO;


public class MessageDTO {
    String invite_message;
    String share_message;
    String add_driver_message;


    public MessageDTO() {
    }
    public MessageDTO(String invite_message, String share_message,
                String add_driver_message) {
        super();
        this.invite_message = invite_message;
        this.share_message = share_message;
        this.add_driver_message = add_driver_message;
    }
    public String getInvite_message() {
        return invite_message;
    }

    public void setInvite_message(String invite_message) {
        this.invite_message = invite_message;
    }
    public String getShare_message() {
        return share_message;
    }

    public void setShare_message(String share_message) {
        this.share_message = share_message;
    }
    public String getAdd_driver_message() {
        return add_driver_message;
    }

    public void setAdd_driver_message(String add_driver_message) {
        this.add_driver_message = add_driver_message;
    }
}

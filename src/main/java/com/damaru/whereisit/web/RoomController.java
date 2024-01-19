package com.damaru.whereisit.web;

import java.io.Serializable;

import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ValueChangeEvent;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import com.damaru.whereisit.model.EditAction;
import com.damaru.whereisit.model.Room;
import com.damaru.whereisit.service.Repository;

@Named
@SessionScoped
public class RoomController implements Serializable {

    private static final long serialVersionUID = 1L;

    @Inject
    private Logger log;
 
    @Inject
    private Repository repository;
    
    @Inject
    private FacesContext facesContext;
    
    private Room room = new Room();
    
    private EditAction editAction = EditAction.none;
    
    private boolean editable;
    
    private String message = " ";
    
    public void newListener(ActionEvent e) {
        
        if (editAction == EditAction.edit) {
            return;
        }
        
        setEditable(true);
        setMessage("Enter new room:");
        room = new Room();
        editAction = EditAction.create;
    }
    
    /*
    // No longer used, replaced by newListener above. 
    public String create() {
        try {
            log.infof("Saving %s", room);
            repository.save(room);
            setMessage("A new room with id " + room.getId() + " has been created successfully");
            room = new Room();
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error occurred while creating the room.", e.getMessage()));
        }
        return "room";
    }
    */
    
    public void saveListener(ActionEvent event) {

        if (editAction == EditAction.none) {
            return;
        }

        try {
            log.infof("Saving %s", room);
            boolean isPersisted = room.isPersisted();
            repository.save(room);
            String msg = isPersisted ? 
                    "Updated the room." : "A new room with id " + room.getId() + " has been created successfully";
            setMessage(msg);
            select(room);
            editAction = EditAction.none;
            editable = false;
        } catch (Exception e) {
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error occurred while creating the room.", e.getMessage()));
        }
    }

    private void select(Room room) {
        // do we need this?
    }
    
    public void cancelListener(ActionEvent e) {

        if (editAction == EditAction.none) {
            setMessage("");
            return;
        }

        setMessage("Cancelled.");
        editAction = EditAction.none;
        setEditable(false);
        
        
        if (room.isPersisted()) {
            room = repository.findRoomById(room.getId());
        } else {
            room = new Room();
        }

    }
    
    public void editListener(ActionEvent e) {

        if (editAction == EditAction.create) {
            return;
        }

        setEditable(true);
        setMessage("Editing: ");
        editAction = EditAction.edit;
    }

    public void deleteListener(ActionEvent ev) {
        
        log.info("deleteListener: " + editAction);

        if (editAction != EditAction.none || !room.isPersisted()) {
            return;
        }
        
        try {
            repository.delete(room);
            room = new Room();
            setEditable(false);
            editAction = EditAction.none;
            setMessage("Deleted " + room.getName());
        } catch (Exception e) {
            e.printStackTrace();
            facesContext.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, "An error occurred while deleting the room.", e.getMessage()));
        }
    }


    
    public void valueChanged(ValueChangeEvent e) {
        Object newValue = e.getNewValue();
        //message = "Value changed: " + newValue;
        //log.info(message);
        
        if (newValue == null) {
            return;
        }
        
        
        if (editAction != EditAction.none) {
            setEditable(false);
        }
        
        room = (Room) e.getNewValue();
        
    }

    public boolean getEditable() {
        return editable;
    }

    public void setEditable(boolean editable) {
        this.editable = editable;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        log.debugf("setRoom: %s", room);
        
        // Don't overwrite this from the listbox if we're editing.
        if (room != null && !editable) {
            this.room = room;
        }
    }
}

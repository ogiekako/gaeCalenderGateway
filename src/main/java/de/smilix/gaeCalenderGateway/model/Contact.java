package de.smilix.gaeCalenderGateway.model;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * A help class to save contacts. It's the intention that you use the {@link #serialize()}/{@link #desrialize(String)} functions to save contacts transparently in an array.  
 * 
 * @author holger
 */
public class Contact implements Serializable {

  private String name;
  private String email;

  public Contact() {
    // for jackson
  }

  public Contact(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public String getEmail() {
    return email;
  }

  public String serialize() {
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.writeValueAsString(this);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException(e);
    }
  }

  public static Contact desrialize(String data) {
    // handle old data
    if (!data.startsWith("{")) {
      return new Contact(data, "");
    }
    ObjectMapper mapper = new ObjectMapper();
    try {
      return mapper.readValue(data, Contact.class);
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }
  
  public static List<Contact> convertStringList(List<String> stringList) {
    ArrayList<Contact> list = new ArrayList<>(stringList.size());
    for (String data : stringList) {
      list.add(Contact.desrialize(data));
    }
    return Collections.unmodifiableList(list);
  }

  public static List<String> convertContactList(List<Contact> contactList) {
    ArrayList<String> list = new ArrayList<>(contactList.size());
    for (Contact contact : contactList) {
      list.add(contact.serialize());
    }
    return list;
  }

  @Override
  public String toString() {
    return String.format("Contact [name=%s, email=%s]", name, email);
  }
}

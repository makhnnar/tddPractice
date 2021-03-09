package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;

import java.util.List;

public interface Listener {

    void onContactItemsFetchedSuccess(List<Contact> contactList);

    void onContactItemsFetchedFailed();

    void onContactItemsFetchedNetworkError();
}

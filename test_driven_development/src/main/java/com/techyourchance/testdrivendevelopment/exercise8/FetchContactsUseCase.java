package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;

import java.util.ArrayList;
import java.util.List;

public class FetchContactsUseCase {

    private final GetContactsHttpEndpoint getContactsHttpEndpoint;

    private List<Listener> listeners = new ArrayList<>();

    public FetchContactsUseCase(GetContactsHttpEndpoint getContactsHttpEndpoint) {
        this.getContactsHttpEndpoint = getContactsHttpEndpoint;
    }

    public void fetchContactsAndNotify(String param) {
        getContactsHttpEndpoint.getContacts(param, new Callback() {
            @Override
            public void onGetContactsSucceeded(List<ContactSchema> cartItems) {
                notifySucceeded(cartItems);
            }

            @Override
            public void onGetContactsFailed(GetContactsHttpEndpoint.FailReason failReason) {
                switch (failReason){
                    case NETWORK_ERROR:
                        notifiedNetError();
                    case GENERAL_ERROR:
                        notifiedFailure();
                        break;
                }
            }
        });
    }

    private void notifiedNetError() {
        for (Listener listener : listeners) {
            listener.onContactItemsFetchedNetworkError();
        }
    }

    private void notifiedFailure() {
        for (Listener listener : listeners) {
            listener.onContactItemsFetchedFailed();
        }
    }

    private void notifySucceeded(List<ContactSchema> contactSchemas) {
        for (Listener listener : listeners) {
            listener.onContactItemsFetchedSuccess(
                    contacItemsFromSchemas(contactSchemas)
            );
        }
    }

    private List<Contact> contacItemsFromSchemas(List<ContactSchema> contactSchemas) {
        List<Contact> contacts = new ArrayList<>();
        for (ContactSchema schema : contactSchemas) {
            contacts.add(new Contact(
                    schema.getId(),
                    schema.getFullName(),
                    schema.getImageUrl()
            ));
        }
        return contacts;
    }

    public void registerListener(Listener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(Listener listener) {
        listeners.remove(listener);
    }
}

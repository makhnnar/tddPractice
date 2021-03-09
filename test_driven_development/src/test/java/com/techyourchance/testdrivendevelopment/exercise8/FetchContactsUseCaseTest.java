package com.techyourchance.testdrivendevelopment.exercise8;

import com.techyourchance.testdrivendevelopment.exercise8.contacts.Contact;
import com.techyourchance.testdrivendevelopment.exercise8.networking.ContactSchema;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.Callback;
import com.techyourchance.testdrivendevelopment.exercise8.networking.GetContactsHttpEndpoint.FailReason;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@RunWith(MockitoJUnitRunner.class)
public class FetchContactsUseCaseTest {

    public static final String FILTERTERM = "filterTerm";
    public static final String ID = "id";
    public static final String FULL_NAME = "fullName";
    public static final String FULL_PHONE_NUMBER = "fullPhoneNumber";
    public static final String IMAGE_URL = "imageUrl";
    public static final int AGE = 18;

    FetchContactsUseCase SUT;

    @Mock
    GetContactsHttpEndpoint getContactsHttpEndpointMock;

    @Captor ArgumentCaptor<List<Contact>> acListContactSchema;

    @Mock Listener listenerMock1;
    @Mock Listener listenerMock2;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchContactsUseCase(
                getContactsHttpEndpointMock
        );
        success();
    }

    //correctTermPassedToEndpoint

    @Test
    public void fetchContacts_correctTermPassedToEndpoint() throws Exception {
        ArgumentCaptor<String> acStr = ArgumentCaptor.forClass(String.class);
        SUT.fetchContactsAndNotify(FILTERTERM);
        verify(getContactsHttpEndpointMock).getContacts(
                acStr.capture(),
                any(Callback.class)
        );
        assertThat(acStr.getValue(),is(FILTERTERM));
    }

    //observersNotifiedWithCorrectData
    @Test
    public void fetchContacts_success_observersNotifiedWithCorrectData() throws Exception {
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchContactsAndNotify(FILTERTERM);
        verify(listenerMock1).onContactItemsFetchedSuccess(
                acListContactSchema.capture()
        );
        verify(listenerMock2).onContactItemsFetchedSuccess(
                acListContactSchema.capture()
        );
        List<List<Contact>> capture =  acListContactSchema.getAllValues();
        assertThat(capture.get(0),is(getContactItems()));
        assertThat(capture.get(1),is(getContactItems()));
    }

    //unsubscribedObserversNotNotified
    @Test
    public void fetchContacts_success_unsubscribedObserversNotNotified() throws Exception {
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.unregisterListener(listenerMock2);
        SUT.fetchContactsAndNotify(FILTERTERM);
        verify(listenerMock1)
                .onContactItemsFetchedSuccess(
                        any(List.class)
                );
        verifyNoMoreInteractions(listenerMock2);
    }
    //no match term observersNotifiedOfFailure

    //general error observersNotifiedOfFailure
    @Test
    public void fetchContacts_generalError_observersNotifiedOfFailure() throws Exception {
        generaError();
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchContactsAndNotify(FILTERTERM);
        verify(listenerMock1).onContactItemsFetchedFailed();
        verify(listenerMock2).onContactItemsFetchedFailed();
    }

    @Test
    public void fetchContacts_networkError_observersNotifiedOfFailure() throws Exception {
        networkError();
        SUT.registerListener(listenerMock1);
        SUT.registerListener(listenerMock2);
        SUT.fetchContactsAndNotify(FILTERTERM);
        verify(listenerMock1).onContactItemsFetchedNetworkError();
        verify(listenerMock2).onContactItemsFetchedNetworkError();
    }

    private void networkError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.NETWORK_ERROR);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(
                anyString(),
                any(Callback.class)
        );
    }

    private void generaError() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsFailed(FailReason.GENERAL_ERROR);
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(
                anyString(),
                any(Callback.class)
        );
    }
    //

    private void success() {
        doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                Callback callback = (Callback) args[1];
                callback.onGetContactsSucceeded(getContactItemSchemes());
                return null;
            }
        }).when(getContactsHttpEndpointMock).getContacts(
                anyString(),
                any(Callback.class)
        );
    }

    private List<Contact> getContactItems() {
        List<Contact> contactItems = new ArrayList<>();
        contactItems.add(
                new Contact(
                        ID,
                        FULL_NAME,
                        IMAGE_URL
                )
        );
        return contactItems;
    }

    private List<ContactSchema> getContactItemSchemes() {
        List<ContactSchema> contactItems = new ArrayList<>();
        contactItems.add(
                new ContactSchema(
                        ID,
                        FULL_NAME,
                        FULL_PHONE_NUMBER,
                        IMAGE_URL,
                        AGE
                )
        );
        return contactItems;
    }

}
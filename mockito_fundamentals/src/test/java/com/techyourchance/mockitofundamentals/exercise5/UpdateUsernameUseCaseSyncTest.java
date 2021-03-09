package com.techyourchance.mockitofundamentals.exercise5;

import com.techyourchance.mockitofundamentals.exercise5.eventbus.UserDetailsChangedEvent;
import com.techyourchance.mockitofundamentals.exercise5.networking.NetworkErrorException;
import com.techyourchance.mockitofundamentals.exercise5.eventbus.EventBusPoster;
import com.techyourchance.mockitofundamentals.exercise5.networking.UpdateUsernameHttpEndpointSync;
import com.techyourchance.mockitofundamentals.exercise5.users.User;
import com.techyourchance.mockitofundamentals.exercise5.users.UsersCache;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

public class UpdateUsernameUseCaseSyncTest {

    public static final String USERID = "userid";
    public static final String USERNAME = "username";

    UpdateUsernameUseCaseSync SUT;

    UpdateUsernameHttpEndpointSync updateUsernameHttpEndpointMock;
    UsersCache usersCacheMock;
    EventBusPoster eventBusPosterMock;

    @Before
    public void setup() throws Exception{
        updateUsernameHttpEndpointMock = mock(UpdateUsernameHttpEndpointSync.class);
        usersCacheMock = mock(UsersCache.class);
        eventBusPosterMock = mock(EventBusPoster.class);
        SUT = new UpdateUsernameUseCaseSync(
                updateUsernameHttpEndpointMock,
                usersCacheMock,
                eventBusPosterMock
        );
        success();
    }

    @Test
    public void usernameSync_success_usernameAndUserIdPassedToEndpoint() throws Exception {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.updateUsernameSync(USERID,USERNAME);
        verify(updateUsernameHttpEndpointMock,times(1)).updateUsername(ac.capture(), ac.capture());
        List<String> captures = ac.getAllValues();
        assertThat(captures.get(0), is(USERID));
        assertThat(captures.get(1), is(USERNAME));
    }

    @Test
    public void usernameSync_success_userCached() throws Exception {
        ArgumentCaptor<User> ac = ArgumentCaptor.forClass(User.class);
        SUT.updateUsernameSync(USERID, USERNAME);
        verify(usersCacheMock).cacheUser(ac.capture());
        User cachedUser = ac.getValue();
        assertThat(cachedUser.getUserId(), is(USERID));
        assertThat(cachedUser.getUsername(), is(USERNAME));
    }

    @Test
    public void usernameSync_generalError_userNotCached() throws Exception {
        generalError();
        SUT.updateUsernameSync(USERID,USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void usernameSync_authError_userNotCached() throws Exception {
        authError();
        SUT.updateUsernameSync(USERID,USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }
    @Test
    public void usernameSync_serverlError_userNotCached() throws Exception {
        serverError();
        SUT.updateUsernameSync(USERID,USERNAME);
        verifyNoMoreInteractions(usersCacheMock);
    }

    @Test
    public void usernameSync_success_userDetailsInEventPosted() throws Exception {
        ArgumentCaptor<Object> ac = ArgumentCaptor.forClass(Object.class);
        SUT.updateUsernameSync(USERID,USERNAME);
        verify(eventBusPosterMock).postEvent(ac.capture());
        assertThat(ac.getValue(), is(instanceOf(UserDetailsChangedEvent.class)));
    }

    @Test
    public void usernameSync_generalError_noInteractionWithEventBusPoster() throws Exception {
        generalError();
        SUT.updateUsernameSync(USERID,USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void usernameSync_authError_noInteractionWithEventBusPoster() throws Exception {
        authError();
        SUT.updateUsernameSync(USERID,USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void usernameSync_serverError_noInteractionWithEventBusPoster() throws Exception {
        serverError();
        SUT.updateUsernameSync(USERID,USERNAME);
        verifyNoMoreInteractions(eventBusPosterMock);
    }

    @Test
    public void usernameSync_success_successReturned() throws Exception {
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USERID,USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.SUCCESS));
    }

    @Test
    public void usernameSync_serverError_failureReturned() throws Exception {
        serverError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USERID,USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void usernameSync_authError_failureReturned() throws Exception {
        authError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USERID,USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void usernameSync_generalError_failureReturned() throws Exception {
        generalError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USERID,USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.FAILURE));
    }

    @Test
    public void usernameSync_networkError_failureReturned() throws Exception {
        networkError();
        UpdateUsernameUseCaseSync.UseCaseResult result = SUT.updateUsernameSync(USERID,USERNAME);
        assertThat(result, is(UpdateUsernameUseCaseSync.UseCaseResult.NETWORK_ERROR));
    }

    private void success() throws NetworkErrorException {
        when(
                updateUsernameHttpEndpointMock.updateUsername(
                        any(String.class),
                        any(String.class)
                )
        ).thenReturn(
                new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.SUCCESS,
                        USERID,
                        USERNAME
                )
        );
    }

    private void networkError() throws Exception {
        doThrow(new NetworkErrorException())
                .when(updateUsernameHttpEndpointMock).updateUsername(
                        any(String.class),
                        any(String.class
                )
        );
    }

    private void generalError() throws Exception {
        when(
                updateUsernameHttpEndpointMock.updateUsername(
                        any(String.class),
                        any(String.class)
                )
        ).thenReturn(
                new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.GENERAL_ERROR,
                        "",
                        ""
                )
        );
    }

    private void authError() throws Exception {
        when(
                updateUsernameHttpEndpointMock.updateUsername(
                        any(String.class),
                        any(String.class)
                )
        ).thenReturn(
                new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.AUTH_ERROR,
                        "",
                        ""
                )
        );
    }

    private void serverError() throws Exception {
        when(
                updateUsernameHttpEndpointMock.updateUsername(
                        any(String.class),
                        any(String.class)
                )
        ).thenReturn(
                new UpdateUsernameHttpEndpointSync.EndpointResult(
                        UpdateUsernameHttpEndpointSync.EndpointResultStatus.SERVER_ERROR,
                        "",
                        ""
                )
        );
    }

}
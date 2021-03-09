package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus;
import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise6.FetchUserUseCaseSync.Status;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FectchUserUseCaseSyncImplTest {

    public final String IDUSER = "iduser";
    public final String USERNAME = "username";
    private final User USER = new User(IDUSER, USERNAME);

    @Mock FetchUserHttpEndpointSync fetchUserHttpEndpointSync;

    @Mock UsersCache usersCache;

    FetchUserUseCaseSync SUT;

    @Before
    public void setUp() throws Exception {
        SUT =  new FectchUserUseCaseSyncImpl(
                fetchUserHttpEndpointSync,
                usersCache
        );
        success();
    }

    //SUCCESS
    @Test
    public void fetchUserSync_correctParametersPassedToEndpoint() throws Exception  {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.fetchUserSync(IDUSER);
        verify(usersCache).getUser(ac.capture());
        assertThat(ac.getValue(),is(IDUSER));
    }

    @Test
    public void fetchUserSync_correctParametersPassedToCache() throws Exception  {
        ArgumentCaptor<String> ac = ArgumentCaptor.forClass(String.class);
        SUT.fetchUserSync(IDUSER);
        verify(fetchUserHttpEndpointSync).fetchUserSync(ac.capture());
        assertThat(ac.getValue(),is(IDUSER));
    }

    @Test
    public void fetchUserSync_notInCacheEndpointSuccess_correctUserReturned() throws Exception {
        // Arrange
        // Act
        UseCaseResult result = SUT.fetchUserSync(IDUSER);
        // Assert
        assertThat(result.getUser(), Is.is(USER));
    }

    //SUCCESS
    @Test
    public void fetchUserSync_success_successReturned() throws Exception  {
        UseCaseResult result = SUT.fetchUserSync(IDUSER);
        assertThat(result.getStatus(), is(Status.SUCCESS));
    }

    //AUTH_ERROR
    @Test
    public void fetchUserSync_authError_failureReturned() throws Exception  {
        authError();
        UseCaseResult result = SUT.fetchUserSync(IDUSER);
        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    //GENERAL_ERROR
    @Test
    public void fetchUserSync_generalError_failureReturned() throws Exception  {
        generalError();
        UseCaseResult result = SUT.fetchUserSync(IDUSER);
        assertThat(result.getStatus(), is(Status.FAILURE));
    }

    //NETWORK_ERROR
    @Test
    public void fetchUserSync_networkError_failureReturned() throws Exception  {
        networkError();
        UseCaseResult result = SUT.fetchUserSync(IDUSER);
        assertThat(result.getStatus(), is(Status.NETWORK_ERROR));
    }

    private void success() throws NetworkErrorException {
        when(
            fetchUserHttpEndpointSync.fetchUserSync(
                any(String.class)
            )
        ).thenReturn(
            new EndpointResult(
                EndpointStatus.SUCCESS,
                IDUSER,
                USERNAME
            )
        );
    }

    private void networkError() throws NetworkErrorException {
        when(
                fetchUserHttpEndpointSync.fetchUserSync(
                        any(String.class)
                )
        ).thenThrow(new NetworkErrorException());
    }

    private void generalError() throws NetworkErrorException {
        when(
                fetchUserHttpEndpointSync.fetchUserSync(
                        any(String.class)
                )
        ).thenReturn(
                new EndpointResult(
                        EndpointStatus.GENERAL_ERROR,
                        IDUSER,
                        USERNAME
                )
        );
    }

    private void authError() throws NetworkErrorException {
        when(
                fetchUserHttpEndpointSync.fetchUserSync(
                        any(String.class)
                )
        ).thenReturn(
                new EndpointResult(
                        EndpointStatus.AUTH_ERROR,
                        IDUSER,
                        USERNAME
                )
        );
    }

}
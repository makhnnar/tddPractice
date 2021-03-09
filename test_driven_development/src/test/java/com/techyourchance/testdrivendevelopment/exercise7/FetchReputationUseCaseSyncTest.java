package com.techyourchance.testdrivendevelopment.exercise7;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.OngoingStubbing;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.UseCaseResult;
import com.techyourchance.testdrivendevelopment.exercise7.FetchReputationUseCaseSync.ReputationResult;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointStatus;
import com.techyourchance.testdrivendevelopment.exercise7.networking.GetReputationHttpEndpointSync.EndpointResult;

@RunWith(MockitoJUnitRunner.class)
public class FetchReputationUseCaseSyncTest {

    FetchReputationUseCaseSync SUT;

    @Mock
    GetReputationHttpEndpointSync getReputationHttpEndpointSyncMock;

    private int REPUTATION = 4;

    @Before
    public void setUp() throws Exception {
        SUT = new FetchReputationUseCaseSync(
                getReputationHttpEndpointSyncMock
        );
        success();
    }

    @Test
    public void fetchReputationSync_success_successReturned() throws Exception {
        ReputationResult result = SUT.getReputationSync();
        assertThat(result.getStatus(),is(UseCaseResult.SUCCESS));
    }

    @Test
    public void fetchReputationSync_generalError_failuredReturned() throws Exception {
        generalError();
        ReputationResult result = SUT.getReputationSync();
        assertThat(result.getStatus(),is(UseCaseResult.FAILURE));
    }

    @Test
    public void fetchReputationSync_networkError_failuredReturned() throws Exception {
        networkError();
        ReputationResult result = SUT.getReputationSync();
        assertThat(result.getStatus(),is(UseCaseResult.FAILURE));
    }

    private void networkError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(
                        new EndpointResult(
                                EndpointStatus.NETWORK_ERROR,
                                0
                        )
                );
    }

    private void generalError() {
        when(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(
                        new EndpointResult(
                                EndpointStatus.GENERAL_ERROR,
                                0
                        )
                );
    }

    private void success() {
        when(getReputationHttpEndpointSyncMock.getReputationSync())
                .thenReturn(
                        new EndpointResult(
                                EndpointStatus.SUCCESS,
                                REPUTATION
                        )
                );
    }

}
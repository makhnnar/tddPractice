package com.techyourchance.testdrivendevelopment.exercise6;

import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointStatus;
import com.techyourchance.testdrivendevelopment.exercise6.networking.FetchUserHttpEndpointSync.EndpointResult;
import com.techyourchance.testdrivendevelopment.exercise6.networking.NetworkErrorException;
import com.techyourchance.testdrivendevelopment.exercise6.users.User;
import com.techyourchance.testdrivendevelopment.exercise6.users.UsersCache;

public class FectchUserUseCaseSyncImpl implements FetchUserUseCaseSync {

    private FetchUserHttpEndpointSync fetchUserHttpEndpointSync;
    private UsersCache usersCache;

    public FectchUserUseCaseSyncImpl(
            FetchUserHttpEndpointSync fetchUserHttpEndpointSync,
            UsersCache usersCache
    ) {
        this.fetchUserHttpEndpointSync = fetchUserHttpEndpointSync;
        this.usersCache = usersCache;
    }

    @Override
    public UseCaseResult fetchUserSync(String userId) {
        User user = usersCache.getUser(userId);
        if(user!=null){
            return new UseCaseResult(
                    Status.SUCCESS,
                    user
            );
        }
        EndpointResult result = null;
        try{
            result = fetchUserHttpEndpointSync.fetchUserSync(userId);
        }catch (NetworkErrorException e){
            return new UseCaseResult(
                    Status.NETWORK_ERROR,
                    null
            );
        }
        if(result.getStatus()== EndpointStatus.SUCCESS) {
            user = new User(
                    result.getUserId(),
                    result.getUsername()
            );
            usersCache.cacheUser(
                    user
            );
            return new UseCaseResult(
                    Status.SUCCESS,
                    user
            );
        }
        return new UseCaseResult(
                Status.FAILURE,
                null
        );
    }
}

package com.techyourchance.testdoublesfundamentals.exercise4;

import com.techyourchance.testdoublesfundamentals.example4.networking.NetworkErrorException;
import com.techyourchance.testdoublesfundamentals.exercise4.networking.UserProfileHttpEndpointSync;
import com.techyourchance.testdoublesfundamentals.exercise4.users.User;
import com.techyourchance.testdoublesfundamentals.exercise4.users.UsersCache;

import org.jetbrains.annotations.Nullable;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

import com.techyourchance.testdoublesfundamentals.exercise4.FetchUserProfileUseCaseSync.UseCaseResult;

public class FetchUserProfileUseCaseSyncTest {

    public static final String USERID = "userid";
    public static final String FULLNAME = "fullname";
    public static final String IMGURL = "imgurl";
    public static final String NONFULLNAME = "nonfullname";
    public static final String NONIMGURL = "nonimgurl";
    public static final User USER = new User(
            USERID,
            FULLNAME,
            IMGURL
    );

    FetchUserProfileUseCaseSync SUT;
    UserProfileHttpEndpointSyncTd userProfileHttpEndpointSyncTd;
    UsersCacheTd usersCacheTd;

    @Before
    public void setup(){
        userProfileHttpEndpointSyncTd = new UserProfileHttpEndpointSyncTd();
        usersCacheTd = new UsersCacheTd();
        SUT = new FetchUserProfileUseCaseSync(
                userProfileHttpEndpointSyncTd,
                usersCacheTd
        );
    }

    //SUCCESS
    @Test
    public void fetchUserSync_success_useridPassedToEndpoint(){
        SUT.fetchUserProfileSync(USERID);
        assertThat(
                userProfileHttpEndpointSyncTd.mUserid,
                is(USERID)
        );
    }

    @Test
    public void fetchUserSync_success_cachedUser() throws Exception {
        SUT.fetchUserProfileSync(USERID);
        User cachedUser = usersCacheTd.getUser(USERID);
        assertNotNull(cachedUser);
        assertThat(cachedUser.getFullName(), is(FULLNAME));
        assertThat(cachedUser.getImageUrl(), is(IMGURL));
        assertThat(cachedUser.getUserId(), is(USERID));
    }

    @Test
    public void fetchUserSync_authError_nonCachedUser() throws Exception {
        userProfileHttpEndpointSyncTd.mIsAuthError = true;
        SUT.fetchUserProfileSync(USERID);
        User cachedUser = usersCacheTd.getUser(USERID);
        assertNull(cachedUser);
    }

    @Test
    public void fetchUserSync_serverError_nonCachedUser() throws Exception {
        userProfileHttpEndpointSyncTd.mIsServerError = true;
        SUT.fetchUserProfileSync(USERID);
        User cachedUser = usersCacheTd.getUser(USERID);
        assertNull(cachedUser);
    }

    @Test
    public void fetchUserSync_generalError_nonCachedUser() throws Exception {
        userProfileHttpEndpointSyncTd.mIsGeneralError = true;
        SUT.fetchUserProfileSync(USERID);
        User cachedUser = usersCacheTd.getUser(USERID);
        assertNull(cachedUser);
    }

    @Test
    public void fetchUserSync_networkError_nonCachedUser() throws Exception {
        userProfileHttpEndpointSyncTd.mIsNetworkError = true;
        SUT.fetchUserProfileSync(USERID);
        User cachedUser = usersCacheTd.getUser(USERID);
        assertNull(cachedUser);
    }

    @Test
    public void cachedUser_saveUser_retrieveUserSuccess() throws Exception {
        usersCacheTd.cacheUser(USER);
        assertThat(
                usersCacheTd.getUser(USERID),
                is(USER)
        );
    }

    //AUTH_SUCCESS
    @Test
    public void fetchUserSync_success_successReturned(){
        UseCaseResult result = SUT.fetchUserProfileSync(USERID);
        assertThat(
                result,
                is(UseCaseResult.SUCCESS)
        );
    }

    //AUTH_ERROR
    @Test
    public void fetchUserSync_authError_failureReturned(){
        userProfileHttpEndpointSyncTd.mIsAuthError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USERID);
        assertThat(
                result,
                is(UseCaseResult.FAILURE)
        );
    }

    //SERVER_ERROR
    @Test
    public void fetchUserSync_serverError_failureReturned(){
        userProfileHttpEndpointSyncTd.mIsServerError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USERID);
        assertThat(
                result,
                is(UseCaseResult.FAILURE)
        );
    }
    //GENERAL_ERROR
    public void fetchUserSync_generalError_failureReturned(){
        userProfileHttpEndpointSyncTd.mIsGeneralError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USERID);
        assertThat(
                result,
                is(UseCaseResult.FAILURE)
        );
    }
    //NETWORK_ERROR
    public void fetchUserSync_networkError_failureReturned(){
        userProfileHttpEndpointSyncTd.mIsNetworkError = true;
        UseCaseResult result = SUT.fetchUserProfileSync(USERID);
        assertThat(
                result,
                is(UseCaseResult.NETWORK_ERROR)
        );
    }


    private static class UserProfileHttpEndpointSyncTd implements UserProfileHttpEndpointSync {

        public String mUserid;
        public boolean mIsGeneralError;
        public boolean mIsAuthError;
        public boolean mIsServerError;
        public boolean mIsNetworkError;

        @Override
        public EndpointResult getUserProfile(String userId) throws NetworkErrorException {
            mUserid = userId;
            if (mIsGeneralError) {
                return new EndpointResult(
                        EndpointResultStatus.GENERAL_ERROR,
                        "",
                        "",
                        ""
                );
            } else if (mIsAuthError) {
                return new EndpointResult(
                        EndpointResultStatus.AUTH_ERROR,
                        userId,
                        NONFULLNAME,
                        NONIMGURL
                );
            }  else if (mIsServerError) {
                return new EndpointResult(
                        EndpointResultStatus.SERVER_ERROR,
                        "",
                        "",
                        ""
                );
            } else if (mIsNetworkError) {
                throw new NetworkErrorException();
            } else {
                return new EndpointResult(
                        EndpointResultStatus.SUCCESS,
                        userId,
                        FULLNAME,
                        IMGURL
                );
            }
        }

    }

    private static class UsersCacheTd implements UsersCache{

        User mUser = null;

        @Override
        public void cacheUser(User user) {
            mUser = user;
        }

        @Nullable
        @Override
        public User getUser(String userId) {
            if(
                    mUser!=null&&
                    mUser.getUserId().equals(userId)
            ){
                return mUser;
            }
            return null;
        }
    }

}
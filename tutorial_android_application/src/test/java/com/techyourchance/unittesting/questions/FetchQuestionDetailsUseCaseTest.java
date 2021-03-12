package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.common.time.TimeProvider;
import com.techyourchance.unittesting.networking.StackoverflowApi;
import com.techyourchance.unittesting.networking.questions.FetchQuestionDetailsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;
import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase.Listener;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.mockito.ArgumentCaptor.*;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class FetchQuestionDetailsUseCaseTest {

    public static final String QUESTION_ID = "questionId";
    public static final String TITLE = "title";
    public static final String BODY = "body";

    FetchQuestionDetailsUseCase SUT;

    FetchQuestionDetailsEndpointTD fetchQuestionDetailsEndpointTD;

    TimeProvider timeProviderTD;

    @Mock Listener listener1;
    @Mock Listener listener2;

    @Before
    public void setUp() throws Exception {
        fetchQuestionDetailsEndpointTD =  new FetchQuestionDetailsEndpointTD();
        timeProviderTD = new TimeProvider();
        SUT = new FetchQuestionDetailsUseCase(
                fetchQuestionDetailsEndpointTD,
                timeProviderTD
        );
    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_idQuestionPassedToEndpoint() throws Exception {
        ArgumentCaptor<String> qIdCaptor = ArgumentCaptor.forClass(String.class);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        assertThat(
                fetchQuestionDetailsEndpointTD.questionId,
                is(QUESTION_ID)
        );
    }

    @Test
    public void fetchQuestionDetailsAndNotify_success_listenersNotifiedWithCorrectData() throws Exception {
        ArgumentCaptor<QuestionDetails> aQDetailCaptor = ArgumentCaptor.forClass(QuestionDetails.class);
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        verify(listener1).onQuestionDetailsFetched(
                aQDetailCaptor.capture()
        );
        verify(listener2).onQuestionDetailsFetched(
                aQDetailCaptor.capture()
        );
        QuestionDetails qDetails1 = aQDetailCaptor.getAllValues().get(0);
        QuestionDetails qDetails2 = aQDetailCaptor.getAllValues().get(1);
        assertThat(qDetails1,is(getExpectedQDetails()));
        assertThat(qDetails2,is(getExpectedQDetails()));
    }

    @Test
    public void fetchQuestionDetailsAndNotify_failed_listenersNotifiedOfFailure() throws Exception {
        failure();
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchQuestionDetailsAndNotify(QUESTION_ID);
        verify(listener1).onQuestionDetailsFetchFailed();
        verify(listener2).onQuestionDetailsFetchFailed();
    }

    private void failure() {
        fetchQuestionDetailsEndpointTD.isAFailure = true;
    }

    private QuestionDetails getExpectedQDetails() {
        return new QuestionDetails(
                QUESTION_ID,
                TITLE,
                BODY
        );
    }

    private static class FetchQuestionDetailsEndpointTD extends FetchQuestionDetailsEndpoint{

        public String questionId;
        public boolean isAFailure;

        public FetchQuestionDetailsEndpointTD() {
            super(null);
        }

        @Override
        public void fetchQuestionDetails(
                String questionId,
                Listener listener
        ) {
            this.questionId = questionId;
            if(isAFailure){
                listener.onQuestionDetailsFetchFailed();
            }else{
                listener.onQuestionDetailsFetched(
                        new QuestionSchema(
                                TITLE,
                                questionId,
                                BODY
                        )
                );
            }
        }
    }

}
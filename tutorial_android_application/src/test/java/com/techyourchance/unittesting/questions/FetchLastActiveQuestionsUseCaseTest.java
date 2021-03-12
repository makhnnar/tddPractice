package com.techyourchance.unittesting.questions;

import com.techyourchance.unittesting.networking.StackoverflowApi;
import com.techyourchance.unittesting.networking.questions.FetchLastActiveQuestionsEndpoint;
import com.techyourchance.unittesting.networking.questions.QuestionSchema;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
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
public class FetchLastActiveQuestionsUseCaseTest {

    FetchLastActiveQuestionsUseCase SUT;

    EndpointTd endpointTd;

    @Mock FetchLastActiveQuestionsUseCase.Listener listener1;
    @Mock FetchLastActiveQuestionsUseCase.Listener listener2;

    @Captor ArgumentCaptor<List<Question>> aQuestionsCaptor;

    @Before
    public void setUp() throws Exception {
        endpointTd =  new EndpointTd();
        SUT = new FetchLastActiveQuestionsUseCase(endpointTd);
    }

    @Test
    public void fetchLastActiveQuestionsAndNotify_success_listenersNotifiedWithCorrectData() throws Exception {
        success();
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchLastActiveQuestionsAndNotify();
        verify(listener1).onLastActiveQuestionsFetched(
                aQuestionsCaptor.capture()
        );
        verify(listener2).onLastActiveQuestionsFetched(
                aQuestionsCaptor.capture()
        );
        List<Question> qList1 = aQuestionsCaptor.getAllValues().get(0);
        List<Question> qList2 = aQuestionsCaptor.getAllValues().get(1);
        assertThat(qList1,is(getExpectedQuestions()));
        assertThat(qList2,is(getExpectedQuestions()));
    }

    @Test
    public void fetchLastActiveQuestionsAndNotify_failure_listenersNotifiedOfFailure() throws Exception {
        failure();
        SUT.registerListener(listener1);
        SUT.registerListener(listener2);
        SUT.fetchLastActiveQuestionsAndNotify();
        verify(listener1).onLastActiveQuestionsFetchFailed();
        verify(listener2).onLastActiveQuestionsFetchFailed();
    }

    private void failure() {
        endpointTd.failure = true;
    }

    private List<Question> getExpectedQuestions() {
        List<Question> questions = new LinkedList<>();
        questions.add(new Question("id1","title1"));
        questions.add(new Question("id2","title2"));
        return questions;
    }

    private void success() {
        //no - op
    }

    private static class EndpointTd extends FetchLastActiveQuestionsEndpoint {

        public boolean failure;

        public EndpointTd() {
            super(null);
        }

        @Override
        public void fetchLastActiveQuestions(Listener listener) {
            if(failure){
                listener.onQuestionsFetchFailed();
            }else{
                List<QuestionSchema> questionSchemas = new LinkedList<>();
                questionSchemas.add(new QuestionSchema("title1","id1","body1"));
                questionSchemas.add(new QuestionSchema("title2","id2","body2"));
                listener.onQuestionsFetched(questionSchemas);
            }

        }
    }

}
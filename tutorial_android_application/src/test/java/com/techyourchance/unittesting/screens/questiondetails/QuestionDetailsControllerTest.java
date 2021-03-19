package com.techyourchance.unittesting.screens.questiondetails;

import com.techyourchance.unittesting.questions.FetchQuestionDetailsUseCase;
import com.techyourchance.unittesting.questions.QuestionDetails;
import com.techyourchance.unittesting.screens.common.screensnavigator.ScreensNavigator;
import com.techyourchance.unittesting.screens.common.toastshelper.ToastsHelper;
import com.techyourchance.unittesting.testdata.QuestionDetailsTestData;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class QuestionDetailsControllerTest {

    public static final QuestionDetails QUESTION_DETAILS_1 = QuestionDetailsTestData.getQuestionDetails1();
    public static final String QUESTION_ID = "questionId";

    QuestionDetailsController SUT;

    private FetchQuestionDetailsUseCaseTD fetchQuestionDetailUseCaseTD;

    @Mock ScreensNavigator screenNavigatorMock;
    @Mock ToastsHelper toastHelperMock;
    @Mock QuestionDetailsViewMvc questionsDetailsViewMvcMock;

    @Before
    public void setUp() throws Exception {
        fetchQuestionDetailUseCaseTD = new FetchQuestionDetailsUseCaseTD();
        SUT = new QuestionDetailsController(
                fetchQuestionDetailUseCaseTD,
                screenNavigatorMock,
                toastHelperMock
        );
        SUT.bindView(questionsDetailsViewMvcMock);
        SUT.bindQuestionId(QUESTION_ID);
    }

    //showprogress
    @Test
    public void onStart_progressIndicationShown() throws Exception {
        SUT.onStart();
        verify(questionsDetailsViewMvcMock).showProgressIndication();
    }

    //hideprogress
    @Test
    public void onStart_successfulResponse_progressIndicationHidden() throws Exception {
        success();
        SUT.onStart();
        verify(questionsDetailsViewMvcMock).hideProgressIndication();
    }

    //onStart_listenersRegistered
    @Test
    public void onStart_listenersRegistered() throws Exception {
        SUT.onStart();
        verify(questionsDetailsViewMvcMock).registerListener(SUT);
        fetchQuestionDetailUseCaseTD.verifyListenerRegistered(SUT);
    }


    //fetchQuestionDetailsAndNotify_succesPassedParams
    @Test
    public void fetchQuestionDetailsAndNotify_succesPassedParams() throws Exception {
        SUT.onStart();
        assertThat(
                fetchQuestionDetailUseCaseTD.idQuestion,
                is(QUESTION_ID)
        );
    }


    //onStart_successfulResponse_questionDetailsBoundToView
    @Test
    public void onStart_successfulResponse_questionDetailsBoundToView() throws Exception {
        ArgumentCaptor<QuestionDetails> ac =  ArgumentCaptor.forClass(QuestionDetails.class);
        SUT.onStart();
        verify(questionsDetailsViewMvcMock).bindQuestion(ac.capture());
        assertThat(ac.getValue(),is(QUESTION_DETAILS_1));
    }

    //onNavigateUpClicked_navigatedToNavigateUp
    @Test
    public void onNavigateUpClicked_navigatedToNavigateUp() throws Exception {
        SUT.onNavigateUpClicked();
        verify(screenNavigatorMock).navigateUp();
    }

    //onStart_failure_errorToastShown
    @Test
    public void onStart_failure_errorToastShown() throws Exception {
        failure();
        SUT.onStart();
        verify(toastHelperMock).showUseCaseError();
    }

    //onStart_failure_questionDetailsNotBoundToView
    @Test
    public void onStart_failure_questionDetailsNotBoundToView() throws Exception {
        failure();
        SUT.onStart();
        verify(questionsDetailsViewMvcMock,never()).bindQuestion(
                any(QuestionDetails.class)
        );
    }

    //onStop_listenersUnregistered
    @Test
    public void onStop_listenersUnregistered() throws Exception {
        SUT.onStop();
        verify(questionsDetailsViewMvcMock).unregisterListener(SUT);
        fetchQuestionDetailUseCaseTD.unregisterListener(SUT);
    }

    private void failure() {
        fetchQuestionDetailUseCaseTD.failure = true;
    }

    private void success() {
        //no-op
    }

    public static class FetchQuestionDetailsUseCaseTD 
            extends FetchQuestionDetailsUseCase{

        public String idQuestion;
        public boolean failure = false;

        public FetchQuestionDetailsUseCaseTD() {
            super(null, null);
        }

        @Override
        public void fetchQuestionDetailsAndNotify(
                String questionId
        ) {
            this.idQuestion = questionId;
            if(!failure){
                for(FetchQuestionDetailsUseCase.Listener listener : getListeners()){
                    listener.onQuestionDetailsFetched(
                            QUESTION_DETAILS_1
                    );
                }
            }else{
                for(FetchQuestionDetailsUseCase.Listener listener : getListeners()){
                    listener.onQuestionDetailsFetchFailed();
                }
            }

        }

        public void verifyListenerRegistered(QuestionDetailsController questionDetailsController) {
            for (FetchQuestionDetailsUseCase.Listener listener : getListeners()) {
                if (listener == questionDetailsController) {
                    return;
                }
            }
            throw new RuntimeException("listener not registered");
        }
    }

}
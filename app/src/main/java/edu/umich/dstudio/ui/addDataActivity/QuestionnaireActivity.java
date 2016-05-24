package edu.umich.dstudio.ui.addDataActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import edu.umich.dstudio.R;
import edu.umich.dstudio.model.QuestionAnswer;
import edu.umich.dstudio.prompt.PromptConfig;
import edu.umich.dstudio.ui.BaseActivity;
import edu.umich.dstudio.utils.Utils;

/**
 * Created by shriti on 4/1/16.
 */
public class QuestionnaireActivity extends BaseActivity{

    private List<QuestionAnswer> questionAnswerList = new ArrayList<QuestionAnswer>();
    private HashMap<TextView, EditText> questionAnswerMap = new HashMap<TextView, EditText>();

    private TextView firstQuestion;
    private TextView secondQuestion;
    private EditText firstQuestionData;
    private EditText secondQuestionData;
    private ImageView acceptButton;
    private ImageView rejectButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_questionnaire);

        List<String> questionList;

       if (getIntent().getExtras().getString("PROMPT_TYPE")!=null) {
           PromptConfig.Type promptType = (PromptConfig.Type.valueOf(getIntent().getExtras().getString("PROMPT_TYPE")));
           questionList = Utils.getQuestionList(promptType);
        }
        else{
           questionList = Utils.getQuestionList(PromptConfig.Type.valueOf(""));
       }
        if(questionList.size() == 1) {
            firstQuestion = (TextView) findViewById(R.id.first_question);
            firstQuestion.setText(questionList.get(0));
            firstQuestionData = (EditText) findViewById(R.id.first_question_data);
            questionAnswerMap.put(firstQuestion, firstQuestionData);
        }
        else{
            firstQuestion = (TextView) findViewById(R.id.first_question);
            firstQuestion.setText(questionList.get(0));
            firstQuestionData = (EditText) findViewById(R.id.first_question_data);
            questionAnswerMap.put(firstQuestion, firstQuestionData);
            secondQuestion = (TextView) findViewById(R.id.second_question);
            secondQuestion.setText(questionList.get(1));
            secondQuestionData = (EditText) findViewById(R.id.second_question_data);
            questionAnswerMap.put(secondQuestion, secondQuestionData);
        }

        // Add click listeners for buttons
        acceptButton = (ImageView) findViewById(R.id.acceptButton);
        rejectButton = (ImageView) findViewById(R.id.rejectButton);

        acceptButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                acceptResults();
            }
        });
        rejectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rejectResults();
            }
        });

        if(getIntent().getExtras()!=null && getIntent().getExtras().getBoolean("FROM_NOTIFICATION")) {
            showToast("This screen was started from a notification.");
        }
    }

    /**
     * This is called when the user pressed "Tick" button on the screen.
     */
    public void acceptResults() {
        LatLng l = getCurrentLocation();
        Iterator it = questionAnswerMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();
            TextView question = (TextView) pair.getKey();
            EditText answer = (EditText) pair.getValue();
            String questionString = question.getText().toString();
            String answerString =  answer.getText().toString();
            // Make sure that there is some data entered by the user in the note field.
            if(answerString ==null || answerString.equals("") || answerString.trim().isEmpty()){
                showToast("Cannot add note without any content.");
                return;
            } else {
                QuestionAnswer QnA = new QuestionAnswer( questionString, answerString, (float) l.latitude, (float) l.longitude);
                questionAnswerList.add(QnA);
            }
            }
        mFirebaseWrapper.uploadQuestionAnswer(questionAnswerList);
        showToast("Your answer has been recorded");
        finish();
        }

    /**
     * This is called when the user presses the "X" button the screen.
     */
    public void rejectResults() {
        showToast("Going back to home screen");
        finish();
    }
}

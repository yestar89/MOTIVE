package com.example.motive_v1.Club.Tip;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.motive_v1.Club.Feed.FeedFragment;
import com.example.motive_v1.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class TipQuestion extends AppCompatActivity {


    RecyclerView recyclerView;
    EditText et_message;
    ImageView imv_send;

    ImageButton tip_imv_leftarrow;
    List<Chat> chatList;
    ChatAdapter chatAdapter;


    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
        OkHttpClient client = new OkHttpClient();






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tip_question);

        //연결시간 설정. 60초/120초/60초
        client = new OkHttpClient().newBuilder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(120, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .build();


        recyclerView = findViewById(R.id.tip_rv1);
        et_message = findViewById(R.id.et_message);
        imv_send = findViewById(R.id.imv_send);
        tip_imv_leftarrow = findViewById(R.id.tip_imv_leftarrow);


        // 돌아가는 왼쪽 이미지 버튼 눌렀을 때 페이지 돌아가기
        tip_imv_leftarrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


//        tip_imv_leftarrow = findViewById(R.id.tip_imv_leftarrow);
//        tip_imv_leftarrow.setOnClickListener(view -> {
//            Intent intent=new Intent(getApplicationContext(), TipFragment.class);
//            startActivity(intent);
//        });


//        TipFragment tipFragment = new TipFragment();
//        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
//        transaction.replace(R.id.content, tipFragment);
//        transaction.addToBackStack(null);
//        transaction.commit();





        // 메시지 초기화(사용자가 메시지를 보내면 메시지 목록이 비어있어야 하므로)
        chatList = new ArrayList<>();


        //RecyclerView
        chatAdapter = new ChatAdapter(chatList);
        recyclerView.setAdapter(chatAdapter);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);


        imv_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String question = et_message.getText().toString().trim();
                addToChat(question, Chat.SENT_BY_ME);
                et_message.setText("");
                callAPI(question);
            }
        });



    }



    void addToChat(String message, String sentBy) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                chatList.add(new Chat(message, sentBy));
                chatAdapter.notifyDataSetChanged();
                recyclerView.smoothScrollToPosition(chatAdapter.getItemCount());

                // 아이템이 추가되면 hint_text를 숨깁니다.
                findViewById(R.id.hint_text).setVisibility(View.GONE);
            }
        });
    }




    void addResponse(String response) {
        chatList.remove(chatList.size()-1);
        addToChat(response, Chat.SENT_BY_BOT);
    }





    // 답변을 기다리는 동안 "Typing.."으로 표시되도록 설정
    void callAPI(String question) {
        chatList.add(new Chat("Typing...", Chat.SENT_BY_BOT));
        JSONObject jsonObject = new JSONObject();




        //추가된 내용
        JSONArray arr = new JSONArray();
        JSONObject baseAi = new JSONObject();
        JSONObject userMsg = new JSONObject();
        try {
            //AI 속성설정
            baseAi.put("role", "user");
            baseAi.put("content", "You are a helpful and kind AI Assistant.");
            //유저 메세지
            userMsg.put("role", "user");
            userMsg.put("content", question);
            //array로 담아서 한번에 보낸다
            arr.put(baseAi);
            arr.put(userMsg);
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }

        JSONObject object = new JSONObject();
        try {
            //모델명 변경
            object.put("model", "gpt-3.5-turbo");
            object.put("messages", arr);
//            아래 put 내용은 삭제하면 된다
//            object.put("model", "text-davinci-003");
//            object.put("prompt", question);
//            object.put("max_tokens", 4000);
//            object.put("temperature", 0);

        } catch (JSONException e){
            e.printStackTrace();
        }


        // okhttp 요청 생성
        RequestBody body = RequestBody.create (object.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/chat/completions")
                .header("Authorization", " Bearer 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")
                .post(body)
                .build();



        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                addResponse("Fail to load response due to " + e.getMessage());
            }



            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {

                if(response.isSuccessful()) {
                    JSONArray jsonArray = null;
                    try {
                        JSONObject jsonObject= new JSONObject(response.body().string());
                        jsonArray = jsonObject.getJSONArray("choices");

                        String result = jsonArray.getJSONObject(0)
                                .getJSONObject("message")
                                .getString("content");
                        addResponse(result.trim());

                    } catch (JSONException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    addResponse("Failed to response due to " + response.body().string());
                }

            }
        });


    }
}

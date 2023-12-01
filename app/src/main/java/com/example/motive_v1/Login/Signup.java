package com.example.motive_v1.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.regex.Pattern;

import com.example.motive_v1.R;

public class Signup extends AppCompatActivity {
    private static final Pattern PASSWORD_PATTERN =
            Pattern.compile("^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[!@#$%^&]+)(?=\\S+$).{8,}$");

    TextInputEditText editTextEmail, editTextPassword, confirmPassword;
    Button buttonReg;
    FirebaseAuth mAuth;
    ProgressBar progressBar;
    private TextInputLayout etEmailLayout;
    private TextInputLayout etPwLayout;
    private TextInputLayout etRepwLayout;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        editTextEmail = findViewById(R.id.et_email);
        editTextPassword = findViewById(R.id.et_pw);
        confirmPassword = findViewById(R.id.et_repw);
        buttonReg = findViewById(R.id.btn_reg);
        progressBar = findViewById(R.id.progressBar);
        etEmailLayout = findViewById(R.id.et_email_lay);
        etPwLayout = findViewById(R.id.et_pw_lay);
        etRepwLayout = findViewById(R.id.et_repw_lay);
        ImageButton backbtnsignup = findViewById(R.id.backbtnsignup);
        //뒤로가기 버튼
        backbtnsignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Signup.this, Signin.class); // 이동할 화면을 지정
                startActivity(intent); // 화면 전환
                finish(); // 현재 액티비티 종료
            }
        });






        buttonReg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                String et_email, et_pw, et_repw;
                et_email = editTextEmail.getText().toString().trim();
                et_pw = editTextPassword.getText().toString().trim();
                et_repw = confirmPassword.getText().toString().trim();

                if (!validateEmail() || !validatePassword() || !validateRepassword()) {
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                mAuth.createUserWithEmailAndPassword(et_email, et_pw)
                        .addOnCompleteListener(Signup.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    FirebaseUser currentUser = mAuth.getCurrentUser();
                                    String userId = currentUser.getUid();
                                    String email = currentUser.getEmail();

                                    DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("Users").child(userId);
                                    // User DB 정보 저장
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("userId", userId);
                                    hashMap.put("email", email);
                                    userRef.setValue(hashMap);

                                    // 회원가입 성공
                                    Toast.makeText(Signup.this, "회원가입에 성공했습니다.", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(getApplicationContext(), SetProfile.class);
                                    startActivity(intent);
                                    finish();
                                } else { // 회원가입 실패
                                    progressBar.setVisibility(View.GONE);
                                    Toast.makeText(Signup.this, "회원가입에 실패했습니다. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

            }
        });
    }



    //이메일
    private boolean validateEmail() {
        String emailInput = editTextEmail.getText().toString().trim();

        if (emailInput.isEmpty()) {
            etEmailLayout.setError("이메일을 입력하세요.");
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()) {
            etEmailLayout.setError("올바른 이메일 형식이 아닙니다.");
            return false;
        } else {
            etEmailLayout.setError(null);
            return true;
        }
    }

    //비밀번호
    private boolean validatePassword() {
        String passwordInput = editTextPassword.getText().toString().trim();

        if (passwordInput.isEmpty()) {
            etPwLayout.setError("비밀번호를 입력하세요.");
            return false;
        } else if (passwordInput.length() < 8) {
            etPwLayout.setError("비밀번호는 최소 8자 이상이어야 합니다.");
            return false;
        } else {
            etPwLayout.setError(null);
            return true;
        }
    }

    //비밀번호 재검사
    private boolean validateRepassword() {
        String repasswordInput = confirmPassword.getText().toString().trim();
        String passwordInput = editTextPassword.getText().toString().trim();

        if (repasswordInput.isEmpty()) {
            etRepwLayout.setError("비밀번호를 다시 입력하세요.");
            return false;
        } else if (!repasswordInput.equals(passwordInput)) {
            etRepwLayout.setError("비밀번호가 일치하지 않습니다.");
            return false;
        } else {
            etRepwLayout.setError(null);
            return true;
        }
    }
    private void updateRegisterButton() {
        if (validateEmail() && validatePassword() && validateRepassword()) {
            buttonReg.setBackgroundResource(R.color.FA4756); // 다음 버튼을 빨간색으로 변경
        } else {
            buttonReg.setBackgroundResource(R.color.grey); // 그렇지 않으면 회색으로 변경
        }
    }

}
package com.yunwltn98.postingapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.yunwltn98.postingapp.api.NetworkClient;
import com.yunwltn98.postingapp.api.UserApi;
import com.yunwltn98.postingapp.config.Config;
import com.yunwltn98.postingapp.model.User;
import com.yunwltn98.postingapp.model.UserRes;

import java.util.regex.Pattern;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

public class RegisterActivity extends AppCompatActivity {

    private ProgressDialog dialog;
    EditText editEmail;
    EditText editPassword;
    Button btnRegister;
    TextView txtLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        editEmail = findViewById(R.id.editEmail);
        editPassword = findViewById(R.id.editPassword);
        btnRegister = findViewById(R.id.btnLogin);
        txtLogin = findViewById(R.id.txtRegister);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 이메일 형식 체크
                String email = editEmail.getText().toString().trim();

                Pattern pattern = Patterns.EMAIL_ADDRESS;
                if (pattern.matcher(email).matches() == false) {
                    Toast.makeText(RegisterActivity.this, "이메일 형식이 올바르지 않습니다", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 비밀번호 체크
                String password = editPassword.getText().toString().trim();

                // 기획에 맞게 처리 비밀번호 길이 4~12자리만 허옹
                if (password.length() < 4 || password.length() > 12) {
                    Toast.makeText(RegisterActivity.this, "비밀번호 길이를 확인하세요", Toast.LENGTH_SHORT).show();
                    return;
                }

                // 회원가입 API를 호출

                // 1. 다이얼로그를 화면에 보여준다
                showProgress("회원가입 진행중...");

                Retrofit retrofit = NetworkClient.getRetrofitClient(RegisterActivity.this);
                UserApi api = retrofit.create(UserApi.class);
                User user = new User(email, password);
                Call<UserRes> call = api.register(user);
                call.enqueue(new Callback<UserRes>() {
                    @Override
                    public void onResponse(Call<UserRes> call, Response<UserRes> response) {
                        // 프로그래스 다이얼로그가 있으면 나타나지않게 해준다
                        dismissProgress();

                        // 서버에서 보낸 응답이 200 OK일때 처리하는 코드
                        if (response.isSuccessful()){
                            UserRes res = response.body();

                            // 억세스토큰은 api 호출할때마다 헤더에서 사용하므로
                            // 회원가입이나 로그인이 끝나면 파일로 꼭 저장해놔야한다
                            SharedPreferences sp = getApplication().getSharedPreferences(Config.PREFERENCE_NAME,MODE_PRIVATE);
                            SharedPreferences.Editor editor = sp.edit();
                            editor.putString(Config.ACCESS_TOKEN, res.getAccess_token());
                            editor.apply();

                            // 3. 데이터를 이상없이 처리하면 메인 액티비티를 화면에 나오게 한다
                            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();

                        } else {
                        }

                    }

                    @Override
                    public void onFailure(Call<UserRes> call, Throwable t) {
                        // 프로그래스 다이얼로그가 있으면 나타나지않게 해준다
                        dismissProgress();

                    }
                });
            }
        });

        txtLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                startActivity(intent);

                finish();
            }
        });
    }

    // 네트워크 로직처리시에 화면에 보여주는 함수
    void showProgress(String message) {
        dialog = new ProgressDialog(this);
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        dialog.setMessage(message);
        dialog.show();
    }

    // 로직처리가 끝나면 화면에서 사라지는 함수
    void dismissProgress() {
        dialog.dismiss();
    }
}
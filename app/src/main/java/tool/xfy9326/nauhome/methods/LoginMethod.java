package tool.xfy9326.nauhome.methods;

import android.util.Base64;

import androidx.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class LoginMethod {
    private static final String ERROR_URL_PARAM = "ErrorMsg=";
    private final OkHttpClient client;
    private final String ClientIP;

    public LoginMethod(String clientIP) {
        this.ClientIP = clientIP;
        OkHttpClient.Builder client_builder = new OkHttpClient.Builder();
        client_builder.connectTimeout(5, TimeUnit.SECONDS);
        client_builder.writeTimeout(2, TimeUnit.SECONDS);
        client_builder.readTimeout(2, TimeUnit.SECONDS);
        client = client_builder.build();
    }

    private static FormBody buildLoginForm(String id, String pw, String type) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("DDDDD", ",0," + id.trim() + "@" + type);
        builder.add("upass", pw.trim());
        return builder.build();
    }

    private static FormBody buildLoginOutForm(String id, String pw) {
        FormBody.Builder builder = new FormBody.Builder();
        builder.add("DDDDD", id.trim());
        builder.add("upass", pw.trim());
        return builder.build();
    }

    private static String buildURL(String ip, boolean isLogin) {
        return "http://10.255.252.20:801/eportal/" +
                "?c=ACSetting" +
                "&a=" + (isLogin ? "Login" : "Logout") +
                "&wlanuserip=" + ip;
    }

    public void login(String id, String pw, String type, OnRequestListener onRequestListener) {
        if (id != null && pw != null && type != null) {
            requestURL(buildURL(ClientIP, true), buildLoginForm(id, pw, type), onRequestListener);
        }
    }

    public void logout(String id, String pw, OnRequestListener onRequestListener) {
        if (id != null && pw != null) {
            requestURL(buildURL(ClientIP, false), buildLoginOutForm(id, pw), onRequestListener);
        }
    }

    private void requestURL(String url, FormBody formBody, final OnRequestListener onRequestListener) {
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(formBody);
        client.newCall(builder.build()).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                if (onRequestListener != null) {
                    onRequestListener.OnRequest(false, "Login Request Error!");
                }
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) {
                String url = response.request().url().toString();
                if (url.contains(ERROR_URL_PARAM)) {
                    if (onRequestListener != null) {
                        byte[] msg = Base64.decode(url.substring(url.indexOf(ERROR_URL_PARAM) + ERROR_URL_PARAM.length()), Base64.DEFAULT);
                        String error = new String(msg);
                        if ("512".equals(error)) {
                            error = "AC authentication failure";
                        }
                        onRequestListener.OnRequest(false, error);
                    }
                } else {
                    if (onRequestListener != null) {
                        onRequestListener.OnRequest(true, "");
                    }
                }
                response.close();
            }
        });
    }

    public interface OnRequestListener {
        void OnRequest(boolean isSuccess, String errorMsg);
    }
}

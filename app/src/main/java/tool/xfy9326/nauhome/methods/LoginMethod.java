package tool.xfy9326.nauhome.methods;

import android.util.Base64;
import android.util.Log;

import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class LoginMethod {
    private static final String ERROR_URL_PARAM = "ErrorMsg=";

    private static HashMap<String, String> buildLoginForm(String id, String pw, String type) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("DDDDD", ",0," + id.trim() + "@" + type);
        hashMap.put("upass", pw.trim());
        return hashMap;
    }

    private static HashMap<String, String> buildLoginOutForm(String id, String pw) {
        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("DDDDD", id.trim());
        hashMap.put("upass", pw.trim());
        return hashMap;
    }

    private static String buildURL(String ip, boolean isLogin) {
        return "http://10.255.252.20:801/eportal/" +
                "?c=ACSetting" +
                "&a=" + (isLogin ? "Login" : "Logout") +
                "&wlanuserip=" + ip;
    }

    private static String buildPostParam(HashMap<String, String> postForm) throws UnsupportedEncodingException {
        StringBuilder paramsString = new StringBuilder();
        for (Map.Entry<String, String> entry : postForm.entrySet()) {
            paramsString.append(entry.getKey());
            paramsString.append("=");
            paramsString.append(URLEncoder.encode(entry.getValue(), "UTF-8"));
            paramsString.append("&");
        }
        paramsString.deleteCharAt(paramsString.length() - 1);
        return paramsString.toString();
    }

    public static void login(final String ip, final String id, final String pw, final String type, final OnRequestListener onRequestListener) {
        if (id != null && pw != null && type != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("LoginMethod", "Login Thread Start");
                    requestURL(buildURL(ip, true), buildLoginForm(id, pw, type), onRequestListener);
                }
            }).start();
        }
    }

    public static void logout(final String ip, final String id, final String pw, final OnRequestListener onRequestListener) {
        if (id != null && pw != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    Log.d("LoginMethod", "Logout Thread Start");
                    requestURL(buildURL(ip, false), buildLoginOutForm(id, pw), onRequestListener);
                }
            }).start();
        }
    }

    private static void requestURL(String url, HashMap<String, String> postForm, final OnRequestListener onRequestListener) {
        HttpURLConnection connection = null;
        try {
            URL requestUrl = new URL(url);

            byte[] postParamEntity = buildPostParam(postForm).getBytes();

            connection = (HttpURLConnection) requestUrl.openConnection(Proxy.NO_PROXY);
            connection.setConnectTimeout(5 * 1000);
            connection.setReadTimeout(5 * 1000);
            connection.setInstanceFollowRedirects(true);
            connection.setUseCaches(false);

            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty("Content-Length", String.valueOf(postParamEntity.length));

            try (OutputStream outputStream = connection.getOutputStream()) {
                outputStream.write(postParamEntity);

                if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    String responseUrl = connection.getURL().toString();

                    if (responseUrl.contains(ERROR_URL_PARAM)) {
                        if (onRequestListener != null) {
                            byte[] msg = Base64.decode(responseUrl.substring(responseUrl.indexOf(ERROR_URL_PARAM) + ERROR_URL_PARAM.length()), Base64.DEFAULT);
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
                } else {
                    if (onRequestListener != null) {
                        onRequestListener.OnRequest(false, "Login Request Error!");
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (onRequestListener != null) {
                onRequestListener.OnRequest(false, "Login Request Error!");
            }
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public interface OnRequestListener {
        void OnRequest(boolean isSuccess, String errorMsg);
    }
}

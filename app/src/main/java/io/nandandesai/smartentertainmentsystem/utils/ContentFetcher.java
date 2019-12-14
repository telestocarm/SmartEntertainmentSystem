package io.nandandesai.smartentertainmentsystem.utils;


import androidx.annotation.Nullable;

import java.io.IOException;
import java.net.CookieManager;
import java.net.CookiePolicy;
import java.net.Proxy;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import okhttp3.Headers;
import okhttp3.Interceptor;
import okhttp3.JavaNetCookieJar;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.internal.http.RealResponseBody;
import okio.GzipSource;
import okio.Okio;

public class ContentFetcher {


    public static String getContentFromUrl(String url, @Nullable Proxy proxy) throws IOException {
        OkHttpClient client = getUnsafeOkHttpClient(proxy);
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();

        if(response.code()==200) {
            return response.body().string();
        }else{
            System.out.println(response);
            return null;
        }
    }

    public static String extractVideoQuality(String input){
        String regex="\\d{3,4}p";

        Pattern pattern=Pattern.compile(regex);
        Matcher matcher=pattern.matcher(input);
        String quality="unknown quality";
        if(matcher.find())
        {
            quality=matcher.group();
        }
        return quality;
    }

    public static String getInfoHash(String magnetLink){
        String infoHash="";
        try {
            //a poor way to do this but it works most of the times I guess
            infoHash = magnetLink.split(":")[3].split("&")[0];
        }catch (StringIndexOutOfBoundsException se){}
        catch (ArrayIndexOutOfBoundsException arrayOutBound){}

        return infoHash;
    }

    public static boolean linkIsWorking(String url, @Nullable Proxy proxy){
        try{
            System.out.println("Checking the URL if it's working: " + url);
            OkHttpClient client = getUnsafeOkHttpClient(proxy);
            Request request = new Request.Builder().headers(Headers.of(getHttpHeaders())).url(url).head().build();
            Response response = client.newCall(request).execute();
            int responseCode=response.code();
            System.out.println("Response code in linkIsWorking() for "+url+" is: "+responseCode);
            if ( responseCode == 200) {
                return true;
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    public static Map<String, String> getHttpHeaders(){
        System.out.println("Using Mozilla headers. Try to emulate browser as much as possible.");
        Map<String, String> headers=new HashMap<>();
        headers.put("User-Agent","Mozilla/5.0 (X11; Linux x86_64; rv:58.0) Gecko/20100101 Firefox/58.0");
        headers.put("Accept-Language","en-US,en;q=0.5");
        headers.put("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        headers.put("Accept-Encoding","gzip, deflate, br");
        headers.put("Accept-Charset","utf-8");
        headers.put("DNT","1");
        headers.put("Connection","keep-alive");
        headers.put("Upgrade-Insecure-Requests","1");

        return headers;
    }


    //The below code is taken from: https://gist.github.com/mefarazath/c9b588044d6bffd26aac3c520660bf40
    public static OkHttpClient getUnsafeOkHttpClient(Proxy proxy) {
        try {
            // Create a trust manager that does not validate certificate chains
            final TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        @Override
                        public void checkClientTrusted(X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public void checkServerTrusted(X509Certificate[] chain,
                                                       String authType) throws CertificateException {
                        }

                        @Override
                        public X509Certificate[] getAcceptedIssuers() {
                            return new X509Certificate[0];
                        }
                    }
            };

            // Install the all-trusting trust manager
            final SSLContext sslContext = SSLContext.getInstance("SSL");
            sslContext.init(null, trustAllCerts, new java.security.SecureRandom());
            // Create an ssl socket factory with our all-trusting manager
            final SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();

            CookieManager cookieManager = new CookieManager();
            cookieManager.setCookiePolicy(CookiePolicy.ACCEPT_ALL);

            if(proxy!=null){
                return new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .proxy(proxy)
                        .cookieJar(new JavaNetCookieJar(cookieManager))
                        .addInterceptor(new UnzippingInterceptor())
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Headers headers=new Headers.Builder().add("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:60.0) Gecko/20100101 Firefox/60.0")
                                        .add("Accept-Language","en-US,en;q=0.5")
                                        .add("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                                        .add("Accept-Charset","utf-8")
                                        .add("DNT","1")
                                        .add("Connection","keep-alive")
                                        .add("Host","yts.lt")
                                        .add("Accept-Encoding","gzip, deflate")
                                        .add("Upgrade-Insecure-Requests","1").build();
                                Request newRequest = chain.request().newBuilder()
                                        .headers(headers)
                                        .build();
                                return chain.proceed(newRequest);
                            }
                        })
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                        .hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        }).build();
            }else {

                return new OkHttpClient.Builder()
                        .connectTimeout(10, TimeUnit.SECONDS)
                        .writeTimeout(10, TimeUnit.SECONDS)
                        .readTimeout(30, TimeUnit.SECONDS)
                        .cookieJar(new JavaNetCookieJar(cookieManager))
                        .addInterceptor(new UnzippingInterceptor())
                        .addInterceptor(new Interceptor() {
                            @Override
                            public Response intercept(Chain chain) throws IOException {
                                Headers headers=new Headers.Builder().add("User-Agent","Mozilla/5.0 (Windows NT 6.1; rv:60.0) Gecko/20100101 Firefox/60.0")
                                        .add("Accept-Language","en-US,en;q=0.5")
                                        .add("Accept","text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                                        .add("Accept-Charset","utf-8")
                                        .add("DNT","1")
                                        .add("Connection","keep-alive")
                                        .add("Host","yts.lt")
                                        .add("Accept-Encoding","gzip, deflate")
                                        .add("Upgrade-Insecure-Requests","1").build();
                                Request newRequest = chain.request().newBuilder()
                                        .headers(headers)
                                        .build();
                                return chain.proceed(newRequest);
                            }
                        })
                        .sslSocketFactory(sslSocketFactory, (X509TrustManager) trustAllCerts[0])
                        .hostnameVerifier(new HostnameVerifier() {
                            @Override
                            public boolean verify(String hostname, SSLSession session) {
                                return true;
                            }
                        }).build();
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static class UnzippingInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());
            return unzip(response);

        }
            // copied from okhttp3.internal.http.HttpEngine (because is private)
            private Response unzip(final Response response) throws IOException
            {
                if (response.body() == null)
                {
                    return response;
                }

                //check if we have gzip response
                String contentEncoding = response.headers().get("Content-Encoding");

                //this is used to decompress gzipped responses
                if (contentEncoding != null && contentEncoding.equals("gzip"))
                {
                    Long contentLength = response.body().contentLength();
                    GzipSource responseBody = new GzipSource(response.body().source());
                    Headers strippedHeaders = response.headers().newBuilder().build();
                    return response.newBuilder().headers(strippedHeaders)
                            .body(new RealResponseBody(response.body().contentType().toString(), contentLength, Okio.buffer(responseBody)))
                            .build();
                }
                else
                {
                    return response;
                }
            }
        }


}

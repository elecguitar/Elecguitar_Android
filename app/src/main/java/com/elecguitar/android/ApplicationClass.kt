package com.elecguitar.android

import android.app.Application
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

class ApplicationClass : Application() {
    companion object{
//         유호 ip - 집
//         private const val IP = "192.168.0.2"
        // 유호 ip - 싸피
         private const val IP = "192.168.33.117"
        // 민하 ip - 싸피
//         private const val IP = "192.168.33.116"

        const val CHARGE_STATION_SERVICE_KEY = "fCAhTCQHRXevvOPci8dslVMalLrDtqce0l1VsrjGngDbLmDuHK5yJ1p9QAxFz92myDShnfidUhtf9XpvGcJozQ%3D%3D"
        const val NAVER_CLIENT_ID = "v6th2fex1s"
        const val NAVER_CLIENT_SECRET = "I7H6dfDPzUpiTnLsglLBx0b91ckD5FZNsgBbL5iv"
        const val SERVER_URL = "http://${IP}:8080/"
        const val EV_SERVER_URL = "http://api.odcloud.kr/api/EvInfoServiceV2/v1/"
        const val GEOCODER_URL = "https://naveropenapi.apigw.ntruss.com/map-reversegeocode/"

        lateinit var retrofit: Retrofit
        lateinit var evRetrofit: Retrofit
        lateinit var naverRetrofit: Retrofit
    }


    override fun onCreate() {
        super.onCreate()

        // 레트로핏 인스턴스를 생성하고, 레트로핏에 각종 설정값들을 지정해줍니다.
        // 연결 타임아웃시간은 5초로 지정이 되어있고, HttpLoggingInterceptor를 붙여서 어떤 요청이 나가고 들어오는지를 보여줍니다.

        val geoCoderInterceptor = Interceptor { chain ->
            val newRequest = chain.request().newBuilder()
                .addHeader("X-NCP-APIGW-API-KEY-ID", NAVER_CLIENT_ID)
                .addHeader("X-NCP-APIGW-API-KEY", NAVER_CLIENT_SECRET)
                .build()
            chain.proceed(newRequest)
        }

        val okHttpClient = OkHttpClient.Builder()
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        val naverOkHttpClient = OkHttpClient.Builder()
            .addNetworkInterceptor(geoCoderInterceptor)
            .readTimeout(5000, TimeUnit.MILLISECONDS)
            .connectTimeout(5000, TimeUnit.MILLISECONDS)
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
            .build()

        // 앱이 처음 생성되는 순간, retrofit 인스턴스를 생성
        retrofit = Retrofit.Builder()
            .baseUrl(SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        // 앱이 처음 생성되는 순간, evRetrofit 인스턴스를 생성 - 공공데이터 api와 통신하기 위한 retrofit 객체
        evRetrofit = Retrofit.Builder()
            .baseUrl(EV_SERVER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()

        naverRetrofit = Retrofit.Builder()
            .baseUrl(GEOCODER_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(naverOkHttpClient)
            .build()

    }
}
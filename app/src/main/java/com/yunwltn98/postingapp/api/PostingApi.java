package com.yunwltn98.postingapp.api;

import com.yunwltn98.postingapp.model.PostingList;
import com.yunwltn98.postingapp.model.Res;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface PostingApi {

    // 포스팅 생성API
    @Multipart
    @POST("/posting")
    Call<Res> addPosting(@Header("Authorization") String token,
                         @Part MultipartBody.Part photo,
                         @Part("content")RequestBody content);

    // 친구들 포스링 가져오는 API
    @GET("/posting")
    Call<PostingList> getPosting(@Header("Authorization") String token,
                                 @Query("offset") int offset,
                                 @Query("limit") int limit);

    // 포스팅 좋아요
    @POST("/like/{postingId}")
    Call<Res> setLike(@Path("postingId") int postingId, @Header("Authorization") String token);

    // 포스팅 좋아요 삭제
    @DELETE("/like/{postingId}")
    Call<Res> deleteLike(@Path("postingId") int postingId, @Header("Authorization") String token);
}

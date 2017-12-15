package com.example.victo.assesmentagoravai.INTERFACE;

import com.example.victo.assesmentagoravai.Model.TarefaResponse;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by victo on 15/12/2017.
 */

public interface iTarefa {
    @GET("./")
    Call<TarefaResponse> getTarefas();
}

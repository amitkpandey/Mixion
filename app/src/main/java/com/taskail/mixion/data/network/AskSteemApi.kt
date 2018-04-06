package com.taskail.mixion.data.network

import com.taskail.mixion.data.models.AskSteemResult
import io.reactivex.Observable
import retrofit2.http.GET
import retrofit2.http.Query

/**
 *Created by ed on 1/29/18.
 */
interface AskSteemApi {

    @GET("search")
    fun askSteem(@Query("q") author: String,
                        @Query(value = "sort_by") sort: String,
                        @Query(value = "order") order: String,
                        @Query("pg") page: Int = 1): Observable<AskSteemResult>

    //call using ("search+" + term)
    @GET("search")
    fun askSteem(@Query("q") searchTerm: String): Observable<AskSteemResult>

    //call using ("search+" + term, page)
    @GET("search")
    fun askMore(
            @Query("q") searchTerm: String,
            @Query("pg") page: Int?): Observable<AskSteemResult>

    @GET("search")
    fun getUserMentions(@Query("q") author: String,
                        @Query(value = "sort_by") sort: String,
                        @Query(value = "order") order: String,
                        @Query("pg") page: Int): Observable<AskSteemResult>
}
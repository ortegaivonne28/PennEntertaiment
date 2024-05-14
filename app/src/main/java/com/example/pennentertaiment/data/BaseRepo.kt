package com.example.pennentertaiment.data

import com.example.pennentertaiment.model.ForecastErrorResponse
import com.example.pennentertaiment.network.Resource
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import retrofit2.Response
import java.io.IOException


abstract class BaseRepo() {

    suspend fun <T> safeApiCall(apiToBeCalled: suspend () -> Response<T>): Resource<T> {

        return withContext(Dispatchers.IO) {
            try {

                val response: Response<T> = apiToBeCalled()

                if (response.isSuccessful && response.body() != null) {
                    Resource.Success(data = response.body()!!)
                } else {
                    val gson = Gson()
                    val errorResponse: ForecastErrorResponse = gson.fromJson(response.errorBody().toString(), ForecastErrorResponse::class.java)
                    Resource.Error(
                        errorMessage = errorResponse.message ?: "Something went wrong"
                    )
                }

            } catch (e: HttpException) {
                Resource.Error(errorMessage = e.message ?: "Something went wrong")
            } catch (e: IOException) {
                Resource.Error("Please check your network connection")
            } catch (e: Exception) {
                Resource.Error(errorMessage = "Something went wrong")
            }
        }
    }
}
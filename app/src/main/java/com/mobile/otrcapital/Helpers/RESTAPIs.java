package com.mobile.otrcapital.Helpers;

/**
 * Created by Jawad_2 on 8/1/2015.
 */

import com.mobile.otrcapital.Activities.LoginScreen;
import com.mobile.otrcapital.Models.ApiInvoiceDataJson;
import com.mobile.otrcapital.Models.CustomerViewModel;

import java.util.List;

import retrofit.Callback;
import retrofit.http.GET;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.mime.TypedFile;

public interface RESTAPIs
{
    //URL: api/GetClientInfo/{username}/{password-base64-encrypted}
    @GET("/GetClientInfo/{username}/{password}")
    void GetClientInfo(
            @Path("username") String username,
            @Path("password") String password,
            Callback<LoginScreen.AgentViewModel> cb
    );

    //URL: api/Upload
    @Multipart
    @POST("/Upload")
    void Upload(
            @Part ("apiInvoiceDataJson") ApiInvoiceDataJson invoiceData,
            @Part("DocumentType") List<String> DocumentType,
            @Part ("file.pdf") TypedFile file,
            @Part ("FactorType") String FactorType,
            @Part ("mType") String mType,
            Callback<String> cb
    );

    //URL: api/BrokerCheck/{mcnumber}
    @GET("/BrokerCheck/{userName}/{password}/{pKey}")
    void BrokerCheck(
            @Path("userName") String username,
            @Path("password") String password,
            @Path("pKey") String pKey,
            Callback<CustomerViewModel> cb
    );

    //URL: api/GetCustomers/{yyyy}/{MM}/{dd}";
    @GET("/GetCustomers/{date}")
    void GetCustomers(
            @Path("date") String date,
            Callback<List<CustomerViewModel>> cb
    );
}

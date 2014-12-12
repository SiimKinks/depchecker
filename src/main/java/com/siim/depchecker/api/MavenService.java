package com.siim.depchecker.api;

import com.siim.depchecker.model.Versions;
import retrofit.http.GET;
import retrofit.http.Query;
import rx.Observable;

public interface MavenService {

    @GET("/select?core=gav&rows=1&wt=json")
    Observable<Versions> getAllVersions(@Query("q") String query);
}

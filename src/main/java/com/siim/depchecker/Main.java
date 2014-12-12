package com.siim.depchecker;

import com.siim.depchecker.api.MavenService;
import com.siim.depchecker.model.Dependency;
import com.siim.depchecker.model.QueryWrapper;
import com.siim.depchecker.util.FileUtil;
import com.siim.depchecker.util.JsonUtils;
import com.siim.depchecker.util.SimpleEndlessObserver;
import com.squareup.okhttp.OkHttpClient;
import retrofit.RestAdapter;
import retrofit.client.OkClient;
import retrofit.converter.JacksonConverter;
import rx.Observable;
import rx.Observer;

import java.util.concurrent.TimeUnit;

public class Main {

    private static final String OUTPUT_FORMAT = "%s has newer version than %s; latest version=%s";
    private static final String QUERY_FORMAT = "g:\"%s\" AND a:\"%s\"";

    public static void main(String[] args) {
        OkHttpClient okHttpClient = new OkHttpClient();
        okHttpClient.setReadTimeout(5000, TimeUnit.MILLISECONDS);
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setClient(new OkClient(okHttpClient))
                .setEndpoint("http://search.maven.org/solrsearch")
                .setConverter(new JacksonConverter(JsonUtils.objectMapper))
                .build();
        MavenService mavenService = restAdapter.create(MavenService.class);

        Observable.from(args)
                .doOnNext(arg -> System.out.println(String.format("*** Checking %s", arg)))
                .flatMap(FileUtil::getDependecies)
                .flatMap(dependency -> {
                    String queryStr = String.format(QUERY_FORMAT, dependency.getGroupId(), dependency.getArtifactId());
                    return mavenService.getAllVersions(queryStr)
                            .flatMap(versions -> Observable.from(versions.getResponse().getDocs()).take(1))
                            .map(doc -> new QueryWrapper(doc, dependency));
                })
                .subscribe(new Observer<QueryWrapper>() {

                    @Override
                    public void onNext(QueryWrapper data) {
                        if (data.isNetVersionNewer()) {
                            Dependency dependency = data.getDependency();
                            String output = String.format(OUTPUT_FORMAT, dependency.getName(), dependency.getVersion(), data.getNetResponse().getV());
                            System.out.println(output);
                        }
                    }

                    @Override
                    public void onCompleted() {
                        System.out.println("Checking done!");
                        onEnd(true);
                    }

                    @Override
                    public void onError(Throwable e) {
                        System.err.println(String.format("Error while checking. Error=\"%s\"", e.getMessage()));
                        onEnd(false);
                    }

                    private void onEnd(boolean isSuccessful) {
                        System.exit(isSuccessful ? 0 : 1);
                    }
                });
    }

}

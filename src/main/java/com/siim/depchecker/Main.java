package com.siim.depchecker;

import com.siim.depchecker.api.MavenService;
import com.siim.depchecker.model.Dependency;
import com.siim.depchecker.model.QueryWrapper;
import com.siim.depchecker.util.FileUtil;
import com.siim.depchecker.util.JsonUtils;
import com.siim.depchecker.util.SimpleEndlessObserver;
import retrofit.RestAdapter;
import retrofit.converter.JacksonConverter;
import rx.Observable;

public class Main {

    private static final String OUTPUT_FORMAT = "%s has newer version than %s; new version=%s";
    private static final String QUERY_FORMAT = "g:\"%s\" AND a:\"%s\"";

    public static void main(String[] args) {
        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint("http://search.maven.org/solrsearch")
                .setConverter(new JacksonConverter(JsonUtils.objectMapper))
                .build();
        MavenService mavenService = restAdapter.create(MavenService.class);

        Observable.from(args)
                .flatMap(FileUtil::getDependecies)
                .flatMap(dependency -> {
                    String queryStr = String.format(QUERY_FORMAT, dependency.getGroupId(), dependency.getArtifactId());
                    return mavenService.getAllVersions(queryStr)
                            .flatMap(versions -> Observable.from(versions.getResponse().getDocs()).take(1))
                            .map(doc -> new QueryWrapper(doc, dependency));
                })
                .subscribe(new SimpleEndlessObserver<>(data -> {
                    if (data.isNetVersionNewer()) {
                        Dependency dependency = data.getDependency();
                        String output = String.format(OUTPUT_FORMAT, dependency.getName(), dependency.getVersion(), data.getNetResponse().getV());
                        System.out.println(output);
                    }
                }));
    }

}

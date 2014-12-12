package com.siim.depchecker.util;

import com.siim.depchecker.model.Dependency;
import rx.Observable;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class FileUtil {

    private static final Set<String> providedGradleCheckTasks = new HashSet<>(Arrays.asList("compile", "provided", "debugCompile", "retrolambdaConfig", "androidTestCompile"));

    public static Observable<Dependency> getDependecies(final String gradleBuildFilePath) {
        return readFile(gradleBuildFilePath)
                .filter(line -> {
                    String[] splitLine = line.trim().split("\\s+");
                    return providedGradleCheckTasks.contains(splitLine[0]);
                })
                .flatMap(line -> {
                    try {
                        String[] splitLine = line.trim().split("\\:");
                        String groupId = splitLine[0].split("\\'")[1];
                        String artifactId = splitLine[1];
                        String version = splitLine[2].split("\\'")[0];
                        return Observable.just(new Dependency(groupId, artifactId, version));
                    } catch (Exception e) {
                        return Observable.empty();
                    }
                });
    }

    public static Observable<String> readFile(final String filePath) {
        return RxUtil.createErrorHandledObservable(subscriber -> {
            try (BufferedReader bfr = new BufferedReader(new FileReader(filePath))) {
                String line = null;
                while ((line = bfr.readLine()) != null && !subscriber.isUnsubscribed()) {
                    subscriber.onNext(line);
                }
                if (!subscriber.isUnsubscribed()) {
                    subscriber.onCompleted();
                }
            }
        });
    }
}

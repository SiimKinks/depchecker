package com.siim.depchecker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class QueryWrapper {

    private Versions.Doc netResponse;
    private Dependency dependency;

    public boolean isNetVersionNewer() {
        return !netResponse.getV().equals(dependency.getVersion());
    }
}

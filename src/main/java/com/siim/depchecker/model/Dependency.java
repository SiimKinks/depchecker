package com.siim.depchecker.model;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Dependency {

    private String groupId;
    private String artifactId;
    private String version;

    public String getName() {
        return groupId + ":" + artifactId;
    }

}

package com.siim.depchecker.model;

import lombok.Data;

import java.util.List;

@Data
public class Versions {
    private Response response;

    @Data
    public static class Response {
        private List<Doc> docs;
    }

    @Data
    public static class Doc {
        private String id;
        private String g;
        private String a;
        private String v;
    }
}

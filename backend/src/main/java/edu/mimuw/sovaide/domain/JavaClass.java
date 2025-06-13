package edu.mimuw.sovaide.domain;

import java.util.List;

public record JavaClass(String name, List<String> imports, String content) {
}

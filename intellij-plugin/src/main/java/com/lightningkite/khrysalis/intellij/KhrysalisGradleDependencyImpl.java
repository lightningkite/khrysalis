package com.lightningkite.khrysalis.intellij;

import com.intellij.serialization.PropertyMapping;
import org.jetbrains.plugins.gradle.model.IntelliJProjectSettings;
import org.jetbrains.plugins.gradle.model.IntelliJSettings;

import java.io.File;
import java.util.List;

public final class KhrysalisGradleDependencyImpl implements KhrysalisGradleDependency {
    private static final long serialVersionUID = 1L;

    private final List<File> files;

    @PropertyMapping("settings")
    public KhrysalisGradleDependencyImpl(List<File> files) {
        this.files = files;
    }

    @Override
    public List<File> getFiles() {
        return this.files;
    }
}
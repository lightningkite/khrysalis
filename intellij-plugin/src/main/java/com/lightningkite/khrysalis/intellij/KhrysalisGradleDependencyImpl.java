package com.lightningkite.khrysalis.intellij;

import com.intellij.serialization.PropertyMapping;
import org.jetbrains.plugins.gradle.model.IntelliJProjectSettings;
import org.jetbrains.plugins.gradle.model.IntelliJSettings;

import java.io.File;
import java.util.List;

public final class KhrysalisGradleDependencyImpl implements KhrysalisGradleDependency {
    private static final long serialVersionUID = 1L;

    private final List<File> files;
    private final List<File> sourceDirectories;

    @PropertyMapping("settings")
    public KhrysalisGradleDependencyImpl(List<File> files, List<File> sourceDirectories) {
        this.files = files;
        this.sourceDirectories = sourceDirectories;
    }

    @Override
    public List<File> getFiles() {
        return this.files;
    }
    public List<File> getSourceDirectories() {
        return this.sourceDirectories;
    }
}
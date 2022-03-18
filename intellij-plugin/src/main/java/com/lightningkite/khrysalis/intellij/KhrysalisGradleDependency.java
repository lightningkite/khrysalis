package com.lightningkite.khrysalis.intellij;

import java.io.File;
import java.io.Serializable;
import java.util.List;

public interface KhrysalisGradleDependency extends Serializable {
    List<File> getFiles();
    List<File> getSourceDirectories();
}
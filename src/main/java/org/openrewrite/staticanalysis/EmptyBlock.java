/*
 * Copyright 2020 the original author or authors.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * https://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.openrewrite.staticanalysis;

import org.openrewrite.ExecutionContext;
import org.openrewrite.Recipe;
import org.openrewrite.SourceFile;
import org.openrewrite.TreeVisitor;
import org.openrewrite.java.JavaIsoVisitor;
import org.openrewrite.java.style.Checkstyle;
import org.openrewrite.java.style.EmptyBlockStyle;
import org.openrewrite.java.tree.JavaSourceFile;

import java.time.Duration;
import java.util.Collections;
import java.util.Set;

public class EmptyBlock extends Recipe {

    @Override
    public String getDisplayName() {
        return "Remove empty blocks";
    }

    @Override
    public String getDescription() {
        return "Remove empty blocks that effectively do nothing.";
    }

    @Override
    public Set<String> getTags() {
        return Collections.singleton("RSPEC-108");
    }

    @Override
    public Duration getEstimatedEffortPerOccurrence() {
        return Duration.ofMinutes(5);
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        return new EmptyBlockFromCompilationUnitStyle();
    }

    private static class EmptyBlockFromCompilationUnitStyle extends JavaIsoVisitor<ExecutionContext> {
        @Override
        public JavaSourceFile visitJavaSourceFile(JavaSourceFile cu, ExecutionContext executionContext) {
            EmptyBlockStyle style = ((SourceFile)cu).getStyle(EmptyBlockStyle.class);
            if (style == null) {
                style = Checkstyle.emptyBlock();
            }
            doAfterVisit(new EmptyBlockVisitor<>(style));
            return cu;
        }
    }
}

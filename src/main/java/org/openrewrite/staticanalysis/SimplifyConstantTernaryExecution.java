/*
 * Copyright 2024 the original author or authors.
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
import org.openrewrite.Repeat;
import org.openrewrite.TreeVisitor;
import org.openrewrite.internal.lang.Nullable;
import org.openrewrite.java.JavaVisitor;
import org.openrewrite.java.tree.Expression;
import org.openrewrite.java.tree.J;

import java.time.Duration;

public class SimplifyConstantTernaryExecution extends Recipe {
    @Override
    public String getDisplayName() {
        return "Simplify constant ternary branch execution";
    }

    @Override
    public String getDescription() {
        return "Checks for ternary expressions that are always `true` or `false` and simplifies them.";
    }

    @Override
    public @Nullable Duration getEstimatedEffortPerOccurrence() {
        return Duration.ofSeconds(15);
    }

    @Override
    public TreeVisitor<?, ExecutionContext> getVisitor() {
        JavaVisitor<ExecutionContext> v = new JavaVisitor<ExecutionContext>() {
            @Override
            public J visitTernary(J.Ternary ternary, ExecutionContext executionContext) {
                J.Ternary t = (J.Ternary) super.visitTernary(ternary, executionContext);
                Expression condition =
                        SimplifyConstantIfBranchExecution.cleanupBooleanExpression(
                                t.getCondition(),
                                getCursor(),
                                executionContext
                        );
                if (J.Literal.isLiteralValue(condition, true)) {
                    return autoFormat(t.getTruePart(), executionContext);
                } else if (J.Literal.isLiteralValue(condition, false)) {
                    return autoFormat(t.getFalsePart(), executionContext);
                }
                return t;
            }
        };
        return Repeat.repeatUntilStable(v);
    }
}

package org.palladiosimulator.somox.analyzer.rules.engine.test;

import org.junit.jupiter.api.Test;
import org.palladiosimulator.somox.analyzer.rules.all.DefaultRule;

public class ACMETest extends RuleEngineTest {

    protected ACMETest() {
        super("external/acmeair-1.2.0", DefaultRule.JAX_RS);
    }

    /**
     * Tests the basic functionality of the RuleEngineAnalyzer when executing the JAX_RS rule.
     * Requires it to execute without an exception and produce an output file with the correct
     * contents.
     */
    @Test
    void test() {
    }
}

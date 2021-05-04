package org.palladiosimulator.somox.analyzer.rules.configuration;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.common.util.URI;
import org.somox.configuration.AbstractMoxConfiguration;

import de.uka.ipd.sdq.workflow.extension.ExtendableJobConfiguration;

public class RuleEngineConfiguration extends AbstractMoxConfiguration implements ExtendableJobConfiguration {

    public static final String RULE_ENGINE_INPUT_PATH = "org.palladiosimulator.somox.analyzer.rules.configuration.input.path";
    public static final String RULE_ENGINE_OUTPUT_PATH = "org.palladiosimulator.somox.analyzer.rules.configuration.output.path";

    private URI inputFolder;
    private URI outputFolder;

    private final Map<String, Object> attributes;

    public RuleEngineConfiguration() {
        this(new HashMap<>());
    }

    public RuleEngineConfiguration(Map<String, Object> attributes) {
        this.attributes = Objects.requireNonNull(attributes);
        applyAttributeMap(attributes);
    }

    @Override
    public void applyAttributeMap(final Map<String, Object> attributeMap) {
        if ((attributeMap == null)) {
            return;
        }
        super.applyAttributeMap(attributeMap);

        if (attributeMap.get(RULE_ENGINE_INPUT_PATH) != null) {
            setInputFolder(URI.createURI((String) attributeMap.get(RULE_ENGINE_INPUT_PATH)));
        }
        if (attributeMap.get(RULE_ENGINE_OUTPUT_PATH) != null) {
            setOutputFolder(URI.createURI((String) attributeMap.get(RULE_ENGINE_OUTPUT_PATH)));
        }
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public URI getInputFolder() {
        return inputFolder;
    }

    public URI getOutputFolder() {
        return outputFolder;
    }

    public void setInputFolder(URI inputFolder) {
        this.inputFolder = inputFolder;
    }

    public void setOutputFolder(URI outputFolder) {
        this.outputFolder = outputFolder;
    }

    @Override
    public Map<String, Object> toMap() {
        final Map<String, Object> result = super.toMap();

        result.put(RULE_ENGINE_INPUT_PATH, inputFolder);
        result.put(RULE_ENGINE_OUTPUT_PATH, outputFolder);

        return result;
    }
}
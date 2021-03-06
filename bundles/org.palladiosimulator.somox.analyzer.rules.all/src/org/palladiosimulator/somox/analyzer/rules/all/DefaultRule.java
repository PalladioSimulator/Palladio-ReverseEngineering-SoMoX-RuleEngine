package org.palladiosimulator.somox.analyzer.rules.all;

import java.lang.reflect.InvocationTargetException;

import org.palladiosimulator.somox.analyzer.rules.blackboard.RuleEngineBlackboard;
import org.palladiosimulator.somox.analyzer.rules.engine.IRule;
import org.palladiosimulator.somox.analyzer.rules.impl.eclipse.JaxRSRules;
import org.palladiosimulator.somox.analyzer.rules.impl.eclipse.SpringRules;
import org.palladiosimulator.somox.analyzer.rules.maven.MavenRules;

/**
* This enum contains all default rule technologies the rule engine provides
*
* @param  url  an absolute URL giving the base location of the image
* @param  name the location of the image, relative to the url argument
* @return      the image at the specified URL
* @see         Image
*/
public enum DefaultRule {

	SPRING(SpringRules.class),
	JAX_RS(JaxRSRules.class),
	MAVEN(MavenRules.class),
	SPRING_EMFTEXT(org.palladiosimulator.somox.analyzer.rules.impl.emftext.SpringRules.class),
	JAX_RS_EMFTEXT(org.palladiosimulator.somox.analyzer.rules.impl.emftext.JaxRSRules.class);

	private final Class<? extends IRule> ruleClass;

	private DefaultRule(Class<? extends IRule> ruleClass){
		this.ruleClass = ruleClass;
	}

	/**
	* Returns the names of all currently available default rule technologies.
	*
	* @return      the names of all available default rule technologies
	* @see         Image
	*/
	public static String[] valuesAsString() {
		String[] names = new String[DefaultRule.values().length];
		for(int i=0; i< DefaultRule.values().length; i++) {
			names[i] = DefaultRule.values()[i].name();
		}

		return names;
	}

	public IRule getRule(RuleEngineBlackboard blackboard) {
		try {
            return ruleClass.getDeclaredConstructor(RuleEngineBlackboard.class).newInstance(blackboard);
        } catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
                | NoSuchMethodException | SecurityException e) {
            // TODO Maybe solve this a little bit better..?
            e.printStackTrace();
        }
		return null;
	}

}

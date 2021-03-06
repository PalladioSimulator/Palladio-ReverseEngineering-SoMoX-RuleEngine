package org.palladiosimulator.somox.analyzer.rules.impl.emftext

import org.emftext.language.java.containers.impl.CompilationUnitImpl
import org.palladiosimulator.somox.analyzer.rules.engine.IRule

import static org.palladiosimulator.somox.analyzer.rules.engine.EMFTextRuleHelper.*
import org.palladiosimulator.somox.analyzer.rules.blackboard.RuleEngineBlackboard
import java.nio.file.Path;

class JaxRSRules extends IRule{
	
	new(RuleEngineBlackboard blackboard) {
		super(blackboard)
	}
	
	override boolean processRules(Path path) {
		val units = blackboard.getCompilationUnitAt(path)
		
		var containedSuccessful = false
		for (unit : units) {
			if (unit.isEMFTextCompilationUnit()) {
				val emfUnit = unit.getEMFTextCompilationUnit()
				containedSuccessful = processRuleForCompUnit(emfUnit) || containedSuccessful
			}
		}
		
		return containedSuccessful
	}
	
	def boolean processRuleForCompUnit(CompilationUnitImpl unit) {
		val pcmDetector = blackboard.getEMFTextPCMDetector()

		// technology based and general recognition
		val isConverter = isUnitAnnotatedWithName(unit, "Converter")
		if(isConverter){
			detectDefault(unit)
		return true
		}
		
		// detect controller component	
		val isUnitController = isUnitAnnotatedWithName(unit, "Path")
		if(isUnitController){
			pcmDetector.detectComponent(unit) 
			pcmDetector.detectOperationInterface(unit)
			getMethods(unit).forEach[m|
			if(isMethodAnnotatedWithName(m,"DELETE","GET","HEAD","PUT","POST","OPTIONS")) pcmDetector.detectProvidedInterface(unit,m)]
			getFields(unit).forEach[f|if(isFieldAbstract(f)) pcmDetector.detectRequiredInterface(unit, f)]
		return true
		} 
		
		val isWebListener = isUnitAnnotatedWithName(unit, "WebListener","WebServlet")
		if(isWebListener){
			pcmDetector.detectComponent(unit)
			pcmDetector.detectOperationInterface(unit)
			getMethods(unit).forEach[m|
			if(isMethodModifiedExactlyWith(m,"public") || isMethodModifiedExactlyWith(m,"protected")) pcmDetector.detectProvidedInterface(unit,m)]
			getFields(unit).forEach[f|if(isFieldAbstract(f)) pcmDetector.detectRequiredInterface(unit, f)]
		return true
		}
		
		// detect implementing component
		val isUnit = isClassImplementing(unit)
		if(isUnit && !isUnitController && !isWebListener && getAllInterfaces(unit).size() > 0){
			pcmDetector.detectComponent(unit)
			val firstIn = getAllInterfaces(unit).get(0)
			pcmDetector.detectOperationInterface(firstIn)
			getMethods(firstIn).forEach[m|pcmDetector.detectProvidedInterface(unit, firstIn, m)]
			getFields(unit).forEach[f|if(isFieldAbstract(f)) pcmDetector.detectRequiredInterface(unit, f)]
			return true
		}
		
		// detect normal components
		val classModified = isClassModifiedExactlyWith(unit, "public","final");
		if(!isUnit && !isUnitController && !isWebListener && classModified){
			pcmDetector.detectComponent(unit)
			detectDefault(unit)
			return true
		} 
		return false
		
	}
	
	def detectDefault(CompilationUnitImpl unit) {
		val pcmDetector = blackboard.getEMFTextPCMDetector()

		pcmDetector.detectComponent(unit)
		pcmDetector.detectOperationInterface(unit)
		getAllPublicMethods(unit).forEach[m|pcmDetector.detectProvidedInterface(unit,m)]
		getFields(unit).forEach[f|if(isFieldAbstract(f)) pcmDetector.detectRequiredInterface(unit, f)]
	}
	
}

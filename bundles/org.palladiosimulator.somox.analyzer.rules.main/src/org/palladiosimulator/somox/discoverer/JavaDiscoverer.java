package org.palladiosimulator.somox.discoverer;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FileASTRequestor;
import org.palladiosimulator.somox.analyzer.rules.blackboard.RuleEngineBlackboard;
import org.palladiosimulator.somox.analyzer.rules.configuration.RuleEngineConfiguration;

import de.uka.ipd.sdq.workflow.jobs.AbstractBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.CleanupFailedException;
import de.uka.ipd.sdq.workflow.jobs.IBlackboardInteractingJob;
import de.uka.ipd.sdq.workflow.jobs.JobFailedException;
import de.uka.ipd.sdq.workflow.jobs.UserCanceledException;

public class JavaDiscoverer implements Discoverer {

    @Override
    public IBlackboardInteractingJob<RuleEngineBlackboard> create(final RuleEngineConfiguration configuration,
            final RuleEngineBlackboard blackboard) {
        return new AbstractBlackboardInteractingJob<>() {

            @Override
            public void cleanup(final IProgressMonitor monitor) throws CleanupFailedException {
                // TODO Auto-generated method stub
            }

            @Override
            public void execute(final IProgressMonitor monitor) throws JobFailedException, UserCanceledException {
                final Path root = Paths.get(configuration.getInputFolder().devicePath()).toAbsolutePath().normalize();
                setBlackboard(Objects.requireNonNull(blackboard));
                final Map<String, CompilationUnit> compilationUnits = new HashMap<>();
                final ASTParser parser = ASTParser.newParser(AST.getJLSLatest());
                parser.setKind(ASTParser.K_COMPILATION_UNIT);
                parser.setResolveBindings(true);
                parser.setBindingsRecovery(true);
                parser.setStatementsRecovery(true);
                final String[] classpathEntries = Discoverer.find(root, ".jar", logger).toArray(String[]::new);
                final String[] sourceFilePaths = Discoverer.find(root, ".java", logger).toArray(String[]::new);
                try {
                    parser.setEnvironment(classpathEntries, sourceFilePaths, null, true);
                    parser.createASTs(sourceFilePaths, new String[sourceFilePaths.length], new String[0], new FileASTRequestor() {
                        @Override
                        public void acceptAST(final String sourceFilePath, final CompilationUnit ast) {
                            compilationUnits.put(sourceFilePath, ast);
                        }
                    }, monitor);
                } catch (IllegalArgumentException | IllegalStateException e) {
                    logger.error(String.format("No Java files in %s could be transposed.", root), e);
                }

            }

            @Override
            public String getName() {
                // TODO Auto-generated method stub
                return null;
            }
        };
    }

    @Override
    public Set<String> getConfigurationKeys() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getID() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

}
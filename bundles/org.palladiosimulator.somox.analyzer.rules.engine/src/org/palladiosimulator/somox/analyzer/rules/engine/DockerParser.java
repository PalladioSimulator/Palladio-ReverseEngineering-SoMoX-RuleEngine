package org.palladiosimulator.somox.analyzer.rules.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.palladiosimulator.somox.analyzer.rules.blackboard.CompilationUnitWrapper;
import org.apache.log4j.Logger;
// import org.yaml.snakeyaml.Yaml;

/**
 * The DockerParser parses a docker-compose file to extract a mapping between service names
 * (microservices) and JaMoPP model instances. Later, this parser will be replaced with the project
 * in: https://github.com/PalladioSimulator/Palladio-ReverseEngineering-Docker
 */
public class DockerParser {
    private final String FILE_NAME = "docker-compose";
    private final Path path;
    private final IPCMDetector pcmDetector;
    private final Map<String, List<CompilationUnitWrapper>> mapping;

    private static final Logger LOG = Logger.getLogger(DockerParser.class);

    public DockerParser(Path path, IPCMDetector pcmDetector) {

        LOG.info("starting docker process");

        this.path = path;
        this.pcmDetector = pcmDetector;
        final InputStream input = getDockerFile();
        final List<String> services = extractServiceNames(input);
        mapping = createServiceComponentMapping(services);
    }

    /**
     * Returns a Stream to the docker-compose file found by walking through a given project
     * directory.
     *
     * @return the docker-compose file as stream
     */
    private InputStream getDockerFile() {

        List<Path> paths = new ArrayList<>();
        try (Stream<Path> files = Files.walk(path)) {
            paths = files.filter(f -> f.getFileName()
                .toString()
                .contains(FILE_NAME))
                .collect(Collectors.toList());
        } catch (final IOException e) {
            e.printStackTrace();
        }
        if (paths.size() <= 0) {
            LOG.info("No docker compose file detected.");
            return null;
        }
        final Path path = paths.get(0);

        final File initialFile = path.toFile();
        InputStream targetStream = null;
        try {
            targetStream = new FileInputStream(initialFile);
            return targetStream;
        } catch (final FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Extracts the service names within a docker-compose file.
     *
     * @param stream
     *            the docker-compose file
     * @return the list of all service names found in the docker-compose file
     */
    @SuppressWarnings("unchecked")
    private List<String> extractServiceNames(InputStream stream) {
        // final Yaml yaml = new Yaml();
        final Map<String, Object> object = new HashMap<>(); // (Map<String, Object>)
                                                            // yaml.load(stream);

        // get all service names from the map
        if (!object.containsKey("services")) {
            LOG.info("No property with name 'services' in docker compose file. File not usable");
            return new ArrayList<String>();
        }
        final List<String> serviceNames = new ArrayList<>();
        serviceNames.addAll(((Map<String, Object>) object.get("services")).keySet());
        return serviceNames;
    }

    /**
     * Creates a mapping between service names and JaMoPP model instances to know which component
     * belongs to which microservice
     *
     * @param serviceNames
     *            a list of all service names from a docker-compose file
     * @return the mapping between service names and JaMoPP model instances
     */
    private Map<String, List<CompilationUnitWrapper>> createServiceComponentMapping(List<String> serviceNames) {

        final List<CompilationUnitWrapper> components = pcmDetector.getWrappedComponents();

        final Map<String, List<CompilationUnitWrapper>> serviceToCompMapping = new HashMap<>();

        components.forEach(comp -> {
            try (Stream<Path> files = Files.walk(path)) {
                // TODO try to find a more robust heuristic
                final List<Path> foundPaths = files.filter(f -> f.toString()
                    .contains(comp.getName()))
                    .collect(Collectors.toList());

                if (foundPaths.size() > 0) {
                    serviceNames.forEach(serviceName -> {
                        if (foundPaths.get(0)
                            .toString()
                            .contains(serviceName)) {
                            if (!serviceToCompMapping.containsKey(serviceName)) {
                                serviceToCompMapping.put(serviceName, new ArrayList<>());
                            }
                            serviceToCompMapping.get(serviceName)
                                .add(comp);
                        }
                    });
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }
        });

        return serviceToCompMapping;
    }

    public Map<String, List<CompilationUnitWrapper>> getMapping() {
        return mapping;
    }

}

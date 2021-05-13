package de.scholzi100.incubator.resolver.modulefinder;

import de.scholzi100.incubator.modulenamefinder.ModuleNameResolverImpl;
import de.scholzi100.incubator.resolver.modulefinder.test.TestMain;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.util.artifact.SubArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.module.*;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.jar.JarFile;
import java.util.stream.Collectors;
import java.util.zip.ZipFile;

public class MavenModuleFinder implements ModuleFinder {

    private final Logger LOGGER = LoggerFactory.getLogger(MavenModuleFinder.class);

    private final Map<String, String> map;
    private final RepoResolverOverlay resolverOverlay;
    private final RepositorySystem repositorySystem;

    private MavenModuleFinder(Map<String, String> map) {
        this.map = map;
        resolverOverlay = TestMain.getRepoResolverOverlay(ModuleLayer.boot(), "modulelayer:default", ClassLoader.getSystemClassLoader());
        repositorySystem = resolverOverlay.newRepositorySystem();
    }

    public static ModuleFinder of(Map<String, String> map){
        return new MavenModuleFinder(map);
    }

    @Override
    public Optional<ModuleReference> find(String name) {
        return Optional.ofNullable(name)
                .filter(map::containsKey)
                .flatMap(this::readModuleDescriptor);
    }

    @Override
    public Set<ModuleReference> findAll() {
        return map.keySet().stream()
                .map(this::readModuleDescriptor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }




    private Optional<ModuleReference> readModuleDescriptor(String moduleName) {

        //Lookup module relation

        final var dependency = map.get(moduleName);

        final var session = resolverOverlay.newRepositorySystemSession(repositorySystem);

        Artifact artifact = new DefaultArtifact(dependency);

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(RepoResolverOverlay.newRepositories(repositorySystem, session));

        final ArtifactResult artifactResults;
        try {
            artifactResults = repositorySystem.resolveArtifact(session, artifactRequest);
        } catch (ArtifactResolutionException e) {
            //TODO change behavior
            LOGGER.trace("Unable to resolve {} because {}", moduleName, e);
            throw new IllegalArgumentException(e);
        }
        artifact = artifactResults.getArtifact();

        final var path = artifact.getFile().toPath();

        final var moduleNameResolver = new ModuleNameResolverImpl();
        final var moduleDescriptor = moduleNameResolver.getModuleDescriptorByPath(path)
                .flatMap(moduleDescriptor1 -> ModuleFinder.of(path).find(moduleDescriptor1.name()));


//        could be used to extract only module-info and manifest from the network
//
//        ByteBuffer bb = null;
//        final ModuleDescriptor moduleDescriptor;
//        try {
//            moduleDescriptor = ModuleDescriptor.read(bb);
//            return Optional.of(moduleDescriptor);
//        } catch (InvalidModuleDescriptorException e) {
//            LOGGER.trace("Unable to lookup ModuleDescriptor for {} because {}", moduleName, e);
//            throw new IllegalArgumentException(e);
//        }
        return moduleDescriptor;
    }

}

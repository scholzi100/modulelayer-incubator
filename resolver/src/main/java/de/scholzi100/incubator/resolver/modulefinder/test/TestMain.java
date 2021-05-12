package de.scholzi100.incubator.resolver.modulefinder.test;

import de.scholzi100.incubator.resolver.modulefinder.MavenModuleFinder;
import de.scholzi100.incubator.resolver.modulefinder.RepoResolverOverlay;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.module.ModuleFinder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestMain {

    private final Logger LOGGER = LoggerFactory.getLogger(TestMain.class);

    public static void main(String[] args) throws ArtifactResolutionException {
        final var testMain = new TestMain();
//        testMain.start();
        /**
         * Create
         */
        testMain.test();
    }

    private void test() throws ArtifactResolutionException {
        final RepoResolverOverlay resolverOverlay = getRepoResolverOverlay(ModuleLayer.boot(), "modulelayer:default", ClassLoader.getSystemClassLoader());

        //Test to resolve jackson-core

        RepositorySystem system = resolverOverlay.newRepositorySystem();

        RepositorySystemSession session = resolverOverlay.newRepositorySystemSession( system );

        Artifact artifact = new DefaultArtifact( "com.fasterxml.jackson.core:jackson-core:2.12.3" );

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact( artifact );
        artifactRequest.setRepositories( RepoResolverOverlay.newRepositories( system, session ) );

        ArtifactResult artifactResult = system.resolveArtifact( session, artifactRequest );

        artifact = artifactResult.getArtifact();

        System.out.println( artifact + " resolved to  " + artifact.getFile() );
    }

    public static RepoResolverOverlay getRepoResolverOverlay(ModuleLayer parentModuleLayer, String uri,  ClassLoader targetClassLoader) {
        // currently project is need to be build
        final var of = Path.of("resolver-provider/target/resolver-provider-0.1.0-SNAPSHOT-test");
        if (Files.notExists(of)){
            throw new IllegalStateException(of.toAbsolutePath().toString());
        }
        final var finder = ModuleFinder.of(of);
        printModuleLayer(parentModuleLayer);
        System.out.println("-----");

        final var configuration = parentModuleLayer.configuration().resolve(finder, ModuleFinder.of(), Set.of("de.scholzi100.incubator.resolver.provider"));
        final var moduleLayer = parentModuleLayer.defineModulesWithOneLoader(configuration, targetClassLoader);

        printModuleLayer(moduleLayer);

        return RepoResolverOverlay.of(moduleLayer, uri);
    }

    private static void printModuleLayer(ModuleLayer moduleLayer) {
        moduleLayer.modules().stream().sorted(Comparator.comparing(module -> module.getDescriptor().name())).forEach(module -> {
            System.out.println(module.getDescriptor().toNameAndVersion()+(module.getDescriptor().isAutomatic() ? " [automatic]" : ""));
        });
    }

    private void start() {

        // MavenModuleFinder does require module-name<->maven-artifact-cords mapping
        // (maybe could be embedded into application jar/modules using maven-plugin)
        final var map = Map.ofEntries(
                Map.entry("com.fasterxml.jackson.core", "com.fasterxml.jackson.core:jackson-core:2.12.3"),
                Map.entry("com.fasterxml.jackson.databind", "com.fasterxml.jackson.core:jackson-databind:2.12.3"),
                Map.entry("com.fasterxml.jackson.annotation", "com.fasterxml.jackson.core:jackson-annotation:2.12.3")
        );

        final var finder = MavenModuleFinder.of(map);

        final var boot = ModuleLayer.boot();

        final var resolve = boot.configuration().resolve(finder, ModuleFinder.of(), List.of("com.fasterxml.jackson.databind"));
        final var layer = boot.defineModulesWithOneLoader(resolve, getClass().getClassLoader());
        final var module = layer.findModule("com.fasterxml.jackson.databind").get();
        System.out.println(module.getPackages());
        try {
            module.getClassLoader().loadClass("de.scholzi100.test.Test");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}

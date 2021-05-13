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
import org.eclipse.aether.util.artifact.SubArtifact;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.module.ModuleFinder;
import java.lang.reflect.InvocationTargetException;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestMain {

    private final Logger LOGGER = LoggerFactory.getLogger(TestMain.class);

    public static void main(String[] args) throws ArtifactResolutionException {
        final var testMain = new TestMain();
        testMain.start();
        /**
         * Create
         */
//        testMain.test();
    }

    private void test() throws ArtifactResolutionException {
        final RepoResolverOverlay resolverOverlay = getRepoResolverOverlay(ModuleLayer.boot(), "modulelayer:default", ClassLoader.getSystemClassLoader());

        //Test to resolve jackson-core

        RepositorySystem system = resolverOverlay.newRepositorySystem();

        RepositorySystemSession session = resolverOverlay.newRepositorySystemSession(system);

        Artifact artifact = new DefaultArtifact("com.fasterxml.jackson.core:jackson-core:2.12.3");
        Artifact shaArtifact = new SubArtifact(artifact, "", "jar.sha1");
        Artifact pomArtifact = new SubArtifact(artifact, "", "pom.sha1");

        ArtifactRequest artifactRequest = new ArtifactRequest();
        artifactRequest.setArtifact(artifact);
        artifactRequest.setRepositories(RepoResolverOverlay.newRepositories(system, session));

        ArtifactRequest pomSha1Request = new ArtifactRequest();
        pomSha1Request.setArtifact(pomArtifact);
        pomSha1Request.setRepositories(RepoResolverOverlay.newRepositories(system, session));

        ArtifactRequest jarSha1Request = new ArtifactRequest();
        jarSha1Request.setArtifact(shaArtifact);
        jarSha1Request.setRepositories(RepoResolverOverlay.newRepositories(system, session));

        final var artifactResults = system.resolveArtifacts(session, List.of(artifactRequest, pomSha1Request, jarSha1Request));
        artifactResults.stream().map(ArtifactResult::getArtifact).forEach(resultArtifact -> {

            if (resultArtifact.getExtension().equals("jar")) {
                final MessageDigest digest;
                try {
                    digest = MessageDigest.getInstance("SHA-1");
                } catch (NoSuchAlgorithmException e) {
                    throw new IllegalArgumentException(e);
                }
                final var path = resultArtifact.getFile().toPath();

                try (final var bufferedReader = Files.newByteChannel(path, StandardOpenOption.READ)) {
                    var bf = ByteBuffer.allocate(1024);
                    while (bufferedReader.read(bf) > 0) {
                        bf.flip();
                        digest.update(bf);
                        bf.clear();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                System.out.printf("%s resolved with SHA1 checksum %040x to %s%n", resultArtifact, new BigInteger(1, digest.digest()), resultArtifact.getFile());
            } else {
                try {
                    System.out.printf("%s resolved with SHA1 checksum %s to %s%n", resultArtifact, Files.readString(resultArtifact.getFile().toPath()), resultArtifact.getFile());
                } catch (IOException e) {
                    System.out.println("Failed to read " + artifact.getFile().toPath().toAbsolutePath());
                }
            }
        });

    }

    public static RepoResolverOverlay getRepoResolverOverlay(ModuleLayer parentModuleLayer, String uri,  ClassLoader targetClassLoader) {
        // currently project is need to be build
        final var of = Path.of("resolver-provider/target/resolver-provider-0.1.0-SNAPSHOT-test");
        if (Files.notExists(of)){
            throw new IllegalStateException(of.toAbsolutePath().toString());
        }
        final var finder = ModuleFinder.of(of);
        //printModuleLayer(parentModuleLayer);
        //System.out.println("-----");

        final var configuration = parentModuleLayer.configuration().resolve(finder, ModuleFinder.of(), Set.of("de.scholzi100.incubator.resolver.provider"));
        final var moduleLayer = parentModuleLayer.defineModulesWithOneLoader(configuration, targetClassLoader);

        //printModuleLayer(moduleLayer);

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
                Map.entry("com.fasterxml.jackson.annotation", "com.fasterxml.jackson.core:jackson-annotations:2.12.3")
        );

        final var finder = MavenModuleFinder.of(map);

        final var boot = ModuleLayer.boot();

        final var resolve = boot.configuration().resolve(finder, ModuleFinder.of(), List.of("com.fasterxml.jackson.databind"));
        final var controller = ModuleLayer.defineModulesWithOneLoader(resolve, List.of(boot) ,getClass().getClassLoader());
        controller.layer().modules().forEach(module -> {
            System.out.println(module.getDescriptor().toNameAndVersion());
        });
    }

}

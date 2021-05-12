package de.scholzi100.incubator.resolver.modulefinder;

import de.scholzi100.incubator.resolver.modulefinder.provider.RepoResolver;
import de.scholzi100.incubator.resolver.modulefinder.providers.*;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.artifact.DefaultArtifactType;
import org.eclipse.aether.collection.DependencyGraphTransformer;
import org.eclipse.aether.collection.DependencyManager;
import org.eclipse.aether.collection.DependencySelector;
import org.eclipse.aether.collection.DependencyTraverser;
import org.eclipse.aether.connector.basic.BasicRepositoryConnectorFactory;
import org.eclipse.aether.impl.*;
import org.eclipse.aether.spi.connector.RepositoryConnectorFactory;
import org.eclipse.aether.spi.connector.transport.TransporterFactory;
import org.eclipse.aether.transport.file.FileTransporterFactory;
import org.eclipse.aether.transport.http.HttpTransporterFactory;
import org.eclipse.aether.util.artifact.DefaultArtifactTypeRegistry;
import org.eclipse.aether.util.graph.manager.ClassicDependencyManager;
import org.eclipse.aether.util.graph.selector.AndDependencySelector;
import org.eclipse.aether.util.graph.selector.ExclusionDependencySelector;
import org.eclipse.aether.util.graph.selector.OptionalDependencySelector;
import org.eclipse.aether.util.graph.selector.ScopeDependencySelector;
import org.eclipse.aether.util.graph.transformer.*;
import org.eclipse.aether.util.graph.traverser.FatArtifactTraverser;
import org.eclipse.aether.util.repository.SimpleArtifactDescriptorPolicy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Properties;

/**
 * Abstract maven resolver implantation behind {@link java.util.ServiceLoader}<br>
 * <p>
 * Related issue https://issues.apache.org/jira/browse/MNG-7037
 */
public class RepoResolverImpl implements RepoResolver {

    private static URI SERVICE_URI;
    private static Logger LOGGER = LoggerFactory.getLogger(RepoResolverImpl.class);

    static {
        try {
            SERVICE_URI = new URI("modulelayer", "default", null);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public static DefaultServiceLocator newServiceLocator() {
        //TODO face out DefaultServiceLocator in favor of SISU or Guava
        DefaultServiceLocator locator = new DefaultServiceLocator();
        locator.addService(ArtifactDescriptorReader.class, DefaultArtifactDescriptorReader.class);
        locator.addService(VersionResolver.class, DefaultVersionResolver.class);
        locator.addService(VersionRangeResolver.class, DefaultVersionRangeResolver.class);
        locator.addService(MetadataGeneratorFactory.class, SnapshotMetadataGeneratorFactory.class);
        locator.addService(MetadataGeneratorFactory.class, VersionsMetadataGeneratorFactory.class);
        return locator;
    }

    @Override
    public URI getURI() {
        return SERVICE_URI;
    }

    @Override
    public RepositorySystem newRepositorySystem() {
        //TODO face out DefaultServiceLocator in favor of SISU or Guava
        DefaultServiceLocator locator = newServiceLocator();
        locator.addService(RepositoryConnectorFactory.class, BasicRepositoryConnectorFactory.class);
        locator.addService(TransporterFactory.class, FileTransporterFactory.class);
        locator.addService(TransporterFactory.class, HttpTransporterFactory.class);

        locator.setErrorHandler(new DefaultServiceLocator.ErrorHandler() {
            @Override
            public void serviceCreationFailed(Class<?> type, Class<?> impl, Throwable exception) {
                LOGGER.error("Service creation failed for {} with implementation {} {}", type, impl, exception);
            }
        });

        return locator.getService(RepositorySystem.class);
    }

    @Override
    public DefaultRepositorySystemSession newSession() {
        //TODO replace session creation
        DefaultRepositorySystemSession session = new DefaultRepositorySystemSession();

        DependencyTraverser depTraverser = new FatArtifactTraverser();
        session.setDependencyTraverser(depTraverser);

        DependencyManager depManager = new ClassicDependencyManager();
        session.setDependencyManager(depManager);

        DependencySelector depFilter = new AndDependencySelector(new ScopeDependencySelector("test", "provided"), new OptionalDependencySelector(), new ExclusionDependencySelector());
        session.setDependencySelector(depFilter);

        DependencyGraphTransformer transformer = new ConflictResolver(new NearestVersionSelector(), new JavaScopeSelector(), new SimpleOptionalitySelector(), new JavaScopeDeriver());
        transformer = new ChainedDependencyGraphTransformer(transformer, new JavaDependencyContextRefiner());
        session.setDependencyGraphTransformer(transformer);

        DefaultArtifactTypeRegistry stereotypes = new DefaultArtifactTypeRegistry();
        stereotypes.add(new DefaultArtifactType("pom"));
        stereotypes.add(new DefaultArtifactType("maven-plugin", "jar", "", "java"));
        stereotypes.add(new DefaultArtifactType("jar", "jar", "", "java"));
        stereotypes.add(new DefaultArtifactType("ejb", "jar", "", "java"));
        stereotypes.add(new DefaultArtifactType("ejb-client", "jar", "client", "java"));
        stereotypes.add(new DefaultArtifactType("test-jar", "jar", "tests", "java"));
        stereotypes.add(new DefaultArtifactType("javadoc", "jar", "javadoc", "java"));
        stereotypes.add(new DefaultArtifactType("java-source", "jar", "sources", "java", false, false));
        stereotypes.add(new DefaultArtifactType("war", "war", "", "java", false, true));
        stereotypes.add(new DefaultArtifactType("ear", "ear", "", "java", false, true));
        stereotypes.add(new DefaultArtifactType("rar", "rar", "", "java", false, true));
        stereotypes.add(new DefaultArtifactType("par", "par", "", "java", false, true));
        session.setArtifactTypeRegistry(stereotypes);

        session.setArtifactDescriptorPolicy(new SimpleArtifactDescriptorPolicy(true, true));

        final Properties systemProperties = new Properties();

        Properties sysProp = System.getProperties();
        synchronized (sysProp) {
            systemProperties.putAll(sysProp);
        }

        session.setSystemProperties(systemProperties);
        session.setConfigProperties(systemProperties);

        return session;
    }

}

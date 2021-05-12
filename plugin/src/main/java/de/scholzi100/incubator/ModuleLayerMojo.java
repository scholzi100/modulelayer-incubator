package de.scholzi100.incubator;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import org.apache.maven.repository.internal.MavenRepositorySystemUtils;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.*;
import org.eclipse.aether.util.artifact.JavaScopes;

import java.io.File;
import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Incubator for creating a maven metadata of dependencies
 */
@Mojo(name = "moduleLayer", defaultPhase = LifecyclePhase.COMPILE)
public class ModuleLayerMojo extends AbstractMojo {

    @Parameter(property = "project", required = true, readonly = true)
    private MavenProject project;

    @Component
    private RepositorySystem repositorySystem;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true)
    private RepositorySystemSession repositorySystemSession;

    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true)
    protected List<RemoteRepository> remoteRepositories;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {

        Artifact artifact = new DefaultArtifact( "com.fasterxml.jackson.core:jackson-databind:2.12.3" );

        final var repositories = newRepositories(repositorySystem, repositorySystemSession);

        DependencyFilter classpathFlter = (node, parents) -> {
            Dependency dependency = node.getDependency();
            if (dependency == null) {
                return true;
            } else {
                String scope = node.getDependency().getScope();
                return scope.equals( JavaScopes.COMPILE);
            }
        };

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot( new Dependency( artifact, JavaScopes.COMPILE ) );
        collectRequest.setRepositories( repositories );

        DependencyRequest dependencyRequest = new DependencyRequest( collectRequest, classpathFlter );

        DependencyResult artifactResults = null;
        try {
            artifactResults = repositorySystem.resolveDependencies( repositorySystemSession, dependencyRequest );
        } catch (DependencyResolutionException e) {
            e.printStackTrace();
        }



        getLog().info("Plugin started!");


        final var paths = artifactResults.getArtifactResults().stream().map(ArtifactResult::getArtifact).map(Artifact::getFile).map(File::toPath).toArray(Path[]::new);

        getLog().info(""+ Arrays.toString(paths));






    }

    private void printClassLoader(String current, ClassLoader classLoader) {
        getLog().info(current + "\n" + classLoader.toString() + "\n" + classLoader.getName());
    }

    private final void printModuleLayers(String name, ModuleLayer moduleLayer) {
        getLog().info(name + ": \n"+ getModules(moduleLayer));
    }

    private String getModules(ModuleLayer moduleLayer) {
        if (moduleLayer == null) {
            return "<empty>";
        }
        final var modules = moduleLayer.modules();
        return modules.stream()
                .map(Module::getDescriptor)
                .map(ModuleDescriptor::toNameAndVersion)
                .collect(Collectors.joining("\n"));
    }
    public static DefaultRepositorySystemSession newRepositorySystemSession(RepositorySystem system )
    {
        DefaultRepositorySystemSession session = MavenRepositorySystemUtils.newSession();

        LocalRepository localRepo = new LocalRepository( "target/local-repo" );
        try {
            Files.createDirectories(Path.of("target/local-repo"));
        } catch (IOException e) {
            e.printStackTrace();
        }
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );

        return session;
    }

    public static List<RemoteRepository> newRepositories(RepositorySystem system, RepositorySystemSession session )
    {
        return new ArrayList<>( Collections.singletonList( newCentralRepository() ) );
    }

    private static RemoteRepository newCentralRepository()
    {
        return new RemoteRepository.Builder( "central", "default", "https://repo.maven.apache.org/maven2/" ).build();
    }

    /**
     * Creates a new filter that selects dependencies whose scope matches one or more of the specified classpath types.
     * A classpath type is a set of scopes separated by either {@code ','} or {@code '+'}.
     *
     * @param classpathTypes The classpath types, may be {@code null} or empty to match no dependency.
     * @return The new filter, never {@code null}.
     * @see JavaScopes
     */
    public static DependencyFilter classpathFilter( Collection<String> classpathTypes )
    {
        Collection<String> types = new HashSet<>();

        if ( classpathTypes != null )
        {
            for ( String classpathType : classpathTypes )
            {
                String[] tokens = classpathType.split( "[+,]" );
                for ( String token : tokens )
                {
                    token = token.trim();
                    if ( token.length() > 0 )
                    {
                        types.add( token );
                    }
                }
            }
        }

        Collection<String> included = new HashSet<>();
        for ( String type : types )
        {
            if ( JavaScopes.COMPILE.equals( type ) )
            {
                Collections.addAll( included, JavaScopes.COMPILE, JavaScopes.PROVIDED, JavaScopes.SYSTEM );
            }
            else if ( JavaScopes.RUNTIME.equals( type ) )
            {
                Collections.addAll( included, JavaScopes.COMPILE, JavaScopes.RUNTIME );
            }
            else if ( JavaScopes.TEST.equals( type ) )
            {
                Collections.addAll( included, JavaScopes.COMPILE, JavaScopes.PROVIDED, JavaScopes.SYSTEM,
                        JavaScopes.RUNTIME, JavaScopes.TEST );
            }
            else
            {
                included.add( type );
            }
        }

        Collection<String> excluded = new HashSet<>();
        Collections.addAll( excluded, JavaScopes.COMPILE, JavaScopes.PROVIDED, JavaScopes.SYSTEM, JavaScopes.RUNTIME,
                JavaScopes.TEST );
        excluded.removeAll( included );

        return new ScopeDependencyFilter( null, excluded );
    }

}

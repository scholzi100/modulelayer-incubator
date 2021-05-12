package de.scholzi100.incubator.resolver.modulefinder;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

import de.scholzi100.incubator.resolver.modulefinder.provider.RepoResolver;
import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.LocalRepository;
import org.eclipse.aether.repository.RemoteRepository;

/**
 * Provide some default resolver functionality
 */
public class RepoResolverOverlay
{

    private final RepoResolver repoResolver;

    private RepoResolverOverlay(RepoResolver repoResolver) {
        this.repoResolver = repoResolver;
    }

    public static RepoResolverOverlay of(ModuleLayer moduleLayer, URI uri){
        final var repoResolver = findRepoResolver(moduleLayer, uri).orElseThrow(IllegalArgumentException::new);
        return new RepoResolverOverlay(repoResolver);
    }

    public static RepoResolverOverlay of(ModuleLayer moduleLayer, String uri) throws IllegalArgumentException{
        final URI realURI;
        try {
            realURI = new URI(uri);
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
        final var repoResolver = findRepoResolver(moduleLayer, realURI).orElseThrow(IllegalArgumentException::new);
        return new RepoResolverOverlay(repoResolver);
    }

    private static Optional<RepoResolver> findRepoResolver(ModuleLayer moduleLayer, URI uri) {
        return ServiceLoader.load(moduleLayer, RepoResolver.class)
                .stream()
                .map(ServiceLoader.Provider::get)
                .filter(repoResolver -> repoResolver.getURI().equals(uri))
                .findFirst();
    }

    public RepositorySystem newRepositorySystem( )
    {
        return repoResolver.newRepositorySystem();
    }

    public RepositorySystemSession newRepositorySystemSession(RepositorySystem system )
    {
        DefaultRepositorySystemSession session = repoResolver.newSession();

        LocalRepository localRepo = new LocalRepository( "target/local-repo" );
        session.setLocalRepositoryManager( system.newLocalRepositoryManager( session, localRepo ) );

//        session.setTransferListener( new ConsoleTransferListener() );
//        session.setRepositoryListener( new ConsoleRepositoryListener() );

        // uncomment to generate dirty trees
        // session.setDependencyGraphTransformer( null );

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

}

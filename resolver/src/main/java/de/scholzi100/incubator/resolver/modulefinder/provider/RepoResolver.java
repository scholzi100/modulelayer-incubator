package de.scholzi100.incubator.resolver.modulefinder.provider;

import org.eclipse.aether.DefaultRepositorySystemSession;
import org.eclipse.aether.RepositorySystem;

import java.net.URI;

public interface RepoResolver {

    URI getURI();

    RepositorySystem newRepositorySystem();

    DefaultRepositorySystemSession newSession();

}

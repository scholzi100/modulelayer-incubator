package de.scholzi100.incubator.resolver.modulefinder.providers;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.VersionRangeResolver;
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResolutionException;
import org.eclipse.aether.resolution.VersionRangeResult;

public class DefaultVersionRangeResolver implements VersionRangeResolver {
    @Override
    public VersionRangeResult resolveVersionRange(RepositorySystemSession repositorySystemSession, VersionRangeRequest versionRangeRequest) throws VersionRangeResolutionException {
        throw new UnsupportedOperationException();
    }
}

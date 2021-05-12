package de.scholzi100.incubator.resolver.modulefinder.providers;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.VersionResolver;
import org.eclipse.aether.resolution.VersionRequest;
import org.eclipse.aether.resolution.VersionResolutionException;
import org.eclipse.aether.resolution.VersionResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultVersionResolver implements VersionResolver {

    private final Logger LOGGER = LoggerFactory.getLogger(DefaultVersionResolver.class);

    @Override
    public VersionResult resolveVersion(RepositorySystemSession repositorySystemSession, VersionRequest versionRequest) throws VersionResolutionException {
        final var versionResult = new VersionResult(versionRequest);
        versionResult.setVersion(versionRequest.getArtifact().getVersion());
        LOGGER.trace("resolveVersion {} - {} - {}", repositorySystemSession, versionRequest, versionResult);
        return versionResult;
    }
}

package de.scholzi100.incubator.resolver.modulefinder.providers;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.impl.ArtifactDescriptorReader;
import org.eclipse.aether.resolution.ArtifactDescriptorException;
import org.eclipse.aether.resolution.ArtifactDescriptorRequest;
import org.eclipse.aether.resolution.ArtifactDescriptorResult;

public class DefaultArtifactDescriptorReader implements ArtifactDescriptorReader {
    @Override
    public ArtifactDescriptorResult readArtifactDescriptor(RepositorySystemSession repositorySystemSession, ArtifactDescriptorRequest artifactDescriptorRequest) throws ArtifactDescriptorException {
        throw new UnsupportedOperationException();
    }
}

package de.scholzi100.incubator.resolver.modulefinder.providers;

import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.deployment.DeployRequest;
import org.eclipse.aether.impl.MetadataGenerator;
import org.eclipse.aether.impl.MetadataGeneratorFactory;
import org.eclipse.aether.installation.InstallRequest;

public class VersionsMetadataGeneratorFactory implements MetadataGeneratorFactory {
    @Override
    public MetadataGenerator newInstance(RepositorySystemSession repositorySystemSession, InstallRequest installRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public MetadataGenerator newInstance(RepositorySystemSession repositorySystemSession, DeployRequest deployRequest) {
        throw new UnsupportedOperationException();
    }

    @Override
    public float getPriority() {
        throw new UnsupportedOperationException();
    }
}

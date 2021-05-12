package de.scholzi100.incubator.resolver.modulefinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleReader;
import java.lang.module.ModuleReference;
import java.net.URI;
import java.util.StringJoiner;

public class MavenModuleReference extends ModuleReference {

    private final Logger LOGGER = LoggerFactory.getLogger(MavenModuleReference.class);

    public MavenModuleReference(ModuleDescriptor descriptor, URI location) {
        super(descriptor, location);
    }

    @Override
    public ModuleReader open() throws IOException {
        LOGGER.trace("open {}", this);
        return new MavenModuleReader();
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MavenModuleReference.class.getSimpleName() + "[", "]")
                .toString();
    }
}

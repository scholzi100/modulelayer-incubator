package de.scholzi100.incubator.modulenamefinder;

import java.lang.module.ModuleDescriptor;
import java.nio.file.Path;
import java.util.Optional;

public interface ModuleNameResolver {

    Optional<ModuleDescriptor> getModuleDescriptorByPath(Path path);

}

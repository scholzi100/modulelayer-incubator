package de.scholzi100.incubator.resolver.modulefinder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class MavenModuleFinder implements ModuleFinder {

    private final Logger LOGGER = LoggerFactory.getLogger(MavenModuleFinder.class);

    private final Map<String, String> map;

    private MavenModuleFinder(Map<String, String> map) {
        this.map = map;
    }

    public static ModuleFinder of(Map<String, String> map){
        return new MavenModuleFinder(map);
    }

    @Override
    public Optional<ModuleReference> find(String name) {
        return Optional.ofNullable(name)
                .filter(map::containsKey)
                .flatMap(this::readModuleDescriptor)
                .flatMap(this::createModuleReference);
    }

    @Override
    public Set<ModuleReference> findAll() {
        return map.keySet().stream()
                .map(this::readModuleDescriptor)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(this::createModuleReference)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }


    private Optional<MavenModuleReference> createModuleReference(ModuleDescriptor moduleDescriptor) {
        LOGGER.trace("Creating ModuleReference from {}", moduleDescriptor);
        final var mavenModuleReference = new MavenModuleReference(moduleDescriptor, null);
        return Optional.of(mavenModuleReference);
    }

    private Optional<ModuleDescriptor> readModuleDescriptor(String moduleName) {

        //Lookup module relation

        return Optional.of(ModuleDescriptor.newModule(moduleName)
                .requires("java.base")
                .opens("de.scholzi100.test")
                .build());

//        ByteBuffer bb = null;
//        final ModuleDescriptor moduleDescriptor;
//        try {
//            moduleDescriptor = ModuleDescriptor.read(bb);
//            return Optional.of(moduleDescriptor);
//        } catch (InvalidModuleDescriptorException e) {
//            LOGGER.trace("Unable to lookup ModuleDescriptor for {} because {}", moduleName, e);
//            return Optional.empty();
//        }
    }

}

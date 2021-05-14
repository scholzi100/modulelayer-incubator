package de.scholzi100.incubator.modulenamefinder;

import de.scholzi100.incubator.modulenamefinder.ModuleNameResolver;

import java.io.IOException;
import java.io.InputStream;
import java.lang.module.ModuleDescriptor;
import java.lang.module.ModuleFinder;
import java.lang.module.ModuleReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.Optional;
import java.util.jar.Attributes;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.zip.ZipFile;

public class ModuleNameResolverImpl implements ModuleNameResolver {

    @Override
    public Optional<ModuleDescriptor> getModuleDescriptorByPath(Path path) {

        //Lookup Module-Info
        // Whats about multi-release-jars?
        // Whats about automatic module
        // - Automatic-Module-Name
        // - file name derived

        Objects.requireNonNull(path);
        if (Files.notExists(path)) {
//            throw new IllegalArgumentException("file not found");
            return Optional.empty();
        }
        if (!Files.isRegularFile(path)){
//            throw new IllegalArgumentException("file not a regular file");
            return Optional.empty();
        }

        try (final var jarFile = new JarFile(path.toFile(), false, ZipFile.OPEN_READ, Runtime.Version.parse("11"))){
            final var multiRelease = jarFile.isMultiRelease();
            final var moduleInfo = jarFile.getJarEntry("module-info.class");
            if (moduleInfo == null){
                final var mainAttributes = jarFile.getManifest().getMainAttributes().getValue("Automatic-Module-Name");
                if (mainAttributes == null){
                    return Optional.empty();
                }
                return Optional.of(ModuleDescriptor.newAutomaticModule(mainAttributes).build());
            }
            System.out.println(jarFile.getVersion());
            try(final var inputStream = jarFile.getInputStream(moduleInfo)) {

                return Optional.ofNullable(ModuleDescriptor.read(inputStream));
            }
            //isMultiRelease is can also be gated by some internal jdk properties
            //"jdk.util.jar.enableMultiRelease"
            //TODO check if there are multiple versions of module-info registered
        } catch (IOException e) {
            // ignore lookup, could indicate this is not a jar
        }


        final var moduleReferences = ModuleFinder.of(path).findAll();
        if (moduleReferences.isEmpty()) {
//            throw new IllegalArgumentException("no module found");
            return Optional.empty();
        }
        if (moduleReferences.size() > 1 ){
//            throw new IllegalArgumentException("found more then one module");
            return Optional.empty();
        }

        return moduleReferences.stream().map(ModuleReference::descriptor).findFirst();
    }

}

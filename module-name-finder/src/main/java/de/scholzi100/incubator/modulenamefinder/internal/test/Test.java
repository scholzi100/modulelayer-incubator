package de.scholzi100.incubator.modulenamefinder.internal.test;

import de.scholzi100.incubator.modulenamefinder.ModuleNameResolverImpl;

import java.lang.module.ModuleDescriptor;
import java.nio.file.Path;

public class Test {

    public static void main(String[] args) {
        final var moduleNameResolver = new ModuleNameResolverImpl();
        final var first = "/home/scholzi100/.m2/repository/org/apache/logging/log4j/log4j-api/2.14.1/log4j-api-2.14.1.jar";
        final var moduleDescriptor = moduleNameResolver.getModuleDescriptorByPath(Path.of(first));
        if (moduleDescriptor.isPresent()) {
            final var moduleName = moduleDescriptor.get();
            System.out.println("isAutomatic: "+moduleName.isAutomatic());
            System.out.println("isOpen: "+moduleName.isOpen());
            System.out.println("mainClass: "+moduleName.mainClass().orElse("none"));
            System.out.println("name: "+moduleName.name());
            System.out.println("version:"+moduleName.version().map(ModuleDescriptor.Version::toString).orElse("none"));
            System.out.println("rawVersion: "+moduleName.rawVersion().orElse("none"));
            System.out.println("modifiers: "+moduleName.modifiers().toString());
        }else{
            System.out.println("No moduleDescriptor found!");
        }
    }

}

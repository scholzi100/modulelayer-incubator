import de.scholzi100.incubator.resolver.modulefinder.provider.RepoResolver;

module de.scholzi100.incubator.resolver {
    uses RepoResolver;
    requires org.slf4j;
    requires org.apache.maven.resolver;
    requires org.apache.maven.resolver.util;
    requires org.apache.logging.log4j;

    requires de.scholzi100.incubator.modulenamefinder;
    uses de.scholzi100.incubator.modulenamefinder.ModuleNameResolver;

    exports de.scholzi100.incubator.resolver.modulefinder.provider;

}
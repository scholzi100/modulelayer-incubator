import de.scholzi100.incubator.resolver.modulefinder.provider.RepoResolver;

module de.scholzi100.incubator.resolver {
    uses RepoResolver;
    requires org.slf4j;
    requires org.apache.maven.resolver;
    requires org.apache.maven.resolver.util;
    requires org.apache.logging.log4j;
    exports de.scholzi100.incubator.resolver.modulefinder.provider;

}
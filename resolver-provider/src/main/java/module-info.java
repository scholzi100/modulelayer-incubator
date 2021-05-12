module de.scholzi100.incubator.resolver.provider {

    requires de.scholzi100.incubator.resolver;
    //required most of those because they are "automatic"-modules
    requires org.apache.maven.resolver;
    requires org.apache.maven.resolver.impl;
    requires org.apache.maven.resolver.spi;
    requires org.apache.maven.resolver.util;
    requires org.apache.maven.resolver.transport.http;
    requires org.apache.maven.resolver.transport.file;
    requires org.apache.maven.resolver.connector.basic;
    requires org.apache.logging.log4j;
    requires org.apache.logging.log4j.slf4j;
    requires org.apache.logging.log4j.core;
    requires org.slf4j;

    // provide RepoResolver service implementation
    provides de.scholzi100.incubator.resolver.modulefinder.provider.RepoResolver
            with de.scholzi100.incubator.resolver.modulefinder.RepoResolverImpl;

    // Allow maven-resolver-impl to access de.scholzi100.incubator.resolver.modulefinder.providers
    exports de.scholzi100.incubator.resolver.modulefinder.providers to org.apache.maven.resolver.impl;

}
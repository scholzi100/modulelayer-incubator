module de.scholzi100.incubator.mavenpacked {

    exports org.apache.maven.repository.internal
            to de.scholzi100.incubator.resolver.provider;

    requires javax.inject;
    requires org.eclipse.sisu.inject;
    requires org.apache.commons.lang3;
    requires org.slf4j;
    requires plexus.utils;
    requires plexus.interpolation;
    requires org.apache.maven.resolver.impl;
    requires org.apache.maven.resolver;

}
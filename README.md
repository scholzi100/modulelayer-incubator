# ModuleLayer isolation over Maven-Artifact-Resolving


## Idea
Allowing Java-Apps to resolve its (module-)dependencies 
at or before runtime and isolating those using ModuleLayers.

This could be done by using [maven-resolver-api](https://github.com/apache/maven-resolver).
Isolating application layers in ModuleLayers,
this can enable using the same artifact with multiple version on the same JVM.

Creating a module-name to artifact mapping maybe using a Maven plugin. 
This could also be used to store dependency checksums to be checked later,
when ModuleLayers are loaded.

## Inspirations
- [Escaping The Jar Hell With Jigsaw Layers, Nikita Lipsky](https://www.youtube.com/watch?v=s3o5sY97m10)
- [microbean maven-cdi](https://github.com/microbean/microbean-maven-cdi)
- [microbean launcher](https://github.com/microbean/microbean-launcher)

## Related foreign project issues
- [APACHE-MNG-7037 - Add JPMS support -> solve split packages problem](https://issues.apache.org/jira/browse/MNG-7037)
- [APACHE-LOG4J2-2463 - ClassNotFoundException when log4j2 is used with slf4j on module path in java 11](https://issues.apache.org/jira/browse/LOG4J2-2463)
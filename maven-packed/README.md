# Maven Packed

This module includes all necessary maven-core modules required for
maven-resolve-api. This is clearly a "dirty" hack,
but since this does only export one "shaded" package
to one module which uses it in a isolated module layer,
this should be ok.

This works around split-packages in core maven modules, also see
[APACHE-MNG-7037 - Add JPMS support -> solve split packages problem](https://issues.apache.org/jira/browse/MNG-7037)

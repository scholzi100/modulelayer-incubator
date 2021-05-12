package de.scholzi100.incubator.resolver.modulefinder.test;

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

import de.scholzi100.incubator.resolver.modulefinder.RepoResolverOverlay;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.collection.CollectRequest;
import org.eclipse.aether.graph.Dependency;
import org.eclipse.aether.graph.DependencyFilter;
import org.eclipse.aether.resolution.ArtifactResult;
import org.eclipse.aether.resolution.DependencyRequest;
import org.eclipse.aether.util.artifact.JavaScopes;

import java.util.List;

/**
 * Resolves the transitive (compile) dependencies of an artifact.
 */
public class ResolveTransitiveDependencies
{

    /**
     * Main.
     * @param args
     * @throws Exception
     */
    public static void main( String[] args )
        throws Exception
    {
        System.out.println( "------------------------------------------------------------" );
        System.out.println( ResolveTransitiveDependencies.class.getSimpleName() );

        final RepoResolverOverlay resolverOverlay = TestMain.getRepoResolverOverlay(ModuleLayer.boot(), "modulelayer:default", ClassLoader.getSystemClassLoader());

        RepositorySystem system = resolverOverlay.newRepositorySystem();

        RepositorySystemSession session = resolverOverlay.newRepositorySystemSession( system );

        Artifact artifact = new DefaultArtifact( "org.apache.maven.resolver:maven-resolver-impl:1.3.3" );

        DependencyFilter classpathFilter = (node, parents) -> {
            Dependency dependency = node.getDependency();
            if (dependency == null) {
                return true;
            } else {
                String scope = node.getDependency().getScope();
                return scope.equals( JavaScopes.COMPILE);
            }
        };

        CollectRequest collectRequest = new CollectRequest();
        collectRequest.setRoot( new Dependency( artifact, JavaScopes.COMPILE) );
        collectRequest.setRepositories( RepoResolverOverlay.newRepositories( system, session ) );

        DependencyRequest dependencyRequest = new DependencyRequest( collectRequest, classpathFilter );

        List<ArtifactResult> artifactResults =
            system.resolveDependencies( session, dependencyRequest ).getArtifactResults();

        for ( ArtifactResult artifactResult : artifactResults )
        {
            System.out.println( artifactResult.getArtifact() + " resolved to "
                + artifactResult.getArtifact().getFile() );
        }
    }

}

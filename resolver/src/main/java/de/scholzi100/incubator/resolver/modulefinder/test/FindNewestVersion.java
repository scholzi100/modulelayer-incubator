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
import org.eclipse.aether.resolution.VersionRangeRequest;
import org.eclipse.aether.resolution.VersionRangeResult;
import org.eclipse.aether.version.Version;

/**
 * Determines the newest version of an artifact.
 */
public class FindNewestVersion
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
        System.out.println( FindNewestVersion.class.getSimpleName() );

        final RepoResolverOverlay resolverOverlay = TestMain.getRepoResolverOverlay(ModuleLayer.boot(), "modulelayer:default", ClassLoader.getSystemClassLoader());

        RepositorySystem system = resolverOverlay.newRepositorySystem();

        RepositorySystemSession session = resolverOverlay.newRepositorySystemSession( system );

        Artifact artifact = new DefaultArtifact( "org.apache.maven.resolver:maven-resolver-util:[0,)" );

        VersionRangeRequest rangeRequest = new VersionRangeRequest();
        rangeRequest.setArtifact( artifact );
        rangeRequest.setRepositories( RepoResolverOverlay.newRepositories( system, session ) );

        VersionRangeResult rangeResult = system.resolveVersionRange( session, rangeRequest );

        Version newestVersion = rangeResult.getHighestVersion();

        System.out.println( "Newest version " + newestVersion + " from repository "
            + rangeResult.getRepository( newestVersion ) );
    }

}

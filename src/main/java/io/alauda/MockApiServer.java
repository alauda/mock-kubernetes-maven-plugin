package io.alauda;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.server.mock.KubernetesServer;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.InetAddress;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Goal which touches a timestamp file.
 *
 * @goal touch
 * 
 * @phase process-sources
 */
@Mojo(name = "mock")
public class MockApiServer
    extends AbstractMojo
{
    @Parameter(defaultValue="${project.basedir}")
    private File basedir;

    @Parameter(name = "resources")
    private List<String> resources;

    @Parameter(name = "port", defaultValue = "8443")
    private int port;

    public void execute()
        throws MojoExecutionException
    {
        File f = basedir;

        if ( !f.exists() )
        {
            f.mkdirs();
        }

        KubernetesServer server = new KubernetesServer(
                true,
                true,
                InetAddress.getLoopbackAddress(),
                port,
                Collections.emptyList());
        server.before();

        KubernetesClient client = server.getClient();

        if(resources == null){
            resources = new ArrayList<>();
        }

        if(resources.size() == 0){
            resources.add(Paths.get(basedir.toString()).resolve("kubernetes").toString());
        }

        for(String resPath:resources){
            try {
                loadResources(client,new File(resPath));
            } catch (FileNotFoundException e) {
                throw new MojoExecutionException("Failed to load resources!",e);
            }
        }

        System.out.println("Press any key to stop the server");
        byte name[] = new byte[100];
        try {
            System.in.read(name);
        } catch (IOException e) {
            throw new MojoExecutionException("Failed to read stdin!",e);
        }
    }

    private void loadResources(KubernetesClient client, File file) throws FileNotFoundException {
        if(file.isDirectory()){
            File[] files = file.listFiles();
            for (File f :files) {
                loadResources(client,f);
            }
        }else if(isYamlFile(file.getName())){
            client.load(new FileInputStream(file.getAbsolutePath())).createOrReplace();
        }
    }

    private boolean isYamlFile(String fileName){
        return fileName.toLowerCase().endsWith("yaml") || fileName.toLowerCase().endsWith("yml");
    }
}

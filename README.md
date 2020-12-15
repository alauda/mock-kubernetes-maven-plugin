# mock-kubernetes-maven-plugin
Mock Api Server For Local Development With [Spring Cloud Kubernetes](https://cloud.spring.io/spring-cloud-kubernetes/)

Spring Cloud Kubernetes provided discovery and config server with Native Kubernetes. But it's hard to debug microservice locally. Neither [Minikube](https://minikube.sigs.k8s.io/) nor [K3S](https://www.rancher.cn/k3s/) is simple solution.
Mock Kubernets Maven Plugin make it more easily. Just plugin configuration and native kubernetes yaml files are required.
Bootstrap with mvn command, Local microservice will fetch configmap、secret even discovery without any change of your code.
It's better to work together with [generator-asf](https://github.com/alauda/generator-asf).



####  Instructions


1. Put kubernetes resources(yaml files) in directory called "kubernetes" under project root directory.

2. For local resources won't be pushed to git repository, put them in directory called ".local" under project root directory. Then you should append '.local/' in your .gitignore file.

3. Edit pom.xml of your project to append mock-kubernetes-maven-plugin:

   ```xml
   <build>
     <plugins>
       <plugin>
         <groupId>io.alauda</groupId>
         <artifactId>mock-kubernetes-maven-plugin</artifactId>
         <version>1.0.1-RELEASE</version>
       </plugin>
     </plugins>
   </build>
   ```

4. Execute command in shell:

   ```shell
   mvn mock-kubernetes:mock
   ```

Then mock api server will bootstrap.It will accept any key for termination.



#### Configuration

Plugin will search yaml files recursion in "kubernetes" directory. Specify multiple directory is optional:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>io.alauda</groupId>
      <artifactId>mock-kubernetes-maven-plugin</artifactId>
      <version>1.0.1-RELEASE</version>
      <configuration>
        <resources>
          <resource>kubernetes</resource>
          <resource>services</resource>
        </resources>
      </configuration>
    </plugin>
  </plugins>
</build>
```

Mock server will startup at 0.0.0.0:8443, port is alternate in configuration:

```xml
<build>
  <plugins>
    <plugin>
      <groupId>io.alauda</groupId>
      <artifactId>mock-kubernetes-maven-plugin</artifactId>
      <version>1.0.1-RELEASE</version>
      <configuration>
        <port>8999</port>
      </configuration>
    </plugin>
  </plugins>
</build>
```

#### Mock Kubernetes Discovery With Spring Cloud

For remote micoservice debuging, you should use Service And Endpoints resources with NodePort address in Kubernetes:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: user-service
---
apiVersion: v1
kind: Endpoints
metadata:
  name: user-service
subsets:
  - addresses:
      - ip: 10.3.200.33
    ports:
      - port: 31101
```



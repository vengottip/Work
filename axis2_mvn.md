<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.example</groupId>
    <artifactId>axis2-client-example</artifactId>
    <version>1.0.0</version>

    <properties>
        <!-- Specify the Axis2 version -->
        <axis2.version>1.7.9</axis2.version>
        <!-- Specify the Maven Axis2 plugin version -->
        <axis2.plugin.version>1.7.9</axis2.plugin.version>
    </properties>

    <build>
        <plugins>
            <plugin>
                <groupId>org.apache.axis2</groupId>
                <artifactId>axis2-wsdl2code-maven-plugin</artifactId>
                <version>${axis2.plugin.version}</version>
                <executions>
                    <execution>
                        <goals>
                            <goal>wsdl2code</goal>
                        </goals>
                        <configuration>
                            <!-- WSDL file location -->
                            <wsdlFile>src/main/resources/YourWebService.wsdl</wsdlFile>
                            <!-- Package name for the generated code -->
                            <packageName>com.example.axis2client</packageName>
                            <!-- Output directory for the generated code -->
                            <outputDirectory>target/generated-sources/axis2/wsdl2code</outputDirectory>
                            <!-- Specify additional options if needed -->
                            <!-- <options>
                                <option>someOption=value</option>
                            </options> -->
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>

    <dependencies>
        <!-- Axis2 dependencies -->
        <dependency>
            <groupId>org.apache.axis2</groupId>
            <artifactId>axis2-adb</artifactId>
            <version>${axis2.version}</version>
        </dependency>
        <dependency>
            <groupId>org.apache.axis2</groupId>
            <artifactId>axis2-kernel</artifactId>
            <version>${axis2.version}</version>
        </dependency>
        <!-- Add other dependencies as needed -->
    </dependencies>
</project>

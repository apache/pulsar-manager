/**
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.pulsar.manager;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.ServletException;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.Context;
import org.apache.catalina.loader.WebappLoader;
import org.apache.catalina.startup.Tomcat;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.embedded.tomcat.TomcatWebServer;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

/**
 * Customize Tomcat Service
 */
@Component
@Slf4j
public class EmbeddedTomcatCustomizer implements
        WebServerFactoryCustomizer<ConfigurableServletWebServerFactory> {

    @Override
    public void customize(ConfigurableServletWebServerFactory factory) {
        log.info("Starting Tomcat Customizer");
    }

    @Bean
    public ServletWebServerFactory servletContainer() {
        log.info("Starting servletContainer");
        return new TomcatServletWebServerFactory() {
            @Override
            protected TomcatWebServer getTomcatWebServer(Tomcat tomcat) {
                try {
                    log.info("Catalina base is " + tomcat.getServer().getCatalinaBase().getAbsolutePath());
                    File lib = new File("lib").getAbsoluteFile();
                    if (lib.isDirectory()) {
                        File bkvmWar = searchWar(lib, "bkvm", ".war");
                        if (bkvmWar != null) {
                            File configFile = new File("bkvm.conf");
                            log.info("looking for BKVM configuration file at " + configFile.getAbsolutePath());
                            if (configFile.isFile()) {
                                Properties props = new Properties();
                                try (FileReader reader = new FileReader(configFile)) {
                                    props.load(reader);
                                }
                                boolean bkvmEnabled = Boolean.parseBoolean(props.getProperty("bkvm.enabled", "false"));
                                log.info("Read bkvm.enabled = {}", bkvmEnabled);
                                if (bkvmEnabled) {
                                    System.setProperty("bookkeeper.visual.manager.config.path", configFile.getAbsolutePath());
                                    File file = new File(tomcat.getServer().getCatalinaBase(), "/webapps");
                                    log.info("Tomcat Webapps directory is " + file.getAbsolutePath());
                                    file.mkdirs();
                                    File bkvmDirectory = new File(file, "bkvm");
                                    log.info("Deploying BKVM to " + bkvmDirectory.getAbsolutePath());
                                    unZip(bkvmWar, bkvmDirectory);
                                    Context context = tomcat.addWebapp("/bkvm", bkvmDirectory.getAbsolutePath());
                                    WebappLoader loader = new WebappLoader(Thread.currentThread().getContextClassLoader());
                                    context.setLoader(loader);
                                }
                            }
                        }
                    }
                    return super.getTomcatWebServer(tomcat);
                } catch (IOException | ServletException ex) {
                    throw new RuntimeException(ex);
                }
            }

        };
    }

    private static File searchWar(File directory, String prefix, String ext) throws IOException {
        log.info("looking for " + prefix + " into " + directory.getAbsolutePath());
        Path path = directory.toPath();
        try (DirectoryStream<Path> list = Files.newDirectoryStream(path);) {
            for (Path o : list) {
                if (o.getFileName().toString().startsWith(prefix) && o.getFileName().toString().endsWith(ext)) {
                    return o.toFile();
                }

            }
        }
        log.info("Cannot find any file whose name starts with " + prefix + " into " + directory.getAbsolutePath());
        return null;
    }

    public static List<File> unZip(File war, File outDir) throws IOException {
        log.info("Unzipping " + war.getAbsolutePath() + " to " + outDir);
        try (ZipInputStream zipStream = new ZipInputStream(new FileInputStream(war), StandardCharsets.UTF_8);) {
            ZipEntry entry = zipStream.getNextEntry();
            List<File> listFiles = new ArrayList<>();
            while (entry != null) {
                if (entry.isDirectory()) {
                    entry = zipStream.getNextEntry();
                    continue;
                }

                String normalized = entry.getName();
                File outFile = new File(outDir, normalized);
                File parentDir = outFile.getParentFile();
                if (parentDir != null && !parentDir.isDirectory()) {
                    Files.createDirectories(parentDir.toPath());
                }

                listFiles.add(outFile);
                try (FileOutputStream out = new FileOutputStream(outFile);
                        BufferedOutputStream oo = new BufferedOutputStream(out)) {
                    copyStream(zipStream, oo);
                }
                entry = zipStream.getNextEntry();

            }
            return listFiles;
        } catch (IllegalArgumentException ex) {
            throw new IOException(ex);
        }

    }

    private static long copyStream(InputStream input, OutputStream output) throws IOException {
        long count = 0;
        int n = 0;
        byte[] buffer = new byte[64 * 1024];
        while (-1 != (n = input.read(buffer))) {
            output.write(buffer, 0, n);
            count += n;
        }
        return count;
    }
}

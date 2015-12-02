/*
 * Copyright (c) 2015 Fraunhofer FOKUS
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.openbaton.nfvo.cli;

import jline.console.ConsoleReader;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.FileNameCompleter;
import jline.console.completer.StringsCompleter;
import org.openbaton.nfvo.repositories.ConfigurationRepository;
import org.openbaton.nfvo.repositories.PluginEndpointRepository;
import org.openbaton.plugin.utils.PluginStartup;
import org.openbaton.utils.rabbit.RabbitManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.*;

/**
 * A Bridge for either executing the openbaton shell standalone or in an existing
 * spring boot environment that either leads to calls of the main() or the run()
 * method.
 */
@Component
@Order(Ordered.LOWEST_PRECEDENCE)
@ConfigurationProperties(prefix = "nfvo.rabbit")
public class OpenbatonCLI implements CommandLineRunner, ApplicationEventPublisherAware {

    private final static Map<String, String> helpCommandList = new HashMap<String, String>() {{
        put("help", "Print the usage");
        put("exit", "Exit the application");
        put("installPlugin", "install a plugin");
        put("listPlugins", "list all registered plugin");
    }};
    protected Logger log = LoggerFactory.getLogger(this.getClass());
    private String brokerIp;
    @Value("${spring.rabbitmq.username:}")
    private String username;
    @Value("${spring.rabbitmq.password:}")
    private String password;
    @Value("${nfvo.rabbit.management.port:}")
    private String port;

    public String getPort() {
        return port;
    }

    public void setPort(String port) {
        this.port = port;
    }
    @Autowired
    private PluginEndpointRepository pluginEndpointRepository;
    private ApplicationEventPublisher publisher;
    @Autowired
    private ConfigurationRepository configurationRepository;

    private static void exit(int status) {
        System.exit(status);
    }

    public static void usage() {
        System.out.println("/~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~/");
        System.out.println("Usage: java -jar build/libs/openbaton-<$version>.jar");
        System.out.println("Available commands are");
        for (Object entry : helpCommandList.entrySet()) {
            System.out.println("\t" + ((Map.Entry) entry).getKey() + ":\t" + ((Map.Entry) entry).getValue());
        }
        System.out.println("/~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~/");
    }

    /**
     * Base start as a single module
     *
     * @param args parameters for starting the shell and bootstrap
     */
    public static void main(String[] args) {
        OpenbatonCLI openbatonCLI = new OpenbatonCLI();
        try {
            openbatonCLI.run(args);
        } catch (Exception e) {
            openbatonCLI.log.error(e.getMessage());
        }
    }

    public String getBrokerIp() {
        return brokerIp;
    }

    public void setBrokerIp(String brokerIp) {
        this.brokerIp = brokerIp;
    }

    /**
     * When running in spring boot application this implements the CommandLineRunner
     * and is executed after all the spring-shell components were loaded.
     *
     * @param args parameters for starting the shell and bootstrap
     */
    @Override
    public void run(String... args) throws Exception {

        ConsoleReader reader = null;
        try {
            reader = new ConsoleReader();
        } catch (IOException e) {
            log.error("Oops, Error while creating ConsoleReader");
            exit(999);
        }
        String line;
        PrintWriter out = new PrintWriter(reader.getOutput());
        List<Completer> completors = new LinkedList<>();
        completors.add(new StringsCompleter(helpCommandList.keySet()));
        completors.add(new FileNameCompleter());
        reader.addCompleter(new ArgumentCompleter(completors));
        reader.setPrompt("\u001B[135m" + System.getProperty("user.name") + "@[\u001B[32mopen-baton\u001B[0m]~> ");
        while ((line = reader.readLine()) != null) {
            out.flush();
            line = line.trim();
            if (line.equalsIgnoreCase("quit") || line.equalsIgnoreCase("exit")) {
                exit(0);
            } else if (line.equalsIgnoreCase("cls")) {
                reader.clearScreen();
            } else if (line.equalsIgnoreCase("help")) {
                usage();
            } else if (line.startsWith("installPlugin ")) {
                installPlugin(line);
            } else if (line.startsWith("listPlugins")) {
                StringTokenizer stringTokenizer = new StringTokenizer(line);
                stringTokenizer.nextToken();
                if (stringTokenizer.hasMoreTokens()) {
                    System.out.println(listPlugins(Integer.parseInt(stringTokenizer.nextToken())));
                }
                    else if (port != null && !port.equals("")){
                    System.out.println(listPlugins(Integer.parseInt(port)));
                }else System.out.println(listPlugins(15672));

            } else if (line.equalsIgnoreCase("")) {
                continue;
            } else usage();
        }
    }

    private String listPlugins(int port) {
        try {
            List<String> plugins = new ArrayList<>();
            List<String> queues = RabbitManager.getQueues(brokerIp, username, password, port);
            for (String queue : queues) {
                if (queue.startsWith("vim-driver") || queue.startsWith("monitor"))
                    plugins.add(queue);
            }
            return plugins.toString();
        } catch (RemoteException e) {
            return e.getMessage();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "error retrieving plugin list";
    }

    private boolean installPlugin(String line) throws IOException {
        StringTokenizer stringTokenizer = new StringTokenizer(line, " ");
        stringTokenizer.nextToken(); // installPlugin
        String path = null;
        if (stringTokenizer.hasMoreTokens()) {
            path = stringTokenizer.nextToken();
        } else {
            log.error("please provide path and name");
            return false;
        }
        String name = null;
        if (stringTokenizer.hasMoreTokens()) {
            name = stringTokenizer.nextToken();
        } else {
            log.error("please provide path and name");
            return false;
        }
        int consumers;
        if (stringTokenizer.hasMoreTokens()) {
            consumers = Integer.parseInt(stringTokenizer.nextToken());
        } else {
            log.error("please provide number of active consumers");
            return false;
        }
        PluginStartup.installPlugin(name, path, "localhost", "1099", consumers);
        return true;
    }

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.publisher = applicationEventPublisher;
    }
}


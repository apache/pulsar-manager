# How to contribute

If you want to make contributions to Apache Pulsar Manager, follow the steps below.

## Prerequisite

Before committing code changes to Apache Pulsar Manager, you need to install required dependencies, fork the pulsar-manager repository and configure IDE.

### Install system dependency

If you have not installed Java and Node, follow the instructions below.

Dependency | Installation guide 
|---|---
Java 8 | https://openjdk.java.net/install/
Node 10.15 | https://nodejs.org/en/

### Fork pulsar-manager repository

Fork the [pulsar-manager](https://github.com/apache/pulsar-manager) repository to your GitHub repository.

### Install pulsar-manager dependency 

1. Clone pulsar-manager code to your machine.
   
    ```bash
    $ git clone git@github.com:[your-github-id]/pulsar-manager.git
    ```

2. Install pulsar-manger dependencies.
   
    ```bash
    $ cd pulsar-manager

    $ ./gradlew build -x test

    $ cd front-end

    $ npm install --save
    ```

## Configure IDE 

Apache Pulsar Manager uses [lombok](https://projectlombok.org/), so set up your IDE with the required plugins.

### Configure Intellij 

To configure annotation processing in IntelliJ, follow the steps below.

1. In IntelliJ, click **Settings** > **Build, Execution, Deployment** > **Compiler** > **Annotation Processors** to open the **Annotation Processors Settings** window.

2. Tick the checkboxes of **Enable annotation processing** and **Obtain processors from project classpath**.

3. Click **OK**.

### Configure Eclipse

Follow the instructions [here](https://howtodoinjava.com/automation/lombok-eclipse-installation-examples/).

## Contribution workflow

1. Add a remote repository.
   
    ```bash
    $ git remote add apache git@github.com:apache/pulsar-manager.git
    ```

2. Sync you local repository with the remote repository.

    ```bash
    $ git checkout master
    $ git pull apache master
    ```

3. Create your PR and commit code changes.

    ```bash
    $ git checkout -b your_branch

    $ git add [your change files]

    $ git commit -m "commit messages"

    $ git push origin your_branch
    ```

> #### Note
> 
> Apache Pulsar Manager uses the open-source protocol of Apache License 2.0. 
>
> If you commit code changes with new files, run the following command to add the license at the beginning of each file. 
>
> ```bash
> ./gradlew licenseFormat
> ```

## Code style

Follow the code styles below to keep code consistent and easy to maintain.

Make sure your code looks plain and simple.

Language | Code style guide 
|---|---
Java | https://google.github.io/styleguide/javaguide.html
Vue | https://vuejs.org/v2/style-guide/


# How to contribute

If you would like to contribute code to this project, fork the repository and send a pull request.

## Prerequisite

If you have not installed Java and Node, install it according to the [installation Java8 instruction](https://openjdk.java.net/install/) and [install Node10.15 instruction](https://nodejs.org/en/).

## Fork

Before contributing, you need to fork [pulsar-manager](https://github.com/streamnative/pulsar-manager) to your github repository.

## Install dependency on Mac OS or Linux

```bash
$ git clone git@github.com:[your-github-id]/pulsar-manager.git
$ cd pulsar-manager
$ ./gradlew build -x test
$ cd front-end
$ npm install --save
```

## Setting up your IDE

pulsar-manager is using [lombok](https://projectlombok.org/) so you have to ensure your IDE setup with
required plugins.

### Intellij

To configure annotation processing in IntelliJ:

1. Open Annotation Processors Settings dialog box by going to
   `Settings -> Build, Execution, Deployment -> Compiler -> Annotation Processors`.

2. Select the following buttons:
   1. "Enable annotation processing"
   2. "Obtain processors from project classpath"

3. Click “OK”.

4. Install the lombok plugin in intellij.

### Eclipse

Follow the instructions [here](https://howtodoinjava.com/automation/lombok-eclipse-installation-examples/)
to configure your Eclipse setup.

## Contribution flow

```bash
$ git remote add streamnative git@github.com:streamnative/pulsar-manager.git
// sync with remote master
$ git checkout master
$ git pull streamnative master
$ git push origin master
// create PR branch
$ git checkout -b your_branch   
# do your work, and then
$ git add [your change files]
$ git commit -sm "commit messages"
$ git push origin your_branch
```

## Code style

Refer to [Java style doc](https://google.github.io/styleguide/javaguide.html).  
Refer to [Vue coding style](https://vuejs.org/v2/style-guide/)

## Format header for new file

The project uses the open source protocol of Apache License 2.0. If you need to create a new file when developing new features, 
add the license at the beginning of each file by run the following command.

```bash
./gradlew licenseFormat
```
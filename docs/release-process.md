# Release process

This guide illustrates how to perform a release for Pulsar Manager.

## Making the release

The steps for releasing are as follows:

1. Prepare for a release
2. Create the release branch
3. Update project version and tag
4. Build and inspect an artifact
5. Write a release note
6. Move master branch to next version
7. Announce the release

## Steps in detail

1. Prepare for a release

Create a new milestone and move the pull requests that can not
be published in this release to the new milestone.

2. Create the release branch

We are going to create a branch from `master` to `branch-x.y`
where the tag will be generated and where new fixes will be
applied as part of the maintenance for the release. `x.y.z`
is the version of the release.

The branch needs only to be created when creating major releases,
and not for patch releases.

Eg: When creating `v0.1.0` release, will be creating
the branch `branch-0.1`, but for `v0.1.1` we
would keep using the old `branch-0.1`.

In these instructions, I'm referring to an fictitious release `x.y.z`.
Change the release version in the examples accordingly with the real version.

It is recommended to create a fresh clone of the repository to 
avoid any local files to interfere in the process:

```shell
$ git clone git@github.com:streamnative/pulsar-manager.git
$ cd pulsar-manager

# Create a branch
$ git checkout -b branch-x.y origin/master

# Create a tag
$ git tag -u $USER@streamnative.io vx.y.z -m 'Release vx.y.z'
```

3. Update project version and tag

In this process the maven version of the project will always be the final one.

```bash
# Bump to the backend release version
$ ./gradlew incrementVersion -Pversion=0.X.0
$ cd front-end
$ npm version 0.X.0

# Commit
$ git commit -m 'Release x.y.z' -a

# Push both the branch and the tag to Github repo
$ git push origin branch-x.y
$ git push origin vx.y.z
```

4. Build and inspect an artifact

Generate a jar package for the backend.

```bash
$ ./gradlew clean distTar
```

Generate a dist file for the frontend.

```bash
$ cd front-end
$ npm install --save
$ npm run build:prod
```

Generate a release package.

```bash
$ mkdir -p temp-release temp-release/pulsar-manager
$ cd temp-release
$ cp ../distribution/LICENSE.bin.txt pulsar-manager/LICENSE
$ cp ../distribution/NOTICE.bin.txt pulsar-manager/NOTICE
$ cp -r ../distribution/licenses pulsar-manager/
$ cp -r ../build/distributions/pulsar-manager.tar ../front-end/dist pulsar-manager
$ tar -czvf apache-pulsar-manager-0.X.0-bin.tar.gz pulsar-manager.tar pulsar-manager
```

5. Publish a release note

Check the milestone in GitHub associated with the release. 

In the released item, add the list of the most important changes 
that happened in the release and a link to the associated milestone,
with the complete list of all the changes. 

Update the release draft at [the release homepage of pulsar-manager](https://github.com/streamnative/pulsar-manager/releases)

Then the release draft, binary, and command doc will be published
 that release automatically.

6. Move master branch to next version

We need to move master version to next iteration `X + 1`.

```bash
$ git checkout master
$ ./gradlew incrementVersion 0.(X+1).0
$ cd front-end
$ npm version 0.(X+1).0
```

7. Announce the release

After publishing and checking the release, work with Growth team
to announce that a new version of pulsar-manager is released.

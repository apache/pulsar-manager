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
/**
 * The pipeline will build a docker image for the website.
 */

def label = "jenkins-pulsar-manager"

podTemplate(label: label, yaml: """
apiVersion: v1
kind: Pod
spec:
  containers:
  - name: imagebuilder
    image: apachepulsar/pulsar-build:ubuntu-18.04
    command: ['cat']
    tty: true
    volumeMounts:
    - name: dockersock
      mountPath: /var/run/docker.sock
  volumes:
  - name: dockersock
    hostPath:
      path: /var/run/docker.sock
"""
  ) {

  node(label) {
    stage('Checkout Git Repo') {
      checkout scm
    }
    stage('Build and Test PSegment') {
      container('imagebuilder') {
        sh """
export JAVA_HOME=/usr/lib/jvm/java-1.8.0-openjdk-amd64
export PATH=/usr/lib/jvm/java-1.8.0-openjdk-amd64/bin:$PATH
javac -version
java -version
./gradlew build
"""
      }
    }
  }
}

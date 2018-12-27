FROM ubuntu:16.04

# Java Instalation
ADD jdk.tar.gz /tmp/
RUN ls /tmp/ | grep jdk | xargs -I % mv /tmp/% /opt/java

# Java Configuration
ENV JAVA_HOME /opt/java
RUN echo "export JAVA_HOME=/opt/java\nexport PATH=$PATH:$JAVA_HOME/bin" > /etc/profile.d/java-env.sh
RUN update-alternatives --install /bin/java java $JAVA_HOME/bin/java 999999

# Package installation
ADD target/notebooks-0.4.jar /notebooks.jar
CMD [ "java", "-jar", "notebooks.jar" ]

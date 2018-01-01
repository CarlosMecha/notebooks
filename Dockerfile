FROM ubuntu:16.04

MAINTAINER CarlosMecha

# Java Instalation
ADD jdk.tar.gz /tmp/
ADD keystore.p12 .
RUN ls /tmp/ | grep jdk | xargs -I % mv /tmp/% /opt/java

# Java Configuration
ENV JAVA_HOME /opt/java
RUN echo "export JAVA_HOME=/opt/java\nexport PATH=$PATH:$JAVA_HOME/bin" > /etc/profile.d/java-env.sh
RUN update-alternatives --install /bin/java java $JAVA_HOME/bin/java 999999

# Package installation
ADD target/notebooks-0.2.jar /notebooks.jar
RUN echo "#!/bin/bash\nsleep 5;\njava -jar notebooks.jar -Xmx256m --server.ssl.key-store-password=banking;" > /notebooks.sh
RUN chmod u+x /notebooks.sh
CMD [ "/notebooks.sh" ]

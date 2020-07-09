#
# Adds Java 14 to flyway image so we can run our custom resource loader
#
FROM flyway/flyway:6.0.1


#
# Install VA certs. We'll need to be root user. Flyways sets the user as 'flyway',
# so we'll need to restore that after we download the certs. Speaking of which,
# this contain does not have curl, or wget, or really not much at all that
# can be used to download files. So here's a Java Nashorn JavaScript hack to
# download the cert.
#
USER root

RUN echo 'java.nio.file.Files.copy(new java.net.URL("http://aia.pki.va.gov/PKI/AIA/VA/VA-Internal-S2-RCA1-v1.cer").openStream(), java.nio.file.Paths.get("/usr/local/share/ca-certificates/VA-Internal-S2-RCA1-v1.cer"), java.nio.file.StandardCopyOption.REPLACE_EXISTING);' > /tmp/download.js \
  && /opt/java/openjdk/bin/jjs /tmp/download.js \
  && /usr/sbin/update-ca-certificates extract \
  && echo yes | keytool -import -cacerts -trustcacerts -storepass changeit \
     -alias VA-Internal-S2-RCA1-v1 \
     -file /usr/local/share/ca-certificates/VA-Internal-S2-RCA1-v1.cer


USER flyway

ENTRYPOINT []
RUN curl -sL http://github.com/AdoptOpenJDK/openjdk14-binaries/releases/download/jdk-14.0.1%2B7/OpenJDK14U-jdk_x64_linux_hotspot_14.0.1_7.tar.gz -o /tmp/OpenJDK14U-jdk_x64_linux_hotspot_14.tar.gz
ENV EXTRA_JRES=/flyway/jre/
RUN mkdir -p $EXTRA_JRES && tar -x -C $EXTRA_JRES -f /tmp/OpenJDK14U-jdk_x64_linux_hotspot_14.tar.gz

RUN curl -sL http://archive.apache.org/dist/maven/maven-3/3.6.3/binaries/apache-maven-3.6.3-bin.tar.gz -o /tmp/apache-maven-3.6.3-bin.tar.gz \
  && tar -x -C /flyway/ -f /tmp/apache-maven-3.6.3-bin.tar.gz
ENV EXTRA_MVNS=/flyway/

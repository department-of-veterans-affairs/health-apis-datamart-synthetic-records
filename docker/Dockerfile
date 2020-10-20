FROM vasdvp/health-apis-dev-tools:mvn-3.6-jdk-14

ARG flyway_version=7.0.4

RUN wget -qO- https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/${flyway_version}/flyway-commandline-${flyway_version}-linux-x64.tar.gz | tar xz  \
  && ln -s /flyway-${flyway_version}/flyway /usr/local/bin \
  && chown 1000:1000 -R /flyway-${flyway_version} \
  && chown 1000:1000 -R /usr/local/bin


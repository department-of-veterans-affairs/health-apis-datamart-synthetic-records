FROM vasdvp/health-apis-dev-tools:mvn-3.6-jdk-14

RUN wget -qO- https://repo1.maven.org/maven2/org/flywaydb/flyway-commandline/7.0.4/flyway-commandline-7.0.4-linux-x64.tar.gz | tar xz  \
  && ln -s /flyway-7.0.4/flyway /usr/local/bin \
  && chown 1000:1000 -R /flyway-7.0.4 \
  && chown 1000:1000 -R /usr/local/bin


# Build file for Docker/Podman for building an image from the native
#   binary version of quarkus-suntimes. This is provided only
#   for illustration -- a build for OpenShift will use the Dockerfiles
#   in src/main/docker.

# We must select a base image that is a reasonable match for the machine
#   where the native executable was compiled. Specifically, the GLibC
#   versions must match.
FROM registry.fedoraproject.org/fedora-minimal:35

ENV LANG='en_US.UTF-8' LANGUAGE='en_US:en'

# Also set up permissions for user `1001`
RUN mkdir /deployments \
    && chown 1001 /deployments \
    && chmod "g+rwX" /deployments \
    && chown 1001:root /deployments 

COPY target/quarkus-ircbot-1.0.0-runner /deployments/

EXPOSE 8080
USER 1001

ENTRYPOINT [ "/deployments/quarkus-suntimes-1.0.0-runner" ]


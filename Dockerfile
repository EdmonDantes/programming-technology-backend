FROM openjdk:12-alpine

WORKDIR /home
COPY ./ .
ENTRYPOINT ["/home/chemistry/bin/chemistry"]

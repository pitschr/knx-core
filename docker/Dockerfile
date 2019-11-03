FROM openjdk:11-slim

# Copy the jar file into package
# The name is hardcoded and copied by 'create-docker-image.sh' script
COPY "knx-link.jar" .
EXPOSE 3671/udp 40000/udp 40001/udp 40002/udp 40003/udp

# Install required packages
RUN apt-get -yqq update && \
    apt-get -yqq install tcpdump net-tools dnsutils traceroute inetutils-ping ethtool sudo iperf3 man

CMD [ "/bin/bash" ]

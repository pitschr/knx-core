#!/bin/bash

DOCKER_FOLDER="$(cd "$(dirname "${BASH_SOURCE}")" >/dev/null 2>&1 && pwd)"
DOCKER_CONTAINER_NAME="knxclient"
DOCKER_IMAGE="pitschr/knx-link:latest"

#
# Start/Run the docker container
#
function startDockerContainer() {
  docker run --rm -it --name "$DOCKER_CONTAINER_NAME" "$DOCKER_IMAGE"
  #docker run --rm -it --name "$DOCKER_CONTAINER_NAME" -p 3671:3671/udp -p 40000:40000/udp -p 40001:40001/udp -p 40002:40002/udp -p 40003:40003/udp "$DOCKER_IMAGE"
  #docker run --rm -it --name "$DOCKER_CONTAINER_NAME" -p 3671:3671/udp -p 127.0.0.1:40000:40000/udp -p 127.0.0.1:40001:40001/udp -p 127.0.0.1:40002:40002/udp -p 127.0.0.1:40003:40003/udp "$DOCKER_IMAGE"
  #docker run --rm -it --name "$DOCKER_CONTAINER_NAME" --net=pub_net --ip=100.98.26.47 -p 3671:3671/udp -p 127.0.0.1:40000:40000/udp -p 127.0.0.1:40001:40001/udp -p 127.0.0.1:40002:40002/udp -p 127.0.0.1:40003:40003/udp "$DOCKER_IMAGE"
}

#
# Entering the running docker container
#
function execDockerContainer() {
  docker exec -it "$DOCKER_CONTAINER_NAME" /bin/bash
}

#
# Main
#
if [[ -z "$(docker images -q "$DOCKER_IMAGE" 2>/dev/null)" ]]; then
  #
  # Docker Image not available
  #
  echo "Docker image doesn't exists."
  . "$DOCKER_FOLDER/create-docker-image.sh"
  startDockerContainer
else
  #
  # Docker Image is available -> Is docker container running?
  #
  DOCKER_IMAGE_LS=$(docker images "$DOCKER_IMAGE")
  if [[ -z "$(docker container ps -q --filter "name=knxclient" 2>/dev/null)" ]]; then
    #
    # Container NOT running
    #
    echo "There is already a docker image available, but no docker container running:"
    echo "--------------------------------------------------------------------------------------------------"
    echo "$DOCKER_IMAGE_LS"
    echo "--------------------------------------------------------------------------------------------------"
    echo
    echo "What do you want to do now?"
    echo "  1) Start container"
    echo "  2) Re-create image and start container"
    echo "  q) Quit"
    while true; do
      read -p "Answer: " answer

      case $answer in
      [1])
        startDockerContainer
        break
        ;;
      [2])
        docker image rm -f "$DOCKER_IMAGE"
        . "$DOCKER_FOLDER/create-docker-image.sh"
        startDockerContainer
        break
        ;;
      [q])
        break
        ;;
      esac
    done
  else
    #
    # Container already running
    #
    DOCKER_CONTAINER_LS=$(docker container ps --filter "name=$DOCKER_CONTAINER_NAME" 2>/dev/null)
    echo "There is already a docker container running:"
    echo "--------------------------------------------------------------------------------------------------"
    echo "$DOCKER_IMAGE_LS"
    echo
    echo "$DOCKER_CONTAINER_LS"
    echo "--------------------------------------------------------------------------------------------------"
    echo
    echo "What do you want to do now?"
    echo "  1) Enter running container"
    echo "  2) Restart container"
    echo "  3) Re-create docker image and start container"
    echo "  q) Quit"
    while true; do
      read -p "Answer: " answer
      case $answer in
      [1])
        execDockerContainer
        break
        ;;
      [2])
        docker container stop "$DOCKER_CONTAINER_NAME"
        startDockerContainer
        break
        ;;
      [3])
        docker container stop "$DOCKER_CONTAINER_NAME"
        docker image rm -f "$DOCKER_IMAGE"
        . "$DOCKER_FOLDER/create-docker-image.sh"
        startDockerContainer
        break
        ;;
      [q])
        break
        ;;
      esac
    done
  fi
fi

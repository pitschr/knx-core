#!/bin/bash

DOCKER_FOLDER="$(cd "$(dirname "${BASH_SOURCE}")" >/dev/null 2>&1 && pwd)"
PROJECT_ROOT_FOLDER="$(cd "$DOCKER_FOLDER/.." >/dev/null 2>&1 && pwd)"

# returns the latest '*-jar-with-dependencies.jar' file
# %T@ ... last modification in unix time stamp
# %p  ... file name
# sort by last modification reversed (newer top), return 1st line and file name only
LATEST_DEPENDENCIES_JAR=$(
  cd "$PROJECT_ROOT_FOLDER"
  ls -r knx-examples/target/*-jar-with-dependencies.jar 2>/dev/null | head -n1 | cut -d " " -f 2
)
if [ -z "$LATEST_DEPENDENCIES_JAR" ]; then
  echo "No suitable JAR file found. Please run 'maven clean package' first"
  return
fi

# Take version from Maven POM
POM_VERSION=$(
  cd "$PROJECT_ROOT_FOLDER"
  fgrep "<version>" pom.xml | head -n1 | sed 's/.*>\(.*\)<.*/\1/g'
)
DOCKER_TAG_VERSION="pitschr/knx-link:$(echo "$POM_VERSION" | awk '{print toupper($0)}')"
DOCKER_TAG_LATEST="pitschr/knx-link:latest"

#
# Function to create the docker image
#
function createDockerImage() {
  # create temporary folder and copy the jar file there
  local TMP_DIR=$(mktemp -d -t tmp-XXXXXXXXXX)
  local TMP_JAR_FILE="$TMP_DIR/knx-link.jar"
  cp "$DOCKER_FOLDER/Dockerfile" "$TMP_DIR/Dockerfile"
  cp "$LATEST_DEPENDENCIES_JAR" "$TMP_JAR_FILE"

  # copy the jar file to hardcoded file name that will be read by docker
  cd "$TMP_DIR" >/dev/null
  echo "Creating Docker image..."
  docker build -t "$DOCKER_TAG_VERSION" -t "$DOCKER_TAG_LATEST" .
  echo "Docker image created."
  cd - >/dev/null

  # clean up the temporary folder
  rm -rf "$TMP_DIR"
}

#
# Main
#
echo "I will create docker image based on:"
echo "-------------------------------------------------"
echo "  jar: $LATEST_DEPENDENCIES_JAR"
echo "  tags: "
echo "    - $DOCKER_TAG_VERSION"
echo "    - $DOCKER_TAG_LATEST"
echo "-------------------------------------------------"
echo

while true; do
  read -p "Agree? (y/n): " yn
  case $yn in
  [Yy]*)
    createDockerImage
    break
    ;;
  [Nn]*)
    break
    ;;
  esac
done
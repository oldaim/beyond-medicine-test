#!/bin/bash

# Define variables
CONTAINER_NAME="mysql-container"
ROOT_PASSWORD="oldaim"
USER_NAME="oldaim"
USER_PASSWORD="oldaim"
DATABASE_NAME="test"
PORT="3306"

echo "Starting MySQL container..."

docker run --name $CONTAINER_NAME \
  -p $PORT:3306 \
  -e MYSQL_ROOT_PASSWORD=$ROOT_PASSWORD \
  -e MYSQL_DATABASE=$DATABASE_NAME \
  -e MYSQL_USER=$USER_NAME \
  -e MYSQL_PASSWORD=$USER_PASSWORD \
  -d mysql:latest

# Check if container started successfully
if [ $? -eq 0 ]; then
  echo "MySQL container started successfully on port $PORT"
  echo "Database: $DATABASE_NAME | User: $USER_NAME | Password: $USER_PASSWORD"
else
  echo "Failed to start MySQL container."
fi

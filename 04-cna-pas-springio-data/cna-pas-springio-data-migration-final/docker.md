docker run -p 3307:3306 --name mysqldb -e MYSQL_ROOT_PASSWORD=root -e MYSQL_DATABASE=aircraft mysql


docker rm mysqldb

docker images

docker kill 76
docker ps
docker version
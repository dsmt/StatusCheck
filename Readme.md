mvn clean compile assembly:single \
&& docker build -t <your_docker_hub>/private:status-check . \
&& docker push <your_docker_hub>/private:status-check

sudo docker stop status-check && sudo docker rm status-check \
&& sudo docker pull <your_docker_hub>/private:status-check \
&& sudo docker run --name status-check -d -p 80:80 <your_docker_hub>/private:status-check
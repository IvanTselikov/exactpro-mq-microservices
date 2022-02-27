FROM openjdk:11

RUN mkdir /app

WORKDIR /app

COPY build/libs/Consumer.jar .

COPY start.sh .

RUN apt-get update && apt-get install -y apt-utils

RUN apt-get install -y netcat

CMD ["./start.sh"]
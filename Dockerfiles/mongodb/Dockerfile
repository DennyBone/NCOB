FROM mongo:latest

RUN mkdir -p /ncob/mongo/conf
RUN mkdir -p /ncob/mongo/logs
RUN chown -R mongodb /ncob

COPY mongodb.conf /ncob/mongo/conf

# Make the new dir a VOLUME to persist it 
VOLUME /data/db

CMD ["mongod", "--config", "/ncob/mongo/conf/mongodb.conf"]
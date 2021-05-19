

FROM postgres

COPY $PWD/.script_files/_init-user-db.sh /docker-entrypoint-initdb.d/_init-user-db.sh

ENV DB_NAMES='auth_db user_db media_db store_db chat_db checkout_db'
ENV POSTGRES_PASSWORD='kjdsfhalkshfkdsjfh'
ENV USER_USERNAME='fishmarket'
ENV USER_PASSWORD='lkjfdsblkdfjglksjdfhglkdjf'
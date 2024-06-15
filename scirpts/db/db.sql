CREATE KEYSPACE IF NOT EXISTS test
    WITH REPLICATION = {'class':'SimpleStrategy', 'replication_factor' : 1};

CREATE TABLE IF NOT EXISTS test.message (
    id BIGINT,
    user_id BIGINT,
    content TEXT,
    create_time TIMESTAMP,
    update_time TIMESTAMP,
    is_deleted TINYINT,
    PRIMARY KEY(user_id, id)
);

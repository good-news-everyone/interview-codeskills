create table customer_portraits
(
    id          bigint not null,
    customer_id bigint,
    s3_location text,
    created_at  timestamptz
);

-- найти последний портрет (id, s3_location, created_at) для каждого customer'a, отфильтровать портреты у которых customer_id = null

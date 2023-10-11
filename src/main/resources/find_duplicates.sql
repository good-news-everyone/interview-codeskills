create table customer_communication_channels
(
    id           int  not null,
    customer_id  int  not null,
    channel_type text not null,
    value        text not null
);

insert into customer_communication_channels
values (1, 1, 'phone', '123456790'),
       (2, 1, 'email', 'smart@local.org'),
       (3, 2, 'email', 'SMART@LOCAL.ORG'),
       (4, 1, 'phone', '123456790'),
       (5, 1, 'email', 'stupid@local.org')
;

-- элементы считаются дублями, если один и тот же канал связи принадлежит разным кастомерам
-- вывести в первой колонке канал связи
-- во второй колонке номер\емейл
-- в третьей customer_communication_channels.id[]
-- в четвертой customer_communication_channels.customer_id[]


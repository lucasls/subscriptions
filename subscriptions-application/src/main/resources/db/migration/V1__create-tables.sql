create table product
(
    code                       text           not null primary key,
    name                       text           not null,
    price_value                numeric(12, 2) not null,
    price_unit                 text           not null,
    subscription_period_months integer        not null,
    tax_rate                   numeric(2, 2)  not null
);

create table subscription
(
    id                         uuid primary key,
    user_id                    uuid             not null,
    product_code               text             not null,
    product_name               text             not null,
    price_value                text             not null,
    price_unit                 text             not null,
    subscription_period_months integer          not null,
    tax_rate                   double precision not null,
    created_at                 timestamp
);

create index on subscription (user_id);

create table subscription_status_changes
(
    subscription_id uuid not null references subscription,
    changed_at      timestamp,
    status          text
);

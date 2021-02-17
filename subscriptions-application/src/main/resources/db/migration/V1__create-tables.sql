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
    user_id                    uuid primary key,
    product_code               text             not null,
    product_name               text             not null,
    price_value                text             not null,
    price_unit                 text             not null,
    subscription_period_months integer          not null,
    tax_rate                   double precision not null,
    created_at                 timestamp
);

create table subscription_status_changes
(
    subscription_user_id uuid not null references subscription,
    changed_at           timestamp,
    status               text
);

INSERT INTO product (code, name, price_value, price_unit, subscription_period_months, tax_rate)
VALUES ('ANNUAL', 'Annual Payment', 83.99, 'EUR', 12, 0.07);

INSERT INTO product (code, name, price_value, price_unit, subscription_period_months, tax_rate)
VALUES ('SEMI_ANNUAL', 'Semi-Annual Payment', 59.99, 'EUR', 6, 0.19);

INSERT INTO product (code, name, price_value, price_unit, subscription_period_months, tax_rate)
VALUES ('QUARTERLY', 'Quarterly payment', 38.99, 'EUR', 3, 0.19);


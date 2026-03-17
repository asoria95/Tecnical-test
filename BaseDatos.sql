

\connect postgres

DROP DATABASE IF EXISTS customerdb;
CREATE DATABASE customerdb
    ENCODING 'UTF8'
    LC_COLLATE 'en_US.UTF-8'
    LC_CTYPE   'en_US.UTF-8'
    TEMPLATE template0;

\connect customerdb

CREATE TABLE customers (
    clienteid       BIGSERIAL       PRIMARY KEY,
    nombre          VARCHAR(255)    NOT NULL,
    genero          VARCHAR(50),
    edad            INTEGER,
    identificacion  VARCHAR(50)     NOT NULL,
    direccion       VARCHAR(255),
    telefono        VARCHAR(20),
    contrasena      VARCHAR(255)    NOT NULL,
    estado          VARCHAR(20)     NOT NULL DEFAULT 'ACTIVE',

    CONSTRAINT uq_customers_identificacion UNIQUE (identificacion)
);

CREATE INDEX idx_customers_nombre ON customers (nombre);

INSERT INTO customers (nombre, genero, edad, identificacion, direccion, telefono, contrasena, estado)
VALUES
    ('Jose Lema',          NULL, NULL, '098254785', 'Otavalo sn y principal',    '098254785', '1234', 'true'),
    ('Marianela Montalvo', NULL, NULL, '097548965', 'Amazonas y NNUU',           '097548965', '5678', 'true'),
    ('Juan Osorio',        NULL, NULL, '098874587', '13 junio y Equinoccial',    '098874587', '1245', 'true');


\connect postgres

DROP DATABASE IF EXISTS accountdb;
CREATE DATABASE accountdb
    ENCODING 'UTF8'
    LC_COLLATE 'en_US.UTF-8'
    LC_CTYPE   'en_US.UTF-8'
    TEMPLATE template0;

\connect accountdb

CREATE TABLE accounts (
    id              BIGSERIAL           PRIMARY KEY,
    account_number  VARCHAR(20)         NOT NULL,
    account_type    VARCHAR(20)         NOT NULL,
    initial_balance NUMERIC(19, 2)      NOT NULL,
    balance         NUMERIC(19, 2)      NOT NULL,
    status          BOOLEAN             NOT NULL DEFAULT TRUE,
    customer_id     VARCHAR(50)         NOT NULL,

    CONSTRAINT uq_accounts_account_number UNIQUE (account_number)
);

CREATE INDEX idx_accounts_customer_id ON accounts (customer_id);

CREATE TABLE movements (
    id              BIGSERIAL           PRIMARY KEY,
    date            TIMESTAMP           NOT NULL,
    movement_type   VARCHAR(50)         NOT NULL,
    amount          NUMERIC(19, 2)      NOT NULL,
    balance         NUMERIC(19, 2)      NOT NULL,
    account_id      BIGINT              NOT NULL,

    CONSTRAINT fk_movements_account FOREIGN KEY (account_id) REFERENCES accounts (id)
        ON DELETE CASCADE
);

CREATE INDEX idx_movements_account_id ON movements (account_id);
CREATE INDEX idx_movements_date       ON movements (date);

INSERT INTO accounts (account_number, account_type, initial_balance, balance, status, customer_id)
VALUES
    ('478758', 'Ahorro',    2000.00, 1425.00, TRUE, '1'),
    ('225487', 'Corriente', 100.00,  700.00,  TRUE, '2'),
    ('495878', 'Ahorros',   0.00,    150.00,  TRUE, '3'),
    ('496825', 'Ahorros',   540.00,  0.00,    TRUE, '2'),
    ('585545', 'Corriente', 1000.00, 1000.00, TRUE, '1');

INSERT INTO movements (date, movement_type, amount, balance, account_id)
VALUES
    -- 478758 (Jose Lema / Ahorro):     retiro 575  -> balance 1425
    ('2022-02-09 10:00:00', 'Retiro',   -575.00, 1425.00,
        (SELECT id FROM accounts WHERE account_number = '478758')),

    -- 225487 (Marianela / Corriente):  deposito 600 -> balance 700
    ('2022-02-10 09:00:00', 'Deposito',  600.00,  700.00,
        (SELECT id FROM accounts WHERE account_number = '225487')),

    -- 495878 (Juan Osorio / Ahorros):  deposito 150 -> balance 150
    ('2022-02-09 11:00:00', 'Deposito',  150.00,  150.00,
        (SELECT id FROM accounts WHERE account_number = '495878')),

    -- 496825 (Marianela / Ahorros):    retiro 540  -> balance 0
    ('2022-02-08 14:00:00', 'Retiro',   -540.00,    0.00,
        (SELECT id FROM accounts WHERE account_number = '496825'));

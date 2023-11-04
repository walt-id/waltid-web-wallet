-- ----------------------------------
-- Emails table
-- ----------------------------------
CREATE TABLE IF NOT EXISTS "emails"
(
    "id" uuid NOT NULL,
    "email" text COLLATE pg_catalog."default" NOT NULL,
    "password" text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "emails_pkey" PRIMARY KEY (id),
    CONSTRAINT email UNIQUE (email)
);
-- ----------------------------------
-- Wallets table
-- ----------------------------------
CREATE TABLE IF NOT EXISTS "wallets"
(
    "id" uuid NOT NULL,
    "address" text COLLATE pg_catalog."default" NOT NULL,
    "ecosystem" text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT wallets_pkey PRIMARY KEY (id),
    CONSTRAINT address UNIQUE (address)
);
-- ----------------------------------
-- Accounts table
-- ----------------------------------
CREATE TABLE IF NOT EXISTS "accounts"
(
    "id" uuid NOT NULL,
    "email" uuid NULL,
    "wallet" uuid NULL,
    CONSTRAINT accounts_pkey PRIMARY KEY (id),
    CONSTRAINT accounts_email_wallet_unique UNIQUE (email, wallet)
        INCLUDE(email, wallet),
    CONSTRAINT account_email_fk FOREIGN KEY (email)
        REFERENCES public.emails (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT account_wallet_fk FOREIGN KEY (wallet)
        REFERENCES public.wallets (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
-- ----------------------------------
-- AccountWallets table
-- ----------------------------------
CREATE TABLE IF NOT EXISTS "account_wallets"
(
    "id" uuid NOT NULL,
    "account" uuid NOT NULL,
    "wallet" uuid NOT NULL,
    CONSTRAINT account_wallets_pkey PRIMARY KEY (id),
    CONSTRAINT account_wallets_account_fk FOREIGN KEY (account)
        REFERENCES "accounts" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT account_wallets_wallet_fk FOREIGN KEY (wallet)
        REFERENCES "wallets" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
-- ----------------------------------
-- WalletOperationHistories table
-- ----------------------------------
CREATE TABLE IF NOT EXISTS "wallet_operation_histories"
(
    "id" uuid NOT NULL,
    "account" uuid NOT NULL,
    "timestamp" text COLLATE pg_catalog."default" NOT NULL,
    "operation" text COLLATE pg_catalog."default" NOT NULL,
    "data" text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT wallet_operation_histories_pkey PRIMARY KEY (id),
    CONSTRAINT wallet_operation_histories_account_fk FOREIGN KEY (account)
        REFERENCES "accounts" (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
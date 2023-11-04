-- ----------------------------------
-- Issuers table
-- ----------------------------------
CREATE TABLE IF NOT EXISTS "issuers"
(
    "id" uuid NOT NULL,
    "name" text COLLATE pg_catalog."default" NOT NULL,
    "description" text COLLATE pg_catalog."default" NOT NULL,
    "ui" text COLLATE pg_catalog."default" NOT NULL,
    "configuration" text COLLATE pg_catalog."default" NOT NULL,
    CONSTRAINT "issuers_pkey" PRIMARY KEY (id)
);
-- ----------------------------------
-- AccountIssuers table
-- ----------------------------------
CREATE TABLE IF NOT EXISTS "account_issuers"
(
    "id" uuid NOT NULL,
    "account" uuid NOT NULL,
    "issuer" uuid NOT NULL,
    CONSTRAINT "account_issuers_pkey" PRIMARY KEY (id),
    CONSTRAINT account_issuers_account_fk FOREIGN KEY (account)
        REFERENCES public.accounts (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE,
    CONSTRAINT account_issuers_issuer_fk FOREIGN KEY (issuer)
        REFERENCES public.issuers (id) MATCH SIMPLE
        ON UPDATE CASCADE
        ON DELETE CASCADE
);
-- ----------------------------------
-- AccountIssuers unique index
-- ----------------------------------
CREATE UNIQUE INDEX account_issuers_account_issuer ON account_issuers(account, issuer);
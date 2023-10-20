-- ----------------------------------
-- Issuers table
-- ----------------------------------
CREATE TABLE "issuers" (
    "id" BINARY(16) NOT NULL PRIMARY KEY,
    "name" VARCHAR(128) NOT NULL,
    "description" TEXT DEFAULT 'no description' NULL,
    "ui" VARCHAR(128) NOT NULL,
    "configuration" VARCHAR(256) NOT NULL
);
-- ----------------------------------
-- AccountIssuers table
-- ----------------------------------
CREATE TABLE "account_issuers" (
    "id" BINARY(16) NOT NULL PRIMARY KEY,
    "account" BINARY(16) NOT NULL,
    "issuer" BINARY(16) NOT NULL,
    CONSTRAINT "fk_account_issuers_account__id" FOREIGN KEY ("account") REFERENCES "accounts"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "fk_account_issuers_issuer__id" FOREIGN KEY ("issuer") REFERENCES "issuers"("id") ON DELETE RESTRICT ON UPDATE CASCADE
);
-- ----------------------------------
-- AccountIssuers unique index
-- ----------------------------------
CREATE UNIQUE INDEX "account_issuers_account_issuer" ON "account_issuers" ("account", "issuer");
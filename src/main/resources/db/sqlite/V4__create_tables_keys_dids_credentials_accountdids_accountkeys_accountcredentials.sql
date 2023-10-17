-- ----------------------------------
-- Keys table
-- ----------------------------------
CREATE TABLE "keys" (
    "id" BINARY(16) NOT NULL PRIMARY KEY,
    "kid" VARCHAR(512) NOT NULL,
    "document" TEXT NOT NULL
);
-- ----------------------------------
-- Dids table
-- ----------------------------------
CREATE TABLE "dids" (
    "id" BINARY(16) NOT NULL PRIMARY KEY,
    "did" VARCHAR(1024) NOT NULL,
    "document" TEXT NOT NULL,
    "key" BINARY(16) NOT NULL,
    CONSTRAINT "fk_dids_key__id" FOREIGN KEY ("key") REFERENCES "keys"("id") ON DELETE RESTRICT ON UPDATE CASCADE
);
-- ----------------------------------
-- Credentials table
-- ----------------------------------
CREATE TABLE "credentials" (
    "id" BINARY(16) NOT NULL PRIMARY KEY,
    "cid" VARCHAR(256) NOT NULL,
    "document" TEXT NOT NULL
);
-- ----------------------------------
-- AccountKeys table
-- ----------------------------------
CREATE TABLE "account_keys" (
    "id" BINARY(16) NOT NULL PRIMARY KEY,
    "account" BINARY(16) NOT NULL,
    "key" BINARY(16) NOT NULL,
    CONSTRAINT "fk_account_keys_account__id" FOREIGN KEY ("account") REFERENCES "accounts"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "fk_account_keys_key__id" FOREIGN KEY ("key") REFERENCES "keys"("id") ON DELETE RESTRICT ON UPDATE CASCADE
);
-- ----------------------------------
-- AccountDids table
-- ----------------------------------
CREATE TABLE "account_dids" (
    "id" BINARY(16) NOT NULL PRIMARY KEY,
    "account" BINARY(16) NOT NULL,
    "did" BINARY(16) NOT NULL,
    "alias" VARCHAR(1024) NOT NULL,
    "default" BOOLEAN DEFAULT 0 NOT NULL,
    CONSTRAINT "fk_account_dids_account__id" FOREIGN KEY ("account") REFERENCES "accounts"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "fk_account_dids_did__id" FOREIGN KEY ("did") REFERENCES "dids"("id") ON DELETE RESTRICT ON UPDATE CASCADE
);
-- ----------------------------------
-- AccountCredentials table
-- ----------------------------------
CREATE TABLE "account_credentials" (
    "id" BINARY(16) NOT NULL PRIMARY KEY,
    "account" BINARY(16) NOT NULL,
    "credential" BINARY(16) NOT NULL,
    CONSTRAINT "fk_account_credentials_account__id" FOREIGN KEY ("account") REFERENCES "accounts"("id") ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT "fk_account_credentials_credential__id" FOREIGN KEY ("credential") REFERENCES "credentials"("id") ON DELETE RESTRICT ON UPDATE CASCADE
);
-- ----------------------------------
-- Keys index
-- ----------------------------------
CREATE UNIQUE INDEX "keys_kid" ON "keys" ("kid");
-- ----------------------------------
-- Dids index
-- ----------------------------------
CREATE UNIQUE INDEX "dids_did" ON "dids" ("did");
-- ----------------------------------
-- Credentials index
-- ----------------------------------
CREATE UNIQUE INDEX "credentials_cid" ON "credentials" ("cid")
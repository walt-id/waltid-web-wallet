-- ----------------------------------
-- Keys table
-- ----------------------------------
CREATE TABLE "keys" (
	"id"	BINARY(16) NOT NULL,
	"kid"	VARCHAR(512) NOT NULL,
	"document"	TEXT NOT NULL,
	PRIMARY KEY("id")
);
-- ----------------------------------
-- Dids table
-- ----------------------------------
CREATE TABLE "dids" (
	"id"	BINARY(16) NOT NULL,
	"did"	VARCHAR(1024) NOT NULL,
	"document"	TEXT NOT NULL,
	PRIMARY KEY("id")
);
-- ----------------------------------
-- Credentials table
-- ----------------------------------
CREATE TABLE "credentials" (
	"id"	BINARY(16) NOT NULL,
	"cid"	VARCHAR(256) NOT NULL,
	"document"	TEXT NOT NULL,
	PRIMARY KEY("id")
);
-- ----------------------------------
-- AccountKeys table
-- ----------------------------------
CREATE TABLE "account_keys" (
    "account" BINARY(16) NOT NULL,
    "kid" VARCHAR(512) NOT NULL,
    "key" TEXT NOT NULL,
    CONSTRAINT "pk_account_keys" PRIMARY KEY ("account", "kid"),
    CONSTRAINT "fk_account_keys_account__id" FOREIGN KEY ("account") REFERENCES "accounts"("id") ON DELETE CASCADE ON UPDATE CASCADE
);
-- ----------------------------------
-- AccountDids table
-- ----------------------------------
CREATE TABLE "account_dids" (
    "account" BINARY(16) NOT NULL,
    "did" VARCHAR(1024) NOT NULL,
    "keyId" VARCHAR(512) NOT NULL,
    "document" TEXT NOT NULL,
    "alias" VARCHAR(1024) NOT NULL,
    "default" BOOLEAN DEFAULT 0 NOT NULL,
    CONSTRAINT "pk_account_dids" PRIMARY KEY ("account", "did"),
    CONSTRAINT "fk_account_dids_account__id" FOREIGN KEY ("account") REFERENCES "accounts"("id") ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT "fk_account_dids_keyId__kid" FOREIGN KEY ("keyId") REFERENCES "account_keys"("kid") ON DELETE CASCADE ON UPDATE CASCADE
);
-- ----------------------------------
-- AccountCredentials table
-- ----------------------------------
CREATE TABLE "account_credentials" (
    "account" BINARY(16) NOT NULL,
    "id" VARCHAR(256) NOT NULL,
    "credential" TEXT NOT NULL,
    CONSTRAINT "pk_account_credentials" PRIMARY KEY (account, id),
    CONSTRAINT "fk_account_credentials_account__id" FOREIGN KEY("account") REFERENCES "accounts"("id") ON DELETE CASCADE ON UPDATE CASCADE
);
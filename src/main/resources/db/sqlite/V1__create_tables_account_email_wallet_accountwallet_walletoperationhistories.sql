-- ----------------------------------
-- Emails table
-- ----------------------------------
CREATE TABLE "emails" (
	"id"	BINARY(16) NOT NULL,
	"email"	VARCHAR(128) NOT NULL,
	"password"	VARCHAR(200) NOT NULL,
	PRIMARY KEY("id")
);
-- ----------------------------------
-- Wallets table
-- ----------------------------------
CREATE TABLE "wallets" (
	"id"	BINARY(16) NOT NULL,
	"address"	VARCHAR(256) NOT NULL,
	"ecosystem"	VARCHAR(128) NOT NULL,
	PRIMARY KEY("id")
);
-- ----------------------------------
-- Accounts table
-- ----------------------------------
CREATE TABLE "accounts" (
	"id"	BINARY(16) NOT NULL,
	"email"	BINARY(16) NULL,
	"wallet"	BINARY(16) NULL,
	CONSTRAINT "fk_accounts_email__id" FOREIGN KEY("email") REFERENCES "emails"("id") ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT "fk_accounts_wallet__id" FOREIGN KEY("wallet") REFERENCES "wallets"("id") ON DELETE CASCADE ON UPDATE CASCADE,
	PRIMARY KEY("id")
);
-- ----------------------------------
-- AccountWallets table
-- ----------------------------------
CREATE TABLE "account_wallets" (
	"id"	BINARY(16) NOT NULL,
	"account"	BINARY(16) NOT NULL,
	"wallet"	BINARY(16) NOT NULL,
	PRIMARY KEY("id"),
	CONSTRAINT "fk_account_wallets_account__id" FOREIGN KEY("account") REFERENCES "accounts"("id") ON DELETE CASCADE ON UPDATE CASCADE,
	CONSTRAINT "fk_account_wallets_wallet__id" FOREIGN KEY("wallet") REFERENCES "wallets"("id") ON DELETE CASCADE ON UPDATE CASCADE
);
-- ----------------------------------
-- WalletOperationHistories table
-- ----------------------------------
CREATE TABLE "wallet_operation_histories" (
	"id"	BINARY(16) NOT NULL,
	"account"	BINARY(16) NOT NULL,
	"timestamp"	TEXT NOT NULL,
	"operation"	VARCHAR(48) NOT NULL,
	"data"	TEXT NOT NULL,
	CONSTRAINT "fk_wallet_operation_histories_account__id" FOREIGN KEY("account") REFERENCES "accounts"("id") ON DELETE CASCADE ON UPDATE CASCADE,
	PRIMARY KEY("id")
);
-- ----------------------------------
-- AccountWallets index
-- ----------------------------------
CREATE UNIQUE INDEX "account_wallets_account_wallet" ON "account_wallets" (
	"account",
	"wallet"
);
-- ----------------------------------
-- AccountEmails index
-- ----------------------------------
CREATE UNIQUE INDEX "accounts_email_wallet" ON "accounts" (
	"email",
	"wallet"
);
-- ----------------------------------
-- Emails index
-- ----------------------------------
CREATE UNIQUE INDEX "emails_email" ON "emails" (
	"email"
);
-- ----------------------------------
-- Wallets index
-- ----------------------------------
CREATE UNIQUE INDEX "wallets_address" ON "wallets" (
	"address"
);
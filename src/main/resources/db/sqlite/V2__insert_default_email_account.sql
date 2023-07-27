-- ----------------------------------
-- Insert emails table
-- ----------------------------------
INSERT INTO "Emails" ("id", "email", "password")
VALUES (X'BB7AB13EDD3644D39AF40F4F19A1CA24', "string@string.string", "$argon2i$v=19$m=65536,t=10,p=1$LDeWD3k4sL7aFnDCag+aqg$cpm8WeBwTDdoUdFpaNxj74PZ7/kJic+7VYua/fYmq/8");
-- ----------------------------------
-- Insert accounts table
-- ----------------------------------
INSERT INTO "Accounts" ("id", "email", "wallet")
VALUES (X'23729AC589BF4AA097B27C4F121F83B1', X'BB7AB13EDD3644D39AF40F4F19A1CA24', NULL);
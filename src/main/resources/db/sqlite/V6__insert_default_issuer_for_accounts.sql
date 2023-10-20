-- ----------------------------------
-- Insert issuers table
-- ----------------------------------
INSERT INTO "Issuers" ("id", "name", "description", "ui", "configuration")
VALUES (X'AA7AB13EDD3644D39AF40F4F19A1CA00', "walt.id", "walt.id issuer portal", "https://portal.walt.id/credentials?ids=", "https://issuer.portal.walt.id/.well-known/openid-credential-issuer");
-- ----------------------------------
-- Insert account-issuers table
-- ----------------------------------
INSERT INTO "account_issuers" ("id", "account", "issuer")
SELECT "id", "id", X'AA7AB13EDD3644D39AF40F4F19A1CA00' from "accounts";
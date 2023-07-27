-- ----------------------------------
-- Insert emails table
-- ----------------------------------
INSERT INTO public."emails" ("id", "email", "password")
VALUES ('3EB17ABB-36DD-D344-9AF4-0F4F19A1CA24'::UUID, 'string@string.string', '$argon2i$v=19$m=65536,t=10,p=1$LDeWD3k4sL7aFnDCag+aqg$cpm8WeBwTDdoUdFpaNxj74PZ7/kJic+7VYua/fYmq/8');
-- ----------------------------------
-- Insert accounts table
-- ----------------------------------
INSERT INTO public."accounts" ("id", "email", "wallet")
VALUES ('C59A7223-BF89-A04A-97B2-7C4F121F83B1'::UUID, '3EB17ABB-36DD-D344-9AF4-0F4F19A1CA24', NULL);
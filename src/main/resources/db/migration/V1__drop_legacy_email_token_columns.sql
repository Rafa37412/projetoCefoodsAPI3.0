-- Remove colunas legadas de verificação por token UUID
-- Execute apenas se as colunas existirem (PostgreSQL syntax with IF EXISTS)
ALTER TABLE tb_usuario DROP COLUMN IF EXISTS email_verification_token;
ALTER TABLE tb_usuario DROP COLUMN IF EXISTS email_verification_expira;
